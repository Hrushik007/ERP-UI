package com.erp.view.dialogs;

import com.erp.model.ChartOfAccount;
import com.erp.service.mock.MockAccountingService;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * ChartOfAccountDialog for creating and editing chart of accounts entries.
 */
public class ChartOfAccountDialog extends JDialog {

    private JTextField accountCodeField;
    private JTextField accountNameField;
    private JComboBox<String> accountTypeCombo;
    private JComboBox<String> accountSubTypeCombo;
    private JComboBox<ParentAccountItem> parentAccountCombo;
    private JTextArea descriptionArea;
    private JCheckBox activeCheckbox;

    private boolean confirmed = false;
    private ChartOfAccount account;
    private MockAccountingService accountingService;

    private static final String[] ACCOUNT_TYPES = {"ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE"};
    private static final String[] ASSET_SUBTYPES = {"CURRENT_ASSET", "FIXED_ASSET", "OTHER_ASSET"};
    private static final String[] LIABILITY_SUBTYPES = {"CURRENT_LIABILITY", "LONG_TERM_LIABILITY"};
    private static final String[] EQUITY_SUBTYPES = {"CAPITAL", "RETAINED_EARNINGS", "DRAWING"};
    private static final String[] REVENUE_SUBTYPES = {"OPERATING_REVENUE", "OTHER_REVENUE"};
    private static final String[] EXPENSE_SUBTYPES = {"OPERATING_EXPENSE", "COST_OF_SALES", "OTHER_EXPENSE"};

    public ChartOfAccountDialog(Frame parent, ChartOfAccount existingAccount) {
        super(parent, existingAccount == null ? "Add Account" : "Edit Account", true);
        this.account = existingAccount;
        this.accountingService = MockAccountingService.getInstance();

        initializeComponents();
        layoutComponents();
        setupActions();

        if (existingAccount != null) {
            populateFields(existingAccount);
        }

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        accountCodeField = new JTextField(20);
        accountCodeField.setFont(Constants.FONT_REGULAR);

        accountNameField = new JTextField(20);
        accountNameField.setFont(Constants.FONT_REGULAR);

        accountTypeCombo = new JComboBox<>(ACCOUNT_TYPES);
        accountTypeCombo.setFont(Constants.FONT_REGULAR);

        accountSubTypeCombo = new JComboBox<>(ASSET_SUBTYPES);
        accountSubTypeCombo.setFont(Constants.FONT_REGULAR);

        // Parent account combo
        parentAccountCombo = new JComboBox<>();
        parentAccountCombo.setFont(Constants.FONT_REGULAR);
        loadParentAccounts(null);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        activeCheckbox = new JCheckBox("Active", true);
        activeCheckbox.setFont(Constants.FONT_REGULAR);
        activeCheckbox.setBackground(Constants.BG_WHITE);
    }

    private void loadParentAccounts(String accountType) {
        parentAccountCombo.removeAllItems();
        parentAccountCombo.addItem(new ParentAccountItem(null)); // No parent option

        List<ChartOfAccount> accounts = accountingService.getAllAccounts();
        for (ChartOfAccount acc : accounts) {
            if (acc.isSystemAccount() || (accountType != null && accountType.equals(acc.getAccountType()))) {
                // Show header accounts or accounts matching the type
                if (acc.isSystemAccount()) {
                    parentAccountCombo.addItem(new ParentAccountItem(acc));
                }
            }
        }
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

        // Account Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Account Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountCodeField, gbc);

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

        // Account Sub-Type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Sub-Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(accountSubTypeCombo, gbc);

        // Parent Account
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Parent Account:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(parentAccountCombo, gbc);

        // Active
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(activeCheckbox, gbc);

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
        label.setPreferredSize(new Dimension(140, 25));
        label.setMinimumSize(new Dimension(140, 25));
        return label;
    }

    private void setupActions() {
        // Update subtypes when type changes
        accountTypeCombo.addActionListener(e -> {
            String type = (String) accountTypeCombo.getSelectedItem();
            updateSubTypes(type);
            loadParentAccounts(type);
        });
    }

    private void updateSubTypes(String type) {
        accountSubTypeCombo.removeAllItems();
        String[] subTypes;
        switch (type) {
            case "ASSET": subTypes = ASSET_SUBTYPES; break;
            case "LIABILITY": subTypes = LIABILITY_SUBTYPES; break;
            case "EQUITY": subTypes = EQUITY_SUBTYPES; break;
            case "REVENUE": subTypes = REVENUE_SUBTYPES; break;
            case "EXPENSE": subTypes = EXPENSE_SUBTYPES; break;
            default: subTypes = ASSET_SUBTYPES;
        }
        for (String subType : subTypes) {
            accountSubTypeCombo.addItem(subType);
        }
    }

    private void populateFields(ChartOfAccount acc) {
        accountCodeField.setText(acc.getAccountCode());
        accountCodeField.setEditable(false); // Cannot change code for existing account
        accountNameField.setText(acc.getAccountName());
        accountTypeCombo.setSelectedItem(acc.getAccountType());
        updateSubTypes(acc.getAccountType());
        if (acc.getAccountSubType() != null) {
            accountSubTypeCombo.setSelectedItem(acc.getAccountSubType());
        }
        descriptionArea.setText(acc.getDescription() != null ? acc.getDescription() : "");
        activeCheckbox.setSelected(acc.isActive());

        // Set parent account
        if (acc.getParentAccountCode() != null) {
            for (int i = 0; i < parentAccountCombo.getItemCount(); i++) {
                ParentAccountItem item = parentAccountCombo.getItemAt(i);
                if (item.getAccount() != null &&
                    acc.getParentAccountCode().equals(item.getAccount().getAccountCode())) {
                    parentAccountCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void saveAccount() {
        // Validation
        if (accountCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            accountCodeField.requestFocus();
            return;
        }

        if (accountNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            accountNameField.requestFocus();
            return;
        }

        // Check for duplicate code (only for new accounts)
        if (account == null && accountingService.accountCodeExists(accountCodeField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Account code already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            accountCodeField.requestFocus();
            return;
        }

        // Create or update account
        if (account == null) {
            account = new ChartOfAccount();
        }

        account.setAccountCode(accountCodeField.getText().trim());
        account.setAccountName(accountNameField.getText().trim());
        account.setAccountType((String) accountTypeCombo.getSelectedItem());
        account.setAccountSubType((String) accountSubTypeCombo.getSelectedItem());
        account.setDescription(descriptionArea.getText().trim());
        account.setActive(activeCheckbox.isSelected());

        ParentAccountItem parentItem = (ParentAccountItem) parentAccountCombo.getSelectedItem();
        if (parentItem != null && parentItem.getAccount() != null) {
            account.setParentAccountCode(parentItem.getAccount().getAccountCode());
            account.setLevel(parentItem.getAccount().getLevel() + 1);
        } else {
            account.setParentAccountCode(null);
            account.setLevel(1);
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ChartOfAccount getAccount() {
        return account;
    }

    // Helper class for parent account combo
    private static class ParentAccountItem {
        private ChartOfAccount account;

        public ParentAccountItem(ChartOfAccount account) {
            this.account = account;
        }

        public ChartOfAccount getAccount() {
            return account;
        }

        @Override
        public String toString() {
            if (account == null) {
                return "(No Parent)";
            }
            return account.getAccountCode() + " - " + account.getAccountName();
        }
    }
}
