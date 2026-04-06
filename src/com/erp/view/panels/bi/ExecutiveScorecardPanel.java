package com.erp.view.panels.bi;

import com.erp.service.mock.MockAnalyticsService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * ExecutiveScorecardPanel displays high-level business KPIs with growth indicators.
 *
 * Shows a grid of scorecard cards, each with:
 * - Metric name and current value
 * - Growth percentage (green for positive, red for negative)
 * - Real-time operational metrics at the bottom
 */
public class ExecutiveScorecardPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    // Scorecard value labels
    private JLabel revenueValue;
    private JLabel revenueGrowth;
    private JLabel ordersValue;
    private JLabel ordersGrowth;
    private JLabel customersValue;
    private JLabel customersGrowth;
    private JLabel avgOrderValue;
    private JLabel avgOrderGrowth;
    private JLabel marginValue;
    private JLabel marginGrowth;
    private JLabel employeesValue;
    private JLabel employeesGrowth;
    private JLabel satisfactionValue;
    private JLabel satisfactionGrowth;
    private JLabel efficiencyValue;
    private JLabel efficiencyGrowth;

    // Real-time labels
    private JLabel ordersTodayLabel;
    private JLabel revenueTodayLabel;
    private JLabel activeUsersLabel;
    private JLabel pendingOrdersLabel;
    private JLabel openTicketsLabel;
    private JLabel uptimeLabel;

    public ExecutiveScorecardPanel() {
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
        // Scorecard labels
        revenueValue = createMetricValue("$0");
        revenueGrowth = createGrowthLabel("+0%");
        ordersValue = createMetricValue("0");
        ordersGrowth = createGrowthLabel("+0%");
        customersValue = createMetricValue("0");
        customersGrowth = createGrowthLabel("+0%");
        avgOrderValue = createMetricValue("$0");
        avgOrderGrowth = createGrowthLabel("+0%");
        marginValue = createMetricValue("0%");
        marginGrowth = createGrowthLabel("+0%");
        employeesValue = createMetricValue("0");
        employeesGrowth = createGrowthLabel("+0%");
        satisfactionValue = createMetricValue("0%");
        satisfactionGrowth = createGrowthLabel("+0%");
        efficiencyValue = createMetricValue("0%");
        efficiencyGrowth = createGrowthLabel("+0%");

        // Real-time labels
        ordersTodayLabel = createMetricValue("0");
        revenueTodayLabel = createMetricValue("$0");
        activeUsersLabel = createMetricValue("0");
        pendingOrdersLabel = createMetricValue("0");
        openTicketsLabel = createMetricValue("0");
        uptimeLabel = createMetricValue("0%");
    }

    private JLabel createMetricValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 20));
        label.setForeground(Constants.TEXT_PRIMARY);
        return label;
    }

    private JLabel createGrowthLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
        return label;
    }

    private void layoutComponents() {
        // Toolbar with refresh
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        // Scorecard grid (2 rows x 4 columns)
        JPanel scorecardGrid = new JPanel(new GridLayout(2, 4, Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        scorecardGrid.setOpaque(false);

        scorecardGrid.add(createScorecardCard("Total Revenue", revenueValue, revenueGrowth, Constants.PRIMARY_COLOR));
        scorecardGrid.add(createScorecardCard("Total Orders", ordersValue, ordersGrowth, Constants.SUCCESS_COLOR));
        scorecardGrid.add(createScorecardCard("New Customers", customersValue, customersGrowth, new Color(23, 162, 184)));
        scorecardGrid.add(createScorecardCard("Avg Order Value", avgOrderValue, avgOrderGrowth, new Color(111, 66, 193)));
        scorecardGrid.add(createScorecardCard("Gross Margin", marginValue, marginGrowth, new Color(253, 126, 20)));
        scorecardGrid.add(createScorecardCard("Employees", employeesValue, employeesGrowth, Constants.SECONDARY_COLOR));
        scorecardGrid.add(createScorecardCard("Customer Satisfaction", satisfactionValue, satisfactionGrowth, Constants.SUCCESS_COLOR));
        scorecardGrid.add(createScorecardCard("Operational Efficiency", efficiencyValue, efficiencyGrowth, Constants.PRIMARY_COLOR));

        // Real-time section
        JPanel realTimeSection = new JPanel(new BorderLayout());
        realTimeSection.setBackground(Constants.BG_WHITE);
        realTimeSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel rtTitle = new JLabel("Real-Time Metrics");
        rtTitle.setFont(Constants.FONT_SUBTITLE);
        rtTitle.setForeground(Constants.TEXT_PRIMARY);
        rtTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JPanel rtGrid = new JPanel(new GridLayout(1, 6, Constants.PADDING_MEDIUM, 0));
        rtGrid.setOpaque(false);
        rtGrid.add(createMiniCard("Orders Today", ordersTodayLabel));
        rtGrid.add(createMiniCard("Revenue Today", revenueTodayLabel));
        rtGrid.add(createMiniCard("Active Users", activeUsersLabel));
        rtGrid.add(createMiniCard("Pending Orders", pendingOrdersLabel));
        rtGrid.add(createMiniCard("Open Tickets", openTicketsLabel));
        rtGrid.add(createMiniCard("Server Uptime", uptimeLabel));

        realTimeSection.add(rtTitle, BorderLayout.NORTH);
        realTimeSection.add(rtGrid, BorderLayout.CENTER);

        // Layout
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(toolbar, BorderLayout.NORTH);
        topSection.add(scorecardGrid, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(realTimeSection, BorderLayout.CENTER);
    }

    private JPanel createScorecardCard(String title, JLabel valueLabel, JLabel growthLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 8));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(0, 4));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 2));
        centerPanel.setOpaque(false);
        centerPanel.add(valueLabel, BorderLayout.CENTER);
        centerPanel.add(growthLabel, BorderLayout.SOUTH);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createMiniCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(3, 3));
        card.setOpaque(false);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Constants.FONT_FAMILY, Font.PLAIN, 10));
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        valueLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 16));

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    public void loadData() {
        // Load executive scorecard
        Map<String, Object> scorecard = analyticsService.getExecutiveScorecard();

        revenueValue.setText("$" + formatNumber((Double) scorecard.getOrDefault("totalRevenue", 0.0)));
        setGrowth(revenueGrowth, (Double) scorecard.getOrDefault("revenueGrowth", 0.0));

        ordersValue.setText(String.valueOf(scorecard.getOrDefault("totalOrders", 0)));
        setGrowth(ordersGrowth, (Double) scorecard.getOrDefault("orderGrowth", 0.0));

        customersValue.setText(String.valueOf(scorecard.getOrDefault("newCustomers", 0)));
        setGrowth(customersGrowth, (Double) scorecard.getOrDefault("customerGrowth", 0.0));

        avgOrderValue.setText("$" + String.format("%.2f", scorecard.getOrDefault("avgOrderValue", 0.0)));
        setGrowth(avgOrderGrowth, (Double) scorecard.getOrDefault("avgOrderGrowth", 0.0));

        marginValue.setText(scorecard.getOrDefault("grossMargin", 0.0) + "%");
        setGrowth(marginGrowth, (Double) scorecard.getOrDefault("marginGrowth", 0.0));

        employeesValue.setText(String.valueOf(scorecard.getOrDefault("employeeCount", 0)));
        setGrowth(employeesGrowth, (Double) scorecard.getOrDefault("employeeGrowth", 0.0));

        satisfactionValue.setText(scorecard.getOrDefault("customerSatisfaction", 0.0) + "%");
        setGrowth(satisfactionGrowth, (Double) scorecard.getOrDefault("satisfactionGrowth", 0.0));

        efficiencyValue.setText(scorecard.getOrDefault("operationalEfficiency", 0.0) + "%");
        setGrowth(efficiencyGrowth, (Double) scorecard.getOrDefault("efficiencyGrowth", 0.0));

        // Load real-time metrics
        Map<String, Object> realTime = analyticsService.getRealTimeMetrics();
        ordersTodayLabel.setText(String.valueOf(realTime.getOrDefault("ordersToday", 0)));
        revenueTodayLabel.setText("$" + String.format("%.2f", realTime.getOrDefault("revenueToday", 0.0)));
        activeUsersLabel.setText(String.valueOf(realTime.getOrDefault("activeUsers", 0)));
        pendingOrdersLabel.setText(String.valueOf(realTime.getOrDefault("pendingOrders", 0)));
        openTicketsLabel.setText(String.valueOf(realTime.getOrDefault("openTickets", 0)));
        uptimeLabel.setText(realTime.getOrDefault("serverUptime", 0.0) + "%");
    }

    private void setGrowth(JLabel label, double growth) {
        String prefix = growth >= 0 ? "+" : "";
        label.setText(prefix + String.format("%.1f%%", growth));
        if (growth > 0) {
            label.setForeground(new Color(21, 87, 36));
        } else if (growth < 0) {
            label.setForeground(new Color(114, 28, 36));
        } else {
            label.setForeground(Constants.TEXT_SECONDARY);
        }
    }

    private String formatNumber(double value) {
        if (value >= 1000000) {
            return String.format("%.1fM", value / 1000000);
        } else if (value >= 1000) {
            return String.format("%.0fK", value / 1000);
        }
        return String.format("%.0f", value);
    }

    public void refreshData() {
        loadData();
    }
}
