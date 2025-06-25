package com.carpooling.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.carpooling.data.DatabaseConnection;
import com.carpooling.model.Notification;
import com.carpooling.model.NotificationType;

/**
 * SQLite implementation of the notification repository.
 * Provides persistent storage for notifications using SQLite database.
 */
public class SqliteNotificationRepository implements INotificationRepository {
    private static final Logger LOGGER = Logger.getLogger(SqliteNotificationRepository.class.getName());
    
    /**
     * Initializes the notification repository and creates the notifications table if it doesn't exist.
     */
    public SqliteNotificationRepository() {
        createNotificationsTableIfNotExists();
    }
    
    /**
     * Creates the notifications table if it doesn't exist.
     */
    private void createNotificationsTableIfNotExists() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS notifications (
                id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                message TEXT NOT NULL,
                type TEXT NOT NULL,
                timestamp DATETIME NOT NULL,
                is_read BOOLEAN DEFAULT FALSE,
                related_entity_id TEXT,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
            LOGGER.info("Notifications table created/verified successfully");
        } catch (SQLException e) {
            LOGGER.severe("Failed to create notifications table: " + e.getMessage());
            throw new RuntimeException("Failed to initialize notification repository", e);
        }
    }
    
    @Override
    public void saveNotification(Notification notification) {
        String sql = """
            INSERT INTO notifications (id, user_id, message, type, timestamp, is_read, related_entity_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notification.getId());
            stmt.setString(2, notification.getRelatedEntityId()); // Assuming this is user_id
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getType());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setBoolean(6, notification.isRead());
            stmt.setString(7, notification.getRelatedEntityId());
            
            stmt.executeUpdate();
            LOGGER.info("Notification saved successfully: " + notification.getId());
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to save notification: " + e.getMessage());
            throw new RuntimeException("Failed to save notification", e);
        }
    }
    
    @Override
    public List<Notification> getNotificationsByUserId(String userId) {
        String sql = """
            SELECT id, message, type, timestamp, is_read, related_entity_id
            FROM notifications
            WHERE user_id = ?
            ORDER BY timestamp DESC
        """;
        
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification(
                    rs.getString("id"),
                    rs.getString("message"),
                    NotificationType.fromCode(rs.getString("type")),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getBoolean("is_read"),
                    rs.getString("related_entity_id")
                );
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to get notifications for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve notifications", e);
        }
        
        return notifications;
    }
    
    @Override
    public List<Notification> getUnreadNotificationsByUserId(String userId) {
        String sql = """
            SELECT id, message, type, timestamp, is_read, related_entity_id
            FROM notifications
            WHERE user_id = ? AND is_read = FALSE
            ORDER BY timestamp DESC
        """;
        
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification(
                    rs.getString("id"),
                    rs.getString("message"),
                    NotificationType.fromCode(rs.getString("type")),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getBoolean("is_read"),
                    rs.getString("related_entity_id")
                );
                notifications.add(notification);
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to get unread notifications for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve unread notifications", e);
        }
        
        return notifications;
    }
    
    @Override
    public void markNotificationAsRead(String notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notificationId);
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                LOGGER.info("Notification marked as read: " + notificationId);
            } else {
                LOGGER.warning("No notification found with ID: " + notificationId);
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to mark notification as read: " + e.getMessage());
            throw new RuntimeException("Failed to mark notification as read", e);
        }
    }
    
    @Override
    public void markAllNotificationsAsRead(String userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            int rowsUpdated = stmt.executeUpdate();
            
            LOGGER.info("Marked " + rowsUpdated + " notifications as read for user: " + userId);
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to mark all notifications as read for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to mark all notifications as read", e);
        }
    }
    
    @Override
    public boolean deleteNotification(String notificationId) {
        String sql = "DELETE FROM notifications WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notificationId);
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                LOGGER.info("Notification deleted: " + notificationId);
                return true;
            } else {
                LOGGER.warning("No notification found with ID: " + notificationId);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to delete notification: " + e.getMessage());
            throw new RuntimeException("Failed to delete notification", e);
        }
    }
    
    @Override
    public int deleteOldNotifications(int days) {
        String sql = "DELETE FROM notifications WHERE timestamp < datetime('now', '-' || ? || ' days')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, days);
            int rowsDeleted = stmt.executeUpdate();
            
            LOGGER.info("Deleted " + rowsDeleted + " notifications older than " + days + " days");
            return rowsDeleted;
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to delete old notifications: " + e.getMessage());
            throw new RuntimeException("Failed to delete old notifications", e);
        }
    }
    
    @Override
    public int getUnreadNotificationCount(String userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Failed to get unread notification count for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to get unread notification count", e);
        }
        
        return 0;
    }
}
