package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payment entity for Finance and Accounting modules.
 */
public class Payment {

    private int paymentId;
    private String paymentNumber;
    private String paymentType; // RECEIVED (from customer) or MADE (to vendor)
    private int invoiceId;
    private int customerId;
    private int vendorId;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private String paymentMethod; // CASH, CHECK, CREDIT_CARD, BANK_TRANSFER, etc.
    private String referenceNumber; // Check number, transaction ID, etc.
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private String bankAccount;
    private String notes;
    private int processedById; // Employee who processed
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment() {
        this.status = "PENDING";
        this.paymentDate = LocalDate.now();
    }

    // Getters and Setters
    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getProcessedById() { return processedById; }
    public void setProcessedById(int processedById) { this.processedById = processedById; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Payment{" + paymentNumber + ", amount=" + amount + ", type=" + paymentType + "}";
    }
}
