package com.erp.view.dialogs;

import com.erp.model.ChartOfAccount;
import com.erp.model.JournalEntry;
import com.erp.model.JournalEntryLine;
import com.erp.service.mock.MockAccountingService;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * JournalEntryDialog for creating and editing journal entries.
 */
public class JournalEntryDialog extends JDialog {

    private JTextField entryNumberField;
    private JSpinner dateSpinner;
    private JTextField descriptionField;
    private JTextField referenceField;
    private JComboBox<String> entryTypeCombo;

    private JTable linesTable;
    private DefaultTableModel linesTableModel;

    private JComboBox<AccountItem> accountCombo;
    private JTextField lineDescField;
    private JTextField debitField;
    private JTextField creditField;

    private JLabel totalDebitLabel;
    private JLabel totalCreditLabel;
    private JLabel balanceLabel;

    private boolean confirmed = false;
    private JournalEntry journalEntry;
    private MockAccountingService accountingService;

    private static final String[] ENTRY_TYPES = {"STANDARD", "ADJUSTING", "CLOSING", "REVERSING"};
    private static final String[] LINE_COLUMNS = {"#", "Account Code", "Account Name", "Description", "Debit", "Credit"};

    public JournalEntryDialog(Frame parent, JournalEntry existingEntry) {
        super(parent, existingEntry == null ? "New Journal Entry" : "Edit Journal Entry", true);
        this.journalEntry = existingEntry != null ? existingEntry : new JournalEntry();
        this.accountingService = MockAccountingService.getInstance();

        initializeComponents();
        layoutComponents();

        if (existingEntry != null) {
            populateFields(existingEntry);
        } else {
            journalEntry.setEntryNumber(accountingService.generateEntryNumber());
            entryNumberField.setText(journalEntry.getEntryNumber());
        }

        updateTotals();

        setSize(750, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
    }

    private void initializeComponents() {
        entryNumberField = new JTextField(15);
        entryNumberField.setFont(Constants.FONT_REGULAR);
        entryNumberField.setEditable(false);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setValue(new Date());
        dateSpinner.setFont(Constants.FONT_REGULAR);

        descriptionField = new JTextField(25);
        descriptionField.setFont(Constants.FONT_REGULAR);

        referenceField = new JTextField(15);
        referenceField.setFont(Constants.FONT_REGULAR);

        entryTypeCombo = new JComboBox<>(ENTRY_TYPES);
        entryTypeCombo.setFont(Constants.FONT_REGULAR);

        // Lines table
        linesTableModel = new DefaultTableModel(LINE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        linesTable = new JTable(linesTableModel);
        linesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        linesTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        linesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        linesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        linesTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        linesTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        linesTable.getColumnModel().getColumn(5).setPreferredWidth(90);

        linesTable.getColumnModel().getColumn(4).setCellRenderer(new CurrencyCellRenderer());
        linesTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());

        // Line entry fields
        accountCombo = new JComboBox<>();
        accountCombo.setFont(Constants.FONT_REGULAR);
        loadAccounts();

        lineDescField = new JTextField(15);
        lineDescField.setFont(Constants.FONT_REGULAR);

        debitField = new JTextField(10);
        debitField.setFont(Constants.FONT_REGULAR);

        creditField = new JTextField(10);
        creditField.setFont(Constants.FONT_REGULAR);

        // Totals
        totalDebitLabel = new JLabel("$0.00");
        totalDebitLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        totalDebitLabel.setForeground(new Color(21, 87, 36));

        totalCreditLabel = new JLabel("$0.00");
        totalCreditLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        totalCreditLabel.setForeground(new Color(114, 28, 36));

        balanceLabel = new JLabel("Balanced");
        balanceLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        balanceLabel.setForeground(Constants.SUCCESS_COLOR);
    }

    private void loadAccounts() {
        List<ChartOfAccount> accounts = accountingService.getActiveAccounts();
        for (ChartOfAccount acc : accounts) {
            if (!acc.isSystemAccount()) { // Only show detail accounts
                accountCombo.addItem(new AccountItem(acc));
            }
        }
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Constants.BG_WHITE);

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Lines panel (table + add line)
        JPanel linesPanel = createLinesPanel();

        // Totals panel
        JPanel totalsPanel = createTotalsPanel();

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(linesPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.add(totalsPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Entry Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Entry #:"), gbc);
        gbc.gridx = 1;
        panel.add(entryNumberField, gbc);

        gbc.gridx = 2;
        panel.add(createLabel("Date:"), gbc);
        gbc.gridx = 3;
        panel.add(dateSpinner, gbc);

        gbc.gridx = 4;
        panel.add(createLabel("Type:"), gbc);
        gbc.gridx = 5;
        panel.add(entryTypeCombo, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(descriptionField, gbc);

        gbc.gridx = 4; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Reference:"), gbc);
        gbc.gridx = 5;
        panel.add(referenceField, gbc);

        return panel;
    }

    private JPanel createLinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Entry Lines"));

        // Add line panel
        JPanel addLinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addLinePanel.setBackground(Constants.BG_WHITE);

        addLinePanel.add(new JLabel("Account:"));
        accountCombo.setPreferredSize(new Dimension(200, 25));
        addLinePanel.add(accountCombo);

        addLinePanel.add(new JLabel("Desc:"));
        lineDescField.setPreferredSize(new Dimension(100, 25));
        addLinePanel.add(lineDescField);

        addLinePanel.add(new JLabel("Debit:"));
        debitField.setPreferredSize(new Dimension(80, 25));
        addLinePanel.add(debitField);

        addLinePanel.add(new JLabel("Credit:"));
        creditField.setPreferredSize(new Dimension(80, 25));
        addLinePanel.add(creditField);

        JButton addLineBtn = new JButton("Add");
        addLineBtn.setFont(Constants.FONT_BUTTON);
        addLineBtn.setBackground(Constants.SUCCESS_COLOR);
        addLineBtn.setForeground(Color.WHITE);
        addLineBtn.setOpaque(true);
        addLineBtn.setBorderPainted(false);
        addLineBtn.addActionListener(e -> addLine());
        addLinePanel.add(addLineBtn);

        JButton removeLineBtn = new JButton("Remove");
        removeLineBtn.setFont(Constants.FONT_BUTTON);
        removeLineBtn.addActionListener(e -> removeLine());
        addLinePanel.add(removeLineBtn);

        JScrollPane scrollPane = new JScrollPane(linesTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        panel.add(addLinePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        panel.add(new JLabel("Total Debit:"));
        panel.add(totalDebitLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Total Credit:"));
        panel.add(totalCreditLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Status:"));
        panel.add(balanceLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Constants.BG_WHITE);

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
        saveBtn.addActionListener(e -> saveEntry());

        panel.add(cancelBtn);
        panel.add(saveBtn);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        return label;
    }

    private void addLine() {
        AccountItem selectedAccount = (AccountItem) accountCombo.getSelectedItem();
        if (selectedAccount == null) {
            JOptionPane.showMessageDialog(this, "Please select an account.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal debit = BigDecimal.ZERO;
        BigDecimal credit = BigDecimal.ZERO;

        try {
            if (!debitField.getText().trim().isEmpty()) {
                debit = new BigDecimal(debitField.getText().trim());
            }
            if (!creditField.getText().trim().isEmpty()) {
                credit = new BigDecimal(creditField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid amounts.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (debit.compareTo(BigDecimal.ZERO) == 0 && credit.compareTo(BigDecimal.ZERO) == 0) {
            JOptionPane.showMessageDialog(this, "Please enter a debit or credit amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (debit.compareTo(BigDecimal.ZERO) > 0 && credit.compareTo(BigDecimal.ZERO) > 0) {
            JOptionPane.showMessageDialog(this, "A line cannot have both debit and credit.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add to journal entry
        JournalEntryLine line = new JournalEntryLine(
            selectedAccount.getAccount().getAccountCode(),
            selectedAccount.getAccount().getAccountName(),
            debit, credit
        );
        line.setDescription(lineDescField.getText().trim());
        journalEntry.addLine(line);

        // Add to table
        refreshLinesTable();

        // Clear fields
        lineDescField.setText("");
        debitField.setText("");
        creditField.setText("");

        updateTotals();
    }

    private void removeLine() {
        int row = linesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a line to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (row < journalEntry.getLines().size()) {
            journalEntry.getLines().remove(row);
            refreshLinesTable();
            updateTotals();
        }
    }

    private void refreshLinesTable() {
        linesTableModel.setRowCount(0);
        int lineNum = 1;
        for (JournalEntryLine line : journalEntry.getLines()) {
            linesTableModel.addRow(new Object[]{
                lineNum++,
                line.getAccountCode(),
                line.getAccountName(),
                line.getDescription(),
                line.getDebitAmount(),
                line.getCreditAmount()
            });
        }
    }

    private void updateTotals() {
        journalEntry.recalculateTotals();
        totalDebitLabel.setText("$" + String.format("%,.2f", journalEntry.getTotalDebit()));
        totalCreditLabel.setText("$" + String.format("%,.2f", journalEntry.getTotalCredit()));

        if (journalEntry.isBalanced()) {
            balanceLabel.setText("Balanced");
            balanceLabel.setForeground(Constants.SUCCESS_COLOR);
        } else {
            BigDecimal diff = journalEntry.getOutOfBalanceAmount();
            balanceLabel.setText("Out of Balance: $" + String.format("%,.2f", diff));
            balanceLabel.setForeground(Constants.DANGER_COLOR);
        }
    }

    private void populateFields(JournalEntry entry) {
        entryNumberField.setText(entry.getEntryNumber());
        if (entry.getEntryDate() != null) {
            dateSpinner.setValue(java.sql.Date.valueOf(entry.getEntryDate()));
        }
        descriptionField.setText(entry.getDescription());
        referenceField.setText(entry.getReference() != null ? entry.getReference() : "");
        entryTypeCombo.setSelectedItem(entry.getEntryType());

        refreshLinesTable();
    }

    private void saveEntry() {
        // Validation
        if (descriptionField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            descriptionField.requestFocus();
            return;
        }

        if (journalEntry.getLines().isEmpty()) {
            JOptionPane.showMessageDialog(this, "At least one line is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update entry from fields
        Date selectedDate = (Date) dateSpinner.getValue();
        journalEntry.setEntryDate(selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        journalEntry.setDescription(descriptionField.getText().trim());
        journalEntry.setReference(referenceField.getText().trim());
        journalEntry.setEntryType((String) entryTypeCombo.getSelectedItem());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public JournalEntry getJournalEntry() {
        return journalEntry;
    }

    // Helper class for account combo
    private static class AccountItem {
        private ChartOfAccount account;

        public AccountItem(ChartOfAccount account) {
            this.account = account;
        }

        public ChartOfAccount getAccount() {
            return account;
        }

        @Override
        public String toString() {
            return account.getAccountCode() + " - " + account.getAccountName();
        }
    }

    // Currency cell renderer
    private static class CurrencyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT);
            if (value instanceof BigDecimal) {
                BigDecimal amount = (BigDecimal) value;
                if (amount.compareTo(BigDecimal.ZERO) == 0) {
                    setText("");
                } else {
                    setText("$" + String.format("%,.2f", amount));
                }
            }
            return this;
        }
    }
}
