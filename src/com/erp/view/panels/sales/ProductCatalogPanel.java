package com.erp.view.panels.sales;

import com.erp.model.Product;
import com.erp.service.mock.MockSalesService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ProductCatalogPanel displays product catalog for reference during order creation.
 *
 * Features:
 * - Product search
 * - Category filtering
 * - Stock status display
 * - Product details view
 */
public class ProductCatalogPanel extends JPanel {

    private MockSalesService salesService;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;

    // Product details
    private JLabel detailName;
    private JLabel detailSku;
    private JLabel detailCategory;
    private JLabel detailPrice;
    private JLabel detailCost;
    private JLabel detailMargin;
    private JLabel detailStock;
    private JLabel detailStatus;

    private static final String[] COLUMNS = {"SKU", "Name", "Category", "Price", "Stock", "Status"};

    public ProductCatalogPanel() {
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
        searchField = UIHelper.createTextField(20);
        searchField.setToolTipText("Search by name or SKU");
        searchField.addActionListener(e -> loadData());

        categoryFilter = new JComboBox<>();
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addItem("All Categories");
        // Categories will be populated from data
        categoryFilter.addActionListener(e -> loadData());

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(productTable);

        // Stock column renderer
        productTable.getColumnModel().getColumn(4).setCellRenderer(new StockCellRenderer());

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showProductDetails();
            }
        });

        // Detail labels
        detailName = new JLabel("-");
        detailSku = new JLabel("-");
        detailCategory = new JLabel("-");
        detailPrice = new JLabel("-");
        detailCost = new JLabel("-");
        detailMargin = new JLabel("-");
        detailStock = new JLabel("-");
        detailStatus = new JLabel("-");

        // Populate categories
        populateCategories();
    }

    private void populateCategories() {
        java.util.Set<String> categories = new java.util.HashSet<>();
        for (Product p : salesService.getAllProducts()) {
            if (p.getCategory() != null) {
                categories.add(p.getCategory());
            }
        }
        for (String cat : categories) {
            categoryFilter.addItem(cat);
        }
    }

    private void layoutComponents() {
        // Top toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        toolbar.add(new JLabel("Category:"));
        toolbar.add(categoryFilter);

        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> loadData());
        toolbar.add(searchBtn);

        // Table
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Details panel
        JPanel detailsPanel = createDetailsPanel();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, detailsPanel);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.7);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel title = new JLabel("Product Details");
        title.setFont(Constants.FONT_SUBTITLE);

        JPanel infoGrid = new JPanel(new GridLayout(8, 2, 10, 10));
        infoGrid.setOpaque(false);
        infoGrid.setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, 0, 0, 0));

        infoGrid.add(createLabel("Name:"));
        infoGrid.add(detailName);
        infoGrid.add(createLabel("SKU:"));
        infoGrid.add(detailSku);
        infoGrid.add(createLabel("Category:"));
        infoGrid.add(detailCategory);
        infoGrid.add(createLabel("Unit Price:"));
        infoGrid.add(detailPrice);
        infoGrid.add(createLabel("Cost Price:"));
        infoGrid.add(detailCost);
        infoGrid.add(createLabel("Margin:"));
        infoGrid.add(detailMargin);
        infoGrid.add(createLabel("In Stock:"));
        infoGrid.add(detailStock);
        infoGrid.add(createLabel("Status:"));
        infoGrid.add(detailStatus);

        panel.add(title, BorderLayout.NORTH);
        panel.add(infoGrid, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        label.setForeground(Constants.TEXT_SECONDARY);
        return label;
    }

    private void loadData() {
        tableModel.setRowCount(0);

        String searchText = searchField.getText().trim();
        String category = (String) categoryFilter.getSelectedItem();

        List<Product> products;

        if (!searchText.isEmpty()) {
            products = salesService.searchProducts(searchText);
        } else if (!"All Categories".equals(category)) {
            products = salesService.getProductsByCategory(category);
        } else {
            products = salesService.getAllProducts();
        }

        // Apply category filter if search was used
        if (!searchText.isEmpty() && !"All Categories".equals(category)) {
            products.removeIf(p -> !category.equals(p.getCategory()));
        }

        for (Product p : products) {
            Object[] row = {
                p.getSku(),
                p.getName(),
                p.getCategory(),
                "$" + p.getUnitPrice().toString(),
                p.getQuantityInStock(),
                p.getStatus()
            };
            tableModel.addRow(row);
        }

        clearDetails();
    }

    private void showProductDetails() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            clearDetails();
            return;
        }

        String sku = (String) tableModel.getValueAt(row, 0);
        Product product = salesService.getProductBySku(sku);

        if (product != null) {
            detailName.setText(product.getName());
            detailName.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));

            detailSku.setText(product.getSku());
            detailCategory.setText(product.getCategory() != null ? product.getCategory() : "-");

            detailPrice.setText("$" + product.getUnitPrice().toString());
            detailPrice.setForeground(Constants.SUCCESS_COLOR);
            detailPrice.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));

            detailCost.setText(product.getCostPrice() != null ? "$" + product.getCostPrice().toString() : "-");

            if (product.getUnitPrice() != null && product.getCostPrice() != null) {
                detailMargin.setText(product.getMarginPercent().setScale(1, java.math.RoundingMode.HALF_UP) + "%");
            } else {
                detailMargin.setText("-");
            }

            int stock = product.getQuantityInStock();
            detailStock.setText(String.valueOf(stock));
            if (stock <= product.getReorderLevel()) {
                detailStock.setForeground(Constants.DANGER_COLOR);
            } else if (stock <= product.getReorderLevel() * 2) {
                detailStock.setForeground(Constants.WARNING_COLOR);
            } else {
                detailStock.setForeground(Constants.SUCCESS_COLOR);
            }

            detailStatus.setText(product.getStatus());
        }
    }

    private void clearDetails() {
        detailName.setText("-");
        detailSku.setText("-");
        detailCategory.setText("-");
        detailPrice.setText("-");
        detailPrice.setForeground(Constants.TEXT_PRIMARY);
        detailCost.setText("-");
        detailMargin.setText("-");
        detailStock.setText("-");
        detailStock.setForeground(Constants.TEXT_PRIMARY);
        detailStatus.setText("-");
    }

    public void refreshData() {
        loadData();
    }

    /**
     * Custom renderer for stock column - shows color based on stock level.
     */
    private class StockCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected && value != null) {
                int stock = (Integer) value;
                if (stock <= 10) {
                    c.setForeground(Constants.DANGER_COLOR);
                } else if (stock <= 25) {
                    c.setForeground(Constants.WARNING_COLOR);
                } else {
                    c.setForeground(Constants.SUCCESS_COLOR);
                }
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }
}
