package com.carpooling.data;

import com.carpooling.model.User;
import com.carpooling.model.Trip;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DataManager {
    private static final Logger LOGGER = Logger.getLogger(DataManager.class.getName());
    private static DataManager instance;
    private User currentUser;

    private DataManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Création de la table users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nom VARCHAR(50) NOT NULL," +
                    "prenom VARCHAR(50) NOT NULL," +
                    "age INT NOT NULL," +
                    "email VARCHAR(100) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "role VARCHAR(20) NOT NULL" +
                    ")");

            // Création de la table trips
            stmt.execute("CREATE TABLE IF NOT EXISTS trips (" +
                    "id VARCHAR(50) PRIMARY KEY," +
                    "driver_id INT NOT NULL," +
                    "departure VARCHAR(100) NOT NULL," +
                    "destination VARCHAR(100) NOT NULL," +
                    "date DATE NOT NULL," +
                    "time TIME NOT NULL," +
                    "available_seats INT NOT NULL," +
                    "price DECIMAL(10,2) NOT NULL," +
                    "trip_type VARCHAR(50) NOT NULL DEFAULT 'Occasionnel'," +
                    "recurring_days VARCHAR(100)," +
                    "FOREIGN KEY (driver_id) REFERENCES users(id)" +
                    ")");

            // Création de la table trip_passengers
            stmt.execute("CREATE TABLE IF NOT EXISTS trip_passengers (" +
                    "trip_id VARCHAR(50) NOT NULL," +
                    "passenger_id INT NOT NULL," +
                    "status VARCHAR(20) NOT NULL," +
                    "PRIMARY KEY (trip_id, passenger_id)," +
                    "FOREIGN KEY (trip_id) REFERENCES trips(id)," +
                    "FOREIGN KEY (passenger_id) REFERENCES users(id)" +
                    ")");

            // Vérifier si l'admin existe déjà
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email = 'admin@admin.com'")) {
                if (!rs.next()) {
                    // Créer l'admin par défaut
                    stmt.execute("INSERT INTO users (nom, prenom, age, email, password, role) VALUES " +
                            "('Admin', 'System', 30, 'admin@admin.com', 'admin2025', 'ADMIN')");
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation de la base de données", e);
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données", e);
        }
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public boolean addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (nom, prenom, age, email, password, role) VALUES (?, ?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setInt(3, user.getAge());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de l'utilisateur", e);
            throw new RuntimeException("Erreur lors de l'ajout de l'utilisateur", e);
        }
    }

    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE email = ?")) {
            
            pstmt.setString(1, email.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("age"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'utilisateur", e);
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur", e);
        }
        return null;
    }

    public boolean authenticateUser(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }

        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public void createTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Le trajet ne peut pas être null");
        }

        LOGGER.info("Création d'un nouveau trajet - ID: " + trip.getId());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO trips (id, driver_id, departure, destination, date, time, available_seats, price, trip_type, recurring_days) " +
                     "VALUES (?, (SELECT id FROM users WHERE email = ?), ?, ?, STR_TO_DATE(?, '%d/%m/%Y'), ?, ?, ?, ?, ?)")) {
            
            String tripId = trip.getId();
            if (tripId == null || tripId.trim().isEmpty()) {
                tripId = UUID.randomUUID().toString();
                LOGGER.info("Nouvel ID généré pour le trajet: " + tripId);
            }
            
            pstmt.setString(1, tripId);
            pstmt.setString(2, trip.getDriver().getEmail());
            pstmt.setString(3, trip.getDeparture());
            pstmt.setString(4, trip.getDestination());
            pstmt.setString(5, trip.getDate());
            pstmt.setString(6, trip.getTime());
            pstmt.setInt(7, trip.getAvailableSeats());
            pstmt.setDouble(8, trip.getPrice());
            pstmt.setString(9, trip.getTripType());
            pstmt.setString(10, String.join(",", trip.getRecurringDays()));
            
            int rowsAffected = pstmt.executeUpdate();
            LOGGER.info("Nombre de lignes affectées lors de la création du trajet: " + rowsAffected);
            
            if (rowsAffected == 0) {
                throw new RuntimeException("Erreur lors de la création du trajet");
            }

            // Vérifier que le trajet a bien été créé
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM trips WHERE id = ?")) {
                checkStmt.setString(1, tripId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        LOGGER.severe("Le trajet n'a pas été créé correctement dans la base de données");
                        throw new RuntimeException("Le trajet n'a pas été créé correctement");
                    }
                    LOGGER.info("Vérification réussie : le trajet existe dans la base de données");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du trajet", e);
            throw new RuntimeException("Erreur lors de la création du trajet", e);
        }
    }

    public boolean addPassengerToTrip(Trip trip, User passenger, String status) {
        if (trip == null) {
            throw new IllegalArgumentException("Le trajet ne peut pas être null");
        }
        if (passenger == null) {
            throw new IllegalArgumentException("Le passager ne peut pas être null");
        }
        if (status == null || (!status.equals("CONFIRMED") && !status.equals("PENDING") && !status.equals("REJECTED"))) {
            throw new IllegalArgumentException("Le statut doit être CONFIRMED, PENDING ou REJECTED");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO trip_passengers (trip_id, passenger_id, status) " +
                     "VALUES (?, (SELECT id FROM users WHERE email = ?), ?) " +
                     "ON DUPLICATE KEY UPDATE status = ?")) {
            
            pstmt.setString(1, trip.getId());
            pstmt.setString(2, passenger.getEmail());
            pstmt.setString(3, status);
            pstmt.setString(4, status);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du passager au trajet", e);
            throw new RuntimeException("Erreur lors de l'ajout du passager au trajet", e);
        }
    }

    public boolean updatePassengerStatus(Trip trip, User passenger, String status) {
        if (trip == null) {
            throw new IllegalArgumentException("Le trajet ne peut pas être null");
        }
        if (passenger == null) {
            throw new IllegalArgumentException("Le passager ne peut pas être null");
        }
        if (status == null || (!status.equals("CONFIRMED") && !status.equals("PENDING") && !status.equals("REJECTED"))) {
            throw new IllegalArgumentException("Le statut doit être CONFIRMED, PENDING ou REJECTED");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Mettre à jour le statut du passager
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE trip_passengers SET status = ? " +
                        "WHERE trip_id = ? AND passenger_id = (SELECT id FROM users WHERE email = ?)")) {
                    
                    pstmt.setString(1, status);
                    pstmt.setString(2, trip.getId());
                    pstmt.setString(3, passenger.getEmail());
                    
                    pstmt.executeUpdate();
                }

                // Si le statut est CONFIRMED, décrémenter le nombre de places disponibles
                if ("CONFIRMED".equals(status)) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE trips SET available_seats = available_seats - 1 " +
                            "WHERE id = ? AND available_seats > 0")) {
                        
                        pstmt.setString(1, trip.getId());
                        int updated = pstmt.executeUpdate();
                        
                        if (updated == 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                // Si le statut est REJECTED et que le passager était CONFIRMED, incrémenter le nombre de places
                else if ("REJECTED".equals(status)) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE trips SET available_seats = available_seats + 1 " +
                            "WHERE id = ? AND EXISTS (SELECT 1 FROM trip_passengers " +
                            "WHERE trip_id = ? AND passenger_id = (SELECT id FROM users WHERE email = ?) " +
                            "AND status = 'CONFIRMED')")) {
                        
                        pstmt.setString(1, trip.getId());
                        pstmt.setString(2, trip.getId());
                        pstmt.setString(3, passenger.getEmail());
                        
                        pstmt.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du statut du passager", e);
            throw new RuntimeException("Erreur lors de la mise à jour du statut du passager", e);
        }
    }

    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String query = "SELECT t.*, t.id as trip_id, u.id as driver_id, u.nom as driver_nom, u.prenom as driver_prenom, " +
                          "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                          "u.role as driver_role, " +
                          "DATE_FORMAT(t.date, '%d/%m/%Y') as formatted_date, " +
                          "DATE_FORMAT(t.time, '%H:%i') as formatted_time " +
                          "FROM trips t " +
                          "JOIN users u ON t.driver_id = u.id " +
                          "ORDER BY t.date DESC, t.time ASC";
            
            LOGGER.info("Exécution de la requête : " + query);
            
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String driverId = rs.getString("driver_id");
                    String driverNom = rs.getString("driver_nom");
                    String driverPrenom = rs.getString("driver_prenom");
                    int driverAge = rs.getInt("driver_age");
                    String driverEmail = rs.getString("driver_email");
                    String driverPassword = rs.getString("driver_password");
                    String driverRole = rs.getString("driver_role");
                    
                    LOGGER.info("Création du conducteur - ID: " + driverId + ", Email: " + driverEmail + ", Rôle: " + driverRole);
                    
                    User driver = new User(
                        driverId,
                        driverNom,
                        driverPrenom,
                        driverAge,
                        driverEmail,
                        driverPassword,
                        driverRole
                    );
                    
                    String tripId = rs.getString("trip_id");
                    String departure = rs.getString("departure");
                    String destination = rs.getString("destination");
                    String formattedDate = rs.getString("formatted_date");
                    String formattedTime = rs.getString("formatted_time");
                    int availableSeats = rs.getInt("available_seats");
                    double price = rs.getDouble("price");
                    String tripType = rs.getString("trip_type");
                    
                    // Validation et remplacement de la date si invalide
                    try {
                        LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (DateTimeParseException e) {
                        LOGGER.warning("Date invalide trouvée pour le trajet " + tripId + ": " + formattedDate + ". Remplacement par 01/01/2000.");
                        formattedDate = "01/01/2000"; // Date par défaut valide
                    }

                    LOGGER.info("Création du trajet - ID: " + tripId + 
                              ", Départ: " + departure + 
                              ", Destination: " + destination + 
                              ", Date: " + formattedDate + 
                              ", Heure: " + formattedTime + 
                              ", Places: " + availableSeats + 
                              ", Prix: " + price + 
                              ", Type: " + tripType);
                    
                    Trip trip = new Trip(
                        tripId,
                        driver,
                        departure,
                        destination,
                        formattedDate,
                        formattedTime,
                        availableSeats,
                        price
                    );
                    
                    trip.setTripType(tripType);
                    String recurringDays = rs.getString("recurring_days");
                    if (recurringDays != null && !recurringDays.isEmpty()) {
                        trip.setRecurringDays(Arrays.asList(recurringDays.split(",")));
                    }
                    
                    trips.add(trip);
                }
            }
            
            LOGGER.info("Nombre total de trajets trouvés : " + trips.size());
            
            // Charger les passagers pour chaque trajet
            for (Trip trip : trips) {
                loadPassengers(trip);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des trajets", e);
            throw new RuntimeException("Erreur lors de la récupération des trajets", e);
        }
        return trips;
    }

    private void loadPassengers(Trip trip) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT u.*, tp.status " +
                     "FROM trip_passengers tp " +
                     "JOIN users u ON tp.passenger_id = u.id " +
                     "WHERE tp.trip_id = ?")) {
            
            pstmt.setString(1, trip.getId());
            
            LOGGER.info("Chargement des passagers pour le trajet " + trip.getId());
            
            // Vider les listes avant de les remplir
            trip.getPassengers().clear();
            trip.getPendingPassengers().clear();
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String passengerId = rs.getString("id");
                    String passengerNom = rs.getString("nom");
                    String passengerPrenom = rs.getString("prenom");
                    int passengerAge = rs.getInt("age");
                    String passengerEmail = rs.getString("email");
                    String passengerPassword = rs.getString("password");
                    String passengerRole = rs.getString("role");
                    String status = rs.getString("status");
                    
                    LOGGER.info("Passager trouvé - ID: " + passengerId + 
                              ", Email: " + passengerEmail + 
                              ", Statut: " + status);
                    
                    User passenger = new User(
                        passengerId,
                        passengerNom,
                        passengerPrenom,
                        passengerAge,
                        passengerEmail,
                        passengerPassword,
                        passengerRole
                    );
                    
                    if ("CONFIRMED".equals(status)) {
                        trip.getPassengers().add(passenger);
                        LOGGER.info("Passager ajouté à la liste des passagers confirmés");
                    } else if ("PENDING".equals(status)) {
                        trip.getPendingPassengers().add(passenger);
                        LOGGER.info("Passager ajouté à la liste des passagers en attente");
                    }
                }
            }
            
            LOGGER.info("Nombre de passagers confirmés : " + trip.getPassengers().size());
            LOGGER.info("Nombre de passagers en attente : " + trip.getPendingPassengers().size());
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des passagers", e);
            throw new RuntimeException("Erreur lors du chargement des passagers", e);
        }
    }

    public List<Trip> getTripsByDriver(String driverEmail) {
        List<Trip> trips = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.*, u.nom as driver_nom, u.prenom as driver_prenom, " +
                     "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                     "DATE_FORMAT(t.date, '%d/%m/%Y') as formatted_date, " +
                     "DATE_FORMAT(t.time, '%H:%i') as formatted_time " +
                     "FROM trips t " +
                     "JOIN users u ON t.driver_id = u.id " +
                     "WHERE u.email = ?")) {
            
            pstmt.setString(1, driverEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User driver = new User(
                        rs.getString("driver_nom"),
                        rs.getString("driver_prenom"),
                        rs.getInt("driver_age"),
                        rs.getString("driver_email"),
                        rs.getString("driver_password"),
                        "DRIVER"
                    );
                    
                    Trip trip = new Trip(
                        driver,
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getString("formatted_date"),
                        rs.getString("formatted_time"),
                        rs.getInt("available_seats"),
                        rs.getDouble("price")
                    );
                    
                    // Définir le type de trajet et les jours récurrents
                    trip.setTripType(rs.getString("trip_type"));
                    String recurringDays = rs.getString("recurring_days");
                    if (recurringDays != null && !recurringDays.isEmpty()) {
                        trip.setRecurringDays(Arrays.asList(recurringDays.split(",")));
                    }
                    
                    trips.add(trip);
                }
            }
            
            // Charger les passagers pour chaque trajet
            for (Trip trip : trips) {
                loadPassengers(trip);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des trajets du conducteur", e);
            throw new RuntimeException("Erreur lors de la récupération des trajets du conducteur", e);
        }
        return trips;
    }

    public List<Trip> getTripsByPassenger(User passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Le passager ne peut pas être null");
        }

        List<Trip> trips = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.*, u.nom as driver_nom, u.prenom as driver_prenom, " +
                     "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                     "DATE_FORMAT(t.date, '%d/%m/%Y') as formatted_date, " +
                     "DATE_FORMAT(t.time, '%H:%i') as formatted_time, " +
                     "tp.status as passenger_status " +
                     "FROM trips t " +
                     "JOIN users u ON t.driver_id = u.id " +
                     "JOIN trip_passengers tp ON t.id = tp.trip_id " +
                     "WHERE tp.passenger_id = (SELECT id FROM users WHERE email = ?)")) {
            
            pstmt.setString(1, passenger.getEmail());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User driver = new User(
                        rs.getString("driver_nom"),
                        rs.getString("driver_prenom"),
                        rs.getInt("driver_age"),
                        rs.getString("driver_email"),
                        rs.getString("driver_password"),
                        "DRIVER"
                    );
                    
                    Trip trip = new Trip(
                        driver,
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getString("formatted_date"),
                        rs.getString("formatted_time"),
                        rs.getInt("available_seats"),
                        rs.getDouble("price")
                    );
                    
                    String status = rs.getString("passenger_status");
                    if ("CONFIRMED".equals(status)) {
                        trip.getPassengers().add(passenger);
                    } else if ("PENDING".equals(status)) {
                        trip.getPendingPassengers().add(passenger);
                    }
                    
                    trips.add(trip);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des trajets du passager", e);
            throw new RuntimeException("Erreur lors de la récupération des trajets du passager", e);
        }
        return trips;
    }

    public Trip getTripById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du trajet ne peut pas être vide");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.*, u.nom as driver_nom, u.prenom as driver_prenom, " +
                     "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                     "DATE_FORMAT(t.date, '%d/%m/%Y') as formatted_date, " +
                     "DATE_FORMAT(t.time, '%H:%i') as formatted_time " +
                     "FROM trips t " +
                     "JOIN users u ON t.driver_id = u.id " +
                     "WHERE t.id = ?")) {
            
            pstmt.setString(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User driver = new User(
                        rs.getString("driver_nom"),
                        rs.getString("driver_prenom"),
                        rs.getInt("driver_age"),
                        rs.getString("driver_email"),
                        rs.getString("driver_password"),
                        "DRIVER"
                    );
                    
                    Trip trip = new Trip(
                        id,
                        driver,
                        rs.getString("departure"),
                        rs.getString("destination"),
                        rs.getString("formatted_date"),
                        rs.getString("formatted_time"),
                        rs.getInt("available_seats"),
                        rs.getDouble("price")
                    );
                    
                    // Charger les passagers
                    loadPassengers(trip);
                    
                    return trip;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du trajet", e);
            throw new RuntimeException("Erreur lors de la récupération du trajet", e);
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("age"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des utilisateurs", e);
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs", e);
        }
        return users;
    }

    public User getUserByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE CONCAT(nom, ' ', prenom) = ?")) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("age"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de l'utilisateur par nom", e);
        }
        return null;
    }

    public boolean deleteTrip(Trip trip) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Supprimer d'abord les entrées dans trip_passengers
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM trip_passengers WHERE trip_id = ?")) {
                    stmt.setString(1, trip.getId());
                    stmt.executeUpdate();
                }
                
                // Ensuite supprimer le trajet
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM trips WHERE id = ?")) {
                    stmt.setString(1, trip.getId());
                    stmt.executeUpdate();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du trajet", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la connexion à la base de données", e);
            return false;
        }
    }

    public boolean updateTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Le trajet ne peut pas être null");
        }

        LOGGER.info("Mise à jour du trajet - ID: " + trip.getId());

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Mise à jour du trajet
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE trips SET " +
                        "departure = ?, " +
                        "destination = ?, " +
                        "date = STR_TO_DATE(?, '%d/%m/%Y'), " +
                        "time = ?, " +
                        "available_seats = ?, " +
                        "price = ?, " +
                        "trip_type = ?, " +
                        "recurring_days = ? " +
                        "WHERE id = ?")) {
                    
                    pstmt.setString(1, trip.getDeparture());
                    pstmt.setString(2, trip.getDestination());
                    pstmt.setString(3, trip.getDate());
                    pstmt.setString(4, trip.getTime());
                    pstmt.setInt(5, trip.getAvailableSeats());
                    pstmt.setDouble(6, trip.getPrice());
                    pstmt.setString(7, trip.getTripType());
                    pstmt.setString(8, String.join(",", trip.getRecurringDays()));
                    pstmt.setString(9, trip.getId());
                    
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected == 0) {
                        conn.rollback();
                        LOGGER.severe("Aucun trajet n'a été mis à jour");
                        return false;
                    }
                    
                    conn.commit();
                    LOGGER.info("Trajet mis à jour avec succès");
                    return true;
                }
            } catch (SQLException e) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du trajet", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la connexion à la base de données", e);
            return false;
        }
    }

    public void updateTripPassengerStatus(Trip trip, User passenger, String status) {
        if (trip == null || passenger == null || status == null) {
            throw new IllegalArgumentException("Le trajet, le passager et le statut ne peuvent pas être null");
        }

        LOGGER.info("Mise à jour du statut du passager - Trip ID: " + trip.getId() + 
                   ", Passenger Email: " + passenger.getEmail() + 
                   ", Status: " + status);

        // Vérifier d'abord si le trajet existe
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT id FROM trips WHERE id = ?")) {
            
            checkStmt.setString(1, trip.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    LOGGER.severe("Le trajet avec l'ID " + trip.getId() + " n'existe pas dans la base de données");
                    throw new RuntimeException("Le trajet n'existe pas dans la base de données");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du trajet", e);
            throw new RuntimeException("Erreur lors de la vérification du trajet", e);
        }

        // Vérifier si le passager existe
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT id FROM users WHERE email = ?")) {
            
            checkStmt.setString(1, passenger.getEmail());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    LOGGER.severe("Le passager avec l'email " + passenger.getEmail() + " n'existe pas dans la base de données");
                    throw new RuntimeException("Le passager n'existe pas dans la base de données");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification du passager", e);
            throw new RuntimeException("Erreur lors de la vérification du passager", e);
        }

        // Mettre à jour le statut
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO trip_passengers (trip_id, passenger_id, status) " +
                     "VALUES (?, (SELECT id FROM users WHERE email = ?), ?) " +
                     "ON DUPLICATE KEY UPDATE status = ?")) {
            
            String tripId = trip.getId();
            LOGGER.info("Trip ID avant insertion: " + tripId);
            
            pstmt.setString(1, tripId);
            pstmt.setString(2, passenger.getEmail());
            pstmt.setString(3, status);
            pstmt.setString(4, status);
            
            int rowsAffected = pstmt.executeUpdate();
            LOGGER.info("Nombre de lignes affectées: " + rowsAffected);
            
            if (rowsAffected == 0) {
                throw new RuntimeException("Erreur lors de la mise à jour du statut du passager");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du statut du passager", e);
            throw new RuntimeException("Erreur lors de la mise à jour du statut du passager", e);
        }
    }
} 