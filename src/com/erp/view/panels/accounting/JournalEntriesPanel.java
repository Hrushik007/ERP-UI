package com.erp.view.panels.accounting;

import com.erp.model.JournalEntry;
import com.erp.model.JournalEntryLine;
import com.erp.service.mock.MockAccountingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.JournalEntryDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JournalEntriesPanel displays and manages journal entries.
 */
public class JournalEntriesPanel extends JPanel {

    private MockAccountingService accountingService;

    private JTable entriesTable;
    private DefaultTableModel tableModel;
    private JTable linesTable;
    private DefaultTableModel linesTableModel;

    private JComboBox<String> statusFilter;

    private JLabel totalEntriesLabel;
    private JLabel draftEntriesLabel;
    private JLabel postedEntriesLabel;

    private static final String[] COLUMNS = {"Entry #", "Date", "Description", "Reference", "Type", "Debit Total", "Credit Total", "Status"};
    private static final String[] LINE_COLUMNS = {"Line", "Account Code", "Account Name", "Description", "Debit", "Credit"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public JournalEntriesPanel() {
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
        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Status", "DRAFT", "POSTED", "VOID"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Main entries table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        entriesTable = new JTable(tableModel);
        entriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(entriesTable);

        // Column widths
        entriesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        entriesTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        entriesTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        entriesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        entriesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        entriesTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        entriesTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        entriesTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Custom renderers
        entriesTable.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
        entriesTable.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
        entriesTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        // Selection listener to show lines
        entriesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showEntryLines();
            }
        });

        // Lines detail table
        linesTableModel = new DefaultTableModel(LINE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        linesTable = new JTable(linesTableModel);
        linesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(linesTable);

        // Lines column widths
        linesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        linesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        linesTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        linesTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        linesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        linesTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        linesTable.getColumnModel().getColumn(4).setCellRenderer(new DebitCreditRenderer(true));
        linesTable.getColumnModel().getColumn(5).setCellRenderer(new DebitCreditRenderer(false));

        // Summary labels
        totalEntriesLabel = createSummaryValue("0");
        draftEntriesLabel = createSummaryValue("0");
        postedEntriesLabel = createSummaryValue("0");
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

        // Main entries scroll pane
        JScrollPane entriesScrollPane = new JScrollPane(entriesTable);
        entriesScrollPane.setBorder(BorderFactory.createTitledBorder("Journal Entries"));
        entriesScrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Lines detail scroll pane
        JScrollPane linesScrollPane = new JScrollPane(linesTable);
        linesScrollPane.setBorder(BorderFactory.createTitledBorder("Entry Lines (Debit/Credit Details)"));
        linesScrollPane.getViewport().setBackground(Constants.BG_WHITE);
        linesScrollPane.setPreferredSize(new Dimension(0, 150));

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        // Split pane for entries and lines
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, entriesScrollPane, linesScrollPane);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerLocation(300);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Entries", totalEntriesLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Draft Entries", draftEntriesLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Posted Entries", postedEntriesLabel, Constants.SUCCESS_COLOR));

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

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton createBtn = UIHelper.createPrimaryButton("New Entry");
        createBtn.setPreferredSize(new Dimension(100, 30));
        createBtn.addActionListener(e -> createEntry());
        toolbar.add(createBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editEntry());
        toolbar.add(editBtn);

        JButton postBtn = new JButton("Post");
        postBtn.setFont(Constants.FONT_BUTTON);
        postBtn.setBackground(Constants.SUCCESS_COLOR);
        postBtn.setForeground(Color.WHITE);
        postBtn.setOpaque(true);
        postBtn.setBorderPainted(false);
        postBtn.setFocusPainted(false);
        postBtn.setPreferredSize(new Dimension(80, 30));
        postBtn.addActionListener(e -> postEntry());
        toolbar.add(postBtn);

        JButton voidBtn = new JButton("Void");
        voidBtn.setFont(Constants.FONT_BUTTON);
        voidBtn.setBackground(Constants.DANGER_COLOR);
        voidBtn.setForeground(Color.WHITE);
        voidBtn.setOpaque(true);
        voidBtn.setBorderPainted(false);
        voidBtn.setFocusPainted(false);
        voidBtn.setPreferredSize(new Dimension(80, 30));
        voidBtn.addActionListener(e -> voidEntry());
        toolbar.add(voidBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        linesTableModel.setRowCount(0);

        List<JournalEntry> entries = accountingService.getAllJournalEntries();
        String statusSelection = (String) statusFilter.getSelectedItem();

        int totalCount = 0;
        int draftCount = 0;
        int postedCount = 0;

        for (JournalEntry e : entries) {
            if (!"All Status".equals(statusSelection) && !statusSelection.equals(e.getStatus())) {
                continue;
            }

            totalCount++;
            if ("DRAFT".equals(e.getStatus())) draftCount++;
            if ("POSTED".equals(e.getStatus())) postedCount++;

            tableModel.addRow(new Object[]{
                e.getEntryNumber(),
                e.getEntryDate() != null ? e.getEntryDate().format(DATE_FORMAT) : "",
                e.getDescription(),
                e.getReference(),
                e.getEntryType(),
                e.getTotalDebit(),
                e.getTotalCredit(),
                e.getStatus()
            });
        }

        totalEntriesLabel.setText(String.valueOf(totalCount));
        draftEntriesLabel.setText(String.valueOf(draftCount));
        postedEntriesLabel.setText(String.valueOf(postedCount));
    }

    private void showEntryLines() {
        linesTableModel.setRowCount(0);

        int row = entriesTable.getSelectedRow();
        if (row < 0) return;

        String entryNumber = (String) tableModel.getValueAt(row, 0);
        JournalEntry entry = accountingService.getJournalEntryByNumber(entryNumber);

        if (entry != null) {
            for (JournalEntryLine line : entry.getLines()) {
                linesTableModel.addRow(new Object[]{
                    line.getLineNumber(),
                    line.getAccountCode(),
                    line.getAccountName(),
                    line.getDescription() != null ? line.getDescription() : "",
                    line.getDebitAmount(),
                    line.getCreditAmount()
                });
            }
        }
    }

    private void createEntry() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        JournalEntryDialog dialog = new JournalEntryDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            JournalEntry newEntry = dialog.getJournalEntry();
            accountingService.createJournalEntry(newEntry);
            UIHelper.showSuccess(this, "Journal entry created successfully.");
            loadData();
        }
    }

    private void editEntry() {
        int row = entriesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an entry to edit.");
            return;
        }

        String entryNumber = (String) tableModel.getValueAt(row, 0);
        JournalEntry entry = accountingService.getJournalEntryByNumber(entryNumber);

        if (entry != null) {
            if (!entry.isDraft()) {
                UIHelper.showError(this, "Only draft entries can be edited.");
                return;
            }

            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            JournalEntryDialog dialog = new JournalEntryDialog(parentFrame, entry);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                accountingService.updateJournalEntry(dialog.getJournalEntry());
                UIHelper.showSuccess(this, "Journal entry updated successfully.");
                loadData();
            }
        }
    }

    private void postEntry() {
        int row = entriesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an entry to post.");
            return;
        }

        String entryNumber = (String) tableModel.getValueAt(row, 0);
        JournalEntry entry = accountingService.getJournalEntryByNumber(entryNumber);

        if (entry != null) {
            if (!entry.isDraft()) {
                UIHelper.showError(this, "Only draft entries can be posted.");
                return;
            }

            if (!entry.isBalanced()) {
                UIHelper.showError(this, "Entry is not balanced. Total debits must equal total credits.");
                return;
            }

            boolean confirm = UIHelper.showConfirm(this, "Post entry '" + entryNumber + "'? This action cannot be undone.");
            if (confirm) {
                if (accountingService.postJournalEntry(entry.getEntryId())) {
                    UIHelper.showSuccess(this, "Journal entry posted successfully.");
                    loadData();
                }
            }
        }
    }

    private void voidEntry() {
        int row = entriesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select an entry to void.");
            return;
        }

        String entryNumber = (String) tableModel.getValueAt(row, 0);
        JournalEntry entry = accountingService.getJournalEntryByNumber(entryNumber);

        if (entry != null) {
            if (entry.isVoid()) {
                UIHelper.showError(this, "This entry is already void.");
                return;
            }

            boolean confirm = UIHelper.showConfirm(this, "Void entry '" + entryNumber + "'? This action cannot be undone.");
            if (confirm) {
                if (accountingService.voidJournalEntry(entry.getEntryId())) {
                    UIHelper.showSuccess(this, "Journal entry voided successfully.");
                    loadData();
                }
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

    // Debit/Credit cell renderer
    private static class DebitCreditRenderer extends DefaultTableCellRenderer {
        private boolean isDebit;

        public DebitCreditRenderer(boolean isDebit) {
            this.isDebit = isDebit;
        }

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
                    if (!isSelected) {
                        setForeground(isDebit ? new Color(21, 87, 36) : new Color(114, 28, 36));
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
                    case "POSTED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "DRAFT":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "VOID":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
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
