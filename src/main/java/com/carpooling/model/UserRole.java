package com.carpooling.model;

/**
 * Enum for user roles in the carpooling system.
 * Defines the different types of users and their permissions.
 */
public enum UserRole {
    /** Administrator with full system access */
    ADMIN("ADMIN", "Administrator"),
    
    /** Driver who can create and manage trips */
    DRIVER("DRIVER", "Driver"),
    
    /** Passenger who can search and book trips */
    PASSENGER("PASSENGER", "Passenger");
    
    private final String code;
    private final String displayName;
    
    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    /**
     * Gets the role code used in database storage.
     * 
     * @return The role code
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
     * Converts a string code to the corresponding UserRole enum.
     * 
     * @param code The role code
     * @return The UserRole enum value
     * @throws IllegalArgumentException if the code is not valid
     */
    public static UserRole fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Role code cannot be null");
        }
        
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("Invalid role code: " + code);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
