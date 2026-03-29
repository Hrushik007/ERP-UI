package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project entity for Project Management module.
 */
public class Project {

    private int projectId;
    private String projectCode;
    private String name;
    private String description;
    private int customerId;
    private Customer customer;
    private int projectManagerId; // Employee
    private String status; // PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private BigDecimal budgetAmount;
    private BigDecimal actualCost;
    private int percentComplete;
    private String category;
    private List<ProjectTask> tasks;
    private List<Integer> teamMemberIds;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Project() {
        this.status = "PLANNED";
        this.priority = "MEDIUM";
        this.percentComplete = 0;
        this.tasks = new ArrayList<>();
        this.teamMemberIds = new ArrayList<>();
    }

    public Project(int projectId, String projectCode, String name) {
        this();
        this.projectId = projectId;
        this.projectCode = projectCode;
        this.name = name;
    }

    public boolean isOverBudget() {
        if (budgetAmount == null || actualCost == null) return false;
        return actualCost.compareTo(budgetAmount) > 0;
    }

    public boolean isOverdue() {
        if (endDate == null) return false;
        return LocalDate.now().isAfter(endDate) && !"COMPLETED".equals(status);
    }

    public BigDecimal getBudgetVariance() {
        if (budgetAmount == null || actualCost == null) return BigDecimal.ZERO;
        return budgetAmount.subtract(actualCost);
    }

    // Getters and Setters
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public int getProjectManagerId() { return projectManagerId; }
    public void setProjectManagerId(int projectManagerId) { this.projectManagerId = projectManagerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(LocalDate actualStartDate) { this.actualStartDate = actualStartDate; }

    public LocalDate getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }

    public BigDecimal getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }

    public BigDecimal getActualCost() { return actualCost; }
    public void setActualCost(BigDecimal actualCost) { this.actualCost = actualCost; }

    public int getPercentComplete() { return percentComplete; }
    public void setPercentComplete(int percentComplete) { this.percentComplete = percentComplete; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<ProjectTask> getTasks() { return tasks; }
    public void setTasks(List<ProjectTask> tasks) { this.tasks = tasks; }

    public List<Integer> getTeamMemberIds() { return teamMemberIds; }
    public void setTeamMemberIds(List<Integer> teamMemberIds) { this.teamMemberIds = teamMemberIds; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Project{" + projectCode + ": " + name + ", status=" + status + "}";
    }
}
