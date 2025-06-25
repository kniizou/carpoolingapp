package com.carpooling.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.carpooling.data.DatabaseConnection;
import com.carpooling.model.User;

/**
 * SQLite implementation of the User repository.
 * Handles all database operations for User entities.
 */
public class SqliteUserRepository implements IUserRepository {
    
    private static final Logger LOGGER = Logger.getLogger(SqliteUserRepository.class.getName());
    
    @Override
    public boolean save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO users (id, nom, prenom, age, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getNom());
            pstmt.setString(3, user.getPrenom());
            pstmt.setInt(4, user.getAge());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPassword());
            pstmt.setString(7, user.getRole());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user", e);
            throw new RuntimeException("Error adding user", e);
        }
    }
    
    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE email = ?")) {
            
            pstmt.setString(1, email.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();
            
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
            LOGGER.log(Level.SEVERE, "Error retrieving user by email", e);
            throw new RuntimeException("Error retrieving user by email", e);
        }
        return null;
    }
    
    @Override
    public User findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE LOWER(nom) LIKE LOWER(?) OR LOWER(prenom) LIKE LOWER(?)")) {
            
            String namePattern = "%" + name.toLowerCase().trim() + "%";
            pstmt.setString(1, namePattern);
            pstmt.setString(2, namePattern);
            ResultSet rs = pstmt.executeQuery();
            
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
            LOGGER.log(Level.SEVERE, "Error retrieving user by name", e);
            throw new RuntimeException("Error retrieving user by name", e);
        }
        return null;
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users ORDER BY nom, prenom");
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("age"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
            throw new RuntimeException("Error retrieving all users", e);
        }
        
        return users;
    }
    
    @Override
    public boolean update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE users SET nom = ?, prenom = ?, age = ?, email = ?, password = ?, role = ? WHERE id = ?")) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setInt(3, user.getAge());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole());
            pstmt.setString(7, user.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            throw new RuntimeException("Error updating user", e);
        }
    }
    
    @Override
    public boolean delete(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            
            pstmt.setString(1, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            throw new RuntimeException("Error deleting user", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT 1 FROM users WHERE email = ?")) {
            
            pstmt.setString(1, email.toLowerCase().trim());
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists by email", e);
            throw new RuntimeException("Error checking if user exists by email", e);
        }
    }
}
