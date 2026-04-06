package com.erp.view.panels.integration;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * IntegrationPanel - Main container for Integration module with tabbed navigation.
 *
 * Contains:
 * - Data Sync Tab: Manage data synchronization between modules
 * - External Integrations Tab: Configure external API connections
 * - Import/Export Tab: Data import and export operations
 * - Health & Logs Tab: Monitor integration health and view logs
 */
public class IntegrationPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private DataSyncPanel dataSyncPanel;
    private ExternalIntegrationsPanel externalIntegrationsPanel;
    private ImportExportPanel importExportPanel;
    private HealthMonitorPanel healthMonitorPanel;

    public IntegrationPanel() {
        super(Constants.MODULE_INTEGRATION);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        dataSyncPanel = new DataSyncPanel();
        externalIntegrationsPanel = new ExternalIntegrationsPanel();
        importExportPanel = new ImportExportPanel();
        healthMonitorPanel = new HealthMonitorPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Data Sync", dataSyncPanel);
        tabbedPane.addTab("External Integrations", externalIntegrationsPanel);
        tabbedPane.addTab("Import / Export", importExportPanel);
        tabbedPane.addTab("Health & Logs", healthMonitorPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: dataSyncPanel.refreshData(); break;
                case 1: externalIntegrationsPanel.refreshData(); break;
                case 2: importExportPanel.refreshData(); break;
                case 3: healthMonitorPanel.refreshData(); break;
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
            case 0: dataSyncPanel.refreshData(); break;
            case 1: externalIntegrationsPanel.refreshData(); break;
            case 2: importExportPanel.refreshData(); break;
            case 3: healthMonitorPanel.refreshData(); break;
        }
    }
}
