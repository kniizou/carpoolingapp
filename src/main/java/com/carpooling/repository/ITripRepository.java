package com.carpooling.repository;

import java.util.List;

import com.carpooling.model.Trip;
import com.carpooling.model.User;

/**
 * Repository interface for Trip data access operations.
 * Provides CRUD operations for Trip entities without business logic.
 */
public interface ITripRepository {
    
    /**
     * Saves a trip to the database.
     * 
     * @param trip The trip to save
     * @return true if save successful, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean save(Trip trip);
    
    /**
     * Finds a trip by its ID.
     * 
     * @param id The trip ID
     * @return The trip if found, null otherwise
     * @throws RuntimeException if database operation fails
     */
    Trip findById(String id);
    
    /**
     * Finds all trips in the database.
     * 
     * @return List of all trips with passengers loaded, empty list if none found
     * @throws RuntimeException if database operation fails
     */
    List<Trip> findAll();
    
    /**
     * Finds all trips for a specific driver.
     * 
     * @param driverEmail The driver's email address
     * @return List of trips for the driver, empty list if none found
     * @throws RuntimeException if database operation fails
     */
    List<Trip> findByDriverEmail(String driverEmail);
    
    /**
     * Finds all trips where a user is a passenger.
     * 
     * @param passenger The passenger user
     * @return List of trips for the passenger, empty list if none found
     * @throws RuntimeException if database operation fails
     */
    List<Trip> findByPassenger(User passenger);
    
    /**
     * Updates an existing trip in the database.
     * 
     * @param trip The trip to update
     * @return true if update successful, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean update(Trip trip);
    
    /**
     * Deletes a trip from the database.
     * 
     * @param trip The trip to delete
     * @return true if delete successful, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean delete(Trip trip);
    
    /**
     * Adds a passenger to a trip with a specific status.
     * 
     * @param trip The trip
     * @param passenger The passenger to add
     * @param status The initial status
     * @return true if passenger added successfully, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean addPassengerToTrip(Trip trip, User passenger, String status);
    
    /**
     * Updates the status of a passenger on a trip.
     * 
     * @param trip The trip
     * @param passenger The passenger
     * @param status The new status
     * @return true if status updated successfully, false otherwise
     * @throws RuntimeException if database operation fails
     */
    boolean updatePassengerStatus(Trip trip, User passenger, String status);
    
    /**
     * Loads passengers for a trip from the database.
     * 
     * @param trip The trip to load passengers for
     * @throws RuntimeException if database operation fails
     */
    void loadPassengers(Trip trip);
}
