package com.erp.view.panels.crm;

import com.erp.model.Opportunity;
import com.erp.service.mock.MockCRMService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * OpportunitiesPanel displays and manages sales opportunities (pipeline).
 */
public class OpportunitiesPanel extends JPanel {

    private MockCRMService crmService;

    private JTable opportunitiesTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> stageFilter;
    private JTextField searchField;

    // Summary labels
    private JLabel totalOpportunitiesLabel;
    private JLabel pipelineValueLabel;
    private JLabel wonValueLabel;
    private JLabel avgProbabilityLabel;

    private JButton editButton;
    private JButton advanceButton;
    private JButton closeWonButton;
    private JButton closeLostButton;

    private static final String[] COLUMNS = {"ID", "Name", "Customer", "Value", "Stage", "Probability", "Expected Close", "Assigned To"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public OpportunitiesPanel() {
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
        // Stage filter
        stageFilter = new JComboBox<>(new String[]{"All Stages", "PROSPECTING", "QUALIFICATION", "PROPOSAL", "NEGOTIATION", "CLOSED_WON", "CLOSED_LOST"});
        stageFilter.setFont(Constants.FONT_REGULAR);
        stageFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by name or customer");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        opportunitiesTable = new JTable(tableModel);
        opportunitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(opportunitiesTable);

        // Column widths
        opportunitiesTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        opportunitiesTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Name
        opportunitiesTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Customer
        opportunitiesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Value
        opportunitiesTable.getColumnModel().getColumn(4).setPreferredWidth(110); // Stage
        opportunitiesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Probability
        opportunitiesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Expected Close
        opportunitiesTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Assigned

        // Stage column renderer
        opportunitiesTable.getColumnModel().getColumn(4).setCellRenderer(new StageCellRenderer());

        opportunitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Summary labels
        totalOpportunitiesLabel = createSummaryValue("0");
        pipelineValueLabel = createSummaryValue("$0");
        wonValueLabel = createSummaryValue("$0");
        avgProbabilityLabel = createSummaryValue("0%");

        // Buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editOpportunity());

        advanceButton = new JButton("Advance Stage");
        advanceButton.setFont(Constants.FONT_BUTTON);
        advanceButton.setBackground(Constants.PRIMARY_COLOR);
        advanceButton.setForeground(Color.WHITE);
        advanceButton.setOpaque(true);
        advanceButton.setBorderPainted(false);
        advanceButton.setFocusPainted(false);
        advanceButton.setPreferredSize(new Dimension(120, 30));
        advanceButton.setEnabled(false);
        advanceButton.addActionListener(e -> advanceStage());

        closeWonButton = new JButton("Close Won");
        closeWonButton.setFont(Constants.FONT_BUTTON);
        closeWonButton.setBackground(Constants.SUCCESS_COLOR);
        closeWonButton.setForeground(Color.WHITE);
        closeWonButton.setOpaque(true);
        closeWonButton.setBorderPainted(false);
        closeWonButton.setFocusPainted(false);
        closeWonButton.setPreferredSize(new Dimension(100, 30));
        closeWonButton.setEnabled(false);
        closeWonButton.addActionListener(e -> closeWon());

        closeLostButton = new JButton("Close Lost");
        closeLostButton.setFont(Constants.FONT_BUTTON);
        closeLostButton.setBackground(Constants.DANGER_COLOR);
        closeLostButton.setForeground(Color.WHITE);
        closeLostButton.setOpaque(true);
        closeLostButton.setBorderPainted(false);
        closeLostButton.setFocusPainted(false);
        closeLostButton.setPreferredSize(new Dimension(100, 30));
        closeLostButton.setEnabled(false);
        closeLostButton.addActionListener(e -> closeLost());
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
        JScrollPane scrollPane = new JScrollPane(opportunitiesTable);
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

        panel.add(createSummaryCard("Active Opportunities", totalOpportunitiesLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Pipeline Value", pipelineValueLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Won (This Period)", wonValueLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Avg Probability", avgProbabilityLabel, Constants.WARNING_COLOR));

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
        filterPanel.add(new JLabel("Stage:"));
        filterPanel.add(stageFilter);
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

        JButton addBtn = UIHelper.createPrimaryButton("Add Opportunity");
        addBtn.setPreferredSize(new Dimension(130, 30));
        addBtn.addActionListener(e -> addOpportunity());
        actionPanel.add(addBtn);

        actionPanel.add(editButton);
        actionPanel.add(advanceButton);
        actionPanel.add(closeWonButton);
        actionPanel.add(closeLostButton);

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

        List<Opportunity> opportunities = crmService.getAllOpportunities();
        String stageSelection = (String) stageFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (Opportunity o : opportunities) {
            // Filter by stage
            if (!"All Stages".equals(stageSelection) && !stageSelection.equals(o.getStage())) {
                continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = o.getName().toLowerCase().contains(searchTerm) ||
                                  (o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(searchTerm));
                if (!matches) continue;
            }

            tableModel.addRow(new Object[]{
                o.getOpportunityId(),
                o.getName(),
                o.getCustomerName(),
                o.getEstimatedValue() != null ? "$" + String.format("%,.0f", o.getEstimatedValue()) : "-",
                o.getStage(),
                o.getProbability() + "%",
                o.getExpectedCloseDate() != null ? o.getExpectedCloseDate().format(DATE_FORMAT) : "",
                o.getAssignedToName() != null ? o.getAssignedToName() : "Unassigned"
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        List<Opportunity> all = crmService.getAllOpportunities();

        long activeCount = all.stream().filter(o -> !o.isClosed()).count();
        BigDecimal pipelineValue = crmService.getTotalPipelineValue();
        BigDecimal wonValue = all.stream()
                .filter(o -> "CLOSED_WON".equals(o.getStage()))
                .map(o -> o.getActualValue() != null ? o.getActualValue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double avgProb = all.stream()
                .filter(o -> !o.isClosed())
                .mapToInt(Opportunity::getProbability)
                .average()
                .orElse(0);

        totalOpportunitiesLabel.setText(String.valueOf(activeCount));
        pipelineValueLabel.setText("$" + String.format("%,.0f", pipelineValue));
        wonValueLabel.setText("$" + String.format("%,.0f", wonValue));
        avgProbabilityLabel.setText(String.format("%.0f%%", avgProb));
    }

    private void updateButtonStates() {
        int row = opportunitiesTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);

        if (hasSelection) {
            String stage = (String) tableModel.getValueAt(row, 4);
            boolean isClosed = "CLOSED_WON".equals(stage) || "CLOSED_LOST".equals(stage);
            advanceButton.setEnabled(!isClosed && !"NEGOTIATION".equals(stage));
            closeWonButton.setEnabled(!isClosed);
            closeLostButton.setEnabled(!isClosed);
        } else {
            advanceButton.setEnabled(false);
            closeWonButton.setEnabled(false);
            closeLostButton.setEnabled(false);
        }
    }

    private void addOpportunity() {
        showOpportunityDialog(null, "Add New Opportunity");
    }

    private void editOpportunity() {
        int row = opportunitiesTable.getSelectedRow();
        if (row < 0) return;

        int oppId = (int) tableModel.getValueAt(row, 0);
        Opportunity opp = crmService.getOpportunityById(oppId);

        if (opp != null) {
            showOpportunityDialog(opp, "Edit Opportunity");
        }
    }

    private void advanceStage() {
        int row = opportunitiesTable.getSelectedRow();
        if (row < 0) return;

        int oppId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Advance '" + name + "' to the next stage?",
            "Confirm Stage Advancement",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (crmService.advanceOpportunityStage(oppId)) {
                UIHelper.showSuccess(this, "Stage advanced successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to advance stage.");
            }
        }
    }

    private void closeWon() {
        int row = opportunitiesTable.getSelectedRow();
        if (row < 0) return;

        int oppId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        Opportunity opp = crmService.getOpportunityById(oppId);

        String valueStr = JOptionPane.showInputDialog(this,
            "Close '" + name + "' as WON.\n\nEnter the actual deal value:",
            opp != null && opp.getEstimatedValue() != null ? opp.getEstimatedValue().toString() : "0");

        if (valueStr != null) {
            try {
                BigDecimal actualValue = new BigDecimal(valueStr.replace(",", "").replace("$", ""));
                if (crmService.closeOpportunityWon(oppId, actualValue)) {
                    UIHelper.showSuccess(this, "Congratulations! Deal closed successfully.");
                    loadData();
                } else {
                    UIHelper.showError(this, "Failed to close opportunity.");
                }
            } catch (NumberFormatException e) {
                UIHelper.showError(this, "Please enter a valid number.");
            }
        }
    }

    private void closeLost() {
        int row = opportunitiesTable.getSelectedRow();
        if (row < 0) return;

        int oppId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        String reason = JOptionPane.showInputDialog(this,
            "Close '" + name + "' as LOST.\n\nEnter the reason for losing:",
            "");

        if (reason != null && !reason.trim().isEmpty()) {
            if (crmService.closeOpportunityLost(oppId, reason.trim())) {
                UIHelper.showSuccess(this, "Opportunity marked as lost.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to close opportunity.");
            }
        }
    }

    private void showOpportunityDialog(Opportunity existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField nameField = new JTextField(existing != null ? existing.getName() : "");

        // Customer combo
        JComboBox<String> customerCombo = new JComboBox<>();
        customerCombo.addItem("-- Select Customer --");
        for (var c : crmService.getAllCustomers()) {
            customerCombo.addItem(c.getCustomerId() + " - " + c.getCompanyName());
        }
        if (existing != null && existing.getCustomerId() > 0) {
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(existing.getCustomerId() + " - ")) {
                    customerCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        JSpinner valueSpinner = new JSpinner(new SpinnerNumberModel(
            existing != null && existing.getEstimatedValue() != null ? existing.getEstimatedValue().doubleValue() : 0,
            0, 10000000, 1000));

        JComboBox<String> stageCombo = new JComboBox<>(new String[]{"PROSPECTING", "QUALIFICATION", "PROPOSAL", "NEGOTIATION"});
        if (existing != null && !existing.isClosed()) {
            stageCombo.setSelectedItem(existing.getStage());
        }

        JSpinner probSpinner = new JSpinner(new SpinnerNumberModel(
            existing != null ? existing.getProbability() : 10, 0, 100, 5));

        // Expected close date (simplified - using text field)
        JTextField closeDateField = new JTextField(
            existing != null && existing.getExpectedCloseDate() != null ?
            existing.getExpectedCloseDate().format(DATE_FORMAT) :
            LocalDate.now().plusDays(30).format(DATE_FORMAT));

        JTextArea descArea = new JTextArea(existing != null && existing.getDescription() != null ? existing.getDescription() : "");
        descArea.setRows(2);

        formPanel.add(new JLabel("Opportunity Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerCombo);
        formPanel.add(new JLabel("Estimated Value ($):"));
        formPanel.add(valueSpinner);
        formPanel.add(new JLabel("Stage:"));
        formPanel.add(stageCombo);
        formPanel.add(new JLabel("Probability (%):"));
        formPanel.add(probSpinner);
        formPanel.add(new JLabel("Expected Close (yyyy-MM-dd):"));
        formPanel.add(closeDateField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            // Validation
            if (nameField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Opportunity name is required.");
                return;
            }
            if (customerCombo.getSelectedIndex() == 0) {
                UIHelper.showError(dialog, "Please select a customer.");
                return;
            }

            // Parse customer ID
            String custSelection = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(custSelection.split(" - ")[0]);
            String customerName = custSelection.substring(custSelection.indexOf(" - ") + 3);

            // Parse date
            LocalDate closeDate;
            try {
                closeDate = LocalDate.parse(closeDateField.getText().trim(), DATE_FORMAT);
            } catch (Exception ex) {
                UIHelper.showError(dialog, "Invalid date format. Use yyyy-MM-dd.");
                return;
            }

            if (existing == null) {
                Opportunity newOpp = new Opportunity();
                newOpp.setName(nameField.getText().trim());
                newOpp.setCustomerId(customerId);
                newOpp.setCustomerName(customerName);
                newOpp.setEstimatedValue(BigDecimal.valueOf((Double) valueSpinner.getValue()));
                newOpp.setStage((String) stageCombo.getSelectedItem());
                newOpp.setProbability((Integer) probSpinner.getValue());
                newOpp.setExpectedCloseDate(closeDate);
                newOpp.setDescription(descArea.getText().trim());
                crmService.createOpportunity(newOpp);
            } else {
                existing.setName(nameField.getText().trim());
                existing.setCustomerId(customerId);
                existing.setCustomerName(customerName);
                existing.setEstimatedValue(BigDecimal.valueOf((Double) valueSpinner.getValue()));
                if (!existing.isClosed()) {
                    existing.setStage((String) stageCombo.getSelectedItem());
                }
                existing.setProbability((Integer) probSpinner.getValue());
                existing.setExpectedCloseDate(closeDate);
                existing.setDescription(descArea.getText().trim());
                crmService.updateOpportunity(existing);
            }

            UIHelper.showSuccess(dialog, "Opportunity saved successfully.");
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

    // Stage cell renderer
    private static class StageCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String stage = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (stage) {
                        case "PROSPECTING":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "QUALIFICATION":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "PROPOSAL":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
                            break;
                        case "NEGOTIATION":
                            setBackground(new Color(254, 226, 226));
                            setForeground(new Color(153, 27, 27));
                            break;
                        case "CLOSED_WON":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "CLOSED_LOST":
                            setBackground(new Color(248, 215, 218));
                            setForeground(new Color(114, 28, 36));
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
