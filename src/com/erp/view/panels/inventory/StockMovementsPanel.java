package com.erp.view.panels.inventory;

import com.erp.model.StockMovement;
import com.erp.model.Warehouse;
import com.erp.service.mock.MockInventoryService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * StockMovementsPanel displays stock movement history and allows recording new movements.
 */
public class StockMovementsPanel extends JPanel {

    private MockInventoryService inventoryService;

    private JTable movementsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilter;
    private JComboBox<String> warehouseFilter;
    private JTextField searchField;

    private static final String[] COLUMNS = {"Movement #", "Date", "Type", "Product", "SKU", "Warehouse", "Qty", "Before", "After", "Reason"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public StockMovementsPanel() {
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
        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "INBOUND", "OUTBOUND", "ADJUSTMENT", "TRANSFER"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Warehouse filter
        warehouseFilter = new JComboBox<>();
        warehouseFilter.setFont(Constants.FONT_REGULAR);
        warehouseFilter.addItem("All Warehouses");
        for (Warehouse w : inventoryService.getActiveWarehouses()) {
            warehouseFilter.addItem(w.getCode() + " - " + w.getName());
        }
        warehouseFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by movement #, product, or reference");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movementsTable = new JTable(tableModel);
        movementsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(movementsTable);

        // Column widths
        movementsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Movement #
        movementsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Date
        movementsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Type
        movementsTable.getColumnModel().getColumn(3).setPreferredWidth(140); // Product
        movementsTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // SKU
        movementsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Warehouse
        movementsTable.getColumnModel().getColumn(6).setPreferredWidth(60);  // Qty
        movementsTable.getColumnModel().getColumn(7).setPreferredWidth(60);  // Before
        movementsTable.getColumnModel().getColumn(8).setPreferredWidth(60);  // After
        movementsTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Reason

        // Type column renderer
        movementsTable.getColumnModel().getColumn(2).setCellRenderer(new TypeCellRenderer());
    }

    private void layoutComponents() {
        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(movementsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        // Filter row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Warehouse:"));
        filterPanel.add(warehouseFilter);
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

        JButton inboundBtn = new JButton("Record Inbound");
        inboundBtn.setFont(Constants.FONT_BUTTON);
        inboundBtn.setBackground(Constants.SUCCESS_COLOR);
        inboundBtn.setForeground(Constants.TEXT_LIGHT);
        inboundBtn.setPreferredSize(new Dimension(130, 30));
        inboundBtn.addActionListener(e -> recordInbound());
        actionPanel.add(inboundBtn);

        JButton outboundBtn = new JButton("Record Outbound");
        outboundBtn.setFont(Constants.FONT_BUTTON);
        outboundBtn.setBackground(Constants.WARNING_COLOR);
        outboundBtn.setForeground(Constants.TEXT_LIGHT);
        outboundBtn.setPreferredSize(new Dimension(130, 30));
        outboundBtn.addActionListener(e -> recordOutbound());
        actionPanel.add(outboundBtn);

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

        List<StockMovement> movements = inventoryService.getAllMovements();
        String typeSelection = (String) typeFilter.getSelectedItem();
        String warehouseSelection = (String) warehouseFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (StockMovement sm : movements) {
            // Filter by type
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(sm.getMovementType())) {
                continue;
            }

            // Filter by warehouse
            if (!"All Warehouses".equals(warehouseSelection)) {
                String whCode = warehouseSelection.split(" - ")[0];
                Warehouse wh = inventoryService.getWarehouseByCode(whCode);
                if (wh == null || sm.getWarehouseId() != wh.getWarehouseId()) continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = sm.getMovementNumber().toLowerCase().contains(searchTerm) ||
                                  sm.getProductName().toLowerCase().contains(searchTerm) ||
                                  (sm.getReferenceNumber() != null && sm.getReferenceNumber().toLowerCase().contains(searchTerm));
                if (!matches) continue;
            }

            tableModel.addRow(new Object[]{
                sm.getMovementNumber(),
                sm.getCreatedAt().format(DATE_FORMAT),
                sm.getMovementType(),
                sm.getProductName(),
                sm.getProductSku(),
                sm.getWarehouseName(),
                sm.getQuantity(),
                sm.getQuantityBefore(),
                sm.getQuantityAfter(),
                sm.getReason()
            });
        }
    }

    private void recordInbound() {
        showMovementDialog("INBOUND", "Record Inbound Stock");
    }

    private void recordOutbound() {
        showMovementDialog("OUTBOUND", "Record Outbound Stock");
    }

    private void showMovementDialog(String type, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Product selection
        JComboBox<String> productCombo = new JComboBox<>();
        for (var p : inventoryService.getAllProducts()) {
            productCombo.addItem(p.getSku() + " - " + p.getName());
        }

        // Warehouse selection
        JComboBox<String> warehouseCombo = new JComboBox<>();
        for (var w : inventoryService.getActiveWarehouses()) {
            warehouseCombo.addItem(w.getCode() + " - " + w.getName());
        }

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JTextField reasonField = new JTextField();
        JTextField referenceField = new JTextField();

        formPanel.add(new JLabel("Product:"));
        formPanel.add(productCombo);
        formPanel.add(new JLabel("Warehouse:"));
        formPanel.add(warehouseCombo);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantitySpinner);
        formPanel.add(new JLabel("Reason:"));
        formPanel.add(reasonField);
        formPanel.add(new JLabel("Reference #:"));
        formPanel.add(referenceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            // In real implementation, would call service to record movement
            UIHelper.showSuccess(dialog, type + " recorded successfully.");
            dialog.dispose();
            loadData();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        loadData();
    }

    // Type cell renderer
    private static class TypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String type = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (type) {
                        case "INBOUND":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "OUTBOUND":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "ADJUSTMENT":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "TRANSFER":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
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
