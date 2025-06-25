package com.carpooling.service;

import java.util.List;
import java.util.logging.Logger;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationType;
import com.carpooling.repository.INotificationRepository;

/**
 * Implementation of the notification service.
 * Provides business logic for notification management operations.
 */
public class NotificationServiceImpl implements INotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationServiceImpl.class.getName());
    
    private final INotificationRepository notificationRepository;
    
    /**
     * Constructor with dependency injection.
     * 
     * @param notificationRepository The notification repository
     */
    public NotificationServiceImpl(INotificationRepository notificationRepository) {
        if (notificationRepository == null) {
            throw new IllegalArgumentException("Notification repository cannot be null");
        }
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public Notification createNotification(String userId, String message, NotificationType type, String relatedEntityId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        // Note: We're using relatedEntityId as userId for now, but this should be redesigned
        // to have a separate userId field in the Notification model
        Notification notification = new Notification(message, type, relatedEntityId);
        
        try {
            notificationRepository.saveNotification(notification);
            LOGGER.info("Notification created for user " + userId + ": " + message);
            return notification;
        } catch (Exception e) {
            LOGGER.severe("Failed to create notification for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to create notification", e);
        }
    }
    
    @Override
    public Notification createNotification(String userId, String message, String type, String relatedEntityId) {
        return createNotification(userId, message, NotificationType.fromCode(type), relatedEntityId);
    }
    
    @Override
    public List<Notification> getNotificationsForUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        try {
            return notificationRepository.getNotificationsByUserId(userId);
        } catch (Exception e) {
            LOGGER.severe("Failed to get notifications for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve notifications", e);
        }
    }
    
    @Override
    public List<Notification> getUnreadNotificationsForUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        try {
            return notificationRepository.getUnreadNotificationsByUserId(userId);
        } catch (Exception e) {
            LOGGER.severe("Failed to get unread notifications for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve unread notifications", e);
        }
    }
    
    @Override
    public void markNotificationAsRead(String notificationId) {
        if (notificationId == null || notificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification ID cannot be null or empty");
        }
        
        try {
            notificationRepository.markNotificationAsRead(notificationId);
        } catch (Exception e) {
            LOGGER.severe("Failed to mark notification as read " + notificationId + ": " + e.getMessage());
            throw new RuntimeException("Failed to mark notification as read", e);
        }
    }
    
    @Override
    public void markAllNotificationsAsRead(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        try {
            notificationRepository.markAllNotificationsAsRead(userId);
        } catch (Exception e) {
            LOGGER.severe("Failed to mark all notifications as read for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to mark all notifications as read", e);
        }
    }
    
    @Override
    public boolean deleteNotification(String notificationId) {
        if (notificationId == null || notificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification ID cannot be null or empty");
        }
        
        try {
            return notificationRepository.deleteNotification(notificationId);
        } catch (Exception e) {
            LOGGER.severe("Failed to delete notification " + notificationId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete notification", e);
        }
    }
    
    @Override
    public int getUnreadNotificationCount(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        try {
            return notificationRepository.getUnreadNotificationCount(userId);
        } catch (Exception e) {
            LOGGER.severe("Failed to get unread notification count for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to get unread notification count", e);
        }
    }
    
    @Override
    public int cleanupOldNotifications(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be positive");
        }
        
        try {
            int deleted = notificationRepository.deleteOldNotifications(days);
            LOGGER.info("Cleaned up " + deleted + " old notifications (older than " + days + " days)");
            return deleted;
        } catch (Exception e) {
            LOGGER.severe("Failed to cleanup old notifications: " + e.getMessage());
            throw new RuntimeException("Failed to cleanup old notifications", e);
        }
    }
    
    @Override
    public Notification createTripRequestNotification(String driverId, String passengerName, String tripDetails, String tripId) {
        String message = String.format("Nouvelle demande de %s pour le trajet: %s", passengerName, tripDetails);
        return createNotification(driverId, message, NotificationType.TRIP_REQUEST, tripId);
    }
    
    @Override
    public Notification createTripConfirmationNotification(String passengerId, String tripDetails, String tripId) {
        String message = String.format("Votre demande pour le trajet '%s' a été acceptée!", tripDetails);
        return createNotification(passengerId, message, NotificationType.TRIP_CONFIRMATION, tripId);
    }
    
    @Override
    public Notification createTripCancellationNotification(String userId, String tripDetails, String tripId) {
        String message = String.format("Le trajet '%s' a été annulé", tripDetails);
        return createNotification(userId, message, NotificationType.TRIP_CANCELLATION, tripId);
    }
}
