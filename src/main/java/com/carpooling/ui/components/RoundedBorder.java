package com.carpooling.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

/**
 * Custom rounded border for modern UI components
 */
public class RoundedBorder extends AbstractBorder {
    
    private final Color color;
    private final int thickness;
    private final int radius;
    
    public RoundedBorder(Color color, int thickness, int radius) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        
        // Draw rounded rectangle border
        for (int i = 0; i < thickness; i++) {
            g2.drawRoundRect(x + i, y + i, width - 2 * i - 1, height - 2 * i - 1, radius, radius);
        }
        
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = thickness + 2;
        return insets;
    }
    
    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
