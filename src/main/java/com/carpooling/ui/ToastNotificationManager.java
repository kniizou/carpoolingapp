package com.carpooling.ui;

import java.util.ArrayList;
import java.util.List;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;

/**
 * Manager for displaying toast notifications
 */
public class ToastNotificationManager {
    private static ToastNotificationManager instance;
    private List<ToastNotification> activeToasts;
    private static final int MAX_TOASTS = 5; // Maximum number of simultaneous toasts
    private static final int TOAST_SPACING = 90; // Vertical spacing between toasts
    
    private ToastNotificationManager() {
        activeToasts = new ArrayList<>();
    }
    
    public static synchronized ToastNotificationManager getInstance() {
        if (instance == null) {
            instance = new ToastNotificationManager();
        }
        return instance;
    }
    
    /**
     * Show a toast notification
     * @param notification The notification to show as toast
     */
    public void showToast(Notification notification) {
        // Remove excess toasts if we have too many
        while (activeToasts.size() >= MAX_TOASTS) {
            ToastNotification oldestToast = activeToasts.remove(0);
            if (oldestToast != null) {
                oldestToast.dispose();
            }
        }
        
        // Create and show new toast
        ToastNotification toast = new ToastNotification(notification);
        activeToasts.add(toast);
        
        // Position the toast
        positionToast(toast, activeToasts.size() - 1);
    }
    
    /**
     * Position a toast notification on screen
     * @param toast The toast to position
     * @param index The index of the toast (0 = topmost)
     */
    private void positionToast(ToastNotification toast, int index) {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - 370; // 350 width + 20 margin
        int y = 20 + (index * TOAST_SPACING);
        toast.setLocation(x, y);
    }
    
    /**
     * Remove a toast from the active list
     * @param toast The toast to remove
     */
    public void removeToast(ToastNotification toast) {
        activeToasts.remove(toast);
        repositionToasts();
    }
    
    /**
     * Reposition all active toasts after one is removed
     */
    private void repositionToasts() {
        for (int i = 0; i < activeToasts.size(); i++) {
            positionToast(activeToasts.get(i), i);
        }
    }
    
    /**
     * Show toast for a specific user's new notifications
     * @param userId The user ID to check for new notifications
     */
    public void showToastsForUser(String userId) {
        NotificationManager notifManager = NotificationManager.getInstance();
        List<Notification> unreadNotifications = notifManager.getUnreadNotificationsForUser(userId);
        
        // Show toast for the most recent notification only
        if (!unreadNotifications.isEmpty()) {
            showToast(unreadNotifications.get(0));
        }
    }
    
    /**
     * Show toast for all unread notifications of a user
     * @param userId The user ID
     * @param maxToasts Maximum number of toasts to show
     */
    public void showAllUnreadToasts(String userId, int maxToasts) {
        NotificationManager notifManager = NotificationManager.getInstance();
        List<Notification> unreadNotifications = notifManager.getUnreadNotificationsForUser(userId);
        
        int count = 0;
        for (Notification notification : unreadNotifications) {
            if (count >= maxToasts) break;
            
            // Add a small delay between toasts
            final int delay = count * 500; // 500ms delay between each toast
            final Notification finalNotification = notification;
            
            javax.swing.Timer timer = new javax.swing.Timer(delay, e -> showToast(finalNotification));
            timer.setRepeats(false);
            timer.start();
            
            count++;
        }
    }
    
    /**
     * Show a success toast notification
     * @param message The success message to display
     */
    public static void showSuccess(String message) {
        Notification notification = new Notification(
            message, 
            com.carpooling.model.NotificationType.TRIP_CONFIRMATION,
            "system"
        );
        getInstance().showToast(notification);
    }
    
    /**
     * Show an error toast notification
     * @param message The error message to display
     */
    public static void showError(String message) {
        Notification notification = new Notification(
            message, 
            com.carpooling.model.NotificationType.TRIP_CANCELLATION,
            "system"
        );
        getInstance().showToast(notification);
    }
    
    /**
     * Show an info toast notification
     * @param message The info message to display
     */
    public static void showInfo(String message) {
        Notification notification = new Notification(
            message, 
            com.carpooling.model.NotificationType.SYSTEM,
            "system"
        );
        getInstance().showToast(notification);
    }
}
