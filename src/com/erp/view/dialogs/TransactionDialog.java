package com.erp.view.dialogs;

import com.erp.model.Account;
import com.erp.model.FinancialTransaction;
import com.erp.service.mock.MockFinanceService;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * TransactionDialog for recording income, expenses, and transfers.
 */
public class TransactionDialog extends JDialog {

    private String transactionType; // INCOME, EXPENSE, TRANSFER

    private JComboBox<AccountItem> accountCombo;
    private JComboBox<AccountItem> toAccountCombo; // For transfers only
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JSpinner dateSpinner;
    private JComboBox<String> paymentMethodCombo;
    private JTextField referenceField;
    private JTextArea descriptionArea;

    private JLabel toAccountLabel;

    private boolean confirmed = false;
    private FinancialTransaction transaction;
    private FinancialTransaction transferTransaction; // For transfer to-account

    private MockFinanceService financeService;

    private static final String[] INCOME_CATEGORIES = {"SALES", "SERVICE", "INTEREST", "REFUND", "OTHER_INCOME"};
    private static final String[] EXPENSE_CATEGORIES = {"PURCHASE", "SALARY", "UTILITIES", "RENT", "SUPPLIES", "TRAVEL", "MARKETING", "OTHER_EXPENSE"};
    private static final String[] PAYMENT_METHODS = {"CASH", "CHECK", "BANK_TRANSFER", "CREDIT_CARD", "DEBIT_CARD"};

    public TransactionDialog(Frame parent, String type) {
        super(parent, getTitle(type), true);
        this.transactionType = type;
        this.financeService = MockFinanceService.getInstance();

        initializeComponents();
        layoutComponents();

        setSize(500, type.equals("TRANSFER") ? 480 : 520);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private static String getTitle(String type) {
        switch (type) {
            case "INCOME": return "Record Income";
            case "EXPENSE": return "Record Expense";
            case "TRANSFER": return "Transfer Between Accounts";
            default: return "Record Transaction";
        }
    }

    private void initializeComponents() {
        // Load accounts
        List<Account> accounts = financeService.getActiveAccounts();

        // Account combo
        accountCombo = new JComboBox<>();
        accountCombo.setFont(Constants.FONT_REGULAR);
        for (Account acc : accounts) {
            accountCombo.addItem(new AccountItem(acc));
        }

        // To Account combo (for transfers)
        toAccountCombo = new JComboBox<>();
        toAccountCombo.setFont(Constants.FONT_REGULAR);
        for (Account acc : accounts) {
            toAccountCombo.addItem(new AccountItem(acc));
        }

        // Category combo
        String[] categories = transactionType.equals("INCOME") ? INCOME_CATEGORIES : EXPENSE_CATEGORIES;
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        // Amount
        amountField = new JTextField(15);
        amountField.setFont(Constants.FONT_REGULAR);

        // Date
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(new Date());
        dateSpinner.setFont(Constants.FONT_REGULAR);

        // Payment Method
        paymentMethodCombo = new JComboBox<>(PAYMENT_METHODS);
        paymentMethodCombo.setFont(Constants.FONT_REGULAR);

        // Reference
        referenceField = new JTextField(15);
        referenceField.setFont(Constants.FONT_REGULAR);

        // Description
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Constants.BG_WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Constants.BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Account (From Account for transfers)
        gbc.gridx = 0; gbc.gridy = row;
        String accountLabel = transactionType.equals("TRANSFER") ? "From Account:*" : "Account:*";
        formPanel.add(createLabel(accountLabel), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountCombo, gbc);

        // To Account (for transfers only)
        if (transactionType.equals("TRANSFER")) {
            row++;
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            toAccountLabel = createLabel("To Account:*");
            formPanel.add(toAccountLabel, gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(toAccountCombo, gbc);
        }

        // Category (not for transfers)
        if (!transactionType.equals("TRANSFER")) {
            row++;
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(createLabel("Category:*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            formPanel.add(categoryCombo, gbc);
        }

        // Amount
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Amount:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel amountPanel = new JPanel(new BorderLayout(5, 0));
        amountPanel.setBackground(Constants.BG_WHITE);
        amountPanel.add(new JLabel("$"), BorderLayout.WEST);
        amountPanel.add(amountField, BorderLayout.CENTER);
        formPanel.add(amountPanel, gbc);

        // Date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Date:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dateSpinner, gbc);

        // Payment Method
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Payment Method:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(paymentMethodCombo, gbc);

        // Reference
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Reference #:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(referenceField, gbc);

        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(200, 60));
        formPanel.add(descScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Constants.BG_WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(Constants.FONT_BUTTON);
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(Constants.FONT_BUTTON);
        saveBtn.setPreferredSize(new Dimension(100, 35));

        // Color based on transaction type
        switch (transactionType) {
            case "INCOME":
                saveBtn.setBackground(Constants.SUCCESS_COLOR);
                break;
            case "EXPENSE":
                saveBtn.setBackground(Constants.DANGER_COLOR);
                break;
            default:
                saveBtn.setBackground(Constants.PRIMARY_COLOR);
        }
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveTransaction());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        label.setPreferredSize(new Dimension(140, 25)); // Ensure labels have enough width
        label.setMinimumSize(new Dimension(140, 25));
        return label;
    }

    private void saveTransaction() {
        // Validation
        if (accountCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an account.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            amountField.requestFocus();
            return;
        }

        AccountItem selectedAccount = (AccountItem) accountCombo.getSelectedItem();
        Date selectedDate = (Date) dateSpinner.getValue();
        LocalDate txnDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (transactionType.equals("TRANSFER")) {
            AccountItem toAccount = (AccountItem) toAccountCombo.getSelectedItem();
            if (toAccount == null || toAccount.getAccount().getAccountId() == selectedAccount.getAccount().getAccountId()) {
                JOptionPane.showMessageDialog(this, "Please select a different destination account.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create withdrawal transaction
            transaction = new FinancialTransaction();
            transaction.setTransactionType("TRANSFER");
            transaction.setAccountId(selectedAccount.getAccount().getAccountId());
            transaction.setAccountName(selectedAccount.getAccount().getAccountName());
            transaction.setCategory("TRANSFER_OUT");
            transaction.setAmount(amount);
            transaction.setTransactionDate(txnDate);
            transaction.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
            transaction.setReference(referenceField.getText().trim());
            transaction.setDescription("Transfer to " + toAccount.getAccount().getAccountName() +
                    (descriptionArea.getText().isEmpty() ? "" : ": " + descriptionArea.getText().trim()));
            transaction.setStatus("COMPLETED");

            // Create deposit transaction
            transferTransaction = new FinancialTransaction();
            transferTransaction.setTransactionType("TRANSFER");
            transferTransaction.setAccountId(toAccount.getAccount().getAccountId());
            transferTransaction.setAccountName(toAccount.getAccount().getAccountName());
            transferTransaction.setCategory("TRANSFER_IN");
            transferTransaction.setAmount(amount);
            transferTransaction.setTransactionDate(txnDate);
            transferTransaction.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
            transferTransaction.setReference(referenceField.getText().trim());
            transferTransaction.setDescription("Transfer from " + selectedAccount.getAccount().getAccountName() +
                    (descriptionArea.getText().isEmpty() ? "" : ": " + descriptionArea.getText().trim()));
            transferTransaction.setStatus("COMPLETED");

        } else {
            // Create single transaction
            transaction = new FinancialTransaction();
            transaction.setTransactionType(transactionType);
            transaction.setAccountId(selectedAccount.getAccount().getAccountId());
            transaction.setAccountName(selectedAccount.getAccount().getAccountName());
            transaction.setCategory((String) categoryCombo.getSelectedItem());
            transaction.setAmount(amount);
            transaction.setTransactionDate(txnDate);
            transaction.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
            transaction.setReference(referenceField.getText().trim());
            transaction.setDescription(descriptionArea.getText().trim());
            transaction.setStatus("COMPLETED");
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public FinancialTransaction getTransaction() {
        return transaction;
    }

    public FinancialTransaction getTransferTransaction() {
        return transferTransaction;
    }

    // Helper class to display accounts in combo box
    private static class AccountItem {
        private Account account;

        public AccountItem(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        @Override
        public String toString() {
            return account.getAccountName() + " (" + account.getAccountType() + ") - $" +
                   String.format("%,.2f", account.getCurrentBalance());
        }
    }
}
