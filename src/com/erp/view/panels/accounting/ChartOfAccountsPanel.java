package com.erp.view.panels.accounting;

import com.erp.model.ChartOfAccount;
import com.erp.service.mock.MockAccountingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.ChartOfAccountDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * ChartOfAccountsPanel displays and manages the chart of accounts.
 */
public class ChartOfAccountsPanel extends JPanel {

    private MockAccountingService accountingService;

    private JTable accountsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilter;

    private JLabel totalAssetsLabel;
    private JLabel totalLiabilitiesLabel;
    private JLabel totalEquityLabel;

    private static final String[] COLUMNS = {"Code", "Account Name", "Type", "Normal Bal", "Debit", "Credit", "Balance", "Status"};

    public ChartOfAccountsPanel() {
        accountingService = MockAccountingService.getInstance();
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
        typeFilter = new JComboBox<>(new String[]{"All Types", "ASSET", "LIABILITY", "EQUITY", "REVENUE", "EXPENSE"});
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
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        accountsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        accountsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        accountsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        accountsTable.getColumnModel().getColumn(7).setPreferredWidth(70);

        // Custom renderers
        accountsTable.getColumnModel().getColumn(2).setCellRenderer(new TypeCellRenderer());
        accountsTable.getColumnModel().getColumn(4).setCellRenderer(new CurrencyCellRenderer());
        accountsTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
        accountsTable.getColumnModel().getColumn(6).setCellRenderer(new BalanceCellRenderer());
        accountsTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalAssetsLabel = createSummaryValue("$0");
        totalLiabilitiesLabel = createSummaryValue("$0");
        totalEquityLabel = createSummaryValue("$0");
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

        panel.add(createSummaryCard("Total Assets", totalAssetsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Liabilities", totalLiabilitiesLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Total Equity", totalEquityLabel, Constants.PRIMARY_COLOR));

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

        JButton deleteBtn = UIHelper.createSecondaryButton("Delete");
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        deleteBtn.addActionListener(e -> deleteAccount());
        toolbar.add(deleteBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<ChartOfAccount> accounts = accountingService.getAllAccounts();
        String typeSelection = (String) typeFilter.getSelectedItem();

        for (ChartOfAccount a : accounts) {
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(a.getAccountType())) {
                continue;
            }

            // Indent name based on level
            String displayName = getIndentedName(a.getAccountName(), a.getLevel());

            tableModel.addRow(new Object[]{
                a.getAccountCode(),
                displayName,
                a.getAccountType(),
                a.getNormalBalance(),
                a.getDebitBalance(),
                a.getCreditBalance(),
                a.getCurrentBalance(),
                a.isActive() ? "Active" : "Inactive"
            });
        }

        // Update summary
        totalAssetsLabel.setText("$" + String.format("%,.2f", accountingService.getTotalAssets()));
        totalLiabilitiesLabel.setText("$" + String.format("%,.2f", accountingService.getTotalLiabilities()));
        totalEquityLabel.setText("$" + String.format("%,.2f", accountingService.getTotalEquity()));
    }

    private String getIndentedName(String name, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < level; i++) {
            sb.append("    ");
        }
        sb.append(name);
        return sb.toString();
    }

    private void addAccount() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ChartOfAccountDialog dialog = new ChartOfAccountDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ChartOfAccount newAccount = dialog.getAccount();
            accountingService.createAccount(newAccount);
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

        String accountCode = (String) tableModel.getValueAt(row, 0);
        ChartOfAccount account = accountingService.getAccountByCode(accountCode);

        if (account != null) {
            if (account.isSystemAccount()) {
                UIHelper.showError(this, "System accounts cannot be edited.");
                return;
            }

            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ChartOfAccountDialog dialog = new ChartOfAccountDialog(parentFrame, account);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                accountingService.updateAccount(dialog.getAccount());
                UIHelper.showSuccess(this, "Account updated successfully.");
                loadData();
            }
        }
    }

    private void deleteAccount() {
        int row = accountsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an account to delete.");
            return;
        }

        String accountCode = (String) tableModel.getValueAt(row, 0);
        ChartOfAccount account = accountingService.getAccountByCode(accountCode);

        if (account != null) {
            if (account.isSystemAccount()) {
                UIHelper.showError(this, "System accounts cannot be deleted.");
                return;
            }

            boolean confirm = UIHelper.showConfirm(this, "Are you sure you want to delete account '" + account.getAccountName() + "'?");
            if (confirm) {
                if (accountingService.deleteAccount(account.getAccountId())) {
                    UIHelper.showSuccess(this, "Account deleted successfully.");
                    loadData();
                }
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Type cell renderer with colors
    private static class TypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String type = value.toString();
                switch (type) {
                    case "ASSET":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "LIABILITY":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "EQUITY":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "REVENUE":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "EXPENSE":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
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
                    setText("-");
                } else {
                    setText("$" + String.format("%,.2f", amount));
                }
            }
            return this;
        }
    }

    // Balance cell renderer
    private static class BalanceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT);
            if (value instanceof BigDecimal) {
                BigDecimal balance = (BigDecimal) value;
                setText("$" + String.format("%,.2f", balance));
                if (!isSelected) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            return this;
        }
    }

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String status = value.toString();
                if ("Active".equals(status)) {
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
}
