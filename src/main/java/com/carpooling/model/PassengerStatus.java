package com.carpooling.model;

/**
 * Enum for passenger status on a trip.
 * Defines the different states a passenger can have on a trip.
 */
public enum PassengerStatus {
    /** Passenger request is pending driver approval */
    PENDING("PENDING", "Pending"),
    
    /** Passenger request has been confirmed by driver */
    CONFIRMED("CONFIRMED", "Confirmed"),
    
    /** Passenger request has been rejected by driver */
    REJECTED("REJECTED", "Rejected"),
    
    /** Passenger has cancelled their request */
    CANCELLED("CANCELLED", "Cancelled");
    
    private final String code;
    private final String displayName;
    
    PassengerStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * Gets the status code used in database storage.
     * 
     * @return The status code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Gets the human-readable display name.
     * 
     * @return The display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Converts a string code to the corresponding PassengerStatus enum.
     * 
     * @param code The status code
     * @return The PassengerStatus enum value
     * @throws IllegalArgumentException if the code is not valid
     */
    public static PassengerStatus fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Status code cannot be null");
        }
        
        for (PassengerStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
