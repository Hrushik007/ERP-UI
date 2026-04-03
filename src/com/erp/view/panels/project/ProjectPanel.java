package com.erp.view.panels.project;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * ProjectPanel - Main container for Project Management module with tabbed navigation.
 *
 * Contains:
 * - Projects Tab: Project listing and management
 * - Tasks Tab: Task management across projects
 * - Milestones Tab: Project milestones tracking
 */
public class ProjectPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private ProjectsPanel projectsPanel;
    private TasksPanel tasksPanel;
    private MilestonesPanel milestonesPanel;

    public ProjectPanel() {
        super(Constants.MODULE_PROJECT);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        projectsPanel = new ProjectsPanel();
        tasksPanel = new TasksPanel();
        milestonesPanel = new MilestonesPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Projects", projectsPanel);
        tabbedPane.addTab("Tasks", tasksPanel);
        tabbedPane.addTab("Milestones", milestonesPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: projectsPanel.refreshData(); break;
                case 1: tasksPanel.refreshData(); break;
                case 2: milestonesPanel.refreshData(); break;
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
            case 0: projectsPanel.refreshData(); break;
            case 1: tasksPanel.refreshData(); break;
            case 2: milestonesPanel.refreshData(); break;
        }
    }
}
