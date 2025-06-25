package com.carpooling.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.carpooling.data.DatabaseConnection;

/**
 * Utility class for database maintenance operations.
 */
public class DatabaseCleaner {
    private static final Logger LOGGER = Logger.getLogger(DatabaseCleaner.class.getName());
    private static final String ADMIN_USER_ID = "admin-001";
    private static final String ADMIN_EMAIL = "admin@admin.com";
    
    /**
     * Clears all data from the database except for the admin user.
     * This removes all trips, trip passengers, notifications, and non-admin users.
     */
    public static void clearDatabaseExceptAdmin() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete in order to respect foreign key constraints
                
                // 1. Delete all notifications (if table exists)
                clearTableIfExists(conn, "notifications", "Notifications");
                
                // 2. Delete all trip passengers
                clearTableIfExists(conn, "trip_passengers", "Trip passengers");
                
                // 3. Delete all trips
                clearTableIfExists(conn, "trips", "Trips");
                
                // 4. Delete all non-admin users
                clearNonAdminUsers(conn);
                
                // Commit transaction
                conn.commit();
                
                LOGGER.info("Database cleared successfully, admin user preserved");
                System.out.println("✅ Database cleared successfully!");
                System.out.println("📊 Admin user preserved:");
                System.out.println("   - ID: " + ADMIN_USER_ID);
                System.out.println("   - Email: " + ADMIN_EMAIL);
                System.out.println("   - Password: admin2025");
                
            } catch (SQLException e) {
                // Rollback on error
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Error clearing database, rolling back", e);
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to clear database", e);
            System.err.println("❌ Failed to clear database: " + e.getMessage());
        }
    }
    
    /**
     * Clears all records from a specific table if it exists.
     */
    private static void clearTableIfExists(Connection conn, String tableName, String displayName) throws SQLException {
        // Check if table exists first
        if (!tableExists(conn, tableName)) {
            System.out.println("⚠️  Table " + tableName + " doesn't exist, skipping");
            return;
        }
        
        String sql = "DELETE FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int deletedRows = stmt.executeUpdate();
            LOGGER.info("Cleared " + deletedRows + " records from " + tableName);
            System.out.println("🗑️  Cleared " + deletedRows + " " + displayName.toLowerCase());
        }
    }
    
    /**
     * Checks if a table exists in the database.
     */
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Clears all records from a specific table.
     */
    private static void clearTable(Connection conn, String tableName, String displayName) throws SQLException {
        String sql = "DELETE FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int deletedRows = stmt.executeUpdate();
            LOGGER.info("Cleared " + deletedRows + " records from " + tableName);
            System.out.println("🗑️  Cleared " + deletedRows + " " + displayName.toLowerCase());
        }
    }
    
    /**
     * Deletes all users except the admin user.
     */
    private static void clearNonAdminUsers(Connection conn) throws SQLException {
        String sql = "DELETE FROM users WHERE id != ? AND email != ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ADMIN_USER_ID);
            stmt.setString(2, ADMIN_EMAIL);
            int deletedRows = stmt.executeUpdate();
            LOGGER.info("Cleared " + deletedRows + " non-admin users");
            System.out.println("🗑️  Cleared " + deletedRows + " non-admin users");
        }
    }
    
    /**
     * Displays current database statistics.
     */
    public static void showDatabaseStats() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            System.out.println("\n📊 Current Database Statistics:");
            System.out.println("================================");
            
            // Count users
            showTableCountIfExists(conn, "users", "Users");
            
            // Count trips
            showTableCountIfExists(conn, "trips", "Trips");
            
            // Count trip passengers
            showTableCountIfExists(conn, "trip_passengers", "Trip passengers");
            
            // Count notifications
            showTableCountIfExists(conn, "notifications", "Notifications");
            
            System.out.println("================================\n");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to show database stats", e);
            System.err.println("❌ Failed to show database stats: " + e.getMessage());
        }
    }
    
    /**
     * Shows count for a specific table if it exists.
     */
    private static void showTableCountIfExists(Connection conn, String tableName, String displayName) throws SQLException {
        if (!tableExists(conn, tableName)) {
            System.out.printf("%-20s: N/A (table doesn't exist)%n", displayName);
            return;
        }
        
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.printf("%-20s: %d%n", displayName, count);
            }
        }
    }
    
    /**
     * Shows count for a specific table.
     */
    private static void showTableCount(Connection conn, String tableName, String displayName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.printf("%-20s: %d%n", displayName, count);
            }
        }
    }
    
    /**
     * Main method for running database cleaning operations.
     */
    public static void main(String[] args) {
        System.out.println("🧹 Database Cleaner Utility");
        System.out.println("============================");
        
        // Show current stats
        showDatabaseStats();
        
        // Clear database except admin
        System.out.println("🧹 Clearing database (preserving admin user)...");
        clearDatabaseExceptAdmin();
        
        // Show final stats
        showDatabaseStats();
    }
}
