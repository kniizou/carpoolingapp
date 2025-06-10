package com.carpooling.util;

import java.util.logging.*;
import java.io.File;
import java.io.IOException;

public class LoggerUtils {
    private static final Logger LOGGER = Logger.getLogger("CarpoolingApp");
    private static final String LOG_FILE = "carpooling.log";
    private static final int MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final int MAX_FILES = 5;

    static {
        try {
            // Créer le dossier logs s'il n'existe pas
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            // Configurer le handler de fichier
            FileHandler fileHandler = new FileHandler(
                "logs/" + LOG_FILE,
                MAX_FILE_SIZE,
                MAX_FILES,
                true
            );
            fileHandler.setFormatter(new SimpleFormatter());

            // Configurer le handler de console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());

            // Ajouter les handlers au logger
            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);

            // Définir le niveau de log
            LOGGER.setLevel(Level.ALL);

            // Empêcher la propagation des logs aux handlers parents
            LOGGER.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println("Erreur lors de la configuration du logger : " + e.getMessage());
        }
    }

    private LoggerUtils() {
        // Empêcher l'instanciation
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warning(String message) {
        LOGGER.warning(message);
    }

    public static void error(String message, Throwable thrown) {
        LOGGER.log(Level.SEVERE, message, thrown);
    }

    public static void debug(String message) {
        LOGGER.fine(message);
    }

    public static void trace(String message) {
        LOGGER.finest(message);
    }

    public static void logUserAction(String user, String action) {
        LOGGER.info(String.format("Utilisateur [%s] : %s", user, action));
    }

    public static void logDatabaseOperation(String operation, String details) {
        LOGGER.info(String.format("Base de données [%s] : %s", operation, details));
    }

    public static void logError(String context, String error, Throwable thrown) {
        LOGGER.log(Level.SEVERE, String.format("%s : %s", context, error), thrown);
    }

    public static void logSecurityEvent(String event, String details) {
        LOGGER.warning(String.format("Sécurité [%s] : %s", event, details));
    }
} 