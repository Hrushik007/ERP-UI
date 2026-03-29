package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Vendor/Supplier entity for Supply Chain module.
 *
 * Used by:
 * - Supply Chain/Purchasing Module
 * - Inventory Module
 * - Manufacturing Module
 */
public class Vendor {

    private int vendorId;
    private String vendorCode;
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
    private String website;
    private String paymentTerms; // NET30, NET60, etc.
    private BigDecimal creditLimit;
    private String bankName;
    private String bankAccountNumber;
    private String taxId;
    private String category; // RAW_MATERIAL, FINISHED_GOODS, SERVICES, etc.
    private int rating; // 1-5 star rating
    private String status; // ACTIVE, INACTIVE, BLOCKED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;

    public Vendor() {
        this.status = "ACTIVE";
        this.rating = 3;
    }

    public Vendor(int vendorId, String vendorCode, String companyName, String email) {
        this();
        this.vendorId = vendorId;
        this.vendorCode = vendorCode;
        this.companyName = companyName;
        this.email = email;
    }

    // Getters and Setters
    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }

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

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Vendor{" + vendorCode + ": " + companyName + "}";
    }
}
