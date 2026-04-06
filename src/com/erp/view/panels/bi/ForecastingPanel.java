package com.erp.view.panels.bi;

import com.erp.service.mock.MockAnalyticsService;
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
 * ForecastingPanel displays sales forecasts and reorder recommendations.
 *
 * Split into two sections:
 * - Top: Sales forecast table with confidence levels
 * - Bottom: Reorder recommendations table with urgency levels
 */
public class ForecastingPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    // Forecast table
    private JTable forecastTable;
    private DefaultTableModel forecastModel;

    // Reorder table
    private JTable reorderTable;
    private DefaultTableModel reorderModel;

    // Summary labels
    private JLabel forecastDaysLabel;
    private JLabel avgForecastLabel;
    private JLabel highConfidenceLabel;
    private JLabel reorderItemsLabel;

    private JComboBox<String> daysCombo;

    private static final String[] FORECAST_COLUMNS = {"Date", "Forecasted Value ($)", "Confidence"};
    private static final String[] REORDER_COLUMNS = {"Product", "Current Stock", "Forecast Demand", "Reorder Qty", "Urgency"};

    public ForecastingPanel() {
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
        // Days selector
        daysCombo = new JComboBox<>(new String[]{"7 Days", "14 Days", "30 Days"});
        daysCombo.setFont(Constants.FONT_REGULAR);
        daysCombo.addActionListener(e -> loadData());

        // Forecast table
        forecastModel = new DefaultTableModel(FORECAST_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        forecastTable = new JTable(forecastModel);
        forecastTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(forecastTable);

        forecastTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        forecastTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        forecastTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Confidence column renderer
        forecastTable.getColumnModel().getColumn(2).setCellRenderer(new ConfidenceCellRenderer());

        // Reorder table
        reorderModel = new DefaultTableModel(REORDER_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reorderTable = new JTable(reorderModel);
        reorderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(reorderTable);

        reorderTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        reorderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        reorderTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        reorderTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        reorderTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Urgency renderer
        reorderTable.getColumnModel().getColumn(4).setCellRenderer(new UrgencyCellRenderer());

        // Summary labels
        forecastDaysLabel = createSummaryValue("0");
        avgForecastLabel = createSummaryValue("$0");
        highConfidenceLabel = createSummaryValue("0");
        reorderItemsLabel = createSummaryValue("0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        summaryPanel.add(createSummaryCard("Forecast Days", forecastDaysLabel, Constants.PRIMARY_COLOR));
        summaryPanel.add(createSummaryCard("Avg Forecast", avgForecastLabel, Constants.SUCCESS_COLOR));
        summaryPanel.add(createSummaryCard("High Confidence", highConfidenceLabel, new Color(23, 162, 184)));
        summaryPanel.add(createSummaryCard("Reorder Items", reorderItemsLabel, Constants.DANGER_COLOR));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));
        toolbar.add(new JLabel("Forecast Period:"));
        toolbar.add(daysCombo);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        // Forecast section
        JPanel forecastSection = createTableSection("Sales Forecast", forecastTable);

        // Reorder section
        JPanel reorderSection = createTableSection("Reorder Recommendations", reorderTable);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, forecastSection, reorderSection);
        splitPane.setDividerLocation(280);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createTableSection(String title, JTable table) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Constants.BG_WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(Constants.FONT_SUBTITLE);
        sectionTitle.setForeground(Constants.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        section.add(sectionTitle, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
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

    public void loadData() {
        // Determine days
        String selected = (String) daysCombo.getSelectedItem();
        int days = selected.startsWith("7") ? 7 : selected.startsWith("14") ? 14 : 30;

        // Load forecasts
        forecastModel.setRowCount(0);
        List<Map<String, Object>> forecasts = analyticsService.getSalesForecast(days);
        double totalForecast = 0;
        int highCount = 0;

        for (Map<String, Object> f : forecasts) {
            String dateStr = f.get("date").toString();
            double value = (double) f.get("value");
            String confidence = (String) f.get("confidence");

            forecastModel.addRow(new Object[]{
                dateStr,
                String.format("$%.2f", value),
                confidence
            });

            totalForecast += value;
            if ("HIGH".equals(confidence)) highCount++;
        }

        forecastDaysLabel.setText(String.valueOf(days));
        avgForecastLabel.setText(forecasts.isEmpty() ? "$0" :
                String.format("$%.0f", totalForecast / forecasts.size()));
        highConfidenceLabel.setText(String.valueOf(highCount));

        // Load reorder recommendations
        reorderModel.setRowCount(0);
        List<Map<String, Object>> reorders = analyticsService.getReorderRecommendations();

        for (Map<String, Object> r : reorders) {
            reorderModel.addRow(new Object[]{
                r.get("productName"),
                r.get("currentStock"),
                r.get("forecastDemand"),
                r.get("reorderQuantity"),
                r.get("urgency")
            });
        }

        reorderItemsLabel.setText(String.valueOf(reorders.size()));
    }

    public void refreshData() {
        loadData();
    }

    // Confidence cell renderer
    private static class ConfidenceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "HIGH":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "MEDIUM":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "LOW":
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

    // Urgency cell renderer
    private static class UrgencyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "HIGH":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "MEDIUM":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "LOW":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
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
