package com.erp.view.panels.finance;

import com.erp.model.Account;
import com.erp.service.mock.MockFinanceService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.AccountDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * AccountsPanel displays and manages financial accounts.
 */
public class AccountsPanel extends JPanel {

    private MockFinanceService financeService;

    private JTable accountsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilter;

    private JLabel totalBalanceLabel;
    private JLabel bankAccountsLabel;
    private JLabel cashAccountsLabel;

    private static final String[] COLUMNS = {"ID", "Account #", "Account Name", "Type", "Bank", "Balance", "Status"};

    public AccountsPanel() {
        financeService = MockFinanceService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "BANK", "CASH", "CREDIT_CARD", "SAVINGS", "INVESTMENT"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountsTable = new JTable(tableModel);
        accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(accountsTable);

        // Column widths
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        accountsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        accountsTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        accountsTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Status column renderer
        accountsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        // Balance column renderer
        accountsTable.getColumnModel().getColumn(5).setCellRenderer(new BalanceCellRenderer());

        // Summary labels
        totalBalanceLabel = createSummaryValue("$0");
        bankAccountsLabel = createSummaryValue("0");
        cashAccountsLabel = createSummaryValue("0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        JPanel summaryPanel = createSummaryPanel();
        JPanel toolbar = createToolbar();

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Balance", totalBalanceLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Bank Accounts", bankAccountsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Cash Accounts", cashAccountsLabel, Constants.WARNING_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_MEDIUM,
                          Constants.PADDING_SMALL, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);

        toolbar.add(new JLabel("Type:"));
        toolbar.add(typeFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton addBtn = UIHelper.createPrimaryButton("Add Account");
        addBtn.setPreferredSize(new Dimension(120, 30));
        addBtn.addActionListener(e -> addAccount());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editAccount());
        toolbar.add(editBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Account> accounts = financeService.getAllAccounts();
        String typeSelection = (String) typeFilter.getSelectedItem();

        int bankCount = 0;
        int cashCount = 0;

        for (Account a : accounts) {
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(a.getAccountType())) {
                continue;
            }

            if ("BANK".equals(a.getAccountType())) bankCount++;
            if ("CASH".equals(a.getAccountType())) cashCount++;

            tableModel.addRow(new Object[]{
                a.getAccountId(),
                a.getAccountNumber(),
                a.getAccountName(),
                a.getAccountType(),
                a.getBankName() != null ? a.getBankName() : "-",
                a.getCurrentBalance(),
                a.getStatus()
            });
        }

        BigDecimal totalBalance = financeService.getTotalBalance();
        totalBalanceLabel.setText("$" + String.format("%,.2f", totalBalance));
        bankAccountsLabel.setText(String.valueOf(bankCount));
        cashAccountsLabel.setText(String.valueOf(cashCount));
    }

    private void addAccount() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        AccountDialog dialog = new AccountDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Account newAccount = dialog.getAccount();
            financeService.createAccount(newAccount);
            UIHelper.showSuccess(this, "Account created successfully.");
            loadData();
        }
    }

    private void editAccount() {
        int row = accountsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an account to edit.");
            return;
        }

        int accountId = (int) tableModel.getValueAt(row, 0);
        Account account = financeService.getAccountById(accountId);

        if (account != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            AccountDialog dialog = new AccountDialog(parentFrame, account);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                financeService.updateAccount(dialog.getAccount());
                UIHelper.showSuccess(this, "Account updated successfully.");
                loadData();
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String status = value.toString();
                if ("ACTIVE".equals(status)) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                }
            }
            return this;
        }
    }

    private static class BalanceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT);
            if (value instanceof BigDecimal && !isSelected) {
                BigDecimal balance = (BigDecimal) value;
                setText("$" + String.format("%,.2f", balance));
                if (balance.compareTo(BigDecimal.ZERO) < 0) {
                    setForeground(Constants.DANGER_COLOR);
                } else {
                    setForeground(Constants.SUCCESS_COLOR);
                }
            }
            return this;
        }
    }
}
