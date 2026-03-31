package com.erp.view.panels.inventory;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * InventoryPanel is the main container for the Inventory Management module.
 *
 * This panel uses a JTabbedPane to organize:
 * - Stock Levels: View current inventory across warehouses
 * - Movements: Track inbound/outbound stock transactions
 * - Warehouses: Manage warehouse locations
 * - Alerts: View reorder and stock alerts
 */
public class InventoryPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private StockLevelsPanel stockLevelsPanel;
    private StockMovementsPanel stockMovementsPanel;
    private WarehousePanel warehousePanel;

    public InventoryPanel() {
        super(Constants.MODULE_INVENTORY);
    }

    @Override
    protected void initializeComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Create tab panels
        stockLevelsPanel = new StockLevelsPanel();
        stockMovementsPanel = new StockMovementsPanel();
        warehousePanel = new WarehousePanel();

        // Add tabs
        tabbedPane.addTab("Stock Levels", null, stockLevelsPanel, "View current inventory levels");
        tabbedPane.addTab("Movements", null, stockMovementsPanel, "Track stock movements");
        tabbedPane.addTab("Warehouses", null, warehousePanel, "Manage warehouses");

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        Component selected = tabbedPane.getSelectedComponent();
        if (selected == stockLevelsPanel) {
            stockLevelsPanel.refreshData();
        } else if (selected == stockMovementsPanel) {
            stockMovementsPanel.refreshData();
        } else if (selected == warehousePanel) {
            warehousePanel.refreshData();
        }
    }
}
