package com.erp.view.panels.reporting;

import com.erp.model.Report;
import com.erp.service.mock.MockReportingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.ReportDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportsPanel displays and manages available reports.
 */
public class ReportsPanel extends JPanel {

    private MockReportingService reportingService;

    private JTable reportsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryFilter;
    private JComboBox<String> typeFilter;

    private JLabel totalReportsLabel;
    private JLabel activeReportsLabel;
    private JLabel totalRunsLabel;

    private static final String[] COLUMNS = {"ID", "Code", "Report Name", "Category", "Type", "Format", "Status", "Run Count", "Last Run"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportsPanel() {
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
        // Category filter
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "SALES", "INVENTORY", "FINANCIAL", "HR", "PROJECT", "CUSTOM"});
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addActionListener(e -> loadData());

        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "TABLE", "CHART", "SUMMARY", "DETAILED"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportsTable = new JTable(tableModel);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(reportsTable);

        // Column widths
        reportsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        reportsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        reportsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        reportsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        reportsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        reportsTable.getColumnModel().getColumn(8).setPreferredWidth(130);

        // Custom renderers
        reportsTable.getColumnModel().getColumn(3).setCellRenderer(new CategoryCellRenderer());
        reportsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalReportsLabel = createSummaryValue("0");
        activeReportsLabel = createSummaryValue("0");
        totalRunsLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(reportsTable);
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

        panel.add(createSummaryCard("Total Reports", totalReportsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Active Reports", activeReportsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Runs", totalRunsLabel, Constants.PRIMARY_LIGHT));

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

        toolbar.add(new JLabel("Category:"));
        toolbar.add(categoryFilter);

        toolbar.add(new JLabel("Type:"));
        toolbar.add(typeFilter);

        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        JButton addBtn = UIHelper.createPrimaryButton("New Report");
        addBtn.setPreferredSize(new Dimension(110, 30));
        addBtn.addActionListener(e -> addReport());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editReport());
        toolbar.add(editBtn);

        JButton runBtn = new JButton("Run Report");
        runBtn.setFont(Constants.FONT_BUTTON);
        runBtn.setBackground(Constants.SUCCESS_COLOR);
        runBtn.setForeground(Color.WHITE);
        runBtn.setOpaque(true);
        runBtn.setBorderPainted(false);
        runBtn.setFocusPainted(false);
        runBtn.setPreferredSize(new Dimension(100, 30));
        runBtn.addActionListener(e -> runReport());
        toolbar.add(runBtn);

        JButton exportBtn = UIHelper.createSecondaryButton("Export");
        exportBtn.setPreferredSize(new Dimension(80, 30));
        exportBtn.addActionListener(e -> exportReport());
        toolbar.add(exportBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Report> reports = reportingService.getAllReports();
        String categorySelection = (String) categoryFilter.getSelectedItem();
        String typeSelection = (String) typeFilter.getSelectedItem();

        int totalCount = 0;
        int activeCount = 0;
        int totalRuns = 0;

        for (Report r : reports) {
            if (!"All Categories".equals(categorySelection) && !categorySelection.equals(r.getCategory())) {
                continue;
            }
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(r.getReportType())) {
                continue;
            }

            totalCount++;
            if (r.isActive()) activeCount++;
            totalRuns += r.getRunCount();

            tableModel.addRow(new Object[]{
                r.getReportId(),
                r.getReportCode(),
                r.getName(),
                r.getCategory(),
                r.getReportType(),
                r.getOutputFormat(),
                r.isActive() ? "Active" : "Inactive",
                r.getRunCount(),
                r.getLastRunDate() != null ? r.getLastRunDate().format(DATE_FORMAT) : "Never"
            });
        }

        totalReportsLabel.setText(String.valueOf(totalCount));
        activeReportsLabel.setText(String.valueOf(activeCount));
        totalRunsLabel.setText(String.valueOf(totalRuns));
    }

    private void addReport() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ReportDialog dialog = new ReportDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Report newReport = dialog.getReport();
            reportingService.createReport(newReport);
            UIHelper.showSuccess(this, "Report created successfully.");
            loadData();
        }
    }

    private void editReport() {
        int row = reportsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a report to edit.");
            return;
        }

        int reportId = (int) tableModel.getValueAt(row, 0);
        Report report = reportingService.getReportById(reportId);

        if (report != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ReportDialog dialog = new ReportDialog(parentFrame, report);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                reportingService.updateReport(dialog.getReport());
                UIHelper.showSuccess(this, "Report updated successfully.");
                loadData();
            }
        }
    }

    private void runReport() {
        int row = reportsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a report to run.");
            return;
        }

        int reportId = (int) tableModel.getValueAt(row, 0);
        String reportName = (String) tableModel.getValueAt(row, 2);

        boolean confirm = UIHelper.showConfirm(this, "Run report '" + reportName + "'?");
        if (confirm) {
            String result = reportingService.executeReport(reportId, "{}");
            UIHelper.showSuccess(this, result);
            loadData();
        }
    }

    private void exportReport() {
        int row = reportsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a report to export.");
            return;
        }

        int reportId = (int) tableModel.getValueAt(row, 0);
        String format = (String) tableModel.getValueAt(row, 5);

        String[] formats = {"PDF", "EXCEL", "CSV", "HTML"};
        String selectedFormat = (String) JOptionPane.showInputDialog(
            this,
            "Select export format:",
            "Export Report",
            JOptionPane.QUESTION_MESSAGE,
            null,
            formats,
            format
        );

        if (selectedFormat != null) {
            String result = reportingService.exportReport(reportId, selectedFormat);
            UIHelper.showSuccess(this, result);
            loadData();
        }
    }

    public void refreshData() {
        loadData();
    }

    // Category cell renderer
    private static class CategoryCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String category = value.toString();
                switch (category) {
                    case "SALES":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "INVENTORY":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "FINANCIAL":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "HR":
                        setBackground(new Color(230, 230, 250));
                        setForeground(new Color(75, 0, 130));
                        break;
                    case "PROJECT":
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
