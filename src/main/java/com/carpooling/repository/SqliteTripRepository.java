package com.carpooling.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.carpooling.data.DatabaseConnection;
import com.carpooling.model.Trip;
import com.carpooling.model.User;

/**
 * SQLite implementation of the Trip repository.
 * Handles all database operations for Trip entities.
 */
public class SqliteTripRepository implements ITripRepository {
    
    private static final Logger LOGGER = Logger.getLogger(SqliteTripRepository.class.getName());
    
    @Override
    public boolean save(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO trips (id, driver_id, departure, destination, date, time, available_seats, price, trip_type, recurring_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, trip.getId());
            pstmt.setString(2, trip.getDriver().getId());
            pstmt.setString(3, trip.getDeparture());
            pstmt.setString(4, trip.getDestination());
            pstmt.setString(5, trip.getDate());
            pstmt.setString(6, trip.getTime());
            pstmt.setInt(7, trip.getAvailableSeats());
            pstmt.setDouble(8, trip.getPrice());
            pstmt.setString(9, trip.getTripType());
            
            // Handle recurring days
            String recurringDays = "";
            if (trip.getRecurringDays() != null && !trip.getRecurringDays().isEmpty()) {
                recurringDays = String.join(",", trip.getRecurringDays());
            }
            pstmt.setString(10, recurringDays);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating trip", e);
            throw new RuntimeException("Error creating trip", e);
        }
    }
    
    @Override
    public Trip findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Trip ID cannot be null or empty");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.*, t.id as trip_id, u.id as driver_id, u.nom as driver_nom, u.prenom as driver_prenom, " +
                     "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                     "u.role as driver_role " +
                     "FROM trips t " +
                     "JOIN users u ON t.driver_id = u.id " +
                     "WHERE t.id = ?")) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Trip trip = createTripFromResultSet(rs);
                loadPassengers(trip);
                return trip;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving trip by ID", e);
            throw new RuntimeException("Error retrieving trip by ID", e);
        }
        return null;
    }
    
    @Override
    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String query = "SELECT t.*, t.id as trip_id, u.id as driver_id, u.nom as driver_nom, u.prenom as driver_prenom, " +
                          "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                          "u.role as driver_role, " +
                          "t.date as formatted_date, " +
                          "t.time as formatted_time " +
                          "FROM trips t " +
                          "JOIN users u ON t.driver_id = u.id " +
                          "ORDER BY t.date DESC, t.time ASC";
            
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Trip trip = createTripFromResultSet(rs);
                    trips.add(trip);
                }
            }
            
            // Load passengers for all trips efficiently
            for (Trip trip : trips) {
                loadPassengers(trip);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all trips", e);
            throw new RuntimeException("Error retrieving all trips", e);
        }
        
        return trips;
    }
    
    @Override
    public List<Trip> findByDriverEmail(String driverEmail) {
        if (driverEmail == null || driverEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver email cannot be null or empty");
        }

        List<Trip> trips = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.*, t.id as trip_id, u.id as driver_id, u.nom as driver_nom, u.prenom as driver_prenom, " +
                     "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                     "u.role as driver_role " +
                     "FROM trips t " +
                     "JOIN users u ON t.driver_id = u.id " +
                     "WHERE u.email = ? " +
                     "ORDER BY t.date DESC, t.time ASC")) {
            
            pstmt.setString(1, driverEmail);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Trip trip = createTripFromResultSet(rs);
                loadPassengers(trip);
                trips.add(trip);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving trips by driver email", e);
            throw new RuntimeException("Error retrieving trips by driver email", e);
        }
        
        return trips;
    }
    
    @Override
    public List<Trip> findByPassenger(User passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null");
        }

        List<Trip> trips = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT t.*, t.id as trip_id, u.id as driver_id, u.nom as driver_nom, u.prenom as driver_prenom, " +
                     "u.email as driver_email, u.age as driver_age, u.password as driver_password, " +
                     "u.role as driver_role " +
                     "FROM trips t " +
                     "JOIN users u ON t.driver_id = u.id " +
                     "JOIN trip_passengers tp ON t.id = tp.trip_id " +
                     "WHERE tp.passenger_id = ? " +
                     "ORDER BY t.date DESC, t.time ASC")) {
            
            pstmt.setString(1, passenger.getId());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Trip trip = createTripFromResultSet(rs);
                loadPassengers(trip);
                trips.add(trip);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving trips by passenger", e);
            throw new RuntimeException("Error retrieving trips by passenger", e);
        }
        
        return trips;
    }
    
    @Override
    public boolean update(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE trips SET departure = ?, destination = ?, date = ?, time = ?, available_seats = ?, price = ?, trip_type = ?, recurring_days = ? WHERE id = ?")) {
            
            pstmt.setString(1, trip.getDeparture());
            pstmt.setString(2, trip.getDestination());
            pstmt.setString(3, trip.getDate());
            pstmt.setString(4, trip.getTime());
            pstmt.setInt(5, trip.getAvailableSeats());
            pstmt.setDouble(6, trip.getPrice());
            pstmt.setString(7, trip.getTripType());
            
            // Handle recurring days
            String recurringDays = "";
            if (trip.getRecurringDays() != null && !trip.getRecurringDays().isEmpty()) {
                recurringDays = String.join(",", trip.getRecurringDays());
            }
            pstmt.setString(8, recurringDays);
            pstmt.setString(9, trip.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating trip", e);
            throw new RuntimeException("Error updating trip", e);
        }
    }
    
    @Override
    public boolean delete(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Delete trip passengers first
                try (PreparedStatement pstmt1 = conn.prepareStatement("DELETE FROM trip_passengers WHERE trip_id = ?")) {
                    pstmt1.setString(1, trip.getId());
                    pstmt1.executeUpdate();
                }
                
                // Delete trip
                try (PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM trips WHERE id = ?")) {
                    pstmt2.setString(1, trip.getId());
                    boolean result = pstmt2.executeUpdate() > 0;
                    
                    conn.commit();
                    return result;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting trip", e);
            throw new RuntimeException("Error deleting trip", e);
        }
    }
    
    @Override
    public boolean addPassengerToTrip(Trip trip, User passenger, String status) {
        if (trip == null || passenger == null || status == null) {
            throw new IllegalArgumentException("Trip, passenger, and status cannot be null");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction for atomic operation
            
            // First, check current seat availability with a database lock
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT available_seats, " +
                    "(SELECT COUNT(*) FROM trip_passengers WHERE trip_id = ? AND status = 'CONFIRMED') as confirmed_count " +
                    "FROM trips WHERE id = ? FOR UPDATE")) {
                
                checkStmt.setString(1, trip.getId());
                checkStmt.setString(2, trip.getId());
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Trip not found");
                    }
                    
                    int availableSeats = rs.getInt("available_seats");
                    int confirmedCount = rs.getInt("confirmed_count");
                    
                    // For CONFIRMED status, check if seats are available
                    if ("CONFIRMED".equals(status) && confirmedCount >= availableSeats) {
                        conn.rollback();
                        throw new IllegalStateException("No available seats for this trip");
                    }
                }
            }
            
            // Check if passenger is already on the trip (prevent duplicates)
            try (PreparedStatement dupCheckStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM trip_passengers WHERE trip_id = ? AND passenger_id = ?")) {
                
                dupCheckStmt.setString(1, trip.getId());
                dupCheckStmt.setString(2, passenger.getId());
                
                try (ResultSet rs = dupCheckStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        throw new IllegalStateException("Passenger is already on this trip");
                    }
                }
            }
            
            // Insert the passenger
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO trip_passengers (trip_id, passenger_id, status) VALUES (?, ?, ?)")) {
                
                insertStmt.setString(1, trip.getId());
                insertStmt.setString(2, passenger.getId());
                insertStmt.setString(3, status);
                
                boolean success = insertStmt.executeUpdate() > 0;
                
                if (success) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Error adding passenger to trip atomically", e);
            throw new RuntimeException("Error adding passenger to trip", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
            }
        }
    }
    
    @Override
    public boolean updatePassengerStatus(Trip trip, User passenger, String status) {
        if (trip == null || passenger == null || status == null) {
            throw new IllegalArgumentException("Trip, passenger, and status cannot be null");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction for atomic operation
            
            // If updating to CONFIRMED, check seat availability
            if ("CONFIRMED".equals(status)) {
                try (PreparedStatement checkStmt = conn.prepareStatement(
                        "SELECT available_seats, " +
                        "(SELECT COUNT(*) FROM trip_passengers WHERE trip_id = ? AND status = 'CONFIRMED' AND passenger_id != ?) as confirmed_count " +
                        "FROM trips WHERE id = ? FOR UPDATE")) {
                    
                    checkStmt.setString(1, trip.getId());
                    checkStmt.setString(2, passenger.getId());
                    checkStmt.setString(3, trip.getId());
                    
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Trip not found");
                        }
                        
                        int availableSeats = rs.getInt("available_seats");
                        int confirmedCount = rs.getInt("confirmed_count");
                        
                        if (confirmedCount >= availableSeats) {
                            conn.rollback();
                            throw new IllegalStateException("No available seats for confirmation");
                        }
                    }
                }
            }
            
            // Update the passenger status
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE trip_passengers SET status = ? WHERE trip_id = ? AND passenger_id = ?")) {
                
                updateStmt.setString(1, status);
                updateStmt.setString(2, trip.getId());
                updateStmt.setString(3, passenger.getId());
                
                boolean success = updateStmt.executeUpdate() > 0;
                
                if (success) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Error updating passenger status atomically", e);
            throw new RuntimeException("Error updating passenger status", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
            }
        }
    }
    
    @Override
    public void loadPassengers(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT u.*, tp.status " +
                     "FROM trip_passengers tp " +
                     "JOIN users u ON tp.passenger_id = u.id " +
                     "WHERE tp.trip_id = ?")) {
            
            pstmt.setString(1, trip.getId());
            
            // Clear existing passengers
            trip.getPassengers().clear();
            trip.getPendingPassengers().clear();
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User passenger = new User(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getInt("age"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                    
                    String status = rs.getString("status");
                    
                    if ("CONFIRMED".equals(status)) {
                        trip.getPassengers().add(passenger);
                    } else if ("PENDING".equals(status)) {
                        trip.getPendingPassengers().add(passenger);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading passengers for trip", e);
            throw new RuntimeException("Error loading passengers for trip", e);
        }
    }
    
    /**
     * Helper method to create a Trip object from a ResultSet.
     */
    private Trip createTripFromResultSet(ResultSet rs) throws SQLException {
        // Create driver
        User driver = new User(
            rs.getString("driver_id"),
            rs.getString("driver_nom"),
            rs.getString("driver_prenom"),
            rs.getInt("driver_age"),
            rs.getString("driver_email"),
            rs.getString("driver_password"),
            rs.getString("driver_role")
        );
        
        String tripId = rs.getString("trip_id");
        String departure = rs.getString("departure");
        String destination = rs.getString("destination");
        String formattedDate = rs.getString("date");
        String formattedTime = rs.getString("time");
        int availableSeats = rs.getInt("available_seats");
        double price = rs.getDouble("price");
        String tripType = rs.getString("trip_type");
        
        // Validate and fix date if invalid
        try {
            LocalDate.parse(formattedDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            LOGGER.warning("Invalid date found for trip " + tripId + ": " + formattedDate + ". Replacing with 01/01/2000.");
            formattedDate = "01/01/2000"; // Valid default date
        }
        
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
        
        return trip;
    }
}
