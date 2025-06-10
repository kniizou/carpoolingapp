package com.carpooling.ui;

import com.carpooling.data.DataManager;
import com.carpooling.model.User;
import com.carpooling.model.Trip;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PassengerDashboard extends JFrame {
    private DataManager dataManager;
    private User currentUser;
    private JTabbedPane tabbedPane;
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
    private static final Logger LOGGER = Logger.getLogger(PassengerDashboard.class.getName());

    public PassengerDashboard() {
        dataManager = DataManager.getInstance();
        currentUser = dataManager.getCurrentUser();
        
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
        logoutButton.addActionListener(_ -> handleLogout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(backgroundColor);
        
        // Onglet Recherche de Trajets
        JPanel searchTripsPanel = createSearchTripsPanel();
        tabbedPane.addTab("Rechercher un Trajet", searchTripsPanel);
        
        // Onglet Créer un Trajet
        JPanel createTripPanel = createCreateTripPanel();
        tabbedPane.addTab("Créer un Trajet", createTripPanel);
        
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
        searchButton.addActionListener(_ -> handleSearch());
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
        requestButton.addActionListener(_ -> handleRequestTrip());
        panel.add(requestButton, BorderLayout.SOUTH);
        
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
        refreshButton.addActionListener(_ -> refreshMyTripsTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void refreshMyTripsTable() {
        myTripsTableModel.setRowCount(0);
        List<Trip> trips = dataManager.getAllTrips();
        
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
    
    private void handleSearch() {
        String departure = departureField.getText().trim();
        String destination = destinationField.getText().trim();
        String date = dateField.getText().trim();
        String maxPriceStr = maxPriceField.getText().trim();
        
        double maxPrice = maxPriceStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceStr);
        
        searchResultsTableModel.setRowCount(0);
        List<Trip> trips = dataManager.getAllTrips();
        
        System.out.println("Nombre total de trajets récupérés : " + trips.size());
        System.out.println("Utilisateur actuel : " + currentUser.getEmail() + " (Rôle : " + currentUser.getRole() + ", ID : " + currentUser.getId() + ")");
        
        for (Trip trip : trips) {
            // Vérifier que le trajet n'est pas déjà demandé ou accepté par le passager
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
            
            System.out.println("Analyse du trajet :");
            System.out.println("- ID : " + trip.getId());
            System.out.println("- Conducteur : " + trip.getDriver().getEmail() + " (Rôle : " + trip.getDriver().getRole() + ", ID : " + trip.getDriver().getId() + ")");
            System.out.println("- Places disponibles : " + trip.getAvailableSeats());
            System.out.println("- Déjà demandé : " + isAlreadyRequested);
            System.out.println("- Déjà accepté : " + isAlreadyAccepted);
            
            // Vérifier si le trajet correspond aux critères de recherche
            boolean matchesSearchCriteria = true;
            
            // Si au moins un critère de recherche est spécifié, vérifier les critères
            if (!departure.isEmpty() || !destination.isEmpty() || !date.isEmpty() || !maxPriceStr.isEmpty()) {
                matchesSearchCriteria = (departure.isEmpty() || trip.getDeparture().toLowerCase().contains(departure.toLowerCase())) &&
                                      (destination.isEmpty() || trip.getDestination().toLowerCase().contains(destination.toLowerCase())) &&
                                      (date.isEmpty() || trip.getDate().equals(date)) &&
                                      trip.getPrice() <= maxPrice;
            }
            
            if (!trip.getDriver().getId().equals(currentUser.getId()) && // Ne pas afficher ses propres trajets
                trip.getAvailableSeats() > 0 &&
                !isAlreadyRequested &&
                !isAlreadyAccepted &&
                matchesSearchCriteria) {
                
                System.out.println("Trajet ajouté à la liste des résultats");
                
                searchResultsTableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getDriver().getName(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime(),
                    trip.getAvailableSeats(),
                    trip.getPrice() + "€",
                    trip.getTripType()
                });
            }
        }
        
        System.out.println("Nombre de trajets affichés : " + searchResultsTableModel.getRowCount());
        
        if (searchResultsTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Aucun trajet ne correspond à vos critères de recherche",
                "Aucun résultat",
                JOptionPane.INFORMATION_MESSAGE);
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
        LOGGER.info("Demande de place pour le trajet ID: " + tripId);
        
        // Vérifier que l'ID n'est pas null ou vide
        if (tripId == null || tripId.trim().isEmpty()) {
            LOGGER.severe("L'ID du trajet est null ou vide");
            JOptionPane.showMessageDialog(this, "ID de trajet invalide", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Trip trip = dataManager.getTripById(tripId);
        
        if (trip == null) {
            LOGGER.severe("Trajet non trouvé avec l'ID: " + tripId);
            JOptionPane.showMessageDialog(this, "Trajet non trouvé", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LOGGER.info("Trajet trouvé - ID: " + trip.getId() + 
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
            
            LOGGER.info("Ajout de la demande de place pour l'utilisateur: " + currentUser.getEmail());
            
            // Ajouter le passager à la liste des demandes en attente
            trip.addPassenger(currentUser);
            dataManager.updateTripPassengerStatus(trip, currentUser, "PENDING");
            
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
    
    private void handleLogout() {
        dataManager.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }

    private JPanel createCreateTripPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Champs du formulaire
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Départ:"), gbc);
        
        JTextField departureField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(departureField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Destination:"), gbc);
        
        JTextField destinationField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(destinationField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        
        JSpinner.DateEditor dateEditor;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        formPanel.add(dateSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Heure:"), gbc);
        
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        gbc.gridx = 1;
        formPanel.add(timeSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Prix maximum:"), gbc);
        
        SpinnerNumberModel priceModel = new SpinnerNumberModel(5.0, 0.0, 1000.0, 0.5);
        JSpinner priceSpinner = new JSpinner(priceModel);
        gbc.gridx = 1;
        formPanel.add(priceSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Type de trajet:"), gbc);
        
        String[] tripTypes = {"Occasionnel", "Régulier (École)", "Régulier (Travail)", "Régulier (Autre)"};
        JComboBox<String> tripTypeCombo = new JComboBox<>(tripTypes);
        gbc.gridx = 1;
        formPanel.add(tripTypeCombo, gbc);
        
        // Panel pour les jours de la semaine (visible uniquement pour les trajets réguliers)
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        daysPanel.setBackground(Color.WHITE);
        JCheckBox[] dayCheckboxes = new JCheckBox[7];
        String[] days = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            dayCheckboxes[i] = new JCheckBox(days[i]);
            daysPanel.add(dayCheckboxes[i]);
        }
        daysPanel.setVisible(false);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(daysPanel, gbc);
        
        // Écouteur pour afficher/masquer les jours de la semaine
        tripTypeCombo.addActionListener(_ -> {
            String selectedType = (String) tripTypeCombo.getSelectedItem();
            daysPanel.setVisible(selectedType != null && selectedType.startsWith("Régulier"));
        });
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        
        JButton createButton = new JButton("Créer le trajet");
        createButton.addActionListener(_ -> {
            String departure = departureField.getText();
            String destination = destinationField.getText();
            Date date = (Date) dateSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();
            double maxPrice = (Double) priceSpinner.getValue();
            String tripType = (String) tripTypeCombo.getSelectedItem();
            
            if (departure.isEmpty() || destination.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Formater la date et l'heure
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStr = dateFormat.format(date);
            String timeStr = timeFormat.format(time);
            
            // Créer le trajet
            Trip newTrip = new Trip(currentUser, departure, destination, dateStr, timeStr, 1, maxPrice);
            newTrip.setTripType(tripType);
            
            // Si c'est un trajet régulier, ajouter les jours sélectionnés
            if (tripType.startsWith("Régulier")) {
                List<String> selectedDays = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    if (dayCheckboxes[i].isSelected()) {
                        selectedDays.add(days[i]);
                    }
                }
                newTrip.setRecurringDays(selectedDays);
            }
            
            try {
                dataManager.createTrip(newTrip);
                JOptionPane.showMessageDialog(this, "Trajet créé avec succès");
                refreshMyTripsTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Impossible de créer le trajet : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(createButton);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createOfferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Panel du formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs du formulaire
        offerDepartureField = new JTextField(20);
        offerDestinationField = new JTextField(20);
        offerDateField = new JTextField(20);
        offerTimeField = new JTextField(20);
        offerPriceField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Départ:"), gbc);
        gbc.gridx = 1;
        formPanel.add(offerDepartureField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        formPanel.add(offerDestinationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(offerDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Heure (HH:mm):"), gbc);
        gbc.gridx = 1;
        formPanel.add(offerTimeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Prix maximum:"), gbc);
        gbc.gridx = 1;
        formPanel.add(offerPriceField, gbc);
        
        // Bouton de création
        JButton createButton = new JButton("Créer l'offre");
        createButton.addActionListener(_ -> handleCreateTrip());
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(createButton, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table des offres
        String[] columnNames = {"ID", "Départ", "Destination", "Date", "Heure", "Prix", "Statut"};
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
        refreshButton.addActionListener(_ -> refreshOffersTable());
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void handleCreateTrip() {
        try {
            String departure = offerDepartureField.getText().trim();
            String destination = offerDestinationField.getText().trim();
            String date = offerDateField.getText().trim();
            String time = offerTimeField.getText().trim();
            double price = Double.parseDouble(offerPriceField.getText().trim());
            
            if (departure.isEmpty() || destination.isEmpty() || date.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs");
                return;
            }
            
            Trip trip = new Trip(currentUser, departure, destination, date, time, 1, price);
            dataManager.createTrip(trip);
            
            JOptionPane.showMessageDialog(this, "Trajet créé avec succès");
            clearOfferFields();
            refreshOffersTable();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un prix valide");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création du trajet: " + e.getMessage());
        }
    }
    
    private void refreshOffersTable() {
        offersTableModel.setRowCount(0);
        List<Trip> trips = dataManager.getAllTrips();
        
        for (Trip trip : trips) {
            if (trip.getDriver().equals(currentUser)) {
                offersTableModel.addRow(new Object[]{
                    trip.getId(),
                    trip.getDeparture(),
                    trip.getDestination(),
                    trip.getDate(),
                    trip.getTime(),
                    trip.getPrice() + "€",
                    trip.getPassengers().isEmpty() ? "En attente" : "Accepté"
                });
            }
        }
    }
    
    private void clearOfferFields() {
        offerDepartureField.setText("");
        offerDestinationField.setText("");
        offerDateField.setText("");
        offerTimeField.setText("");
        offerPriceField.setText("");
    }
} 