package cpw.mods.fml.common.discovery;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import cpw.mods.fml.common.ModContainer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ASMDataTable {
    private SetMultimap<String, ASMDataTable.ASMData> globalAnnotationData = HashMultimap.create();
    private Map<ModContainer, SetMultimap<String, ASMDataTable.ASMData>> containerAnnotationData;
    private List<ModContainer> containers = Lists.newArrayList();

    public ASMDataTable() {
    }

    public SetMultimap<String, ASMDataTable.ASMData> getAnnotationsFor(ModContainer container) {
        if (this.containerAnnotationData == null) {
            ImmutableMap.Builder<ModContainer, SetMultimap<String, ASMDataTable.ASMData>> mapBuilder = ImmutableMap.builder();
            Iterator i$ = this.containers.iterator();

            while(i$.hasNext()) {
                ModContainer cont = (ModContainer)i$.next();
                Multimap<String, ASMDataTable.ASMData> values = Multimaps.filterValues(this.globalAnnotationData, new ASMDataTable.ModContainerPredicate(cont));
                mapBuilder.put(cont, ImmutableSetMultimap.copyOf(values));
            }

            this.containerAnnotationData = mapBuilder.build();
        }

        return (SetMultimap)this.containerAnnotationData.get(container);
    }

    public Set<ASMDataTable.ASMData> getAll(String annotation) {
        return this.globalAnnotationData.get(annotation);
    }

    public void addASMData(ModCandidate candidate, String annotation, String className, String objectName, Map<String, Object> annotationInfo) {
        this.globalAnnotationData.put(annotation, new ASMDataTable.ASMData(candidate, annotation, className, objectName, annotationInfo));
    }

    public void addContainer(ModContainer container) {
        this.containers.add(container);
    }

    private static class ModContainerPredicate implements Predicate<ASMDataTable.ASMData> {
        private ModContainer container;

        public ModContainerPredicate(ModContainer container) {
            this.container = container;
        }

        public boolean apply(ASMDataTable.ASMData data) {
            return this.container.getSource().equals(data.candidate.getModContainer());
        }
    }

    public static class ASMData {
        private ModCandidate candidate;
        private String annotationName;
        private String className;
        private String objectName;
        private Map<String, Object> annotationInfo;

        public ASMData(ModCandidate candidate, String annotationName, String className, String objectName, Map<String, Object> info) {
            this.candidate = candidate;
            this.annotationName = annotationName;
            this.className = className;
            this.objectName = objectName;
            this.annotationInfo = info;
        }

        public ModCandidate getCandidate() {
            return this.candidate;
        }

        public String getAnnotationName() {
            return this.annotationName;
        }

        public String getClassName() {
            return this.className;
        }

        public String getObjectName() {
            return this.objectName;
        }

        public Map<String, Object> getAnnotationInfo() {
            return this.annotationInfo;
        }
    }
}