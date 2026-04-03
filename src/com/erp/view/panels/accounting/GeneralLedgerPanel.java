package com.erp.view.panels.accounting;

import com.erp.model.ChartOfAccount;
import com.erp.service.mock.MockAccountingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * GeneralLedgerPanel displays account summaries and trial balance.
 */
public class GeneralLedgerPanel extends JPanel {

    private MockAccountingService accountingService;

    private JTable trialBalanceTable;
    private DefaultTableModel tableModel;

    private JLabel totalDebitsLabel;
    private JLabel totalCreditsLabel;
    private JLabel netIncomeLabel;

    private static final String[] COLUMNS = {"Account Code", "Account Name", "Type", "Debit Balance", "Credit Balance"};

    public GeneralLedgerPanel() {
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
        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trialBalanceTable = new JTable(tableModel);
        trialBalanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(trialBalanceTable);

        // Column widths
        trialBalanceTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        trialBalanceTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        trialBalanceTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        trialBalanceTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        trialBalanceTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Custom renderers
        trialBalanceTable.getColumnModel().getColumn(2).setCellRenderer(new TypeCellRenderer());
        trialBalanceTable.getColumnModel().getColumn(3).setCellRenderer(new DebitCellRenderer());
        trialBalanceTable.getColumnModel().getColumn(4).setCellRenderer(new CreditCellRenderer());

        // Summary labels
        totalDebitsLabel = createSummaryValue("$0");
        totalCreditsLabel = createSummaryValue("$0");
        netIncomeLabel = createSummaryValue("$0");
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

        JScrollPane scrollPane = new JScrollPane(trialBalanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Trial Balance"));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Financial summary panel at bottom
        JPanel financialSummary = createFinancialSummary();

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(financialSummary, BorderLayout.SOUTH);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Debits", totalDebitsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Credits", totalCreditsLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Net Income", netIncomeLabel, Constants.PRIMARY_COLOR));

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

        JButton refreshBtn = UIHelper.createPrimaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        JButton exportBtn = UIHelper.createSecondaryButton("Export");
        exportBtn.setPreferredSize(new Dimension(90, 30));
        exportBtn.addActionListener(e -> exportTrialBalance());
        toolbar.add(exportBtn);

        return toolbar;
    }

    private JPanel createFinancialSummary() {
        JPanel panel = new JPanel(new GridLayout(2, 4, Constants.PADDING_MEDIUM, Constants.PADDING_SMALL));
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Financial Summary"),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_MEDIUM,
                          Constants.PADDING_SMALL, Constants.PADDING_MEDIUM)
        ));

        // Row 1: Assets, Liabilities, Equity
        panel.add(createFinancialItem("Total Assets:", accountingService.getTotalAssets(), Constants.SUCCESS_COLOR));
        panel.add(createFinancialItem("Total Liabilities:", accountingService.getTotalLiabilities(), Constants.DANGER_COLOR));
        panel.add(createFinancialItem("Total Equity:", accountingService.getTotalEquity(), Constants.PRIMARY_COLOR));
        panel.add(createFinancialItem("Assets - Liabilities:",
                accountingService.getTotalAssets().subtract(accountingService.getTotalLiabilities()), Constants.PRIMARY_COLOR));

        // Row 2: Revenue, Expenses, Net Income
        panel.add(createFinancialItem("Total Revenue:", accountingService.getTotalRevenue(), Constants.SUCCESS_COLOR));
        panel.add(createFinancialItem("Total Expenses:", accountingService.getTotalExpenses(), Constants.DANGER_COLOR));
        panel.add(createFinancialItem("Net Income:", accountingService.getNetIncome(),
                accountingService.getNetIncome().compareTo(BigDecimal.ZERO) >= 0 ? Constants.SUCCESS_COLOR : Constants.DANGER_COLOR));
        panel.add(new JLabel("")); // Empty cell

        return panel;
    }

    private JPanel createFinancialItem(String label, BigDecimal amount, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Constants.FONT_REGULAR);

        JLabel valueComp = new JLabel("$" + String.format("%,.2f", amount));
        valueComp.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
        valueComp.setForeground(color);
        valueComp.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(labelComp, BorderLayout.WEST);
        panel.add(valueComp, BorderLayout.EAST);

        return panel;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<ChartOfAccount> accounts = accountingService.getAllAccounts();

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (ChartOfAccount a : accounts) {
            // Skip header accounts and zero balance accounts
            if (a.isSystemAccount()) continue;
            if (a.getDebitBalance().compareTo(BigDecimal.ZERO) == 0 &&
                a.getCreditBalance().compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal debitBal = BigDecimal.ZERO;
            BigDecimal creditBal = BigDecimal.ZERO;

            // Show balance in appropriate column based on normal balance
            if ("DEBIT".equals(a.getNormalBalance())) {
                if (a.getCurrentBalance().compareTo(BigDecimal.ZERO) >= 0) {
                    debitBal = a.getCurrentBalance();
                } else {
                    creditBal = a.getCurrentBalance().abs();
                }
            } else {
                if (a.getCurrentBalance().compareTo(BigDecimal.ZERO) >= 0) {
                    creditBal = a.getCurrentBalance();
                } else {
                    debitBal = a.getCurrentBalance().abs();
                }
            }

            totalDebits = totalDebits.add(debitBal);
            totalCredits = totalCredits.add(creditBal);

            tableModel.addRow(new Object[]{
                a.getAccountCode(),
                a.getAccountName(),
                a.getAccountType(),
                debitBal,
                creditBal
            });
        }

        // Add totals row
        tableModel.addRow(new Object[]{
            "",
            "TOTALS",
            "",
            totalDebits,
            totalCredits
        });

        totalDebitsLabel.setText("$" + String.format("%,.2f", totalDebits));
        totalCreditsLabel.setText("$" + String.format("%,.2f", totalCredits));

        BigDecimal netIncome = accountingService.getNetIncome();
        netIncomeLabel.setText("$" + String.format("%,.2f", netIncome));
        netIncomeLabel.setForeground(netIncome.compareTo(BigDecimal.ZERO) >= 0 ? Constants.SUCCESS_COLOR : Constants.DANGER_COLOR);

        // Refresh financial summary
        removeAll();
        layoutComponents();
        revalidate();
        repaint();
    }

    private void exportTrialBalance() {
        UIHelper.showSuccess(this, "Trial Balance export functionality will be implemented.");
    }

    public void refreshData() {
        loadData();
    }

    // Type cell renderer
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

    // Debit cell renderer
    private static class DebitCellRenderer extends DefaultTableCellRenderer {
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
                    // Bold for totals row
                    if (row == table.getRowCount() - 1) {
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
            }
            return this;
        }
    }

    // Credit cell renderer
    private static class CreditCellRenderer extends DefaultTableCellRenderer {
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
                    // Bold for totals row
                    if (row == table.getRowCount() - 1) {
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
            }
            return this;
        }
    }
}
