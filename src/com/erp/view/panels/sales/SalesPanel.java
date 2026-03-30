package com.erp.view.panels.sales;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * SalesPanel is the main container for the Sales & Order Processing module.
 *
 * This panel uses a JTabbedPane to organize:
 * - Orders: View and manage all orders
 * - New Order: Create new sales orders
 * - Customers: Quick customer reference
 * - Products: Quick product reference
 */
public class SalesPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private OrderListPanel orderListPanel;
    private NewOrderPanel newOrderPanel;
    private CustomerListPanel customerListPanel;
    private ProductCatalogPanel productCatalogPanel;

    public SalesPanel() {
        super(Constants.MODULE_SALES);
    }

    @Override
    protected void initializeComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Create tab panels
        orderListPanel = new OrderListPanel();
        newOrderPanel = new NewOrderPanel(this::onOrderCreated);
        customerListPanel = new CustomerListPanel();
        productCatalogPanel = new ProductCatalogPanel();

        // Add tabs
        tabbedPane.addTab("Orders", null, orderListPanel, "View and manage orders");
        tabbedPane.addTab("New Order", null, newOrderPanel, "Create a new order");
        tabbedPane.addTab("Customers", null, customerListPanel, "Customer directory");
        tabbedPane.addTab("Products", null, productCatalogPanel, "Product catalog");

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Callback when a new order is created - switch to Orders tab and refresh.
     */
    private void onOrderCreated() {
        orderListPanel.refreshData();
        tabbedPane.setSelectedIndex(0); // Switch to Orders tab
    }

    @Override
    public void refreshData() {
        Component selected = tabbedPane.getSelectedComponent();
        if (selected == orderListPanel) {
            orderListPanel.refreshData();
        } else if (selected == newOrderPanel) {
            newOrderPanel.refreshData();
        } else if (selected == customerListPanel) {
            customerListPanel.refreshData();
        } else if (selected == productCatalogPanel) {
            productCatalogPanel.refreshData();
        }
    }
}
