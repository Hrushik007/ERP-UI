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
 * CustomerInsightsPanel displays customer segmentation and churn risk analysis.
 *
 * Split into two sections:
 * - Top: Customer segments table with revenue share and descriptions
 * - Bottom: Churn risk analysis table with risk scores and levels
 */
public class CustomerInsightsPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    // Segments table
    private JTable segmentsTable;
    private DefaultTableModel segmentsModel;

    // Churn risk table
    private JTable churnTable;
    private DefaultTableModel churnModel;

    // Summary labels
    private JLabel totalSegmentsLabel;
    private JLabel totalCustomersLabel;
    private JLabel highRiskLabel;
    private JLabel atRiskLabel;

    private static final String[] SEGMENT_COLUMNS = {"Segment", "Customers", "Revenue Share %", "Avg Order Value", "Description"};
    private static final String[] CHURN_COLUMNS = {"Customer ID", "Customer Name", "Risk Score", "Risk Level", "Last Purchase", "Days Since Contact"};

    public CustomerInsightsPanel() {
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
        // Segments table
        segmentsModel = new DefaultTableModel(SEGMENT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        segmentsTable = new JTable(segmentsModel);
        segmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(segmentsTable);

        segmentsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        segmentsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        segmentsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        segmentsTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        segmentsTable.getColumnModel().getColumn(4).setPreferredWidth(250);

        // Churn table
        churnModel = new DefaultTableModel(CHURN_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        churnTable = new JTable(churnModel);
        churnTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(churnTable);

        churnTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        churnTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        churnTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        churnTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        churnTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        churnTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        // Risk level renderer
        churnTable.getColumnModel().getColumn(3).setCellRenderer(new RiskLevelRenderer());

        // Summary labels
        totalSegmentsLabel = createSummaryValue("0");
        totalCustomersLabel = createSummaryValue("0");
        highRiskLabel = createSummaryValue("0");
        atRiskLabel = createSummaryValue("0");
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

        summaryPanel.add(createSummaryCard("Segments", totalSegmentsLabel, Constants.PRIMARY_COLOR));
        summaryPanel.add(createSummaryCard("Total Customers", totalCustomersLabel, Constants.SUCCESS_COLOR));
        summaryPanel.add(createSummaryCard("High Risk", highRiskLabel, Constants.DANGER_COLOR));
        summaryPanel.add(createSummaryCard("At Risk Segment", atRiskLabel, Constants.WARNING_COLOR));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        // Segments section
        JPanel segmentsSection = createTableSection("Customer Segments", segmentsTable);

        // Churn section
        JPanel churnSection = createTableSection("Churn Risk Analysis", churnTable);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, segmentsSection, churnSection);
        splitPane.setDividerLocation(230);
        splitPane.setResizeWeight(0.4);
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
        // Load segments
        segmentsModel.setRowCount(0);
        List<Map<String, Object>> segments = analyticsService.getCustomerSegments();
        int totalCustomers = 0;
        int atRiskCount = 0;

        for (Map<String, Object> s : segments) {
            int count = (int) s.get("customerCount");
            totalCustomers += count;
            if ("At Risk".equals(s.get("segment"))) atRiskCount = count;

            segmentsModel.addRow(new Object[]{
                s.get("segment"),
                count,
                s.get("revenueShare") + "%",
                "$" + String.format("%.2f", s.get("avgOrderValue")),
                s.get("description")
            });
        }

        totalSegmentsLabel.setText(String.valueOf(segments.size()));
        totalCustomersLabel.setText(String.valueOf(totalCustomers));
        atRiskLabel.setText(String.valueOf(atRiskCount));

        // Load churn risk
        churnModel.setRowCount(0);
        List<Map<String, Object>> churnData = analyticsService.getChurnRiskAnalysis();
        int highRisk = 0;

        for (Map<String, Object> c : churnData) {
            String riskLevel = (String) c.get("riskLevel");
            if ("HIGH".equals(riskLevel)) highRisk++;

            churnModel.addRow(new Object[]{
                c.get("customerId"),
                c.get("customerName"),
                c.get("riskScore"),
                riskLevel,
                c.get("lastPurchase"),
                c.get("daysSinceContact")
            });
        }

        highRiskLabel.setText(String.valueOf(highRisk));
    }

    public void refreshData() {
        loadData();
    }

    // Risk level cell renderer
    private static class RiskLevelRenderer extends DefaultTableCellRenderer {
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
