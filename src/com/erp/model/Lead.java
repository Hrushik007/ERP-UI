package com.erp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lead entity representing a potential customer/prospect.
 *
 * Used by:
 * - CRM Module
 * - Sales Module (conversion to opportunity)
 * - Marketing Module (campaigns)
 */
public class Lead {

    private int leadId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private String jobTitle;
    private String source;             // WEB, REFERRAL, CAMPAIGN, COLD_CALL, TRADE_SHOW
    private String status;             // NEW, CONTACTED, QUALIFIED, UNQUALIFIED, CONVERTED
    private String industry;
    private String notes;
    private int assignedTo;            // Employee ID
    private String assignedToName;
    private LocalDate expectedCloseDate;
    private java.math.BigDecimal estimatedValue;
    private int rating;                // 1-5 lead quality rating
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastContactDate;
    private LocalDateTime convertedAt;
    private int convertedToCustomerId;
    private int convertedToOpportunityId;

    public Lead() {
        this.status = "NEW";
        this.rating = 3;
        this.createdAt = LocalDateTime.now();
    }

    public Lead(String firstName, String lastName, String email, String company) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
    }

    // Getters and Setters
    public int getLeadId() { return leadId; }
    public void setLeadId(int leadId) { this.leadId = leadId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getAssignedTo() { return assignedTo; }
    public void setAssignedTo(int assignedTo) { this.assignedTo = assignedTo; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public LocalDate getExpectedCloseDate() { return expectedCloseDate; }
    public void setExpectedCloseDate(LocalDate expectedCloseDate) { this.expectedCloseDate = expectedCloseDate; }

    public java.math.BigDecimal getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(java.math.BigDecimal estimatedValue) { this.estimatedValue = estimatedValue; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastContactDate() { return lastContactDate; }
    public void setLastContactDate(LocalDateTime lastContactDate) { this.lastContactDate = lastContactDate; }

    public LocalDateTime getConvertedAt() { return convertedAt; }
    public void setConvertedAt(LocalDateTime convertedAt) { this.convertedAt = convertedAt; }

    public int getConvertedToCustomerId() { return convertedToCustomerId; }
    public void setConvertedToCustomerId(int convertedToCustomerId) { this.convertedToCustomerId = convertedToCustomerId; }

    public int getConvertedToOpportunityId() { return convertedToOpportunityId; }
    public void setConvertedToOpportunityId(int convertedToOpportunityId) { this.convertedToOpportunityId = convertedToOpportunityId; }

    // Convenience methods for compatibility
    public String getCompanyName() { return company; }
    public void setCompanyName(String companyName) { this.company = companyName; }

    public String getContactName() { return getFullName(); }
    public void setContactName(String contactName) {
        if (contactName != null) {
            String[] parts = contactName.split(" ", 2);
            this.firstName = parts[0];
            this.lastName = parts.length > 1 ? parts[1] : "";
        }
    }

    public String getTitle() { return jobTitle; }
    public void setTitle(String title) { this.jobTitle = title; }

    public boolean isConverted() { return "CONVERTED".equals(status); }
    public boolean isQualified() { return "QUALIFIED".equals(status); }

    @Override
    public String toString() {
        return getFullName() + " (" + company + ")";
    }
}
