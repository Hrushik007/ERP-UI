package com.erp.view.panels.finance;

import com.erp.model.FinancialTransaction;
import com.erp.service.mock.MockFinanceService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.TransactionDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TransactionsPanel displays and manages financial transactions.
 */
public class TransactionsPanel extends JPanel {

    private MockFinanceService financeService;

    private JTable transactionsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilter;

    private JLabel totalIncomeLabel;
    private JLabel totalExpensesLabel;
    private JLabel netCashFlowLabel;

    private static final String[] COLUMNS = {"TXN #", "Date", "Type", "Category", "Account", "Amount", "Description", "Status"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TransactionsPanel() {
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
        typeFilter = new JComboBox<>(new String[]{"All Types", "INCOME", "EXPENSE", "TRANSFER", "REFUND"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionsTable = new JTable(tableModel);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(transactionsTable);

        // Column widths
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        transactionsTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        // Type column renderer
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new TypeCellRenderer());
        // Amount column renderer
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());

        // Summary labels
        totalIncomeLabel = createSummaryValue("$0");
        totalExpensesLabel = createSummaryValue("$0");
        netCashFlowLabel = createSummaryValue("$0");
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

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
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

        panel.add(createSummaryCard("Total Income", totalIncomeLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Expenses", totalExpensesLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Net Cash Flow", netCashFlowLabel, Constants.PRIMARY_COLOR));

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

        JButton incomeBtn = new JButton("Record Income");
        incomeBtn.setFont(Constants.FONT_BUTTON);
        incomeBtn.setBackground(Constants.SUCCESS_COLOR);
        incomeBtn.setForeground(Color.WHITE);
        incomeBtn.setOpaque(true);
        incomeBtn.setBorderPainted(false);
        incomeBtn.setFocusPainted(false);
        incomeBtn.setPreferredSize(new Dimension(130, 30));
        incomeBtn.addActionListener(e -> recordIncome());
        toolbar.add(incomeBtn);

        JButton expenseBtn = new JButton("Record Expense");
        expenseBtn.setFont(Constants.FONT_BUTTON);
        expenseBtn.setBackground(Constants.DANGER_COLOR);
        expenseBtn.setForeground(Color.WHITE);
        expenseBtn.setOpaque(true);
        expenseBtn.setBorderPainted(false);
        expenseBtn.setFocusPainted(false);
        expenseBtn.setPreferredSize(new Dimension(130, 30));
        expenseBtn.addActionListener(e -> recordExpense());
        toolbar.add(expenseBtn);

        JButton transferBtn = UIHelper.createSecondaryButton("Transfer");
        transferBtn.setPreferredSize(new Dimension(90, 30));
        transferBtn.addActionListener(e -> recordTransfer());
        toolbar.add(transferBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<FinancialTransaction> transactions = financeService.getAllTransactions();
        String typeSelection = (String) typeFilter.getSelectedItem();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (FinancialTransaction t : transactions) {
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(t.getTransactionType())) {
                continue;
            }

            if ("INCOME".equals(t.getTransactionType())) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if ("EXPENSE".equals(t.getTransactionType())) {
                totalExpenses = totalExpenses.add(t.getAmount());
            }

            tableModel.addRow(new Object[]{
                t.getTransactionNumber(),
                t.getTransactionDate() != null ? t.getTransactionDate().format(DATE_FORMAT) : "",
                t.getTransactionType(),
                t.getCategory(),
                t.getAccountName(),
                t.getAmount(),
                t.getDescription(),
                t.getStatus()
            });
        }

        totalIncomeLabel.setText("$" + String.format("%,.2f", totalIncome));
        totalExpensesLabel.setText("$" + String.format("%,.2f", totalExpenses));
        BigDecimal netCashFlow = totalIncome.subtract(totalExpenses);
        netCashFlowLabel.setText("$" + String.format("%,.2f", netCashFlow));
        netCashFlowLabel.setForeground(netCashFlow.compareTo(BigDecimal.ZERO) >= 0 ? Constants.SUCCESS_COLOR : Constants.DANGER_COLOR);
    }

    private void recordIncome() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TransactionDialog dialog = new TransactionDialog(parentFrame, "INCOME");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            financeService.createTransaction(dialog.getTransaction());
            UIHelper.showSuccess(this, "Income recorded successfully.");
            loadData();
        }
    }

    private void recordExpense() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TransactionDialog dialog = new TransactionDialog(parentFrame, "EXPENSE");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            financeService.createTransaction(dialog.getTransaction());
            UIHelper.showSuccess(this, "Expense recorded successfully.");
            loadData();
        }
    }

    private void recordTransfer() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TransactionDialog dialog = new TransactionDialog(parentFrame, "TRANSFER");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            // Create both withdrawal and deposit transactions
            financeService.createTransaction(dialog.getTransaction());
            if (dialog.getTransferTransaction() != null) {
                financeService.createTransaction(dialog.getTransferTransaction());
            }
            UIHelper.showSuccess(this, "Transfer completed successfully.");
            loadData();
        }
    }

    public void refreshData() {
        loadData();
    }

    private static class TypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String type = value.toString();
                switch (type) {
                    case "INCOME":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "EXPENSE":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "TRANSFER":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    private static class AmountCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT);
            if (value instanceof BigDecimal) {
                setText("$" + String.format("%,.2f", (BigDecimal) value));
            }
            return this;
        }
    }
}
