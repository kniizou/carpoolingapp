package com.carpooling.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.carpooling.service.IAuthService;
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.ui.components.PanelCover;
import com.carpooling.ui.components.PanelLoginAndRegister;

/**
 * Modern login frame with sliding panels and gradient background
 */
public class ModernAuthFrame extends JFrame {
    
    private PanelCover cover;
    private PanelLoginAndRegister loginAndRegister;
    private boolean isLogin = true;
    
    public ModernAuthFrame(IAuthService authService, IUserService userService, ITripService tripService) {
        initializeComponents(authService, userService, tripService);
        setupUI();
        setupEventListeners();
    }
    
    private void initializeComponents(IAuthService authService, IUserService userService, ITripService tripService) {
        cover = new PanelCover();
        loginAndRegister = new PanelLoginAndRegister(authService, userService, tripService);
    }
    
    private void setupUI() {
        setTitle("Covoiturage - Authentification");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(128, 15, 28));
        headerPanel.setPreferredSize(new Dimension(900, 60));
        
        JLabel titleLabel = new JLabel("Application de Covoiturage");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Split panel for cover and login/register
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(cover, BorderLayout.WEST);
        contentPanel.add(loginAndRegister, BorderLayout.CENTER);
        
        // Set preferred sizes
        cover.setPreferredSize(new Dimension(400, 540));
        loginAndRegister.setPreferredSize(new Dimension(500, 540));
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLogin) {
                    // Switch to register
                    loginAndRegister.showRegister(true);
                    cover.registerLeft(true);
                } else {
                    // Switch to login
                    loginAndRegister.showRegister(false);
                    cover.registerLeft(false);
                }
                isLogin = !isLogin;
            }
        });
    }
    
    public void showView() {
        setVisible(true);
    }
}
