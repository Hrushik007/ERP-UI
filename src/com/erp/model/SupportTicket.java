package com.erp.model;

import java.time.LocalDateTime;

/**
 * SupportTicket entity for CRM Customer Service module.
 */
public class SupportTicket {

    private int ticketId;
    private String ticketNumber;
    private int customerId;
    private Customer customer;
    private String subject;
    private String description;
    private String category; // TECHNICAL, BILLING, GENERAL, COMPLAINT, FEATURE_REQUEST
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String status; // OPEN, IN_PROGRESS, WAITING_CUSTOMER, RESOLVED, CLOSED
    private int assignedToId; // Employee
    private int createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private String resolution;
    private int satisfactionRating; // 1-5
    private String satisfactionFeedback;

    public SupportTicket() {
        this.status = "OPEN";
        this.priority = "MEDIUM";
        this.createdAt = LocalDateTime.now();
    }

    public long getResponseTimeMinutes() {
        if (createdAt == null || updatedAt == null) return 0;
        return java.time.Duration.between(createdAt, updatedAt).toMinutes();
    }

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAssignedToId() { return assignedToId; }
    public void setAssignedToId(int assignedToId) { this.assignedToId = assignedToId; }

    public int getCreatedById() { return createdById; }
    public void setCreatedById(int createdById) { this.createdById = createdById; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public int getSatisfactionRating() { return satisfactionRating; }
    public void setSatisfactionRating(int satisfactionRating) { this.satisfactionRating = satisfactionRating; }

    public String getSatisfactionFeedback() { return satisfactionFeedback; }
    public void setSatisfactionFeedback(String satisfactionFeedback) { this.satisfactionFeedback = satisfactionFeedback; }

    @Override
    public String toString() {
        return "SupportTicket{" + ticketNumber + ": " + subject + ", status=" + status + "}";
    }
}
