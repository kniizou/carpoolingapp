package com.carpooling.service;

import java.util.List;

import com.carpooling.model.Trip;
import com.carpooling.model.User;
import com.carpooling.repository.ITripRepository;

/**
 * Implementation of the trip service.
 * Handles trip creation, retrieval, passenger management, and validation with business logic.
 */
public class TripServiceImpl implements ITripService {
    
    private final ITripRepository tripRepository;
    
    public TripServiceImpl(ITripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }
    
    @Override
    public void createTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }
        
        validateTripData(trip);
        
        if (!tripRepository.save(trip)) {
            throw new RuntimeException("Failed to create trip");
        }
    }
    
    @Override
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }
    
    @Override
    public Trip getTripById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Trip ID cannot be null or empty");
        }
        
        return tripRepository.findById(id);
    }
    
    @Override
    public List<Trip> getTripsByDriver(String driverEmail) {
        if (driverEmail == null || driverEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver email cannot be null or empty");
        }
        
        return tripRepository.findByDriverEmail(driverEmail);
    }
    
    @Override
    public List<Trip> getTripsByPassenger(User passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null");
        }
        
        return tripRepository.findByPassenger(passenger);
    }
    
    @Override
    public boolean addPassengerToTrip(Trip trip, User passenger, String status) {
        if (trip == null || passenger == null || status == null) {
            throw new IllegalArgumentException("Trip, passenger, and status cannot be null");
        }
        
        // Business logic: Check if passenger is not the driver
        if (passenger.getId().equals(trip.getDriver().getId())) {
            throw new IllegalArgumentException("Driver cannot be a passenger on their own trip");
        }
        
        // Atomic seat availability and duplicate checking is handled in repository layer
        return tripRepository.addPassengerToTrip(trip, passenger, status);
    }
    
    @Override
    public boolean updatePassengerStatus(Trip trip, User passenger, String status) {
        if (trip == null || passenger == null || status == null) {
            throw new IllegalArgumentException("Trip, passenger, and status cannot be null");
        }
        
        // Atomic seat availability checking is handled in repository layer
        return tripRepository.updatePassengerStatus(trip, passenger, status);
    }
    
    @Override
    public boolean deleteTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }
        
        return tripRepository.delete(trip);
    }
    
    @Override
    public boolean updateTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }
        
        validateTripData(trip);
        
        return tripRepository.update(trip);
    }
    
    @Override
    public boolean requestSeat(String tripId, User passenger) {
        if (tripId == null || tripId.trim().isEmpty()) {
            throw new IllegalArgumentException("Trip ID cannot be null or empty");
        }
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null");
        }
        
        Trip trip = tripRepository.findById(tripId);
        if (trip == null) {
            throw new IllegalArgumentException("Trip not found");
        }
        
        return addPassengerToTrip(trip, passenger, "PENDING");
    }
    
    @Override
    public void validateTripData(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }
        
        if (trip.getDriver() == null) {
            throw new IllegalArgumentException("Trip must have a driver");
        }
        
        if (trip.getDeparture() == null || trip.getDeparture().trim().isEmpty()) {
            throw new IllegalArgumentException("Departure location cannot be null or empty");
        }
        
        if (trip.getDestination() == null || trip.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be null or empty");
        }
        
        if (trip.getDate() == null || trip.getDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        
        if (trip.getTime() == null || trip.getTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Time cannot be null or empty");
        }
        
        if (trip.getAvailableSeats() <= 0 || trip.getAvailableSeats() > 8) {
            throw new IllegalArgumentException("Available seats must be between 1 and 8");
        }
        
        if (trip.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        if (trip.getDeparture().equalsIgnoreCase(trip.getDestination())) {
            throw new IllegalArgumentException("Departure and destination cannot be the same");
        }
    }
}
