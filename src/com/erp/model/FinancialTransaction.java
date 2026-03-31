package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FinancialTransaction entity representing a financial transaction.
 *
 * Used by:
 * - Finance Module (transaction tracking)
 * - Accounting Module (journal entries)
 */
public class FinancialTransaction {

    private int transactionId;
    private String transactionNumber;
    private String transactionType;    // INCOME, EXPENSE, TRANSFER, REFUND
    private int accountId;
    private String accountName;
    private int toAccountId;           // For transfers
    private String toAccountName;
    private String category;           // SALES, PURCHASE, SALARY, UTILITIES, RENT, etc.
    private BigDecimal amount;
    private String currency;
    private LocalDate transactionDate;
    private String paymentMethod;      // CASH, CHECK, BANK_TRANSFER, CREDIT_CARD
    private String reference;          // Check number, transaction ID, etc.
    private int relatedEntityId;       // Invoice ID, Order ID, etc.
    private String relatedEntityType;  // INVOICE, ORDER, EXPENSE
    private String description;
    private String status;             // PENDING, COMPLETED, CANCELLED, RECONCILED
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FinancialTransaction() {
        this.currency = "USD";
        this.status = "COMPLETED";
        this.transactionDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public FinancialTransaction(String transactionType, int accountId, BigDecimal amount) {
        this();
        this.transactionType = transactionType;
        this.accountId = accountId;
        this.amount = amount;
    }

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public String getTransactionNumber() { return transactionNumber; }
    public void setTransactionNumber(String transactionNumber) { this.transactionNumber = transactionNumber; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public int getToAccountId() { return toAccountId; }
    public void setToAccountId(int toAccountId) { this.toAccountId = toAccountId; }

    public String getToAccountName() { return toAccountName; }
    public void setToAccountName(String toAccountName) { this.toAccountName = toAccountName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public int getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(int relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isIncome() {
        return "INCOME".equals(transactionType);
    }

    public boolean isExpense() {
        return "EXPENSE".equals(transactionType);
    }

    public boolean isTransfer() {
        return "TRANSFER".equals(transactionType);
    }

    @Override
    public String toString() {
        return transactionNumber + " - " + transactionType + " - " + amount;
    }
}
