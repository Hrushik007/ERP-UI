package com.erp.view.panels.bi;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * BIPanel - Main container for Business Intelligence module with tabbed navigation.
 *
 * Covers the advanced analytics capabilities from the AnalyticsService interface:
 * - Executive Scorecard: High-level business metrics and growth indicators
 * - Forecasting: Sales and demand forecasting with confidence levels
 * - Customer Insights: Customer segmentation and churn risk analysis
 * - Trend Analysis: Statistical summaries, trends, and anomaly detection
 */
public class BIPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private ExecutiveScorecardPanel scorecardPanel;
    private ForecastingPanel forecastingPanel;
    private CustomerInsightsPanel customerInsightsPanel;
    private TrendAnalysisPanel trendAnalysisPanel;

    public BIPanel() {
        super(Constants.MODULE_BI);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        scorecardPanel = new ExecutiveScorecardPanel();
        forecastingPanel = new ForecastingPanel();
        customerInsightsPanel = new CustomerInsightsPanel();
        trendAnalysisPanel = new TrendAnalysisPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Executive Scorecard", scorecardPanel);
        tabbedPane.addTab("Forecasting", forecastingPanel);
        tabbedPane.addTab("Customer Insights", customerInsightsPanel);
        tabbedPane.addTab("Trend Analysis", trendAnalysisPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: scorecardPanel.refreshData(); break;
                case 1: forecastingPanel.refreshData(); break;
                case 2: customerInsightsPanel.refreshData(); break;
                case 3: trendAnalysisPanel.refreshData(); break;
            }
        });
    }

    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        int index = tabbedPane.getSelectedIndex();
        switch (index) {
            case 0: scorecardPanel.refreshData(); break;
            case 1: forecastingPanel.refreshData(); break;
            case 2: customerInsightsPanel.refreshData(); break;
            case 3: trendAnalysisPanel.refreshData(); break;
        }
    }
}
