package com.erp.view.panels.inventory;

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
 * WarehousePanel displays warehouse information and utilization.
 */
public class WarehousePanel extends JPanel {

    private MockInventoryService inventoryService;

    private JTable warehouseTable;
    private DefaultTableModel tableModel;

    // Details panel
    private JLabel detailName;
    private JLabel detailCode;
    private JLabel detailType;
    private JLabel detailAddress;
    private JLabel detailManager;
    private JLabel detailCapacity;
    private JProgressBar utilizationBar;

    private JButton editButton;
    private JButton addButton;

    private static final String[] COLUMNS = {"Code", "Name", "Type", "City", "Capacity", "Used", "Utilization", "Status"};

    public WarehousePanel() {
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
        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        warehouseTable = new JTable(tableModel);
        warehouseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(warehouseTable);

        // Column widths
        warehouseTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Code
        warehouseTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        warehouseTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Type
        warehouseTable.getColumnModel().getColumn(3).setPreferredWidth(100); // City
        warehouseTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Capacity
        warehouseTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Used
        warehouseTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Utilization
        warehouseTable.getColumnModel().getColumn(7).setPreferredWidth(70);  // Status

        // Utilization column renderer
        warehouseTable.getColumnModel().getColumn(6).setCellRenderer(new UtilizationCellRenderer());

        warehouseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showWarehouseDetails();
            }
        });

        // Detail labels
        detailName = new JLabel("-");
        detailCode = new JLabel("-");
        detailType = new JLabel("-");
        detailAddress = new JLabel("-");
        detailManager = new JLabel("-");
        detailCapacity = new JLabel("-");

        utilizationBar = new JProgressBar(0, 100);
        utilizationBar.setStringPainted(true);
        utilizationBar.setPreferredSize(new Dimension(150, 20));

        // Buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editWarehouse());

        addButton = UIHelper.createPrimaryButton("Add Warehouse");
        addButton.addActionListener(e -> addWarehouse());
    }

    private void layoutComponents() {
        // Toolbar
        JPanel toolbar = createToolbar();

        // Main content - split pane
        JSplitPane splitPane = createMainContent();

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        toolbar.add(new JLabel("Actions:"));
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(addButton);
        toolbar.add(editButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    private JSplitPane createMainContent() {
        // Left - Table
        JScrollPane tableScroll = new JScrollPane(warehouseTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScroll.getViewport().setBackground(Constants.BG_WHITE);

        // Right - Details panel
        JPanel detailsPanel = createDetailsPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailsPanel);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.6);

        return splitPane;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        // Title
        JLabel title = new JLabel("Warehouse Details");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, Constants.PADDING_MEDIUM, 0));

        // Info grid
        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 8));
        infoPanel.setOpaque(false);

        infoPanel.add(createInfoLabel("Name:"));
        infoPanel.add(detailName);
        infoPanel.add(createInfoLabel("Code:"));
        infoPanel.add(detailCode);
        infoPanel.add(createInfoLabel("Type:"));
        infoPanel.add(detailType);
        infoPanel.add(createInfoLabel("Address:"));
        infoPanel.add(detailAddress);
        infoPanel.add(createInfoLabel("Manager:"));
        infoPanel.add(detailManager);
        infoPanel.add(createInfoLabel("Capacity:"));
        infoPanel.add(detailCapacity);
        infoPanel.add(createInfoLabel("Utilization:"));
        infoPanel.add(utilizationBar);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(infoPanel, BorderLayout.CENTER);

        panel.add(topSection, BorderLayout.NORTH);

        return panel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        label.setForeground(Constants.TEXT_SECONDARY);
        return label;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Warehouse> warehouses = inventoryService.getAllWarehouses();
        Map<Integer, Double> utilization = inventoryService.getWarehouseUtilization();

        for (Warehouse w : warehouses) {
            double util = utilization.getOrDefault(w.getWarehouseId(), 0.0);
            tableModel.addRow(new Object[]{
                w.getCode(),
                w.getName(),
                w.getType(),
                w.getCity(),
                w.getCapacity(),
                w.getUsedCapacity(),
                String.format("%.1f%%", util),
                w.isActive() ? "Active" : "Inactive"
            });
        }

        clearDetails();
    }

    private void showWarehouseDetails() {
        int row = warehouseTable.getSelectedRow();
        if (row < 0) {
            clearDetails();
            return;
        }

        String code = (String) tableModel.getValueAt(row, 0);
        Warehouse w = inventoryService.getWarehouseByCode(code);

        if (w != null) {
            detailName.setText(w.getName());
            detailCode.setText(w.getCode());
            detailType.setText(w.getType());
            detailAddress.setText(w.getCity() + ", " + w.getState());
            detailManager.setText(w.getManagerName() != null ? w.getManagerName() : "Not assigned");
            detailCapacity.setText(w.getUsedCapacity() + " / " + w.getCapacity());

            int utilPercent = (int) w.getUtilizationPercent();
            utilizationBar.setValue(utilPercent);
            utilizationBar.setString(utilPercent + "%");

            if (utilPercent > 90) {
                utilizationBar.setForeground(Constants.DANGER_COLOR);
            } else if (utilPercent > 70) {
                utilizationBar.setForeground(Constants.WARNING_COLOR);
            } else {
                utilizationBar.setForeground(Constants.SUCCESS_COLOR);
            }

            editButton.setEnabled(true);
        }
    }

    private void clearDetails() {
        detailName.setText("-");
        detailCode.setText("-");
        detailType.setText("-");
        detailAddress.setText("-");
        detailManager.setText("-");
        detailCapacity.setText("-");
        utilizationBar.setValue(0);
        utilizationBar.setString("0%");
        editButton.setEnabled(false);
    }

    private void editWarehouse() {
        int row = warehouseTable.getSelectedRow();
        if (row < 0) return;

        String code = (String) tableModel.getValueAt(row, 0);
        Warehouse w = inventoryService.getWarehouseByCode(code);

        if (w != null) {
            showWarehouseDialog(w, "Edit Warehouse");
        }
    }

    private void addWarehouse() {
        showWarehouseDialog(null, "Add New Warehouse");
    }

    private void showWarehouseDialog(Warehouse existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField codeField = new JTextField(existing != null ? existing.getCode() : "");
        JTextField nameField = new JTextField(existing != null ? existing.getName() : "");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"MAIN", "DISTRIBUTION", "RETAIL", "RETURNS"});
        JTextField cityField = new JTextField(existing != null ? existing.getCity() : "");
        JTextField stateField = new JTextField(existing != null ? existing.getState() : "");
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(
            existing != null ? existing.getCapacity() : 5000, 100, 100000, 100));
        JTextField managerField = new JTextField(existing != null && existing.getManagerName() != null ? existing.getManagerName() : "");

        if (existing != null) {
            typeCombo.setSelectedItem(existing.getType());
            codeField.setEnabled(false);
        }

        formPanel.add(new JLabel("Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);
        formPanel.add(new JLabel("State:"));
        formPanel.add(stateField);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacitySpinner);
        formPanel.add(new JLabel("Manager:"));
        formPanel.add(managerField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            // Validation
            if (nameField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Name is required.");
                return;
            }

            if (existing == null && codeField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Code is required.");
                return;
            }

            // In real implementation, would save via service
            UIHelper.showSuccess(dialog, "Warehouse saved successfully.");
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

    // Utilization cell renderer
    private static class UtilizationCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.CENTER);
                String utilStr = value.toString().replace("%", "");
                try {
                    double util = Double.parseDouble(utilStr);
                    if (!isSelected) {
                        if (util > 90) {
                            setBackground(new Color(248, 215, 218));
                            setForeground(new Color(114, 28, 36));
                        } else if (util > 70) {
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                        } else {
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Use default colors
                }
            }
            return this;
        }
    }
}
