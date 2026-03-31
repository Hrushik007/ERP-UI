package com.erp.view.panels.inventory;

import com.erp.model.StockLevel;
import com.erp.model.Warehouse;
import com.erp.service.mock.MockInventoryService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * StockLevelsPanel displays current inventory levels across warehouses.
 */
public class StockLevelsPanel extends JPanel {

    private MockInventoryService inventoryService;

    private JTable stockTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> warehouseFilter;
    private JComboBox<String> statusFilter;
    private JTextField searchField;

    // Summary labels
    private JLabel totalProductsLabel;
    private JLabel lowStockLabel;
    private JLabel outOfStockLabel;
    private JLabel inventoryValueLabel;

    private static final String[] COLUMNS = {"Product", "SKU", "Warehouse", "Quantity", "Reserved", "Available", "Reorder Lvl", "Status"};

    public StockLevelsPanel() {
        inventoryService = MockInventoryService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Warehouse filter
        warehouseFilter = new JComboBox<>();
        warehouseFilter.setFont(Constants.FONT_REGULAR);
        warehouseFilter.addItem("All Warehouses");
        for (Warehouse w : inventoryService.getActiveWarehouses()) {
            warehouseFilter.addItem(w.getCode() + " - " + w.getName());
        }
        warehouseFilter.addActionListener(e -> loadData());

        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "IN_STOCK", "LOW_STOCK", "OUT_OF_STOCK", "OVERSTOCK"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by product name or SKU");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(tableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(stockTable);

        // Column widths
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Product
        stockTable.getColumnModel().getColumn(1).setPreferredWidth(100); // SKU
        stockTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Warehouse
        stockTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
        stockTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Reserved
        stockTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Available
        stockTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Reorder
        stockTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Status

        // Status column renderer
        stockTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalProductsLabel = createSummaryValue("0");
        lowStockLabel = createSummaryValue("0");
        outOfStockLabel = createSummaryValue("0");
        inventoryValueLabel = createSummaryValue("$0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Top - Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Items", totalProductsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Low Stock", lowStockLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Out of Stock", outOfStockLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Inventory Value", inventoryValueLabel, Constants.SUCCESS_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_MEDIUM,
                          Constants.PADDING_SMALL, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        // Filters row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Warehouse:"));
        filterPanel.add(warehouseFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> loadData());
        filterPanel.add(searchBtn);

        // Actions row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));

        JButton adjustBtn = UIHelper.createPrimaryButton("Adjust Stock");
        adjustBtn.setPreferredSize(new Dimension(110, 30));
        adjustBtn.addActionListener(e -> adjustStock());
        actionPanel.add(adjustBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        actionPanel.add(refreshBtn);

        toolbar.add(filterPanel, BorderLayout.NORTH);
        toolbar.add(actionPanel, BorderLayout.SOUTH);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<StockLevel> stockLevels = inventoryService.getAllStockLevels();
        String warehouseSelection = (String) warehouseFilter.getSelectedItem();
        String statusSelection = (String) statusFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (StockLevel sl : stockLevels) {
            // Filter by warehouse
            if (!"All Warehouses".equals(warehouseSelection)) {
                String whCode = warehouseSelection.split(" - ")[0];
                Warehouse wh = inventoryService.getWarehouseByCode(whCode);
                if (wh == null || sl.getWarehouseId() != wh.getWarehouseId()) continue;
            }

            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(sl.getStockStatus())) {
                continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = sl.getProductName().toLowerCase().contains(searchTerm) ||
                                  sl.getProductSku().toLowerCase().contains(searchTerm);
                if (!matches) continue;
            }

            tableModel.addRow(new Object[]{
                sl.getProductName(),
                sl.getProductSku(),
                sl.getWarehouseName(),
                sl.getQuantity(),
                sl.getReservedQuantity(),
                sl.getAvailableQuantity(),
                sl.getReorderLevel(),
                sl.getStockStatus()
            });
        }

        updateSummary();
    }

    private void updateSummary() {
        Map<String, Integer> counts = inventoryService.getStockCountByStatus();
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();

        totalProductsLabel.setText(String.valueOf(total));
        lowStockLabel.setText(String.valueOf(counts.getOrDefault("LOW_STOCK", 0)));
        outOfStockLabel.setText(String.valueOf(counts.getOrDefault("OUT_OF_STOCK", 0)));
        inventoryValueLabel.setText("$" + String.format("%,.0f", inventoryService.getTotalInventoryValue()));
    }

    private void adjustStock() {
        int row = stockTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a stock item to adjust.");
            return;
        }

        String productName = (String) tableModel.getValueAt(row, 0);
        String warehouseName = (String) tableModel.getValueAt(row, 2);
        int currentQty = (int) tableModel.getValueAt(row, 3);

        String input = JOptionPane.showInputDialog(this,
            "Adjust stock for " + productName + " at " + warehouseName + "\n" +
            "Current quantity: " + currentQty + "\n\nEnter new quantity:",
            "Stock Adjustment",
            JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(input.trim());
                if (newQty < 0) {
                    UIHelper.showError(this, "Quantity cannot be negative.");
                    return;
                }
                // In real implementation, would call service to record adjustment
                UIHelper.showSuccess(this, "Stock adjusted successfully.");
                loadData();
            } catch (NumberFormatException e) {
                UIHelper.showError(this, "Please enter a valid number.");
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (status) {
                        case "IN_STOCK":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "LOW_STOCK":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "OUT_OF_STOCK":
                            setBackground(new Color(248, 215, 218));
                            setForeground(new Color(114, 28, 36));
                            break;
                        case "OVERSTOCK":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        default:
                            setBackground(table.getBackground());
                            setForeground(table.getForeground());
                    }
                }
            }
            return this;
        }
    }
}
