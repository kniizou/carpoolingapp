package com.carpooling.service;

import java.util.List;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationType;

/**
 * Service interface for notification management operations.
 * Handles notification creation, retrieval, and management business logic.
 */
public interface INotificationService {
    
    /**
     * Creates and saves a new notification for a user.
     * 
     * @param userId The ID of the user to notify
     * @param message The notification message
     * @param type The notification type
     * @param relatedEntityId Optional ID of related entity
     * @return The created notification
     */
    Notification createNotification(String userId, String message, NotificationType type, String relatedEntityId);
    
    /**
     * Creates and saves a new notification for a user (backward compatibility).
     * 
     * @param userId The ID of the user to notify
     * @param message The notification message
     * @param type The notification type as string
     * @param relatedEntityId Optional ID of related entity
     * @return The created notification
     */
    Notification createNotification(String userId, String message, String type, String relatedEntityId);
    
    /**
     * Gets all notifications for a user.
     * 
     * @param userId The user ID
     * @return List of notifications for the user
     */
    List<Notification> getNotificationsForUser(String userId);
    
    /**
     * Gets unread notifications for a user.
     * 
     * @param userId The user ID
     * @return List of unread notifications for the user
     */
    List<Notification> getUnreadNotificationsForUser(String userId);
    
    /**
     * Marks a notification as read.
     * 
     * @param notificationId The notification ID
     */
    void markNotificationAsRead(String notificationId);
    
    /**
     * Marks all notifications for a user as read.
     * 
     * @param userId The user ID
     */
    void markAllNotificationsAsRead(String userId);
    
    /**
     * Deletes a notification.
     * 
     * @param notificationId The notification ID
     * @return true if the notification was deleted, false otherwise
     */
    boolean deleteNotification(String notificationId);
    
    /**
     * Gets the count of unread notifications for a user.
     * 
     * @param userId The user ID
     * @return The count of unread notifications
     */
    int getUnreadNotificationCount(String userId);
    
    /**
     * Cleans up old notifications (older than specified days).
     * 
     * @param days Number of days
     * @return The number of notifications deleted
     */
    int cleanupOldNotifications(int days);
    
    /**
     * Creates a trip request notification.
     * 
     * @param driverId The driver's user ID
     * @param passengerName The passenger's name
     * @param tripDetails The trip details
     * @param tripId The trip ID
     * @return The created notification
     */
    Notification createTripRequestNotification(String driverId, String passengerName, String tripDetails, String tripId);
    
    /**
     * Creates a trip confirmation notification.
     * 
     * @param passengerId The passenger's user ID
     * @param tripDetails The trip details
     * @param tripId The trip ID
     * @return The created notification
     */
    Notification createTripConfirmationNotification(String passengerId, String tripDetails, String tripId);
    
    /**
     * Creates a trip cancellation notification.
     * 
     * @param userId The user ID to notify
     * @param tripDetails The trip details
     * @param tripId The trip ID
     * @return The created notification
     */
    Notification createTripCancellationNotification(String userId, String tripDetails, String tripId);
}
