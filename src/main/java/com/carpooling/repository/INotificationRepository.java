package com.carpooling.repository;

import java.util.List;

import com.carpooling.model.Notification;

/**
 * Repository interface for notification data access operations.
 * Handles persistent storage and retrieval of notifications.
 */
public interface INotificationRepository {
    
    /**
     * Saves a notification to persistent storage.
     * 
     * @param notification The notification to save
     */
    void saveNotification(Notification notification);
    
    /**
     * Gets all notifications for a specific user.
     * 
     * @param userId The user ID
     * @return List of notifications for the user, ordered by timestamp (newest first)
     */
    List<Notification> getNotificationsByUserId(String userId);
    
    /**
     * Gets unread notifications for a specific user.
     * 
     * @param userId The user ID
     * @return List of unread notifications for the user, ordered by timestamp (newest first)
     */
    List<Notification> getUnreadNotificationsByUserId(String userId);
    
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
     * Deletes all notifications older than the specified number of days.
     * 
     * @param days Number of days
     * @return The number of notifications deleted
     */
    int deleteOldNotifications(int days);
    
    /**
     * Gets the count of unread notifications for a user.
     * 
     * @param userId The user ID
     * @return The count of unread notifications
     */
    int getUnreadNotificationCount(String userId);
}
