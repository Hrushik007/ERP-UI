package com.erp.service.interfaces;

import com.erp.model.ChartOfAccount;
import com.erp.model.JournalEntry;
import com.erp.model.JournalEntryLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AccountingService interface defining accounting operations.
 *
 * Manages Chart of Accounts, Journal Entries, and General Ledger.
 */
public interface AccountingService {

    // ==================== Chart of Accounts ====================

    /**
     * Get all accounts in the chart of accounts
     */
    List<ChartOfAccount> getAllAccounts();

    /**
     * Get accounts by type (ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE)
     */
    List<ChartOfAccount> getAccountsByType(String accountType);

    /**
     * Get active accounts only
     */
    List<ChartOfAccount> getActiveAccounts();

    /**
     * Get account by ID
     */
    ChartOfAccount getAccountById(int accountId);

    /**
     * Get account by code
     */
    ChartOfAccount getAccountByCode(String accountCode);

    /**
     * Create a new account
     */
    ChartOfAccount createAccount(ChartOfAccount account);

    /**
     * Update an existing account
     */
    boolean updateAccount(ChartOfAccount account);

    /**
     * Delete an account (soft delete)
     */
    boolean deleteAccount(int accountId);

    /**
     * Check if account code exists
     */
    boolean accountCodeExists(String accountCode);

    // ==================== Journal Entries ====================

    /**
     * Get all journal entries
     */
    List<JournalEntry> getAllJournalEntries();

    /**
     * Get journal entries by status (DRAFT, POSTED, VOID)
     */
    List<JournalEntry> getJournalEntriesByStatus(String status);

    /**
     * Get journal entries by date range
     */
    List<JournalEntry> getJournalEntriesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get journal entry by ID
     */
    JournalEntry getJournalEntryById(int entryId);

    /**
     * Get journal entry by entry number
     */
    JournalEntry getJournalEntryByNumber(String entryNumber);

    /**
     * Create a new journal entry
     */
    JournalEntry createJournalEntry(JournalEntry entry);

    /**
     * Update a journal entry (only if DRAFT)
     */
    boolean updateJournalEntry(JournalEntry entry);

    /**
     * Post a journal entry
     */
    boolean postJournalEntry(int entryId);

    /**
     * Void a journal entry
     */
    boolean voidJournalEntry(int entryId);

    /**
     * Delete a journal entry (only if DRAFT)
     */
    boolean deleteJournalEntry(int entryId);

    /**
     * Generate next entry number
     */
    String generateEntryNumber();

    // ==================== General Ledger ====================

    /**
     * Get general ledger entries for an account
     */
    List<JournalEntryLine> getLedgerEntriesForAccount(String accountCode);

    /**
     * Get general ledger entries for an account within date range
     */
    List<JournalEntryLine> getLedgerEntriesForAccount(String accountCode, LocalDate startDate, LocalDate endDate);

    /**
     * Get account balance as of a specific date
     */
    BigDecimal getAccountBalance(String accountCode, LocalDate asOfDate);

    /**
     * Get trial balance (all accounts with their balances)
     */
    Map<String, BigDecimal> getTrialBalance();

    /**
     * Get trial balance as of a specific date
     */
    Map<String, BigDecimal> getTrialBalance(LocalDate asOfDate);

    // ==================== Reports ====================

    /**
     * Get total assets
     */
    BigDecimal getTotalAssets();

    /**
     * Get total liabilities
     */
    BigDecimal getTotalLiabilities();

    /**
     * Get total equity
     */
    BigDecimal getTotalEquity();

    /**
     * Get total revenue
     */
    BigDecimal getTotalRevenue();

    /**
     * Get total expenses
     */
    BigDecimal getTotalExpenses();

    /**
     * Get net income (Revenue - Expenses)
     */
    BigDecimal getNetIncome();
}
