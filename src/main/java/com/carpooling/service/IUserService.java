package com.carpooling.service;

import java.util.List;

import com.carpooling.model.User;

/**
 * Service interface for user management operations.
 * Handles user registration, retrieval, and user-related business logic.
 */
public interface IUserService {
    
    /**
     * Registers a new user in the system.
     * 
     * @param user The user to register
     * @return true if registration successful, false otherwise
     * @throws IllegalArgumentException if user is null or invalid
     */
    boolean registerUser(User user);
    
    /**
     * Retrieves a user by their email address.
     * 
     * @param email The user's email address
     * @return The user if found, null otherwise
     * @throws IllegalArgumentException if email is null or empty
     */
    User getUserByEmail(String email);
    
    /**
     * Retrieves a user by their name.
     * 
     * @param name The user's name
     * @return The user if found, null otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    User getUserByName(String name);
    
    /**
     * Retrieves all users in the system.
     * 
     * @return List of all users, empty list if none found
     */
    List<User> getAllUsers();
    
    /**
     * Validates user data before registration.
     * 
     * @param user The user to validate
     * @throws IllegalArgumentException if user data is invalid
     */
    void validateUserData(User user);
}
