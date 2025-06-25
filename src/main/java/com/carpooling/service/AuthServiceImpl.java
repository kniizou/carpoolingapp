package com.carpooling.service;

import java.util.logging.Logger;

import com.carpooling.model.User;
import com.carpooling.repository.IUserRepository;
import com.carpooling.util.SecurityUtils;

/**
 * Implementation of the authentication service.
 * Handles user authentication and session management.
 */
public class AuthServiceImpl implements IAuthService {
    
    private static final Logger LOGGER = Logger.getLogger(AuthServiceImpl.class.getName());
    private final IUserRepository userRepository;
    private User currentUser;
    
    public AuthServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        User user = userRepository.findByEmail(email);
        if (user != null && SecurityUtils.verifyPassword(password, user.getPassword())) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    @Override
    public void logout() {
        currentUser = null;
    }
    
    @Override
    public User getCurrentUser() {
        return currentUser;
    }
    
    @Override
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * Administrative method to clear all user data from the database.
     * This is intended for testing and administrative purposes only.
     * 
     * @param adminConfirmation Must be "ADMIN_CLEAR_DATABASE" to proceed
     * @throws IllegalArgumentException if confirmation is incorrect
     * @throws RuntimeException if database operation fails
     */
    public void clearAllUserData(String adminConfirmation) {
        if (!"ADMIN_CLEAR_DATABASE".equals(adminConfirmation)) {
            throw new IllegalArgumentException("Invalid admin confirmation for database clearing");
        }
        
        try {
            // Use direct SQL to clear data
            java.sql.Connection conn = com.carpooling.data.DatabaseConnection.getConnection();
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM trip_passengers");
                stmt.execute("DELETE FROM trips");
                stmt.execute("DELETE FROM users WHERE email != 'admin@admin.com'"); // Keep admin
                
                // Reset current user
                currentUser = null;
                
                LOGGER.info("Database cleared successfully - admin user preserved");
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }
    
    @Override
    public User register(String nom, String prenom, int age, String email, String password, String role) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Check if user already exists
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("A user with this email already exists");
        }
        
        // Hash the password before creating user
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        // Create user with hashed password
        User newUser = new User(nom, prenom, age, email, hashedPassword, role);
        
        // Save user to repository
        boolean saved = userRepository.save(newUser);
        if (!saved) {
            throw new RuntimeException("Failed to save user to database");
        }
        
        return newUser;
    }
}
