package com.carpooling.ui.icons;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Simple icon generator for basic UI icons.
 * Creates text-based icons as placeholders.
 */
public class IconGenerator {
    
    public static ImageIcon createTextIcon(String text, int size, Color backgroundColor, Color textColor) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fill background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, size, size, 4, 4);
        
        // Draw text
        g2d.setColor(textColor);
        Font font = new Font("SansSerif", Font.BOLD, size / 3);
        g2d.setFont(font);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (size - textWidth) / 2;
        int y = (size + textHeight) / 2 - fm.getDescent();
        
        g2d.drawString(text, x, y);
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    public static ImageIcon createSymbolIcon(String symbol, int size) {
        return createTextIcon(symbol, size, new Color(37, 99, 235), Color.WHITE);
    }
}
