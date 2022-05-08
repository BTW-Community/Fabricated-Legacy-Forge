package cpw.mods.fml.common.toposort;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModSorter {
    private TopologicalSort.DirectedGraph<ModContainer> modGraph;
    private ModContainer beforeAll = new DummyModContainer();
    private ModContainer afterAll = new DummyModContainer();
    private ModContainer before = new DummyModContainer();
    private ModContainer after = new DummyModContainer();

    public ModSorter(List<ModContainer> modList, Map<String, ModContainer> nameLookup) {
        this.buildGraph(modList, nameLookup);
    }

    private void buildGraph(List<ModContainer> modList, Map<String, ModContainer> nameLookup) {
        this.modGraph = new TopologicalSort.DirectedGraph();
        this.modGraph.addNode(this.beforeAll);
        this.modGraph.addNode(this.before);
        this.modGraph.addNode(this.afterAll);
        this.modGraph.addNode(this.after);
        this.modGraph.addEdge(this.before, this.after);
        this.modGraph.addEdge(this.beforeAll, this.before);
        this.modGraph.addEdge(this.after, this.afterAll);
        Iterator i$ = modList.iterator();

        ModContainer mod;
        while(i$.hasNext()) {
            mod = (ModContainer)i$.next();
            this.modGraph.addNode(mod);
        }

        i$ = modList.iterator();

        while(true) {
            while(i$.hasNext()) {
                mod = (ModContainer)i$.next();
                if (mod.isImmutable()) {
                    this.modGraph.addEdge(this.beforeAll, mod);
                    this.modGraph.addEdge(mod, this.before);
                } else {
                    boolean preDepAdded = false;
                    boolean postDepAdded = false;
                    Iterator i$ = mod.getDependencies().iterator();

                    ArtifactVersion dep;
                    String modid;
                    while(i$.hasNext()) {
                        dep = (ArtifactVersion)i$.next();
                        preDepAdded = true;
                        modid = dep.getLabel();
                        if (modid.equals("*")) {
                            this.modGraph.addEdge(mod, this.afterAll);
                            this.modGraph.addEdge(this.after, mod);
                            postDepAdded = true;
                        } else {
                            this.modGraph.addEdge(this.before, mod);
                            if (Loader.isModLoaded(modid)) {
                                this.modGraph.addEdge(nameLookup.get(modid), mod);
                            }
                        }
                    }

                    i$ = mod.getDependants().iterator();

                    while(i$.hasNext()) {
                        dep = (ArtifactVersion)i$.next();
                        postDepAdded = true;
                        modid = dep.getLabel();
                        if (modid.equals("*")) {
                            this.modGraph.addEdge(this.beforeAll, mod);
                            this.modGraph.addEdge(mod, this.before);
                            preDepAdded = true;
                        } else {
                            this.modGraph.addEdge(mod, this.after);
                            if (Loader.isModLoaded(modid)) {
                                this.modGraph.addEdge(mod, nameLookup.get(modid));
                            }
                        }
                    }

                    if (!preDepAdded) {
                        this.modGraph.addEdge(this.before, mod);
                    }

                    if (!postDepAdded) {
                        this.modGraph.addEdge(mod, this.after);
                    }
                }
            }

            return;
        }
    }

    public List<ModContainer> sort() {
        List<ModContainer> sortedList = TopologicalSort.topologicalSort(this.modGraph);
        sortedList.removeAll(Arrays.asList(this.beforeAll, this.before, this.after, this.afterAll));
        return sortedList;
    }
}