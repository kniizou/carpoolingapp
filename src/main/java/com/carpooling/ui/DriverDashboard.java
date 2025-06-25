package com.carpooling.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.carpooling.model.Trip;
import com.carpooling.model.User;
import com.carpooling.service.IAuthService;  // Temporary for transition
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.ui.validation.UIValidationHelper;
import com.carpooling.websocket.NotificationWebSocketClient;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class DriverDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DriverDashboard.class.getName());
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Color PRIMARY_COLOR = new Color(128, 0, 0);
    private static final Color HEADER_BACKGROUND = new Color(70, 70, 70);
    private static final Color HEADER_FOREGROUND = new Color(94, 0, 0);
    private static final Color TABLE_GRID_COLOR = new Color(200, 200, 200);
    private static final Color TABLE_SELECTION_COLOR = new Color(230, 230, 230);
    
    // Services - for future full refactoring
    private final IAuthService authService;
    private final IUserService userService;
    private final ITripService tripService;
    
    private final User currentUser;
    private Trip selectedTrip;
    private NotificationWebSocketClient webSocketClient;
    private NotificationPanel notificationPanel;
    
    private JTable tripsTable;
    private DefaultTableModel tripsTableModel;
    private JTable pendingPassengersTable;
    private DefaultTableModel pendingPassengersTableModel;
    
    // Champs pour la création de trajet
    private JTextField departureField;
    private JTextField destinationField;
    private JTextField timeField;
    private JTextField seatsField;
    private JTextField priceField;
    private JComboBox<String> tripTypeComboBox;
    private DatePicker datePicker;
    private JPanel recurringDaysPanel;
    
    public DriverDashboard(IAuthService authService, IUserService userService, ITripService tripService) {
        this.authService = authService;
        this.userService = userService;
        this.tripService = tripService;
        this.currentUser = authService.getCurrentUser();
        
        setTitle("Tableau de bord conducteur");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        
        // Création du panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(128, 0, 0));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("Tableau de bord conducteur");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(MAIN_FONT);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(PRIMARY_COLOR);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.addActionListener(e -> {
            authService.logout();
            dispose();
            // Note: Creating LoginFrame would create circular dependency
            // This will be handled by application controller
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Create notification panel for driver
        notificationPanel = new NotificationPanel(currentUser);
        
        // Create top panel with header and notifications
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(notificationPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Contenu principal
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentSplitPane.setDividerLocation(0.4);
        contentSplitPane.setResizeWeight(0.5);
        contentSplitPane.setBackground(new Color(240, 240, 240));
        
        // Panneau de création de trajet
        JPanel createTripPanel = new JPanel(new BorderLayout());
        createTripPanel.setBackground(Color.WHITE);
        createTripPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Créer un nouveau trajet",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY_COLOR
            )
        ));
        
        // Formulaire de création
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Initialisation des champs
        departureField = new JTextField();
        departureField.setFont(MAIN_FONT);
        departureField.setPreferredSize(new Dimension(250, 30));
        UIValidationHelper.setupRequiredTextValidation(departureField, "Départ");
        
        destinationField = new JTextField();
        destinationField.setFont(MAIN_FONT);
        destinationField.setPreferredSize(new Dimension(250, 30));
        UIValidationHelper.setupRequiredTextValidation(destinationField, "Destination");
        
        timeField = new JTextField();
        timeField.setFont(MAIN_FONT);
        timeField.setPreferredSize(new Dimension(250, 30));
        UIValidationHelper.setupTimeValidation(timeField);
        
        seatsField = new JTextField();
        seatsField.setFont(MAIN_FONT);
        seatsField.setPreferredSize(new Dimension(250, 30));
        UIValidationHelper.setupNumericValidation(seatsField, 1, "Places disponibles");
        
        priceField = new JTextField();
        priceField.setFont(MAIN_FONT);
        priceField.setPreferredSize(new Dimension(250, 30));
        UIValidationHelper.setupPriceValidation(priceField);
        
        tripTypeComboBox = new JComboBox<>(new String[]{"Occasionnel", "École", "Travail", "Courses"});
        tripTypeComboBox.setFont(MAIN_FONT);
        tripTypeComboBox.setPreferredSize(new Dimension(250, 30));
        UIValidationHelper.setupComboBoxHighlighting(tripTypeComboBox);
        
        // DatePicker
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dateSettings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        dateSettings.setFontCalendarDateLabels(MAIN_FONT);
        dateSettings.setFontCalendarWeekdayLabels(MAIN_FONT);
        datePicker = new DatePicker(dateSettings);
        datePicker.setPreferredSize(new Dimension(250, 30));
        
        // Jours récurrents
        recurringDaysPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        recurringDaysPanel.setBackground(Color.WHITE);
        String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for (String day : days) {
            JCheckBox checkBox = new JCheckBox(day);
            checkBox.setFont(MAIN_FONT);
            checkBox.setBackground(Color.WHITE);
            recurringDaysPanel.add(checkBox);
        }
        JScrollPane recurringDaysScrollPane = new JScrollPane(recurringDaysPanel);
        recurringDaysScrollPane.setPreferredSize(new Dimension(250, 100));
        
        // Ajout des champs au formulaire
        addFormField(formPanel, gbc, "Départ:", departureField, MAIN_FONT);
        addFormField(formPanel, gbc, "Destination:", destinationField, MAIN_FONT);
        addFormField(formPanel, gbc, "Date:", datePicker, MAIN_FONT);
        addFormField(formPanel, gbc, "Heure (HH:mm):", timeField, MAIN_FONT);
        addFormField(formPanel, gbc, "Places disponibles:", seatsField, MAIN_FONT);
        addFormField(formPanel, gbc, "Prix:", priceField, MAIN_FONT);
        addFormField(formPanel, gbc, "Type de trajet:", tripTypeComboBox, MAIN_FONT);
        addFormField(formPanel, gbc, "Jours récurrents:", recurringDaysScrollPane, MAIN_FONT);
        
        // Écouteur pour le type de trajet
        tripTypeComboBox.addActionListener(e ->{
            String selectedType = (String) tripTypeComboBox.getSelectedItem();
            boolean isRecurring = !"Occasionnel".equals(selectedType);
            recurringDaysPanel.setVisible(isRecurring);
            datePicker.setEnabled(!isRecurring);
        });
        
        // Initialisation de l'état des composants
        recurringDaysPanel.setVisible(false);
        
        // Bouton de création
        JButton createButton = new JButton("Créer le trajet");
        createButton.setFont(HEADER_FONT);
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(128, 0, 0));
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setPreferredSize(new Dimension(150, 35));
        createButton.addActionListener(e ->handleCreateTrip());
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(createButton, gbc);
        
        createTripPanel.add(formPanel, BorderLayout.CENTER);
        contentSplitPane.setTopComponent(createTripPanel);
        
        // Panneau inférieur
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        bottomSplitPane.setDividerLocation(0.5);
        bottomSplitPane.setResizeWeight(0.5);
        
        // Panneau des trajets
        JPanel tripsPanel = new JPanel(new BorderLayout());
        tripsPanel.setBackground(Color.WHITE);
        tripsPanel.setMinimumSize(new Dimension(300, 300));
        tripsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Mes trajets",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY_COLOR
            )
        ));
        
        // Table des trajets
        String[] tripColumns = {"ID", "Départ", "Destination", "Date", "Heure", "Places", "Prix", "Type"};
        tripsTableModel = new DefaultTableModel(tripColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tripsTable = new JTable(tripsTableModel);
        tripsTable.setFont(MAIN_FONT);
        tripsTable.setRowHeight(30);
        tripsTable.getTableHeader().setFont(HEADER_FONT);
        tripsTable.getTableHeader().setBackground(HEADER_BACKGROUND);
        tripsTable.getTableHeader().setForeground(HEADER_FOREGROUND);
        tripsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tripsTable.setSelectionBackground(TABLE_SELECTION_COLOR);
        tripsTable.setGridColor(TABLE_GRID_COLOR);
        tripsTable.setShowGrid(true);
        tripsTable.setFillsViewportHeight(true);
        tripsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tripsTable.setPreferredScrollableViewportSize(new Dimension(750, 450));
        tripsTable.getSelectionModel().addListSelectionListener(e ->handleTripSelection());
        
        // Ajuster la largeur des colonnes pour le tableau des trajets
        tripsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tripsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Départ
        tripsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Destination
        tripsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        tripsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Heure
        tripsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Places
        tripsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Prix
        tripsTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Type

        JScrollPane tripsScrollPane = new JScrollPane(tripsTable);
        tripsScrollPane.setBorder(BorderFactory.createLineBorder(TABLE_GRID_COLOR));
        tripsScrollPane.setColumnHeaderView(tripsTable.getTableHeader());
        tripsScrollPane.setViewportView(tripsTable);
        tripsPanel.add(tripsScrollPane, BorderLayout.CENTER);
        
        // Boutons d'action des trajets
        JPanel tripsActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tripsActionPanel.setBackground(Color.WHITE);
        
        JButton refreshTripsButton = new JButton("Actualiser");
        refreshTripsButton.setFont(HEADER_FONT);
        refreshTripsButton.setForeground(Color.WHITE);
        refreshTripsButton.setBackground(new Color(128, 0, 0));
        refreshTripsButton.setFocusPainted(false);
        refreshTripsButton.setBorderPainted(false);
        refreshTripsButton.setPreferredSize(new Dimension(150, 35));
        refreshTripsButton.addActionListener(e ->refreshTripsTable());
        
        JButton deleteTripButton = new JButton("Supprimer");
        deleteTripButton.setFont(HEADER_FONT);
        deleteTripButton.setForeground(Color.WHITE);
        deleteTripButton.setBackground(new Color(128, 0, 0));
        deleteTripButton.setFocusPainted(false);
        deleteTripButton.setBorderPainted(false);
        deleteTripButton.setPreferredSize(new Dimension(150, 35));
        deleteTripButton.addActionListener(e ->handleDeleteTrip());

        JButton editTripButton = new JButton("Modifier");
        editTripButton.setFont(HEADER_FONT);
        editTripButton.setForeground(Color.WHITE);
        editTripButton.setBackground(new Color(128, 0, 0));
        editTripButton.setFocusPainted(false);
        editTripButton.setBorderPainted(false);
        editTripButton.setPreferredSize(new Dimension(150, 35));
        editTripButton.addActionListener(e ->handleEditTrip());

        tripsActionPanel.add(refreshTripsButton);
        tripsActionPanel.add(deleteTripButton);
        tripsActionPanel.add(editTripButton);
        
        tripsPanel.add(tripsActionPanel, BorderLayout.SOUTH);
        
        // Panneau des demandes de passagers
        JPanel passengerRequestsPanel = new JPanel(new BorderLayout());
        passengerRequestsPanel.setBackground(Color.WHITE);
        passengerRequestsPanel.setMinimumSize(new Dimension(300, 300));
        passengerRequestsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Demandes des passagers",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY_COLOR
            )
        ));

        // Table des demandes
        String[] columns = {"ID", "Passager", "Départ", "Destination", "Date", "Heure", "Statut"};
        pendingPassengersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingPassengersTable = new JTable(pendingPassengersTableModel);
        pendingPassengersTable.setFont(MAIN_FONT);
        pendingPassengersTable.setRowHeight(30);
        pendingPassengersTable.getTableHeader().setFont(HEADER_FONT);
        pendingPassengersTable.getTableHeader().setBackground(HEADER_BACKGROUND);
        pendingPassengersTable.getTableHeader().setForeground(HEADER_FOREGROUND);
        pendingPassengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingPassengersTable.setSelectionBackground(TABLE_SELECTION_COLOR);
        pendingPassengersTable.setGridColor(TABLE_GRID_COLOR);
        pendingPassengersTable.setShowGrid(true);
        pendingPassengersTable.setFillsViewportHeight(true);
        pendingPassengersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        pendingPassengersTable.setPreferredScrollableViewportSize(new Dimension(750, 450));

        // Ajuster la largeur des colonnes pour le tableau des demandes
        pendingPassengersTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        pendingPassengersTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Passager
        pendingPassengersTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Départ
        pendingPassengersTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Destination
        pendingPassengersTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Date
        pendingPassengersTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Heure
        pendingPassengersTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Statut

        JScrollPane scrollPane = new JScrollPane(pendingPassengersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(TABLE_GRID_COLOR));
        scrollPane.setColumnHeaderView(pendingPassengersTable.getTableHeader());
        scrollPane.setViewportView(pendingPassengersTable);
        passengerRequestsPanel.add(scrollPane, BorderLayout.CENTER);

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton acceptButton = new JButton("Accepter");
        acceptButton.setFont(HEADER_FONT);
        acceptButton.setForeground(Color.WHITE);
        acceptButton.setBackground(new Color(128, 0, 0));
        acceptButton.setFocusPainted(false);
        acceptButton.setBorderPainted(false);
        acceptButton.setPreferredSize(new Dimension(150, 35));
        acceptButton.addActionListener(e ->handleAcceptRequest());

        JButton rejectButton = new JButton("Refuser");
        rejectButton.setFont(HEADER_FONT);
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setBackground(new Color(128, 0, 0));
        rejectButton.setFocusPainted(false);
        rejectButton.setBorderPainted(false);
        rejectButton.setPreferredSize(new Dimension(150, 35));
        rejectButton.addActionListener(e ->handleRejectRequest());

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setFont(HEADER_FONT);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(128, 0, 0));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setPreferredSize(new Dimension(150, 35));
        refreshButton.addActionListener(e ->refreshPendingPassengersTable());

        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);

        passengerRequestsPanel.add(buttonPanel, BorderLayout.SOUTH);

        bottomSplitPane.setLeftComponent(tripsPanel);
        bottomSplitPane.setRightComponent(passengerRequestsPanel);
        
        contentSplitPane.setBottomComponent(bottomSplitPane);
        mainPanel.add(contentSplitPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Chargement initial des trajets
        refreshTripsTable();
        
        // Connect to WebSocket for real-time notifications
        webSocketClient = NotificationWebSocketClient.getInstance();
        webSocketClient.connectAndRegister(currentUser.getId());
        
        // Show toast notifications for any existing unread notifications
        javax.swing.SwingUtilities.invokeLater(() -> {
            ToastNotificationManager.getInstance().showToastsForUser(currentUser.getId());
        });

        // Forcer le rafraîchissement de l'interface utilisateur
        mainPanel.revalidate();
        mainPanel.repaint();
        tripsScrollPane.revalidate();
        tripsScrollPane.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
        
        // Setup real-time validation for time field
        setupTimeFieldValidation();
    }
    
    private void handleCreateTrip() {
        try {
            String departure = departureField.getText().trim();
            String destination = destinationField.getText().trim();
            String time = timeField.getText().trim();
            String seats = seatsField.getText().trim();
            String price = priceField.getText().trim();
            String type = (String) tripTypeComboBox.getSelectedItem();
            
            // Validation des champs
            if (departure.isEmpty() || destination.isEmpty() || time.isEmpty() || 
                seats.isEmpty() || price.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez remplir tous les champs obligatoires", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation du format de l'heure (HH:mm)
            if (!time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                JOptionPane.showMessageDialog(this, 
                    "Format d'heure invalide. Utilisez le format HH:mm", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation des nombres
            int seatsNum;
            double priceNum;
            try {
                seatsNum = Integer.parseInt(seats);
                priceNum = Double.parseDouble(price);
                if (seatsNum <= 0 || priceNum <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Le nombre de places et le prix doivent être des nombres positifs", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Récupération et formatage de la date
            LocalDate selectedDate = datePicker.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner une date", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Création du trajet
            Trip trip = new Trip(
                currentUser,
                departure,
                destination,
                formattedDate,
                time,
                seatsNum,
                priceNum,
                type,
                null
            );
            
            // Ajout des jours récurrents si nécessaire
            if (!"Occasionnel".equals(type)) {
                List<String> recurringDays = new ArrayList<>();
                for (Component comp : recurringDaysPanel.getComponents()) {
                    if (comp instanceof JCheckBox) {
                        JCheckBox checkBox = (JCheckBox) comp;
                        if (checkBox.isSelected()) {
                            recurringDays.add(checkBox.getText());
                        }
                    }
                }
                trip.setRecurringDays(recurringDays);
            }
            
            // Sauvegarde du trajet
            tripService.createTrip(trip);
            JOptionPane.showMessageDialog(this, 
                "Trajet créé avec succès", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Réinitialisation des champs
            clearTripFields();
            
            // Actualisation de la liste des trajets
            refreshTripsTable();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la création du trajet", e);
            JOptionPane.showMessageDialog(this, 
                "Une erreur est survenue lors de la création du trajet : " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleAcceptRequest() {
        int selectedRow = pendingPassengersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une demande");
            return;
        }
        
        String passengerEmail = (String) pendingPassengersTable.getValueAt(selectedRow, 1);
        User passenger = userService.getUserByEmail(passengerEmail);
        
        if (selectedTrip != null && passenger != null) {
            try {
                tripService.updatePassengerStatus(selectedTrip, passenger, "CONFIRMED");
                JOptionPane.showMessageDialog(this, "Demande acceptée avec succès");
                refreshPendingPassengersTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'acceptation de la demande: " + e.getMessage());
            }
        }
    }
    
    private void handleRejectRequest() {
        int selectedRow = pendingPassengersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une demande");
            return;
        }
        
        String passengerEmail = (String) pendingPassengersTable.getValueAt(selectedRow, 1);
        User passenger = userService.getUserByEmail(passengerEmail);
        
        if (selectedTrip != null && passenger != null) {
            try {
                tripService.updatePassengerStatus(selectedTrip, passenger, "REJECTED");
                JOptionPane.showMessageDialog(this, "Demande refusée avec succès");
                refreshPendingPassengersTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors du refus de la demande: " + e.getMessage());
            }
        }
    }
    
    private void handleDeleteTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet à supprimer");
            return;
        }
        
        String tripId = (String) tripsTable.getValueAt(selectedRow, 0);
        Trip trip = tripService.getTripById(tripId);
        
        if (trip != null) {
            if (tripService.deleteTrip(trip)) {
                JOptionPane.showMessageDialog(this, "Trajet supprimé avec succès");
                refreshTripsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du trajet");
            }
        }
    }
    
    private void handleEditTrip() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet à modifier");
            return;
        }

        String tripId = (String) tripsTable.getValueAt(selectedRow, 0);
        Trip trip = tripService.getTripById(tripId);
        
        if (trip != null) {
            // Créer une fenêtre de dialogue pour la modification
            JDialog editDialog = new JDialog(this, "Modifier le trajet", true);
            editDialog.setLayout(new BorderLayout());
            editDialog.setSize(400, 500);
            editDialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Champs de formulaire
            JTextField departureField = new JTextField(trip.getDeparture(), 20);
            JTextField destinationField = new JTextField(trip.getDestination(), 20);
            JTextField dateField = new JTextField(trip.getDate(), 20);
            JTextField timeField = new JTextField(trip.getTime(), 20);
            JTextField seatsField = new JTextField(String.valueOf(trip.getAvailableSeats()), 20);
            JTextField priceField = new JTextField(String.valueOf(trip.getPrice()), 20);
            JComboBox<String> tripTypeCombo = new JComboBox<>(new String[]{"Occasionnel", "Régulier (École)", "Régulier (Travail)", "Régulier (Autre)"});
            tripTypeCombo.setSelectedItem(trip.getTripType());

            // Ajout des composants au panneau
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Départ:"), gbc);
            gbc.gridx = 1;
            formPanel.add(departureField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Destination:"), gbc);
            gbc.gridx = 1;
            formPanel.add(destinationField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Date (dd/MM/yyyy):"), gbc);
            gbc.gridx = 1;
            formPanel.add(dateField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Heure (HH:mm):"), gbc);
            gbc.gridx = 1;
            formPanel.add(timeField, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("Places disponibles:"), gbc);
            gbc.gridx = 1;
            formPanel.add(seatsField, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            formPanel.add(new JLabel("Prix:"), gbc);
            gbc.gridx = 1;
            formPanel.add(priceField, gbc);

            gbc.gridx = 0; gbc.gridy = 6;
            formPanel.add(new JLabel("Type de trajet:"), gbc);
            gbc.gridx = 1;
            formPanel.add(tripTypeCombo, gbc);

            // Boutons
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");

            saveButton.addActionListener(e ->{
                try {
                    // Validation des champs
                    String departure = departureField.getText().trim();
                    String destination = destinationField.getText().trim();
                    String date = dateField.getText().trim();
                    String time = timeField.getText().trim();
                    int seats = Integer.parseInt(seatsField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    String type = (String) tripTypeCombo.getSelectedItem();

                    // Mise à jour du trajet
                    trip.setDeparture(departure);
                    trip.setDestination(destination);
                    trip.setDate(date);
                    trip.setTime(time);
                    trip.setAvailableSeats(seats);
                    trip.setPrice(price);
                    trip.setTripType(type);

                    // Sauvegarde dans la base de données
                    if (tripService.updateTrip(trip)) {
                        JOptionPane.showMessageDialog(editDialog, "Trajet modifié avec succès");
                        editDialog.dispose();
                        refreshTripsTable();
                    } else {
                        JOptionPane.showMessageDialog(editDialog, "Erreur lors de la modification du trajet");
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(editDialog, "Veuillez entrer des valeurs numériques valides pour les places et le prix");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "Erreur lors de la modification: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(evt -> editDialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            editDialog.add(formPanel, BorderLayout.CENTER);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);
            editDialog.setVisible(true);
        }
    }
    
    private void clearTripFields() {
        departureField.setText("");
        destinationField.setText("");
        datePicker.clear();
        timeField.setText("");
        seatsField.setText("");
        priceField.setText("");
        tripTypeComboBox.setSelectedIndex(0);
        for (Component comp : recurringDaysPanel.getComponents()) {
            if (comp instanceof JCheckBox) {
                ((JCheckBox) comp).setSelected(false);
            }
        }
    }
    
    private void refreshTripsTable() {
        tripsTableModel.setRowCount(0);
        List<Trip> trips = tripService.getAllTrips();
        
        for (Trip trip : trips) {
            if (trip.getDriver().getEmail().equals(currentUser.getEmail())) {
                tripsTableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime(),
                    trip.getAvailableSeats(),
                    String.format("%.2f €", trip.getPrice()),
                    trip.getTripType()
                });
            }
        }
    }
    
    private void handleTripSelection() {
        int selectedRow = tripsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String tripId = (String) tripsTable.getValueAt(selectedRow, 0);
            selectedTrip = tripService.getTripById(tripId);
            if (selectedTrip != null) {
                LOGGER.info("Trajet sélectionné: " + selectedTrip.getId());
                refreshPendingPassengersTable();
            }
        }
    }
    
    private void refreshPendingPassengersTable() {
        pendingPassengersTableModel.setRowCount(0);
        if (selectedTrip != null) {
            for (User passenger : selectedTrip.getPendingPassengers()) {
                pendingPassengersTableModel.addRow(new Object[]{
                    selectedTrip.getId(),
                    passenger.getName() + " (" + passenger.getEmail() + ")",
                    selectedTrip.getDeparture(),
                    selectedTrip.getDestination(),
                    selectedTrip.getDate(),
                    selectedTrip.getTime(),
                    "⏳ En attente"
                });
            }
        }
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, 
                            JComponent field, Font labelFont) {
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(new Color(51, 51, 51));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void setupTimeFieldValidation() {
        // Add real-time validation to time field
        timeField.setToolTipText("Format: HH:mm (ex: 14:30)");
        timeField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                validateTimeFieldFormat();
            }
        });
        
        // Add document listener for immediate feedback
        timeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateTimeFieldFormat(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateTimeFieldFormat(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateTimeFieldFormat(); }
        });
    }
    
    private void validateTimeFieldFormat() {
        String time = timeField.getText().trim();
        if (!time.isEmpty() && !time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            timeField.setBackground(new Color(255, 200, 200)); // Light red
            timeField.setToolTipText("Format invalide! Utilisez HH:mm (ex: 14:30)");
        } else {
            timeField.setBackground(Color.WHITE);
            timeField.setToolTipText("Format: HH:mm (ex: 14:30)");
        }
    }
}