package com.carpooling.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationType;
import com.carpooling.repository.INotificationRepository;

/**
 * Unit tests for NotificationServiceImpl.
 */
class NotificationServiceImplTest {

    @Mock
    private INotificationRepository mockNotificationRepository;

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl(mockNotificationRepository);
    }

    @Test
    void testConstructor_NullRepository_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new NotificationServiceImpl(null);
        });
        
        assertEquals("Notification repository cannot be null", exception.getMessage());
    }

    @Test
    void testCreateNotification_ValidData_Success() {
        // Arrange
        String userId = "user123";
        String message = "Test notification";
        NotificationType type = NotificationType.TRIP_REQUEST;
        String relatedEntityId = "trip123";

        // Act
        Notification result = notificationService.createNotification(userId, message, type, relatedEntityId);

        // Assert
        assertNotNull(result);
        assertEquals(message, result.getMessage());
        assertEquals(type.getCode(), result.getType());
        assertEquals(relatedEntityId, result.getRelatedEntityId());
        verify(mockNotificationRepository).saveNotification(any(Notification.class));
    }

    @Test
    void testCreateNotification_StringType_Success() {
        // Arrange
        String userId = "user123";
        String message = "Test notification";
        String type = "TRIP_REQUEST";
        String relatedEntityId = "trip123";

        // Act
        Notification result = notificationService.createNotification(userId, message, type, relatedEntityId);

        // Assert
        assertNotNull(result);
        assertEquals(message, result.getMessage());
        assertEquals(type, result.getType());
        assertEquals(relatedEntityId, result.getRelatedEntityId());
        verify(mockNotificationRepository).saveNotification(any(Notification.class));
    }

    @Test
    void testCreateNotification_NullUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification(null, "message", NotificationType.SYSTEM, "entityId");
        });
        
        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateNotification_EmptyUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification("", "message", NotificationType.SYSTEM, "entityId");
        });
        
        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateNotification_NullMessage_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification("user123", null, NotificationType.SYSTEM, "entityId");
        });
        
        assertEquals("Message cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateNotification_EmptyMessage_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification("user123", "", NotificationType.SYSTEM, "entityId");
        });
        
        assertEquals("Message cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCreateNotification_NullType_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification("user123", "message", (NotificationType) null, "entityId");
        });
        
        assertEquals("Notification type cannot be null", exception.getMessage());
    }

    @Test
    void testGetNotificationsForUser_ValidUserId_ReturnsNotifications() {
        // Arrange
        String userId = "user123";
        List<Notification> expectedNotifications = Arrays.asList(
            new Notification("message1", NotificationType.TRIP_REQUEST, "entity1"),
            new Notification("message2", NotificationType.TRIP_CONFIRMATION, "entity2")
        );
        when(mockNotificationRepository.getNotificationsByUserId(userId)).thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getNotificationsForUser(userId);

        // Assert
        assertEquals(expectedNotifications.size(), result.size());
        assertEquals(expectedNotifications, result);
        verify(mockNotificationRepository).getNotificationsByUserId(userId);
    }

    @Test
    void testGetNotificationsForUser_NullUserId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.getNotificationsForUser(null);
        });
        
        assertEquals("User ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetUnreadNotificationsForUser_ValidUserId_ReturnsUnreadNotifications() {
        // Arrange
        String userId = "user123";
        List<Notification> expectedNotifications = Arrays.asList(
            new Notification("unread message", NotificationType.TRIP_REQUEST, "entity1")
        );
        when(mockNotificationRepository.getUnreadNotificationsByUserId(userId)).thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getUnreadNotificationsForUser(userId);

        // Assert
        assertEquals(expectedNotifications.size(), result.size());
        assertEquals(expectedNotifications, result);
        verify(mockNotificationRepository).getUnreadNotificationsByUserId(userId);
    }

    @Test
    void testMarkNotificationAsRead_ValidId_CallsRepository() {
        // Arrange
        String notificationId = "notif123";

        // Act
        notificationService.markNotificationAsRead(notificationId);

        // Assert
        verify(mockNotificationRepository).markNotificationAsRead(notificationId);
    }

    @Test
    void testMarkNotificationAsRead_NullId_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.markNotificationAsRead(null);
        });
        
        assertEquals("Notification ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void testDeleteNotification_ValidId_ReturnsTrue() {
        // Arrange
        String notificationId = "notif123";
        when(mockNotificationRepository.deleteNotification(notificationId)).thenReturn(true);

        // Act
        boolean result = notificationService.deleteNotification(notificationId);

        // Assert
        assertTrue(result);
        verify(mockNotificationRepository).deleteNotification(notificationId);
    }

    @Test
    void testGetUnreadNotificationCount_ValidUserId_ReturnsCount() {
        // Arrange
        String userId = "user123";
        int expectedCount = 5;
        when(mockNotificationRepository.getUnreadNotificationCount(userId)).thenReturn(expectedCount);

        // Act
        int result = notificationService.getUnreadNotificationCount(userId);

        // Assert
        assertEquals(expectedCount, result);
        verify(mockNotificationRepository).getUnreadNotificationCount(userId);
    }

    @Test
    void testCleanupOldNotifications_ValidDays_ReturnsDeletedCount() {
        // Arrange
        int days = 30;
        int expectedDeleted = 10;
        when(mockNotificationRepository.deleteOldNotifications(days)).thenReturn(expectedDeleted);

        // Act
        int result = notificationService.cleanupOldNotifications(days);

        // Assert
        assertEquals(expectedDeleted, result);
        verify(mockNotificationRepository).deleteOldNotifications(days);
    }

    @Test
    void testCleanupOldNotifications_InvalidDays_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.cleanupOldNotifications(0);
        });
        
        assertEquals("Days must be positive", exception.getMessage());
    }

    @Test
    void testCreateTripRequestNotification_ValidData_CreatesNotification() {
        // Arrange
        String driverId = "driver123";
        String passengerName = "John Doe";
        String tripDetails = "Paris -> Lyon";
        String tripId = "trip123";

        // Act
        Notification result = notificationService.createTripRequestNotification(driverId, passengerName, tripDetails, tripId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMessage().contains(passengerName));
        assertTrue(result.getMessage().contains(tripDetails));
        assertEquals(NotificationType.TRIP_REQUEST.getCode(), result.getType());
        assertEquals(tripId, result.getRelatedEntityId());
        verify(mockNotificationRepository).saveNotification(any(Notification.class));
    }

    @Test
    void testCreateTripConfirmationNotification_ValidData_CreatesNotification() {
        // Arrange
        String passengerId = "passenger123";
        String tripDetails = "Paris -> Lyon";
        String tripId = "trip123";

        // Act
        Notification result = notificationService.createTripConfirmationNotification(passengerId, tripDetails, tripId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMessage().contains(tripDetails));
        assertTrue(result.getMessage().contains("acceptée"));
        assertEquals(NotificationType.TRIP_CONFIRMATION.getCode(), result.getType());
        assertEquals(tripId, result.getRelatedEntityId());
        verify(mockNotificationRepository).saveNotification(any(Notification.class));
    }

    @Test
    void testCreateTripCancellationNotification_ValidData_CreatesNotification() {
        // Arrange
        String userId = "user123";
        String tripDetails = "Paris -> Lyon";
        String tripId = "trip123";

        // Act
        Notification result = notificationService.createTripCancellationNotification(userId, tripDetails, tripId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMessage().contains(tripDetails));
        assertTrue(result.getMessage().contains("annulé"));
        assertEquals(NotificationType.TRIP_CANCELLATION.getCode(), result.getType());
        assertEquals(tripId, result.getRelatedEntityId());
        verify(mockNotificationRepository).saveNotification(any(Notification.class));
    }
}
