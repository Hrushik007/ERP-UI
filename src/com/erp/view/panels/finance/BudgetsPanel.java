package com.erp.view.panels.finance;

import com.erp.model.Budget;
import com.erp.service.mock.MockFinanceService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.BudgetDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BudgetsPanel displays and manages budget planning and tracking.
 */
public class BudgetsPanel extends JPanel {

    private MockFinanceService financeService;

    private JTable budgetsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryFilter;
    private JComboBox<Integer> yearFilter;

    private JLabel totalBudgetedLabel;
    private JLabel totalActualLabel;
    private JLabel totalVarianceLabel;

    private static final String[] COLUMNS = {"ID", "Budget Name", "Category", "Period", "Year", "Budgeted", "Actual", "Variance", "Utilization", "Status"};

    public BudgetsPanel() {
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
        // Category filter
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "SALES", "MARKETING", "OPERATIONS", "HR", "IT", "ADMIN", "OTHER"});
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addActionListener(e -> loadData());

        // Year filter
        int currentYear = LocalDate.now().getYear();
        yearFilter = new JComboBox<>(new Integer[]{currentYear, currentYear - 1, currentYear + 1});
        yearFilter.setFont(Constants.FONT_REGULAR);
        yearFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        budgetsTable = new JTable(tableModel);
        budgetsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(budgetsTable);

        // Column widths
        budgetsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        budgetsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        budgetsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        budgetsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        budgetsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        budgetsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        budgetsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        budgetsTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        budgetsTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        budgetsTable.getColumnModel().getColumn(9).setPreferredWidth(80);

        // Custom renderers
        budgetsTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
        budgetsTable.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
        budgetsTable.getColumnModel().getColumn(7).setCellRenderer(new VarianceCellRenderer());
        budgetsTable.getColumnModel().getColumn(8).setCellRenderer(new UtilizationCellRenderer());
        budgetsTable.getColumnModel().getColumn(9).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalBudgetedLabel = createSummaryValue("$0");
        totalActualLabel = createSummaryValue("$0");
        totalVarianceLabel = createSummaryValue("$0");
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

        JScrollPane scrollPane = new JScrollPane(budgetsTable);
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

        panel.add(createSummaryCard("Total Budgeted", totalBudgetedLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Total Actual", totalActualLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Total Variance", totalVarianceLabel, Constants.SUCCESS_COLOR));

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

        toolbar.add(new JLabel("Category:"));
        toolbar.add(categoryFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        toolbar.add(new JLabel("Year:"));
        toolbar.add(yearFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton createBtn = UIHelper.createPrimaryButton("Create Budget");
        createBtn.setPreferredSize(new Dimension(120, 30));
        createBtn.addActionListener(e -> createBudget());
        toolbar.add(createBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editBudget());
        toolbar.add(editBtn);

        JButton approveBtn = UIHelper.createSecondaryButton("Approve");
        approveBtn.setPreferredSize(new Dimension(90, 30));
        approveBtn.addActionListener(e -> approveBudget());
        toolbar.add(approveBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        Integer selectedYear = (Integer) yearFilter.getSelectedItem();
        String categorySelection = (String) categoryFilter.getSelectedItem();

        List<Budget> budgets = financeService.getBudgetsByYear(selectedYear);

        BigDecimal totalBudgeted = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;

        for (Budget b : budgets) {
            if (!"All Categories".equals(categorySelection) && !categorySelection.equals(b.getCategory())) {
                continue;
            }

            totalBudgeted = totalBudgeted.add(b.getBudgetedAmount());
            totalActual = totalActual.add(b.getActualAmount());

            tableModel.addRow(new Object[]{
                b.getBudgetId(),
                b.getBudgetName(),
                b.getCategory(),
                b.getPeriod(),
                b.getFiscalYear(),
                b.getBudgetedAmount(),
                b.getActualAmount(),
                b.getVariance(),
                b.getUtilizationPercentage(),
                b.getStatus()
            });
        }

        totalBudgetedLabel.setText("$" + String.format("%,.2f", totalBudgeted));
        totalActualLabel.setText("$" + String.format("%,.2f", totalActual));
        BigDecimal totalVariance = totalBudgeted.subtract(totalActual);
        totalVarianceLabel.setText("$" + String.format("%,.2f", totalVariance));
        totalVarianceLabel.setForeground(totalVariance.compareTo(BigDecimal.ZERO) >= 0 ? Constants.SUCCESS_COLOR : Constants.DANGER_COLOR);
    }

    private void createBudget() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        BudgetDialog dialog = new BudgetDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Budget newBudget = dialog.getBudget();
            financeService.createBudget(newBudget);
            UIHelper.showSuccess(this, "Budget created successfully.");
            loadData();
        }
    }

    private void editBudget() {
        int row = budgetsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a budget to edit.");
            return;
        }

        int budgetId = (int) tableModel.getValueAt(row, 0);
        Budget budget = financeService.getBudgetById(budgetId);

        if (budget != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            BudgetDialog dialog = new BudgetDialog(parentFrame, budget);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                financeService.updateBudget(dialog.getBudget());
                UIHelper.showSuccess(this, "Budget updated successfully.");
                loadData();
            }
        }
    }

    private void approveBudget() {
        int row = budgetsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a budget to approve.");
            return;
        }

        int budgetId = (int) tableModel.getValueAt(row, 0);
        String budgetName = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve budget '" + budgetName + "'?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (financeService.approveBudget(budgetId, 1)) {
                UIHelper.showSuccess(this, "Budget approved successfully.");
                loadData();
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Currency cell renderer
    private static class CurrencyCellRenderer extends DefaultTableCellRenderer {
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

    // Variance cell renderer (shows positive/negative with colors)
    private static class VarianceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.RIGHT);
            if (value instanceof BigDecimal) {
                BigDecimal variance = (BigDecimal) value;
                setText("$" + String.format("%,.2f", variance));
                if (!isSelected) {
                    if (variance.compareTo(BigDecimal.ZERO) >= 0) {
                        setForeground(new Color(21, 87, 36)); // Green for under budget
                    } else {
                        setForeground(new Color(114, 28, 36)); // Red for over budget
                    }
                }
            }
            return this;
        }
    }

    // Utilization percentage cell renderer
    private static class UtilizationCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value instanceof Double) {
                double util = (Double) value;
                setText(String.format("%.1f%%", util));
                if (!isSelected) {
                    if (util <= 75) {
                        setBackground(new Color(212, 237, 218)); // Green
                        setForeground(new Color(21, 87, 36));
                    } else if (util <= 100) {
                        setBackground(new Color(255, 243, 205)); // Yellow
                        setForeground(new Color(133, 100, 4));
                    } else {
                        setBackground(new Color(248, 215, 218)); // Red
                        setForeground(new Color(114, 28, 36));
                    }
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
                switch (status) {
                    case "APPROVED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "DRAFT":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "CLOSED":
                        setBackground(new Color(226, 232, 240));
                        setForeground(new Color(71, 85, 105));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }
}
