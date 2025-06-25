package com.carpooling.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class representing a notification in the carpooling application
 */
public class Notification {
    private String id;
    private String message;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean read;
    private String relatedEntityId; // ID of related trip or user

    /**
     * Constructor for a new notification
     * 
     * @param message The notification message
     * @param type The notification type
     * @param relatedEntityId Optional ID of related entity (Trip ID, User ID, etc.)
     */
    public Notification(String message, NotificationType type, String relatedEntityId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.read = false;
        this.relatedEntityId = relatedEntityId;
    }

    /**
     * Constructor for backward compatibility with string type
     */
    public Notification(String message, String type, String relatedEntityId) {
        this(message, NotificationType.fromCode(type), relatedEntityId);
    }

    /**
     * Constructor with existing ID (for database retrieval)
     */
    public Notification(String id, String message, NotificationType type, LocalDateTime timestamp, boolean read, String relatedEntityId) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.read = read;
        this.relatedEntityId = relatedEntityId;
    }
    
    /**
     * Constructor with existing ID (for database retrieval) - backward compatibility
     */
    public Notification(String id, String message, String type, LocalDateTime timestamp, boolean read, String relatedEntityId) {
        this(id, message, NotificationType.fromCode(type), timestamp, read, relatedEntityId);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type.getCode();
    }
    
    /**
     * Gets the notification type as an enum.
     * 
     * @return The NotificationType enum value
     */
    public NotificationType getNotificationType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = NotificationType.fromCode(type);
    }
    
    /**
     * Sets the notification type using an enum.
     * 
     * @param type The NotificationType enum value
     */
    public void setNotificationType(NotificationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.type = type;
    }

    public void markAsRead() {
        this.read = true;
    }

    public void markAsUnread() {
        this.read = false;
    }

    /**
     * Get formatted timestamp for display
     */
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    /**
     * Factory method for creating an info notification
     */
    public static Notification createInfoNotification(String message, String relatedEntityId) {
        return new Notification(message, "INFO", relatedEntityId);
    }

    /**
     * Factory method for creating a success notification
     */
    public static Notification createSuccessNotification(String message, String relatedEntityId) {
        return new Notification(message, "SUCCESS", relatedEntityId);
    }

    /**
     * Factory method for creating a warning notification
     */
    public static Notification createWarningNotification(String message, String relatedEntityId) {
        return new Notification(message, "WARNING", relatedEntityId);
    }

    /**
     * Factory method for creating an error notification
     */
    public static Notification createErrorNotification(String message, String relatedEntityId) {
        return new Notification(message, "ERROR", relatedEntityId);
    }
}
