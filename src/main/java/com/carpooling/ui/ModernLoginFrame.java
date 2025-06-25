package com.carpooling.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.carpooling.model.User;
import com.carpooling.service.IAuthService;
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.ui.presenter.LoginPresenter;
import com.carpooling.ui.view.ILoginView;

/**
 * Modern Login Frame implementing MVP pattern with clean UI design.
 */
public class ModernLoginFrame extends JFrame implements ILoginView {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private JLabel loadingLabel;
    
    private LoginPresenter presenter;
    
    public ModernLoginFrame(IAuthService authService, IUserService userService, ITripService tripService) {
        this.presenter = new LoginPresenter(this, authService, userService, tripService);
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setTitle("Covoiturage - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with theme background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Theme.BACKGROUND_COLOR);
        titlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Connexion", SwingConstants.CENTER);
        Theme.styleLabel(titleLabel, Theme.FONT_TITLE, Theme.TEXT_PRIMARY);
        titlePanel.add(titleLabel);
        
        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.SURFACE_COLOR);
        formPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Adresse email");
        Theme.styleLabel(emailLabel, Theme.FONT_BODY_BOLD, Theme.TEXT_PRIMARY);
        formPanel.add(emailLabel, gbc);
        
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        Theme.styleTextField(emailField);
        formPanel.add(emailField, gbc);
        
        // Password field
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Mot de passe");
        Theme.styleLabel(passwordLabel, Theme.FONT_BODY_BOLD, Theme.TEXT_PRIMARY);
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        Theme.styleTextField(passwordField);
        formPanel.add(passwordField, gbc);
        
        // Status/Loading label
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        statusLabel = new JLabel(" ");
        Theme.styleLabel(statusLabel, Theme.FONT_SMALL, Theme.ERROR_COLOR);
        formPanel.add(statusLabel, gbc);
        
        loadingLabel = new JLabel("Connexion en cours...");
        Theme.styleLabel(loadingLabel, Theme.FONT_SMALL, Theme.TEXT_SECONDARY);
        loadingLabel.setVisible(false);
        gbc.gridy = 5;
        formPanel.add(loadingLabel, gbc);
        
        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        loginButton = new JButton("Se connecter");
        Theme.stylePrimaryButton(loginButton);
        loginButton.setPreferredSize(new java.awt.Dimension(150, 40));
        
        registerButton = new JButton("Créer un compte");
        Theme.styleSecondaryButton(registerButton);
        registerButton.setPreferredSize(new java.awt.Dimension(150, 40));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(javax.swing.Box.createHorizontalStrut(15));
        buttonPanel.add(registerButton);
        
        return buttonPanel;
    }
    
    private void setupEventListeners() {
        loginButton.addActionListener(e -> presenter.onLoginClicked());
        registerButton.addActionListener(e -> presenter.onRegisterClicked());
        
        // Enable Enter key for login
        passwordField.addActionListener(e -> presenter.onLoginClicked());
        emailField.addActionListener(e -> passwordField.requestFocus());
    }
    
    // ILoginView implementation
    
    @Override
    public String getEmail() {
        return emailField.getText();
    }
    
    @Override
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    @Override
    public void clearPassword() {
        passwordField.setText("");
    }
    
    @Override
    public void showLoginError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Theme.ERROR_COLOR);
        statusLabel.setVisible(true);
        ToastNotificationManager.showError(message);
    }
    
    @Override
    public void showLoginSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Theme.SUCCESS_COLOR);
        statusLabel.setVisible(true);
        ToastNotificationManager.showSuccess(message);
    }
    
    @Override
    public void navigateToDashboard(User user) {
        switch (user.getRole()) {
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
    }
    
    @Override
    public void navigateToRegister() {
        RegisterFrame registerFrame = new RegisterFrame(
            presenter.getUserService(), 
            presenter.getAuthService()
        );
        registerFrame.setVisible(true);
    }
    
    @Override
    public void setLoading(boolean loading) {
        loadingLabel.setVisible(loading);
        statusLabel.setVisible(!loading);
    }
    
    @Override
    public void setFormEnabled(boolean enabled) {
        emailField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
    }
    
    public void showView() {
        setVisible(true);
        emailField.requestFocus();
    }
    
    @Override
    public void hide() {
        super.setVisible(false);
    }
    
    // Dashboard navigation methods
    private void openAdminDashboard() {
        AdminDashboard adminDashboard = new AdminDashboard(
            presenter.getAuthService(), 
            presenter.getUserService(), 
            presenter.getTripService()
        );
        adminDashboard.setVisible(true);
    }
    
    private void openDriverDashboard() {
        DriverDashboard driverDashboard = new DriverDashboard(
            presenter.getAuthService(), 
            presenter.getUserService(), 
            presenter.getTripService()
        );
        driverDashboard.setVisible(true);
    }
    
    private void openPassengerDashboard() {
        PassengerDashboard passengerDashboard = new PassengerDashboard(
            presenter.getAuthService(), 
            presenter.getUserService(), 
            presenter.getTripService()
        );
        passengerDashboard.setVisible(true);
    }
}
