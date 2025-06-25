package com.carpooling.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

/**
 * Custom button with modern styling and hover effects
 */
public class Button extends JButton {
    
    private final Color originalBackground;
    private final Color hoverBackground;
    private boolean isHovering = false;
    private final int cornerRadius = 10;
    
    public Button() {
        this("");
    }
    
    public Button(String text) {
        super(text);
        this.originalBackground = new Color(128, 15, 28);
        this.hoverBackground = new Color(150, 20, 35);
        initializeComponent();
    }
    
    private void initializeComponent() {
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setBackground(originalBackground);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovering = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint background
        if (isHovering) {
            g2.setColor(hoverBackground);
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        repaint();
    }
}
