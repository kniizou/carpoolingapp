-- Création de la base de données
CREATE DATABASE IF NOT EXISTS carpoolingapp;
USE carpoolingapp;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (age >= 18)
);

-- Table des trajets
CREATE TABLE IF NOT EXISTS trips (
    id VARCHAR(50) PRIMARY KEY,
    driver_id INT NOT NULL,
    departure VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    date VARCHAR(20) NOT NULL,
    time VARCHAR(20) NOT NULL,
    available_seats INT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES users(id)
);

-- Table des passagers des trajets
CREATE TABLE IF NOT EXISTS trip_passengers (
    trip_id VARCHAR(50) NOT NULL,
    passenger_id INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (trip_id, passenger_id),
    FOREIGN KEY (trip_id) REFERENCES trips(id),
    FOREIGN KEY (passenger_id) REFERENCES users(id)
);

-- Insertion de l'administrateur par défaut
INSERT INTO users (nom, prenom, age, email, password, role)
VALUES ('Admin', 'System', 30, 'admin@admin.com', 'admin2025', 'ADMIN')
ON DUPLICATE KEY UPDATE id=id; 