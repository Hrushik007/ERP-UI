package com.erp.view.panels.hr;

import com.erp.model.Attendance;
import com.erp.model.Employee;
import com.erp.service.mock.MockHRService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * AttendancePanel manages employee check-in/check-out and attendance records.
 *
 * This demonstrates:
 * 1. Split pane - Divides the panel into two resizable sections
 * 2. Date picker simulation - Using combo boxes for date selection
 * 3. Real-time updates - Refreshing data based on date selection
 * 4. Summary statistics - Calculated data displayed in cards
 */
public class AttendancePanel extends JPanel {

    private MockHRService hrService;

    // Left panel components - Quick actions
    private JComboBox<String> employeeCombo;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JLabel currentTimeLabel;

    // Right panel components - Attendance records
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;

    // Summary labels
    private JLabel totalLabel;
    private JLabel presentLabel;
    private JLabel lateLabel;
    private JLabel checkedInLabel;

    private static final String[] COLUMNS = {"Employee ID", "Name", "Check In", "Check Out", "Status"};
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    public AttendancePanel() {
        hrService = MockHRService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
        startTimeUpdater();
    }

    private void initializeComponents() {
        // Employee combo for check-in/out
        employeeCombo = new JComboBox<>();
        employeeCombo.setFont(Constants.FONT_REGULAR);
        for (Employee emp : hrService.getAllEmployees()) {
            employeeCombo.addItem(emp.getEmployeeId() + " - " + emp.getFullName());
        }

        // Action buttons
        checkInButton = UIHelper.createPrimaryButton("Check In");
        checkInButton.addActionListener(e -> performCheckIn());

        checkOutButton = UIHelper.createSecondaryButton("Check Out");
        checkOutButton.addActionListener(e -> performCheckOut());

        // Current time display
        currentTimeLabel = new JLabel();
        currentTimeLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 24));
        currentTimeLabel.setForeground(Constants.PRIMARY_COLOR);
        updateTime();

        // Date selectors
        LocalDate today = LocalDate.now();

        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayCombo.addItem(i);
        dayCombo.setSelectedItem(today.getDayOfMonth());
        dayCombo.addActionListener(e -> loadData());

        monthCombo = new JComboBox<>(MONTHS);
        monthCombo.setSelectedIndex(today.getMonthValue() - 1);
        monthCombo.addActionListener(e -> loadData());

        yearCombo = new JComboBox<>();
        for (int y = today.getYear() - 2; y <= today.getYear() + 1; y++) yearCombo.addItem(y);
        yearCombo.setSelectedItem(today.getYear());
        yearCombo.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
        UIHelper.styleTable(attendanceTable);

        // Summary labels
        totalLabel = createSummaryLabel("0");
        presentLabel = createSummaryLabel("0");
        lateLabel = createSummaryLabel("0");
        checkedInLabel = createSummaryLabel("0");
    }

    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 20));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Top section - Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Left panel - Quick actions
        JPanel actionsPanel = createActionsPanel();

        // Right panel - Attendance table
        JPanel tablePanel = createTablePanel();

        // Use split pane for actions and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionsPanel, tablePanel);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0);

        add(summaryPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_MEDIUM, 0));

        panel.add(createSummaryCard("Total Records", totalLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Present", presentLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Late", lateLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Checked In", checkedInLabel, Constants.ACCENT_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
                          Constants.PADDING_LARGE, Constants.PADDING_LARGE)
        ));

        // Title
        JLabel title = new JLabel("Quick Check-In/Out");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setForeground(Constants.TEXT_PRIMARY);

        // Time display
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timePanel.setOpaque(false);
        timePanel.add(currentTimeLabel);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(Constants.FONT_REGULAR);
        empLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        employeeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        employeeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonRow = new JPanel(new GridLayout(1, 2, Constants.PADDING_SMALL, 0));
        buttonRow.setOpaque(false);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonRow.add(checkInButton);
        buttonRow.add(checkOutButton);

        formPanel.add(empLabel);
        formPanel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        formPanel.add(employeeCombo);
        formPanel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
        formPanel.add(buttonRow);

        // Assemble
        JPanel content = new JPanel(new BorderLayout(0, Constants.PADDING_LARGE));
        content.setOpaque(false);
        content.add(timePanel, BorderLayout.NORTH);
        content.add(formPanel, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        panel.setOpaque(false);

        // Date selector row
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        datePanel.setOpaque(false);
        datePanel.add(new JLabel("View Date:"));
        datePanel.add(dayCombo);
        datePanel.add(monthCombo);
        datePanel.add(yearCombo);

        JButton todayButton = UIHelper.createSecondaryButton("Today");
        todayButton.setPreferredSize(new Dimension(80, 30));
        todayButton.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            dayCombo.setSelectedItem(today.getDayOfMonth());
            monthCombo.setSelectedIndex(today.getMonthValue() - 1);
            yearCombo.setSelectedItem(today.getYear());
        });
        datePanel.add(todayButton);

        // Table with scroll
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        panel.add(datePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        // Get selected date
        int day = (Integer) dayCombo.getSelectedItem();
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();

        LocalDate selectedDate;
        try {
            selectedDate = LocalDate.of(year, month, day);
        } catch (Exception e) {
            // Invalid date (e.g., Feb 30)
            selectedDate = LocalDate.of(year, month, 1);
        }

        // Clear table
        tableModel.setRowCount(0);

        // Get attendance records
        List<Attendance> records = hrService.getAttendanceByDate(selectedDate);

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        for (Attendance att : records) {
            Employee emp = hrService.getEmployeeById(att.getEmployeeId());
            String empName = (emp != null) ? emp.getFullName() : "Unknown";

            String checkIn = att.getCheckInTime() != null ? att.getCheckInTime().format(timeFormat) : "-";
            String checkOut = att.getCheckOutTime() != null ? att.getCheckOutTime().format(timeFormat) : "-";

            Object[] row = {
                att.getEmployeeId(),
                empName,
                checkIn,
                checkOut,
                att.getStatus()
            };
            tableModel.addRow(row);
        }

        // Update summary
        updateSummary(selectedDate);
    }

    private void updateSummary(LocalDate date) {
        Map<String, Integer> summary = hrService.getAttendanceSummary(date, date);
        totalLabel.setText(String.valueOf(summary.getOrDefault("totalRecords", 0)));
        presentLabel.setText(String.valueOf(summary.getOrDefault("present", 0)));
        lateLabel.setText(String.valueOf(summary.getOrDefault("late", 0)));
        checkedInLabel.setText(String.valueOf(summary.getOrDefault("checkedIn", 0)));
    }

    private void performCheckIn() {
        String selected = (String) employeeCombo.getSelectedItem();
        if (selected == null) return;

        int empId = Integer.parseInt(selected.split(" - ")[0]);
        Attendance att = hrService.checkIn(empId);

        if (att != null) {
            UIHelper.showSuccess(this, "Check-in recorded at " +
                att.getCheckInTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            loadData();
        }
    }

    private void performCheckOut() {
        String selected = (String) employeeCombo.getSelectedItem();
        if (selected == null) return;

        int empId = Integer.parseInt(selected.split(" - ")[0]);
        Attendance att = hrService.checkOut(empId);

        if (att != null) {
            UIHelper.showSuccess(this, "Check-out recorded at " +
                att.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            loadData();
        } else {
            UIHelper.showError(this, "No check-in found for today. Please check in first.");
        }
    }

    private void updateTime() {
        currentTimeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void startTimeUpdater() {
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
    }

    public void refreshData() {
        loadData();
    }
}
