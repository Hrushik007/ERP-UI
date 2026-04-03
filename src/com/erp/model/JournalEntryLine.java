package com.erp.model;

import java.math.BigDecimal;

/**
 * JournalEntryLine entity representing a single line in a journal entry.
 *
 * Each line affects one account and has either a debit or credit amount (not both).
 */
public class JournalEntryLine {

    private int lineId;
    private int entryId;
    private int lineNumber;
    private String accountCode;
    private String accountName;
    private String description;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    public JournalEntryLine() {
        this.debitAmount = BigDecimal.ZERO;
        this.creditAmount = BigDecimal.ZERO;
    }

    public JournalEntryLine(String accountCode, String accountName, BigDecimal debit, BigDecimal credit) {
        this();
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.debitAmount = debit != null ? debit : BigDecimal.ZERO;
        this.creditAmount = credit != null ? credit : BigDecimal.ZERO;
    }

    // Create a debit line
    public static JournalEntryLine debit(String accountCode, String accountName, BigDecimal amount) {
        return new JournalEntryLine(accountCode, accountName, amount, BigDecimal.ZERO);
    }

    // Create a credit line
    public static JournalEntryLine credit(String accountCode, String accountName, BigDecimal amount) {
        return new JournalEntryLine(accountCode, accountName, BigDecimal.ZERO, amount);
    }

    // Check if this is a debit line
    public boolean isDebit() {
        return debitAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Check if this is a credit line
    public boolean isCredit() {
        return creditAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    // Get the amount (whichever is non-zero)
    public BigDecimal getAmount() {
        if (isDebit()) return debitAmount;
        if (isCredit()) return creditAmount;
        return BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getLineId() { return lineId; }
    public void setLineId(int lineId) { this.lineId = lineId; }

    public int getEntryId() { return entryId; }
    public void setEntryId(int entryId) { this.entryId = entryId; }

    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getDebitAmount() { return debitAmount; }
    public void setDebitAmount(BigDecimal debitAmount) { this.debitAmount = debitAmount != null ? debitAmount : BigDecimal.ZERO; }

    public BigDecimal getCreditAmount() { return creditAmount; }
    public void setCreditAmount(BigDecimal creditAmount) { this.creditAmount = creditAmount != null ? creditAmount : BigDecimal.ZERO; }

    @Override
    public String toString() {
        if (isDebit()) {
            return accountCode + " DR " + debitAmount;
        } else {
            return accountCode + " CR " + creditAmount;
        }
    }
}
