package com.erp.view.panels.marketing;

import com.erp.model.Campaign;
import com.erp.service.mock.MockMarketingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * MarketingAnalyticsPanel displays marketing performance metrics.
 *
 * Shows:
 * - Top-level KPI summary cards (total spend, total leads, avg cost per lead, overall ROI)
 * - Spend breakdown by campaign type
 * - Per-campaign performance table with ROI, cost-per-lead, budget variance
 */
public class MarketingAnalyticsPanel extends JPanel {

    private MockMarketingService marketingService;

    // KPI labels
    private JLabel totalSpendLabel;
    private JLabel totalLeadsLabel;
    private JLabel avgCostPerLeadLabel;
    private JLabel overallROILabel;

    // Spend by type table
    private JTable spendByTypeTable;
    private DefaultTableModel spendByTypeModel;

    // Campaign performance table
    private JTable performanceTable;
    private DefaultTableModel performanceModel;

    private static final String[] SPEND_COLUMNS = {"Campaign Type", "Total Spend", "Campaigns"};
    private static final String[] PERFORMANCE_COLUMNS = {"Campaign", "Type", "Status", "Budget", "Spent", "Variance", "Leads", "Cost/Lead", "ROI %"};

    public MarketingAnalyticsPanel() {
        marketingService = MockMarketingService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // KPI summary labels
        totalSpendLabel = createSummaryValue("$0");
        totalLeadsLabel = createSummaryValue("0");
        avgCostPerLeadLabel = createSummaryValue("$0");
        overallROILabel = createSummaryValue("0%");

        // Spend by type table
        spendByTypeModel = new DefaultTableModel(SPEND_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        spendByTypeTable = new JTable(spendByTypeModel);
        spendByTypeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(spendByTypeTable);

        spendByTypeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        spendByTypeTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        spendByTypeTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        // Campaign performance table
        performanceModel = new DefaultTableModel(PERFORMANCE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        performanceTable = new JTable(performanceModel);
        performanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(performanceTable);

        performanceTable.getColumnModel().getColumn(0).setPreferredWidth(180);  // Campaign
        performanceTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Type
        performanceTable.getColumnModel().getColumn(2).setPreferredWidth(80);   // Status
        performanceTable.getColumnModel().getColumn(3).setPreferredWidth(90);   // Budget
        performanceTable.getColumnModel().getColumn(4).setPreferredWidth(90);   // Spent
        performanceTable.getColumnModel().getColumn(5).setPreferredWidth(90);   // Variance
        performanceTable.getColumnModel().getColumn(6).setPreferredWidth(50);   // Leads
        performanceTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Cost/Lead
        performanceTable.getColumnModel().getColumn(8).setPreferredWidth(70);   // ROI

        // ROI column renderer
        performanceTable.getColumnModel().getColumn(8).setCellRenderer(new ROICellRenderer());
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // KPI Summary cards at top
        JPanel kpiPanel = createKPIPanel();

        // Middle section: Spend by type
        JPanel spendSection = createSpendByTypeSection();

        // Bottom section: Campaign performance
        JPanel performanceSection = createPerformanceSection();

        // Toolbar with refresh
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        // Top section: KPIs + toolbar
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(kpiPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        // Center split: spend by type (top) + performance (bottom)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spendSection, performanceSection);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);
        splitPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createKPIPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Spend", totalSpendLabel, new Color(231, 76, 60)));
        panel.add(createSummaryCard("Total Leads", totalLeadsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Avg Cost/Lead", avgCostPerLeadLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Overall ROI", overallROILabel, new Color(111, 66, 193)));

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

    private JPanel createSpendByTypeSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Constants.BG_WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel sectionTitle = new JLabel("Spend by Campaign Type");
        sectionTitle.setFont(Constants.FONT_SUBTITLE);
        sectionTitle.setForeground(Constants.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane scrollPane = new JScrollPane(spendByTypeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        section.add(sectionTitle, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    private JPanel createPerformanceSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Constants.BG_WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel sectionTitle = new JLabel("Campaign Performance");
        sectionTitle.setFont(Constants.FONT_SUBTITLE);
        sectionTitle.setForeground(Constants.TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane scrollPane = new JScrollPane(performanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        section.add(sectionTitle, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    public void loadData() {
        loadKPIs();
        loadSpendByType();
        loadPerformance();
    }

    private void loadKPIs() {
        BigDecimal totalSpend = marketingService.getTotalSpend();
        int totalLeads = marketingService.getTotalLeadsGenerated();

        totalSpendLabel.setText("$" + totalSpend.toPlainString());
        totalLeadsLabel.setText(String.valueOf(totalLeads));

        if (totalLeads > 0) {
            BigDecimal avgCost = totalSpend.divide(new BigDecimal(totalLeads), 2, RoundingMode.HALF_UP);
            avgCostPerLeadLabel.setText("$" + avgCost.toPlainString());
        } else {
            avgCostPerLeadLabel.setText("$0");
        }

        // Overall ROI: assume each lead worth $150
        if (totalSpend.compareTo(BigDecimal.ZERO) > 0) {
            double revenue = totalLeads * 150.0;
            double roi = (revenue - totalSpend.doubleValue()) / totalSpend.doubleValue() * 100;
            overallROILabel.setText(String.format("%.1f%%", roi));
        } else {
            overallROILabel.setText("0%");
        }
    }

    private void loadSpendByType() {
        spendByTypeModel.setRowCount(0);

        Map<String, Integer> countByType = marketingService.getCampaignCountByType();
        Map<String, BigDecimal> spendByType = marketingService.getSpendByType(
                java.time.LocalDate.now().minusYears(1), java.time.LocalDate.now());

        for (Map.Entry<String, BigDecimal> entry : spendByType.entrySet()) {
            spendByTypeModel.addRow(new Object[]{
                entry.getKey(),
                "$" + entry.getValue().toPlainString(),
                countByType.getOrDefault(entry.getKey(), 0)
            });
        }
    }

    private void loadPerformance() {
        performanceModel.setRowCount(0);

        List<Campaign> campaigns = marketingService.getAllCampaigns();

        for (Campaign c : campaigns) {
            BigDecimal budget = c.getBudget() != null ? c.getBudget() : BigDecimal.ZERO;
            BigDecimal spent = c.getActualSpend() != null ? c.getActualSpend() : BigDecimal.ZERO;
            BigDecimal variance = budget.subtract(spent);

            BigDecimal costPerLead = BigDecimal.ZERO;
            if (c.getLeadsGenerated() > 0 && spent.compareTo(BigDecimal.ZERO) > 0) {
                costPerLead = spent.divide(new BigDecimal(c.getLeadsGenerated()), 2, RoundingMode.HALF_UP);
            }

            double roi = 0;
            if (spent.compareTo(BigDecimal.ZERO) > 0) {
                double revenue = c.getLeadsGenerated() * 150.0;
                roi = (revenue - spent.doubleValue()) / spent.doubleValue() * 100;
            }

            performanceModel.addRow(new Object[]{
                c.getName(),
                c.getType(),
                c.getStatus(),
                "$" + budget.toPlainString(),
                "$" + spent.toPlainString(),
                "$" + variance.toPlainString(),
                c.getLeadsGenerated(),
                "$" + costPerLead.toPlainString(),
                String.format("%.1f%%", roi)
            });
        }
    }

    public void refreshData() {
        loadData();
    }

    // ROI cell renderer - green for positive, red for negative
    private static class ROICellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null && !isSelected) {
                String text = value.toString().replace("%", "").trim();
                try {
                    double roiValue = Double.parseDouble(text);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    if (roiValue > 0) {
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                    } else if (roiValue < 0) {
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                    } else {
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                    }
                } catch (NumberFormatException e) {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    }
}
