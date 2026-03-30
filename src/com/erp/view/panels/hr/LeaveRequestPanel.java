package com.erp.view.panels.hr;

import com.erp.model.Employee;
import com.erp.model.LeaveRequest;
import com.erp.service.mock.MockHRService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * LeaveRequestPanel manages employee leave requests.
 *
 * This demonstrates:
 * 1. Custom cell renderer - Different colors for different statuses
 * 2. Approval workflow - Pending requests can be approved/rejected
 * 3. Leave balance display - Shows remaining leave days
 * 4. Form within panel - Submit new leave request inline
 */
public class LeaveRequestPanel extends JPanel {

    private MockHRService hrService;

    // Table components
    private JTable leaveTable;
    private DefaultTableModel tableModel;

    // Filter components
    private JComboBox<String> statusFilter;
    private JComboBox<String> employeeFilter;

    // Action buttons
    private JButton approveButton;
    private JButton rejectButton;
    private JButton newRequestButton;

    // New request form components
    private JComboBox<String> requestEmployeeCombo;
    private JComboBox<String> leaveTypeCombo;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea reasonArea;
    private JButton submitButton;

    // Leave balance display
    private JLabel vacationBalance;
    private JLabel sickBalance;
    private JLabel personalBalance;

    private static final String[] COLUMNS = {
        "ID", "Employee", "Type", "Start Date", "End Date", "Days", "Status", "Submitted"
    };

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LeaveRequestPanel() {
        hrService = MockHRService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "PENDING", "APPROVED", "REJECTED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Employee filter
        employeeFilter = new JComboBox<>();
        employeeFilter.setFont(Constants.FONT_REGULAR);
        employeeFilter.addItem("All Employees");
        for (Employee emp : hrService.getAllEmployees()) {
            employeeFilter.addItem(emp.getEmployeeId() + " - " + emp.getFullName());
        }
        employeeFilter.addActionListener(e -> {
            loadData();
            updateLeaveBalance();
        });

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leaveTable = new JTable(tableModel);
        UIHelper.styleTable(leaveTable);
        leaveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for status column
        leaveTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Selection listener
        leaveTable.getSelectionModel().addListSelectionListener(e -> {
            int row = leaveTable.getSelectedRow();
            if (row >= 0) {
                String status = (String) tableModel.getValueAt(
                    leaveTable.convertRowIndexToModel(row), 6);
                approveButton.setEnabled("PENDING".equals(status));
                rejectButton.setEnabled("PENDING".equals(status));
            } else {
                approveButton.setEnabled(false);
                rejectButton.setEnabled(false);
            }
        });

        // Action buttons
        approveButton = new JButton("Approve");
        approveButton.setFont(Constants.FONT_BUTTON);
        approveButton.setForeground(Constants.TEXT_LIGHT);
        approveButton.setBackground(Constants.SUCCESS_COLOR);
        approveButton.setEnabled(false);
        approveButton.addActionListener(e -> approveSelected());

        rejectButton = new JButton("Reject");
        rejectButton.setFont(Constants.FONT_BUTTON);
        rejectButton.setForeground(Constants.TEXT_LIGHT);
        rejectButton.setBackground(Constants.DANGER_COLOR);
        rejectButton.setEnabled(false);
        rejectButton.addActionListener(e -> rejectSelected());

        newRequestButton = UIHelper.createPrimaryButton("New Request");
        newRequestButton.addActionListener(e -> showNewRequestForm());

        // New request form components
        requestEmployeeCombo = new JComboBox<>();
        requestEmployeeCombo.setFont(Constants.FONT_REGULAR);
        for (Employee emp : hrService.getAllEmployees()) {
            requestEmployeeCombo.addItem(emp.getEmployeeId() + " - " + emp.getFullName());
        }

        leaveTypeCombo = new JComboBox<>(new String[]{"VACATION", "SICK", "PERSONAL", "MATERNITY", "PATERNITY", "UNPAID"});
        leaveTypeCombo.setFont(Constants.FONT_REGULAR);

        startDateField = UIHelper.createTextField(10);
        startDateField.setText(LocalDate.now().plusDays(1).format(DATE_FORMAT));
        startDateField.setToolTipText("Format: YYYY-MM-DD");

        endDateField = UIHelper.createTextField(10);
        endDateField.setText(LocalDate.now().plusDays(1).format(DATE_FORMAT));
        endDateField.setToolTipText("Format: YYYY-MM-DD");

        reasonArea = new JTextArea(3, 20);
        reasonArea.setFont(Constants.FONT_REGULAR);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        submitButton = UIHelper.createPrimaryButton("Submit Request");
        submitButton.addActionListener(e -> submitLeaveRequest());

        // Leave balance labels
        vacationBalance = createBalanceLabel();
        sickBalance = createBalanceLabel();
        personalBalance = createBalanceLabel();
    }

    private JLabel createBalanceLabel() {
        JLabel label = new JLabel("--", SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Top - filters and actions
        JPanel topPanel = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Employee:"));
        filterPanel.add(employeeFilter);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(newRequestButton);
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);

        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        // Center - table
        JScrollPane scrollPane = new JScrollPane(leaveTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Right - new request form (collapsible)
        JPanel rightPanel = createNewRequestPanel();

        // Main split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, rightPanel);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(1.0);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createNewRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));
        panel.setPreferredSize(new Dimension(280, 0));

        // Title
        JLabel title = new JLabel("Submit Leave Request");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setForeground(Constants.TEXT_PRIMARY);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        formPanel.add(createFormField("Employee:", requestEmployeeCombo));
        formPanel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        formPanel.add(createFormField("Leave Type:", leaveTypeCombo));
        formPanel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        formPanel.add(createFormField("Start Date:", startDateField));
        formPanel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        formPanel.add(createFormField("End Date:", endDateField));
        formPanel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));

        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setFont(Constants.FONT_REGULAR);
        reasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(reasonLabel);
        formPanel.add(Box.createVerticalStrut(5));

        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        reasonScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        formPanel.add(reasonScroll);

        formPanel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        formPanel.add(submitButton);

        // Leave balance section
        JPanel balancePanel = createBalancePanel();

        // Assemble
        JPanel content = new JPanel(new BorderLayout(0, Constants.PADDING_LARGE));
        content.setOpaque(false);
        content.add(formPanel, BorderLayout.NORTH);
        content.add(balancePanel, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Constants.FONT_REGULAR);
        lbl.setPreferredSize(new Dimension(80, 25));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            "Leave Balance",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            Constants.FONT_REGULAR
        ));

        JPanel balanceGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        balanceGrid.setOpaque(false);
        balanceGrid.setBorder(new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                                              Constants.PADDING_SMALL, Constants.PADDING_SMALL));

        balanceGrid.add(new JLabel("Vacation:"));
        balanceGrid.add(vacationBalance);
        balanceGrid.add(new JLabel("Sick:"));
        balanceGrid.add(sickBalance);
        balanceGrid.add(new JLabel("Personal:"));
        balanceGrid.add(personalBalance);

        panel.add(balanceGrid, BorderLayout.CENTER);

        // Update balance when employee changes
        requestEmployeeCombo.addActionListener(e -> updateLeaveBalanceForRequest());

        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);

        List<LeaveRequest> requests;
        String statusSel = (String) statusFilter.getSelectedItem();
        String empSel = (String) employeeFilter.getSelectedItem();

        // Get requests based on filters
        if ("All Statuses".equals(statusSel)) {
            if ("All Employees".equals(empSel)) {
                // Get all - combine all statuses
                requests = hrService.getLeaveRequestsByStatus("PENDING");
                requests.addAll(hrService.getLeaveRequestsByStatus("APPROVED"));
                requests.addAll(hrService.getLeaveRequestsByStatus("REJECTED"));
            } else {
                int empId = Integer.parseInt(empSel.split(" - ")[0]);
                requests = hrService.getLeaveRequestsByEmployee(empId);
            }
        } else {
            requests = hrService.getLeaveRequestsByStatus(statusSel);
            if (!"All Employees".equals(empSel)) {
                int empId = Integer.parseInt(empSel.split(" - ")[0]);
                requests.removeIf(lr -> lr.getEmployeeId() != empId);
            }
        }

        for (LeaveRequest lr : requests) {
            Employee emp = hrService.getEmployeeById(lr.getEmployeeId());
            String empName = (emp != null) ? emp.getFullName() : "Unknown";

            long days = ChronoUnit.DAYS.between(lr.getStartDate(), lr.getEndDate()) + 1;

            Object[] row = {
                lr.getLeaveRequestId(),
                empName,
                lr.getLeaveType(),
                lr.getStartDate().format(DATE_FORMAT),
                lr.getEndDate().format(DATE_FORMAT),
                days,
                lr.getStatus(),
                lr.getCreatedAt() != null ?
                    lr.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-"
            };
            tableModel.addRow(row);
        }
    }

    private void updateLeaveBalance() {
        String selected = (String) employeeFilter.getSelectedItem();
        if ("All Employees".equals(selected)) {
            vacationBalance.setText("--");
            sickBalance.setText("--");
            personalBalance.setText("--");
            return;
        }

        int empId = Integer.parseInt(selected.split(" - ")[0]);
        Map<String, Integer> balance = hrService.getLeaveBalance(empId);

        vacationBalance.setText(balance.getOrDefault("VACATION", 0) + " days");
        sickBalance.setText(balance.getOrDefault("SICK", 0) + " days");
        personalBalance.setText(balance.getOrDefault("PERSONAL", 0) + " days");
    }

    private void updateLeaveBalanceForRequest() {
        String selected = (String) requestEmployeeCombo.getSelectedItem();
        if (selected == null) return;

        int empId = Integer.parseInt(selected.split(" - ")[0]);
        Map<String, Integer> balance = hrService.getLeaveBalance(empId);

        vacationBalance.setText(balance.getOrDefault("VACATION", 0) + " days");
        sickBalance.setText(balance.getOrDefault("SICK", 0) + " days");
        personalBalance.setText(balance.getOrDefault("PERSONAL", 0) + " days");
    }

    private void approveSelected() {
        int row = leaveTable.getSelectedRow();
        if (row < 0) return;

        int modelRow = leaveTable.convertRowIndexToModel(row);
        int requestId = (int) tableModel.getValueAt(modelRow, 0);

        String comments = JOptionPane.showInputDialog(this, "Approval comments (optional):");

        if (hrService.approveLeaveRequest(requestId, 1, comments)) {
            UIHelper.showSuccess(this, "Leave request approved.");
            loadData();
        }
    }

    private void rejectSelected() {
        int row = leaveTable.getSelectedRow();
        if (row < 0) return;

        int modelRow = leaveTable.convertRowIndexToModel(row);
        int requestId = (int) tableModel.getValueAt(modelRow, 0);

        String reason = JOptionPane.showInputDialog(this, "Rejection reason:");
        if (reason == null || reason.trim().isEmpty()) {
            UIHelper.showError(this, "Please provide a reason for rejection.");
            return;
        }

        if (hrService.rejectLeaveRequest(requestId, 1, reason)) {
            UIHelper.showSuccess(this, "Leave request rejected.");
            loadData();
        }
    }

    private void showNewRequestForm() {
        // The form is already visible on the right side
        requestEmployeeCombo.requestFocus();
    }

    private void submitLeaveRequest() {
        // Validate
        String empSel = (String) requestEmployeeCombo.getSelectedItem();
        if (empSel == null) {
            UIHelper.showError(this, "Please select an employee.");
            return;
        }

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMAT);
            endDate = LocalDate.parse(endDateField.getText().trim(), DATE_FORMAT);
        } catch (Exception e) {
            UIHelper.showError(this, "Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            UIHelper.showError(this, "End date must be after start date.");
            return;
        }

        if (startDate.isBefore(LocalDate.now())) {
            UIHelper.showError(this, "Start date cannot be in the past.");
            return;
        }

        // Create request
        int empId = Integer.parseInt(empSel.split(" - ")[0]);
        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(empId);
        request.setLeaveType((String) leaveTypeCombo.getSelectedItem());
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setReason(reasonArea.getText().trim());

        LeaveRequest created = hrService.submitLeaveRequest(request);
        if (created != null) {
            UIHelper.showSuccess(this, "Leave request submitted successfully!");
            loadData();
            reasonArea.setText("");
        }
    }

    public void refreshData() {
        loadData();
    }

    /**
     * Custom cell renderer for status column - shows different colors for different statuses.
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                String status = (String) value;
                switch (status) {
                    case "APPROVED":
                        c.setForeground(Constants.SUCCESS_COLOR);
                        break;
                    case "REJECTED":
                        c.setForeground(Constants.DANGER_COLOR);
                        break;
                    case "PENDING":
                        c.setForeground(Constants.WARNING_COLOR);
                        break;
                    default:
                        c.setForeground(Constants.TEXT_PRIMARY);
                }
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }
}
