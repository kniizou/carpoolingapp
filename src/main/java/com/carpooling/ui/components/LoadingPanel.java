package com.carpooling.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.carpooling.ui.Theme;

/**
 * Component for displaying loading states with optional message.
 */
public class LoadingPanel extends JPanel {
    
    private final JLabel messageLabel;
    private final JLabel spinnerLabel;
    private final Timer animationTimer;
    private int animationFrame = 0;
    private final String[] spinnerFrames = {"◐", "◓", "◑", "◒"};
    
    public LoadingPanel() {
        this("Chargement en cours...");
    }
    
    public LoadingPanel(String message) {
        setLayout(new GridBagLayout());
        setBackground(Theme.SURFACE_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Spinner
        spinnerLabel = new JLabel(spinnerFrames[0]);
        spinnerLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        spinnerLabel.setForeground(Theme.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(spinnerLabel, gbc);
        
        // Message
        messageLabel = new JLabel(message);
        Theme.styleLabel(messageLabel, Theme.FONT_BODY, Theme.TEXT_SECONDARY);
        gbc.gridy = 1;
        add(messageLabel, gbc);
        
        // Animation timer
        animationTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationFrame = (animationFrame + 1) % spinnerFrames.length;
                spinnerLabel.setText(spinnerFrames[animationFrame]);
            }
        });
    }
    
    /**
     * Sets the loading message.
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    /**
     * Starts the loading animation.
     */
    public void startLoading() {
        setVisible(true);
        animationTimer.start();
    }
    
    /**
     * Stops the loading animation.
     */
    public void stopLoading() {
        animationTimer.stop();
        setVisible(false);
    }
    
    /**
     * Creates an overlay loading panel that covers a parent component.
     */
    public static LoadingPanel createOverlay(JComponent parent, String message) {
        LoadingPanel loadingPanel = new LoadingPanel(message);
        loadingPanel.setOpaque(true);
        loadingPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        
        // Add to parent using overlay layout
        OverlayLayout overlay = new OverlayLayout(parent);
        parent.setLayout(overlay);
        parent.add(loadingPanel);
        loadingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        return loadingPanel;
    }
}
