package com.erp.model;

import java.time.LocalDateTime;

/**
 * ChartConfig model representing a chart configuration for analytics.
 */
public class ChartConfig {
    private int chartId;
    private String chartCode;
    private String name;
    private String description;
    private String chartType; // BAR, LINE, PIE, DOUGHNUT, AREA, SCATTER, GAUGE, TABLE
    private String category; // SALES, FINANCIAL, OPERATIONS, HR, INVENTORY, PROJECT
    private String dataSource; // The data query or source
    private String xAxis; // X-axis field
    private String yAxis; // Y-axis field(s), comma-separated
    private String groupBy; // Grouping field
    private String filters; // JSON string of filter conditions
    private String colors; // JSON array of colors
    private boolean showLegend;
    private boolean showLabels;
    private boolean showGrid;
    private String timeRange; // LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, THIS_YEAR, CUSTOM
    private int refreshInterval; // Seconds, 0 = manual
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public ChartConfig() {
        this.chartType = "BAR";
        this.showLegend = true;
        this.showLabels = true;
        this.showGrid = true;
        this.timeRange = "LAST_30_DAYS";
        this.refreshInterval = 300;
        this.active = true;
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getChartId() {
        return chartId;
    }

    public void setChartId(int chartId) {
        this.chartId = chartId;
    }

    public String getChartCode() {
        return chartCode;
    }

    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
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

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getXAxis() {
        return xAxis;
    }

    public void setXAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getYAxis() {
        return yAxis;
    }

    public void setYAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
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

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
