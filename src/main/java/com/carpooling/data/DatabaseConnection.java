package com.carpooling.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_FILE = "carpooling.db"; // Store in current working directory
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Connect to SQLite database (will create file if it doesn't exist)
                connection = DriverManager.getConnection(DB_URL);
                
                // Enable foreign keys
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                }
                
                initializeDatabase(connection);
                LOGGER.info("Successfully connected to SQLite database in current working directory");
                System.out.println("Database location: " + new File(DB_FILE).getAbsolutePath());
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error connecting to SQLite database", e);
                throw e;
            }
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Création de la table users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY," +
                "nom TEXT NOT NULL," +
                "prenom TEXT NOT NULL," +
                "age INTEGER NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "CHECK (age >= 18)" +
                ")");

            // Création de la table trips
            stmt.execute("CREATE TABLE IF NOT EXISTS trips (" +
                "id TEXT PRIMARY KEY," +
                "driver_id TEXT NOT NULL," +
                "departure TEXT NOT NULL," +
                "destination TEXT NOT NULL," +
                "date TEXT NOT NULL," +
                "time TEXT NOT NULL," +
                "available_seats INTEGER NOT NULL," +
                "price REAL NOT NULL," +
                "trip_type TEXT DEFAULT 'Occasionnel'," +
                "recurring_days TEXT," +
                "FOREIGN KEY (driver_id) REFERENCES users(id)" +
                ")");

            // Création de la table trip_passengers
            stmt.execute("CREATE TABLE IF NOT EXISTS trip_passengers (" +
                "trip_id TEXT NOT NULL," +
                "passenger_id TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "PRIMARY KEY (trip_id, passenger_id)," +
                "FOREIGN KEY (trip_id) REFERENCES trips(id)," +
                "FOREIGN KEY (passenger_id) REFERENCES users(id)" +
                ")");

            // SQLite doesn't support ALTER TABLE to add columns if they exist
            // The table creation above already includes all needed columns

            // Insertion/mise à jour de l'administrateur par défaut
            // Check if admin exists and update password if it's plain text
            String hashedAdminPassword = com.carpooling.util.SecurityUtils.hashPassword("admin2025");
            
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT password FROM users WHERE email = ?")) {
                checkStmt.setString(1, "admin@admin.com");
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    String currentPassword = rs.getString("password");
                    // If password is plain text (not a BCrypt hash), update it
                    if (!currentPassword.startsWith("$2")) { // BCrypt hashes start with $2
                        try (PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE users SET password = ? WHERE email = ?")) {
                            updateStmt.setString(1, hashedAdminPassword);
                            updateStmt.setString(2, "admin@admin.com");
                            updateStmt.executeUpdate();
                            LOGGER.info("Admin password updated to BCrypt hash");
                        }
                    }
                } else {
                    // Admin doesn't exist, create it
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO users (id, nom, prenom, age, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                        insertStmt.setString(1, "admin-001");
                        insertStmt.setString(2, "Admin");
                        insertStmt.setString(3, "System");
                        insertStmt.setInt(4, 30);
                        insertStmt.setString(5, "admin@admin.com");
                        insertStmt.setString(6, hashedAdminPassword);
                        insertStmt.setString(7, "ADMIN");
                        insertStmt.executeUpdate();
                        LOGGER.info("Admin user created with BCrypt password");
                    }
                }
            }

            LOGGER.info("SQLite database initialized successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation de la base de données", e);
            throw e;
        }
    }

    /**
     * Clears all data from the database tables while preserving table structure.
     * This is a destructive operation that removes all user accounts, trips, and related data.
     * 
     * @param confirmationKey A safety key that must match "CLEAR_ALL_DATA" to proceed
     * @throws SQLException if database operation fails
     * @throws IllegalArgumentException if confirmation key is incorrect
     */
    public static void clearAllData(String confirmationKey) throws SQLException {
        if (!"CLEAR_ALL_DATA".equals(confirmationKey)) {
            throw new IllegalArgumentException("Invalid confirmation key. Operation cancelled for safety.");
        }
        
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            
            // Clear data in order respecting foreign key constraints
            try (Statement stmt = conn.createStatement()) {
                // Disable foreign key checks temporarily for easier clearing
                stmt.execute("PRAGMA foreign_keys = OFF");
                
                // Clear dependent tables first
                stmt.execute("DELETE FROM trip_passengers");
                stmt.execute("DELETE FROM trips");
                stmt.execute("DELETE FROM notifications");
                stmt.execute("DELETE FROM users");
                
                // Re-enable foreign key checks
                stmt.execute("PRAGMA foreign_keys = ON");
                
                LOGGER.info("All database tables cleared successfully");
            }
            
            conn.commit();
            
            // Reinitialize with default admin user
            initializeDatabase(conn);
            
        } catch (SQLException e) {
            conn.rollback();
            LOGGER.log(Level.SEVERE, "Error clearing database data", e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * Completely removes the database file and recreates it.
     * This is the most thorough way to reset the database.
     * 
     * @param confirmationKey A safety key that must match "RECREATE_DATABASE" to proceed
     * @throws IllegalArgumentException if confirmation key is incorrect
     */
    public static void recreateDatabase(String confirmationKey) {
        if (!"RECREATE_DATABASE".equals(confirmationKey)) {
            throw new IllegalArgumentException("Invalid confirmation key. Operation cancelled for safety.");
        }
        
        try {
            // Close existing connection
            closeConnection();
            
            // Delete the database file
            File dbFile = new File(DB_FILE);
            if (dbFile.exists() && dbFile.delete()) {
                LOGGER.info("Database file deleted successfully");
            }
            
            // Force reconnection which will recreate the database
            connection = null;
            getConnection();
            
            LOGGER.info("Database recreated successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error recreating database", e);
            throw new RuntimeException("Failed to recreate database", e);
        }
    }
    
    /**
     * Utility method for testing - clears all data without confirmation key.
     * Should only be used in test environments.
     */
    public static void clearAllDataForTesting() throws SQLException {
        clearAllData("CLEAR_ALL_DATA");
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Connexion à la base de données fermée");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion", e);
            }
        }
    }
}
