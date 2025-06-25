package com.carpooling.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

/**
 * Cover panel with gradient background and welcome message
 */
public class PanelCover extends JPanel {

    private ActionListener event;
    private MigLayout layout;
    private JLabel title;
    private JLabel description;
    private JLabel description1;
    private ButtonOutLine button;
    private boolean isLogin;

    public PanelCover() {
        initComponents();
        setOpaque(false);
        layout = new MigLayout("wrap, fill", "[center]", "push[]25[]10[]25[]push");
        setLayout(layout);
        init();
    }

    private void init() {
        title = new JLabel("Bienvenue!");
        title.setFont(new Font("sansserif", 1, 30));
        title.setForeground(new Color(245, 245, 245));
        add(title);
        
        description = new JLabel("Pour rester connecté avec nous");
        description.setForeground(new Color(245, 245, 245));
        add(description);
        
        description1 = new JLabel("saisis tes informations personnelles!");
        description1.setForeground(new Color(245, 245, 245));
        add(description1);
        
        button = new ButtonOutLine();
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(255, 255, 255));
        button.setText("CONNEXION");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (event != null) {
                    event.actionPerformed(ae);
                }
            }
        });
        add(button, "w 60%, h 40");
    }

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        GradientPaint g = new GradientPaint(0, 0, new Color(128, 15, 28), 0, getHeight(), new Color(94, 11, 21));
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(getWidth() - 20, 0, getWidth(), getHeight());
        super.paintChildren(grphcs);
    }

    public void addEvent(ActionListener event) {
        this.event = event;
    }

    public void registerLeft(boolean left) {
        animateTextTransition(left);
    }
    
    private void animateTextTransition(boolean left) {
        // Create scale and fade animation for button
        Timer animationTimer = new Timer(20, null);
        final float[] alpha = {1.0f};
        final float[] scale = {1.0f};
        final int[] step = {0};
        final int totalSteps = 15;
        
        animationTimer.addActionListener(e -> {
            step[0]++;
            
            if (step[0] <= totalSteps / 2) {
                // Scale down and fade out
                float progress = (float) step[0] / (totalSteps / 2);
                alpha[0] = 1.0f - progress;
                scale[0] = 1.0f - (progress * 0.1f); // Slight scale down
            } else if (step[0] == (totalSteps / 2) + 1) {
                // Change text content at midpoint
                if (left) {
                    isLogin = false;
                    title.setText("Bienvenue!");
                    description.setText("Pour rester connecté avec nous");
                    description1.setText("saisis tes informations personnelles!");
                    button.setText("CONNEXION");
                } else {
                    isLogin = true;
                    title.setText("Salut, ami!");
                    description.setText("Saisis tes informations personnelles");
                    description1.setText("et commence ton voyage avec nous");
                    button.setText("S'INSCRIRE");
                }
            } else {
                // Scale up and fade in
                float progress = (float) (step[0] - totalSteps / 2) / (totalSteps / 2);
                alpha[0] = progress;
                scale[0] = 0.9f + (progress * 0.1f); // Scale back up
            }
            
            // Apply transformations
            title.setForeground(new Color(245, 245, 245, Math.max(0, Math.min(255, (int)(alpha[0] * 255)))));
            description.setForeground(new Color(245, 245, 245, Math.max(0, Math.min(255, (int)(alpha[0] * 255)))));
            description1.setForeground(new Color(245, 245, 245, Math.max(0, Math.min(255, (int)(alpha[0] * 255)))));
            
            if (step[0] >= totalSteps) {
                animationTimer.stop();
                // Ensure final state
                title.setForeground(new Color(245, 245, 245));
                description.setForeground(new Color(245, 245, 245));
                description1.setForeground(new Color(245, 245, 245));
            }
            
            repaint();
        });
        
        animationTimer.start();
    }

    private void initComponents() {
        // Generated code placeholder
    }
}
