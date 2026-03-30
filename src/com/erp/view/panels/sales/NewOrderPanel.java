package com.erp.view.panels.sales;

import com.erp.model.Customer;
import com.erp.model.Order;
import com.erp.model.OrderItem;
import com.erp.model.Product;
import com.erp.service.mock.MockSalesService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * NewOrderPanel provides a form to create new sales orders.
 *
 * Features:
 * - Customer selection
 * - Product search and add
 * - Line items management
 * - Real-time total calculation
 * - Order submission
 */
public class NewOrderPanel extends JPanel {

    private MockSalesService salesService;
    private Runnable onOrderCreated;

    // Customer selection
    private JComboBox<String> customerCombo;
    private JLabel customerInfo;

    // Product selection
    private JComboBox<String> productCombo;
    private JSpinner quantitySpinner;
    private JLabel productPrice;
    private JButton addItemButton;

    // Order items table
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private JButton removeItemButton;

    // Totals
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;

    // Actions
    private JButton clearButton;
    private JButton submitButton;

    // Data
    private List<OrderItem> orderItems;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal taxRate = new BigDecimal("0.08");

    private static final String[] ITEM_COLUMNS = {"Product", "SKU", "Qty", "Unit Price", "Line Total", "Remove"};

    public NewOrderPanel(Runnable onOrderCreated) {
        this.onOrderCreated = onOrderCreated;
        this.salesService = MockSalesService.getInstance();
        this.orderItems = new ArrayList<>();

        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadComboData();
    }

    private void initializeComponents() {
        // Customer combo
        customerCombo = new JComboBox<>();
        customerCombo.setFont(Constants.FONT_REGULAR);
        customerCombo.addActionListener(e -> updateCustomerInfo());

        customerInfo = new JLabel(" ");
        customerInfo.setFont(Constants.FONT_SMALL);
        customerInfo.setForeground(Constants.TEXT_SECONDARY);

        // Product combo
        productCombo = new JComboBox<>();
        productCombo.setFont(Constants.FONT_REGULAR);
        productCombo.addActionListener(e -> updateProductPrice());

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setFont(Constants.FONT_REGULAR);
        ((JSpinner.DefaultEditor) quantitySpinner.getEditor()).getTextField().setColumns(5);

        productPrice = new JLabel("$0.00");
        productPrice.setFont(Constants.FONT_REGULAR);

        addItemButton = UIHelper.createPrimaryButton("Add Item");
        addItemButton.addActionListener(e -> addItem());

        // Items table
        itemsTableModel = new DefaultTableModel(ITEM_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = new JTable(itemsTableModel);
        UIHelper.styleTable(itemsTable);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Hide the "Remove" column data but use it for identification
        itemsTable.getColumnModel().getColumn(5).setMinWidth(0);
        itemsTable.getColumnModel().getColumn(5).setMaxWidth(0);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(0);

        removeItemButton = new JButton("Remove Selected");
        removeItemButton.setFont(Constants.FONT_BUTTON);
        removeItemButton.setForeground(Constants.DANGER_COLOR);
        removeItemButton.setBackground(Constants.BG_WHITE);
        removeItemButton.setBorder(BorderFactory.createLineBorder(Constants.DANGER_COLOR, 1));
        removeItemButton.addActionListener(e -> removeSelectedItem());

        // Total labels
        subtotalLabel = createTotalLabel("$0.00");
        taxLabel = createTotalLabel("$0.00");
        totalLabel = createTotalLabel("$0.00");
        totalLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        totalLabel.setForeground(Constants.SUCCESS_COLOR);

        // Action buttons
        clearButton = UIHelper.createSecondaryButton("Clear All");
        clearButton.addActionListener(e -> clearOrder());

        submitButton = UIHelper.createPrimaryButton("Submit Order");
        submitButton.setPreferredSize(new Dimension(140, 40));
        submitButton.addActionListener(e -> submitOrder());
    }

    private JLabel createTotalLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        label.setForeground(Constants.TEXT_PRIMARY);
        return label;
    }

    private void layoutComponents() {
        // Left side - Customer and Product selection
        JPanel leftPanel = createSelectionPanel();

        // Center - Items table
        JPanel centerPanel = createItemsPanel();

        // Right side - Totals and submit
        JPanel rightPanel = createTotalsPanel();

        // Main layout
        JPanel topPanel = new JPanel(new BorderLayout(Constants.PADDING_LARGE, 0));
        topPanel.setOpaque(false);
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));
        panel.setPreferredSize(new Dimension(400, 250));

        // Customer section
        JLabel customerTitle = new JLabel("Select Customer");
        customerTitle.setFont(Constants.FONT_SUBTITLE);
        customerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        customerCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        customerCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Product section
        JLabel productTitle = new JLabel("Add Product");
        productTitle.setFont(Constants.FONT_SUBTITLE);
        productTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        productCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        productCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qtyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        qtyPanel.add(new JLabel("Qty:"));
        qtyPanel.add(quantitySpinner);
        qtyPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        qtyPanel.add(new JLabel("Price:"));
        qtyPanel.add(productPrice);

        addItemButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addItemButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Assemble
        panel.add(customerTitle);
        panel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        panel.add(customerCombo);
        panel.add(Box.createVerticalStrut(3));
        panel.add(customerInfo);
        panel.add(Box.createVerticalStrut(Constants.PADDING_LARGE));
        panel.add(productTitle);
        panel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        panel.add(productCombo);
        panel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        panel.add(qtyPanel);
        panel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        panel.add(addItemButton);

        return panel;
    }

    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, 0, 0, 0));

        JLabel title = new JLabel("Order Items");
        title.setFont(Constants.FONT_SUBTITLE);

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(removeItemButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));
        panel.setPreferredSize(new Dimension(250, 250));

        JLabel title = new JLabel("Order Summary");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Totals grid
        JPanel totalsGrid = new JPanel(new GridLayout(4, 2, 10, 10));
        totalsGrid.setOpaque(false);
        totalsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        totalsGrid.add(new JLabel("Subtotal:"));
        totalsGrid.add(subtotalLabel);
        totalsGrid.add(new JLabel("Tax (8%):"));
        totalsGrid.add(taxLabel);
        totalsGrid.add(new JLabel("Total:"));
        totalsGrid.add(totalLabel);
        totalsGrid.add(new JLabel("")); // spacer
        totalsGrid.add(new JLabel("")); // spacer

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, Constants.PADDING_SMALL));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        panel.add(title);
        panel.add(Box.createVerticalStrut(Constants.PADDING_LARGE));
        panel.add(totalsGrid);
        panel.add(Box.createVerticalGlue());
        panel.add(buttonPanel);

        return panel;
    }

    private void loadComboData() {
        // Load customers
        customerCombo.removeAllItems();
        customerCombo.addItem("-- Select Customer --");
        for (Customer c : salesService.getAllCustomers()) {
            customerCombo.addItem(c.getCustomerId() + " - " + c.getCompanyName());
        }

        // Load products
        productCombo.removeAllItems();
        productCombo.addItem("-- Select Product --");
        for (Product p : salesService.getAvailableProducts()) {
            productCombo.addItem(p.getProductId() + " - " + p.getName() + " ($" + p.getUnitPrice() + ")");
        }
    }

    private void updateCustomerInfo() {
        String selected = (String) customerCombo.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            customerInfo.setText(" ");
            return;
        }

        int customerId = Integer.parseInt(selected.split(" - ")[0]);
        Customer customer = salesService.getCustomerById(customerId);
        if (customer != null) {
            customerInfo.setText(customer.getContactName() + " | " + customer.getEmail() +
                               " | " + customer.getCustomerType());
        }
    }

    private void updateProductPrice() {
        String selected = (String) productCombo.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            productPrice.setText("$0.00");
            return;
        }

        int productId = Integer.parseInt(selected.split(" - ")[0]);
        Product product = salesService.getProductById(productId);
        if (product != null) {
            productPrice.setText("$" + product.getUnitPrice().toString());
        }
    }

    private void addItem() {
        String productSel = (String) productCombo.getSelectedItem();
        if (productSel == null || productSel.startsWith("--")) {
            UIHelper.showError(this, "Please select a product");
            return;
        }

        int productId = Integer.parseInt(productSel.split(" - ")[0]);
        Product product = salesService.getProductById(productId);
        int quantity = (Integer) quantitySpinner.getValue();

        if (product == null) {
            UIHelper.showError(this, "Product not found");
            return;
        }

        if (quantity > product.getQuantityInStock()) {
            UIHelper.showError(this, "Not enough stock. Available: " + product.getQuantityInStock());
            return;
        }

        // Check if product already in order
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getProductId() == productId) {
                // Update existing item
                OrderItem existing = orderItems.get(i);
                existing.setQuantity(existing.getQuantity() + quantity);
                existing.calculateLineTotal();
                refreshItemsTable();
                calculateTotals();
                return;
            }
        }

        // Add new item
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setProductName(product.getName());
        item.setProductSku(product.getSku());
        item.setQuantity(quantity);
        item.setUnitPrice(product.getUnitPrice());
        item.calculateLineTotal();

        orderItems.add(item);
        refreshItemsTable();
        calculateTotals();

        // Reset quantity
        quantitySpinner.setValue(1);
    }

    private void refreshItemsTable() {
        itemsTableModel.setRowCount(0);
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            Object[] row = {
                item.getProductName(),
                item.getProductSku(),
                item.getQuantity(),
                "$" + item.getUnitPrice().toString(),
                "$" + item.getLineTotal().toString(),
                i // hidden index for removal
            };
            itemsTableModel.addRow(row);
        }
    }

    private void removeSelectedItem() {
        int row = itemsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an item to remove");
            return;
        }

        orderItems.remove(row);
        refreshItemsTable();
        calculateTotals();
    }

    private void calculateTotals() {
        subtotal = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            subtotal = subtotal.add(item.getLineTotal());
        }

        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        subtotalLabel.setText("$" + subtotal.setScale(2, RoundingMode.HALF_UP).toString());
        taxLabel.setText("$" + tax.toString());
        totalLabel.setText("$" + total.setScale(2, RoundingMode.HALF_UP).toString());
    }

    private void clearOrder() {
        orderItems.clear();
        refreshItemsTable();
        calculateTotals();
        customerCombo.setSelectedIndex(0);
        productCombo.setSelectedIndex(0);
        quantitySpinner.setValue(1);
    }

    private void submitOrder() {
        // Validate
        String customerSel = (String) customerCombo.getSelectedItem();
        if (customerSel == null || customerSel.startsWith("--")) {
            UIHelper.showError(this, "Please select a customer");
            return;
        }

        if (orderItems.isEmpty()) {
            UIHelper.showError(this, "Please add at least one item");
            return;
        }

        int customerId = Integer.parseInt(customerSel.split(" - ")[0]);

        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setItems(new ArrayList<>(orderItems));
        order.setStatus("PENDING");

        Order created = salesService.createOrder(order);
        if (created != null) {
            UIHelper.showSuccess(this, "Order " + created.getOrderNumber() + " created successfully!");
            clearOrder();
            if (onOrderCreated != null) {
                onOrderCreated.run();
            }
        } else {
            UIHelper.showError(this, "Failed to create order");
        }
    }

    public void refreshData() {
        loadComboData();
    }
}
