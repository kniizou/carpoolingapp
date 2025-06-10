package com.carpooling.ui;

import com.carpooling.data.DataManager;
import com.carpooling.model.User;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private DataManager dataManager;

    public LoginFrame() {
        dataManager = DataManager.getInstance();
        
        setTitle("Covoiturage - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Configuration des couleurs
        Color primaryColor = new Color(94, 0, 0); // #5e0000
        Color backgroundColor = Color.WHITE;
        Color textColor = new Color(50, 50, 50);
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Panel de connexion
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Titre
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(titleLabel, gbc);
        
        // Email
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(textColor);
        loginPanel.add(emailLabel, gbc);
        
        emailField = new JTextField(20);
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(textColor);
        gbc.gridx = 1;
        loginPanel.add(emailField, gbc);
        
        // Mot de passe
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setForeground(textColor);
        loginPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(textColor);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        
        loginButton = new JButton("Connexion");
        loginButton.setBackground(primaryColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(_ -> handleLogin());
        
        registerButton = new JButton("Inscription");
        registerButton.setBackground(primaryColor);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(_ -> openRegistration());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);
        
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        if (dataManager.authenticateUser(email, password)) {
            User currentUser = dataManager.getCurrentUser();
            JOptionPane.showMessageDialog(this, "Connexion réussie!");
            
            // Ouvrir le dashboard approprié selon le rôle
            switch (currentUser.getRole()) {
                case "ADMIN":
                    openAdminDashboard();
                    break;
                case "DRIVER":
                    openDriverDashboard();
                    break;
                case "PASSENGER":
                    openPassengerDashboard();
                    break;
            }
            
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openRegistration() {
        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setVisible(true);
    }
    
    private void openAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard();
        adminDashboard.setVisible(true);
    }
    
    private void openDriverDashboard() {
        DriverDashboard driverDashboard = new DriverDashboard();
        driverDashboard.setVisible(true);
    }
    
    private void openPassengerDashboard() {
        PassengerDashboard passengerDashboard = new PassengerDashboard();
        passengerDashboard.setVisible(true);
    }
} 