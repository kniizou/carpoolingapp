package com.carpooling.websocket;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;
import com.carpooling.ui.ToastNotificationManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.swing.SwingUtilities;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocket server for real-time notifications
 */
public class NotificationWebSocketServer extends WebSocketServer {
    private static final Logger LOGGER = Logger.getLogger(NotificationWebSocketServer.class.getName());
    private static final int PORT = 8080;
    private static NotificationWebSocketServer instance;
    
    // Map to store connected users: userId -> WebSocket connection
    private final Map<String, WebSocket> connectedUsers = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationManager notificationManager = NotificationManager.getInstance();
    
    private NotificationWebSocketServer() {
        super(new InetSocketAddress(PORT));
        LOGGER.info("WebSocket server initialized on port " + PORT);
    }
    
    public static synchronized NotificationWebSocketServer getInstance() {
        if (instance == null) {
            instance = new NotificationWebSocketServer();
        }
        return instance;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info("New WebSocket connection from: " + conn.getRemoteSocketAddress());
        // User identification will be handled in onMessage when they send their userId
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Remove user from connected users map
        String disconnectedUserId = null;
        for (Map.Entry<String, WebSocket> entry : connectedUsers.entrySet()) {
            if (entry.getValue() == conn) {
                disconnectedUserId = entry.getKey();
                break;
            }
        }
        
        if (disconnectedUserId != null) {
            connectedUsers.remove(disconnectedUserId);
            LOGGER.info("User " + disconnectedUserId + " disconnected");
        }
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonNode messageJson = objectMapper.readTree(message);
            String type = messageJson.get("type").asText();
            
            switch (type) {
                case "register":
                    handleUserRegistration(conn, messageJson);
                    break;
                case "trip_request":
                    handleTripRequest(messageJson);
                    break;
                case "trip_response":
                    handleTripResponse(messageJson);
                    break;
                default:
                    LOGGER.warning("Unknown message type: " + type);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing WebSocket message", e);
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.log(Level.SEVERE, "WebSocket error", ex);
    }
    
    @Override
    public void onStart() {
        LOGGER.info("WebSocket server started on port " + PORT);
    }
    
    /**
     * Handle user registration for WebSocket connection
     */
    private void handleUserRegistration(WebSocket conn, JsonNode messageJson) {
        String userId = messageJson.get("userId").asText();
        connectedUsers.put(userId, conn);
        LOGGER.info("User " + userId + " registered for WebSocket notifications");
        
        // Send confirmation
        sendMessage(conn, createMessage("registration_success", "User registered successfully"));
    }
    
    /**
     * Handle trip request - notify relevant drivers
     */
    private void handleTripRequest(JsonNode messageJson) throws JsonProcessingException {
        String passengerId = messageJson.get("passengerId").asText();
        String departure = messageJson.get("departure").asText();
        String destination = messageJson.get("destination").asText();
        String date = messageJson.get("date").asText();
        
        LOGGER.info("Trip request from " + passengerId + ": " + departure + " -> " + destination + " on " + date);
        
        // Create notification for drivers
        String notificationMessage = "Nouvelle demande de trajet: " + departure + " → " + destination + " le " + date;
        
        // Notify all connected drivers
        for (Map.Entry<String, WebSocket> entry : connectedUsers.entrySet()) {
            String userId = entry.getKey();
            WebSocket connection = entry.getValue();
            
            // For now, notify all connected users (in real app, you'd filter by drivers near the route)
            if (!userId.equals(passengerId)) {
                // Create notification in database
                Notification notification = Notification.createInfoNotification(notificationMessage, "trip_request_" + System.currentTimeMillis());
                notificationManager.addNotification(userId, notification);
                
                // Send real-time notification via WebSocket
                String notificationJson = createNotificationMessage("new_trip_request", notificationMessage, passengerId, departure, destination, date);
                sendMessage(connection, notificationJson);
                
                // Show toast notification in UI (if user is active)
                SwingUtilities.invokeLater(() -> {
                    ToastNotificationManager.getInstance().showToast(notification);
                });
            }
        }
    }
    
    /**
     * Handle trip response - notify the passenger
     */
    private void handleTripResponse(JsonNode messageJson) throws JsonProcessingException {
        String driverId = messageJson.get("driverId").asText();
        String passengerId = messageJson.get("passengerId").asText();
        String response = messageJson.get("response").asText(); // "accepted" or "declined"
        String tripDetails = messageJson.get("tripDetails").asText();
        
        WebSocket passengerConnection = connectedUsers.get(passengerId);
        if (passengerConnection != null) {
            String message;
            if ("accepted".equals(response)) {
                message = "Votre demande de trajet a été acceptée! " + tripDetails;
            } else {
                message = "Votre demande de trajet a été déclinée. " + tripDetails;
            }
            
            // Create notification in database
            Notification notification = "accepted".equals(response) 
                ? Notification.createSuccessNotification(message, "trip_response_" + System.currentTimeMillis())
                : Notification.createWarningNotification(message, "trip_response_" + System.currentTimeMillis());
            notificationManager.addNotification(passengerId, notification);
            
            // Send real-time notification
            String notificationJson = createNotificationMessage("trip_response", message, driverId, "", "", "");
            sendMessage(passengerConnection, notificationJson);
            
            // Show toast notification
            SwingUtilities.invokeLater(() -> {
                ToastNotificationManager.getInstance().showToast(notification);
            });
        }
    }
    
    /**
     * Send a message to a specific WebSocket connection
     */
    private void sendMessage(WebSocket connection, String message) {
        if (connection != null && connection.isOpen()) {
            connection.send(message);
        }
    }
    
    /**
     * Broadcast a notification to all connected users
     */
    public void broadcastNotification(String message) {
        String notificationJson = createMessage("broadcast", message);
        for (WebSocket connection : connectedUsers.values()) {
            sendMessage(connection, notificationJson);
        }
    }
    
    /**
     * Send notification to a specific user
     */
    public void sendNotificationToUser(String userId, String message) {
        WebSocket connection = connectedUsers.get(userId);
        if (connection != null) {
            String notificationJson = createMessage("notification", message);
            sendMessage(connection, notificationJson);
        }
    }
    
    /**
     * Create a simple JSON message
     */
    private String createMessage(String type, String content) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                "type", type,
                "message", content,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error creating message", e);
            return "{\"type\":\"error\",\"message\":\"Message creation failed\"}";
        }
    }
    
    /**
     * Create a detailed notification message
     */
    private String createNotificationMessage(String type, String message, String fromUserId, 
                                           String departure, String destination, String date) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                "type", type,
                "message", message,
                "fromUserId", fromUserId,
                "departure", departure,
                "destination", destination,
                "date", date,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, "Error creating notification message", e);
            return createMessage("error", "Notification creation failed");
        }
    }
    
    /**
     * Get the number of connected users
     */
    public int getConnectedUserCount() {
        return connectedUsers.size();
    }
    
    /**
     * Check if a user is connected
     */
    public boolean isUserConnected(String userId) {
        return connectedUsers.containsKey(userId);
    }
}
