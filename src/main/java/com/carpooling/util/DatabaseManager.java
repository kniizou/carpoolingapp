package com.carpooling.util;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.carpooling.data.DatabaseConnection;

/**
 * Utility class for database management operations.
 * Provides methods for safely clearing and resetting the database.
 */
public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    
    /**
     * Clears all data from the database while preserving table structure.
     * Requires explicit confirmation to prevent accidental data loss.
     * 
     * @param confirm Set to true to confirm the operation
     * @throws IllegalArgumentException if confirmation is not provided
     * @throws SQLException if database operation fails
     */
    public static void clearAllData(boolean confirm) throws SQLException {
        if (!confirm) {
            throw new IllegalArgumentException(
                "Database clearing requires explicit confirmation. Call with confirm=true to proceed."
            );
        }
        
        LOGGER.warning("DANGEROUS OPERATION: Clearing all database data");
        DatabaseConnection.clearAllData("CLEAR_ALL_DATA");
        LOGGER.info("Database data cleared successfully. Default admin user recreated.");
    }
    
    /**
     * Completely recreates the database file.
     * This is the most thorough way to reset the database.
     * 
     * @param confirm Set to true to confirm the operation
     * @throws IllegalArgumentException if confirmation is not provided
     */
    public static void recreateDatabase(boolean confirm) {
        if (!confirm) {
            throw new IllegalArgumentException(
                "Database recreation requires explicit confirmation. Call with confirm=true to proceed."
            );
        }
        
        LOGGER.warning("DANGEROUS OPERATION: Recreating entire database");
        DatabaseConnection.recreateDatabase("RECREATE_DATABASE");
        LOGGER.info("Database recreated successfully with default admin user.");
    }
    
    /**
     * Utility method for testing environments.
     * Clears all data without requiring explicit confirmation.
     * Should only be used in test environments.
     */
    public static void clearForTesting() throws SQLException {
        LOGGER.info("Clearing database for testing purposes");
        DatabaseConnection.clearAllDataForTesting();
    }
    
    /**
     * Displays current database status and location.
     */
    public static void displayDatabaseInfo() {
        try {
            DatabaseConnection.getConnection();
            LOGGER.info("Database connection successful");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed: {0}", e.getMessage());
        }
    }
    
    /**
     * Command-line utility for database operations.
     * Usage: java DatabaseManager [clear|recreate|status]
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java DatabaseManager [clear|recreate|status]");
            System.out.println("  clear    - Clear all data from database");
            System.out.println("  recreate - Completely recreate database file");
            System.out.println("  status   - Display database connection status");
            return;
        }
        
        String operation = args[0].toLowerCase();
        
        try {
            switch (operation) {
                case "clear" -> {
                    System.out.println("WARNING: This will delete ALL data from the database!");
                    System.out.println("Are you sure? This operation cannot be undone.");
                    System.out.print("Type 'CONFIRM' to proceed: ");
                    
                    java.util.Scanner scanner = new java.util.Scanner(System.in);
                    String confirmation = scanner.nextLine();
                    
                    if ("CONFIRM".equals(confirmation)) {
                        clearAllData(true);
                        System.out.println("Database cleared successfully.");
                    } else {
                        System.out.println("Operation cancelled.");
                    }
                    scanner.close();
                }
                case "recreate" -> {
                    System.out.println("WARNING: This will completely recreate the database file!");
                    System.out.println("All data will be permanently lost.");
                    System.out.print("Type 'RECREATE' to proceed: ");
                    
                    java.util.Scanner recreateScanner = new java.util.Scanner(System.in);
                    String recreateConfirmation = recreateScanner.nextLine();
                    
                    if ("RECREATE".equals(recreateConfirmation)) {
                        recreateDatabase(true);
                        System.out.println("Database recreated successfully.");
                    } else {
                        System.out.println("Operation cancelled.");
                    }
                    recreateScanner.close();
                }
                case "status" -> {
                    displayDatabaseInfo();
                    System.out.println("Database status check completed. See logs for details.");
                }
                default -> {
                    System.out.println("Unknown operation: " + operation);
                    System.out.println("Valid operations: clear, recreate, status");
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("Error performing database operation: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Database operation failed", e);
        }
    }
}
