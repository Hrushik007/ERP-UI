package com.erp.model;

import java.time.LocalDateTime;

/**
 * Contact entity representing a person associated with a customer/company.
 *
 * Used by:
 * - CRM Module
 * - Sales Module
 */
public class Contact {

    private int contactId;
    private int customerId;           // Associated customer/company
    private String customerName;      // Cached for display
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String mobile;
    private String jobTitle;
    private String department;
    private String type;              // PRIMARY, BILLING, TECHNICAL, DECISION_MAKER
    private boolean isPrimary;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String notes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastContactDate;

    public Contact() {
        this.active = true;
        this.isPrimary = false;
        this.createdAt = LocalDateTime.now();
    }

    public Contact(String firstName, String lastName, String email) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and Setters
    public int getContactId() { return contactId; }
    public void setContactId(int contactId) { this.contactId = contactId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastContactDate() { return lastContactDate; }
    public void setLastContactDate(LocalDateTime lastContactDate) { this.lastContactDate = lastContactDate; }

    // Convenience methods for compatibility
    public String getTitle() { return jobTitle; }
    public void setTitle(String title) { this.jobTitle = title; }

    public String getMobilePhone() { return mobile; }
    public void setMobilePhone(String mobilePhone) { this.mobile = mobilePhone; }

    @Override
    public String toString() {
        return getFullName() + " (" + (customerName != null ? customerName : "No company") + ")";
    }
}
