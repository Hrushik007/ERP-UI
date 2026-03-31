package com.erp.model;

import java.time.LocalDateTime;

/**
 * Activity entity representing CRM activities like calls, meetings, tasks.
 *
 * Used by:
 * - CRM Module
 * - Sales Module
 */
public class Activity {

    private int activityId;
    private String type;               // CALL, MEETING, EMAIL, TASK, NOTE
    private String subject;
    private String description;
    private String status;             // PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority;           // LOW, MEDIUM, HIGH
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private int assignedTo;            // Employee ID
    private String assignedToName;

    // Related entity (can be linked to lead, contact, opportunity, or customer)
    private String relatedType;        // LEAD, CONTACT, OPPORTUNITY, CUSTOMER
    private int relatedId;
    private String relatedName;        // Cached for display

    private int duration;              // Duration in minutes (for calls/meetings)
    private String outcome;            // Result of the activity
    private String location;           // For meetings
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int createdBy;
    private String createdByName;

    public Activity() {
        this.status = "PLANNED";
        this.priority = "MEDIUM";
        this.createdAt = LocalDateTime.now();
    }

    public Activity(String type, String subject, LocalDateTime dueDate) {
        this();
        this.type = type;
        this.subject = subject;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public String getRelatedType() { return relatedType; }
    public void setRelatedType(String relatedType) { this.relatedType = relatedType; }

    public int getRelatedId() { return relatedId; }
    public void setRelatedId(int relatedId) { this.relatedId = relatedId; }

    public String getRelatedName() { return relatedName; }
    public void setRelatedName(String relatedName) { this.relatedName = relatedName; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    // Helper methods
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && !isCompleted();
    }

    public void complete() {
        this.status = "COMPLETED";
        this.completedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return type + ": " + subject + " (" + status + ")";
    }
}
