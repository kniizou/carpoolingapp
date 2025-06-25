package com.carpooling.model;

/**
 * Enum for notification types in the carpooling system.
 * Defines the different types of notifications that can be sent to users.
 */
public enum NotificationType {
    /** Notification for trip requests */
    TRIP_REQUEST("TRIP_REQUEST", "Trip Request"),
    
    /** Notification for trip confirmations */
    TRIP_CONFIRMATION("TRIP_CONFIRMATION", "Trip Confirmation"),
    
    /** Notification for trip cancellations */
    TRIP_CANCELLATION("TRIP_CANCELLATION", "Trip Cancellation"),
    
    /** Notification for trip updates */
    TRIP_UPDATE("TRIP_UPDATE", "Trip Update"),
    
    /** Notification for passenger status changes */
    PASSENGER_STATUS("PASSENGER_STATUS", "Passenger Status"),
    
    /** General system notifications */
    SYSTEM("SYSTEM", "System Notification");
    
    private final String code;
    private final String displayName;
    
    NotificationType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * Gets the type code used in database storage.
     * 
     * @return The type code
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
     * Converts a string code to the corresponding NotificationType enum.
     * 
     * @param code The type code
     * @return The NotificationType enum value
     * @throws IllegalArgumentException if the code is not valid
     */
    public static NotificationType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Type code cannot be null");
        }
        
        for (NotificationType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Invalid type code: " + code);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
