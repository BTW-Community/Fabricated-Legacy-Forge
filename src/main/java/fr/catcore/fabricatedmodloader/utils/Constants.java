package fr.catcore.fabricatedmodloader.utils;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

public class Constants {
    public static final File MAIN_FOLDER = new File(FabricLoader.getInstance().getGameDir().toFile(), "fabricated-modloader");
    public static final File VERSIONED_FOLDER = new File(MAIN_FOLDER, FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().getFriendlyString());
    public static final File MAPPINGS_FILE = new File(VERSIONED_FOLDER, "mappings.tiny");
    public static final File MOD_MAPPINGS_FILE = new File(VERSIONED_FOLDER, "mods_mappings.tiny");
    public static final File REMAPPED_FOLDER = new File(VERSIONED_FOLDER, "mods");

    public static final LoggerContext LOG_CATEGORY = new LoggerContext("Mod", "FabricatedModLoader");
    public static final LoggerContext MODLOADER_LOG_CATEGORY = new LoggerContext("Mod", "FabricatedModLoader", "ModLoader");

    static {
        MAIN_FOLDER.mkdirs();
        VERSIONED_FOLDER.mkdirs();
        REMAPPED_FOLDER.mkdirs();
    }
}
