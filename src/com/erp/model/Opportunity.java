package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Opportunity entity representing a sales deal/potential sale.
 *
 * Used by:
 * - CRM Module (pipeline management)
 * - Sales Module (forecasting)
 */
public class Opportunity {

    private int opportunityId;
    private String name;
    private int customerId;
    private String customerName;       // Cached for display
    private int contactId;
    private String contactName;        // Cached for display
    private int leadId;                // Original lead if converted
    private String stage;              // PROSPECTING, QUALIFICATION, PROPOSAL, NEGOTIATION, CLOSED_WON, CLOSED_LOST
    private BigDecimal amount;
    private int probability;           // 0-100%
    private LocalDate expectedCloseDate;
    private String type;               // NEW_BUSINESS, EXISTING_BUSINESS, RENEWAL
    private String source;             // Lead source
    private int assignedTo;            // Employee ID
    private String assignedToName;
    private String description;
    private String nextStep;
    private String lostReason;         // If CLOSED_LOST
    private BigDecimal actualValue;    // Actual closed value
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedDate;

    public Opportunity() {
        this.stage = "PROSPECTING";
        this.probability = 10;
        this.amount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public Opportunity(String name, int customerId, BigDecimal amount) {
        this();
        this.name = name;
        this.customerId = customerId;
        this.amount = amount;
    }

    // Getters and Setters
    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public int getLeadId() { return leadId; }
    public void setLeadId(int leadId) { this.leadId = leadId; }

    public String getStage() { return stage; }
    public void setStage(String stage) {
        this.stage = stage;
        updateProbabilityByStage();
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getProbability() { return probability; }
    public void setProbability(int probability) { this.probability = Math.max(0, Math.min(100, probability)); }

    public LocalDate getExpectedCloseDate() { return expectedCloseDate; }
    public void setExpectedCloseDate(LocalDate expectedCloseDate) { this.expectedCloseDate = expectedCloseDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNextStep() { return nextStep; }
    public void setNextStep(String nextStep) { this.nextStep = nextStep; }

    public String getLostReason() { return lostReason; }
    public void setLostReason(String lostReason) { this.lostReason = lostReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getClosedDate() { return closedDate; }
    public void setClosedDate(LocalDateTime closedDate) { this.closedDate = closedDate; }

    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }

    // Convenience methods for compatibility
    public BigDecimal getEstimatedValue() { return amount; }
    public void setEstimatedValue(BigDecimal estimatedValue) { this.amount = estimatedValue; }

    // Helper methods
    public boolean isOpen() {
        return !stage.startsWith("CLOSED");
    }

    public boolean isClosed() {
        return stage.startsWith("CLOSED");
    }

    public boolean isWon() {
        return "CLOSED_WON".equals(stage);
    }

    public boolean isLost() {
        return "CLOSED_LOST".equals(stage);
    }

    public void closeAsWon(BigDecimal actualClosedValue) {
        this.stage = "CLOSED_WON";
        this.probability = 100;
        this.actualValue = actualClosedValue;
        this.closedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void closeAsLost(String reason) {
        this.stage = "CLOSED_LOST";
        this.probability = 0;
        this.lostReason = reason;
        this.closedDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getWeightedAmount() {
        if (amount == null) return BigDecimal.ZERO;
        return amount.multiply(BigDecimal.valueOf(probability)).divide(BigDecimal.valueOf(100));
    }

    private void updateProbabilityByStage() {
        switch (stage) {
            case "PROSPECTING": probability = 10; break;
            case "QUALIFICATION": probability = 25; break;
            case "PROPOSAL": probability = 50; break;
            case "NEGOTIATION": probability = 75; break;
            case "CLOSED_WON": probability = 100; break;
            case "CLOSED_LOST": probability = 0; break;
        }
    }

    @Override
    public String toString() {
        return name + " - $" + amount + " (" + stage + ")";
    }
}
