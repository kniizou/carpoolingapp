package com.carpooling.service;

import java.util.List;

import com.carpooling.model.Trip;
import com.carpooling.model.User;

/**
 * Service interface for trip management operations.
 * Handles trip creation, retrieval, passenger management, and trip-related business logic.
 */
public interface ITripService {
    
    /**
     * Creates a new trip.
     * 
     * @param trip The trip to create
     * @throws IllegalArgumentException if trip is null or invalid
     */
    void createTrip(Trip trip);
    
    /**
     * Gets all available trips.
     * 
     * @return List of all trips, empty list if none found
     */
    List<Trip> getAllTrips();
    
    /**
     * Gets a trip by its ID.
     * 
     * @param id The trip ID
     * @return The trip if found, null otherwise
     * @throws IllegalArgumentException if id is null or empty
     */
    Trip getTripById(String id);
    
    /**
     * Gets all trips for a specific driver.
     * 
     * @param driverEmail The driver's email address
     * @return List of trips for the driver, empty list if none found
     * @throws IllegalArgumentException if driverEmail is null or empty
     */
    List<Trip> getTripsByDriver(String driverEmail);
    
    /**
     * Gets all trips where a user is a passenger.
     * 
     * @param passenger The passenger user
     * @return List of trips for the passenger, empty list if none found
     * @throws IllegalArgumentException if passenger is null
     */
    List<Trip> getTripsByPassenger(User passenger);
    
    /**
     * Adds a passenger to a trip with a specific status.
     * 
     * @param trip The trip to add passenger to
     * @param passenger The passenger to add
     * @param status The initial status of the passenger
     * @return true if passenger added successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is null
     */
    boolean addPassengerToTrip(Trip trip, User passenger, String status);
    
    /**
     * Updates the status of a passenger on a trip.
     * 
     * @param trip The trip
     * @param passenger The passenger
     * @param status The new status
     * @return true if status updated successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is null
     */
    boolean updatePassengerStatus(Trip trip, User passenger, String status);
    
    /**
     * Deletes a trip.
     * 
     * @param trip The trip to delete
     * @return true if trip deleted successfully, false otherwise
     * @throws IllegalArgumentException if trip is null
     */
    boolean deleteTrip(Trip trip);
    
    /**
     * Updates an existing trip.
     * 
     * @param trip The trip to update
     * @return true if trip updated successfully, false otherwise
     * @throws IllegalArgumentException if trip is null or invalid
     */
    boolean updateTrip(Trip trip);
    
    /**
     * Requests a seat on a trip for a passenger.
     * 
     * @param tripId The ID of the trip
     * @param passenger The passenger requesting the seat
     * @return true if seat requested successfully, false otherwise
     * @throws IllegalArgumentException if tripId is null/empty or passenger is null
     */
    boolean requestSeat(String tripId, User passenger);
    
    /**
     * Validates trip data before creation or update.
     * 
     * @param trip The trip to validate
     * @throws IllegalArgumentException if trip data is invalid
     */
    void validateTripData(Trip trip);
}
