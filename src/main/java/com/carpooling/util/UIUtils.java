package com.carpooling.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIUtils {
    private UIUtils() {
        // Empêcher l'instanciation
    }

    public static void configureFrame(JFrame frame) {
        frame.setTitle(Constants.APP_TITLE);
        frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.decode(Constants.BACKGROUND_COLOR));
    }

    public static JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode(Constants.PRIMARY_COLOR));
        button.setForeground(Color.decode(Constants.SECONDARY_COLOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(listener);
        return button;
    }

    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(Constants.PRIMARY_COLOR)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode(Constants.PRIMARY_COLOR)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return passwordField;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.decode(Constants.TEXT_COLOR));
        return label;
    }

    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode(Constants.BACKGROUND_COLOR));
        return panel;
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirmation", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static JTable createTable(String[] columnNames) {
        JTable table = new JTable();
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {},
            columnNames
        ) {
            boolean[] canEdit = new boolean[columnNames.length];
            {
                for (int i = 0; i < columnNames.length; i++) {
                    canEdit[i] = false;
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        return table;
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode(Constants.PRIMARY_COLOR)));
        return scrollPane;
    }

    public static void centerComponent(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - component.getWidth()) / 2;
        int y = (screenSize.height - component.getHeight()) / 2;
        component.setLocation(x, y);
    }
} 