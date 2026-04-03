package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * KPI model representing a Key Performance Indicator.
 */
public class KPI {
    private int kpiId;
    private String kpiCode;
    private String name;
    private String description;
    private String category; // SALES, FINANCIAL, OPERATIONS, HR, CUSTOMER, QUALITY
    private String unit; // CURRENCY, PERCENTAGE, NUMBER, TIME, RATIO
    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private BigDecimal previousValue;
    private String trend; // UP, DOWN, STABLE
    private BigDecimal trendPercentage;
    private String frequency; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    private String dataSource; // Where the data comes from
    private String calculation; // Formula or method
    private BigDecimal minThreshold; // Red zone below this
    private BigDecimal maxThreshold; // Green zone above this
    private boolean active;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdDate;

    public KPI() {
        this.active = true;
        this.trend = "STABLE";
        this.trendPercentage = BigDecimal.ZERO;
        this.frequency = "MONTHLY";
        this.createdDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getKpiId() {
        return kpiId;
    }

    public void setKpiId(int kpiId) {
        this.kpiId = kpiId;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    public BigDecimal getActualValue() {
        return actualValue;
    }

    public void setActualValue(BigDecimal actualValue) {
        this.actualValue = actualValue;
    }

    public BigDecimal getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(BigDecimal previousValue) {
        this.previousValue = previousValue;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public BigDecimal getTrendPercentage() {
        return trendPercentage;
    }

    public void setTrendPercentage(BigDecimal trendPercentage) {
        this.trendPercentage = trendPercentage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public BigDecimal getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(BigDecimal minThreshold) {
        this.minThreshold = minThreshold;
    }

    public BigDecimal getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(BigDecimal maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Calculate achievement percentage (actual vs target)
     */
    public BigDecimal getAchievement() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (actualValue == null) {
            return BigDecimal.ZERO;
        }
        return actualValue.multiply(new BigDecimal("100")).divide(targetValue, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Get status based on thresholds
     */
    public String getStatus() {
        if (actualValue == null) return "UNKNOWN";
        if (maxThreshold != null && actualValue.compareTo(maxThreshold) >= 0) return "GOOD";
        if (minThreshold != null && actualValue.compareTo(minThreshold) <= 0) return "CRITICAL";
        return "WARNING";
    }
}
