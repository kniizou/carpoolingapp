package com.carpooling.test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;
import com.carpooling.model.Trip;
import com.carpooling.model.User;
import com.carpooling.ui.NotificationPanel;

/**
 * Test class for the notification system
 */
public class NotificationSystemTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            testNotificationSystem();
        });
    }
    
    /**
     * Test the notification system functionality
     */
    public static void testNotificationSystem() {
        System.out.println("Starting Notification System Test...");
        
        // Create test users
        User passenger = new User("1", "Jean", "Dupont", 25, "jean.dupont@email.com", "password123", "PASSENGER");
        User driver = new User("2", "Marie", "Martin", 30, "marie.martin@email.com", "password456", "DRIVER");
        
        // Create test trip
        Trip trip = new Trip("trip123", driver, "Paris", "Lyon", "20/06/2025", "14:30", 3, 25.0);
        
        // Get notification manager instance
        NotificationManager notifManager = NotificationManager.getInstance();
        
        // Clear any existing notifications
        notifManager.clearAllNotifications();
        
        // Test 1: Basic notification creation
        System.out.println("\n--- Test 1: Basic Notification Creation ---");
        Notification infoNotif = Notification.createInfoNotification("Test info message", null);
        Notification successNotif = Notification.createSuccessNotification("Test success message", "trip123");
        Notification warningNotif = Notification.createWarningNotification("Test warning message", null);
        Notification errorNotif = Notification.createErrorNotification("Test error message", "trip456");
        
        System.out.println("Created notifications:");
        System.out.println("- Info: " + infoNotif.getMessage() + " (Type: " + infoNotif.getType() + ")");
        System.out.println("- Success: " + successNotif.getMessage() + " (Type: " + successNotif.getType() + ")");
        System.out.println("- Warning: " + warningNotif.getMessage() + " (Type: " + warningNotif.getType() + ")");
        System.out.println("- Error: " + errorNotif.getMessage() + " (Type: " + errorNotif.getType() + ")");
        
        // Test 2: Add notifications to user
        System.out.println("\n--- Test 2: Adding Notifications to User ---");
        notifManager.addNotification(passenger.getId(), infoNotif);
        notifManager.addNotification(passenger.getId(), successNotif);
        notifManager.addNotification(passenger.getId(), warningNotif);
        notifManager.addNotification(passenger.getId(), errorNotif);
        
        int unreadCount = notifManager.countUnreadNotifications(passenger.getId());
        System.out.println("Unread notifications for passenger: " + unreadCount);
        
        // Test 3: Retrieve notifications
        System.out.println("\n--- Test 3: Retrieving Notifications ---");
        List<Notification> allNotifications = notifManager.getNotificationsForUser(passenger.getId());
        System.out.println("Total notifications for passenger: " + allNotifications.size());
        
        for (Notification notif : allNotifications) {
            System.out.println("- " + notif.getType() + ": " + notif.getMessage() + 
                             " (Read: " + notif.isRead() + ", Time: " + notif.getFormattedTimestamp() + ")");
        }
        
        // Test 4: Mark notifications as read
        System.out.println("\n--- Test 4: Marking Notifications as Read ---");
        notifManager.markNotificationAsRead(passenger.getId(), infoNotif.getId());
        notifManager.markNotificationAsRead(passenger.getId(), successNotif.getId());
        
        int unreadCountAfterRead = notifManager.countUnreadNotifications(passenger.getId());
        System.out.println("Unread notifications after marking some as read: " + unreadCountAfterRead);
        
        // Test 5: Trip status notifications
        System.out.println("\n--- Test 5: Trip Status Notifications ---");
        notifManager.addTripStatusNotification(passenger.getId(), trip, "accepted");
        notifManager.addTripStatusNotification(passenger.getId(), trip, "rejected");
        
        List<Notification> updatedNotifications = notifManager.getNotificationsForUser(passenger.getId());
        System.out.println("Total notifications after trip status updates: " + updatedNotifications.size());
        
        // Test 6: Test notification panel in a simple window
        System.out.println("\n--- Test 6: Testing Notification Panel UI ---");
        createTestWindow(passenger);
        
        System.out.println("\nNotification System Test completed successfully!");
    }
    
    /**
     * Create a test window to demonstrate the notification panel
     */
    private static void createTestWindow(User user) {
        JFrame testFrame = new JFrame("Notification Panel Test");
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(600, 400);
        testFrame.setLocationRelativeTo(null);
        
        // Create notification panel
        NotificationPanel notificationPanel = new NotificationPanel(user);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(notificationPanel, BorderLayout.NORTH);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton addTestNotifButton = new JButton("Add Test Notification");
        addTestNotifButton.addActionListener(e -> {
            notificationPanel.addTestNotification();
        });
        
        JButton addMultipleButton = new JButton("Add Multiple Notifications");
        addMultipleButton.addActionListener(e -> {
            NotificationManager notifManager = NotificationManager.getInstance();
            
            notifManager.addNotification(user.getId(), 
                Notification.createInfoNotification("New trip available from Marseille to Nice", "trip001"));
            notifManager.addNotification(user.getId(), 
                Notification.createSuccessNotification("Your trip request has been accepted!", "trip002"));
            notifManager.addNotification(user.getId(), 
                Notification.createWarningNotification("Weather alert: Rain expected for your trip tomorrow", "trip003"));
            notifManager.addNotification(user.getId(), 
                Notification.createErrorNotification("Trip cancelled due to driver unavailability", "trip004"));
                
            notificationPanel.updateNotificationCount();
        });
        
        JButton clearAllButton = new JButton("Clear All Notifications");
        clearAllButton.addActionListener(e -> {
            NotificationManager.getInstance().clearAllNotifications();
            notificationPanel.updateNotificationCount();
            notificationPanel.refreshNotifications();
        });
        
        controlPanel.add(addTestNotifButton);
        controlPanel.add(addMultipleButton);
        controlPanel.add(clearAllButton);
        
        // Create info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        JTextArea instructions = new JTextArea(
            "Click the bell icon to view notifications.\n" +
            "Use the buttons below to test different notification scenarios.\n" +
            "You can delete individual notifications by clicking the X button.\n" +
            "Viewing notifications marks them as read automatically."
        );
        instructions.setEditable(false);
        instructions.setBackground(infoPanel.getBackground());
        infoPanel.add(instructions);
        
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        testFrame.add(mainPanel);
        testFrame.setVisible(true);
        
        System.out.println("Test window created. You can now interact with the notification system.");
    }
}
