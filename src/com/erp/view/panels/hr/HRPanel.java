package com.erp.view.panels.hr;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * HRPanel is the main container for the HR Management module.
 *
 * This demonstrates:
 * 1. JTabbedPane - A container that lets users switch between components by clicking tabs
 * 2. COMPOSITION - HRPanel contains multiple sub-panels (Employee, Attendance, Leave)
 * 3. MODULAR DESIGN - Each tab is its own class, making code organized and maintainable
 *
 * Structure:
 * HRPanel (BasePanel)
 *   └── JTabbedPane
 *       ├── EmployeePanel (tab 1)
 *       ├── AttendancePanel (tab 2)
 *       └── LeaveRequestPanel (tab 3)
 */
public class HRPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private EmployeePanel employeePanel;
    private AttendancePanel attendancePanel;
    private LeaveRequestPanel leaveRequestPanel;

    public HRPanel() {
        super(Constants.MODULE_HR);
    }

    @Override
    protected void initializeComponents() {
        // Create the tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Create each tab panel
        employeePanel = new EmployeePanel();
        attendancePanel = new AttendancePanel();
        leaveRequestPanel = new LeaveRequestPanel();

        // Add tabs
        tabbedPane.addTab("Employees", createTabIcon(), employeePanel, "Manage employees");
        tabbedPane.addTab("Attendance", createTabIcon(), attendancePanel, "Track attendance");
        tabbedPane.addTab("Leave Requests", createTabIcon(), leaveRequestPanel, "Manage leave requests");

        // Style the tabs
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    protected void layoutComponents() {
        // Use the full content panel for the tabbed pane
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Creates a simple icon placeholder for tabs.
     * In a real app, you'd use actual icons.
     */
    private Icon createTabIcon() {
        // Return null for now - tabs work without icons
        return null;
    }

    @Override
    public void refreshData() {
        // Refresh the currently selected tab
        Component selected = tabbedPane.getSelectedComponent();
        if (selected instanceof EmployeePanel) {
            ((EmployeePanel) selected).refreshData();
        } else if (selected instanceof AttendancePanel) {
            ((AttendancePanel) selected).refreshData();
        } else if (selected instanceof LeaveRequestPanel) {
            ((LeaveRequestPanel) selected).refreshData();
        }
    }
}
