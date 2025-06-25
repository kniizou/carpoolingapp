package com.carpooling.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.carpooling.model.Notification;

/**
 * Simple visible toast test without fancy animations
 */
public class SimpleToastTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a simple notification
            Notification testNotification = new Notification(
                "🎉 This is a TEST TOAST notification!",
                "SUCCESS",
                "test123"
            );
            
            // Create a simple visible window
            JWindow toast = new JWindow();
            toast.setAlwaysOnTop(true);
            toast.setSize(400, 100);
            
            // Position at top-right of screen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            toast.setLocation(screenSize.width - 420, 20);
            
            // Create content
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(76, 175, 80)); // Green background
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 3),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            
            JLabel messageLabel = new JLabel("<html><div style='color:white; font-size:14px; font-weight:bold;'>" + 
                                           testNotification.getMessage() + "</div></html>");
            messageLabel.setForeground(Color.WHITE);
            
            JLabel typeLabel = new JLabel(testNotification.getType());
            typeLabel.setForeground(Color.WHITE);
            typeLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            
            panel.add(typeLabel, BorderLayout.NORTH);
            panel.add(messageLabel, BorderLayout.CENTER);
            
            toast.add(panel);
            toast.setVisible(true);
            
            System.out.println("Toast should be visible at top-right of screen!");
            System.out.println("Screen size: " + screenSize);
            System.out.println("Toast position: " + toast.getLocation());
            
            // Auto close after 5 seconds
            Timer closeTimer = new Timer(5000, e -> {
                toast.dispose();
                System.out.println("Toast closed!");
                System.exit(0);
            });
            closeTimer.setRepeats(false);
            closeTimer.start();
        });
    }
}
