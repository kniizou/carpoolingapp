package com.carpooling.ui;

import com.carpooling.data.DataManager;
import com.carpooling.model.User;
import com.carpooling.model.Trip;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private DataManager dataManager;
    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private JTable tripsTable;
    private DefaultTableModel usersTableModel;
    private DefaultTableModel tripsTableModel;

    public AdminDashboard() {
        dataManager = DataManager.getInstance();
        
        setTitle("Dashboard Administrateur");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Configuration des couleurs
        Color primaryColor = new Color(94, 0, 0); // #5e0000
        Color backgroundColor = Color.WHITE;
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(800, 50));
        
        JLabel titleLabel = new JLabel("Dashboard Administrateur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(primaryColor);
        logoutButton.addActionListener(_ -> handleLogout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(backgroundColor);
        
        // Onglet Utilisateurs
        JPanel usersPanel = createUsersPanel();
        tabbedPane.addTab("Utilisateurs", usersPanel);
        
        // Onglet Trajets
        JPanel tripsPanel = createTripsPanel();
        tabbedPane.addTab("Trajets", tripsPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Charger les données
        refreshUsersTable();
        refreshTripsTable();
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Table des utilisateurs
        String[] columns = {"Nom", "Email", "Rôle"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(_ -> refreshUsersTable());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Table des trajets
        String[] columns = {"ID", "Conducteur", "Départ", "Destination", "Date", "Heure", "Places", "Prix"};
        tripsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tripsTable = new JTable(tripsTableModel);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tripsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(_ -> refreshTripsTable());
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void refreshUsersTable() {
        usersTableModel.setRowCount(0);
        List<User> users = dataManager.getAllUsers();
        
        for (User user : users) {
            Object[] row = {
                user.getName(),
                user.getEmail(),
                user.getRole()
            };
            usersTableModel.addRow(row);
        }
    }
    
    private void refreshTripsTable() {
        tripsTableModel.setRowCount(0);
        List<Trip> trips = dataManager.getAllTrips();
        
        for (Trip trip : trips) {
            Object[] row = {
                trip.getId(),
                trip.getDriver().getName(),
                trip.getDeparture(),
                trip.getDestination(),
                trip.getDate(),
                trip.getTime(),
                trip.getAvailableSeats(),
                trip.getPrice()
            };
            tripsTableModel.addRow(row);
        }
    }
    
    private void handleLogout() {
        dataManager.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }
} 