package com.carpooling.ui.validation;

import java.awt.Color;
import java.util.function.Predicate;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.carpooling.util.ValidationUtils;

/**
 * Helper class for real-time UI field validation and dynamic highlighting.
 * Provides centralized validation logic with visual feedback.
 */
public class UIValidationHelper {
    
    // Color constants for validation feedback
    public static final Color VALID_COLOR = Color.WHITE;
    public static final Color INVALID_COLOR = new Color(255, 200, 200); // Light red
    public static final Color REQUIRED_EMPTY_COLOR = new Color(255, 235, 200); // Light orange
    public static final Color FOCUS_COLOR = new Color(240, 248, 255); // Light blue
    
    /**
     * Setup validation for time input fields (HH:mm format)
     */
    public static void setupTimeValidation(JTextField timeField) {
        setupFieldValidation(timeField, input -> {
            if (input.isEmpty()) return true; // Empty is allowed
            return ValidationUtils.isValidTimeFormat(input);
        }, "Format: HH:MM (exemple: 14:30)", "Format invalide. Utilisez HH:MM (exemple: 14:30)");
    }
    
    /**
     * Setup validation for numeric fields (integer)
     */
    public static void setupNumericValidation(JTextField numericField, int minValue, String fieldName) {
        setupFieldValidation(numericField, input -> {
            if (input.isEmpty()) return true; // Empty is allowed
            try {
                int value = Integer.parseInt(input);
                return value >= minValue;
            } catch (NumberFormatException e) {
                return false;
            }
        }, fieldName + " (nombre >= " + minValue + ")", "Valeur invalide. Entrez un nombre >= " + minValue);
    }
    
    /**
     * Setup validation for price fields (double)
     */
    public static void setupPriceValidation(JTextField priceField) {
        setupFieldValidation(priceField, input -> {
            if (input.isEmpty()) return true; // Empty is allowed
            try {
                double value = Double.parseDouble(input);
                return value >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }, "Prix (nombre >= 0)", "Prix invalide. Entrez un nombre >= 0");
    }
    
    /**
     * Setup validation for email fields
     */
    public static void setupEmailValidation(JTextField emailField) {
        setupFieldValidation(emailField, input -> {
            if (input.isEmpty()) return true; // Empty is allowed
            return input.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }, "Format: exemple@domaine.com", "Format d'email invalide");
    }
    
    /**
     * Setup validation for required text fields
     */
    public static void setupRequiredTextValidation(JTextField textField, String fieldName) {
        setupFieldValidation(textField, input -> !input.trim().isEmpty(), 
                fieldName + " (obligatoire)", fieldName + " ne peut pas être vide");
        
        // Add special highlighting for required fields when empty
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateRequiredField(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateRequiredField(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateRequiredField(); }
            
            private void updateRequiredField() {
                String input = textField.getText().trim();
                if (input.isEmpty()) {
                    textField.setBackground(REQUIRED_EMPTY_COLOR);
                    textField.setToolTipText(fieldName + " est obligatoire");
                } else {
                    textField.setBackground(VALID_COLOR);
                    textField.setToolTipText(fieldName + " (valide)");
                }
            }
        });
    }
    
    /**
     * Setup validation for date fields (dd/MM/yyyy format)
     */
    public static void setupDateValidation(JTextField dateField) {
        setupFieldValidation(dateField, input -> {
            if (input.isEmpty()) return true; // Empty is allowed
            try {
                ValidationUtils.validateDate(input);
                return true;
            } catch (Exception e) {
                return false;
            }
        }, "Format: dd/MM/yyyy (exemple: 23/06/2025)", "Format de date invalide. Utilisez dd/MM/yyyy");
    }
    
    /**
     * Generic field validation setup with custom validator
     */
    public static void setupFieldValidation(JTextField field, Predicate<String> validator, 
                                          String validTooltip, String invalidTooltip) {
        field.setToolTipText(validTooltip);
        
        // Add real-time validation
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateField(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateField(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateField(); }
            
            private void validateField() {
                String input = field.getText().trim();
                if (validator.test(input)) {
                    field.setBackground(VALID_COLOR);
                    field.setToolTipText(input.isEmpty() ? validTooltip : "Valide: " + validTooltip);
                } else {
                    field.setBackground(INVALID_COLOR);
                    field.setToolTipText(invalidTooltip);
                }
            }
        });
        
        // Add focus highlighting
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getBackground().equals(VALID_COLOR)) {
                    field.setBackground(FOCUS_COLOR);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Re-validate on focus lost
                String input = field.getText().trim();
                if (validator.test(input)) {
                    field.setBackground(VALID_COLOR);
                } else {
                    field.setBackground(INVALID_COLOR);
                }
            }
        });
    }
    
    /**
     * Setup highlighting for combo boxes when focused
     */
    public static void setupComboBoxHighlighting(JComboBox<?> comboBox) {
        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                comboBox.setBackground(FOCUS_COLOR);
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                comboBox.setBackground(VALID_COLOR);
            }
        });
    }
    
    /**
     * Validate all fields in a form and return true if all are valid
     */
    public static boolean validateForm(JTextField... fields) {
        boolean allValid = true;
        for (JTextField field : fields) {
            if (field.getBackground().equals(INVALID_COLOR) || 
                field.getBackground().equals(REQUIRED_EMPTY_COLOR)) {
                allValid = false;
                // Flash the invalid field to draw attention
                flashField(field);
            }
        }
        return allValid;
    }
    
    /**
     * Flash a field to draw user attention
     */
    private static void flashField(JTextField field) {
        Color originalColor = field.getBackground();
        new javax.swing.Timer(100, e -> {
            field.setBackground(originalColor.equals(Color.RED) ? INVALID_COLOR : Color.RED);
        }).start();
        
        new javax.swing.Timer(300, e -> field.setBackground(originalColor)).start();
    }
    
    /**
     * Clear validation state and reset field to normal appearance
     */
    public static void clearValidation(JTextField field) {
        field.setBackground(VALID_COLOR);
        field.setToolTipText("");
    }
    
    /**
     * Set field as valid with custom message
     */
    public static void setFieldValid(JTextField field, String message) {
        field.setBackground(VALID_COLOR);
        field.setToolTipText(message);
    }
    
    /**
     * Set field as invalid with custom message
     */
    public static void setFieldInvalid(JTextField field, String message) {
        field.setBackground(INVALID_COLOR);
        field.setToolTipText(message);
    }
}
