package com.carpooling.ui.components;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import com.carpooling.ui.Theme;

/**
 * Enhanced table component with modern styling and improved state management.
 */
public class ModernTable extends JTable {
    
    private final Consumer<Integer> onSelectionChanged;
    private final List<JButton> contextualButtons;
    
    public ModernTable(DefaultTableModel model, Consumer<Integer> onSelectionChanged, List<JButton> contextualButtons) {
        super(model);
        this.onSelectionChanged = onSelectionChanged;
        this.contextualButtons = contextualButtons;
        
        setupTableAppearance();
        setupSelectionBehavior();
    }
    
    private void setupTableAppearance() {
        // Apply theme styling
        setFont(Theme.FONT_BODY);
        setRowHeight(40);
        setGridColor(Theme.BORDER_COLOR);
        setSelectionBackground(Theme.PRIMARY_LIGHT);
        setSelectionForeground(Theme.TEXT_PRIMARY);
        setBackground(Theme.SURFACE_COLOR);
        setForeground(Theme.TEXT_PRIMARY);
        
        // Header styling
        JTableHeader header = getTableHeader();
        header.setFont(Theme.FONT_BODY_BOLD);
        header.setBackground(Theme.SECONDARY_LIGHT);
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_COLOR));
        
        // Selection mode
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(false);
        
        // Disable column reordering
        header.setReorderingAllowed(false);
        
        // Add padding to cells
        setIntercellSpacing(new Dimension(10, 2));
    }
    
    private void setupSelectionBehavior() {
        // Add selection listener
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = getSelectedRow();
                    updateContextualButtons(selectedRow >= 0);
                    
                    if (onSelectionChanged != null) {
                        onSelectionChanged.accept(selectedRow);
                    }
                }
            }
        });
        
        // Initially disable all contextual buttons
        updateContextualButtons(false);
    }
    
    private void updateContextualButtons(boolean hasSelection) {
        if (contextualButtons != null) {
            for (JButton button : contextualButtons) {
                button.setEnabled(hasSelection);
            }
        }
    }
    
    /**
     * Creates a scroll pane with modern styling for this table.
     */
    public JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Theme.SURFACE_COLOR);
        
        // Style the scroll bars
        scrollPane.getVerticalScrollBar().setBackground(Theme.BACKGROUND_COLOR);
        scrollPane.getHorizontalScrollBar().setBackground(Theme.BACKGROUND_COLOR);
        
        return scrollPane;
    }
    
    /**
     * Applies modern column sizing.
     */
    public void setColumnWidths(int... widths) {
        TableColumnModel columnModel = getColumnModel();
        for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }
    
    /**
     * Gets the selected row data as an Object array.
     */
    public Object[] getSelectedRowData() {
        int selectedRow = getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) getModel();
            Object[] rowData = new Object[model.getColumnCount()];
            for (int i = 0; i < rowData.length; i++) {
                rowData[i] = model.getValueAt(selectedRow, i);
            }
            return rowData;
        }
        return null;
    }
    
    /**
     * Refreshes the table data while preserving selection if possible.
     */
    public void refreshData() {
        int selectedRow = getSelectedRow();
        ((DefaultTableModel) getModel()).fireTableDataChanged();
        
        // Try to restore selection
        if (selectedRow >= 0 && selectedRow < getRowCount()) {
            setRowSelectionInterval(selectedRow, selectedRow);
        }
    }
}
