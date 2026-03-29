package com.erp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ProjectTask entity - a task within a project.
 */
public class ProjectTask {

    private int taskId;
    private int projectId;
    private String name;
    private String description;
    private int assignedToId; // Employee
    private String status; // TODO, IN_PROGRESS, REVIEW, COMPLETED, BLOCKED
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private int estimatedHours;
    private int actualHours;
    private int percentComplete;
    private int parentTaskId; // For subtasks
    private int displayOrder;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectTask() {
        this.status = "TODO";
        this.priority = "MEDIUM";
        this.percentComplete = 0;
    }

    public ProjectTask(int taskId, int projectId, String name) {
        this();
        this.taskId = taskId;
        this.projectId = projectId;
        this.name = name;
    }

    public boolean isOverdue() {
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate) &&
               !"COMPLETED".equals(status);
    }

    // Getters and Setters
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAssignedToId() { return assignedToId; }
    public void setAssignedToId(int assignedToId) { this.assignedToId = assignedToId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public int getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(int estimatedHours) { this.estimatedHours = estimatedHours; }

    public int getActualHours() { return actualHours; }
    public void setActualHours(int actualHours) { this.actualHours = actualHours; }

    public int getPercentComplete() { return percentComplete; }
    public void setPercentComplete(int percentComplete) { this.percentComplete = percentComplete; }

    public int getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(int parentTaskId) { this.parentTaskId = parentTaskId; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ProjectTask{" + taskId + ": " + name + ", status=" + status + "}";
    }
}
