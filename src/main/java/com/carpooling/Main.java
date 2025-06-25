package com.carpooling;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.carpooling.repository.ITripRepository;
import com.carpooling.repository.IUserRepository;
import com.carpooling.repository.SqliteTripRepository;
import com.carpooling.repository.SqliteUserRepository;
import com.carpooling.service.AuthServiceImpl;
import com.carpooling.service.IAuthService;
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.service.TripServiceImpl;
import com.carpooling.service.UserServiceImpl;
import com.carpooling.ui.ModernAuthFrame;
import com.carpooling.websocket.WebSocketManager;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Main application entry point.
 * Responsible for dependency injection and application bootstrapping.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        // Set up modern FlatLaf Look and Feel
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            LOGGER.warning("Failed to initialize FlatLaf, falling back to system look and feel");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Start WebSocket server for real-time notifications
        LOGGER.info("Starting WebSocket server...");
        WebSocketManager.getInstance().startServer();
        
        SwingUtilities.invokeLater(() -> {
            // Create repository implementations
            IUserRepository userRepository = new SqliteUserRepository();
            ITripRepository tripRepository = new SqliteTripRepository();
            
            // Create service implementations with dependency injection
            IUserService userService = new UserServiceImpl(userRepository);
            IAuthService authService = new AuthServiceImpl(userRepository);
            ITripService tripService = new TripServiceImpl(tripRepository);
            
            // Create and show the modern auth frame with service dependencies
            ModernAuthFrame authFrame = new ModernAuthFrame(authService, userService, tripService);
            authFrame.showView();
        });
        
        // Add shutdown hook to stop WebSocket server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down WebSocket server...");
            WebSocketManager.getInstance().stopServer();
        }));
    }
} 