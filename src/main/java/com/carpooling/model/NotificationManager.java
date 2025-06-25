package com.carpooling.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manager class for handling notifications
 */
public class NotificationManager {
    private static NotificationManager instance;
    private Map<String, List<Notification>> userNotifications; // Map of userId -> List of notifications
    
    private NotificationManager() {
        this.userNotifications = new HashMap<>();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    /**
     * Add a notification for a user
     * 
     * @param userId The ID of the user to receive the notification
     * @param notification The notification to add
     */
    public void addNotification(String userId, Notification notification) {
        if (!userNotifications.containsKey(userId)) {
            userNotifications.put(userId, new ArrayList<>());
        }
        userNotifications.get(userId).add(notification);
    }
    
    /**
     * Get all notifications for a user
     * 
     * @param userId The ID of the user
     * @return List of notifications for the user
     */
    public List<Notification> getNotificationsForUser(String userId) {
        if (!userNotifications.containsKey(userId)) {
            return new ArrayList<>();
        }
        return userNotifications.get(userId).stream()
                .sorted(Comparator.comparing(Notification::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications for a user
     * 
     * @param userId The ID of the user
     * @return List of unread notifications for the user
     */
    public List<Notification> getUnreadNotificationsForUser(String userId) {
        if (!userNotifications.containsKey(userId)) {
            return new ArrayList<>();
        }
        return userNotifications.get(userId).stream()
                .filter(n -> !n.isRead())
                .sorted(Comparator.comparing(Notification::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    /**
     * Mark a notification as read
     * 
     * @param notificationId The ID of the notification to mark as read
     */
    public void markNotificationAsRead(String userId, String notificationId) {
        if (userNotifications.containsKey(userId)) {
            userNotifications.get(userId).stream()
                    .filter(n -> n.getId().equals(notificationId))
                    .findFirst()
                    .ifPresent(Notification::markAsRead);
        }
    }
    
    /**
     * Mark all notifications for a user as read
     * 
     * @param userId The ID of the user
     */
    public void markAllNotificationsAsRead(String userId) {
        if (userNotifications.containsKey(userId)) {
            userNotifications.get(userId).forEach(Notification::markAsRead);
        }
    }
    
    /**
     * Remove a notification
     * 
     * @param userId The ID of the user
     * @param notificationId The ID of the notification to remove
     */
    public void removeNotification(String userId, String notificationId) {
        if (userNotifications.containsKey(userId)) {
            userNotifications.get(userId).removeIf(n -> n.getId().equals(notificationId));
        }
    }
    
    /**
     * Count unread notifications for a user
     * 
     * @param userId The ID of the user
     * @return Number of unread notifications
     */
    public int countUnreadNotifications(String userId) {
        if (!userNotifications.containsKey(userId)) {
            return 0;
        }
        return (int) userNotifications.get(userId).stream()
                .filter(n -> !n.isRead())
                .count();
    }
    
    /**
     * Add a test notification for a user (for testing purposes)
     * 
     * @param userId The ID of the user to receive the notification
     */
    public void addTestNotification(String userId) {
        addNotification(userId, Notification.createInfoNotification(
                "Ceci est une notification de test envoyée à " + userId + " le " + java.time.LocalDateTime.now(), 
                null));
    }
    
    /**
     * Add a trip status notification for a user
     * 
     * @param userId The ID of the user to receive the notification
     * @param trip The trip related to the notification
     * @param status The status of the request (accepted, rejected, etc.)
     */
    public void addTripStatusNotification(String userId, Trip trip, String status) {
        String message;
        String type;
        
        if ("accepted".equalsIgnoreCase(status)) {
            message = "Votre demande pour le trajet de " + trip.getDeparture() + " à " + trip.getDestination() + 
                     " le " + trip.getDate() + " a été acceptée par le conducteur.";
            type = "SUCCESS";
        } else if ("rejected".equalsIgnoreCase(status)) {
            message = "Votre demande pour le trajet de " + trip.getDeparture() + " à " + trip.getDestination() + 
                     " le " + trip.getDate() + " a été refusée par le conducteur.";
            type = "ERROR";
        } else {
            message = "Le statut de votre demande pour le trajet de " + trip.getDeparture() + " à " + trip.getDestination() + 
                     " le " + trip.getDate() + " a été mis à jour : " + status;
            type = "INFO";
        }
        
        addNotification(userId, new Notification(message, type, trip.getId()));
    }
    
    /**
     * Clear all notifications (for testing purposes)
     */
    public void clearAllNotifications() {
        userNotifications.clear();
    }
}
