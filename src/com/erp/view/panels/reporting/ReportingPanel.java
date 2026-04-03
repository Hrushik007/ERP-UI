package com.erp.view.panels.reporting;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Reporting Panel containing tabs for Reports, Templates, and Scheduled Reports.
 */
public class ReportingPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private ReportsPanel reportsPanel;
    private ReportTemplatesPanel templatesPanel;
    private ScheduledReportsPanel scheduledReportsPanel;

    public ReportingPanel() {
        super("Reporting");
    }

    @Override
    protected void initializeComponents() {
        reportsPanel = new ReportsPanel();
        templatesPanel = new ReportTemplatesPanel();
        scheduledReportsPanel = new ScheduledReportsPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        tabbedPane.addTab("Reports", reportsPanel);
        tabbedPane.addTab("Templates", templatesPanel);
        tabbedPane.addTab("Scheduled Reports", scheduledReportsPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    reportsPanel.refreshData();
                    break;
                case 1:
                    templatesPanel.refreshData();
                    break;
                case 2:
                    scheduledReportsPanel.refreshData();
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
                reportsPanel.refreshData();
                break;
            case 1:
                templatesPanel.refreshData();
                break;
            case 2:
                scheduledReportsPanel.refreshData();
                break;
        }
    }
}
