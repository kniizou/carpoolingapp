package com.carpooling.demo;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;
import com.carpooling.model.Trip;
import com.carpooling.model.User;

/**
 * Demo class to showcase the notification system functionality
 */
public class NotificationDemo {
    
    public static void main(String[] args) {
        System.out.println("=== CARPOOLING NOTIFICATION SYSTEM DEMO ===\n");
        
        // Create demo users
        User alice = new User("alice123", "Alice", "Johnson", 28, "alice@example.com", "password123", "PASSENGER");
        User bob = new User("bob456", "Bob", "Smith", 32, "bob@example.com", "password456", "DRIVER");
        
        // Create demo trip
        Trip trip = new Trip("trip789", bob, "Paris", "Lyon", "25/06/2025", "09:00", 3, 30.0);
        
        NotificationManager notifManager = NotificationManager.getInstance();
        
        // Clear any existing notifications
        notifManager.clearAllNotifications();
        
        System.out.println("1. Creating test users:");
        System.out.println("   - Alice (Passenger): " + alice.getEmail());
        System.out.println("   - Bob (Driver): " + bob.getEmail());
        System.out.println();
        
        System.out.println("2. Creating a trip:");
        System.out.println("   - From: " + trip.getDeparture());
        System.out.println("   - To: " + trip.getDestination());
        System.out.println("   - Date: " + trip.getDate());
        System.out.println("   - Driver: " + trip.getDriver().getName());
        System.out.println();
        
        // Scenario 1: Passenger requests to join trip
        System.out.println("3. Scenario: Alice requests to join Bob's trip");
        
        // Notification to driver (Bob)
        notifManager.addNotification(bob.getId(), 
            Notification.createInfoNotification(
                "Nouvelle demande de trajet: " + alice.getName() + 
                " souhaite rejoindre votre trajet de " + trip.getDeparture() + 
                " à " + trip.getDestination() + " le " + trip.getDate(),
                trip.getId()
            ));
        
        // Confirmation notification to passenger (Alice)
        notifManager.addNotification(alice.getId(),
            Notification.createSuccessNotification(
                "Vous avez demandé à rejoindre le trajet de " + 
                trip.getDeparture() + " à " + trip.getDestination() + 
                " le " + trip.getDate() + ". Vous serez notifié quand le conducteur aura répondu.",
                trip.getId()
            ));
        
        displayNotifications(alice, "Alice (Passenger)");
        displayNotifications(bob, "Bob (Driver)");
        
        // Scenario 2: Driver accepts the request
        System.out.println("\n4. Scenario: Bob accepts Alice's request");
        
        notifManager.addTripStatusNotification(alice.getId(), trip, "accepted");
        
        displayNotifications(alice, "Alice (Passenger) - After acceptance");
        
        // Scenario 3: Add some general notifications
        System.out.println("\n5. Adding various notification types:");
        
        notifManager.addNotification(alice.getId(),
            Notification.createWarningNotification(
                "Attention: Votre trajet de demain pourrait être retardé en raison des conditions météorologiques",
                trip.getId()
            ));
        
        notifManager.addNotification(alice.getId(),
            Notification.createErrorNotification(
                "Votre trajet de Lyon à Marseille a été annulé par le conducteur",
                "trip999"
            ));
        
        notifManager.addNotification(alice.getId(),
            Notification.createInfoNotification(
                "Nouveau trajet disponible: Nice à Monaco, départ 15h00",
                "trip888"
            ));
        
        displayNotifications(alice, "Alice (Passenger) - Final state");
        
        // Display statistics
        System.out.println("\n6. Notification Statistics:");
        System.out.println("   - Alice has " + notifManager.countUnreadNotifications(alice.getId()) + " unread notifications");
        System.out.println("   - Bob has " + notifManager.countUnreadNotifications(bob.getId()) + " unread notifications");
        System.out.println("   - Alice has " + notifManager.getNotificationsForUser(alice.getId()).size() + " total notifications");
        System.out.println("   - Bob has " + notifManager.getNotificationsForUser(bob.getId()).size() + " total notifications");
        
        // Test marking notifications as read
        System.out.println("\n7. Marking all of Alice's notifications as read...");
        notifManager.markAllNotificationsAsRead(alice.getId());
        System.out.println("   - Alice now has " + notifManager.countUnreadNotifications(alice.getId()) + " unread notifications");
        
        System.out.println("\n=== DEMO COMPLETED ===");
        System.out.println("The notification system is working correctly!");
        System.out.println("You can now test it in the PassengerDashboard by clicking the notification buttons.");
    }
    
    private static void displayNotifications(User user, String context) {
        NotificationManager notifManager = NotificationManager.getInstance();
        var notifications = notifManager.getNotificationsForUser(user.getId());
        
        System.out.println("\n   " + context + " notifications (" + notifications.size() + " total):");
        if (notifications.isEmpty()) {
            System.out.println("     (No notifications)");
        } else {
            for (Notification notif : notifications) {
                String readStatus = notif.isRead() ? "[READ]" : "[UNREAD]";
                System.out.println("     " + readStatus + " " + notif.getType() + ": " + notif.getMessage());
                System.out.println("       Time: " + notif.getFormattedTimestamp());
            }
        }
    }
}
