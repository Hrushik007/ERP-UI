package com.erp.view.panels.crm;

import com.erp.model.Lead;
import com.erp.service.mock.MockCRMService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * LeadsPanel displays and manages sales leads.
 */
public class LeadsPanel extends JPanel {

    private MockCRMService crmService;

    private JTable leadsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> statusFilter;
    private JComboBox<String> sourceFilter;
    private JTextField searchField;

    // Summary labels
    private JLabel totalLeadsLabel;
    private JLabel newLeadsLabel;
    private JLabel qualifiedLeadsLabel;
    private JLabel convertedLeadsLabel;

    private JButton editButton;
    private JButton convertButton;

    private static final String[] COLUMNS = {"ID", "Company", "Contact", "Email", "Phone", "Source", "Status", "Assigned To", "Created"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LeadsPanel() {
        crmService = MockCRMService.getInstance();
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
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "NEW", "CONTACTED", "QUALIFIED", "UNQUALIFIED", "CONVERTED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Source filter
        sourceFilter = new JComboBox<>(new String[]{"All Sources", "WEBSITE", "REFERRAL", "TRADE_SHOW", "COLD_CALL", "ADVERTISEMENT", "OTHER"});
        sourceFilter.setFont(Constants.FONT_REGULAR);
        sourceFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by company, contact, or email");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leadsTable = new JTable(tableModel);
        leadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(leadsTable);

        // Column widths
        leadsTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        leadsTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Company
        leadsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Contact
        leadsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        leadsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Phone
        leadsTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Source
        leadsTable.getColumnModel().getColumn(6).setPreferredWidth(90);  // Status
        leadsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Assigned
        leadsTable.getColumnModel().getColumn(8).setPreferredWidth(90);  // Created

        // Status column renderer
        leadsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        leadsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Summary labels
        totalLeadsLabel = createSummaryValue("0");
        newLeadsLabel = createSummaryValue("0");
        qualifiedLeadsLabel = createSummaryValue("0");
        convertedLeadsLabel = createSummaryValue("0");

        // Buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editLead());

        convertButton = new JButton("Convert to Opportunity");
        convertButton.setFont(Constants.FONT_BUTTON);
        convertButton.setBackground(Constants.SUCCESS_COLOR);
        convertButton.setForeground(Color.WHITE);
        convertButton.setOpaque(true);
        convertButton.setBorderPainted(false);
        convertButton.setFocusPainted(false);
        convertButton.setPreferredSize(new Dimension(160, 30));
        convertButton.setEnabled(false);
        convertButton.addActionListener(e -> convertLead());
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Top - Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(leadsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Leads", totalLeadsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("New", newLeadsLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Qualified", qualifiedLeadsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Converted", convertedLeadsLabel, new Color(111, 66, 193)));

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
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        // Filters row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Source:"));
        filterPanel.add(sourceFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> loadData());
        filterPanel.add(searchBtn);

        // Actions row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));

        JButton addBtn = UIHelper.createPrimaryButton("Add Lead");
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.addActionListener(e -> addLead());
        actionPanel.add(addBtn);

        actionPanel.add(editButton);
        actionPanel.add(convertButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        actionPanel.add(refreshBtn);

        toolbar.add(filterPanel, BorderLayout.NORTH);
        toolbar.add(actionPanel, BorderLayout.SOUTH);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Lead> leads = crmService.getAllLeads();
        String statusSelection = (String) statusFilter.getSelectedItem();
        String sourceSelection = (String) sourceFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (Lead l : leads) {
            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(l.getStatus())) {
                continue;
            }

            // Filter by source
            if (!"All Sources".equals(sourceSelection) && !sourceSelection.equals(l.getSource())) {
                continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = l.getCompanyName().toLowerCase().contains(searchTerm) ||
                                  l.getContactName().toLowerCase().contains(searchTerm) ||
                                  l.getEmail().toLowerCase().contains(searchTerm);
                if (!matches) continue;
            }

            tableModel.addRow(new Object[]{
                l.getLeadId(),
                l.getCompanyName(),
                l.getContactName(),
                l.getEmail(),
                l.getPhone(),
                l.getSource(),
                l.getStatus(),
                l.getAssignedToName() != null ? l.getAssignedToName() : "Unassigned",
                l.getCreatedAt() != null ? l.getCreatedAt().format(DATE_FORMAT) : ""
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        Map<String, Integer> counts = crmService.getLeadCountByStatus();
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();

        totalLeadsLabel.setText(String.valueOf(total));
        newLeadsLabel.setText(String.valueOf(counts.getOrDefault("NEW", 0)));
        qualifiedLeadsLabel.setText(String.valueOf(counts.getOrDefault("QUALIFIED", 0)));
        convertedLeadsLabel.setText(String.valueOf(counts.getOrDefault("CONVERTED", 0)));
    }

    private void updateButtonStates() {
        int row = leadsTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);

        if (hasSelection) {
            String status = (String) tableModel.getValueAt(row, 6);
            convertButton.setEnabled(!"CONVERTED".equals(status));
        } else {
            convertButton.setEnabled(false);
        }
    }

    private void addLead() {
        showLeadDialog(null, "Add New Lead");
    }

    private void editLead() {
        int row = leadsTable.getSelectedRow();
        if (row < 0) return;

        int leadId = (int) tableModel.getValueAt(row, 0);
        Lead lead = crmService.getLeadById(leadId);

        if (lead != null) {
            showLeadDialog(lead, "Edit Lead");
        }
    }

    private void convertLead() {
        int row = leadsTable.getSelectedRow();
        if (row < 0) return;

        int leadId = (int) tableModel.getValueAt(row, 0);
        String company = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Convert lead '" + company + "' to an opportunity?\n\n" +
            "This will:\n" +
            "- Create a new customer record\n" +
            "- Create a new contact\n" +
            "- Create a new opportunity\n" +
            "- Mark the lead as converted",
            "Confirm Conversion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            var opp = crmService.convertLead(leadId);
            if (opp != null) {
                UIHelper.showSuccess(this, "Lead converted successfully!\nOpportunity '" + opp.getName() + "' created.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to convert lead.");
            }
        }
    }

    private void showLeadDialog(Lead existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField companyField = new JTextField(existing != null ? existing.getCompanyName() : "");
        JTextField contactField = new JTextField(existing != null ? existing.getContactName() : "");
        JTextField emailField = new JTextField(existing != null ? existing.getEmail() : "");
        JTextField phoneField = new JTextField(existing != null ? existing.getPhone() : "");
        JComboBox<String> sourceCombo = new JComboBox<>(new String[]{"WEBSITE", "REFERRAL", "TRADE_SHOW", "COLD_CALL", "ADVERTISEMENT", "OTHER"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"NEW", "CONTACTED", "QUALIFIED", "UNQUALIFIED"});
        JTextField titleField = new JTextField(existing != null && existing.getTitle() != null ? existing.getTitle() : "");
        JTextArea notesArea = new JTextArea(existing != null && existing.getNotes() != null ? existing.getNotes() : "");
        notesArea.setRows(2);

        if (existing != null) {
            sourceCombo.setSelectedItem(existing.getSource());
            statusCombo.setSelectedItem(existing.getStatus());
        }

        formPanel.add(new JLabel("Company Name:"));
        formPanel.add(companyField);
        formPanel.add(new JLabel("Contact Name:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Source:"));
        formPanel.add(sourceCombo);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            // Validation
            if (companyField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Company name is required.");
                return;
            }
            if (contactField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Contact name is required.");
                return;
            }

            if (existing == null) {
                Lead newLead = new Lead();
                newLead.setCompanyName(companyField.getText().trim());
                newLead.setContactName(contactField.getText().trim());
                newLead.setEmail(emailField.getText().trim());
                newLead.setPhone(phoneField.getText().trim());
                newLead.setTitle(titleField.getText().trim());
                newLead.setSource((String) sourceCombo.getSelectedItem());
                newLead.setStatus((String) statusCombo.getSelectedItem());
                newLead.setNotes(notesArea.getText().trim());
                crmService.createLead(newLead);
            } else {
                existing.setCompanyName(companyField.getText().trim());
                existing.setContactName(contactField.getText().trim());
                existing.setEmail(emailField.getText().trim());
                existing.setPhone(phoneField.getText().trim());
                existing.setTitle(titleField.getText().trim());
                existing.setSource((String) sourceCombo.getSelectedItem());
                existing.setStatus((String) statusCombo.getSelectedItem());
                existing.setNotes(notesArea.getText().trim());
                crmService.updateLead(existing);
            }

            UIHelper.showSuccess(dialog, "Lead saved successfully.");
            dialog.dispose();
            loadData();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        loadData();
    }

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (status) {
                        case "NEW":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "CONTACTED":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "QUALIFIED":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "UNQUALIFIED":
                            setBackground(new Color(248, 215, 218));
                            setForeground(new Color(114, 28, 36));
                            break;
                        case "CONVERTED":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
                            break;
                        default:
                            setBackground(table.getBackground());
                            setForeground(table.getForeground());
                    }
                }
            }
            return this;
        }
    }
}
