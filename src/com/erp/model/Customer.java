package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Customer entity for CRM and Sales modules.
 *
 * Used by:
 * - CRM Module (customer management)
 * - Sales Module (customer orders)
 * - Marketing Module (campaigns)
 */
public class Customer {

    private int customerId;
    private String companyName;
    private String contactName;
    private String contactTitle;
    private String email;
    private String phone;
    private String fax;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String customerType; // LEAD, PROSPECT, CUSTOMER, VIP
    private String industry;
    private BigDecimal creditLimit;
    private String paymentTerms;
    private int assignedSalesRepId; // References Employee
    private LocalDate createdAt;
    private LocalDate lastContactDate;
    private String notes;
    private boolean active;

    public Customer() {
        this.active = true;
        this.customerType = "LEAD";
    }

    public Customer(int customerId, String companyName, String contactName, String email) {
        this();
        this.customerId = customerId;
        this.companyName = companyName;
        this.contactName = contactName;
        this.email = email;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactTitle() { return contactTitle; }
    public void setContactTitle(String contactTitle) { this.contactTitle = contactTitle; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public int getAssignedSalesRepId() { return assignedSalesRepId; }
    public void setAssignedSalesRepId(int assignedSalesRepId) { this.assignedSalesRepId = assignedSalesRepId; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getLastContactDate() { return lastContactDate; }
    public void setLastContactDate(LocalDate lastContactDate) { this.lastContactDate = lastContactDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (city != null) sb.append(", ").append(city);
        if (state != null) sb.append(", ").append(state);
        if (postalCode != null) sb.append(" ").append(postalCode);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Customer{" + customerId + ": " + companyName + "}";
    }
}
