package fr.catcore.fabricatedmodloader.utils;

import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.metadata.EntrypointMetadata;
import net.fabricmc.loader.metadata.LoaderModMetadata;
import net.fabricmc.loader.metadata.NestedJarEntry;
import net.fabricmc.loader.util.version.StringVersion;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MLModMetadata implements LoaderModMetadata {

    private final String modid;
    private final String modName;

    protected MLModMetadata(String modid, String modName) {
        this.modid = modid;
        this.modName = modName;
    }

    @Override
    public String getType() {
        return "modloader";
    }

    @Override
    public String getId() {
        return this.modid;
    }

    @Override
    public Collection<String> getProvides() {
        return Collections.emptyList();
    }

    @Override
    public Version getVersion() {
        return new StringVersion("1.0.0");
    }

    @Override
    public ModEnvironment getEnvironment() {
        return ModEnvironment.UNIVERSAL;
    }

    @Override
    public Collection<ModDependency> getDepends() {
        return null;
    }

    @Override
    public Collection<ModDependency> getRecommends() {
        return null;
    }

    @Override
    public Collection<ModDependency> getSuggests() {
        return null;
    }

    @Override
    public Collection<ModDependency> getConflicts() {
        return null;
    }

    @Override
    public Collection<ModDependency> getBreaks() {
        return null;
    }


    @Override
    public String getName() {
        return this.modName;
    }

    @Override
    public String getDescription() {
        return "This is a converted ModLoader mod.";
    }

    @Override
    public Collection<Person> getAuthors() {
        return Collections.emptyList();
    }

    @Override
    public Collection<Person> getContributors() {
        return Collections.emptyList();
    }

    @Override
    public ContactInformation getContact() {
        return ContactInformation.EMPTY;
    }

    @Override
    public Collection<String> getLicense() {
        return Collections.emptyList();
    }

    @Override
    public Optional<String> getIconPath(int size) {
        return Optional.empty();
    }

    @Override
    public boolean containsCustomValue(String key) {
        return false;
    }

    @Override
    public CustomValue getCustomValue(String key) {
        return null;
    }

    @Override
    public Map<String, CustomValue> getCustomValues() {
        return Maps.newHashMap();
    }


    @Override
    public int getSchemaVersion() {
        return 0;
    }

    @Override
    public Map<String, String> getLanguageAdapterDefinitions() {
        return Maps.newHashMap();
    }

    @Override
    public Collection<NestedJarEntry> getJars() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getMixinConfigs(EnvType type) {
        return Collections.emptyList();
    }

    @Override
    public String getAccessWidener() {
        return null;
    }

    @Override
    public boolean loadsInEnvironment(EnvType type) {
        return true;
    }

    @Override
    public List<EntrypointMetadata> getEntrypoints(String type) {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getEntrypointKeys() {
        return Collections.emptyList();
    }

    @Override
    public void emitFormatWarnings(Logger logger) {

    }


}
