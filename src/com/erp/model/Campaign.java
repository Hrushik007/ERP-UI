package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Campaign entity for Marketing module.
 */
public class Campaign {

    private int campaignId;
    private String name;
    private String description;
    private String type; // EMAIL, SOCIAL_MEDIA, ADS, EVENT, CONTENT, etc.
    private String status; // PLANNED, ACTIVE, PAUSED, COMPLETED, CANCELLED
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budget;
    private BigDecimal actualSpend;
    private String targetAudience;
    private int leadTarget;
    private int leadsGenerated;
    private int ownerId; // Employee managing the campaign
    private String channel;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Campaign() {
        this.status = "PLANNED";
        this.leadsGenerated = 0;
    }

    public BigDecimal getBudgetVariance() {
        if (budget == null || actualSpend == null) return BigDecimal.ZERO;
        return budget.subtract(actualSpend);
    }

    public double getLeadConversionRate() {
        if (leadTarget == 0) return 0;
        return (double) leadsGenerated / leadTarget * 100;
    }

    // Getters and Setters
    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public BigDecimal getActualSpend() { return actualSpend; }
    public void setActualSpend(BigDecimal actualSpend) { this.actualSpend = actualSpend; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public int getLeadTarget() { return leadTarget; }
    public void setLeadTarget(int leadTarget) { this.leadTarget = leadTarget; }

    public int getLeadsGenerated() { return leadsGenerated; }
    public void setLeadsGenerated(int leadsGenerated) { this.leadsGenerated = leadsGenerated; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Campaign{" + name + ", type=" + type + ", status=" + status + "}";
    }
}
