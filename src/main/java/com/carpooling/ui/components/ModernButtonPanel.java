package com.carpooling.ui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.carpooling.ui.Theme;

/**
 * Panel for organizing action buttons with consistent styling and layout.
 */
public class ModernButtonPanel extends JPanel {
    
    private final List<JButton> primaryButtons = new ArrayList<>();
    private final List<JButton> secondaryButtons = new ArrayList<>();
    private final List<JButton> dangerButtons = new ArrayList<>();
    
    public ModernButtonPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBackground(Theme.BACKGROUND_COLOR);
    }
    
    /**
     * Adds a primary action button.
     */
    public JButton addPrimaryButton(String text, Runnable action) {
        JButton button = new JButton(text);
        Theme.stylePrimaryButton(button);
        button.setPreferredSize(new Dimension(120, 36));
        button.addActionListener(e -> action.run());
        
        primaryButtons.add(button);
        add(button);
        return button;
    }
    
    /**
     * Adds a secondary action button.
     */
    public JButton addSecondaryButton(String text, Runnable action) {
        JButton button = new JButton(text);
        Theme.styleSecondaryButton(button);
        button.setPreferredSize(new Dimension(120, 36));
        button.addActionListener(e -> action.run());
        
        secondaryButtons.add(button);
        add(button);
        return button;
    }
    
    /**
     * Adds a danger action button.
     */
    public JButton addDangerButton(String text, Runnable action) {
        JButton button = new JButton(text);
        Theme.styleDangerButton(button);
        button.setPreferredSize(new Dimension(120, 36));
        button.addActionListener(e -> action.run());
        
        dangerButtons.add(button);
        add(button);
        return button;
    }
    
    /**
     * Adds an icon button.
     */
    public JButton addIconButton(String iconName, String tooltip, Runnable action) {
        JButton button = new JButton();
        ImageIcon icon = Theme.getScaledIcon(iconName, 16, 16);
        if (icon != null) {
            button.setIcon(icon);
        } else {
            button.setText(iconName);
        }
        
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(36, 36));
        Theme.styleSecondaryButton(button);
        button.addActionListener(e -> action.run());
        
        add(button);
        return button;
    }
    
    /**
     * Adds a separator between button groups.
     */
    public void addSeparator() {
        add(Box.createHorizontalStrut(20));
    }
    
    /**
     * Enables/disables all buttons.
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component component : getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(enabled);
            }
        }
    }
    
    /**
     * Gets all buttons of a specific type.
     */
    public List<JButton> getPrimaryButtons() {
        return new ArrayList<>(primaryButtons);
    }
    
    public List<JButton> getSecondaryButtons() {
        return new ArrayList<>(secondaryButtons);
    }
    
    public List<JButton> getDangerButtons() {
        return new ArrayList<>(dangerButtons);
    }
}
