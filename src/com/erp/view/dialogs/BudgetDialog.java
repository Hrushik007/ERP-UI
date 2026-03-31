package com.erp.view.dialogs;

import com.erp.model.Budget;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * BudgetDialog for creating and editing budgets.
 */
public class BudgetDialog extends JDialog {

    private JTextField budgetNameField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> periodCombo;
    private JComboBox<Integer> yearCombo;
    private JTextField budgetedAmountField;
    private JTextField actualAmountField;
    private JComboBox<String> statusCombo;
    private JTextArea notesArea;

    private boolean confirmed = false;
    private Budget budget;

    private static final String[] CATEGORIES = {"SALES", "MARKETING", "OPERATIONS", "HR", "IT", "ADMIN", "R&D", "OTHER"};
    private static final String[] PERIODS = {"MONTHLY", "QUARTERLY", "YEARLY"};
    private static final String[] STATUSES = {"DRAFT", "APPROVED", "CLOSED"};

    public BudgetDialog(Frame parent, Budget existingBudget) {
        super(parent, existingBudget == null ? "Create Budget" : "Edit Budget", true);
        this.budget = existingBudget;

        initializeComponents();
        layoutComponents();

        if (existingBudget != null) {
            populateFields(existingBudget);
        }

        setSize(500, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        budgetNameField = new JTextField(20);
        budgetNameField.setFont(Constants.FONT_REGULAR);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        periodCombo = new JComboBox<>(PERIODS);
        periodCombo.setFont(Constants.FONT_REGULAR);

        // Year combo - current year and next few years
        int currentYear = LocalDate.now().getYear();
        Integer[] years = {currentYear - 1, currentYear, currentYear + 1, currentYear + 2};
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(currentYear);
        yearCombo.setFont(Constants.FONT_REGULAR);

        budgetedAmountField = new JTextField(20);
        budgetedAmountField.setFont(Constants.FONT_REGULAR);

        actualAmountField = new JTextField(20);
        actualAmountField.setFont(Constants.FONT_REGULAR);
        actualAmountField.setText("0.00");

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

        // Budget Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Budget Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(budgetNameField, gbc);

        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Category:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

        // Period
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Period:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(periodCombo, gbc);

        // Fiscal Year
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Fiscal Year:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(yearCombo, gbc);

        // Budgeted Amount
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Budgeted Amount:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel budgetPanel = new JPanel(new BorderLayout(5, 0));
        budgetPanel.setBackground(Constants.BG_WHITE);
        budgetPanel.add(new JLabel("$"), BorderLayout.WEST);
        budgetPanel.add(budgetedAmountField, BorderLayout.CENTER);
        formPanel.add(budgetPanel, gbc);

        // Actual Amount
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Actual Amount:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel actualPanel = new JPanel(new BorderLayout(5, 0));
        actualPanel.setBackground(Constants.BG_WHITE);
        actualPanel.add(new JLabel("$"), BorderLayout.WEST);
        actualPanel.add(actualAmountField, BorderLayout.CENTER);
        formPanel.add(actualPanel, gbc);

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
        saveBtn.addActionListener(e -> saveBudget());

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

    private void populateFields(Budget b) {
        budgetNameField.setText(b.getBudgetName());
        categoryCombo.setSelectedItem(b.getCategory());
        periodCombo.setSelectedItem(b.getPeriod());
        yearCombo.setSelectedItem(b.getFiscalYear());
        budgetedAmountField.setText(b.getBudgetedAmount().toString());
        actualAmountField.setText(b.getActualAmount().toString());
        statusCombo.setSelectedItem(b.getStatus());
        notesArea.setText(b.getNotes() != null ? b.getNotes() : "");
    }

    private void saveBudget() {
        // Validation
        if (budgetNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Budget name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            budgetNameField.requestFocus();
            return;
        }

        BigDecimal budgetedAmount;
        try {
            budgetedAmount = new BigDecimal(budgetedAmountField.getText().trim());
            if (budgetedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive budgeted amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            budgetedAmountField.requestFocus();
            return;
        }

        BigDecimal actualAmount;
        try {
            actualAmount = new BigDecimal(actualAmountField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid actual amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            actualAmountField.requestFocus();
            return;
        }

        // Create or update budget
        if (budget == null) {
            budget = new Budget();
        }

        budget.setBudgetName(budgetNameField.getText().trim());
        budget.setCategory((String) categoryCombo.getSelectedItem());
        budget.setPeriod((String) periodCombo.getSelectedItem());
        budget.setFiscalYear((Integer) yearCombo.getSelectedItem());
        budget.setBudgetedAmount(budgetedAmount);
        budget.setActualAmount(actualAmount);
        budget.setStatus((String) statusCombo.getSelectedItem());
        budget.setNotes(notesArea.getText().trim());
        budget.calculateVariance();

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Budget getBudget() {
        return budget;
    }
}
