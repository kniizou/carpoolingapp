package com.carpooling.util;

import com.carpooling.model.Notification;
import com.carpooling.model.NotificationManager;

/**
 * Utility class for creating sample notifications for testing purposes
 */
public class NotificationTestUtils {
    
    /**
     * Create sample passenger notifications for testing
     * @param userId The user ID to create notifications for
     */
    public static void createPassengerTestNotifications(String userId) {
        NotificationManager notifManager = NotificationManager.getInstance();
        
        // Clear existing notifications first
        notifManager.clearAllNotifications();
        
        // Welcome notification
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "🎉 Bienvenue dans le système de covoiturage! Cliquez sur la cloche pour voir vos notifications.",
                null
            ));
        
        // Trip request accepted
        notifManager.addNotification(userId,
            Notification.createSuccessNotification(
                "✅ Excellente nouvelle! Votre demande pour le trajet Paris → Lyon le 25/06/2025 à 14h30 a été acceptée par le conducteur.",
                "trip001"
            ));
        
        // New trip available
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "🚗 Nouveau trajet disponible: Marseille → Nice le 28/06/2025 à 16h00 (2 places disponibles, 20€ par personne)",
                "trip002"
            ));
        
        // Weather warning
        notifManager.addNotification(userId,
            Notification.createWarningNotification(
                "⚠️ Alerte météo: Pluie prévue pour votre trajet de demain. Le conducteur vous contactera si nécessaire.",
                "trip003"
            ));
        
        // Trip cancelled
        notifManager.addNotification(userId,
            Notification.createErrorNotification(
                "❌ Désolé, le trajet Lyon → Grenoble du 22/06/2025 a été annulé par le conducteur. Remboursement automatique.",
                "trip004"
            ));
        
        // Price drop notification
        notifManager.addNotification(userId,
            Notification.createSuccessNotification(
                "💰 Super! Le prix du trajet Toulouse → Bordeaux a baissé de 25€ à 18€. Réservez maintenant!",
                "trip005"
            ));
    }
    
    /**
     * Create sample driver notifications for testing
     * @param userId The user ID to create notifications for
     */
    public static void createDriverTestNotifications(String userId) {
        NotificationManager notifManager = NotificationManager.getInstance();
        
        // Clear existing notifications first
        notifManager.clearAllNotifications();
        
        // Welcome notification
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "🚗 Bienvenue conducteur! Vous avez 3 nouvelles demandes de passagers en attente.",
                null
            ));
        
        // New passenger request
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "👤 Jean Dupont (⭐⭐⭐⭐⭐) souhaite rejoindre votre trajet Paris → Lyon le 25/06/2025",
                "trip001"
            ));
        
        // Another passenger request
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "👤 Marie Martin (⭐⭐⭐⭐) souhaite rejoindre votre trajet Marseille → Nice le 28/06/2025",
                "trip002"
            ));
        
        // Trip fully booked
        notifManager.addNotification(userId,
            Notification.createSuccessNotification(
                "🎉 Félicitations! Votre trajet Lyon → Grenoble est maintenant complet (4/4 places occupées)",
                "trip003"
            ));
        
        // Payment received
        notifManager.addNotification(userId,
            Notification.createSuccessNotification(
                "💳 Paiement reçu: 75€ pour le trajet Nice → Monaco (3 passagers)",
                "trip004"
            ));
    }
    
    /**
     * Create sample admin notifications for testing
     * @param userId The user ID to create notifications for
     */
    public static void createAdminTestNotifications(String userId) {
        NotificationManager notifManager = NotificationManager.getInstance();
        
        // Clear existing notifications first
        notifManager.clearAllNotifications();
        
        // System notification
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "⚙️ Bienvenue administrateur! Le système fonctionne normalement. 45 trajets actifs, 128 utilisateurs.",
                null
            ));
        
        // User report
        notifManager.addNotification(userId,
            Notification.createWarningNotification(
                "⚠️ Signalement utilisateur: Comportement inapproprié rapporté pour l'utilisateur ID: user123",
                "user123"
            ));
        
        // System update
        notifManager.addNotification(userId,
            Notification.createSuccessNotification(
                "✅ Mise à jour système effectuée avec succès. Toutes les fonctionnalités sont opérationnelles.",
                null
            ));
        
        // Database backup
        notifManager.addNotification(userId,
            Notification.createInfoNotification(
                "💾 Sauvegarde automatique effectuée: 1,250 trajets et 500 utilisateurs sauvegardés",
                null
            ));
    }
}
