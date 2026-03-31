package com.erp.view.panels.manufacturing;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * ManufacturingPanel - Main container for Manufacturing module with tabbed navigation.
 *
 * Contains:
 * - Work Orders Tab: Production order management
 * - BOM Tab: Bill of materials management
 * - Production Schedule Tab: Planning and scheduling
 */
public class ManufacturingPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private WorkOrdersPanel workOrdersPanel;
    private BOMPanel bomPanel;
    private ProductionSchedulePanel productionSchedulePanel;

    public ManufacturingPanel() {
        super(Constants.MODULE_MANUFACTURING);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        workOrdersPanel = new WorkOrdersPanel();
        bomPanel = new BOMPanel();
        productionSchedulePanel = new ProductionSchedulePanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Work Orders", workOrdersPanel);
        tabbedPane.addTab("Bill of Materials", bomPanel);
        tabbedPane.addTab("Production Schedule", productionSchedulePanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: workOrdersPanel.refreshData(); break;
                case 1: bomPanel.refreshData(); break;
                case 2: productionSchedulePanel.refreshData(); break;
            }
        });
    }

    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        int index = tabbedPane.getSelectedIndex();
        switch (index) {
            case 0: workOrdersPanel.refreshData(); break;
            case 1: bomPanel.refreshData(); break;
            case 2: productionSchedulePanel.refreshData(); break;
        }
    }
}
