package com.carpooling.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.carpooling.data.DatabaseConnection;
import com.carpooling.model.Trip;
import com.carpooling.model.User;
import com.carpooling.model.UserRole;
import com.carpooling.repository.SqliteTripRepository;
import com.carpooling.repository.SqliteUserRepository;

/**
 * Integration tests for atomic seat booking functionality
 * Tests race condition prevention in concurrent booking scenarios
 */
public class AtomicBookingIntegrationTest {
    
    private TripServiceImpl tripService;
    private SqliteTripRepository tripRepository;
    private SqliteUserRepository userRepository;
    private User driver;
    private Trip testTrip;
    
    @BeforeEach
    public void setUp() {
        // Initialize repositories and services
        try {
            DatabaseConnection.getConnection(); // This will initialize the database
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
        
        tripRepository = new SqliteTripRepository();
        userRepository = new SqliteUserRepository();
        tripService = new TripServiceImpl(tripRepository);
        
        // Create test driver
        driver = new User("driver-test-123", "Test", "Driver", 25, "driver@test.com", "password123", UserRole.DRIVER);
        userRepository.save(driver);
        
        // Create test trip with limited seats (using correct date format dd/MM/yyyy)
        testTrip = new Trip("trip-test-123", driver, "Paris", "Lyon", "01/07/2025", "14:30", 2, 25.0);
        tripService.createTrip(testTrip);
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up test data
        try {
            tripRepository.delete(testTrip);
            userRepository.delete(driver);
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    public void testAtomicSeatBooking_PreventRaceCondition() throws Exception {
        // Create multiple passengers trying to book simultaneously
        User passenger1 = createTestPassenger("passenger1@test.com", "pass-1");
        User passenger2 = createTestPassenger("passenger2@test.com", "pass-2");
        User passenger3 = createTestPassenger("passenger3@test.com", "pass-3");
        User passenger4 = createTestPassenger("passenger4@test.com", "pass-4");
        
        // Track successful bookings
        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);
        
        // Create executor for concurrent operations
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Submit concurrent booking requests
        CompletableFuture<Void> booking1 = CompletableFuture.runAsync(() -> {
            try {
                boolean success = tripService.addPassengerToTrip(testTrip, passenger1, "CONFIRMED");
                if (success) {
                    successfulBookings.incrementAndGet();
                } else {
                    failedBookings.incrementAndGet();
                }
            } catch (Exception e) {
                failedBookings.incrementAndGet();
                System.out.println("Booking 1 failed: " + e.getMessage());
            }
        }, executor);
        
        CompletableFuture<Void> booking2 = CompletableFuture.runAsync(() -> {
            try {
                boolean success = tripService.addPassengerToTrip(testTrip, passenger2, "CONFIRMED");
                if (success) {
                    successfulBookings.incrementAndGet();
                } else {
                    failedBookings.incrementAndGet();
                }
            } catch (Exception e) {
                failedBookings.incrementAndGet();
                System.out.println("Booking 2 failed: " + e.getMessage());
            }
        }, executor);
        
        CompletableFuture<Void> booking3 = CompletableFuture.runAsync(() -> {
            try {
                boolean success = tripService.addPassengerToTrip(testTrip, passenger3, "CONFIRMED");
                if (success) {
                    successfulBookings.incrementAndGet();
                } else {
                    failedBookings.incrementAndGet();
                }
            } catch (Exception e) {
                failedBookings.incrementAndGet();
                System.out.println("Booking 3 failed: " + e.getMessage());
            }
        }, executor);
        
        CompletableFuture<Void> booking4 = CompletableFuture.runAsync(() -> {
            try {
                boolean success = tripService.addPassengerToTrip(testTrip, passenger4, "CONFIRMED");
                if (success) {
                    successfulBookings.incrementAndGet();
                } else {
                    failedBookings.incrementAndGet();
                }
            } catch (Exception e) {
                failedBookings.incrementAndGet();
                System.out.println("Booking 4 failed: " + e.getMessage());
            }
        }, executor);
        
        // Wait for all bookings to complete
        CompletableFuture.allOf(booking1, booking2, booking3, booking4).get(10, TimeUnit.SECONDS);
        
        // Shutdown executor
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        // Verify results
        System.out.println("Successful bookings: " + successfulBookings.get());
        System.out.println("Failed bookings: " + failedBookings.get());
        
        // Should have exactly 2 successful bookings (available seats)
        // and 2 failed bookings (no more seats available)
        assertEquals(2, successfulBookings.get(), "Should have exactly 2 successful bookings");
        assertEquals(2, failedBookings.get(), "Should have exactly 2 failed bookings");
        
        // Verify trip state
        Trip updatedTrip = tripRepository.findById(testTrip.getId());
        assertNotNull(updatedTrip);
        tripRepository.loadPassengers(updatedTrip);
        
        // Should have exactly 2 confirmed passengers
        long confirmedCount = updatedTrip.getPassengers().size();
        assertEquals(2, confirmedCount, "Trip should have exactly 2 confirmed passengers");
        
        // Clean up test passengers
        userRepository.delete(passenger1);
        userRepository.delete(passenger2);
        userRepository.delete(passenger3);
        userRepository.delete(passenger4);
    }
    
    @Test
    public void testAtomicSeatBooking_PreventDuplicateBooking() {
        // Create test passenger
        User passenger = createTestPassenger("duplicate@test.com", "dup-pass");
        
        // First booking should succeed
        boolean firstBooking = tripService.addPassengerToTrip(testTrip, passenger, "CONFIRMED");
        assertTrue(firstBooking, "First booking should succeed");
        
        // Second booking with same passenger should fail
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            tripService.addPassengerToTrip(testTrip, passenger, "CONFIRMED");
        });
        
        assertTrue(exception.getMessage().contains("already on this trip"), 
                  "Should prevent duplicate booking");
        
        // Clean up
        userRepository.delete(passenger);
    }
    
    @Test
    public void testAtomicSeatBooking_DriverCannotBePassenger() {
        // Driver should not be able to book their own trip
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tripService.addPassengerToTrip(testTrip, driver, "CONFIRMED");
        });
        
        assertTrue(exception.getMessage().contains("Driver cannot be a passenger"), 
                  "Driver should not be able to book their own trip");
    }
    
    private User createTestPassenger(String email, String id) {
        User passenger = new User(id, "Test", "Passenger" + id, 25, email, "password123", UserRole.PASSENGER);
        userRepository.save(passenger);
        return passenger;
    }
}
