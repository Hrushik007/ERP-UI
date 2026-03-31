package com.erp.service.interfaces;

import com.erp.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * FinanceService Interface - CONTRACT for Finance Module Backend Team
 *
 * Covers: Accounts Receivable, Accounts Payable, Cash Management, Budgeting
 */
public interface FinanceService {

    // ==================== ACCOUNTS RECEIVABLE (INVOICING) ====================

    /**
     * Get all invoices.
     * @return List of all invoices
     */
    List<Invoice> getAllInvoices();

    /**
     * Get invoices by status.
     * @param status DRAFT, SENT, PAID, PARTIAL, OVERDUE, CANCELLED, VOID
     * @return List of invoices
     */
    List<Invoice> getInvoicesByStatus(String status);

    /**
     * Get invoices for a customer.
     * @param customerId The customer ID
     * @return List of invoices
     */
    List<Invoice> getInvoicesByCustomer(int customerId);

    /**
     * Get overdue invoices.
     * @return List of overdue invoices
     */
    List<Invoice> getOverdueInvoices();

    /**
     * Get an invoice by ID.
     * @param invoiceId The invoice ID
     * @return Invoice object or null
     */
    Invoice getInvoiceById(int invoiceId);

    /**
     * Get invoice by number.
     * @param invoiceNumber The invoice number
     * @return Invoice object or null
     */
    Invoice getInvoiceByNumber(String invoiceNumber);

    /**
     * Create an invoice from an order.
     * @param orderId The order ID
     * @return Created invoice
     */
    Invoice createInvoiceFromOrder(int orderId);

    /**
     * Create a custom invoice.
     * @param invoice The invoice data
     * @return Created invoice with ID
     */
    Invoice createInvoice(Invoice invoice);

    /**
     * Update an invoice.
     * @param invoice The invoice to update
     * @return true if successful
     */
    boolean updateInvoice(Invoice invoice);

    /**
     * Send an invoice to customer.
     * @param invoiceId The invoice ID
     * @return true if sent successfully
     */
    boolean sendInvoice(int invoiceId);

    /**
     * Void an invoice.
     * @param invoiceId The invoice ID
     * @param reason Reason for voiding
     * @return true if successful
     */
    boolean voidInvoice(int invoiceId, String reason);


    // ==================== PAYMENTS ====================

    /**
     * Record a payment received from customer.
     * @param payment The payment data
     * @return Created payment record
     */
    Payment recordPayment(Payment payment);

    /**
     * Get all payments for an invoice.
     * @param invoiceId The invoice ID
     * @return List of payments
     */
    List<Payment> getPaymentsByInvoice(int invoiceId);

    /**
     * Get all payments for a customer.
     * @param customerId The customer ID
     * @return List of payments
     */
    List<Payment> getPaymentsByCustomer(int customerId);

    /**
     * Get payments by date range.
     * @param startDate Start date
     * @param endDate End date
     * @return List of payments
     */
    List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get a payment by ID.
     * @param paymentId The payment ID
     * @return Payment object or null
     */
    Payment getPaymentById(int paymentId);

    /**
     * Refund a payment.
     * @param paymentId The original payment ID
     * @param amount Amount to refund
     * @param reason Refund reason
     * @return The refund payment record
     */
    Payment refundPayment(int paymentId, BigDecimal amount, String reason);


    // ==================== FINANCIAL ANALYTICS ====================

    /**
     * Get total accounts receivable (outstanding invoices).
     * @return Total AR amount
     */
    BigDecimal getTotalAccountsReceivable();

    /**
     * Get aging report (AR by age brackets).
     * @return Map of age bracket to amount (e.g., "0-30 days" -> $10000)
     */
    Map<String, BigDecimal> getAgingReport();

    /**
     * Get revenue for a period.
     * @param startDate Start date
     * @param endDate End date
     * @return Total revenue
     */
    BigDecimal getRevenue(LocalDate startDate, LocalDate endDate);

    /**
     * Get cash collected for a period.
     * @param startDate Start date
     * @param endDate End date
     * @return Total cash collected
     */
    BigDecimal getCashCollected(LocalDate startDate, LocalDate endDate);

    /**
     * Get top customers by revenue.
     * @param limit Number of customers
     * @param startDate Start date
     * @param endDate End date
     * @return Map of customer ID to revenue
     */
    Map<Integer, BigDecimal> getTopCustomersByRevenue(int limit, LocalDate startDate, LocalDate endDate);


    // ==================== ACCOUNTS MANAGEMENT ====================

    /**
     * Get all accounts.
     * @return List of all accounts
     */
    List<Account> getAllAccounts();

    /**
     * Get active accounts.
     * @return List of active accounts
     */
    List<Account> getActiveAccounts();

    /**
     * Get accounts by type.
     * @param accountType BANK, CASH, CREDIT_CARD, SAVINGS, INVESTMENT
     * @return List of accounts
     */
    List<Account> getAccountsByType(String accountType);

    /**
     * Get an account by ID.
     * @param accountId The account ID
     * @return Account object or null
     */
    Account getAccountById(int accountId);

    /**
     * Create a new account.
     * @param account The account data
     * @return Created account with ID
     */
    Account createAccount(Account account);

    /**
     * Update an account.
     * @param account The account to update
     * @return true if successful
     */
    boolean updateAccount(Account account);

    /**
     * Get total balance across all accounts.
     * @return Total balance
     */
    BigDecimal getTotalBalance();


    // ==================== TRANSACTIONS ====================

    /**
     * Get all transactions.
     * @return List of all transactions
     */
    List<FinancialTransaction> getAllTransactions();

    /**
     * Get transactions by type.
     * @param transactionType INCOME, EXPENSE, TRANSFER, REFUND
     * @return List of transactions
     */
    List<FinancialTransaction> getTransactionsByType(String transactionType);

    /**
     * Get transactions by account.
     * @param accountId The account ID
     * @return List of transactions
     */
    List<FinancialTransaction> getTransactionsByAccount(int accountId);

    /**
     * Get transactions by date range.
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     */
    List<FinancialTransaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Get a transaction by ID.
     * @param transactionId The transaction ID
     * @return Transaction object or null
     */
    FinancialTransaction getTransactionById(int transactionId);

    /**
     * Create a new transaction.
     * @param transaction The transaction data
     * @return Created transaction with ID
     */
    FinancialTransaction createTransaction(FinancialTransaction transaction);

    /**
     * Get total income for a period.
     * @param startDate Start date
     * @param endDate End date
     * @return Total income
     */
    BigDecimal getTotalIncome(LocalDate startDate, LocalDate endDate);

    /**
     * Get total expenses for a period.
     * @param startDate Start date
     * @param endDate End date
     * @return Total expenses
     */
    BigDecimal getTotalExpenses(LocalDate startDate, LocalDate endDate);


    // ==================== BUDGETS ====================

    /**
     * Get all budgets.
     * @return List of all budgets
     */
    List<Budget> getAllBudgets();

    /**
     * Get budgets by fiscal year.
     * @param fiscalYear The fiscal year
     * @return List of budgets
     */
    List<Budget> getBudgetsByYear(int fiscalYear);

    /**
     * Get budgets by category.
     * @param category The budget category
     * @return List of budgets
     */
    List<Budget> getBudgetsByCategory(String category);

    /**
     * Get a budget by ID.
     * @param budgetId The budget ID
     * @return Budget object or null
     */
    Budget getBudgetById(int budgetId);

    /**
     * Create a new budget.
     * @param budget The budget data
     * @return Created budget with ID
     */
    Budget createBudget(Budget budget);

    /**
     * Update a budget.
     * @param budget The budget to update
     * @return true if successful
     */
    boolean updateBudget(Budget budget);

    /**
     * Approve a budget.
     * @param budgetId The budget ID
     * @param approvedBy The approver's employee ID
     * @return true if successful
     */
    boolean approveBudget(int budgetId, int approvedBy);

    /**
     * Update actual amount for a budget.
     * @param budgetId The budget ID
     * @param actualAmount The actual amount
     * @return true if successful
     */
    boolean updateBudgetActual(int budgetId, BigDecimal actualAmount);

    /**
     * Get budget summary (total budgeted vs actual).
     * @param fiscalYear The fiscal year
     * @return Map with keys: totalBudgeted, totalActual, totalVariance
     */
    Map<String, BigDecimal> getBudgetSummary(int fiscalYear);
}
