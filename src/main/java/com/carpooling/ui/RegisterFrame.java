package com.carpooling.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.carpooling.model.User;
import com.carpooling.service.IAuthService;
import com.carpooling.service.IUserService;
import com.carpooling.util.SecurityUtils;

public class RegisterFrame extends JFrame {
    private final IUserService userService;
    private final IAuthService authService;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField ageField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public RegisterFrame(IUserService userService, IAuthService authService) {
        this.userService = userService;
        this.authService = authService;
        
        setTitle("Inscription");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Nom:"), gbc);
        
        nomField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(nomField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Prénom:"), gbc);
        
        prenomField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(prenomField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Âge:"), gbc);
        
        ageField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(ageField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        emailField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Mot de passe:"), gbc);
        
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Rôle:"), gbc);
        
        String[] roles = {"Conducteur", "Passager"};
        roleComboBox = new JComboBox<>(roles);
        gbc.gridx = 1;
        mainPanel.add(roleComboBox, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("S'inscrire");
        JButton cancelButton = new JButton("Annuler");
        
        registerButton.addActionListener(e -> handleRegistration());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void handleRegistration() {
        try {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            int age = Integer.parseInt(ageField.getText());
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = ((String) roleComboBox.getSelectedItem()).equals("Conducteur") ? "DRIVER" : "PASSENGER";
            
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Hash the password using BCrypt before creating the user
            String hashedPassword = SecurityUtils.hashPassword(password);
            User newUser = new User(nom, prenom, age, email, hashedPassword, role);
            
            if (userService.registerUser(newUser)) {
                JOptionPane.showMessageDialog(this, "Inscription réussie !");
                dispose();
                // Note: We cannot create LoginFrame here without circular dependency
                // This will be handled in the main application
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "L'âge doit être un nombre", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
} 