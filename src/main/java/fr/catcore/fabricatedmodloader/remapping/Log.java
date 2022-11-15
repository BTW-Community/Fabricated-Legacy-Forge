package fr.catcore.fabricatedmodloader.remapping;

import org.apache.logging.log4j.core.LoggerContext;

public class Log {
    public static void infro() {

    }

    public static void info(LoggerContext logCategory, String s) {
        logCategory.getLogger("").info(s);
    }

    public static void debug(LoggerContext logCategory, String s) {
        logCategory.getLogger("").debug(s);
    }

    public static void trace(LoggerContext logCategory, String s, Throwable s2) {
        logCategory.getLogger("").trace(s);
        logCategory.getLogger("").trace(s2.getMessage());
    }

    public static void trace(LoggerContext logCategory, String s) {
        logCategory.getLogger("").trace(s);
    }
}
