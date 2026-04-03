package com.erp.view.panels.analytics;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main Analytics Panel containing tabs for Dashboards, KPIs, and Charts.
 */
public class AnalyticsPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private DashboardsPanel dashboardsPanel;
    private KPIsPanel kpisPanel;
    private ChartsPanel chartsPanel;

    public AnalyticsPanel() {
        super("Analytics");
    }

    @Override
    protected void initializeComponents() {
        dashboardsPanel = new DashboardsPanel();
        kpisPanel = new KPIsPanel();
        chartsPanel = new ChartsPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        tabbedPane.addTab("Dashboards", dashboardsPanel);
        tabbedPane.addTab("KPIs", kpisPanel);
        tabbedPane.addTab("Charts", chartsPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    dashboardsPanel.refreshData();
                    break;
                case 1:
                    kpisPanel.refreshData();
                    break;
                case 2:
                    chartsPanel.refreshData();
                    break;
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
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                dashboardsPanel.refreshData();
                break;
            case 1:
                kpisPanel.refreshData();
                break;
            case 2:
                chartsPanel.refreshData();
                break;
        }
    }
}
