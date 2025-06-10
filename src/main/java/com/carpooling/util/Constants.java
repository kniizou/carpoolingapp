package com.carpooling.util;

public class Constants {
    // Couleurs
    public static final String PRIMARY_COLOR = "#5e0000";
    public static final String SECONDARY_COLOR = "#FFFFFF";
    public static final String BACKGROUND_COLOR = "#F5F5F5";
    public static final String TEXT_COLOR = "#333333";
    public static final String ERROR_COLOR = "#FF0000";
    public static final String SUCCESS_COLOR = "#00FF00";

    // Rôles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DRIVER = "DRIVER";
    public static final String ROLE_PASSENGER = "PASSENGER";

    // Statuts des passagers
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_REJECTED = "REJECTED";

    // Formats
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm";

    // Messages d'erreur
    public static final String ERROR_EMPTY_FIELD = "Ce champ ne peut pas être vide";
    public static final String ERROR_INVALID_EMAIL = "L'email n'est pas valide";
    public static final String ERROR_INVALID_PASSWORD = "Le mot de passe doit contenir au moins 6 caractères";
    public static final String ERROR_INVALID_AGE = "L'âge doit être supérieur ou égal à 18 ans";
    public static final String ERROR_INVALID_ROLE = "Le rôle n'est pas valide";
    public static final String ERROR_INVALID_DATE = "Format de date invalide. Utilisez le format dd/MM/yyyy";
    public static final String ERROR_INVALID_TIME = "Format d'heure invalide. Utilisez le format HH:mm";
    public static final String ERROR_INVALID_PRICE = "Le prix ne peut pas être négatif";
    public static final String ERROR_INVALID_SEATS = "Le nombre de places doit être supérieur à 0";
    public static final String ERROR_NO_SEATS = "Il n'y a plus de places disponibles";
    public static final String ERROR_ALREADY_PASSENGER = "Vous êtes déjà passager de ce trajet";
    public static final String ERROR_ALREADY_PENDING = "Vous avez déjà une demande en attente pour ce trajet";
    public static final String ERROR_DRIVER_PASSENGER = "Le conducteur ne peut pas être passager";

    // Messages de succès
    public static final String SUCCESS_LOGIN = "Connexion réussie";
    public static final String SUCCESS_REGISTER = "Inscription réussie";
    public static final String SUCCESS_TRIP_CREATED = "Trajet créé avec succès";
    public static final String SUCCESS_TRIP_UPDATED = "Trajet mis à jour avec succès";
    public static final String SUCCESS_TRIP_DELETED = "Trajet supprimé avec succès";
    public static final String SUCCESS_PASSENGER_ADDED = "Passager ajouté avec succès";
    public static final String SUCCESS_PASSENGER_REMOVED = "Passager retiré avec succès";
    public static final String SUCCESS_REQUEST_APPROVED = "Demande approuvée avec succès";
    public static final String SUCCESS_REQUEST_REJECTED = "Demande rejetée avec succès";

    // Configuration de la base de données
    public static final String DB_URL = "jdbc:mysql://localhost:3306/carpooling";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Configuration de l'application
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final String APP_TITLE = "Application de Covoiturage";
    public static final String APP_VERSION = "1.0.0";

    private Constants() {
        // Empêcher l'instanciation
    }
} 