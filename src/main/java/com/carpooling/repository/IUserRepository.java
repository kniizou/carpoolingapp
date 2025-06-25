package com.carpooling.repository;

import java.util.List;

import com.carpooling.model.User;

/**
 * Repository interface for User data access operations.
 * Provides CRUD operations for User entities without business logic.
 */
public interface IUserRepository {
    
    /**
     * Saves a user to the database.
     * 
     * @param user The user to save
     * @return true if save successful, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean save(User user);
    
    /**
     * Finds a user by their email address.
     * 
     * @param email The user's email address
     * @return The user if found, null otherwise
     * @throws RuntimeException if database operation fails
     */
    User findByEmail(String email);
    
    /**
     * Finds a user by their name.
     * 
     * @param name The user's name
     * @return The user if found, null otherwise
     * @throws RuntimeException if database operation fails
     */
    User findByName(String name);
    
    /**
     * Finds all users in the database.
     * 
     * @return List of all users, empty list if none found
     * @throws RuntimeException if database operation fails
     */
    List<User> findAll();
    
    /**
     * Updates an existing user in the database.
     * 
     * @param user The user to update
     * @return true if update successful, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean update(User user);
    
    /**
     * Deletes a user from the database.
     * 
     * @param user The user to delete
     * @return true if delete successful, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean delete(User user);
    
    /**
     * Checks if a user exists with the given email.
     * 
     * @param email The email to check
     * @return true if user exists, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean existsByEmail(String email);
}
