package com.erp.service.interfaces;

import com.erp.model.ChartConfig;
import com.erp.model.Dashboard;
import com.erp.model.KPI;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AnalyticsService Interface - CONTRACT for Data Analytics & BI Module Backend Team
 *
 * Covers: Descriptive Analytics, Predictive Analytics, Data Integration, Dashboards,
 *         KPI Management, Chart Configuration
 *
 * This service provides analytical insights beyond basic reporting.
 */
public interface AnalyticsService {

    // ==================== DASHBOARD MANAGEMENT ====================

    List<Dashboard> getAllDashboards();
    List<Dashboard> getDashboardsByCategory(String category);
    List<Dashboard> getPublicDashboards();
    Dashboard getDashboardById(int dashboardId);
    Dashboard getDashboardByCode(String dashboardCode);
    Dashboard createDashboard(Dashboard dashboard);
    boolean updateDashboard(Dashboard dashboard);
    boolean deleteDashboard(int dashboardId);
    boolean setDefaultDashboard(int dashboardId);

    // ==================== KPI MANAGEMENT ====================

    List<KPI> getAllKPIs();
    List<KPI> getKPIsByCategory(String category);
    List<KPI> getActiveKPIs();
    KPI getKPIById(int kpiId);
    KPI getKPIByCode(String kpiCode);
    KPI createKPI(KPI kpi);
    boolean updateKPI(KPI kpi);
    boolean deleteKPI(int kpiId);
    boolean refreshKPI(int kpiId);
    boolean refreshAllKPIs();

    // ==================== CHART CONFIGURATION ====================

    List<ChartConfig> getAllCharts();
    List<ChartConfig> getChartsByCategory(String category);
    List<ChartConfig> getChartsByType(String chartType);
    ChartConfig getChartById(int chartId);
    ChartConfig getChartByCode(String chartCode);
    ChartConfig createChart(ChartConfig chart);
    boolean updateChart(ChartConfig chart);
    boolean deleteChart(int chartId);

    // ==================== STATISTICS ====================

    int getTotalDashboards();
    int getTotalKPIs();
    int getTotalCharts();
    int getKPIsOnTarget();
    int getKPIsBelowTarget();

    // ==================== DESCRIPTIVE ANALYTICS ====================

    /**
     * Get key performance indicators (KPIs).
     * @return Map of KPI name to value
     */
    Map<String, Object> getKPIs();

    /**
     * Get trend analysis for a metric.
     * @param metricName The metric to analyze (sales, revenue, orders, etc.)
     * @param startDate Start date
     * @param endDate End date
     * @param granularity DAY, WEEK, MONTH
     * @return List of data points with date and value
     */
    List<Map<String, Object>> getTrend(String metricName, LocalDate startDate, LocalDate endDate, String granularity);

    /**
     * Get comparison analysis (current vs previous period).
     * @param metricName The metric to compare
     * @param currentStart Current period start
     * @param currentEnd Current period end
     * @return Map with current, previous, change, percentChange
     */
    Map<String, Object> getPeriodComparison(String metricName, LocalDate currentStart, LocalDate currentEnd);


    // ==================== PREDICTIVE ANALYTICS ====================

    /**
     * Get sales forecast.
     * @param daysAhead Number of days to forecast
     * @return List of forecasted values by date
     */
    List<Map<String, Object>> getSalesForecast(int daysAhead);

    /**
     * Get demand forecast for a product.
     * @param productId The product ID
     * @param daysAhead Number of days to forecast
     * @return List of forecasted demand by date
     */
    List<Map<String, Object>> getDemandForecast(int productId, int daysAhead);

    /**
     * Get customer churn risk analysis.
     * @return List of customers with churn risk score
     */
    List<Map<String, Object>> getChurnRiskAnalysis();

    /**
     * Get reorder recommendations.
     * @return List of products with recommended reorder quantities
     */
    List<Map<String, Object>> getReorderRecommendations();


    // ==================== SEGMENTATION & CLUSTERING ====================

    /**
     * Get customer segments.
     * @return List of segments with customer counts and characteristics
     */
    List<Map<String, Object>> getCustomerSegments();

    /**
     * Get product performance categories.
     * @return Map categorizing products (stars, cash cows, dogs, etc.)
     */
    Map<String, List<Integer>> getProductCategories();


    // ==================== STATISTICAL ANALYSIS ====================

    /**
     * Get statistical summary for a metric.
     * @param metricName The metric to analyze
     * @param startDate Start date
     * @param endDate End date
     * @return Map with mean, median, stdDev, min, max, quartiles
     */
    Map<String, Double> getStatisticalSummary(String metricName, LocalDate startDate, LocalDate endDate);

    /**
     * Get correlation analysis between two metrics.
     * @param metric1 First metric
     * @param metric2 Second metric
     * @param startDate Start date
     * @param endDate End date
     * @return Correlation coefficient and related data
     */
    Map<String, Object> getCorrelation(String metric1, String metric2, LocalDate startDate, LocalDate endDate);


    // ==================== ANOMALY DETECTION ====================

    /**
     * Detect anomalies in a metric.
     * @param metricName The metric to analyze
     * @param startDate Start date
     * @param endDate End date
     * @return List of anomalies with date and details
     */
    List<Map<String, Object>> detectAnomalies(String metricName, LocalDate startDate, LocalDate endDate);


    // ==================== DASHBOARDS ====================

    /**
     * Get executive scorecard data.
     * @return Map with scorecard metrics and targets
     */
    Map<String, Object> getExecutiveScorecard();

    /**
     * Get real-time metrics.
     * @return Map with current real-time values
     */
    Map<String, Object> getRealTimeMetrics();

    /**
     * Get available metrics for analysis.
     * @return List of available metric names
     */
    List<String> getAvailableMetrics();
}
