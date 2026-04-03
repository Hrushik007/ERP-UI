package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ChartOfAccount entity representing an account in the Chart of Accounts.
 *
 * Account Types follow standard accounting principles:
 * - ASSET: Resources owned (Cash, Inventory, Equipment)
 * - LIABILITY: Obligations owed (Accounts Payable, Loans)
 * - EQUITY: Owner's stake (Capital, Retained Earnings)
 * - REVENUE: Income earned (Sales, Service Revenue)
 * - EXPENSE: Costs incurred (Salaries, Rent, Utilities)
 */
public class ChartOfAccount {

    private int accountId;
    private String accountCode;
    private String accountName;
    private String accountType;      // ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
    private String accountSubType;   // e.g., CURRENT_ASSET, FIXED_ASSET, etc.
    private String parentAccountCode;
    private int level;               // Hierarchy level (1=top level)
    private BigDecimal debitBalance;
    private BigDecimal creditBalance;
    private BigDecimal currentBalance;
    private String normalBalance;    // DEBIT or CREDIT
    private String description;
    private boolean isActive;
    private boolean isSystemAccount; // Cannot be deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChartOfAccount() {
        this.debitBalance = BigDecimal.ZERO;
        this.creditBalance = BigDecimal.ZERO;
        this.currentBalance = BigDecimal.ZERO;
        this.isActive = true;
        this.isSystemAccount = false;
        this.level = 1;
        this.createdAt = LocalDateTime.now();
    }

    public ChartOfAccount(String accountCode, String accountName, String accountType) {
        this();
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.normalBalance = determineNormalBalance(accountType);
    }

    // Determine normal balance based on account type
    private String determineNormalBalance(String type) {
        if (type == null) return "DEBIT";
        switch (type) {
            case "ASSET":
            case "EXPENSE":
                return "DEBIT";
            case "LIABILITY":
            case "EQUITY":
            case "REVENUE":
                return "CREDIT";
            default:
                return "DEBIT";
        }
    }

    // Calculate current balance based on normal balance
    public void calculateBalance() {
        if ("DEBIT".equals(normalBalance)) {
            this.currentBalance = debitBalance.subtract(creditBalance);
        } else {
            this.currentBalance = creditBalance.subtract(debitBalance);
        }
    }

    // Add to debit side
    public void debit(BigDecimal amount) {
        this.debitBalance = this.debitBalance.add(amount);
        calculateBalance();
    }

    // Add to credit side
    public void credit(BigDecimal amount) {
        this.creditBalance = this.creditBalance.add(amount);
        calculateBalance();
    }

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
        this.normalBalance = determineNormalBalance(accountType);
    }

    public String getAccountSubType() { return accountSubType; }
    public void setAccountSubType(String accountSubType) { this.accountSubType = accountSubType; }

    public String getParentAccountCode() { return parentAccountCode; }
    public void setParentAccountCode(String parentAccountCode) { this.parentAccountCode = parentAccountCode; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public BigDecimal getDebitBalance() { return debitBalance; }
    public void setDebitBalance(BigDecimal debitBalance) { this.debitBalance = debitBalance; }

    public BigDecimal getCreditBalance() { return creditBalance; }
    public void setCreditBalance(BigDecimal creditBalance) { this.creditBalance = creditBalance; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public String getNormalBalance() { return normalBalance; }
    public void setNormalBalance(String normalBalance) { this.normalBalance = normalBalance; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isSystemAccount() { return isSystemAccount; }
    public void setSystemAccount(boolean systemAccount) { isSystemAccount = systemAccount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return accountCode + " - " + accountName;
    }
}
