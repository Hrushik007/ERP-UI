package com.erp.view.panels.manufacturing;

import com.erp.model.WorkOrder;
import com.erp.service.mock.MockManufacturingService;
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
 * WorkOrdersPanel displays and manages work orders.
 */
public class WorkOrdersPanel extends JPanel {

    private MockManufacturingService manufacturingService;

    private JTable workOrdersTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> statusFilter;
    private JTextField searchField;

    // Summary labels
    private JLabel totalOrdersLabel;
    private JLabel inProgressLabel;
    private JLabel completedLabel;
    private JLabel overdueLabel;

    private JButton editButton;
    private JButton startButton;
    private JButton completeButton;
    private JButton releaseButton;

    private static final String[] COLUMNS = {"WO Number", "Product", "Quantity", "Completed", "Status", "Priority", "Start Date", "End Date", "Progress"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public WorkOrdersPanel() {
        manufacturingService = MockManufacturingService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "PLANNED", "RELEASED", "IN_PROGRESS", "COMPLETED", "CANCELLED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by WO number or product");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        workOrdersTable = new JTable(tableModel);
        workOrdersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(workOrdersTable);

        // Column widths
        workOrdersTable.getColumnModel().getColumn(0).setPreferredWidth(120); // WO Number
        workOrdersTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Product
        workOrdersTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        workOrdersTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Completed
        workOrdersTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        workOrdersTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Priority
        workOrdersTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Start Date
        workOrdersTable.getColumnModel().getColumn(7).setPreferredWidth(100); // End Date
        workOrdersTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Progress

        // Status column renderer
        workOrdersTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        // Priority column renderer
        workOrdersTable.getColumnModel().getColumn(5).setCellRenderer(new PriorityCellRenderer());

        workOrdersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Summary labels
        totalOrdersLabel = createSummaryValue("0");
        inProgressLabel = createSummaryValue("0");
        completedLabel = createSummaryValue("0");
        overdueLabel = createSummaryValue("0");

        // Buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editWorkOrder());

        releaseButton = new JButton("Release");
        releaseButton.setFont(Constants.FONT_BUTTON);
        releaseButton.setBackground(new Color(23, 162, 184));
        releaseButton.setForeground(Color.WHITE);
        releaseButton.setOpaque(true);
        releaseButton.setBorderPainted(false);
        releaseButton.setFocusPainted(false);
        releaseButton.setPreferredSize(new Dimension(90, 30));
        releaseButton.setEnabled(false);
        releaseButton.addActionListener(e -> releaseWorkOrder());

        startButton = new JButton("Start");
        startButton.setFont(Constants.FONT_BUTTON);
        startButton.setBackground(Constants.PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(80, 30));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startWorkOrder());

        completeButton = new JButton("Complete");
        completeButton.setFont(Constants.FONT_BUTTON);
        completeButton.setBackground(Constants.SUCCESS_COLOR);
        completeButton.setForeground(Color.WHITE);
        completeButton.setOpaque(true);
        completeButton.setBorderPainted(false);
        completeButton.setFocusPainted(false);
        completeButton.setPreferredSize(new Dimension(100, 30));
        completeButton.setEnabled(false);
        completeButton.addActionListener(e -> completeWorkOrder());
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
        JScrollPane scrollPane = new JScrollPane(workOrdersTable);
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

        panel.add(createSummaryCard("Total Orders", totalOrdersLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("In Progress", inProgressLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Completed", completedLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Overdue", overdueLabel, Constants.DANGER_COLOR));

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

        JButton addBtn = UIHelper.createPrimaryButton("Create Work Order");
        addBtn.setPreferredSize(new Dimension(140, 30));
        addBtn.addActionListener(e -> createWorkOrder());
        actionPanel.add(addBtn);

        actionPanel.add(editButton);
        actionPanel.add(releaseButton);
        actionPanel.add(startButton);
        actionPanel.add(completeButton);

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

        List<WorkOrder> workOrders = manufacturingService.getAllWorkOrders();
        String statusSelection = (String) statusFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        int totalCount = 0;
        int inProgressCount = 0;
        int completedCount = 0;
        int overdueCount = 0;

        for (WorkOrder wo : workOrders) {
            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(wo.getStatus())) {
                continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = wo.getWorkOrderNumber().toLowerCase().contains(searchTerm) ||
                                  (wo.getProductName() != null && wo.getProductName().toLowerCase().contains(searchTerm));
                if (!matches) continue;
            }

            totalCount++;
            if ("IN_PROGRESS".equals(wo.getStatus())) inProgressCount++;
            if ("COMPLETED".equals(wo.getStatus())) completedCount++;
            if (wo.isOverdue()) overdueCount++;

            tableModel.addRow(new Object[]{
                wo.getWorkOrderNumber(),
                wo.getProductName(),
                wo.getQuantity(),
                wo.getQuantityCompleted(),
                wo.getStatus(),
                wo.getPriority(),
                wo.getScheduledStartDate() != null ? wo.getScheduledStartDate().format(DATE_FORMAT) : "",
                wo.getScheduledEndDate() != null ? wo.getScheduledEndDate().format(DATE_FORMAT) : "",
                (int)wo.getCompletionPercentage() + "%"
            });
        }

        totalOrdersLabel.setText(String.valueOf(totalCount));
        inProgressLabel.setText(String.valueOf(inProgressCount));
        completedLabel.setText(String.valueOf(completedCount));
        overdueLabel.setText(String.valueOf(overdueCount));

        updateButtonStates();
    }

    private void updateButtonStates() {
        int row = workOrdersTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);

        if (hasSelection) {
            String status = (String) tableModel.getValueAt(row, 4);
            releaseButton.setEnabled("PLANNED".equals(status));
            startButton.setEnabled("PLANNED".equals(status) || "RELEASED".equals(status));
            completeButton.setEnabled("IN_PROGRESS".equals(status));
        } else {
            releaseButton.setEnabled(false);
            startButton.setEnabled(false);
            completeButton.setEnabled(false);
        }
    }

    private void createWorkOrder() {
        UIHelper.showSuccess(this, "Create Work Order functionality will be implemented with full form.");
    }

    private void editWorkOrder() {
        UIHelper.showSuccess(this, "Edit Work Order functionality will be implemented with full form.");
    }

    private void releaseWorkOrder() {
        int row = workOrdersTable.getSelectedRow();
        if (row < 0) return;

        String woNumber = (String) tableModel.getValueAt(row, 0);
        WorkOrder wo = manufacturingService.getWorkOrderByNumber(woNumber);

        if (wo != null && manufacturingService.releaseWorkOrder(wo.getWorkOrderId())) {
            UIHelper.showSuccess(this, "Work order released successfully.");
            loadData();
        }
    }

    private void startWorkOrder() {
        int row = workOrdersTable.getSelectedRow();
        if (row < 0) return;

        String woNumber = (String) tableModel.getValueAt(row, 0);
        WorkOrder wo = manufacturingService.getWorkOrderByNumber(woNumber);

        if (wo != null && manufacturingService.startWorkOrder(wo.getWorkOrderId())) {
            UIHelper.showSuccess(this, "Work order started.");
            loadData();
        }
    }

    private void completeWorkOrder() {
        int row = workOrdersTable.getSelectedRow();
        if (row < 0) return;

        String woNumber = (String) tableModel.getValueAt(row, 0);
        WorkOrder wo = manufacturingService.getWorkOrderByNumber(woNumber);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Mark work order '" + woNumber + "' as completed?",
            "Confirm Completion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION && wo != null) {
            if (manufacturingService.completeWorkOrder(wo.getWorkOrderId())) {
                UIHelper.showSuccess(this, "Work order completed.");
                loadData();
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
                        case "PLANNED":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "RELEASED":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "IN_PROGRESS":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
                            break;
                        case "COMPLETED":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "CANCELLED":
                            setBackground(new Color(248, 215, 218));
                            setForeground(new Color(114, 28, 36));
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

    // Priority cell renderer
    private static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String priority = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (priority) {
                        case "LOW":
                            setBackground(new Color(230, 230, 230));
                            setForeground(new Color(80, 80, 80));
                            break;
                        case "MEDIUM":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "HIGH":
                            setBackground(new Color(254, 226, 226));
                            setForeground(new Color(153, 27, 27));
                            break;
                        case "URGENT":
                            setBackground(new Color(248, 215, 218));
                            setForeground(new Color(114, 28, 36));
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
