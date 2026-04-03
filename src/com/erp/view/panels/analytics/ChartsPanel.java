package com.erp.view.panels.analytics;

import com.erp.model.ChartConfig;
import com.erp.service.mock.MockAnalyticsService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.ChartDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ChartsPanel displays and manages chart configurations.
 */
public class ChartsPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    private JTable chartsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryFilter;
    private JComboBox<String> typeFilter;

    private JLabel totalChartsLabel;
    private JLabel activeChartsLabel;

    private static final String[] COLUMNS = {"ID", "Code", "Chart Name", "Type", "Category", "X-Axis", "Y-Axis", "Time Range", "Active"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ChartsPanel() {
        analyticsService = MockAnalyticsService.getInstance();
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
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "SALES", "FINANCIAL", "OPERATIONS", "HR", "INVENTORY", "PROJECT", "CUSTOMER"});
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addActionListener(e -> loadData());

        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "BAR", "LINE", "PIE", "DOUGHNUT", "AREA", "SCATTER", "GAUGE", "TABLE"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        chartsTable = new JTable(tableModel);
        chartsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(chartsTable);

        // Column widths
        chartsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        chartsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        chartsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        chartsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        chartsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        chartsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        chartsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        chartsTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        chartsTable.getColumnModel().getColumn(8).setPreferredWidth(60);

        // Custom renderers
        chartsTable.getColumnModel().getColumn(3).setCellRenderer(new ChartTypeCellRenderer());
        chartsTable.getColumnModel().getColumn(4).setCellRenderer(new CategoryCellRenderer());
        chartsTable.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalChartsLabel = createSummaryValue("0");
        activeChartsLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(chartsTable);
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
        JPanel panel = new JPanel(new GridLayout(1, 2, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Charts", totalChartsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Active Charts", activeChartsLabel, Constants.SUCCESS_COLOR));

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

        JButton addBtn = UIHelper.createPrimaryButton("New Chart");
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.addActionListener(e -> addChart());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editChart());
        toolbar.add(editBtn);

        JButton previewBtn = new JButton("Preview");
        previewBtn.setFont(Constants.FONT_BUTTON);
        previewBtn.setBackground(Constants.SUCCESS_COLOR);
        previewBtn.setForeground(Color.WHITE);
        previewBtn.setOpaque(true);
        previewBtn.setBorderPainted(false);
        previewBtn.setFocusPainted(false);
        previewBtn.setPreferredSize(new Dimension(90, 30));
        previewBtn.addActionListener(e -> previewChart());
        toolbar.add(previewBtn);

        JButton duplicateBtn = UIHelper.createSecondaryButton("Duplicate");
        duplicateBtn.setPreferredSize(new Dimension(90, 30));
        duplicateBtn.addActionListener(e -> duplicateChart());
        toolbar.add(duplicateBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(Constants.FONT_BUTTON);
        deleteBtn.setBackground(Constants.DANGER_COLOR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        deleteBtn.addActionListener(e -> deleteChart());
        toolbar.add(deleteBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<ChartConfig> charts = analyticsService.getAllCharts();
        String categorySelection = (String) categoryFilter.getSelectedItem();
        String typeSelection = (String) typeFilter.getSelectedItem();

        int totalCount = 0;
        int activeCount = 0;

        for (ChartConfig c : charts) {
            if (!"All Categories".equals(categorySelection) && !categorySelection.equals(c.getCategory())) {
                continue;
            }
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(c.getChartType())) {
                continue;
            }

            totalCount++;
            if (c.isActive()) activeCount++;

            tableModel.addRow(new Object[]{
                c.getChartId(),
                c.getChartCode(),
                c.getName(),
                c.getChartType(),
                c.getCategory(),
                c.getXAxis(),
                c.getYAxis(),
                c.getTimeRange(),
                c.isActive() ? "Yes" : "No"
            });
        }

        totalChartsLabel.setText(String.valueOf(totalCount));
        activeChartsLabel.setText(String.valueOf(activeCount));
    }

    private void addChart() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ChartDialog dialog = new ChartDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ChartConfig newChart = dialog.getChartConfig();
            analyticsService.createChart(newChart);
            UIHelper.showSuccess(this, "Chart created successfully.");
            loadData();
        }
    }

    private void editChart() {
        int row = chartsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a chart to edit.");
            return;
        }

        int chartId = (int) tableModel.getValueAt(row, 0);
        ChartConfig chart = analyticsService.getChartById(chartId);

        if (chart != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ChartDialog dialog = new ChartDialog(parentFrame, chart);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                analyticsService.updateChart(dialog.getChartConfig());
                UIHelper.showSuccess(this, "Chart updated successfully.");
                loadData();
            }
        }
    }

    private void previewChart() {
        int row = chartsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a chart to preview.");
            return;
        }

        String chartName = (String) tableModel.getValueAt(row, 2);
        String chartType = (String) tableModel.getValueAt(row, 3);

        UIHelper.showSuccess(this, "Preview for '" + chartName + "' (" + chartType + " chart) would open here.");
    }

    private void duplicateChart() {
        int row = chartsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a chart to duplicate.");
            return;
        }

        int chartId = (int) tableModel.getValueAt(row, 0);
        ChartConfig original = analyticsService.getChartById(chartId);

        if (original != null) {
            ChartConfig copy = new ChartConfig();
            copy.setChartCode("CHT-" + System.currentTimeMillis() % 10000);
            copy.setName(original.getName() + " (Copy)");
            copy.setDescription(original.getDescription());
            copy.setChartType(original.getChartType());
            copy.setCategory(original.getCategory());
            copy.setDataSource(original.getDataSource());
            copy.setXAxis(original.getXAxis());
            copy.setYAxis(original.getYAxis());
            copy.setGroupBy(original.getGroupBy());
            copy.setFilters(original.getFilters());
            copy.setColors(original.getColors());
            copy.setShowLegend(original.isShowLegend());
            copy.setShowLabels(original.isShowLabels());
            copy.setShowGrid(original.isShowGrid());
            copy.setTimeRange(original.getTimeRange());
            copy.setRefreshInterval(original.getRefreshInterval());
            copy.setActive(true);

            analyticsService.createChart(copy);
            UIHelper.showSuccess(this, "Chart duplicated successfully.");
            loadData();
        }
    }

    private void deleteChart() {
        int row = chartsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a chart to delete.");
            return;
        }

        int chartId = (int) tableModel.getValueAt(row, 0);
        String chartName = (String) tableModel.getValueAt(row, 2);

        boolean confirm = UIHelper.showConfirm(this, "Delete chart '" + chartName + "'? This cannot be undone.");
        if (confirm) {
            if (analyticsService.deleteChart(chartId)) {
                UIHelper.showSuccess(this, "Chart deleted successfully.");
                loadData();
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Chart Type cell renderer
    private static class ChartTypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String type = value.toString();
                switch (type) {
                    case "BAR":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "LINE":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "PIE":
                    case "DOUGHNUT":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "AREA":
                        setBackground(new Color(230, 230, 250));
                        setForeground(new Color(75, 0, 130));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Category cell renderer
    private static class CategoryCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
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
                if ("Yes".equals(value.toString())) {
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
