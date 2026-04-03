package com.erp.service.mock;

import com.erp.model.Report;
import com.erp.model.ReportTemplate;
import com.erp.model.ScheduledReport;
import com.erp.service.interfaces.ReportingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Mock implementation of ReportingService with sample data.
 */
public class MockReportingService implements ReportingService {

    private static MockReportingService instance;
    private Map<Integer, Report> reports;
    private Map<Integer, ReportTemplate> templates;
    private Map<Integer, ScheduledReport> scheduledReports;
    private int nextReportId = 1;
    private int nextTemplateId = 1;
    private int nextScheduleId = 1;

    private MockReportingService() {
        reports = new HashMap<>();
        templates = new HashMap<>();
        scheduledReports = new HashMap<>();
        initializeSampleData();
    }

    public static synchronized MockReportingService getInstance() {
        if (instance == null) {
            instance = new MockReportingService();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Sample Reports
        createReport(buildReport("RPT-001", "Sales Summary Report", "Summary of sales by period", "SALES", "SUMMARY", "PDF"));
        createReport(buildReport("RPT-002", "Sales by Product", "Detailed sales breakdown by product", "SALES", "DETAILED", "EXCEL"));
        createReport(buildReport("RPT-003", "Sales by Customer", "Sales analysis by customer", "SALES", "TABLE", "PDF"));
        createReport(buildReport("RPT-004", "Inventory Valuation", "Current inventory value report", "INVENTORY", "TABLE", "EXCEL"));
        createReport(buildReport("RPT-005", "Low Stock Alert", "Products below reorder level", "INVENTORY", "TABLE", "PDF"));
        createReport(buildReport("RPT-006", "Stock Movement", "Inventory ins and outs", "INVENTORY", "DETAILED", "EXCEL"));
        createReport(buildReport("RPT-007", "Profit & Loss Statement", "P&L for specified period", "FINANCIAL", "SUMMARY", "PDF"));
        createReport(buildReport("RPT-008", "Balance Sheet", "Financial position statement", "FINANCIAL", "SUMMARY", "PDF"));
        createReport(buildReport("RPT-009", "Cash Flow Report", "Cash flow analysis", "FINANCIAL", "DETAILED", "EXCEL"));
        createReport(buildReport("RPT-010", "AR Aging Report", "Accounts receivable aging", "FINANCIAL", "TABLE", "PDF"));
        createReport(buildReport("RPT-011", "Employee Directory", "Complete employee listing", "HR", "TABLE", "PDF"));
        createReport(buildReport("RPT-012", "Attendance Summary", "Employee attendance report", "HR", "SUMMARY", "EXCEL"));
        createReport(buildReport("RPT-013", "Payroll Report", "Payroll summary by period", "HR", "DETAILED", "PDF"));
        createReport(buildReport("RPT-014", "Project Status Report", "All projects status overview", "PROJECT", "SUMMARY", "PDF"));
        createReport(buildReport("RPT-015", "Task Progress Report", "Tasks completion analysis", "PROJECT", "DETAILED", "EXCEL"));

        // Sample Templates
        createTemplate(buildTemplate("TPL-001", "Standard Financial Report", "Standard template for financial reports", "FINANCIAL", "PORTRAIT", "A4"));
        createTemplate(buildTemplate("TPL-002", "Sales Dashboard Template", "Template for sales dashboards", "SALES", "LANDSCAPE", "A4"));
        createTemplate(buildTemplate("TPL-003", "Inventory List Template", "Template for inventory listings", "INVENTORY", "PORTRAIT", "LETTER"));
        createTemplate(buildTemplate("TPL-004", "HR Report Template", "Standard HR report template", "HR", "PORTRAIT", "A4"));
        createTemplate(buildTemplate("TPL-005", "Project Summary Template", "Template for project summaries", "PROJECT", "LANDSCAPE", "A4"));

        // Sample Scheduled Reports
        createScheduledReport(buildScheduledReport(1, "Daily Sales Summary", "DAILY", "08:00", "sales@company.com"));
        createScheduledReport(buildScheduledReport(4, "Weekly Inventory Check", "WEEKLY", "09:00", "inventory@company.com"));
        createScheduledReport(buildScheduledReport(7, "Monthly P&L Report", "MONTHLY", "06:00", "finance@company.com"));
        createScheduledReport(buildScheduledReport(10, "Weekly AR Aging", "WEEKLY", "10:00", "accounting@company.com"));
    }

    private Report buildReport(String code, String name, String desc, String category, String type, String format) {
        Report r = new Report();
        r.setReportCode(code);
        r.setName(name);
        r.setDescription(desc);
        r.setCategory(category);
        r.setReportType(type);
        r.setOutputFormat(format);
        r.setActive(true);
        r.setRunCount((int) (Math.random() * 50));
        if (r.getRunCount() > 0) {
            r.setLastRunDate(LocalDateTime.now().minusDays((int) (Math.random() * 30)));
        }
        return r;
    }

    private ReportTemplate buildTemplate(String code, String name, String desc, String category, String layout, String paperSize) {
        ReportTemplate t = new ReportTemplate();
        t.setTemplateCode(code);
        t.setName(name);
        t.setDescription(desc);
        t.setCategory(category);
        t.setLayout(layout);
        t.setPaperSize(paperSize);
        t.setActive(true);
        return t;
    }

    private ScheduledReport buildScheduledReport(int reportId, String name, String frequency, String time, String recipients) {
        ScheduledReport s = new ScheduledReport();
        s.setReportId(reportId);
        s.setScheduleName(name);
        s.setFrequency(frequency);
        s.setTimeOfDay(time);
        s.setRecipients(recipients);
        s.setOutputFormat("PDF");
        s.setDeliveryMethod("EMAIL");
        s.setActive(true);
        s.setLastRunStatus("SUCCESS");
        s.setLastRunDate(LocalDateTime.now().minusDays((int) (Math.random() * 7)));
        s.setNextRunDate(LocalDateTime.now().plusDays((int) (Math.random() * 7) + 1));
        if (frequency.equals("WEEKLY")) {
            s.setDayOfWeek("MONDAY");
        } else if (frequency.equals("MONTHLY")) {
            s.setDayOfMonth(1);
        }
        return s;
    }

    // ==================== REPORT MANAGEMENT ====================

    @Override
    public List<Report> getAllReports() {
        return new ArrayList<>(reports.values());
    }

    @Override
    public List<Report> getReportsByCategory(String category) {
        List<Report> result = new ArrayList<>();
        for (Report r : reports.values()) {
            if (category.equals(r.getCategory())) {
                result.add(r);
            }
        }
        return result;
    }

    @Override
    public List<Report> getActiveReports() {
        List<Report> result = new ArrayList<>();
        for (Report r : reports.values()) {
            if (r.isActive()) {
                result.add(r);
            }
        }
        return result;
    }

    @Override
    public Report getReportById(int reportId) {
        return reports.get(reportId);
    }

    @Override
    public Report getReportByCode(String reportCode) {
        for (Report r : reports.values()) {
            if (reportCode.equals(r.getReportCode())) {
                return r;
            }
        }
        return null;
    }

    @Override
    public Report createReport(Report report) {
        report.setReportId(nextReportId++);
        report.setCreatedDate(LocalDateTime.now());
        reports.put(report.getReportId(), report);
        return report;
    }

    @Override
    public boolean updateReport(Report report) {
        if (reports.containsKey(report.getReportId())) {
            reports.put(report.getReportId(), report);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReport(int reportId) {
        return reports.remove(reportId) != null;
    }

    @Override
    public boolean activateReport(int reportId) {
        Report r = reports.get(reportId);
        if (r != null) {
            r.setActive(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivateReport(int reportId) {
        Report r = reports.get(reportId);
        if (r != null) {
            r.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public void recordReportRun(int reportId) {
        Report r = reports.get(reportId);
        if (r != null) {
            r.incrementRunCount();
        }
    }

    // ==================== REPORT TEMPLATES ====================

    @Override
    public List<ReportTemplate> getAllTemplates() {
        return new ArrayList<>(templates.values());
    }

    @Override
    public List<ReportTemplate> getTemplatesByCategory(String category) {
        List<ReportTemplate> result = new ArrayList<>();
        for (ReportTemplate t : templates.values()) {
            if (category.equals(t.getCategory())) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public List<ReportTemplate> getActiveTemplates() {
        List<ReportTemplate> result = new ArrayList<>();
        for (ReportTemplate t : templates.values()) {
            if (t.isActive()) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public ReportTemplate getTemplateById(int templateId) {
        return templates.get(templateId);
    }

    @Override
    public ReportTemplate getTemplateByCode(String templateCode) {
        for (ReportTemplate t : templates.values()) {
            if (templateCode.equals(t.getTemplateCode())) {
                return t;
            }
        }
        return null;
    }

    @Override
    public ReportTemplate createTemplate(ReportTemplate template) {
        template.setTemplateId(nextTemplateId++);
        template.setCreatedDate(LocalDateTime.now());
        templates.put(template.getTemplateId(), template);
        return template;
    }

    @Override
    public boolean updateTemplate(ReportTemplate template) {
        if (templates.containsKey(template.getTemplateId())) {
            template.setModifiedDate(LocalDateTime.now());
            templates.put(template.getTemplateId(), template);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTemplate(int templateId) {
        return templates.remove(templateId) != null;
    }

    // ==================== SCHEDULED REPORTS ====================

    @Override
    public List<ScheduledReport> getAllScheduledReports() {
        return new ArrayList<>(scheduledReports.values());
    }

    @Override
    public List<ScheduledReport> getScheduledReportsByReportId(int reportId) {
        List<ScheduledReport> result = new ArrayList<>();
        for (ScheduledReport s : scheduledReports.values()) {
            if (s.getReportId() == reportId) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public List<ScheduledReport> getActiveScheduledReports() {
        List<ScheduledReport> result = new ArrayList<>();
        for (ScheduledReport s : scheduledReports.values()) {
            if (s.isActive()) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public List<ScheduledReport> getPendingScheduledReports() {
        List<ScheduledReport> result = new ArrayList<>();
        for (ScheduledReport s : scheduledReports.values()) {
            if (s.isActive() && "PENDING".equals(s.getLastRunStatus())) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public ScheduledReport getScheduledReportById(int scheduleId) {
        return scheduledReports.get(scheduleId);
    }

    @Override
    public ScheduledReport createScheduledReport(ScheduledReport scheduledReport) {
        scheduledReport.setScheduleId(nextScheduleId++);
        scheduledReport.setCreatedDate(LocalDateTime.now());
        scheduledReports.put(scheduledReport.getScheduleId(), scheduledReport);
        return scheduledReport;
    }

    @Override
    public boolean updateScheduledReport(ScheduledReport scheduledReport) {
        if (scheduledReports.containsKey(scheduledReport.getScheduleId())) {
            scheduledReports.put(scheduledReport.getScheduleId(), scheduledReport);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteScheduledReport(int scheduleId) {
        return scheduledReports.remove(scheduleId) != null;
    }

    @Override
    public boolean activateScheduledReport(int scheduleId) {
        ScheduledReport s = scheduledReports.get(scheduleId);
        if (s != null) {
            s.setActive(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivateScheduledReport(int scheduleId) {
        ScheduledReport s = scheduledReports.get(scheduleId);
        if (s != null) {
            s.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean runScheduledReportNow(int scheduleId) {
        ScheduledReport s = scheduledReports.get(scheduleId);
        if (s != null) {
            s.setLastRunDate(LocalDateTime.now());
            s.setLastRunStatus("SUCCESS");
            s.setLastRunMessage("Report executed successfully");
            recordReportRun(s.getReportId());
            return true;
        }
        return false;
    }

    // ==================== REPORT EXECUTION ====================

    @Override
    public String executeReport(int reportId, String parameters) {
        Report r = reports.get(reportId);
        if (r != null) {
            r.incrementRunCount();
            return "Report '" + r.getName() + "' executed successfully with parameters: " + parameters;
        }
        return "Report not found";
    }

    @Override
    public String exportReport(int reportId, String outputFormat) {
        Report r = reports.get(reportId);
        if (r != null) {
            r.incrementRunCount();
            return "Report exported as " + outputFormat + ": " + r.getName() + "." + outputFormat.toLowerCase();
        }
        return "Report not found";
    }

    // ==================== STATISTICS ====================

    @Override
    public int getTotalReportsCount() {
        return reports.size();
    }

    @Override
    public int getTotalTemplatesCount() {
        return templates.size();
    }

    @Override
    public int getTotalScheduledCount() {
        return scheduledReports.size();
    }

    @Override
    public int getActiveScheduledCount() {
        int count = 0;
        for (ScheduledReport s : scheduledReports.values()) {
            if (s.isActive()) count++;
        }
        return count;
    }

    // ==================== EXISTING REPORT DATA METHODS ====================

    @Override
    public Map<String, Object> getSalesSummaryReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", 125000.00);
        result.put("orderCount", 156);
        result.put("avgOrderValue", 801.28);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        return result;
    }

    @Override
    public List<Map<String, Object>> getSalesByProductReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(createProductSalesRow("Product A", 45000.00, 120));
        result.add(createProductSalesRow("Product B", 32000.00, 85));
        result.add(createProductSalesRow("Product C", 28000.00, 95));
        return result;
    }

    private Map<String, Object> createProductSalesRow(String product, double sales, int qty) {
        Map<String, Object> row = new HashMap<>();
        row.put("product", product);
        row.put("totalSales", sales);
        row.put("quantity", qty);
        return row;
    }

    @Override
    public List<Map<String, Object>> getSalesByCustomerReport(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(createCustomerSalesRow("Customer A", 25000.00, 15));
        result.add(createCustomerSalesRow("Customer B", 18000.00, 12));
        return result;
    }

    private Map<String, Object> createCustomerSalesRow(String customer, double sales, int orders) {
        Map<String, Object> row = new HashMap<>();
        row.put("customer", customer);
        row.put("totalSales", sales);
        row.put("orderCount", orders);
        return row;
    }

    @Override
    public List<Map<String, Object>> getSalesBySalesRepReport(LocalDate startDate, LocalDate endDate) {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getSalesTrendReport(LocalDate startDate, LocalDate endDate, String groupBy) {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getInventoryValuationReport() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getLowStockReport() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getInventoryMovementReport(LocalDate startDate, LocalDate endDate) {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getARAgingReport() {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getRevenueReport(LocalDate startDate, LocalDate endDate) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getProfitLossReport(LocalDate startDate, LocalDate endDate) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getCashFlowReport(LocalDate startDate, LocalDate endDate) {
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getHeadcountReport() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getAttendanceReport(LocalDate startDate, LocalDate endDate, int departmentId) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getPayrollSummaryReport(String payPeriod) {
        return new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> getProjectStatusReport() {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getResourceUtilizationReport(LocalDate startDate, LocalDate endDate) {
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> executeCustomReport(String reportType, Map<String, Object> parameters) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getAvailableReportTypes() {
        return Arrays.asList("SALES", "INVENTORY", "FINANCIAL", "HR", "PROJECT", "CUSTOM");
    }

    @Override
    public Map<String, Object> getExecutiveDashboard() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getSalesDashboard() {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> getOperationsDashboard() {
        return new HashMap<>();
    }
}
