package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account entity representing a financial account (bank, cash, credit).
 *
 * Used by:
 * - Finance Module (cash management)
 * - Accounting Module (chart of accounts)
 */
public class Account {

    private int accountId;
    private String accountNumber;
    private String accountName;
    private String accountType;        // BANK, CASH, CREDIT_CARD, SAVINGS, INVESTMENT
    private String currency;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private String bankName;
    private String bankBranch;
    private String description;
    private String status;             // ACTIVE, INACTIVE, CLOSED
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Account() {
        this.currency = "USD";
        this.currentBalance = BigDecimal.ZERO;
        this.availableBalance = BigDecimal.ZERO;
        this.status = "ACTIVE";
        this.isDefault = false;
        this.createdAt = LocalDateTime.now();
    }

    public Account(String accountNumber, String accountName, String accountType) {
        this();
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
    }

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    @Override
    public String toString() {
        return accountName + " (" + accountNumber + ") - " + currency + " " + currentBalance;
    }
}
