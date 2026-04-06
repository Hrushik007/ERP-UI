package com.erp.view.panels.bi;

import com.erp.service.mock.MockAnalyticsService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * TrendAnalysisPanel displays statistical analysis, trend data, and anomaly detection.
 *
 * Contains:
 * - Statistical summary cards (mean, median, std dev, min, max)
 * - Trend data table for selected metric
 * - Anomaly detection table
 */
public class TrendAnalysisPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    // Metric selector
    private JComboBox<String> metricCombo;

    // Stat labels
    private JLabel meanLabel;
    private JLabel medianLabel;
    private JLabel stdDevLabel;
    private JLabel minLabel;
    private JLabel maxLabel;
    private JLabel countLabel;

    // Trend table
    private JTable trendTable;
    private DefaultTableModel trendModel;

    // Anomaly table
    private JTable anomalyTable;
    private DefaultTableModel anomalyModel;

    // Correlation display
    private JLabel correlationLabel;
    private JLabel correlationStrengthLabel;

    private static final String[] TREND_COLUMNS = {"Date", "Value"};
    private static final String[] ANOMALY_COLUMNS = {"Date", "Actual Value", "Expected Value", "Type", "Description"};

    public TrendAnalysisPanel() {
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
        // Metric selector
        List<String> metrics = analyticsService.getAvailableMetrics();
        metricCombo = new JComboBox<>(metrics.toArray(new String[0]));
        metricCombo.setFont(Constants.FONT_REGULAR);
        metricCombo.addActionListener(e -> loadData());

        // Stat labels
        meanLabel = createStatValue("0");
        medianLabel = createStatValue("0");
        stdDevLabel = createStatValue("0");
        minLabel = createStatValue("0");
        maxLabel = createStatValue("0");
        countLabel = createStatValue("0");

        // Correlation labels
        correlationLabel = new JLabel("0.00", SwingConstants.CENTER);
        correlationLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 24));
        correlationLabel.setForeground(Constants.PRIMARY_COLOR);

        correlationStrengthLabel = new JLabel("N/A", SwingConstants.CENTER);
        correlationStrengthLabel.setFont(Constants.FONT_REGULAR);
        correlationStrengthLabel.setForeground(Constants.TEXT_SECONDARY);

        // Trend table
        trendModel = new DefaultTableModel(TREND_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        trendTable = new JTable(trendModel);
        trendTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(trendTable);

        trendTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        trendTable.getColumnModel().getColumn(1).setPreferredWidth(120);

        // Anomaly table
        anomalyModel = new DefaultTableModel(ANOMALY_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        anomalyTable = new JTable(anomalyModel);
        anomalyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(anomalyTable);

        anomalyTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        anomalyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        anomalyTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        anomalyTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        anomalyTable.getColumnModel().getColumn(4).setPreferredWidth(250);

        // Anomaly type renderer
        anomalyTable.getColumnModel().getColumn(3).setCellRenderer(new AnomalyTypeRenderer());
    }

    private JLabel createStatValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));
        toolbar.add(new JLabel("Metric:"));
        toolbar.add(metricCombo);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        // Stats cards (1 row x 6)
        JPanel statsPanel = new JPanel(new GridLayout(1, 6, Constants.PADDING_SMALL, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        statsPanel.add(createStatCard("Mean", meanLabel, Constants.PRIMARY_COLOR));
        statsPanel.add(createStatCard("Median", medianLabel, Constants.SUCCESS_COLOR));
        statsPanel.add(createStatCard("Std Dev", stdDevLabel, new Color(253, 126, 20)));
        statsPanel.add(createStatCard("Min", minLabel, new Color(23, 162, 184)));
        statsPanel.add(createStatCard("Max", maxLabel, new Color(111, 66, 193)));
        statsPanel.add(createStatCard("Data Points", countLabel, Constants.SECONDARY_COLOR));

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(toolbar, BorderLayout.NORTH);
        topSection.add(statsPanel, BorderLayout.CENTER);

        // Left: Trend table + Correlation
        JPanel leftPanel = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        leftPanel.setOpaque(false);

        JPanel trendSection = createTableSection("Trend Data (Last 30 Days)", trendTable);

        // Correlation card
        JPanel corrCard = new JPanel(new BorderLayout(5, 5));
        corrCard.setBackground(Constants.BG_WHITE);
        corrCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel corrTitle = new JLabel("Correlation (Sales vs Revenue)");
        corrTitle.setFont(Constants.FONT_SUBTITLE);
        corrTitle.setForeground(Constants.TEXT_PRIMARY);

        JPanel corrCenter = new JPanel(new BorderLayout(0, 5));
        corrCenter.setOpaque(false);
        corrCenter.add(correlationLabel, BorderLayout.CENTER);
        corrCenter.add(correlationStrengthLabel, BorderLayout.SOUTH);

        corrCard.add(corrTitle, BorderLayout.NORTH);
        corrCard.add(corrCenter, BorderLayout.CENTER);
        corrCard.setPreferredSize(new Dimension(0, 100));

        leftPanel.add(trendSection, BorderLayout.CENTER);
        leftPanel.add(corrCard, BorderLayout.SOUTH);

        // Right: Anomaly table
        JPanel anomalySection = createTableSection("Detected Anomalies", anomalyTable);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, anomalySection);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(3, 3));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                          Constants.PADDING_SMALL, Constants.PADDING_SMALL)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 10));
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
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

    public void loadData() {
        String metric = (String) metricCombo.getSelectedItem();
        if (metric == null) metric = "sales";

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        // Load statistical summary
        Map<String, Double> stats = analyticsService.getStatisticalSummary(metric, startDate, endDate);
        meanLabel.setText(String.format("%.0f", stats.getOrDefault("mean", 0.0)));
        medianLabel.setText(String.format("%.0f", stats.getOrDefault("median", 0.0)));
        stdDevLabel.setText(String.format("%.0f", stats.getOrDefault("stdDev", 0.0)));
        minLabel.setText(String.format("%.0f", stats.getOrDefault("min", 0.0)));
        maxLabel.setText(String.format("%.0f", stats.getOrDefault("max", 0.0)));
        countLabel.setText(String.format("%.0f", stats.getOrDefault("count", 0.0)));

        // Load trend data
        trendModel.setRowCount(0);
        List<Map<String, Object>> trends = analyticsService.getTrend(metric, startDate, endDate, "DAY");
        for (Map<String, Object> t : trends) {
            trendModel.addRow(new Object[]{
                t.get("date").toString(),
                String.format("%.2f", (double) t.get("value"))
            });
        }

        // Load anomalies
        anomalyModel.setRowCount(0);
        List<Map<String, Object>> anomalies = analyticsService.detectAnomalies(metric, startDate, endDate);
        for (Map<String, Object> a : anomalies) {
            anomalyModel.addRow(new Object[]{
                a.get("date").toString(),
                String.format("%.0f", a.get("value")),
                String.format("%.0f", a.get("expected")),
                a.get("type"),
                a.get("description")
            });
        }

        // Load correlation
        Map<String, Object> corr = analyticsService.getCorrelation("sales", "revenue", startDate, endDate);
        double coefficient = (double) corr.getOrDefault("coefficient", 0.0);
        correlationLabel.setText(String.format("%.2f", coefficient));
        correlationStrengthLabel.setText((String) corr.getOrDefault("strength", "N/A"));

        if (coefficient > 0.5) {
            correlationLabel.setForeground(new Color(21, 87, 36));
        } else if (coefficient < -0.5) {
            correlationLabel.setForeground(new Color(114, 28, 36));
        } else {
            correlationLabel.setForeground(Constants.TEXT_SECONDARY);
        }
    }

    public void refreshData() {
        loadData();
    }

    // Anomaly type cell renderer
    private static class AnomalyTypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                if ("SPIKE".equals(value.toString())) {
                    setBackground(new Color(255, 243, 205));
                    setForeground(new Color(133, 100, 4));
                } else if ("DROP".equals(value.toString())) {
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
}
