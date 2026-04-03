package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JournalEntry entity representing a double-entry accounting transaction.
 *
 * A journal entry must always balance (total debits = total credits).
 * Each entry contains multiple lines that affect different accounts.
 */
public class JournalEntry {

    private int entryId;
    private String entryNumber;
    private LocalDate entryDate;
    private String description;
    private String reference;         // External reference (invoice #, etc.)
    private String entryType;         // STANDARD, ADJUSTING, CLOSING, REVERSING
    private String status;            // DRAFT, POSTED, VOID
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private List<JournalEntryLine> lines;
    private int createdBy;
    private int approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime postedAt;

    public JournalEntry() {
        this.lines = new ArrayList<>();
        this.totalDebit = BigDecimal.ZERO;
        this.totalCredit = BigDecimal.ZERO;
        this.status = "DRAFT";
        this.entryType = "STANDARD";
        this.entryDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    public JournalEntry(String entryNumber, String description) {
        this();
        this.entryNumber = entryNumber;
        this.description = description;
    }

    // Add a line to this journal entry
    public void addLine(JournalEntryLine line) {
        line.setEntryId(this.entryId);
        line.setLineNumber(this.lines.size() + 1);
        this.lines.add(line);
        recalculateTotals();
    }

    // Remove a line from this journal entry
    public void removeLine(JournalEntryLine line) {
        this.lines.remove(line);
        recalculateTotals();
    }

    // Recalculate total debits and credits
    public void recalculateTotals() {
        this.totalDebit = BigDecimal.ZERO;
        this.totalCredit = BigDecimal.ZERO;
        for (JournalEntryLine line : lines) {
            if (line.getDebitAmount() != null) {
                this.totalDebit = this.totalDebit.add(line.getDebitAmount());
            }
            if (line.getCreditAmount() != null) {
                this.totalCredit = this.totalCredit.add(line.getCreditAmount());
            }
        }
    }

    // Check if entry is balanced
    public boolean isBalanced() {
        return totalDebit.compareTo(totalCredit) == 0;
    }

    // Post the journal entry
    public boolean post() {
        if (!isBalanced()) {
            return false;
        }
        if (lines.isEmpty()) {
            return false;
        }
        this.status = "POSTED";
        this.postedAt = LocalDateTime.now();
        return true;
    }

    // Void the journal entry
    public void voidEntry() {
        this.status = "VOID";
    }

    // Get the out-of-balance amount
    public BigDecimal getOutOfBalanceAmount() {
        return totalDebit.subtract(totalCredit).abs();
    }

    // Getters and Setters
    public int getEntryId() { return entryId; }
    public void setEntryId(int entryId) {
        this.entryId = entryId;
        // Update all lines with the entry ID
        for (JournalEntryLine line : lines) {
            line.setEntryId(entryId);
        }
    }

    public String getEntryNumber() { return entryNumber; }
    public void setEntryNumber(String entryNumber) { this.entryNumber = entryNumber; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }

    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }

    public List<JournalEntryLine> getLines() { return lines; }
    public void setLines(List<JournalEntryLine> lines) {
        this.lines = lines;
        recalculateTotals();
    }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public int getApprovedBy() { return approvedBy; }
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    public boolean isDraft() { return "DRAFT".equals(status); }
    public boolean isPosted() { return "POSTED".equals(status); }
    public boolean isVoid() { return "VOID".equals(status); }

    @Override
    public String toString() {
        return entryNumber + " - " + description + " (" + status + ")";
    }
}
