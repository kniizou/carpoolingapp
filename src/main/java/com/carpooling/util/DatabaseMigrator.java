package com.carpooling.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for migrating data from MySQL to SQLite database
 */
public class DatabaseMigrator {
    private static final Logger LOGGER = Logger.getLogger(DatabaseMigrator.class.getName());
    
    // MySQL connection details
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/carpooling";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "";
    
    // SQLite connection details
    private static final String DB_FILE = "carpooling.db"; // Store in current working directory
    private static final String SQLITE_URL = "jdbc:sqlite:" + DB_FILE;
    
    public static void main(String[] args) {
        if (args.length > 0 && "--migrate".equals(args[0])) {
            try {
                migrateDatabase();
                System.out.println("Migration completed successfully!");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Migration failed", e);
                System.err.println("Migration failed: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.println("Usage: java -cp <classpath> com.carpooling.util.DatabaseMigrator --migrate");
        }
    }
    
    public static void migrateDatabase() throws Exception {
        // Check if SQLite database already exists
        File dbFile = new File(DB_FILE);
        if (dbFile.exists()) {
            LOGGER.warning(() -> "SQLite database already exists at: " + dbFile.getAbsolutePath());
            System.out.println("Warning: SQLite database already exists. Migration will overwrite existing data.");
            System.out.print("Continue? (y/n): ");
            char answer = (char) System.in.read();
            if (answer != 'y' && answer != 'Y') {
                System.out.println("Migration cancelled.");
                return;
            }
        }
        
        // Initialize SQLite database
        try (Connection sqliteConn = DriverManager.getConnection(SQLITE_URL)) {
            initializeSQLiteDatabase(sqliteConn);
            
            // Connect to MySQL and migrate data
            try (Connection mysqlConn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS)) {
                // Migrate users
                migrateUsers(mysqlConn, sqliteConn);
                
                // Migrate trips
                migrateTrips(mysqlConn, sqliteConn);
                
                // Migrate trip_passengers
                migrateTripPassengers(mysqlConn, sqliteConn);
                
                LOGGER.info("Migration completed successfully");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database", e);
                throw new SQLException("Failed to connect to MySQL database. Please check your MySQL configuration.", e);
            }
        }
    }
    
    private static void initializeSQLiteDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");
            
            // Create tables based on the SQLite schema
            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT NOT NULL," +
                "prenom TEXT NOT NULL," +
                "age INTEGER NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")");
            
            // Trips table
            stmt.execute("CREATE TABLE IF NOT EXISTS trips (" +
                "id TEXT PRIMARY KEY," +
                "driver_id INTEGER NOT NULL," +
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
            
            // Trip passengers table
            stmt.execute("CREATE TABLE IF NOT EXISTS trip_passengers (" +
                "trip_id TEXT NOT NULL," +
                "passenger_id INTEGER NOT NULL," +
                "status TEXT NOT NULL," +
                "PRIMARY KEY (trip_id, passenger_id)," +
                "FOREIGN KEY (trip_id) REFERENCES trips(id)," +
                "FOREIGN KEY (passenger_id) REFERENCES users(id)" +
                ")");
        }
    }
    
    private static void migrateUsers(Connection mysqlConn, Connection sqliteConn) throws SQLException {
        System.out.println("Migrating users...");
        
        // Clear existing users in SQLite (except keep default admin if exists)
        try (Statement stmt = sqliteConn.createStatement()) {
            stmt.execute("DELETE FROM users WHERE id != 1");
        }
        
        // Get users from MySQL
        String selectSQL = "SELECT id, nom, prenom, age, email, password, role, date_inscription FROM users";
        try (Statement mysqlStmt = mysqlConn.createStatement();
             ResultSet rs = mysqlStmt.executeQuery(selectSQL)) {
            
            // Insert into SQLite
            String insertSQL = "INSERT OR REPLACE INTO users (id, nom, prenom, age, email, password, role, date_inscription) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement sqliteStmt = sqliteConn.prepareStatement(insertSQL)) {
                int count = 0;
                while (rs.next()) {
                    sqliteStmt.setObject(1, rs.getObject("id"));
                    sqliteStmt.setString(2, rs.getString("nom"));
                    sqliteStmt.setString(3, rs.getString("prenom"));
                    sqliteStmt.setInt(4, rs.getInt("age"));
                    sqliteStmt.setString(5, rs.getString("email"));
                    sqliteStmt.setString(6, rs.getString("password"));
                    sqliteStmt.setString(7, rs.getString("role"));
                    sqliteStmt.setString(8, rs.getString("date_inscription"));
                    sqliteStmt.executeUpdate();
                    count++;
                }
                System.out.println("Migrated " + count + " user(s)");
            }
        }
    }
    
    private static void migrateTrips(Connection mysqlConn, Connection sqliteConn) throws SQLException {
        System.out.println("Migrating trips...");
        
        // Clear existing trips in SQLite
        try (Statement stmt = sqliteConn.createStatement()) {
            stmt.execute("DELETE FROM trips");
        }
        
        // Get trips from MySQL
        String selectSQL = "SELECT id, driver_id, departure, destination, date, time, available_seats, price, " +
                          "trip_type, recurring_days FROM trips";
        try (Statement mysqlStmt = mysqlConn.createStatement();
             ResultSet rs = mysqlStmt.executeQuery(selectSQL)) {
            
            // Insert into SQLite
            String insertSQL = "INSERT INTO trips (id, driver_id, departure, destination, date, time, available_seats, " +
                              "price, trip_type, recurring_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement sqliteStmt = sqliteConn.prepareStatement(insertSQL)) {
                int count = 0;
                while (rs.next()) {
                    sqliteStmt.setString(1, rs.getString("id"));
                    sqliteStmt.setObject(2, rs.getObject("driver_id"));
                    sqliteStmt.setString(3, rs.getString("departure"));
                    sqliteStmt.setString(4, rs.getString("destination"));
                    sqliteStmt.setString(5, rs.getString("date"));
                    sqliteStmt.setString(6, rs.getString("time"));
                    sqliteStmt.setInt(7, rs.getInt("available_seats"));
                    sqliteStmt.setDouble(8, rs.getDouble("price"));
                    sqliteStmt.setString(9, rs.getString("trip_type"));
                    sqliteStmt.setString(10, rs.getString("recurring_days"));
                    sqliteStmt.executeUpdate();
                    count++;
                }
                System.out.println("Migrated " + count + " trip(s)");
            }
        }
    }
    
    private static void migrateTripPassengers(Connection mysqlConn, Connection sqliteConn) throws SQLException {
        System.out.println("Migrating trip passengers...");
        
        // Clear existing trip_passengers in SQLite
        try (Statement stmt = sqliteConn.createStatement()) {
            stmt.execute("DELETE FROM trip_passengers");
        }
        
        // Get trip_passengers from MySQL
        String selectSQL = "SELECT trip_id, passenger_id, status FROM trip_passengers";
        try (Statement mysqlStmt = mysqlConn.createStatement();
             ResultSet rs = mysqlStmt.executeQuery(selectSQL)) {
            
            // Insert into SQLite
            String insertSQL = "INSERT INTO trip_passengers (trip_id, passenger_id, status) VALUES (?, ?, ?)";
            try (PreparedStatement sqliteStmt = sqliteConn.prepareStatement(insertSQL)) {
                int count = 0;
                while (rs.next()) {
                    sqliteStmt.setString(1, rs.getString("trip_id"));
                    sqliteStmt.setObject(2, rs.getObject("passenger_id"));
                    sqliteStmt.setString(3, rs.getString("status"));
                    sqliteStmt.executeUpdate();
                    count++;
                }
                System.out.println("Migrated " + count + " trip passenger record(s)");
            }
        }
    }
}
