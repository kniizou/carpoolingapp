package com.carpooling.ui;

import com.carpooling.model.Notification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Toast notification popup that appears temporarily on screen
 */
public class ToastNotification extends JWindow {
    private static final int TOAST_WIDTH = 350;
    private static final int TOAST_HEIGHT = 80;
    private static final int DISPLAY_TIME = 4000; // 4 seconds
    private static final int ANIMATION_DELAY = 10;
    private static final int ANIMATION_STEP = 5;
    
    private Timer displayTimer;
    private Timer fadeTimer;
    private float opacity = 0.0f;
    private boolean fadingIn = true;
    
    public ToastNotification(Notification notification) {
        setupToast(notification);
        animateToast();
    }
    
    private void setupToast(Notification notification) {
        setAlwaysOnTop(true);
        setFocusable(false);
        
        // Create main panel with rounded corners effect
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(getBackgroundColor(notification.getType()));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBorderColor(notification.getType()), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Icon panel
        JLabel iconLabel = new JLabel(getIcon(notification.getType()));
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        iconLabel.setForeground(getTextColor(notification.getType()));
        
        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(mainPanel.getBackground());
        
        // Title (notification type)
        JLabel titleLabel = new JLabel(notification.getType());
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        titleLabel.setForeground(getTextColor(notification.getType()));
        
        // Message
        JLabel messageLabel = new JLabel("<html><div style='width:250px'>" + 
                                        notification.getMessage() + "</div></html>");
        messageLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        messageLabel.setForeground(getTextColor(notification.getType()));
        
        messagePanel.add(titleLabel, BorderLayout.NORTH);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Close button
        JLabel closeLabel = new JLabel("×");
        closeLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        closeLabel.setForeground(getTextColor(notification.getType()));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideToast();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                closeLabel.setForeground(Color.RED);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                closeLabel.setForeground(getTextColor(notification.getType()));
            }
        });
        
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(closeLabel, BorderLayout.EAST);
        
        // Add click listener to close on click
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideToast();
            }
        });
        
        add(mainPanel);
        setSize(TOAST_WIDTH, TOAST_HEIGHT);
        
        // Position at top-right of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width - TOAST_WIDTH - 20;
        int y = 20;
        setLocation(x, y);
    }
    
    private void animateToast() {
        // Fade in animation
        fadeTimer = new Timer(ANIMATION_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fadingIn) {
                    opacity += 0.05f;
                    if (opacity >= 0.95f) {
                        opacity = 0.95f;
                        fadingIn = false;
                        fadeTimer.stop();
                        startDisplayTimer();
                    }
                } else {
                    opacity -= 0.05f;
                    if (opacity <= 0.0f) {
                        opacity = 0.0f;
                        fadeTimer.stop();
                        dispose();
                    }
                }
                
                // Apply opacity (this is a simplified approach)
                setVisible(opacity > 0);
            }
        });
        
        setVisible(true);
        fadeTimer.start();
    }
    
    private void startDisplayTimer() {
        displayTimer = new Timer(DISPLAY_TIME, e -> hideToast());
        displayTimer.setRepeats(false);
        displayTimer.start();
    }
    
    private void hideToast() {
        if (displayTimer != null) {
            displayTimer.stop();
        }
        
        fadingIn = false;
        if (fadeTimer != null && !fadeTimer.isRunning()) {
            fadeTimer.start();
        }
    }
    
    private Color getBackgroundColor(String type) {
        switch (type.toUpperCase()) {
            case "SUCCESS":
                return new Color(232, 245, 233); // Light green
            case "WARNING":
                return new Color(255, 248, 225); // Light orange
            case "ERROR":
                return new Color(255, 235, 238); // Light red
            case "INFO":
            default:
                return new Color(227, 242, 253); // Light blue
        }
    }
    
    private Color getBorderColor(String type) {
        switch (type.toUpperCase()) {
            case "SUCCESS":
                return new Color(76, 175, 80);
            case "WARNING":
                return new Color(255, 152, 0);
            case "ERROR":
                return new Color(244, 67, 54);
            case "INFO":
            default:
                return new Color(33, 150, 243);
        }
    }
    
    private Color getTextColor(String type) {
        switch (type.toUpperCase()) {
            case "SUCCESS":
                return new Color(27, 94, 32);
            case "WARNING":
                return new Color(230, 81, 0);
            case "ERROR":
                return new Color(183, 28, 28);
            case "INFO":
            default:
                return new Color(13, 71, 161);
        }
    }
    
    private String getIcon(String type) {
        switch (type.toUpperCase()) {
            case "SUCCESS":
                return "✅";
            case "WARNING":
                return "⚠️";
            case "ERROR":
                return "❌";
            case "INFO":
            default:
                return "ℹ️";
        }
    }
    
    /**
     * Show a toast notification
     * @param notification The notification to display
     */
    public static void showToast(Notification notification) {
        SwingUtilities.invokeLater(() -> {
            new ToastNotification(notification);
        });
    }
}
