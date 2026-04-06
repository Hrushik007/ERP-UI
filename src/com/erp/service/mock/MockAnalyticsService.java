package com.erp.service.mock;

import com.erp.model.ChartConfig;
import com.erp.model.Dashboard;
import com.erp.model.KPI;
import com.erp.service.interfaces.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Mock implementation of AnalyticsService with sample data.
 */
public class MockAnalyticsService implements AnalyticsService {

    private static MockAnalyticsService instance;
    private Map<Integer, Dashboard> dashboards;
    private Map<Integer, KPI> kpis;
    private Map<Integer, ChartConfig> charts;
    private int nextDashboardId = 1;
    private int nextKpiId = 1;
    private int nextChartId = 1;

    private MockAnalyticsService() {
        dashboards = new HashMap<>();
        kpis = new HashMap<>();
        charts = new HashMap<>();
        initializeSampleData();
    }

    public static synchronized MockAnalyticsService getInstance() {
        if (instance == null) {
            instance = new MockAnalyticsService();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Sample Dashboards
        createDashboard(buildDashboard("DASH-001", "Executive Overview", "High-level business metrics", "EXECUTIVE", true, true));
        createDashboard(buildDashboard("DASH-002", "Sales Performance", "Sales team metrics and trends", "SALES", false, true));
        createDashboard(buildDashboard("DASH-003", "Operations Dashboard", "Operational efficiency metrics", "OPERATIONS", false, true));
        createDashboard(buildDashboard("DASH-004", "Financial Overview", "Financial health indicators", "FINANCIAL", false, true));
        createDashboard(buildDashboard("DASH-005", "HR Analytics", "Workforce metrics and analytics", "HR", false, false));

        // Sample KPIs
        createKPI(buildKPI("KPI-001", "Monthly Revenue", "FINANCIAL", "CURRENCY", 500000, 485000, 450000, "UP"));
        createKPI(buildKPI("KPI-002", "Customer Satisfaction", "CUSTOMER", "PERCENTAGE", 90, 87, 85, "UP"));
        createKPI(buildKPI("KPI-003", "Order Fulfillment Rate", "OPERATIONS", "PERCENTAGE", 95, 92, 94, "DOWN"));
        createKPI(buildKPI("KPI-004", "Employee Turnover", "HR", "PERCENTAGE", 10, 12, 11, "DOWN"));
        createKPI(buildKPI("KPI-005", "Lead Conversion Rate", "SALES", "PERCENTAGE", 25, 28, 22, "UP"));
        createKPI(buildKPI("KPI-006", "Inventory Turnover", "OPERATIONS", "RATIO", 8, 7.5, 7.2, "UP"));
        createKPI(buildKPI("KPI-007", "Gross Profit Margin", "FINANCIAL", "PERCENTAGE", 35, 33, 32, "UP"));
        createKPI(buildKPI("KPI-008", "Average Response Time", "CUSTOMER", "TIME", 2, 2.5, 3, "UP"));
        createKPI(buildKPI("KPI-009", "Production Efficiency", "OPERATIONS", "PERCENTAGE", 90, 88, 85, "UP"));
        createKPI(buildKPI("KPI-010", "Sales Growth", "SALES", "PERCENTAGE", 15, 12, 10, "DOWN"));

        // Sample Charts
        createChart(buildChart("CHT-001", "Monthly Sales Trend", "LINE", "SALES", "date", "revenue"));
        createChart(buildChart("CHT-002", "Revenue by Product", "BAR", "SALES", "product", "revenue"));
        createChart(buildChart("CHT-003", "Customer Distribution", "PIE", "CUSTOMER", "segment", "count"));
        createChart(buildChart("CHT-004", "Inventory Levels", "BAR", "INVENTORY", "category", "quantity"));
        createChart(buildChart("CHT-005", "Expense Breakdown", "DOUGHNUT", "FINANCIAL", "category", "amount"));
        createChart(buildChart("CHT-006", "Order Volume", "AREA", "OPERATIONS", "date", "orders"));
        createChart(buildChart("CHT-007", "Employee Distribution", "PIE", "HR", "department", "headcount"));
        createChart(buildChart("CHT-008", "Project Status", "BAR", "PROJECT", "status", "count"));
    }

    private Dashboard buildDashboard(String code, String name, String desc, String category, boolean isDefault, boolean isPublic) {
        Dashboard d = new Dashboard();
        d.setDashboardCode(code);
        d.setName(name);
        d.setDescription(desc);
        d.setCategory(category);
        d.setDefault(isDefault);
        d.setPublic(isPublic);
        d.setColumns(3);
        d.setRefreshInterval(300);
        return d;
    }

    private KPI buildKPI(String code, String name, String category, String unit, double target, double actual, double previous, String trend) {
        KPI k = new KPI();
        k.setKpiCode(code);
        k.setName(name);
        k.setCategory(category);
        k.setUnit(unit);
        k.setTargetValue(new BigDecimal(target));
        k.setActualValue(new BigDecimal(actual));
        k.setPreviousValue(new BigDecimal(previous));
        k.setTrend(trend);
        if (previous > 0) {
            double change = ((actual - previous) / previous) * 100;
            k.setTrendPercentage(new BigDecimal(change).setScale(1, BigDecimal.ROUND_HALF_UP));
        }
        k.setMinThreshold(new BigDecimal(target * 0.7));
        k.setMaxThreshold(new BigDecimal(target * 0.9));
        k.setFrequency("MONTHLY");
        k.setLastUpdated(LocalDateTime.now().minusHours((int)(Math.random() * 48)));
        k.setActive(true);
        return k;
    }

    private ChartConfig buildChart(String code, String name, String type, String category, String xAxis, String yAxis) {
        ChartConfig c = new ChartConfig();
        c.setChartCode(code);
        c.setName(name);
        c.setChartType(type);
        c.setCategory(category);
        c.setXAxis(xAxis);
        c.setYAxis(yAxis);
        c.setShowLegend(true);
        c.setShowLabels(true);
        c.setShowGrid(true);
        c.setTimeRange("LAST_30_DAYS");
        c.setRefreshInterval(300);
        c.setActive(true);
        return c;
    }

    // ==================== DASHBOARD MANAGEMENT ====================

    @Override
    public List<Dashboard> getAllDashboards() {
        return new ArrayList<>(dashboards.values());
    }

    @Override
    public List<Dashboard> getDashboardsByCategory(String category) {
        List<Dashboard> result = new ArrayList<>();
        for (Dashboard d : dashboards.values()) {
            if (category.equals(d.getCategory())) {
                result.add(d);
            }
        }
        return result;
    }

    @Override
    public List<Dashboard> getPublicDashboards() {
        List<Dashboard> result = new ArrayList<>();
        for (Dashboard d : dashboards.values()) {
            if (d.isPublic()) {
                result.add(d);
            }
        }
        return result;
    }

    @Override
    public Dashboard getDashboardById(int dashboardId) {
        return dashboards.get(dashboardId);
    }

    @Override
    public Dashboard getDashboardByCode(String dashboardCode) {
        for (Dashboard d : dashboards.values()) {
            if (dashboardCode.equals(d.getDashboardCode())) {
                return d;
            }
        }
        return null;
    }

    @Override
    public Dashboard createDashboard(Dashboard dashboard) {
        dashboard.setDashboardId(nextDashboardId++);
        dashboard.setCreatedDate(LocalDateTime.now());
        dashboards.put(dashboard.getDashboardId(), dashboard);
        return dashboard;
    }

    @Override
    public boolean updateDashboard(Dashboard dashboard) {
        if (dashboards.containsKey(dashboard.getDashboardId())) {
            dashboard.setModifiedDate(LocalDateTime.now());
            dashboards.put(dashboard.getDashboardId(), dashboard);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDashboard(int dashboardId) {
        return dashboards.remove(dashboardId) != null;
    }

    @Override
    public boolean setDefaultDashboard(int dashboardId) {
        // Clear existing default
        for (Dashboard d : dashboards.values()) {
            d.setDefault(false);
        }
        // Set new default
        Dashboard d = dashboards.get(dashboardId);
        if (d != null) {
            d.setDefault(true);
            return true;
        }
        return false;
    }

    // ==================== KPI MANAGEMENT ====================

    @Override
    public List<KPI> getAllKPIs() {
        return new ArrayList<>(kpis.values());
    }

    @Override
    public List<KPI> getKPIsByCategory(String category) {
        List<KPI> result = new ArrayList<>();
        for (KPI k : kpis.values()) {
            if (category.equals(k.getCategory())) {
                result.add(k);
            }
        }
        return result;
    }

    @Override
    public List<KPI> getActiveKPIs() {
        List<KPI> result = new ArrayList<>();
        for (KPI k : kpis.values()) {
            if (k.isActive()) {
                result.add(k);
            }
        }
        return result;
    }

    @Override
    public KPI getKPIById(int kpiId) {
        return kpis.get(kpiId);
    }

    @Override
    public KPI getKPIByCode(String kpiCode) {
        for (KPI k : kpis.values()) {
            if (kpiCode.equals(k.getKpiCode())) {
                return k;
            }
        }
        return null;
    }

    @Override
    public KPI createKPI(KPI kpi) {
        kpi.setKpiId(nextKpiId++);
        kpi.setCreatedDate(LocalDateTime.now());
        kpis.put(kpi.getKpiId(), kpi);
        return kpi;
    }

    @Override
    public boolean updateKPI(KPI kpi) {
        if (kpis.containsKey(kpi.getKpiId())) {
            kpis.put(kpi.getKpiId(), kpi);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteKPI(int kpiId) {
        return kpis.remove(kpiId) != null;
    }

    @Override
    public boolean refreshKPI(int kpiId) {
        KPI k = kpis.get(kpiId);
        if (k != null) {
            k.setLastUpdated(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean refreshAllKPIs() {
        LocalDateTime now = LocalDateTime.now();
        for (KPI k : kpis.values()) {
            k.setLastUpdated(now);
        }
        return true;
    }

    // ==================== CHART CONFIGURATION ====================

    @Override
    public List<ChartConfig> getAllCharts() {
        return new ArrayList<>(charts.values());
    }

    @Override
    public List<ChartConfig> getChartsByCategory(String category) {
        List<ChartConfig> result = new ArrayList<>();
        for (ChartConfig c : charts.values()) {
            if (category.equals(c.getCategory())) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public List<ChartConfig> getChartsByType(String chartType) {
        List<ChartConfig> result = new ArrayList<>();
        for (ChartConfig c : charts.values()) {
            if (chartType.equals(c.getChartType())) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public ChartConfig getChartById(int chartId) {
        return charts.get(chartId);
    }

    @Override
    public ChartConfig getChartByCode(String chartCode) {
        for (ChartConfig c : charts.values()) {
            if (chartCode.equals(c.getChartCode())) {
                return c;
            }
        }
        return null;
    }

    @Override
    public ChartConfig createChart(ChartConfig chart) {
        chart.setChartId(nextChartId++);
        chart.setCreatedDate(LocalDateTime.now());
        charts.put(chart.getChartId(), chart);
        return chart;
    }

    @Override
    public boolean updateChart(ChartConfig chart) {
        if (charts.containsKey(chart.getChartId())) {
            chart.setModifiedDate(LocalDateTime.now());
            charts.put(chart.getChartId(), chart);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteChart(int chartId) {
        return charts.remove(chartId) != null;
    }

    // ==================== STATISTICS ====================

    @Override
    public int getTotalDashboards() {
        return dashboards.size();
    }

    @Override
    public int getTotalKPIs() {
        return kpis.size();
    }

    @Override
    public int getTotalCharts() {
        return charts.size();
    }

    @Override
    public int getKPIsOnTarget() {
        int count = 0;
        for (KPI k : kpis.values()) {
            if (k.getActualValue() != null && k.getTargetValue() != null) {
                if (k.getActualValue().compareTo(k.getTargetValue().multiply(new BigDecimal("0.9"))) >= 0) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int getKPIsBelowTarget() {
        int count = 0;
        for (KPI k : kpis.values()) {
            if (k.getActualValue() != null && k.getTargetValue() != null) {
                if (k.getActualValue().compareTo(k.getTargetValue().multiply(new BigDecimal("0.7"))) < 0) {
                    count++;
                }
            }
        }
        return count;
    }

    // ==================== EXISTING ANALYTICS METHODS ====================

    @Override
    public Map<String, Object> getKPIs() {
        Map<String, Object> result = new HashMap<>();
        for (KPI k : kpis.values()) {
            result.put(k.getName(), k.getActualValue());
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getTrend(String metricName, LocalDate startDate, LocalDate endDate, String granularity) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate current = startDate;
        double value = 1000 + Math.random() * 500;
        while (!current.isAfter(endDate)) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", current);
            point.put("value", value + (Math.random() - 0.5) * 100);
            result.add(point);
            current = "DAY".equals(granularity) ? current.plusDays(1) :
                     "WEEK".equals(granularity) ? current.plusWeeks(1) : current.plusMonths(1);
        }
        return result;
    }

    @Override
    public Map<String, Object> getPeriodComparison(String metricName, LocalDate currentStart, LocalDate currentEnd) {
        Map<String, Object> result = new HashMap<>();
        result.put("current", 125000.0);
        result.put("previous", 115000.0);
        result.put("change", 10000.0);
        result.put("percentChange", 8.7);
        return result;
    }

    @Override
    public List<Map<String, Object>> getSalesForecast(int daysAhead) {
        List<Map<String, Object>> result = new ArrayList<>();
        double baseValue = 12000 + Math.random() * 3000;
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= daysAhead; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", today.plusDays(i));
            // Simulate a slight upward trend with noise
            double value = baseValue + (i * 50) + (Math.random() - 0.4) * 800;
            point.put("value", Math.round(value * 100.0) / 100.0);
            point.put("confidence", i <= 7 ? "HIGH" : i <= 14 ? "MEDIUM" : "LOW");
            result.add(point);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDemandForecast(int productId, int daysAhead) {
        List<Map<String, Object>> result = new ArrayList<>();
        double baseUnits = 50 + Math.random() * 100;
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= daysAhead; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", today.plusDays(i));
            point.put("productId", productId);
            point.put("units", (int)(baseUnits + (Math.random() - 0.3) * 30));
            result.add(point);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getChurnRiskAnalysis() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[][] customers = {
            {"1", "Acme Corporation", "12", "2024-01-15", "LOW"},
            {"2", "TechStart Inc", "85", "2024-11-02", "HIGH"},
            {"3", "Global Industries", "45", "2024-06-20", "MEDIUM"},
            {"4", "Local Shop LLC", "72", "2024-09-10", "HIGH"},
            {"5", "Enterprise Solutions", "20", "2024-03-05", "LOW"},
            {"6", "Growth Corp", "55", "2024-08-18", "MEDIUM"},
            {"7", "Startup Labs", "90", "2024-12-01", "HIGH"},
            {"8", "Future Tech Co", "30", "2024-04-22", "LOW"}
        };
        for (String[] c : customers) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("customerId", Integer.parseInt(c[0]));
            entry.put("customerName", c[1]);
            entry.put("riskScore", Integer.parseInt(c[2]));
            entry.put("lastPurchase", c[3]);
            entry.put("riskLevel", c[4]);
            entry.put("daysSinceContact", (int)(Math.random() * 90) + 5);
            result.add(entry);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getReorderRecommendations() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[][] products = {
            {"1", "Widget A", "15", "100", "85"},
            {"2", "Gadget B", "8", "50", "42"},
            {"3", "Component C", "22", "200", "178"},
            {"4", "Part D", "3", "75", "72"},
            {"5", "Module E", "45", "150", "105"}
        };
        for (String[] p : products) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("productId", Integer.parseInt(p[0]));
            entry.put("productName", p[1]);
            entry.put("currentStock", Integer.parseInt(p[2]));
            entry.put("reorderQuantity", Integer.parseInt(p[3]));
            entry.put("forecastDemand", Integer.parseInt(p[4]));
            entry.put("urgency", Integer.parseInt(p[2]) < 10 ? "HIGH" : Integer.parseInt(p[2]) < 25 ? "MEDIUM" : "LOW");
            result.add(entry);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCustomerSegments() {
        List<Map<String, Object>> result = new ArrayList<>();
        Object[][] segments = {
            {"Premium", 45, 68.5, 4500.0, "High value, frequent buyers"},
            {"Regular", 120, 42.0, 1200.0, "Consistent mid-range purchases"},
            {"Occasional", 85, 15.0, 350.0, "Infrequent, low-value orders"},
            {"New", 60, 8.5, 800.0, "Recently acquired, potential growth"},
            {"At Risk", 30, 5.0, 200.0, "Declining engagement, needs attention"}
        };
        for (Object[] s : segments) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("segment", s[0]);
            entry.put("customerCount", s[1]);
            entry.put("revenueShare", s[2]);
            entry.put("avgOrderValue", s[3]);
            entry.put("description", s[4]);
            result.add(entry);
        }
        return result;
    }

    @Override
    public Map<String, List<Integer>> getProductCategories() {
        Map<String, List<Integer>> result = new HashMap<>();
        result.put("Stars", Arrays.asList(1, 5, 8));
        result.put("Cash Cows", Arrays.asList(2, 3, 6));
        result.put("Question Marks", Arrays.asList(4, 9, 10));
        result.put("Dogs", Arrays.asList(7, 11));
        return result;
    }

    @Override
    public Map<String, Double> getStatisticalSummary(String metricName, LocalDate startDate, LocalDate endDate) {
        Map<String, Double> result = new HashMap<>();
        result.put("mean", 12500.0);
        result.put("median", 11800.0);
        result.put("stdDev", 2300.0);
        result.put("min", 8500.0);
        result.put("max", 18200.0);
        result.put("q1", 10200.0);
        result.put("q3", 14100.0);
        result.put("count", 30.0);
        return result;
    }

    @Override
    public Map<String, Object> getCorrelation(String metric1, String metric2, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("metric1", metric1);
        result.put("metric2", metric2);
        result.put("coefficient", 0.78);
        result.put("strength", "Strong Positive");
        result.put("significance", "p < 0.01");
        result.put("dataPoints", 30);
        return result;
    }

    @Override
    public List<Map<String, Object>> detectAnomalies(String metricName, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        Object[][] anomalies = {
            {LocalDate.now().minusDays(15), 28500.0, 12500.0, "SPIKE", "Unusual spike — 128% above average"},
            {LocalDate.now().minusDays(8), 3200.0, 12500.0, "DROP", "Significant drop — 74% below average"},
            {LocalDate.now().minusDays(3), 22000.0, 12500.0, "SPIKE", "Elevated value — 76% above average"}
        };
        for (Object[] a : anomalies) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", a[0]);
            entry.put("value", a[1]);
            entry.put("expected", a[2]);
            entry.put("type", a[3]);
            entry.put("description", a[4]);
            entry.put("metric", metricName);
            result.add(entry);
        }
        return result;
    }

    @Override
    public Map<String, Object> getExecutiveScorecard() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", 1250000.0);
        result.put("revenueGrowth", 8.5);
        result.put("totalOrders", 3420);
        result.put("orderGrowth", 12.3);
        result.put("newCustomers", 145);
        result.put("customerGrowth", 5.2);
        result.put("avgOrderValue", 365.50);
        result.put("avgOrderGrowth", -2.1);
        result.put("grossMargin", 34.5);
        result.put("marginGrowth", 1.8);
        result.put("employeeCount", 87);
        result.put("employeeGrowth", 3.5);
        result.put("customerSatisfaction", 87.0);
        result.put("satisfactionGrowth", 2.0);
        result.put("operationalEfficiency", 91.5);
        result.put("efficiencyGrowth", 4.2);
        return result;
    }

    @Override
    public Map<String, Object> getRealTimeMetrics() {
        Map<String, Object> result = new HashMap<>();
        result.put("ordersToday", (int)(Math.random() * 30) + 10);
        result.put("revenueToday", Math.round((Math.random() * 15000 + 5000) * 100.0) / 100.0);
        result.put("activeUsers", (int)(Math.random() * 20) + 5);
        result.put("pendingOrders", (int)(Math.random() * 15) + 3);
        result.put("openTickets", (int)(Math.random() * 10) + 2);
        result.put("serverUptime", 99.97);
        return result;
    }

    @Override
    public List<String> getAvailableMetrics() {
        return Arrays.asList("sales", "revenue", "orders", "customers", "inventory", "production");
    }
}
