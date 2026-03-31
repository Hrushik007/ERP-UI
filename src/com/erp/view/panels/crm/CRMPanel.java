package com.erp.view.panels.crm;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * CRMPanel - Main container for CRM module with tabbed navigation.
 *
 * Contains:
 * - Leads Tab: Manage sales leads
 * - Opportunities Tab: Track sales pipeline
 * - Contacts Tab: Customer contacts management
 * - Activities Tab: Tasks, calls, meetings
 */
public class CRMPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private LeadsPanel leadsPanel;
    private OpportunitiesPanel opportunitiesPanel;
    private ContactsPanel contactsPanel;
    private ActivitiesPanel activitiesPanel;

    public CRMPanel() {
        super(Constants.MODULE_CRM);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        leadsPanel = new LeadsPanel();
        opportunitiesPanel = new OpportunitiesPanel();
        contactsPanel = new ContactsPanel();
        activitiesPanel = new ActivitiesPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Leads", leadsPanel);
        tabbedPane.addTab("Opportunities", opportunitiesPanel);
        tabbedPane.addTab("Contacts", contactsPanel);
        tabbedPane.addTab("Activities", activitiesPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: leadsPanel.refreshData(); break;
                case 1: opportunitiesPanel.refreshData(); break;
                case 2: contactsPanel.refreshData(); break;
                case 3: activitiesPanel.refreshData(); break;
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
            case 0: leadsPanel.refreshData(); break;
            case 1: opportunitiesPanel.refreshData(); break;
            case 2: contactsPanel.refreshData(); break;
            case 3: activitiesPanel.refreshData(); break;
        }
    }
}
