package com.erp.view.panels.accounting;

import com.erp.util.Constants;
import com.erp.view.panels.BasePanel;

import javax.swing.*;
import java.awt.*;

/**
 * AccountingPanel - Main container for Accounting module with tabbed navigation.
 *
 * Contains:
 * - Chart of Accounts Tab: Account structure and hierarchy
 * - Journal Entries Tab: Double-entry transactions
 * - General Ledger Tab: Account summaries and trial balance
 */
public class AccountingPanel extends BasePanel {

    private JTabbedPane tabbedPane;
    private ChartOfAccountsPanel chartOfAccountsPanel;
    private JournalEntriesPanel journalEntriesPanel;
    private GeneralLedgerPanel generalLedgerPanel;

    public AccountingPanel() {
        super(Constants.MODULE_ACCOUNTING);
    }

    @Override
    protected void initializeComponents() {
        // Create tab panels
        chartOfAccountsPanel = new ChartOfAccountsPanel();
        journalEntriesPanel = new JournalEntriesPanel();
        generalLedgerPanel = new GeneralLedgerPanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(Constants.FONT_REGULAR);
        tabbedPane.setBackground(Constants.BG_WHITE);

        // Add tabs
        tabbedPane.addTab("Chart of Accounts", chartOfAccountsPanel);
        tabbedPane.addTab("Journal Entries", journalEntriesPanel);
        tabbedPane.addTab("General Ledger", generalLedgerPanel);

        // Refresh data when tab changes
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: chartOfAccountsPanel.refreshData(); break;
                case 1: journalEntriesPanel.refreshData(); break;
                case 2: generalLedgerPanel.refreshData(); break;
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
            case 0: chartOfAccountsPanel.refreshData(); break;
            case 1: journalEntriesPanel.refreshData(); break;
            case 2: generalLedgerPanel.refreshData(); break;
        }
    }
}
