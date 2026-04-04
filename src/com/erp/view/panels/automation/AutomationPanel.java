package com.erp.view.panels.automation;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * AutomationPanel - Main container for Automation module with tabbed navigation.
 *
 * Contains:
 * - Workflows Tab: Manage workflow definitions and instances
 * - Approvals Tab: Handle pending approvals
 * - Notifications Tab: View and manage notifications
 * - Scheduled Tasks Tab: Configure and monitor scheduled tasks
 */
public class AutomationPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private WorkflowsPanel workflowsPanel;
    private ApprovalsPanel approvalsPanel;
    private NotificationsPanel notificationsPanel;
    private ScheduledTasksPanel scheduledTasksPanel;

    public AutomationPanel() {
        super(Constants.MODULE_AUTOMATION);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        workflowsPanel = new WorkflowsPanel();
        approvalsPanel = new ApprovalsPanel();
        notificationsPanel = new NotificationsPanel();
        scheduledTasksPanel = new ScheduledTasksPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Workflows", workflowsPanel);
        tabbedPane.addTab("Approvals", approvalsPanel);
        tabbedPane.addTab("Notifications", notificationsPanel);
        tabbedPane.addTab("Scheduled Tasks", scheduledTasksPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: workflowsPanel.refreshData(); break;
                case 1: approvalsPanel.refreshData(); break;
                case 2: notificationsPanel.refreshData(); break;
                case 3: scheduledTasksPanel.refreshData(); break;
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
            case 0: workflowsPanel.refreshData(); break;
            case 1: approvalsPanel.refreshData(); break;
            case 2: notificationsPanel.refreshData(); break;
            case 3: scheduledTasksPanel.refreshData(); break;
        }
    }
}
