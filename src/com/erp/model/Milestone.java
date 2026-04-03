package com.erp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Milestone entity - a key checkpoint within a project.
 */
public class Milestone {

    private int milestoneId;
    private int projectId;
    private String name;
    private String description;
    private LocalDate targetDate;
    private LocalDate completedDate;
    private String status;          // PENDING, COMPLETED, MISSED
    private boolean isCritical;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Milestone() {
        this.status = "PENDING";
        this.isCritical = false;
        this.createdAt = LocalDateTime.now();
    }

    public Milestone(int milestoneId, int projectId, String name) {
        this();
        this.milestoneId = milestoneId;
        this.projectId = projectId;
        this.name = name;
    }

    public boolean isOverdue() {
        if (targetDate == null || "COMPLETED".equals(status)) return false;
        return LocalDate.now().isAfter(targetDate);
    }

    public boolean isDue() {
        if (targetDate == null) return false;
        return LocalDate.now().equals(targetDate) && !"COMPLETED".equals(status);
    }

    public long getDaysUntilDue() {
        if (targetDate == null) return -1;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }

    public void complete() {
        this.status = "COMPLETED";
        this.completedDate = LocalDate.now();
    }

    // Getters and Setters
    public int getMilestoneId() { return milestoneId; }
    public void setMilestoneId(int milestoneId) { this.milestoneId = milestoneId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCritical() { return isCritical; }
    public void setCritical(boolean critical) { isCritical = critical; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name + " (" + status + ")";
    }
}
