package me.dkim19375.continuityboost.plugin.util;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingUtils {
    private static final Logger logger = Bukkit.getLogger();

    public static void logInfo(Level level, String str) {
        logger.log(level, str);
    }

    public static void logInfo(String str) {
        logInfo(Level.INFO, str);
    }
}
