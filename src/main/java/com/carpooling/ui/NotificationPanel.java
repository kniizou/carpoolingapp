package com.carpooling.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;
import com.carpooling.model.User;

/**
 * Panel for displaying notifications in the passenger dashboard
 */
public class NotificationPanel extends JPanel {
    private User user;
    private NotificationManager notificationManager;
    private JLabel notificationCountLabel;
    private JPanel notificationsContainer;
    private boolean expanded = false;
    
    /**
     * Create a new notification panel
     * 
     * @param user The current user
     */
    public NotificationPanel(User user) {
        this.user = user;
        this.notificationManager = NotificationManager.getInstance();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create the notification bell icon with counter
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel bellIcon = new JLabel("\uD83D\uDD14"); // Bell emoji
        bellIcon.setFont(new Font("Dialog", Font.PLAIN, 18));
        bellIcon.setToolTipText("Notifications");
        
        notificationCountLabel = new JLabel("0");
        notificationCountLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        notificationCountLabel.setForeground(Color.WHITE);
        notificationCountLabel.setBackground(Color.RED);
        notificationCountLabel.setOpaque(true);
        notificationCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationCountLabel.setPreferredSize(new Dimension(20, 20));
        notificationCountLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        
        // Container for the notifications (initially empty/hidden)
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(new Color(250, 250, 250));
        notificationsContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setVisible(false);
        
        // Add click listener to toggle notifications
        JPanel iconPanel = new JPanel(new FlowLayout());
        iconPanel.add(bellIcon);
        iconPanel.add(notificationCountLabel);
        iconPanel.setBackground(new Color(245, 245, 245));
        iconPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        iconPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expanded = !expanded;
                scrollPane.setVisible(expanded);
                if (expanded) {
                    refreshNotifications();
                    notificationManager.markAllNotificationsAsRead(user.getId());
                    updateNotificationCount();
                }
                revalidate();
                repaint();
            }
        });
        
        headerPanel.add(iconPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Update notification count
        updateNotificationCount();
    }
    
    /**
     * Update the notification count badge
     */
    public void updateNotificationCount() {
        int count = notificationManager.countUnreadNotifications(user.getId());
        notificationCountLabel.setText(String.valueOf(count));
        notificationCountLabel.setVisible(count > 0);
    }
    
    /**
     * Refresh the notifications displayed in the panel
     */
    public void refreshNotifications() {
        notificationsContainer.removeAll();
        
        List<Notification> notifications = notificationManager.getNotificationsForUser(user.getId());
        
        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("Pas de notifications");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            notificationsContainer.add(emptyLabel);
        } else {
            for (Notification notification : notifications) {
                JPanel notifPanel = createNotificationPanel(notification);
                notificationsContainer.add(notifPanel);
                notificationsContainer.add(Box.createVerticalStrut(5)); // Add space between notifications
            }
        }
        
        notificationsContainer.revalidate();
        notificationsContainer.repaint();
    }
    
    /**
     * Create a panel for a single notification
     * 
     * @param notification The notification to display
     * @return A JPanel containing the notification
     */
    private JPanel createNotificationPanel(Notification notification) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getColorForNotificationType(notification.getType()), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        // Icon based on notification type
        JLabel typeIcon = new JLabel(getIconForNotificationType(notification.getType()));
        typeIcon.setFont(new Font("Dialog", Font.PLAIN, 18));
        
        // Message
        JLabel messageLabel = new JLabel("<html><div style='width:300px'>" + notification.getMessage() + "</div></html>");
        messageLabel.setFont(new Font("Dialog", notification.isRead() ? Font.PLAIN : Font.BOLD, 12));
        
        // Timestamp
        JLabel timeLabel = new JLabel(notification.getFormattedTimestamp());
        timeLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
        timeLabel.setForeground(Color.GRAY);
        
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(panel.getBackground());
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(timeLabel, BorderLayout.SOUTH);
        
        panel.add(typeIcon, BorderLayout.WEST);
        panel.add(messagePanel, BorderLayout.CENTER);
        
        // Mark as read/Delete controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlsPanel.setBackground(panel.getBackground());
        
        JLabel deleteLabel = new JLabel("❌");
        deleteLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        deleteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteLabel.setToolTipText("Supprimer");
        deleteLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                notificationManager.removeNotification(user.getId(), notification.getId());
                refreshNotifications();
                updateNotificationCount();
            }
        });
        
        controlsPanel.add(deleteLabel);
        panel.add(controlsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Get the appropriate color for a notification type
     * 
     * @param type The notification type
     * @return The color associated with the type
     */
    private Color getColorForNotificationType(String type) {
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
    
    /**
     * Get the appropriate icon for a notification type
     * 
     * @param type The notification type
     * @return The icon character associated with the type
     */
    private String getIconForNotificationType(String type) {
        switch (type.toUpperCase()) {
            case "SUCCESS":
                return "✅"; // Check mark
            case "WARNING":
                return "⚠️"; // Warning
            case "ERROR":
                return "❌"; // X mark
            case "INFO":
            default:
                return "ℹ️"; // Info
        }
    }
    
    /**
     * Add a test notification (for testing purposes)
     */
    public void addTestNotification() {
        notificationManager.addTestNotification(user.getId());
        updateNotificationCount();
        if (expanded) {
            refreshNotifications();
        }
        
        // Show toast notification for the new notification
        ToastNotificationManager.getInstance().showToastsForUser(user.getId());
    }
}
