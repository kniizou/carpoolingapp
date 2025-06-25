package com.carpooling.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

/**
 * Custom password field with modern styling, icons and placeholder hints
 */
public class MyPasswordField extends JPasswordField {
    
    private String hint = "";
    private Icon prefixIcon;
    private final Color hintColor = new Color(150, 150, 150);
    private final Color focusColor = new Color(128, 15, 28);
    private final Color normalColor = new Color(200, 200, 200);
    private boolean showingHint = true;
    
    public MyPasswordField() {
        initializeComponent();
    }
    
    public MyPasswordField(String hint) {
        this.hint = hint;
        initializeComponent();
    }
    
    private void initializeComponent() {
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(normalColor, 1, 10),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(focusColor, 2, 10),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                if (showingHint) {
                    setText("");
                    setForeground(Color.BLACK);
                    setEchoChar('*');
                    showingHint = false;
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(normalColor, 1, 10),
                    new EmptyBorder(8, 12, 8, 12)
                ));
                if (getPassword().length == 0) {
                    showHint();
                }
            }
        });
        
        showHint();
    }
    
    private void showHint() {
        if (!hint.isEmpty()) {
            setText(hint);
            setForeground(hintColor);
            setEchoChar((char) 0); // Show text instead of dots
            showingHint = true;
        }
    }
    
    @Override
    public char[] getPassword() {
        return showingHint ? new char[0] : super.getPassword();
    }
    
    public void setHint(String hint) {
        this.hint = hint;
        if (showingHint || getPassword().length == 0) {
            showHint();
        }
    }
    
    public void setPrefixIcon(Icon icon) {
        this.prefixIcon = icon;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (prefixIcon != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Insets insets = getInsets();
            int iconY = (getHeight() - prefixIcon.getIconHeight()) / 2;
            prefixIcon.paintIcon(this, g2, insets.left + 5, iconY);
            
            g2.dispose();
        }
    }
    
    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        if (prefixIcon != null) {
            insets.left += prefixIcon.getIconWidth() + 10;
        }
        return insets;
    }
}
