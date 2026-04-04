package com.erp.view.panels.automation;

import com.erp.service.mock.MockAutomationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * ApprovalsPanel displays and manages approval workflows.
 *
 * Shows all approvals with ability to approve/reject pending items.
 */
public class ApprovalsPanel extends JPanel {

    private MockAutomationService automationService;

    private JTable approvalsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> statusFilter;
    private JComboBox<String> typeFilter;

    // Summary labels
    private JLabel totalApprovalsLabel;
    private JLabel pendingLabel;
    private JLabel approvedLabel;
    private JLabel rejectedLabel;

    private JButton approveButton;
    private JButton rejectButton;

    private static final String[] COLUMNS = {"ID", "Type", "Item ID", "Description", "Requested By", "Status", "Created"};

    public ApprovalsPanel() {
        automationService = MockAutomationService.getInstance();
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

        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "ORDER", "LEAVE_REQUEST", "INVOICE"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        approvalsTable = new JTable(tableModel);
        approvalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(approvalsTable);

        approvalsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        approvalsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        approvalsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        approvalsTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        approvalsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        approvalsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        approvalsTable.getColumnModel().getColumn(6).setPreferredWidth(130);

        // Status column renderer
        approvalsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        approvalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // Summary labels
        totalApprovalsLabel = createSummaryValue("0");
        pendingLabel = createSummaryValue("0");
        approvedLabel = createSummaryValue("0");
        rejectedLabel = createSummaryValue("0");

        // Action buttons
        approveButton = new JButton("Approve");
        approveButton.setFont(Constants.FONT_BUTTON);
        approveButton.setBackground(Constants.SUCCESS_COLOR);
        approveButton.setForeground(Color.WHITE);
        approveButton.setOpaque(true);
        approveButton.setBorderPainted(false);
        approveButton.setFocusPainted(false);
        approveButton.setPreferredSize(new Dimension(100, 30));
        approveButton.setEnabled(false);
        approveButton.addActionListener(e -> approveItem());

        rejectButton = new JButton("Reject");
        rejectButton.setFont(Constants.FONT_BUTTON);
        rejectButton.setBackground(Constants.DANGER_COLOR);
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setOpaque(true);
        rejectButton.setBorderPainted(false);
        rejectButton.setFocusPainted(false);
        rejectButton.setPreferredSize(new Dimension(100, 30));
        rejectButton.setEnabled(false);
        rejectButton.addActionListener(e -> rejectItem());
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(approvalsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total", totalApprovalsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Pending", pendingLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Approved", approvedLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Rejected", rejectedLabel, Constants.DANGER_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_MEDIUM,
                          Constants.PADDING_SMALL, Constants.PADDING_MEDIUM)
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

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        // Filters row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);

        // Actions row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        actionPanel.add(refreshBtn);

        toolbar.add(filterPanel, BorderLayout.NORTH);
        toolbar.add(actionPanel, BorderLayout.SOUTH);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Map<String, Object>> approvals = automationService.getAllApprovals();
        String statusSelection = (String) statusFilter.getSelectedItem();
        String typeSelection = (String) typeFilter.getSelectedItem();

        for (Map<String, Object> a : approvals) {
            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(a.get("status"))) {
                continue;
            }
            // Filter by type
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(a.get("itemType"))) {
                continue;
            }

            tableModel.addRow(new Object[]{
                a.get("id"),
                a.get("itemType"),
                a.get("itemId"),
                a.get("description"),
                a.get("requestedBy"),
                a.get("status"),
                a.get("createdAt")
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        Map<String, Integer> counts = automationService.getApprovalCountByStatus();
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        totalApprovalsLabel.setText(String.valueOf(total));
        pendingLabel.setText(String.valueOf(counts.getOrDefault("PENDING", 0)));
        approvedLabel.setText(String.valueOf(counts.getOrDefault("APPROVED", 0)));
        rejectedLabel.setText(String.valueOf(counts.getOrDefault("REJECTED", 0)));
    }

    private void updateButtonStates() {
        int row = approvalsTable.getSelectedRow();
        if (row >= 0) {
            String status = (String) tableModel.getValueAt(row, 5);
            boolean isPending = "PENDING".equals(status);
            approveButton.setEnabled(isPending);
            rejectButton.setEnabled(isPending);
        } else {
            approveButton.setEnabled(false);
            rejectButton.setEnabled(false);
        }
    }

    private void approveItem() {
        int row = approvalsTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) tableModel.getValueAt(row, 0);
        String desc = (String) tableModel.getValueAt(row, 3);

        String comments = JOptionPane.showInputDialog(this, "Approval comments (optional):", "Approve: " + desc, JOptionPane.PLAIN_MESSAGE);
        if (comments != null) {
            if (automationService.approve(id, 1, comments)) {
                UIHelper.showSuccess(this, "Item approved successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to approve item.");
            }
        }
    }

    private void rejectItem() {
        int row = approvalsTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) tableModel.getValueAt(row, 0);
        String desc = (String) tableModel.getValueAt(row, 3);

        String reason = JOptionPane.showInputDialog(this, "Rejection reason:*", "Reject: " + desc, JOptionPane.PLAIN_MESSAGE);
        if (reason != null) {
            if (reason.trim().isEmpty()) {
                UIHelper.showError(this, "Rejection reason is required.");
                return;
            }
            if (automationService.reject(id, 1, reason)) {
                UIHelper.showSuccess(this, "Item rejected.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to reject item.");
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "PENDING":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "APPROVED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "REJECTED":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }
}
