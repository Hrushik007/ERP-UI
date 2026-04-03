package com.erp.service.interfaces;

import com.erp.model.Report;
import com.erp.model.ReportTemplate;
import com.erp.model.ScheduledReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ReportingService Interface - CONTRACT for Reporting Module Backend Team
 *
 * Covers: Standard Reports, Custom Reports, Dashboards, Compliance Reporting,
 *         Report Management, Templates, and Scheduled Reports
 *
 * Reports return data as Maps and Lists for flexible UI rendering.
 * Each report method returns data that can be displayed in tables, charts, etc.
 */
public interface ReportingService {

    // ==================== REPORT MANAGEMENT ====================

    List<Report> getAllReports();
    List<Report> getReportsByCategory(String category);
    List<Report> getActiveReports();
    Report getReportById(int reportId);
    Report getReportByCode(String reportCode);
    Report createReport(Report report);
    boolean updateReport(Report report);
    boolean deleteReport(int reportId);
    boolean activateReport(int reportId);
    boolean deactivateReport(int reportId);
    void recordReportRun(int reportId);

    // ==================== REPORT TEMPLATES ====================

    List<ReportTemplate> getAllTemplates();
    List<ReportTemplate> getTemplatesByCategory(String category);
    List<ReportTemplate> getActiveTemplates();
    ReportTemplate getTemplateById(int templateId);
    ReportTemplate getTemplateByCode(String templateCode);
    ReportTemplate createTemplate(ReportTemplate template);
    boolean updateTemplate(ReportTemplate template);
    boolean deleteTemplate(int templateId);

    // ==================== SCHEDULED REPORTS ====================

    List<ScheduledReport> getAllScheduledReports();
    List<ScheduledReport> getScheduledReportsByReportId(int reportId);
    List<ScheduledReport> getActiveScheduledReports();
    List<ScheduledReport> getPendingScheduledReports();
    ScheduledReport getScheduledReportById(int scheduleId);
    ScheduledReport createScheduledReport(ScheduledReport scheduledReport);
    boolean updateScheduledReport(ScheduledReport scheduledReport);
    boolean deleteScheduledReport(int scheduleId);
    boolean activateScheduledReport(int scheduleId);
    boolean deactivateScheduledReport(int scheduleId);
    boolean runScheduledReportNow(int scheduleId);

    // ==================== REPORT EXECUTION ====================

    String executeReport(int reportId, String parameters);
    String exportReport(int reportId, String outputFormat);

    // ==================== STATISTICS ====================

    int getTotalReportsCount();
    int getTotalTemplatesCount();
    int getTotalScheduledCount();
    int getActiveScheduledCount();

    // ==================== SALES REPORTS ====================

    /**
     * Generate sales summary report.
     * @param startDate Start date
     * @param endDate End date
     * @return Report data with keys: totalSales, orderCount, avgOrderValue, etc.
     */
    Map<String, Object> getSalesSummaryReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generate sales by product report.
     * @param startDate Start date
     * @param endDate End date
     * @return List of maps with product sales data
     */
    List<Map<String, Object>> getSalesByProductReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generate sales by customer report.
     * @param startDate Start date
     * @param endDate End date
     * @return List of maps with customer sales data
     */
    List<Map<String, Object>> getSalesByCustomerReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generate sales by sales rep report.
     * @param startDate Start date
     * @param endDate End date
     * @return List of maps with sales rep performance data
     */
    List<Map<String, Object>> getSalesBySalesRepReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generate sales trend report (by month/week/day).
     * @param startDate Start date
     * @param endDate End date
     * @param groupBy "DAY", "WEEK", "MONTH"
     * @return List of maps with sales trend data
     */
    List<Map<String, Object>> getSalesTrendReport(LocalDate startDate, LocalDate endDate, String groupBy);


    // ==================== INVENTORY REPORTS ====================

    /**
     * Generate inventory valuation report.
     * @return List of maps with product inventory data
     */
    List<Map<String, Object>> getInventoryValuationReport();

    /**
     * Generate low stock report.
     * @return List of maps with products below reorder level
     */
    List<Map<String, Object>> getLowStockReport();

    /**
     * Generate inventory movement report (ins and outs).
     * @param startDate Start date
     * @param endDate End date
     * @return List of maps with inventory movement data
     */
    List<Map<String, Object>> getInventoryMovementReport(LocalDate startDate, LocalDate endDate);


    // ==================== FINANCIAL REPORTS ====================

    /**
     * Generate accounts receivable aging report.
     * @return List of maps with AR aging data by customer
     */
    List<Map<String, Object>> getARAgingReport();

    /**
     * Generate revenue report.
     * @param startDate Start date
     * @param endDate End date
     * @return Report data with revenue metrics
     */
    Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generate profit and loss summary.
     * @param startDate Start date
     * @param endDate End date
     * @return Report data with P&L figures
     */
    Map<String, Object> getProfitLossReport(LocalDate startDate, LocalDate endDate);

    /**
     * Generate cash flow report.
     * @param startDate Start date
     * @param endDate End date
     * @return Report data with cash flow metrics
     */
    Map<String, Object> getCashFlowReport(LocalDate startDate, LocalDate endDate);


    // ==================== HR REPORTS ====================

    /**
     * Generate employee headcount report.
     * @return List of maps with headcount by department
     */
    List<Map<String, Object>> getHeadcountReport();

    /**
     * Generate attendance report.
     * @param startDate Start date
     * @param endDate End date
     * @param departmentId Department ID (0 for all)
     * @return List of maps with attendance data
     */
    List<Map<String, Object>> getAttendanceReport(LocalDate startDate, LocalDate endDate, int departmentId);

    /**
     * Generate payroll summary report.
     * @param payPeriod The pay period
     * @return Report data with payroll summary
     */
    Map<String, Object> getPayrollSummaryReport(String payPeriod);


    // ==================== PROJECT REPORTS ====================

    /**
     * Generate project status report.
     * @return List of maps with project status data
     */
    List<Map<String, Object>> getProjectStatusReport();

    /**
     * Generate resource utilization report.
     * @param startDate Start date
     * @param endDate End date
     * @return List of maps with employee utilization data
     */
    List<Map<String, Object>> getResourceUtilizationReport(LocalDate startDate, LocalDate endDate);


    // ==================== CUSTOM REPORTS ====================

    /**
     * Execute a custom report query.
     * This allows for flexible reporting beyond standard reports.
     *
     * @param reportType The type of custom report
     * @param parameters Report parameters
     * @return Report data
     */
    List<Map<String, Object>> executeCustomReport(String reportType, Map<String, Object> parameters);

    /**
     * Get available report types.
     * @return List of available report type names
     */
    List<String> getAvailableReportTypes();


    // ==================== DASHBOARD DATA ====================

    /**
     * Get executive dashboard data.
     * @return Map with key metrics for executive dashboard
     */
    Map<String, Object> getExecutiveDashboard();

    /**
     * Get sales dashboard data.
     * @return Map with key sales metrics
     */
    Map<String, Object> getSalesDashboard();

    /**
     * Get operations dashboard data.
     * @return Map with operations metrics
     */
    Map<String, Object> getOperationsDashboard();
}
