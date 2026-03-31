package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Budget entity representing a financial budget.
 *
 * Used by:
 * - Finance Module (budget planning)
 * - Accounting Module (budget vs actual)
 */
public class Budget {

    private int budgetId;
    private String budgetName;
    private String category;           // SALES, MARKETING, OPERATIONS, HR, IT, etc.
    private String period;             // MONTHLY, QUARTERLY, YEARLY
    private int fiscalYear;
    private int fiscalMonth;           // 1-12 for monthly, 1-4 for quarterly
    private BigDecimal budgetedAmount;
    private BigDecimal actualAmount;
    private BigDecimal variance;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;             // DRAFT, APPROVED, ACTIVE, CLOSED
    private String notes;
    private int createdBy;
    private int approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Budget() {
        this.budgetedAmount = BigDecimal.ZERO;
        this.actualAmount = BigDecimal.ZERO;
        this.variance = BigDecimal.ZERO;
        this.status = "DRAFT";
        this.fiscalYear = LocalDate.now().getYear();
        this.createdAt = LocalDateTime.now();
    }

    public Budget(String budgetName, String category, BigDecimal budgetedAmount) {
        this();
        this.budgetName = budgetName;
        this.category = category;
        this.budgetedAmount = budgetedAmount;
    }

    // Getters and Setters
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public String getBudgetName() { return budgetName; }
    public void setBudgetName(String budgetName) { this.budgetName = budgetName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int fiscalYear) { this.fiscalYear = fiscalYear; }

    public int getFiscalMonth() { return fiscalMonth; }
    public void setFiscalMonth(int fiscalMonth) { this.fiscalMonth = fiscalMonth; }

    public BigDecimal getBudgetedAmount() { return budgetedAmount; }
    public void setBudgetedAmount(BigDecimal budgetedAmount) { this.budgetedAmount = budgetedAmount; }

    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
        calculateVariance();
    }

    public BigDecimal getVariance() { return variance; }
    public void setVariance(BigDecimal variance) { this.variance = variance; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public int getApprovedBy() { return approvedBy; }
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public void calculateVariance() {
        if (budgetedAmount != null && actualAmount != null) {
            this.variance = budgetedAmount.subtract(actualAmount);
        }
    }

    public double getUtilizationPercentage() {
        if (budgetedAmount == null || budgetedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return actualAmount.divide(budgetedAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    public boolean isOverBudget() {
        return variance != null && variance.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isUnderBudget() {
        return variance != null && variance.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return budgetName + " - " + category + " (" + fiscalYear + ")";
    }
}
