package com.carpooling.service;

import com.carpooling.model.User;

/**
 * Service interface for authentication operations.
 * Handles user login, logout, and session management.
 */
public interface IAuthService {
    
    /**
     * Authenticates a user with email and password.
     * 
     * @param email User's email address
     * @param password User's plain text password
     * @return true if authentication successful, false otherwise
     * @throws IllegalArgumentException if email or password is null/empty
     */
    boolean authenticate(String email, String password);
    
    /**
     * Logs out the current user.
     */
    void logout();
    
    /**
     * Gets the currently authenticated user.
     * 
     * @return Current user or null if no user is authenticated
     */
    User getCurrentUser();
    
    /**
     * Checks if a user is currently authenticated.
     * 
     * @return true if a user is authenticated, false otherwise
     */
    boolean isAuthenticated();
    
    /**
     * Registers a new user with secure password hashing.
     * 
     * @param nom User's last name
     * @param prenom User's first name
     * @param age User's age
     * @param email User's email address
     * @param password User's plain text password (will be hashed)
     * @param role User's role code
     * @return The registered user with hashed password
     * @throws IllegalArgumentException if validation fails or user already exists
     */
    User register(String nom, String prenom, int age, String email, String password, String role);
}
