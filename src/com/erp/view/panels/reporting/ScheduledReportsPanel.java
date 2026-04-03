package com.erp.view.panels.reporting;

import com.erp.model.Report;
import com.erp.model.ScheduledReport;
import com.erp.service.mock.MockReportingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.ScheduledReportDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ScheduledReportsPanel displays and manages scheduled reports.
 */
public class ScheduledReportsPanel extends JPanel {

    private MockReportingService reportingService;

    private JTable scheduledTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> frequencyFilter;
    private JComboBox<String> statusFilter;

    private JLabel totalScheduledLabel;
    private JLabel activeScheduledLabel;
    private JLabel pendingScheduledLabel;

    private static final String[] COLUMNS = {"ID", "Schedule Name", "Report", "Frequency", "Time", "Status", "Delivery", "Last Run", "Next Run"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ScheduledReportsPanel() {
        reportingService = MockReportingService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Frequency filter
        frequencyFilter = new JComboBox<>(new String[]{"All Frequencies", "DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"});
        frequencyFilter.setFont(Constants.FONT_REGULAR);
        frequencyFilter.addActionListener(e -> loadData());

        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Status", "Active", "Inactive"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduledTable = new JTable(tableModel);
        scheduledTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(scheduledTable);

        // Column widths
        scheduledTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        scheduledTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        scheduledTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        scheduledTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        scheduledTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        scheduledTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        scheduledTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        scheduledTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        scheduledTable.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Custom renderers
        scheduledTable.getColumnModel().getColumn(3).setCellRenderer(new FrequencyCellRenderer());
        scheduledTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalScheduledLabel = createSummaryValue("0");
        activeScheduledLabel = createSummaryValue("0");
        pendingScheduledLabel = createSummaryValue("0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        JPanel summaryPanel = createSummaryPanel();
        JPanel toolbar = createToolbar();

        JScrollPane scrollPane = new JScrollPane(scheduledTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Scheduled", totalScheduledLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Active", activeScheduledLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Pending Run", pendingScheduledLabel, Constants.WARNING_COLOR));

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
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);

        toolbar.add(new JLabel("Frequency:"));
        toolbar.add(frequencyFilter);

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);

        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        JButton addBtn = UIHelper.createPrimaryButton("Schedule Report");
        addBtn.setPreferredSize(new Dimension(130, 30));
        addBtn.addActionListener(e -> addSchedule());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editSchedule());
        toolbar.add(editBtn);

        JButton runNowBtn = new JButton("Run Now");
        runNowBtn.setFont(Constants.FONT_BUTTON);
        runNowBtn.setBackground(Constants.SUCCESS_COLOR);
        runNowBtn.setForeground(Color.WHITE);
        runNowBtn.setOpaque(true);
        runNowBtn.setBorderPainted(false);
        runNowBtn.setFocusPainted(false);
        runNowBtn.setPreferredSize(new Dimension(90, 30));
        runNowBtn.addActionListener(e -> runNow());
        toolbar.add(runNowBtn);

        JButton toggleBtn = UIHelper.createSecondaryButton("Toggle");
        toggleBtn.setPreferredSize(new Dimension(80, 30));
        toggleBtn.addActionListener(e -> toggleSchedule());
        toolbar.add(toggleBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<ScheduledReport> schedules = reportingService.getAllScheduledReports();
        String frequencySelection = (String) frequencyFilter.getSelectedItem();
        String statusSelection = (String) statusFilter.getSelectedItem();

        int totalCount = 0;
        int activeCount = 0;
        int pendingCount = 0;

        for (ScheduledReport s : schedules) {
            if (!"All Frequencies".equals(frequencySelection) && !frequencySelection.equals(s.getFrequency())) {
                continue;
            }
            if (!"All Status".equals(statusSelection)) {
                boolean isActive = s.isActive();
                if ("Active".equals(statusSelection) && !isActive) continue;
                if ("Inactive".equals(statusSelection) && isActive) continue;
            }

            totalCount++;
            if (s.isActive()) activeCount++;
            if ("PENDING".equals(s.getLastRunStatus())) pendingCount++;

            // Get report name
            Report report = reportingService.getReportById(s.getReportId());
            String reportName = report != null ? report.getName() : "Unknown";

            tableModel.addRow(new Object[]{
                s.getScheduleId(),
                s.getScheduleName(),
                reportName,
                s.getFrequency(),
                s.getTimeOfDay(),
                s.isActive() ? "Active" : "Inactive",
                s.getDeliveryMethod(),
                s.getLastRunDate() != null ? s.getLastRunDate().format(DATE_FORMAT) : "Never",
                s.getNextRunDate() != null ? s.getNextRunDate().format(DATE_FORMAT) : "Not Set"
            });
        }

        totalScheduledLabel.setText(String.valueOf(totalCount));
        activeScheduledLabel.setText(String.valueOf(activeCount));
        pendingScheduledLabel.setText(String.valueOf(pendingCount));
    }

    private void addSchedule() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ScheduledReportDialog dialog = new ScheduledReportDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ScheduledReport newSchedule = dialog.getScheduledReport();
            reportingService.createScheduledReport(newSchedule);
            UIHelper.showSuccess(this, "Schedule created successfully.");
            loadData();
        }
    }

    private void editSchedule() {
        int row = scheduledTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a schedule to edit.");
            return;
        }

        int scheduleId = (int) tableModel.getValueAt(row, 0);
        ScheduledReport schedule = reportingService.getScheduledReportById(scheduleId);

        if (schedule != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ScheduledReportDialog dialog = new ScheduledReportDialog(parentFrame, schedule);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                reportingService.updateScheduledReport(dialog.getScheduledReport());
                UIHelper.showSuccess(this, "Schedule updated successfully.");
                loadData();
            }
        }
    }

    private void runNow() {
        int row = scheduledTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a schedule to run.");
            return;
        }

        int scheduleId = (int) tableModel.getValueAt(row, 0);
        String scheduleName = (String) tableModel.getValueAt(row, 1);

        boolean confirm = UIHelper.showConfirm(this, "Run schedule '" + scheduleName + "' now?");
        if (confirm) {
            if (reportingService.runScheduledReportNow(scheduleId)) {
                UIHelper.showSuccess(this, "Report executed successfully.");
                loadData();
            }
        }
    }

    private void toggleSchedule() {
        int row = scheduledTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a schedule to toggle.");
            return;
        }

        int scheduleId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 5);

        if ("Active".equals(status)) {
            reportingService.deactivateScheduledReport(scheduleId);
            UIHelper.showSuccess(this, "Schedule deactivated.");
        } else {
            reportingService.activateScheduledReport(scheduleId);
            UIHelper.showSuccess(this, "Schedule activated.");
        }
        loadData();
    }

    public void refreshData() {
        loadData();
    }

    // Frequency cell renderer
    private static class FrequencyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String frequency = value.toString();
                switch (frequency) {
                    case "DAILY":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "WEEKLY":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "MONTHLY":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "QUARTERLY":
                        setBackground(new Color(230, 230, 250));
                        setForeground(new Color(75, 0, 130));
                        break;
                    case "YEARLY":
                        setBackground(new Color(255, 228, 225));
                        setForeground(new Color(139, 69, 19));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String status = value.toString();
                if ("Active".equals(status)) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                }
            }
            return this;
        }
    }
}
