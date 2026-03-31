package com.erp.view.dialogs;

import com.erp.model.Account;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

/**
 * AccountDialog for creating and editing financial accounts.
 */
public class AccountDialog extends JDialog {

    private JTextField accountNumberField;
    private JTextField accountNameField;
    private JComboBox<String> accountTypeCombo;
    private JComboBox<String> currencyCombo;
    private JTextField bankNameField;
    private JTextField initialBalanceField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;

    private boolean confirmed = false;
    private Account account;

    private static final String[] ACCOUNT_TYPES = {"BANK", "CASH", "CREDIT_CARD", "SAVINGS", "INVESTMENT"};
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "INR", "JPY"};
    private static final String[] STATUSES = {"ACTIVE", "INACTIVE", "CLOSED"};

    public AccountDialog(Frame parent, Account existingAccount) {
        super(parent, existingAccount == null ? "Add Account" : "Edit Account", true);
        this.account = existingAccount;

        initializeComponents();
        layoutComponents();
        setupActions();

        if (existingAccount != null) {
            populateFields(existingAccount);
        }

        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        accountNumberField = new JTextField(20);
        accountNumberField.setFont(Constants.FONT_REGULAR);

        accountNameField = new JTextField(20);
        accountNameField.setFont(Constants.FONT_REGULAR);

        accountTypeCombo = new JComboBox<>(ACCOUNT_TYPES);
        accountTypeCombo.setFont(Constants.FONT_REGULAR);

        currencyCombo = new JComboBox<>(CURRENCIES);
        currencyCombo.setFont(Constants.FONT_REGULAR);

        bankNameField = new JTextField(20);
        bankNameField.setFont(Constants.FONT_REGULAR);

        initialBalanceField = new JTextField(20);
        initialBalanceField.setFont(Constants.FONT_REGULAR);
        initialBalanceField.setText("0.00");

        statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(Constants.FONT_REGULAR);

        notesArea = new JTextArea(3, 20);
        notesArea.setFont(Constants.FONT_REGULAR);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
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

        // Account Number
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Account Number:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountNumberField, gbc);

        // Account Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Account Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountNameField, gbc);

        // Account Type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Account Type:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountTypeCombo, gbc);

        // Currency
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Currency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(currencyCombo, gbc);

        // Bank Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Bank Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(bankNameField, gbc);

        // Initial Balance
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Initial Balance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(initialBalanceField, gbc);

        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusCombo, gbc);

        // Notes
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(200, 60));
        formPanel.add(notesScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Constants.BG_WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(Constants.FONT_BUTTON);
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(Constants.FONT_BUTTON);
        saveBtn.setBackground(Constants.PRIMARY_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(100, 35));
        saveBtn.addActionListener(e -> saveAccount());

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

    private void setupActions() {
        // Auto-generate account number based on type
        accountTypeCombo.addActionListener(e -> {
            if (account == null) { // Only for new accounts
                String type = (String) accountTypeCombo.getSelectedItem();
                String prefix = type.substring(0, 3).toUpperCase();
                accountNumberField.setText(prefix + "-" + System.currentTimeMillis() % 100000);
            }
        });
    }

    private void populateFields(Account acc) {
        accountNumberField.setText(acc.getAccountNumber());
        accountNameField.setText(acc.getAccountName());
        accountTypeCombo.setSelectedItem(acc.getAccountType());
        currencyCombo.setSelectedItem(acc.getCurrency());
        bankNameField.setText(acc.getBankName() != null ? acc.getBankName() : "");
        initialBalanceField.setText(acc.getCurrentBalance().toString());
        statusCombo.setSelectedItem(acc.getStatus());
        notesArea.setText(acc.getDescription() != null ? acc.getDescription() : "");

        // Disable changing balance for existing accounts
        initialBalanceField.setEditable(false);
        initialBalanceField.setToolTipText("Use transactions to change balance");
    }

    private void saveAccount() {
        // Validation
        if (accountNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            accountNameField.requestFocus();
            return;
        }

        BigDecimal balance;
        try {
            balance = new BigDecimal(initialBalanceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid balance amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            initialBalanceField.requestFocus();
            return;
        }

        // Create or update account
        if (account == null) {
            account = new Account();
        }

        account.setAccountNumber(accountNumberField.getText().trim());
        account.setAccountName(accountNameField.getText().trim());
        account.setAccountType((String) accountTypeCombo.getSelectedItem());
        account.setCurrency((String) currencyCombo.getSelectedItem());
        account.setBankName(bankNameField.getText().trim());
        account.setCurrentBalance(balance);
        account.setAvailableBalance(balance);
        account.setStatus((String) statusCombo.getSelectedItem());
        account.setDescription(notesArea.getText().trim());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Account getAccount() {
        return account;
    }
}
