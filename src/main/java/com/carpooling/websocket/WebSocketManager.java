package com.carpooling.websocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

/**
 * Manager for WebSocket server lifecycle
 */
public class WebSocketManager {
    private static final Logger LOGGER = Logger.getLogger(WebSocketManager.class.getName());
    private static WebSocketManager instance;
    private NotificationWebSocketServer server;
    private boolean isServerRunning = false;
    private static final int WEBSOCKET_PORT = 8080;
    
    private WebSocketManager() {
    }
    
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    
    /**
     * Check if a port is available
     */
    private boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Start the WebSocket server
     */
    public void startServer() {
        if (!isServerRunning) {
            // Check if port is available before attempting to start
            if (!isPortAvailable(WEBSOCKET_PORT)) {
                LOGGER.warning("Port " + WEBSOCKET_PORT + " is already in use. The application will continue without hosting WebSocket server.");
                LOGGER.info("Real-time notifications will still work if another instance is running the WebSocket server.");
                isServerRunning = false;
                return;
            }
            
            try {
                server = NotificationWebSocketServer.getInstance();
                server.start();
                isServerRunning = true;
                LOGGER.info("WebSocket server started successfully");
            } catch (Exception e) {
                LOGGER.severe("Failed to start WebSocket server: " + e.getMessage());
                isServerRunning = false;
            }
        } else {
            LOGGER.info("WebSocket server is already running");
        }
    }
    
    /**
     * Stop the WebSocket server
     */
    public void stopServer() {
        if (isServerRunning && server != null) {
            try {
                server.stop();
                isServerRunning = false;
                LOGGER.info("WebSocket server stopped");
            } catch (Exception e) {
                LOGGER.severe("Error stopping WebSocket server: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if server is running
     */
    public boolean isServerRunning() {
        return isServerRunning;
    }
    
    /**
     * Get the server instance
     */
    public NotificationWebSocketServer getServer() {
        return server;
    }
    
    /**
     * Send notification to a specific user
     */
    public void sendNotificationToUser(String userId, String message) {
        if (isServerRunning && server != null) {
            server.sendNotificationToUser(userId, message);
        }
    }
    
    /**
     * Broadcast notification to all users
     */
    public void broadcastNotification(String message) {
        if (isServerRunning && server != null) {
            server.broadcastNotification(message);
        }
    }
}
