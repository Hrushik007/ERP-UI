package com.erp.model;

import java.time.LocalDateTime;

/**
 * Report model representing a report definition.
 */
public class Report {
    private int reportId;
    private String reportCode;
    private String name;
    private String description;
    private String category; // FINANCIAL, SALES, INVENTORY, HR, PROJECT, CUSTOM
    private String reportType; // TABLE, CHART, SUMMARY, DETAILED
    private String query; // SQL or data source query
    private String parameters; // JSON string of report parameters
    private String outputFormat; // PDF, EXCEL, CSV, HTML
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime lastRunDate;
    private int runCount;

    public Report() {
        this.active = true;
        this.createdDate = LocalDateTime.now();
        this.runCount = 0;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(LocalDateTime lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public void incrementRunCount() {
        this.runCount++;
        this.lastRunDate = LocalDateTime.now();
    }
}
