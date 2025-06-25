package com.carpooling.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;
import com.carpooling.ui.icons.IconGenerator;

/**
 * Centralized theme configuration for the carpooling application.
 * Provides consistent colors, fonts, and icons throughout the application.
 */
public class Theme {
    
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(37, 99, 235);     // Blue-600
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);     // Blue-700
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);   // Blue-100
    
    public static final Color SECONDARY_COLOR = new Color(107, 114, 128);  // Gray-500
    public static final Color SECONDARY_LIGHT = new Color(209, 213, 219); // Gray-300
    
    public static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Green-500
    public static final Color SUCCESS_LIGHT = new Color(220, 252, 231);    // Green-100
    
    public static final Color WARNING_COLOR = new Color(245, 158, 11);     // Amber-500
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);    // Amber-100
    
    public static final Color ERROR_COLOR = new Color(239, 68, 68);        // Red-500
    public static final Color ERROR_LIGHT = new Color(254, 226, 226);      // Red-100
    
    public static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Gray-50
    public static final Color SURFACE_COLOR = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(229, 231, 235);     // Gray-200
    
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);        // Gray-900
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);   // Gray-500
    public static final Color TEXT_DISABLED = new Color(156, 163, 175);    // Gray-400
    
    // Typography
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    
    // Icon cache
    private static final Map<String, ImageIcon> iconCache = new HashMap<>();
    
    /**
     * Gets an icon by name, creating a simple text-based icon if not found in resources.
     * @param iconName the name of the icon
     * @return the ImageIcon
     */
    public static ImageIcon getIcon(String iconName) {
        if (iconCache.containsKey(iconName)) {
            return iconCache.get(iconName);
        }
        
        try {
            String path = "/icons/" + iconName + ".png";
            ImageIcon icon = new ImageIcon(Theme.class.getResource(path));
            iconCache.put(iconName, icon);
            return icon;
        } catch (Exception e) {
            // Create a simple text-based icon as fallback
            String symbol = getIconSymbol(iconName);
            ImageIcon icon = IconGenerator.createSymbolIcon(symbol, 16);
            iconCache.put(iconName, icon);
            return icon;
        }
    }
    
    /**
     * Maps icon names to simple text symbols.
     */
    private static String getIconSymbol(String iconName) {
        switch (iconName) {
            case ICON_LOGIN: return "→";
            case ICON_LOGOUT: return "←";
            case ICON_USER: return "👤";
            case ICON_CAR: return "🚗";
            case ICON_SEARCH: return "🔍";
            case ICON_ADD: return "+";
            case ICON_EDIT: return "✏";
            case ICON_DELETE: return "×";
            case ICON_REFRESH: return "↻";
            case ICON_SETTINGS: return "⚙";
            case ICON_NOTIFICATION: return "🔔";
            case ICON_DASHBOARD: return "📊";
            case ICON_TRIP: return "🗺";
            case ICON_PASSENGER: return "👥";
            case ICON_DRIVER: return "🚙";
            case ICON_ADMIN: return "⚡";
            default: return "?";
        }
    }
    
    /**
     * Gets a scaled icon by name.
     * @param iconName the name of the icon
     * @param width the desired width
     * @param height the desired height
     * @return the scaled ImageIcon, or null if not found
     */
    public static ImageIcon getScaledIcon(String iconName, int width, int height) {
        ImageIcon originalIcon = getIcon(iconName);
        if (originalIcon != null) {
            return new ImageIcon(originalIcon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
        }
        return null;
    }
    
    // Icon Constants (using simple text icons for now, can be replaced with actual icons)
    public static final String ICON_LOGIN = "login";
    public static final String ICON_LOGOUT = "logout";
    public static final String ICON_USER = "user";
    public static final String ICON_CAR = "car";
    public static final String ICON_SEARCH = "search";
    public static final String ICON_ADD = "add";
    public static final String ICON_EDIT = "edit";
    public static final String ICON_DELETE = "delete";
    public static final String ICON_REFRESH = "refresh";
    public static final String ICON_SETTINGS = "settings";
    public static final String ICON_NOTIFICATION = "notification";
    public static final String ICON_DASHBOARD = "dashboard";
    public static final String ICON_TRIP = "trip";
    public static final String ICON_PASSENGER = "passenger";
    public static final String ICON_DRIVER = "driver";
    public static final String ICON_ADMIN = "admin";
    
    // Utility methods for creating consistent UI components
    
    /**
     * Creates a primary button style configuration.
     */
    public static void stylePrimaryButton(javax.swing.JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
    }
    
    /**
     * Creates a secondary button style configuration.
     */
    public static void styleSecondaryButton(javax.swing.JButton button) {
        button.setBackground(SURFACE_COLOR);
        button.setForeground(PRIMARY_COLOR);
        button.setFont(FONT_BUTTON);
        button.setBorder(javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setOpaque(true);
    }
    
    /**
     * Creates a danger button style configuration.
     */
    public static void styleDangerButton(javax.swing.JButton button) {
        button.setBackground(ERROR_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
    }
    
    /**
     * Styles a text field with consistent appearance.
     */
    public static void styleTextField(javax.swing.JTextField field) {
        field.setFont(FONT_BODY);
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER_COLOR, 1),
            javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    /**
     * Styles a label with consistent appearance.
     */
    public static void styleLabel(javax.swing.JLabel label, Font font, Color color) {
        label.setFont(font);
        label.setForeground(color);
    }
}
