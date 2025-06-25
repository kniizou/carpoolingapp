-- SQLite database schema for Carpooling App

-- Enable foreign key constraints
PRAGMA foreign_keys = ON;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    age INTEGER NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (age >= 18)
);

-- Table des trajets
CREATE TABLE IF NOT EXISTS trips (
    id TEXT PRIMARY KEY,
    driver_id TEXT NOT NULL,
    departure TEXT NOT NULL,
    destination TEXT NOT NULL,
    date TEXT NOT NULL,
    time TEXT NOT NULL,
    available_seats INTEGER NOT NULL,
    price REAL NOT NULL,
    trip_type TEXT DEFAULT 'Occasionnel',
    recurring_days TEXT,
    FOREIGN KEY (driver_id) REFERENCES users(id)
);

-- Table des passagers des trajets
CREATE TABLE IF NOT EXISTS trip_passengers (
    trip_id TEXT NOT NULL,
    passenger_id TEXT NOT NULL,
    status TEXT NOT NULL,
    PRIMARY KEY (trip_id, passenger_id),
    FOREIGN KEY (trip_id) REFERENCES trips(id),
    FOREIGN KEY (passenger_id) REFERENCES users(id)
);

-- Insertion de l'administrateur par défaut is now handled by Java code
-- with proper BCrypt password hashing for security
-- See DatabaseConnection.java initializeDatabase() method