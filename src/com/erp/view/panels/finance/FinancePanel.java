package com.erp.view.panels.finance;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * FinancePanel - Main container for Finance module with tabbed navigation.
 *
 * Contains:
 * - Accounts Tab: Bank and cash account management
 * - Transactions Tab: Income, expenses, and transfers
 * - Budgets Tab: Budget planning and tracking
 */
public class FinancePanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private AccountsPanel accountsPanel;
    private TransactionsPanel transactionsPanel;
    private BudgetsPanel budgetsPanel;

    public FinancePanel() {
        super(Constants.MODULE_FINANCE);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        accountsPanel = new AccountsPanel();
        transactionsPanel = new TransactionsPanel();
        budgetsPanel = new BudgetsPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Accounts", accountsPanel);
        tabbedPane.addTab("Transactions", transactionsPanel);
        tabbedPane.addTab("Budgets", budgetsPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: accountsPanel.refreshData(); break;
                case 1: transactionsPanel.refreshData(); break;
                case 2: budgetsPanel.refreshData(); break;
            }
        });
    }

    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        int index = tabbedPane.getSelectedIndex();
        switch (index) {
            case 0: accountsPanel.refreshData(); break;
            case 1: transactionsPanel.refreshData(); break;
            case 2: budgetsPanel.refreshData(); break;
        }
    }
}
