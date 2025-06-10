package com.carpooling.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost:3306/carpooling";
    private static final String USER = "root";
    private static final String PASS = "";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Créer la base de données si elle n'existe pas
                Connection tempConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASS);
                Statement stmt = tempConnection.createStatement();
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS carpooling");
                tempConnection.close();

                // Connexion à la base de données
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                initializeDatabase(connection);
                LOGGER.info("Connexion à la base de données établie avec succès");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la connexion à la base de données", e);
                throw e;
            }
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Création de la table users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "nom VARCHAR(50) NOT NULL," +
                "prenom VARCHAR(50) NOT NULL," +
                "age INT NOT NULL," +
                "email VARCHAR(100) UNIQUE NOT NULL," +
                "password VARCHAR(100) NOT NULL," +
                "role VARCHAR(20) NOT NULL" +
                ")");

            // Création de la table trips
            stmt.execute("CREATE TABLE IF NOT EXISTS trips (" +
                "id VARCHAR(36) PRIMARY KEY," +
                "driver_id VARCHAR(36) NOT NULL," +
                "departure VARCHAR(100) NOT NULL," +
                "destination VARCHAR(100) NOT NULL," +
                "date VARCHAR(10) NOT NULL," +
                "time VARCHAR(5) NOT NULL," +
                "available_seats INT NOT NULL," +
                "price DOUBLE NOT NULL," +
                "trip_type VARCHAR(20) DEFAULT 'Occasionnel'," +
                "recurring_days VARCHAR(100)," +
                "FOREIGN KEY (driver_id) REFERENCES users(id)" +
                ")");

            // Création de la table trip_passengers
            stmt.execute("CREATE TABLE IF NOT EXISTS trip_passengers (" +
                "trip_id VARCHAR(36) NOT NULL," +
                "passenger_id VARCHAR(36) NOT NULL," +
                "status VARCHAR(20) NOT NULL," +
                "PRIMARY KEY (trip_id, passenger_id)," +
                "FOREIGN KEY (trip_id) REFERENCES trips(id)," +
                "FOREIGN KEY (passenger_id) REFERENCES users(id)" +
                ")");

            // Ajout des colonnes manquantes si nécessaire
            try {
                stmt.execute("ALTER TABLE trips ADD COLUMN trip_type VARCHAR(20) DEFAULT 'Occasionnel'");
            } catch (SQLException e) {
                // La colonne existe déjà
            }

            try {
                stmt.execute("ALTER TABLE trips ADD COLUMN recurring_days VARCHAR(100)");
            } catch (SQLException e) {
                // La colonne existe déjà
            }

            // Insertion de l'administrateur par défaut s'il n'existe pas
            stmt.execute("INSERT IGNORE INTO users (id, nom, prenom, age, email, password, role) " +
                "VALUES ('admin', 'Admin', 'System', 30, 'admin@admin.com', 'admin2025', 'ADMIN')");

            LOGGER.info("Base de données initialisée avec succès");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation de la base de données", e);
            throw e;
        }
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