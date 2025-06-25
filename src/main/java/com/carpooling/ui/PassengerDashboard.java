package com.carpooling.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;
import com.carpooling.model.Trip;
import com.carpooling.model.User;
import com.carpooling.service.IAuthService;
import com.carpooling.service.INotificationService;
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.util.ValidationUtils;
import com.carpooling.websocket.NotificationWebSocketClient;

public class PassengerDashboard extends JFrame {
    // Services - for future full refactoring
    private final IAuthService authService;
    private final IUserService userService;
    private final ITripService tripService;
    
    private final User currentUser;
    private final JTabbedPane tabbedPane;
    private JTable searchResultsTable;
    private JTable myTripsTable;
    private JTable offersTable;
    private DefaultTableModel myTripsTableModel;
    private JTextField departureField;
    private JTextField destinationField;
    private JTextField dateField;
    private JTextField maxPriceField;
    private JTextField offerDepartureField;
    private JTextField offerDestinationField;
    private JTextField offerDateField;
    private JTextField offerTimeField;
    private JTextField offerPriceField;
    private DefaultTableModel searchResultsTableModel;
    private DefaultTableModel offersTableModel;
    private NotificationPanel notificationPanel;
    private NotificationWebSocketClient webSocketClient;
    private static final Logger LOGGER = Logger.getLogger(PassengerDashboard.class.getName());

    public PassengerDashboard(IAuthService authService, IUserService userService, ITripService tripService) {
        this.authService = authService;
        this.userService = userService;
        this.tripService = tripService;
        this.currentUser = authService.getCurrentUser();
        
        setTitle("Dashboard Passager");
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
        
        JLabel titleLabel = new JLabel("Dashboard Passager - " + currentUser.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(primaryColor);
        logoutButton.addActionListener(e -> handleLogout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Create notification panel and add it below the header
        notificationPanel = new NotificationPanel(currentUser);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(notificationPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(backgroundColor);
        
        // Onglet Recherche de Trajets
        JPanel searchTripsPanel = createSearchTripsPanel();
        tabbedPane.addTab("Rechercher un Trajet", searchTripsPanel);
        
        // Onglet Demander un Trajet (Request a Trip)
        JPanel requestTripPanel = createRequestTripPanel();
        tabbedPane.addTab("Demander un Trajet", requestTripPanel);
        
        // Onglet Mes Trajets
        JPanel myTripsPanel = createMyTripsPanel();
        tabbedPane.addTab("Mes Trajets", myTripsPanel);
        
        // Onglet Mes Offres
        JPanel offersPanel = createOfferPanel();
        tabbedPane.addTab("Mes Offres", offersPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Charger les données
        refreshMyTripsTable();
        refreshOffersTable();
        
        // Show toast notifications for any existing unread notifications
        SwingUtilities.invokeLater(() -> {
            ToastNotificationManager.getInstance().showToastsForUser(currentUser.getId());
        });
        
        // Connect to WebSocket for real-time notifications
        webSocketClient = NotificationWebSocketClient.getInstance();
        webSocketClient.connectAndRegister(currentUser.getId());
    }

    private JPanel createSearchTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Panel de recherche
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs de recherche
        departureField = new JTextField(20);
        destinationField = new JTextField(20);
        dateField = new JTextField(20);
        maxPriceField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Départ:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(departureField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(destinationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        searchPanel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        searchPanel.add(new JLabel("Prix maximum:"), gbc);
        gbc.gridx = 1;
        searchPanel.add(maxPriceField, gbc);
        
        // Bouton de recherche
        JButton searchButton = new JButton("Rechercher");
        searchButton.addActionListener(e -> handleSearch());
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        searchPanel.add(searchButton, gbc);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table des résultats
        String[] columnNames = {"ID", "Conducteur", "Départ", "Destination", "Date", "Heure", "Places", "Prix", "Type"};
        searchResultsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        searchResultsTable = new JTable(searchResultsTableModel);
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bouton de demande
        JButton requestButton = new JButton("Demander une place");
        requestButton.addActionListener(e -> handleRequestTrip());
        panel.add(requestButton, BorderLayout.SOUTH);
        
        // Auto-refresh functionality
        enableAutoRefresh();
        
        return panel;
    }

    private JPanel createRequestTripPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(new JLabel("<html><h3>Demander un Trajet</h3>" +
            "<p>Vous ne trouvez pas le trajet parfait dans les recherches ?</p>" +
            "<p>Créez une demande de trajet pour que les conducteurs puissent vous proposer leurs services.</p></html>"));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Request form fields
        JTextField requestDepartureField = new JTextField(20);
        JTextField requestDestinationField = new JTextField(20);
        JTextField requestDateField = new JTextField(20);
        JTextField requestTimeField = new JTextField(20);
        JTextField maxBudgetField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Départ souhaité:"), gbc);
        gbc.gridx = 1;
        formPanel.add(requestDepartureField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination souhaitée:"), gbc);
        gbc.gridx = 1;
        formPanel.add(requestDestinationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date souhaitée (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(requestDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Heure souhaitée:"), gbc);
        gbc.gridx = 1;
        formPanel.add(requestTimeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Budget maximum:"), gbc);
        gbc.gridx = 1;
        formPanel.add(maxBudgetField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        
        JButton createRequestButton = new JButton("Publier la Demande");
        createRequestButton.addActionListener(e -> handleCreateTripRequest(
            requestDepartureField, requestDestinationField, requestDateField, 
            requestTimeField, maxBudgetField));
        
        JButton clearButton = new JButton("Effacer");
        clearButton.addActionListener(e -> {
            requestDepartureField.setText("");
            requestDestinationField.setText("");
            requestDateField.setText("");
            requestTimeField.setText("");
            maxBudgetField.setText("");
        });
        
        buttonPanel.add(createRequestButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Add time field validation for passenger request
        setupTimeFieldValidation(requestTimeField);
        
        return panel;
    }

    private JPanel createMyTripsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Table des trajets
        String[] columnNames = {"ID", "Conducteur", "Départ", "Destination", "Date", "Heure", "Statut"};
        myTripsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myTripsTable = new JTable(myTripsTableModel);
        JScrollPane scrollPane = new JScrollPane(myTripsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bouton d'actualisation
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> refreshMyTripsTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createOfferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Table des offres
        String[] columnNames = {"ID", "Départ", "Destination", "Date", "Heure", "Places", "Prix", "Statut"};
        offersTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        offersTable = new JTable(offersTableModel);
        JScrollPane scrollPane = new JScrollPane(offersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bouton d'actualisation
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> refreshOffersTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private void refreshMyTripsTable() {
        myTripsTableModel.setRowCount(0);
        List<Trip> trips = tripService.getAllTrips();
        
        for (Trip trip : trips) {
            for (User passenger : trip.getPassengers()) {
                if (passenger.equals(currentUser)) {
                    myTripsTableModel.addRow(new Object[]{
                        trip.getId(),
                        trip.getDriver().getName(),
                        trip.getDeparture(),
                        trip.getDestination(),
                        trip.getDate(),
                        trip.getTime(),
                        "Accepté"
                    });
                }
            }
            for (User passenger : trip.getPendingPassengers()) {
                if (passenger.equals(currentUser)) {
                    myTripsTableModel.addRow(new Object[]{
                        trip.getId(),
                        trip.getDriver().getName(),
                        trip.getDeparture(),
                        trip.getDestination(),
                        trip.getDate(),
                        trip.getTime(),
                        "En attente"
                    });
                }
            }
        }
    }

    private void refreshOffersTable() {
        offersTableModel.setRowCount(0);
        List<Trip> trips = tripService.getAllTrips();
        
        for (Trip trip : trips) {
            if (trip.getDriver().getId().equals(currentUser.getId())) {
                offersTableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime(),
                    trip.getAvailableSeats(),
                    trip.getPrice(),
                    "Active"
                });
            }
        }
    }

    private void handleSearch() {
        searchResultsTableModel.setRowCount(0);
        
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = dateField.getText().trim();
        String maxPriceText = maxPriceField.getText().trim();
        
        List<Trip> allTrips = tripService.getAllTrips();
        
        for (Trip trip : allTrips) {
            boolean matches = true;
            
            if (!departure.isEmpty() && !trip.getDeparture().toLowerCase().contains(departure.toLowerCase())) {
                matches = false;
            }
            if (!destination.isEmpty() && !trip.getDestination().toLowerCase().contains(destination.toLowerCase())) {
                matches = false;
            }
            if (!date.isEmpty() && !trip.getDate().equals(date)) {
                matches = false;
            }
            if (!maxPriceText.isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(maxPriceText);
                    if (trip.getPrice() > maxPrice) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid price
                }
            }
            
            if (matches && trip.getAvailableSeats() > 0) {
                searchResultsTableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getDriver().getName(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime(),
                    trip.getAvailableSeats(),
                    trip.getPrice(),
                    trip.getTripType()
                });
            }
        }
        
        // Enable auto-refresh after first search
        if (isSearchActive() && !autoRefreshEnabled) {
            enableAutoRefresh();
        }
    }

    private void handleRequestTrip() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tripId = (String) searchResultsTable.getValueAt(selectedRow, 0);
        LOGGER.info(() -> "Demande de place pour le trajet ID: " + tripId);
        
        if (tripId == null || tripId.trim().isEmpty()) {
            LOGGER.severe("L'ID du trajet est null ou vide");
            JOptionPane.showMessageDialog(this, "ID de trajet invalide", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Trip trip = tripService.getTripById(tripId);
        
        if (trip == null) {
            LOGGER.severe(() -> "Trajet non trouvé avec l'ID: " + tripId);
            JOptionPane.showMessageDialog(this, "Trajet non trouvé", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LOGGER.info(() -> "Trajet trouvé - ID: " + trip.getId() + 
                   ", Départ: " + trip.getDeparture() + 
                   ", Destination: " + trip.getDestination());

        try {
            // Vérifier si le passager n'a pas déjà demandé ou est déjà accepté
            boolean isAlreadyRequested = false;
            boolean isAlreadyAccepted = false;
            
            for (User passenger : trip.getPendingPassengers()) {
                if (passenger.getId().equals(currentUser.getId())) {
                    isAlreadyRequested = true;
                    break;
                }
            }
            
            for (User passenger : trip.getPassengers()) {
                if (passenger.getId().equals(currentUser.getId())) {
                    isAlreadyAccepted = true;
                    break;
                }
            }
            
            if (isAlreadyRequested) {
                LOGGER.info("L'utilisateur a déjà demandé une place pour ce trajet");
                JOptionPane.showMessageDialog(this, "Vous avez déjà demandé une place pour ce trajet", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (isAlreadyAccepted) {
                LOGGER.info("L'utilisateur est déjà accepté pour ce trajet");
                JOptionPane.showMessageDialog(this, "Vous êtes déjà accepté pour ce trajet", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            LOGGER.info(() -> "Ajout de la demande de place pour l'utilisateur: " + currentUser.getEmail());
            
            // Ajouter le passager à la liste des demandes en attente
            trip.addPassenger(currentUser);
            tripService.addPassengerToTrip(trip, currentUser, "PENDING");
            
            // Add a notification for the driver
            NotificationManager.getInstance().addNotification(
                trip.getDriver().getId(),
                Notification.createInfoNotification(
                    "Nouvelle demande de trajet: " + currentUser.getName() + 
                    " souhaite rejoindre votre trajet de " + trip.getDeparture() + 
                    " à " + trip.getDestination() + " le " + trip.getDate(),
                    trip.getId()
                )
            );
            
            // Send WebSocket notification to the driver for real-time notification
            if (webSocketClient != null && webSocketClient.isOpen()) {
                webSocketClient.sendTripRequest(
                    currentUser.getId(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate()
                );
            }
            
            // Add a confirmation notification for the current user
            NotificationManager.getInstance().addNotification(
                currentUser.getId(),
                Notification.createSuccessNotification(
                    "Vous avez demandé à rejoindre le trajet de " + 
                    trip.getDeparture() + " à " + trip.getDestination() + 
                    " le " + trip.getDate() + ". Vous serez notifié quand le conducteur aura répondu.",
                    trip.getId()
                )
            );
            
            // Update the notification count in the UI
            notificationPanel.updateNotificationCount();
            
            JOptionPane.showMessageDialog(this, "Votre demande a été envoyée au conducteur", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            // Rafraîchir les tableaux
            refreshMyTripsTable();
            handleSearch();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la demande de place", e);
            JOptionPane.showMessageDialog(this, "Erreur lors de la demande de place : " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateTripRequest(JTextField departureField, JTextField destinationField, 
                                       JTextField dateField, JTextField timeField, JTextField budgetField) {
        try {
            String departure = departureField.getText().trim();
            String destination = destinationField.getText().trim();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String budgetText = budgetField.getText().trim();
            
            if (departure.isEmpty() || destination.isEmpty() || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir au moins le départ, la destination et la date", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double budget = 0;
            if (!budgetText.isEmpty()) {
                budget = Double.parseDouble(budgetText);
            }
            
            // Create a trip request (as a special trip type)
            Trip tripRequest = new Trip(currentUser, departure, destination, date, time, 1, budget, "DEMANDE", "");
            tripService.createTrip(tripRequest);
            
            // Notification de succès
            NotificationManager.getInstance().addNotification(
                currentUser.getId(),
                Notification.createSuccessNotification(
                    "Votre demande de trajet de " + departure + " à " + destination + " a été publiée avec succès",
                    tripRequest.getId()
                )
            );
            
            JOptionPane.showMessageDialog(this, "Demande de trajet publiée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            // Effacer les champs
            departureField.setText("");
            destinationField.setText("");
            dateField.setText("");
            timeField.setText("");
            budgetField.setText("");
            
            // Actualiser la notification
            notificationPanel.updateNotificationCount();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Budget invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création de la demande", e);
            JOptionPane.showMessageDialog(this, "Erreur lors de la création de la demande: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogout() {
        authService.logout();
        dispose();
        // Note: Creating LoginFrame would create circular dependency
        // This will be handled by application controller
    }
    
    // Auto-refresh functionality
    private Timer searchRefreshTimer;
    private boolean autoRefreshEnabled = false;
    private final int REFRESH_INTERVAL_MS = 30000; // 30 seconds
    
    /**
     * Enable auto-refresh for search results
     */
    private void enableAutoRefresh() {
        if (searchRefreshTimer != null) {
            searchRefreshTimer.stop();
        }
        
        searchRefreshTimer = new Timer(REFRESH_INTERVAL_MS, e -> {
            if (autoRefreshEnabled && isSearchActive()) {
                SwingUtilities.invokeLater(() -> {
                    refreshSearchResults();
                });
            }
        });
        
        autoRefreshEnabled = true;
        searchRefreshTimer.start();
    }
    
    /**
     * Disable auto-refresh
     */
    private void disableAutoRefresh() {
        autoRefreshEnabled = false;
        if (searchRefreshTimer != null) {
            searchRefreshTimer.stop();
        }
    }
    
    /**
     * Check if search is currently active (has search criteria)
     */
    private boolean isSearchActive() {
        return !departureField.getText().trim().isEmpty() || 
               !destinationField.getText().trim().isEmpty() || 
               !dateField.getText().trim().isEmpty() || 
               !maxPriceField.getText().trim().isEmpty();
    }
    
    /**
     * Refresh search results with current criteria
     */
    private void refreshSearchResults() {
        if (isSearchActive()) {
            // Store current selection
            int selectedRow = searchResultsTable.getSelectedRow();
            String selectedTripId = null;
            if (selectedRow >= 0) {
                selectedTripId = (String) searchResultsTableModel.getValueAt(selectedRow, 0);
            }
            
            // Refresh results
            handleSearch();
            
            // Restore selection if possible
            if (selectedTripId != null) {
                for (int i = 0; i < searchResultsTableModel.getRowCount(); i++) {
                    if (selectedTripId.equals(searchResultsTableModel.getValueAt(i, 0))) {
                        searchResultsTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
            
            // Add visual feedback
            searchResultsTable.repaint();
        }
    }
    
    /**
     * Setup real-time validation for time input field
     */
    private void setupTimeFieldValidation(JTextField timeField) {
        timeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                validateTimeFieldFormat(timeField);
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                validateTimeFieldFormat(timeField);
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                validateTimeFieldFormat(timeField);
            }
        });
        
        // Set placeholder/hint text
        timeField.setToolTipText("Format: HH:MM (exemple: 14:30)");
    }
    
    /**
     * Validate time field format and provide visual feedback
     */
    private void validateTimeFieldFormat(JTextField timeField) {
        String input = timeField.getText().trim();
        
        if (input.isEmpty()) {
            // Reset to normal appearance for empty field
            timeField.setBackground(Color.WHITE);
            timeField.setToolTipText("Format: HH:MM (exemple: 14:30)");
            return;
        }
        
        // Check time format using ValidationUtils
        try {
            if (ValidationUtils.isValidTimeFormat(input)) {
                timeField.setBackground(Color.WHITE);
                timeField.setToolTipText("Format valide");
            } else {
                timeField.setBackground(new Color(255, 200, 200)); // Light red
                timeField.setToolTipText("Format invalide. Utilisez HH:MM (exemple: 14:30)");
            }
        } catch (Exception e) {
            timeField.setBackground(new Color(255, 200, 200)); // Light red
            timeField.setToolTipText("Format invalide. Utilisez HH:MM (exemple: 14:30)");
        }
    }
}
