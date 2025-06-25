package com.carpooling.test;

import javax.swing.SwingUtilities;

import com.carpooling.model.Notification;
import com.carpooling.ui.ToastNotification;
import com.carpooling.ui.ToastNotificationManager;

/**
 * Simple test to trigger toast notifications manually
 */
public class ToastTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Test 1: Direct toast notification
            System.out.println("Creating direct toast notification...");
            Notification testNotification = new Notification(
                "This is a test toast notification!",
                "INFO",
                "test123"
            );
            
            ToastNotification.showToast(testNotification);
            
            // Test 2: Using ToastNotificationManager
            System.out.println("Creating toast via manager...");
            ToastNotificationManager.getInstance().showToast(testNotification);
            
            // Test 3: Multiple toasts with delay
            System.out.println("Creating multiple toasts...");
            for (int i = 1; i <= 3; i++) {
                final int num = i;
                javax.swing.Timer timer = new javax.swing.Timer(i * 1000, e -> {
                    Notification delayedNotif = new Notification(
                        "This is toast notification number " + num,
                        "SUCCESS",
                        "test" + num
                    );
                    ToastNotification.showToast(delayedNotif);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        // Keep the program running
        try {
            Thread.sleep(10000); // Wait 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
