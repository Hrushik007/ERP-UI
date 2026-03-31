package com.erp.service.mock;

import com.erp.model.*;
import com.erp.service.interfaces.FinanceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockFinanceService provides sample data for Finance Module UI.
 */
public class MockFinanceService implements FinanceService {

    private Map<Integer, Invoice> invoices;
    private Map<Integer, Payment> payments;
    private Map<Integer, Account> accounts;
    private Map<Integer, FinancialTransaction> transactions;
    private Map<Integer, Budget> budgets;

    private int nextInvoiceId = 1;
    private int nextPaymentId = 1;
    private int nextAccountId = 1;
    private int nextTransactionId = 1;
    private int nextBudgetId = 1;

    private static MockFinanceService instance;

    public static synchronized MockFinanceService getInstance() {
        if (instance == null) {
            instance = new MockFinanceService();
        }
        return instance;
    }

    private MockFinanceService() {
        invoices = new HashMap<>();
        payments = new HashMap<>();
        accounts = new HashMap<>();
        transactions = new HashMap<>();
        budgets = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create sample accounts
        createSampleAccount("1001", "Main Operating Account", "BANK", "First National Bank", new BigDecimal("125000"));
        createSampleAccount("1002", "Payroll Account", "BANK", "First National Bank", new BigDecimal("45000"));
        createSampleAccount("1003", "Petty Cash", "CASH", null, new BigDecimal("500"));
        createSampleAccount("1004", "Business Credit Card", "CREDIT_CARD", "Chase", new BigDecimal("-2500"));
        createSampleAccount("1005", "Savings Reserve", "SAVINGS", "First National Bank", new BigDecimal("50000"));

        // Create sample invoices
        createSampleInvoice("INV-2024-001", 1, new BigDecimal("5000"), "PAID");
        createSampleInvoice("INV-2024-002", 2, new BigDecimal("7500"), "SENT");
        createSampleInvoice("INV-2024-003", 1, new BigDecimal("12000"), "PARTIAL");
        createSampleInvoice("INV-2024-004", 3, new BigDecimal("3200"), "OVERDUE");
        createSampleInvoice("INV-2024-005", 2, new BigDecimal("8500"), "DRAFT");

        // Create sample transactions
        createSampleTransaction("INCOME", 1, "SALES", new BigDecimal("5000"), "Payment from Customer A");
        createSampleTransaction("EXPENSE", 1, "UTILITIES", new BigDecimal("350"), "Electric bill");
        createSampleTransaction("EXPENSE", 1, "RENT", new BigDecimal("2500"), "Monthly office rent");
        createSampleTransaction("INCOME", 1, "SALES", new BigDecimal("7500"), "Payment from Customer B");
        createSampleTransaction("EXPENSE", 4, "SUPPLIES", new BigDecimal("450"), "Office supplies");
        createSampleTransaction("TRANSFER", 1, "TRANSFER", new BigDecimal("10000"), "Transfer to savings");

        // Create sample budgets
        createSampleBudget("Q1 Marketing", "MARKETING", new BigDecimal("25000"), new BigDecimal("22000"));
        createSampleBudget("Q1 Operations", "OPERATIONS", new BigDecimal("50000"), new BigDecimal("48500"));
        createSampleBudget("Q1 IT", "IT", new BigDecimal("15000"), new BigDecimal("18000"));
        createSampleBudget("Q1 HR", "HR", new BigDecimal("35000"), new BigDecimal("34000"));
        createSampleBudget("Q1 Sales", "SALES", new BigDecimal("20000"), new BigDecimal("19500"));
    }

    private void createSampleAccount(String number, String name, String type, String bankName, BigDecimal balance) {
        Account a = new Account();
        a.setAccountId(nextAccountId++);
        a.setAccountNumber(number);
        a.setAccountName(name);
        a.setAccountType(type);
        a.setBankName(bankName);
        a.setCurrentBalance(balance);
        a.setAvailableBalance(balance);
        a.setStatus("ACTIVE");
        a.setCreatedAt(LocalDateTime.now().minusMonths(6));
        accounts.put(a.getAccountId(), a);
    }

    private void createSampleInvoice(String number, int customerId, BigDecimal amount, String status) {
        Invoice inv = new Invoice();
        inv.setInvoiceId(nextInvoiceId++);
        inv.setInvoiceNumber(number);
        inv.setCustomerId(customerId);
        inv.setInvoiceDate(LocalDate.now().minusDays((int)(Math.random() * 30)));
        inv.setDueDate(inv.getInvoiceDate().plusDays(30));
        inv.setTotalAmount(amount);
        inv.setStatus(status);

        if ("PAID".equals(status)) {
            inv.setAmountPaid(amount);
        } else if ("PARTIAL".equals(status)) {
            inv.setAmountPaid(amount.multiply(new BigDecimal("0.5")));
        } else {
            inv.setAmountPaid(BigDecimal.ZERO);
        }
        inv.calculateBalance();
        inv.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 30)));

        invoices.put(inv.getInvoiceId(), inv);
    }

    private void createSampleTransaction(String type, int accountId, String category, BigDecimal amount, String desc) {
        FinancialTransaction t = new FinancialTransaction();
        t.setTransactionId(nextTransactionId++);
        t.setTransactionNumber("TXN-" + String.format("%06d", t.getTransactionId()));
        t.setTransactionType(type);
        t.setAccountId(accountId);
        Account acc = accounts.get(accountId);
        if (acc != null) {
            t.setAccountName(acc.getAccountName());
        }
        t.setCategory(category);
        t.setAmount(amount);
        t.setDescription(desc);
        t.setTransactionDate(LocalDate.now().minusDays((int)(Math.random() * 30)));
        t.setStatus("COMPLETED");
        t.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 30)));

        if ("TRANSFER".equals(type)) {
            t.setToAccountId(5);
            t.setToAccountName("Savings Reserve");
        }

        transactions.put(t.getTransactionId(), t);
    }

    private void createSampleBudget(String name, String category, BigDecimal budgeted, BigDecimal actual) {
        Budget b = new Budget();
        b.setBudgetId(nextBudgetId++);
        b.setBudgetName(name);
        b.setCategory(category);
        b.setPeriod("QUARTERLY");
        b.setFiscalYear(LocalDate.now().getYear());
        b.setFiscalMonth(1);
        b.setBudgetedAmount(budgeted);
        b.setActualAmount(actual);
        b.calculateVariance();
        b.setStartDate(LocalDate.of(b.getFiscalYear(), 1, 1));
        b.setEndDate(LocalDate.of(b.getFiscalYear(), 3, 31));
        b.setStatus("APPROVED");
        b.setCreatedAt(LocalDateTime.now().minusMonths(3));

        budgets.put(b.getBudgetId(), b);
    }

    // ==================== INVOICES ====================

    @Override
    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices.values());
    }

    @Override
    public List<Invoice> getInvoicesByStatus(String status) {
        return invoices.values().stream()
                .filter(i -> status.equals(i.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> getInvoicesByCustomer(int customerId) {
        return invoices.values().stream()
                .filter(i -> i.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invoice> getOverdueInvoices() {
        return invoices.values().stream()
                .filter(Invoice::isOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public Invoice getInvoiceById(int invoiceId) {
        return invoices.get(invoiceId);
    }

    @Override
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoices.values().stream()
                .filter(i -> invoiceNumber.equals(i.getInvoiceNumber()))
                .findFirst().orElse(null);
    }

    @Override
    public Invoice createInvoiceFromOrder(int orderId) {
        Invoice inv = new Invoice();
        inv.setInvoiceId(nextInvoiceId++);
        inv.setInvoiceNumber("INV-" + String.format("%06d", inv.getInvoiceId()));
        inv.setOrderId(orderId);
        inv.setInvoiceDate(LocalDate.now());
        inv.setDueDate(LocalDate.now().plusDays(30));
        inv.setCreatedAt(LocalDateTime.now());
        invoices.put(inv.getInvoiceId(), inv);
        return inv;
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceId(nextInvoiceId++);
        invoice.setCreatedAt(LocalDateTime.now());
        invoices.put(invoice.getInvoiceId(), invoice);
        return invoice;
    }

    @Override
    public boolean updateInvoice(Invoice invoice) {
        if (invoices.containsKey(invoice.getInvoiceId())) {
            invoice.setUpdatedAt(LocalDateTime.now());
            invoices.put(invoice.getInvoiceId(), invoice);
            return true;
        }
        return false;
    }

    @Override
    public boolean sendInvoice(int invoiceId) {
        Invoice inv = invoices.get(invoiceId);
        if (inv != null && "DRAFT".equals(inv.getStatus())) {
            inv.setStatus("SENT");
            inv.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean voidInvoice(int invoiceId, String reason) {
        Invoice inv = invoices.get(invoiceId);
        if (inv != null) {
            inv.setStatus("VOID");
            inv.setNotes(reason);
            inv.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    // ==================== PAYMENTS ====================

    @Override
    public Payment recordPayment(Payment payment) {
        payment.setPaymentId(nextPaymentId++);
        payment.setCreatedAt(LocalDateTime.now());
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    @Override
    public List<Payment> getPaymentsByInvoice(int invoiceId) {
        return payments.values().stream()
                .filter(p -> p.getInvoiceId() == invoiceId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> getPaymentsByCustomer(int customerId) {
        return payments.values().stream()
                .filter(p -> p.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return payments.values().stream()
                .filter(p -> p.getPaymentDate() != null &&
                        !p.getPaymentDate().isBefore(startDate) &&
                        !p.getPaymentDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        return payments.get(paymentId);
    }

    @Override
    public Payment refundPayment(int paymentId, BigDecimal amount, String reason) {
        Payment original = payments.get(paymentId);
        if (original != null) {
            Payment refund = new Payment();
            refund.setPaymentId(nextPaymentId++);
            refund.setAmount(amount.negate());
            refund.setPaymentMethod("REFUND");
            refund.setNotes("Refund for payment " + paymentId + ": " + reason);
            refund.setCreatedAt(LocalDateTime.now());
            payments.put(refund.getPaymentId(), refund);
            return refund;
        }
        return null;
    }

    // ==================== ANALYTICS ====================

    @Override
    public BigDecimal getTotalAccountsReceivable() {
        return invoices.values().stream()
                .filter(i -> !"PAID".equals(i.getStatus()) && !"VOID".equals(i.getStatus()))
                .map(Invoice::getBalanceDue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, BigDecimal> getAgingReport() {
        Map<String, BigDecimal> aging = new LinkedHashMap<>();
        aging.put("0-30 days", new BigDecimal("15000"));
        aging.put("31-60 days", new BigDecimal("8500"));
        aging.put("61-90 days", new BigDecimal("3200"));
        aging.put("90+ days", new BigDecimal("1500"));
        return aging;
    }

    @Override
    public BigDecimal getRevenue(LocalDate startDate, LocalDate endDate) {
        return new BigDecimal("125000");
    }

    @Override
    public BigDecimal getCashCollected(LocalDate startDate, LocalDate endDate) {
        return new BigDecimal("98500");
    }

    @Override
    public Map<Integer, BigDecimal> getTopCustomersByRevenue(int limit, LocalDate startDate, LocalDate endDate) {
        Map<Integer, BigDecimal> top = new LinkedHashMap<>();
        top.put(1, new BigDecimal("45000"));
        top.put(2, new BigDecimal("35000"));
        top.put(3, new BigDecimal("28000"));
        return top;
    }

    // ==================== ACCOUNTS ====================

    @Override
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public List<Account> getActiveAccounts() {
        return accounts.values().stream()
                .filter(Account::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccountsByType(String accountType) {
        return accounts.values().stream()
                .filter(a -> accountType.equals(a.getAccountType()))
                .collect(Collectors.toList());
    }

    @Override
    public Account getAccountById(int accountId) {
        return accounts.get(accountId);
    }

    @Override
    public Account createAccount(Account account) {
        account.setAccountId(nextAccountId++);
        account.setCreatedAt(LocalDateTime.now());
        accounts.put(account.getAccountId(), account);
        return account;
    }

    @Override
    public boolean updateAccount(Account account) {
        if (accounts.containsKey(account.getAccountId())) {
            account.setUpdatedAt(LocalDateTime.now());
            accounts.put(account.getAccountId(), account);
            return true;
        }
        return false;
    }

    @Override
    public BigDecimal getTotalBalance() {
        return accounts.values().stream()
                .filter(Account::isActive)
                .map(Account::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==================== TRANSACTIONS ====================

    @Override
    public List<FinancialTransaction> getAllTransactions() {
        return transactions.values().stream()
                .sorted((a, b) -> b.getTransactionDate().compareTo(a.getTransactionDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransaction> getTransactionsByType(String transactionType) {
        return transactions.values().stream()
                .filter(t -> transactionType.equals(t.getTransactionType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransaction> getTransactionsByAccount(int accountId) {
        return transactions.values().stream()
                .filter(t -> t.getAccountId() == accountId || t.getToAccountId() == accountId)
                .collect(Collectors.toList());
    }

    @Override
    public List<FinancialTransaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactions.values().stream()
                .filter(t -> t.getTransactionDate() != null &&
                        !t.getTransactionDate().isBefore(startDate) &&
                        !t.getTransactionDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public FinancialTransaction getTransactionById(int transactionId) {
        return transactions.get(transactionId);
    }

    @Override
    public FinancialTransaction createTransaction(FinancialTransaction transaction) {
        transaction.setTransactionId(nextTransactionId++);
        transaction.setTransactionNumber("TXN-" + String.format("%06d", transaction.getTransactionId()));
        transaction.setCreatedAt(LocalDateTime.now());
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction;
    }

    @Override
    public BigDecimal getTotalIncome(LocalDate startDate, LocalDate endDate) {
        return transactions.values().stream()
                .filter(t -> "INCOME".equals(t.getTransactionType()))
                .filter(t -> t.getTransactionDate() != null &&
                        !t.getTransactionDate().isBefore(startDate) &&
                        !t.getTransactionDate().isAfter(endDate))
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalExpenses(LocalDate startDate, LocalDate endDate) {
        return transactions.values().stream()
                .filter(t -> "EXPENSE".equals(t.getTransactionType()))
                .filter(t -> t.getTransactionDate() != null &&
                        !t.getTransactionDate().isBefore(startDate) &&
                        !t.getTransactionDate().isAfter(endDate))
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==================== BUDGETS ====================

    @Override
    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets.values());
    }

    @Override
    public List<Budget> getBudgetsByYear(int fiscalYear) {
        return budgets.values().stream()
                .filter(b -> b.getFiscalYear() == fiscalYear)
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> getBudgetsByCategory(String category) {
        return budgets.values().stream()
                .filter(b -> category.equals(b.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public Budget getBudgetById(int budgetId) {
        return budgets.get(budgetId);
    }

    @Override
    public Budget createBudget(Budget budget) {
        budget.setBudgetId(nextBudgetId++);
        budget.setCreatedAt(LocalDateTime.now());
        budgets.put(budget.getBudgetId(), budget);
        return budget;
    }

    @Override
    public boolean updateBudget(Budget budget) {
        if (budgets.containsKey(budget.getBudgetId())) {
            budget.setUpdatedAt(LocalDateTime.now());
            budgets.put(budget.getBudgetId(), budget);
            return true;
        }
        return false;
    }

    @Override
    public boolean approveBudget(int budgetId, int approvedBy) {
        Budget b = budgets.get(budgetId);
        if (b != null && "DRAFT".equals(b.getStatus())) {
            b.setStatus("APPROVED");
            b.setApprovedBy(approvedBy);
            b.setApprovedAt(LocalDateTime.now());
            b.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateBudgetActual(int budgetId, BigDecimal actualAmount) {
        Budget b = budgets.get(budgetId);
        if (b != null) {
            b.setActualAmount(actualAmount);
            b.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public Map<String, BigDecimal> getBudgetSummary(int fiscalYear) {
        Map<String, BigDecimal> summary = new HashMap<>();

        BigDecimal totalBudgeted = budgets.values().stream()
                .filter(b -> b.getFiscalYear() == fiscalYear)
                .map(Budget::getBudgetedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActual = budgets.values().stream()
                .filter(b -> b.getFiscalYear() == fiscalYear)
                .map(Budget::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.put("totalBudgeted", totalBudgeted);
        summary.put("totalActual", totalActual);
        summary.put("totalVariance", totalBudgeted.subtract(totalActual));

        return summary;
    }
}
