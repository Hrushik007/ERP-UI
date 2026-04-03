package com.erp.model;

import java.time.LocalDateTime;

/**
 * Dashboard model representing an analytics dashboard.
 */
public class Dashboard {
    private int dashboardId;
    private String dashboardCode;
    private String name;
    private String description;
    private String category; // EXECUTIVE, SALES, OPERATIONS, FINANCIAL, HR, CUSTOM
    private String layout; // GRID, FREEFORM
    private int columns; // Number of columns in grid layout
    private String widgets; // JSON string of widget configurations
    private boolean isDefault;
    private boolean isPublic;
    private int createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private int refreshInterval; // Seconds, 0 = manual refresh

    public Dashboard() {
        this.layout = "GRID";
        this.columns = 3;
        this.isDefault = false;
        this.isPublic = false;
        this.refreshInterval = 300; // 5 minutes default
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(int dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getDashboardCode() {
        return dashboardCode;
    }

    public void setDashboardCode(String dashboardCode) {
        this.dashboardCode = dashboardCode;
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

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getWidgets() {
        return widgets;
    }

    public void setWidgets(String widgets) {
        this.widgets = widgets;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}
