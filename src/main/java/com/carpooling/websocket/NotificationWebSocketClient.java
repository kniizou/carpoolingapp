package com.carpooling.websocket;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;
import com.carpooling.ui.ToastNotificationManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WebSocket client for connecting to the notification server
 */
public class NotificationWebSocketClient extends WebSocketClient {
    private static final Logger LOGGER = Logger.getLogger(NotificationWebSocketClient.class.getName());
    private static NotificationWebSocketClient instance;
    private String currentUserId;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Retry and acknowledgment fields
    private int maxRetries = 3;
    private long retryDelayMs = 2000;
    private final Map<String, Long> pendingMessages = new ConcurrentHashMap<>();
    private final Map<String, Integer> messageRetryCount = new ConcurrentHashMap<>();
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(2);
    
    private NotificationWebSocketClient(URI serverUri) {
        super(serverUri);
    }
    
    public static synchronized NotificationWebSocketClient getInstance() {
        if (instance == null) {
            try {
                URI serverUri = new URI("ws://localhost:8080");
                instance = new NotificationWebSocketClient(serverUri);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create WebSocket client", e);
            }
        }
        return instance;
    }
    
    /**
     * Connect and register user for notifications
     */
    public void connectAndRegister(String userId) {
        this.currentUserId = userId;
        
        if (!isOpen()) {
            try {
                LOGGER.info("Attempting to connect to WebSocket server...");
                connect();
                
                // Wait a bit for connection to establish, then register
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // Wait 1 second
                        if (isOpen()) {
                            registerUser(userId);
                        } else {
                            LOGGER.warning("WebSocket connection not established. User will not receive real-time notifications.");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
                
            } catch (Exception e) {
                LOGGER.warning("Could not connect to WebSocket server. Real-time notifications disabled. " + e.getMessage());
            }
        } else {
            registerUser(userId);
        }
    }
    
    /**
     * Register user with the server
     */
    private void registerUser(String userId) {
        try {
            String registerMessage = objectMapper.writeValueAsString(Map.of(
                "type", "register",
                "userId", userId
            ));
            send(registerMessage);
            LOGGER.info("Registered user: " + userId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to register user", e);
        }
    }
    
    /**
     * Send trip request to notify drivers
     */
    public void sendTripRequest(String passengerId, String departure, String destination, String date) {
        if (isOpen()) {
            try {
                String tripRequestMessage = objectMapper.writeValueAsString(Map.of(
                    "type", "trip_request",
                    "passengerId", passengerId,
                    "departure", departure,
                    "destination", destination,
                    "date", date
                ));
                sendMessageWithRetry(UUID.randomUUID().toString(), tripRequestMessage, "trip request");
                LOGGER.info("Sent trip request: " + departure + " -> " + destination);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to send trip request", e);
            }
        } else {
            LOGGER.info("WebSocket not connected. Trip request notification will be handled through database only.");
        }
    }
    
    /**
     * Send trip response (accept/decline)
     */
    public void sendTripResponse(String driverId, String passengerId, String response, String tripDetails) {
        if (isOpen()) {
            try {
                String responseMessage = objectMapper.writeValueAsString(Map.of(
                    "type", "trip_response",
                    "driverId", driverId,
                    "passengerId", passengerId,
                    "response", response,
                    "tripDetails", tripDetails
                ));
                sendMessageWithRetry(UUID.randomUUID().toString(), responseMessage, "trip response");
                LOGGER.info("Sent trip response: " + response);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to send trip response", e);
            }
        }
    }
    
    @Override
    public void onOpen(ServerHandshake handshake) {
        LOGGER.info("Connected to WebSocket server");
        if (currentUserId != null) {
            registerUser(currentUserId);
        }
    }
    
    @Override
    public void onMessage(String message) {
        try {
            JsonNode messageJson = objectMapper.readTree(message);
            String type = messageJson.get("type").asText();
            
            switch (type) {
                case "registration_success":
                    LOGGER.info("Successfully registered for WebSocket notifications");
                    break;
                    
                case "new_trip_request":
                    handleNewTripRequest(messageJson);
                    break;
                    
                case "trip_response":
                    handleTripResponse(messageJson);
                    break;
                    
                case "broadcast":
                    handleBroadcast(messageJson);
                    break;
                    
                case "notification":
                    handleNotification(messageJson);
                    break;
                    
                case "acknowledgment":
                    handleAcknowledgment(messageJson);
                    break;
                    
                default:
                    LOGGER.info("Received unknown message type: " + type);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing WebSocket message: " + message, e);
        }
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.info("WebSocket connection closed: " + reason);
    }
    
    @Override
    public void onError(Exception ex) {
        LOGGER.log(Level.WARNING, "WebSocket error", ex);
    }
    
    /**
     * Handle new trip request notification
     */
    private void handleNewTripRequest(JsonNode messageJson) {
        String message = messageJson.get("message").asText();
        
        LOGGER.info("Received trip request notification: " + message);
        
        // Create and show toast notification
        SwingUtilities.invokeLater(() -> {
            Notification notification = Notification.createInfoNotification(message, "websocket_trip_request");
            ToastNotificationManager.getInstance().showToast(notification);
            
            // Also add to notification manager for persistence
            if (currentUserId != null) {
                NotificationManager.getInstance().addNotification(currentUserId, notification);
            }
        });
    }
    
    /**
     * Handle trip response notification
     */
    private void handleTripResponse(JsonNode messageJson) {
        String message = messageJson.get("message").asText();
        
        LOGGER.info("Received trip response notification: " + message);
        
        // Create and show toast notification
        SwingUtilities.invokeLater(() -> {
            Notification notification = message.contains("acceptée") 
                ? Notification.createSuccessNotification(message, "websocket_trip_response")
                : Notification.createWarningNotification(message, "websocket_trip_response");
            
            ToastNotificationManager.getInstance().showToast(notification);
            
            // Also add to notification manager for persistence
            if (currentUserId != null) {
                NotificationManager.getInstance().addNotification(currentUserId, notification);
            }
        });
    }
    
    /**
     * Handle broadcast notification
     */
    private void handleBroadcast(JsonNode messageJson) {
        String message = messageJson.get("message").asText();
        
        LOGGER.info("Received broadcast notification: " + message);
        
        SwingUtilities.invokeLater(() -> {
            Notification notification = Notification.createInfoNotification(message, "websocket_broadcast");
            ToastNotificationManager.getInstance().showToast(notification);
            
            if (currentUserId != null) {
                NotificationManager.getInstance().addNotification(currentUserId, notification);
            }
        });
    }
    
    /**
     * Handle regular notification
     */
    private void handleNotification(JsonNode messageJson) {
        String message = messageJson.get("message").asText();
        
        LOGGER.info("Received notification: " + message);
        
        SwingUtilities.invokeLater(() -> {
            Notification notification = Notification.createInfoNotification(message, "websocket_notification");
            ToastNotificationManager.getInstance().showToast(notification);
            
            if (currentUserId != null) {
                NotificationManager.getInstance().addNotification(currentUserId, notification);
            }
        });
    }
    
    /**
     * Handle acknowledgment messages
     */
    private void handleAcknowledgment(JsonNode messageJson) {
        if (messageJson.has("messageId")) {
            String messageId = messageJson.get("messageId").asText();
            acknowledgeMessage(messageId);
        }
    }
    
    /**
     * Send message with retry logic and acknowledgment tracking
     */
    private void sendMessageWithRetry(String messageId, String messageContent, String messageType) {
        if (!isOpen()) {
            LOGGER.warning("WebSocket not connected. Cannot send " + messageType + " message.");
            return;
        }
        
        try {
            send(messageContent);
            
            // Track message for acknowledgment
            pendingMessages.put(messageId, System.currentTimeMillis());
            messageRetryCount.put(messageId, 0);
            
            // Schedule retry check
            retryExecutor.schedule(() -> checkAndRetryMessage(messageId, messageContent, messageType), 
                                  retryDelayMs, TimeUnit.MILLISECONDS);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send " + messageType + " message", e);
        }
    }
    
    /**
     * Check if message needs retry and retry if necessary
     */
    private void checkAndRetryMessage(String messageId, String messageContent, String messageType) {
        if (!pendingMessages.containsKey(messageId)) {
            // Message was acknowledged, no need to retry
            return;
        }
        
        int retryCount = messageRetryCount.getOrDefault(messageId, 0);
        if (retryCount >= maxRetries) {
            LOGGER.warning("Max retries reached for " + messageType + " message: " + messageId);
            pendingMessages.remove(messageId);
            messageRetryCount.remove(messageId);
            return;
        }
        
        // Retry sending the message
        if (isOpen()) {
            try {
                send(messageContent);
                messageRetryCount.put(messageId, retryCount + 1);
                
                // Schedule next retry check
                retryExecutor.schedule(() -> checkAndRetryMessage(messageId, messageContent, messageType), 
                                      retryDelayMs * (retryCount + 1), TimeUnit.MILLISECONDS);
                
                LOGGER.info("Retried " + messageType + " message (" + (retryCount + 1) + "/" + maxRetries + "): " + messageId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to retry " + messageType + " message", e);
            }
        } else {
            LOGGER.warning("WebSocket disconnected. Cannot retry " + messageType + " message: " + messageId);
        }
    }
    
    /**
     * Acknowledge message receipt
     */
    private void acknowledgeMessage(String messageId) {
        if (pendingMessages.remove(messageId) != null) {
            messageRetryCount.remove(messageId);
            LOGGER.info("Acknowledged message: " + messageId);
        }
    }
    
    /**
     * Disconnect from the WebSocket server and cleanup resources
     */
    public void disconnect() {
        if (isOpen()) {
            close();
        }
        
        // Clear pending messages
        pendingMessages.clear();
        messageRetryCount.clear();
        
        // Shutdown retry executor
        if (!retryExecutor.isShutdown()) {
            retryExecutor.shutdown();
        }
    }
}
