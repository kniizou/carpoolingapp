package com.carpooling.model;

/**
 * Enum for trip status in the carpooling system.
 * Defines the different states a trip can be in.
 */
public enum TripStatus {
    /** Trip is available for booking */
    AVAILABLE("AVAILABLE", "Available"),
    
    /** Trip is full, no more seats available */
    FULL("FULL", "Full"),
    
    /** Trip has been cancelled */
    CANCELLED("CANCELLED", "Cancelled"),
    
    /** Trip is completed */
    COMPLETED("COMPLETED", "Completed"),
    
    /** Trip is active/in progress */
    ACTIVE("ACTIVE", "Active");
    
    private final String code;
    private final String displayName;
    
    TripStatus(String code, String displayName) {
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
     * Converts a string code to the corresponding TripStatus enum.
     * 
     * @param code The status code
     * @return The TripStatus enum value
     * @throws IllegalArgumentException if the code is not valid
     */
    public static TripStatus fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Status code cannot be null");
        }
        
        for (TripStatus status : values()) {
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
