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
 * Outline button with transparent background and border
 */
public class ButtonOutLine extends JButton {
    
    private Color color = Color.WHITE;
    private boolean isHovering = false;
    private final int cornerRadius = 10;
    
    public ButtonOutLine() {
        initializeComponent();
    }
    
    private void initializeComponent() {
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setForeground(color);
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
        
        // Paint background when hovering
        if (isHovering) {
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
        
        // Paint border
        g2.setColor(color);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        this.color = fg;
        repaint();
    }
}
