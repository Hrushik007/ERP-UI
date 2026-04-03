package com.erp.view.panels.analytics;

import com.erp.model.KPI;
import com.erp.service.mock.MockAnalyticsService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.KPIDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * KPIsPanel displays and manages Key Performance Indicators.
 */
public class KPIsPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    private JTable kpiTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryFilter;

    private JLabel totalKPIsLabel;
    private JLabel onTargetLabel;
    private JLabel belowTargetLabel;

    private static final String[] COLUMNS = {"ID", "Code", "KPI Name", "Category", "Target", "Actual", "Achievement", "Trend", "Status"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public KPIsPanel() {
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
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "SALES", "FINANCIAL", "OPERATIONS", "HR", "CUSTOMER", "QUALITY"});
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        kpiTable = new JTable(tableModel);
        kpiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(kpiTable);

        // Column widths
        kpiTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        kpiTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        kpiTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        kpiTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        kpiTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        kpiTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        kpiTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        kpiTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        kpiTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        // Custom renderers
        kpiTable.getColumnModel().getColumn(3).setCellRenderer(new CategoryCellRenderer());
        kpiTable.getColumnModel().getColumn(6).setCellRenderer(new AchievementCellRenderer());
        kpiTable.getColumnModel().getColumn(7).setCellRenderer(new TrendCellRenderer());
        kpiTable.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalKPIsLabel = createSummaryValue("0");
        onTargetLabel = createSummaryValue("0");
        belowTargetLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(kpiTable);
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

        panel.add(createSummaryCard("Total KPIs", totalKPIsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("On Target", onTargetLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Below Target", belowTargetLabel, Constants.DANGER_COLOR));

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

        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        JButton addBtn = UIHelper.createPrimaryButton("New KPI");
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.addActionListener(e -> addKPI());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editKPI());
        toolbar.add(editBtn);

        JButton refreshKPIBtn = new JButton("Refresh KPI");
        refreshKPIBtn.setFont(Constants.FONT_BUTTON);
        refreshKPIBtn.setBackground(Constants.SUCCESS_COLOR);
        refreshKPIBtn.setForeground(Color.WHITE);
        refreshKPIBtn.setOpaque(true);
        refreshKPIBtn.setBorderPainted(false);
        refreshKPIBtn.setFocusPainted(false);
        refreshKPIBtn.setPreferredSize(new Dimension(100, 30));
        refreshKPIBtn.addActionListener(e -> refreshSelectedKPI());
        toolbar.add(refreshKPIBtn);

        JButton refreshAllBtn = UIHelper.createSecondaryButton("Refresh All");
        refreshAllBtn.setPreferredSize(new Dimension(100, 30));
        refreshAllBtn.addActionListener(e -> refreshAllKPIs());
        toolbar.add(refreshAllBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(Constants.FONT_BUTTON);
        deleteBtn.setBackground(Constants.DANGER_COLOR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        deleteBtn.addActionListener(e -> deleteKPI());
        toolbar.add(deleteBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<KPI> kpiList = analyticsService.getAllKPIs();
        String categorySelection = (String) categoryFilter.getSelectedItem();

        int totalCount = 0;
        int onTargetCount = 0;
        int belowTargetCount = 0;

        for (KPI k : kpiList) {
            if (!"All Categories".equals(categorySelection) && !categorySelection.equals(k.getCategory())) {
                continue;
            }

            totalCount++;

            BigDecimal achievement = k.getAchievement();
            if (achievement.compareTo(new BigDecimal("90")) >= 0) {
                onTargetCount++;
            } else if (achievement.compareTo(new BigDecimal("70")) < 0) {
                belowTargetCount++;
            }

            String trendDisplay = k.getTrend();
            if (k.getTrendPercentage() != null) {
                trendDisplay += " (" + (k.getTrendPercentage().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") +
                               k.getTrendPercentage() + "%)";
            }

            tableModel.addRow(new Object[]{
                k.getKpiId(),
                k.getKpiCode(),
                k.getName(),
                k.getCategory(),
                formatValue(k.getTargetValue(), k.getUnit()),
                formatValue(k.getActualValue(), k.getUnit()),
                achievement + "%",
                trendDisplay,
                k.getStatus()
            });
        }

        totalKPIsLabel.setText(String.valueOf(totalCount));
        onTargetLabel.setText(String.valueOf(onTargetCount));
        belowTargetLabel.setText(String.valueOf(belowTargetCount));
        belowTargetLabel.setForeground(belowTargetCount > 0 ? Constants.DANGER_COLOR : Constants.PRIMARY_COLOR);
    }

    private String formatValue(BigDecimal value, String unit) {
        if (value == null) return "-";
        if ("CURRENCY".equals(unit)) {
            return "$" + String.format("%,.0f", value.doubleValue());
        } else if ("PERCENTAGE".equals(unit)) {
            return value + "%";
        }
        return value.toString();
    }

    private void addKPI() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        KPIDialog dialog = new KPIDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            KPI newKPI = dialog.getKPI();
            analyticsService.createKPI(newKPI);
            UIHelper.showSuccess(this, "KPI created successfully.");
            loadData();
        }
    }

    private void editKPI() {
        int row = kpiTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a KPI to edit.");
            return;
        }

        int kpiId = (int) tableModel.getValueAt(row, 0);
        KPI kpi = analyticsService.getKPIById(kpiId);

        if (kpi != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            KPIDialog dialog = new KPIDialog(parentFrame, kpi);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                analyticsService.updateKPI(dialog.getKPI());
                UIHelper.showSuccess(this, "KPI updated successfully.");
                loadData();
            }
        }
    }

    private void refreshSelectedKPI() {
        int row = kpiTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a KPI to refresh.");
            return;
        }

        int kpiId = (int) tableModel.getValueAt(row, 0);
        if (analyticsService.refreshKPI(kpiId)) {
            UIHelper.showSuccess(this, "KPI data refreshed.");
            loadData();
        }
    }

    private void refreshAllKPIs() {
        if (analyticsService.refreshAllKPIs()) {
            UIHelper.showSuccess(this, "All KPIs refreshed.");
            loadData();
        }
    }

    private void deleteKPI() {
        int row = kpiTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a KPI to delete.");
            return;
        }

        int kpiId = (int) tableModel.getValueAt(row, 0);
        String kpiName = (String) tableModel.getValueAt(row, 2);

        boolean confirm = UIHelper.showConfirm(this, "Delete KPI '" + kpiName + "'? This cannot be undone.");
        if (confirm) {
            if (analyticsService.deleteKPI(kpiId)) {
                UIHelper.showSuccess(this, "KPI deleted successfully.");
                loadData();
            }
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
                    case "FINANCIAL":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "OPERATIONS":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "HR":
                        setBackground(new Color(255, 228, 225));
                        setForeground(new Color(139, 69, 19));
                        break;
                    case "CUSTOMER":
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

    // Achievement cell renderer
    private static class AchievementCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String val = value.toString().replace("%", "");
                try {
                    double achievement = Double.parseDouble(val);
                    if (achievement >= 90) {
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                    } else if (achievement >= 70) {
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                    } else {
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                    }
                } catch (NumberFormatException e) {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Trend cell renderer
    private static class TrendCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String trend = value.toString();
                if (trend.startsWith("UP")) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else if (trend.startsWith("DOWN")) {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                } else {
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
                switch (status) {
                    case "GOOD":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "WARNING":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "CRITICAL":
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
