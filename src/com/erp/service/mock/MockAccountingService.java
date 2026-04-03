package com.erp.service.mock;

import com.erp.model.ChartOfAccount;
import com.erp.model.JournalEntry;
import com.erp.model.JournalEntryLine;
import com.erp.service.interfaces.AccountingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of AccountingService with sample data.
 */
public class MockAccountingService implements AccountingService {

    private static MockAccountingService instance;
    private List<ChartOfAccount> accounts;
    private List<JournalEntry> journalEntries;
    private int nextAccountId = 1;
    private int nextEntryId = 1;
    private int entryCounter = 1000;

    private MockAccountingService() {
        accounts = new ArrayList<>();
        journalEntries = new ArrayList<>();
        initializeSampleData();
    }

    public static MockAccountingService getInstance() {
        if (instance == null) {
            instance = new MockAccountingService();
        }
        return instance;
    }

    private void initializeSampleData() {
        // ========== ASSETS (1000-1999) ==========
        addAccount("1000", "Assets", "ASSET", null, true);
        addAccount("1100", "Current Assets", "ASSET", "1000", true);
        addAccount("1110", "Cash", "ASSET", "1100", false);
        addAccount("1120", "Accounts Receivable", "ASSET", "1100", false);
        addAccount("1130", "Inventory", "ASSET", "1100", false);
        addAccount("1140", "Prepaid Expenses", "ASSET", "1100", false);
        addAccount("1200", "Fixed Assets", "ASSET", "1000", true);
        addAccount("1210", "Equipment", "ASSET", "1200", false);
        addAccount("1220", "Furniture & Fixtures", "ASSET", "1200", false);
        addAccount("1230", "Vehicles", "ASSET", "1200", false);
        addAccount("1290", "Accumulated Depreciation", "ASSET", "1200", false);

        // ========== LIABILITIES (2000-2999) ==========
        addAccount("2000", "Liabilities", "LIABILITY", null, true);
        addAccount("2100", "Current Liabilities", "LIABILITY", "2000", true);
        addAccount("2110", "Accounts Payable", "LIABILITY", "2100", false);
        addAccount("2120", "Accrued Expenses", "LIABILITY", "2100", false);
        addAccount("2130", "Taxes Payable", "LIABILITY", "2100", false);
        addAccount("2140", "Salaries Payable", "LIABILITY", "2100", false);
        addAccount("2200", "Long-term Liabilities", "LIABILITY", "2000", true);
        addAccount("2210", "Bank Loan", "LIABILITY", "2200", false);
        addAccount("2220", "Mortgage Payable", "LIABILITY", "2200", false);

        // ========== EQUITY (3000-3999) ==========
        addAccount("3000", "Equity", "EQUITY", null, true);
        addAccount("3100", "Owner's Capital", "EQUITY", "3000", false);
        addAccount("3200", "Retained Earnings", "EQUITY", "3000", false);
        addAccount("3300", "Dividends", "EQUITY", "3000", false);

        // ========== REVENUE (4000-4999) ==========
        addAccount("4000", "Revenue", "REVENUE", null, true);
        addAccount("4100", "Sales Revenue", "REVENUE", "4000", false);
        addAccount("4200", "Service Revenue", "REVENUE", "4000", false);
        addAccount("4300", "Interest Income", "REVENUE", "4000", false);
        addAccount("4400", "Other Income", "REVENUE", "4000", false);

        // ========== EXPENSES (5000-5999) ==========
        addAccount("5000", "Expenses", "EXPENSE", null, true);
        addAccount("5100", "Cost of Goods Sold", "EXPENSE", "5000", false);
        addAccount("5200", "Salaries & Wages", "EXPENSE", "5000", false);
        addAccount("5300", "Rent Expense", "EXPENSE", "5000", false);
        addAccount("5400", "Utilities Expense", "EXPENSE", "5000", false);
        addAccount("5500", "Office Supplies", "EXPENSE", "5000", false);
        addAccount("5600", "Depreciation Expense", "EXPENSE", "5000", false);
        addAccount("5700", "Insurance Expense", "EXPENSE", "5000", false);
        addAccount("5800", "Marketing Expense", "EXPENSE", "5000", false);
        addAccount("5900", "Miscellaneous Expense", "EXPENSE", "5000", false);

        // Set some initial balances
        setAccountBalance("1110", new BigDecimal("50000.00"), BigDecimal.ZERO);  // Cash
        setAccountBalance("1120", new BigDecimal("25000.00"), BigDecimal.ZERO);  // A/R
        setAccountBalance("1130", new BigDecimal("35000.00"), BigDecimal.ZERO);  // Inventory
        setAccountBalance("1210", new BigDecimal("15000.00"), BigDecimal.ZERO);  // Equipment
        setAccountBalance("2110", BigDecimal.ZERO, new BigDecimal("18000.00"));  // A/P
        setAccountBalance("2210", BigDecimal.ZERO, new BigDecimal("50000.00"));  // Bank Loan
        setAccountBalance("3100", BigDecimal.ZERO, new BigDecimal("45000.00"));  // Owner's Capital
        setAccountBalance("3200", BigDecimal.ZERO, new BigDecimal("12000.00"));  // Retained Earnings
        setAccountBalance("4100", BigDecimal.ZERO, new BigDecimal("85000.00"));  // Sales Revenue
        setAccountBalance("4200", BigDecimal.ZERO, new BigDecimal("15000.00"));  // Service Revenue
        setAccountBalance("5100", new BigDecimal("45000.00"), BigDecimal.ZERO);  // COGS
        setAccountBalance("5200", new BigDecimal("28000.00"), BigDecimal.ZERO);  // Salaries
        setAccountBalance("5300", new BigDecimal("6000.00"), BigDecimal.ZERO);   // Rent
        setAccountBalance("5400", new BigDecimal("2000.00"), BigDecimal.ZERO);   // Utilities

        // Add sample journal entries
        createSampleJournalEntries();
    }

    private void addAccount(String code, String name, String type, String parentCode, boolean isHeader) {
        ChartOfAccount account = new ChartOfAccount(code, name, type);
        account.setAccountId(nextAccountId++);
        account.setParentAccountCode(parentCode);
        account.setSystemAccount(isHeader);
        if (parentCode != null) {
            ChartOfAccount parent = getAccountByCode(parentCode);
            if (parent != null) {
                account.setLevel(parent.getLevel() + 1);
            }
        }
        accounts.add(account);
    }

    private void setAccountBalance(String code, BigDecimal debit, BigDecimal credit) {
        ChartOfAccount account = getAccountByCode(code);
        if (account != null) {
            account.setDebitBalance(debit);
            account.setCreditBalance(credit);
            account.calculateBalance();
        }
    }

    private void createSampleJournalEntries() {
        // Entry 1: Record Sales
        JournalEntry entry1 = new JournalEntry();
        entry1.setEntryId(nextEntryId++);
        entry1.setEntryNumber("JE-1001");
        entry1.setEntryDate(LocalDate.now().minusDays(10));
        entry1.setDescription("Record sales revenue");
        entry1.setReference("INV-2024-001");
        entry1.setStatus("POSTED");
        entry1.addLine(JournalEntryLine.debit("1120", "Accounts Receivable", new BigDecimal("5000.00")));
        entry1.addLine(JournalEntryLine.credit("4100", "Sales Revenue", new BigDecimal("5000.00")));
        journalEntries.add(entry1);

        // Entry 2: Pay Rent
        JournalEntry entry2 = new JournalEntry();
        entry2.setEntryId(nextEntryId++);
        entry2.setEntryNumber("JE-1002");
        entry2.setEntryDate(LocalDate.now().minusDays(7));
        entry2.setDescription("Pay monthly rent");
        entry2.setReference("CHK-5001");
        entry2.setStatus("POSTED");
        entry2.addLine(JournalEntryLine.debit("5300", "Rent Expense", new BigDecimal("2000.00")));
        entry2.addLine(JournalEntryLine.credit("1110", "Cash", new BigDecimal("2000.00")));
        journalEntries.add(entry2);

        // Entry 3: Record Purchase on Credit
        JournalEntry entry3 = new JournalEntry();
        entry3.setEntryId(nextEntryId++);
        entry3.setEntryNumber("JE-1003");
        entry3.setEntryDate(LocalDate.now().minusDays(5));
        entry3.setDescription("Purchase inventory on credit");
        entry3.setReference("PO-2024-015");
        entry3.setStatus("POSTED");
        entry3.addLine(JournalEntryLine.debit("1130", "Inventory", new BigDecimal("8000.00")));
        entry3.addLine(JournalEntryLine.credit("2110", "Accounts Payable", new BigDecimal("8000.00")));
        journalEntries.add(entry3);

        // Entry 4: Draft entry (not posted)
        JournalEntry entry4 = new JournalEntry();
        entry4.setEntryId(nextEntryId++);
        entry4.setEntryNumber("JE-1004");
        entry4.setEntryDate(LocalDate.now());
        entry4.setDescription("Record utility expense");
        entry4.setStatus("DRAFT");
        entry4.addLine(JournalEntryLine.debit("5400", "Utilities Expense", new BigDecimal("500.00")));
        entry4.addLine(JournalEntryLine.credit("1110", "Cash", new BigDecimal("500.00")));
        journalEntries.add(entry4);

        entryCounter = 1005;
    }

    // ==================== Chart of Accounts Implementation ====================

    @Override
    public List<ChartOfAccount> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    @Override
    public List<ChartOfAccount> getAccountsByType(String accountType) {
        return accounts.stream()
                .filter(a -> accountType.equals(a.getAccountType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartOfAccount> getActiveAccounts() {
        return accounts.stream()
                .filter(ChartOfAccount::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public ChartOfAccount getAccountById(int accountId) {
        return accounts.stream()
                .filter(a -> a.getAccountId() == accountId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ChartOfAccount getAccountByCode(String accountCode) {
        return accounts.stream()
                .filter(a -> accountCode.equals(a.getAccountCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ChartOfAccount createAccount(ChartOfAccount account) {
        account.setAccountId(nextAccountId++);
        accounts.add(account);
        return account;
    }

    @Override
    public boolean updateAccount(ChartOfAccount account) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountId() == account.getAccountId()) {
                accounts.set(i, account);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteAccount(int accountId) {
        ChartOfAccount account = getAccountById(accountId);
        if (account != null && !account.isSystemAccount()) {
            account.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean accountCodeExists(String accountCode) {
        return accounts.stream().anyMatch(a -> accountCode.equals(a.getAccountCode()));
    }

    // ==================== Journal Entries Implementation ====================

    @Override
    public List<JournalEntry> getAllJournalEntries() {
        return new ArrayList<>(journalEntries);
    }

    @Override
    public List<JournalEntry> getJournalEntriesByStatus(String status) {
        return journalEntries.stream()
                .filter(e -> status.equals(e.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<JournalEntry> getJournalEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        return journalEntries.stream()
                .filter(e -> !e.getEntryDate().isBefore(startDate) && !e.getEntryDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public JournalEntry getJournalEntryById(int entryId) {
        return journalEntries.stream()
                .filter(e -> e.getEntryId() == entryId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public JournalEntry getJournalEntryByNumber(String entryNumber) {
        return journalEntries.stream()
                .filter(e -> entryNumber.equals(e.getEntryNumber()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public JournalEntry createJournalEntry(JournalEntry entry) {
        entry.setEntryId(nextEntryId++);
        if (entry.getEntryNumber() == null || entry.getEntryNumber().isEmpty()) {
            entry.setEntryNumber(generateEntryNumber());
        }
        journalEntries.add(entry);
        return entry;
    }

    @Override
    public boolean updateJournalEntry(JournalEntry entry) {
        if (!"DRAFT".equals(entry.getStatus())) {
            return false; // Can only update draft entries
        }
        for (int i = 0; i < journalEntries.size(); i++) {
            if (journalEntries.get(i).getEntryId() == entry.getEntryId()) {
                journalEntries.set(i, entry);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean postJournalEntry(int entryId) {
        JournalEntry entry = getJournalEntryById(entryId);
        if (entry != null && entry.isDraft() && entry.isBalanced()) {
            return entry.post();
        }
        return false;
    }

    @Override
    public boolean voidJournalEntry(int entryId) {
        JournalEntry entry = getJournalEntryById(entryId);
        if (entry != null && !entry.isVoid()) {
            entry.voidEntry();
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteJournalEntry(int entryId) {
        JournalEntry entry = getJournalEntryById(entryId);
        if (entry != null && entry.isDraft()) {
            return journalEntries.remove(entry);
        }
        return false;
    }

    @Override
    public String generateEntryNumber() {
        return "JE-" + (entryCounter++);
    }

    // ==================== General Ledger Implementation ====================

    @Override
    public List<JournalEntryLine> getLedgerEntriesForAccount(String accountCode) {
        List<JournalEntryLine> ledgerEntries = new ArrayList<>();
        for (JournalEntry entry : journalEntries) {
            if ("POSTED".equals(entry.getStatus())) {
                for (JournalEntryLine line : entry.getLines()) {
                    if (accountCode.equals(line.getAccountCode())) {
                        ledgerEntries.add(line);
                    }
                }
            }
        }
        return ledgerEntries;
    }

    @Override
    public List<JournalEntryLine> getLedgerEntriesForAccount(String accountCode, LocalDate startDate, LocalDate endDate) {
        List<JournalEntryLine> ledgerEntries = new ArrayList<>();
        for (JournalEntry entry : journalEntries) {
            if ("POSTED".equals(entry.getStatus()) &&
                !entry.getEntryDate().isBefore(startDate) &&
                !entry.getEntryDate().isAfter(endDate)) {
                for (JournalEntryLine line : entry.getLines()) {
                    if (accountCode.equals(line.getAccountCode())) {
                        ledgerEntries.add(line);
                    }
                }
            }
        }
        return ledgerEntries;
    }

    @Override
    public BigDecimal getAccountBalance(String accountCode, LocalDate asOfDate) {
        ChartOfAccount account = getAccountByCode(accountCode);
        if (account == null) return BigDecimal.ZERO;
        return account.getCurrentBalance();
    }

    @Override
    public Map<String, BigDecimal> getTrialBalance() {
        Map<String, BigDecimal> trialBalance = new LinkedHashMap<>();
        for (ChartOfAccount account : accounts) {
            if (!account.isSystemAccount() && account.getCurrentBalance().compareTo(BigDecimal.ZERO) != 0) {
                trialBalance.put(account.getAccountCode() + " - " + account.getAccountName(),
                        account.getCurrentBalance());
            }
        }
        return trialBalance;
    }

    @Override
    public Map<String, BigDecimal> getTrialBalance(LocalDate asOfDate) {
        return getTrialBalance(); // Simplified for mock
    }

    // ==================== Reports Implementation ====================

    @Override
    public BigDecimal getTotalAssets() {
        return accounts.stream()
                .filter(a -> "ASSET".equals(a.getAccountType()) && !a.isSystemAccount())
                .map(ChartOfAccount::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalLiabilities() {
        return accounts.stream()
                .filter(a -> "LIABILITY".equals(a.getAccountType()) && !a.isSystemAccount())
                .map(ChartOfAccount::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalEquity() {
        return accounts.stream()
                .filter(a -> "EQUITY".equals(a.getAccountType()) && !a.isSystemAccount())
                .map(ChartOfAccount::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalRevenue() {
        return accounts.stream()
                .filter(a -> "REVENUE".equals(a.getAccountType()) && !a.isSystemAccount())
                .map(ChartOfAccount::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalExpenses() {
        return accounts.stream()
                .filter(a -> "EXPENSE".equals(a.getAccountType()) && !a.isSystemAccount())
                .map(ChartOfAccount::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getNetIncome() {
        return getTotalRevenue().subtract(getTotalExpenses());
    }
}
