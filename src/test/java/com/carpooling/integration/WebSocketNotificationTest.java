package com.carpooling.test;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.carpooling.websocket.NotificationWebSocketClient;
import com.carpooling.websocket.WebSocketManager;

/**
 * Test application for WebSocket notifications
 */
public class WebSocketNotificationTest {
    private static WebSocketManager webSocketManager;
    private static NotificationWebSocketClient client1;
    private static NotificationWebSocketClient client2;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createTestUI();
        });
    }
    
    private static void createTestUI() {
        JFrame frame = new JFrame("WebSocket Notification Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2, 10, 10));
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        
        // Start Server Button
        JButton startServerBtn = new JButton("Start WebSocket Server");
        startServerBtn.addActionListener(e -> {
            webSocketManager = WebSocketManager.getInstance();
            webSocketManager.startServer();
            JOptionPane.showMessageDialog(frame, "WebSocket Server started on port 8080");
        });
        
        // Stop Server Button
        JButton stopServerBtn = new JButton("Stop WebSocket Server");
        stopServerBtn.addActionListener(e -> {
            if (webSocketManager != null) {
                webSocketManager.stopServer();
                JOptionPane.showMessageDialog(frame, "WebSocket Server stopped");
            }
        });
        
        // Connect Client 1 (Passenger)
        JButton connectClient1Btn = new JButton("Connect Passenger (Client 1)");
        connectClient1Btn.addActionListener(e -> {
            try {
                client1 = NotificationWebSocketClient.getInstance();
                client1.connectAndRegister("passenger123");
                JOptionPane.showMessageDialog(frame, "Passenger connected to WebSocket");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error connecting passenger: " + ex.getMessage());
            }
        });
        
        // Send Trip Request
        JButton sendRequestBtn = new JButton("Send Trip Request");
        sendRequestBtn.addActionListener(e -> {
            if (client1 != null) {
                client1.sendTripRequest("passenger123", "Paris", "Lyon", "2025-06-15");
                JOptionPane.showMessageDialog(frame, "Trip request sent: Paris -> Lyon");
            } else {
                JOptionPane.showMessageDialog(frame, "Please connect passenger first");
            }
        });
        
        // Connect Client 2 (Driver)
        JButton connectClient2Btn = new JButton("Connect Driver (Client 2)");
        connectClient2Btn.addActionListener(e -> {
            try {
                // Get client instance for testing
                client2 = NotificationWebSocketClient.getInstance();
                if (client2 != null) {
                    System.out.println("Driver connection initiated!");
                }
                client2.connectAndRegister("driver456");
                JOptionPane.showMessageDialog(frame, "Driver connected to WebSocket");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error connecting driver: " + ex.getMessage());
            }
        });
        
        // Send Trip Response
        JButton sendResponseBtn = new JButton("Send Trip Response (Accept)");
        sendResponseBtn.addActionListener(e -> {
            if (client2 != null) {
                client2.sendTripResponse("driver456", "passenger123", "accepted", "Paris -> Lyon on 2025-06-15");
                JOptionPane.showMessageDialog(frame, "Trip response sent: ACCEPTED");
            } else {
                JOptionPane.showMessageDialog(frame, "Please connect driver first");
            }
        });
        
        frame.add(startServerBtn);
        frame.add(stopServerBtn);
        frame.add(connectClient1Btn);
        frame.add(connectClient2Btn);
        frame.add(sendRequestBtn);
        frame.add(sendResponseBtn);
        
        frame.setVisible(true);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (client1 != null) client1.disconnect();
            if (client2 != null) client2.disconnect();
            if (webSocketManager != null) webSocketManager.stopServer();
        }));
    }
}
