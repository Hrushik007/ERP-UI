package com.erp.view.panels.sales;

import com.erp.model.Customer;
import com.erp.model.Order;
import com.erp.model.OrderItem;
import com.erp.service.mock.MockSalesService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * OrderListPanel displays all sales orders with filtering and status management.
 *
 * Features:
 * - Order table with sorting
 * - Status filtering
 * - Order details view
 * - Status update workflow
 * - Summary statistics
 */
public class OrderListPanel extends JPanel {

    private MockSalesService salesService;

    // Table components
    private JTable orderTable;
    private DefaultTableModel tableModel;

    // Filters
    private JComboBox<String> statusFilter;
    private JTextField searchField;

    // Details panel
    private JPanel detailsPanel;
    private JLabel detailOrderNumber;
    private JLabel detailCustomer;
    private JLabel detailDate;
    private JLabel detailStatus;
    private JLabel detailTotal;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;

    // Action buttons
    private JButton viewButton;
    private JButton processButton;
    private JButton shipButton;
    private JButton deliverButton;
    private JButton cancelButton;

    // Summary labels
    private JLabel totalOrdersLabel;
    private JLabel pendingLabel;
    private JLabel revenueLabel;

    private static final String[] ORDER_COLUMNS = {"Order #", "Customer", "Date", "Items", "Total", "Status"};
    private static final String[] ITEM_COLUMNS = {"Product", "SKU", "Qty", "Unit Price", "Line Total"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public OrderListPanel() {
        salesService = MockSalesService.getInstance();
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
        statusFilter = new JComboBox<>(new String[]{
            "All Statuses", "PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"
        });
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by order number or customer");
        searchField.addActionListener(e -> loadData());

        // Order table
        tableModel = new DefaultTableModel(ORDER_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(orderTable);

        // Set column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Order #
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Date
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(50);  // Items
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status

        // Status column renderer
        orderTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        // Selection listener
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onOrderSelected();
            }
        });

        // Items table (for details)
        itemsTableModel = new DefaultTableModel(ITEM_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = new JTable(itemsTableModel);
        UIHelper.styleTable(itemsTable);

        // Set column widths for items table
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Product
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // SKU
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(50);  // Qty
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Unit Price
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Line Total

        // Action buttons
        viewButton = UIHelper.createSecondaryButton("View");
        viewButton.setEnabled(false);
        viewButton.addActionListener(e -> viewOrderDetails());

        processButton = new JButton("Process");
        styleActionButton(processButton, Constants.ACCENT_COLOR);
        processButton.setEnabled(false);
        processButton.addActionListener(e -> updateStatus("PROCESSING"));

        shipButton = new JButton("Ship");
        styleActionButton(shipButton, Constants.PRIMARY_COLOR);
        shipButton.setEnabled(false);
        shipButton.addActionListener(e -> updateStatus("SHIPPED"));

        deliverButton = new JButton("Deliver");
        styleActionButton(deliverButton, Constants.SUCCESS_COLOR);
        deliverButton.setEnabled(false);
        deliverButton.addActionListener(e -> updateStatus("DELIVERED"));

        cancelButton = new JButton("Cancel");
        styleActionButton(cancelButton, Constants.DANGER_COLOR);
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelOrder());

        // Summary labels
        totalOrdersLabel = createSummaryValue("0");
        pendingLabel = createSummaryValue("0");
        revenueLabel = createSummaryValue("$0.00");

        // Details labels
        detailOrderNumber = new JLabel("-");
        detailCustomer = new JLabel("-");
        detailDate = new JLabel("-");
        detailStatus = new JLabel("-");
        detailTotal = new JLabel("-");
    }

    private void styleActionButton(JButton button, Color color) {
        button.setFont(Constants.FONT_BUTTON);
        button.setForeground(Constants.TEXT_LIGHT);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(90, 30));
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

        // Center - Split pane with table and details
        JSplitPane splitPane = createMainContent();

        // Assemble
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Orders", totalOrdersLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Pending", pendingLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Revenue", revenueLabel, Constants.SUCCESS_COLOR));

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

        // Top row - filters
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

        // Bottom row - action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(viewButton);
        actionPanel.add(processButton);
        actionPanel.add(shipButton);
        actionPanel.add(deliverButton);
        actionPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        actionPanel.add(cancelButton);

        toolbar.add(filterPanel, BorderLayout.NORTH);
        toolbar.add(actionPanel, BorderLayout.SOUTH);

        return toolbar;
    }

    private JSplitPane createMainContent() {
        // Left - Order table
        JScrollPane tableScroll = new JScrollPane(orderTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tableScroll.getViewport().setBackground(Constants.BG_WHITE);

        // Right - Order details
        detailsPanel = createDetailsPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailsPanel);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);
        detailsPanel.setMinimumSize(new Dimension(400, 0));

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

        // Header
        JLabel title = new JLabel("Order Details");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setForeground(Constants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        // Info grid - compact
        JPanel infoPanel = new JPanel(new GridLayout(3, 4, 8, 4));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_MEDIUM, 0));

        infoPanel.add(createInfoLabel("Order #:"));
        infoPanel.add(detailOrderNumber);
        infoPanel.add(createInfoLabel("Customer:"));
        infoPanel.add(detailCustomer);
        infoPanel.add(createInfoLabel("Date:"));
        infoPanel.add(detailDate);
        infoPanel.add(createInfoLabel("Status:"));
        infoPanel.add(detailStatus);
        infoPanel.add(createInfoLabel("Total:"));
        infoPanel.add(detailTotal);
        infoPanel.add(new JLabel("")); // Spacer
        infoPanel.add(new JLabel("")); // Spacer

        // Items table section
        JLabel itemsTitle = new JLabel("Order Items");
        itemsTitle.setFont(Constants.FONT_HEADING);
        itemsTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        // Configure table for auto-resize
        itemsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        itemsScroll.getViewport().setBackground(Constants.BG_WHITE);

        // Top section with header and info
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(infoPanel, BorderLayout.CENTER);
        topSection.add(itemsTitle, BorderLayout.SOUTH);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(itemsScroll, BorderLayout.CENTER);

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

        String statusSel = (String) statusFilter.getSelectedItem();
        String searchText = searchField.getText().trim().toLowerCase();

        List<Order> ordersList;
        if ("All Statuses".equals(statusSel)) {
            ordersList = salesService.getAllOrders();
        } else {
            ordersList = salesService.getOrdersByStatus(statusSel);
        }

        // Apply search filter
        if (!searchText.isEmpty()) {
            ordersList.removeIf(o -> {
                Customer c = salesService.getCustomerById(o.getCustomerId());
                String customerName = c != null ? c.getCompanyName().toLowerCase() : "";
                return !o.getOrderNumber().toLowerCase().contains(searchText) &&
                       !customerName.contains(searchText);
            });
        }

        // Sort by date descending
        ordersList.sort((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()));

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int pendingCount = 0;

        for (Order order : ordersList) {
            Customer customer = salesService.getCustomerById(order.getCustomerId());
            String customerName = customer != null ? customer.getCompanyName() : "Unknown";
            int itemCount = order.getItems() != null ? order.getItems().size() : 0;

            Object[] row = {
                order.getOrderNumber(),
                customerName,
                order.getOrderDate().format(DATE_FORMAT),
                itemCount,
                "$" + order.getTotalAmount().toString(),
                order.getStatus()
            };
            tableModel.addRow(row);

            if (!"CANCELLED".equals(order.getStatus())) {
                totalRevenue = totalRevenue.add(order.getTotalAmount());
            }
            if ("PENDING".equals(order.getStatus())) {
                pendingCount++;
            }
        }

        // Update summary
        totalOrdersLabel.setText(String.valueOf(ordersList.size()));
        pendingLabel.setText(String.valueOf(pendingCount));
        revenueLabel.setText("$" + totalRevenue.setScale(2).toString());

        // Clear details
        clearDetails();
    }

    private void onOrderSelected() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            clearDetails();
            updateButtonStates(null);
            return;
        }

        String orderNumber = (String) tableModel.getValueAt(row, 0);
        Order order = salesService.getOrderByNumber(orderNumber);

        if (order != null) {
            displayOrderDetails(order);
            updateButtonStates(order.getStatus());
        }
    }

    private void displayOrderDetails(Order order) {
        Customer customer = salesService.getCustomerById(order.getCustomerId());

        detailOrderNumber.setText(order.getOrderNumber());
        detailOrderNumber.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));

        detailCustomer.setText(customer != null ? customer.getCompanyName() : "Unknown");
        detailDate.setText(order.getOrderDate().format(DATE_FORMAT));
        detailStatus.setText(order.getStatus());
        detailStatus.setForeground(getStatusColor(order.getStatus()));

        detailTotal.setText("$" + order.getTotalAmount().toString());
        detailTotal.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        detailTotal.setForeground(Constants.SUCCESS_COLOR);

        // Load items
        itemsTableModel.setRowCount(0);
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Object[] row = {
                    item.getProductName(),
                    item.getProductSku(),
                    item.getQuantity(),
                    "$" + item.getUnitPrice().toString(),
                    "$" + item.getLineTotal().toString()
                };
                itemsTableModel.addRow(row);
            }
        }
    }

    private void clearDetails() {
        detailOrderNumber.setText("-");
        detailCustomer.setText("-");
        detailDate.setText("-");
        detailStatus.setText("-");
        detailStatus.setForeground(Constants.TEXT_PRIMARY);
        detailTotal.setText("-");
        detailTotal.setForeground(Constants.TEXT_PRIMARY);
        itemsTableModel.setRowCount(0);
    }

    private void updateButtonStates(String status) {
        viewButton.setEnabled(status != null);

        if (status == null) {
            processButton.setEnabled(false);
            shipButton.setEnabled(false);
            deliverButton.setEnabled(false);
            cancelButton.setEnabled(false);
            return;
        }

        processButton.setEnabled("PENDING".equals(status) || "CONFIRMED".equals(status));
        shipButton.setEnabled("PROCESSING".equals(status));
        deliverButton.setEnabled("SHIPPED".equals(status));
        cancelButton.setEnabled(!"DELIVERED".equals(status) && !"CANCELLED".equals(status));
    }

    private void updateStatus(String newStatus) {
        int row = orderTable.getSelectedRow();
        if (row < 0) return;

        String orderNumber = (String) tableModel.getValueAt(row, 0);
        Order order = salesService.getOrderByNumber(orderNumber);

        if (order != null) {
            salesService.updateOrderStatus(order.getOrderId(), newStatus);
            loadData();
            UIHelper.showSuccess(this, "Order status updated to " + newStatus);
        }
    }

    private void cancelOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) return;

        String orderNumber = (String) tableModel.getValueAt(row, 0);
        Order order = salesService.getOrderByNumber(orderNumber);

        if (order != null) {
            String reason = JOptionPane.showInputDialog(this, "Cancellation reason:");
            if (reason != null && !reason.trim().isEmpty()) {
                salesService.cancelOrder(order.getOrderId(), reason);
                loadData();
                UIHelper.showSuccess(this, "Order cancelled");
            }
        }
    }

    private void viewOrderDetails() {
        // Details are already shown in the split pane
        UIHelper.showSuccess(this, "Order details shown on the right panel");
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "PENDING": return Constants.WARNING_COLOR;
            case "CONFIRMED": return Constants.PRIMARY_COLOR;
            case "PROCESSING": return Constants.ACCENT_COLOR;
            case "SHIPPED": return new Color(52, 152, 219);
            case "DELIVERED": return Constants.SUCCESS_COLOR;
            case "CANCELLED": return Constants.DANGER_COLOR;
            default: return Constants.TEXT_PRIMARY;
        }
    }

    public void refreshData() {
        loadData();
    }

    /**
     * Custom cell renderer for status column.
     */
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected && value != null) {
                c.setForeground(getStatusColor(value.toString()));
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }
}
