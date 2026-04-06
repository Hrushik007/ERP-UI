package com.erp.view.panels.integration;

import com.erp.service.mock.MockIntegrationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExternalIntegrationsPanel displays and manages external API integrations.
 *
 * Features:
 * - Integration status overview with summary cards
 * - Table of all external integrations with status
 * - Test connection, configure, and enable/disable actions
 */
public class ExternalIntegrationsPanel extends JPanel {

    private MockIntegrationService integrationService;

    private JTable integrationTable;
    private DefaultTableModel tableModel;

    // Summary labels
    private JLabel totalLabel;
    private JLabel connectedLabel;
    private JLabel disconnectedLabel;
    private JLabel errorCountLabel;

    private JButton testButton;
    private JButton configureButton;
    private JButton enableButton;
    private JButton disableButton;

    private static final String[] COLUMNS = {"Name", "Category", "Status", "Endpoint", "Last Check", "API Version", "Rate Limit Left"};

    public ExternalIntegrationsPanel() {
        integrationService = MockIntegrationService.getInstance();
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
            public boolean isCellEditable(int row, int column) { return false; }
        };
        integrationTable = new JTable(tableModel);
        integrationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(integrationTable);

        integrationTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        integrationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        integrationTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        integrationTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        integrationTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        integrationTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        integrationTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Status column renderer
        integrationTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        integrationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // Summary labels
        totalLabel = createSummaryValue("0");
        connectedLabel = createSummaryValue("0");
        disconnectedLabel = createSummaryValue("0");
        errorCountLabel = createSummaryValue("0");

        // Action buttons
        testButton = UIHelper.createPrimaryButton("Test Connection");
        testButton.setPreferredSize(new Dimension(140, 30));
        testButton.setEnabled(false);
        testButton.addActionListener(e -> testConnection());

        configureButton = UIHelper.createSecondaryButton("Configure");
        configureButton.setPreferredSize(new Dimension(100, 30));
        configureButton.setEnabled(false);
        configureButton.addActionListener(e -> configureIntegration());

        enableButton = new JButton("Enable");
        enableButton.setFont(Constants.FONT_BUTTON);
        enableButton.setBackground(Constants.SUCCESS_COLOR);
        enableButton.setForeground(Color.WHITE);
        enableButton.setOpaque(true);
        enableButton.setBorderPainted(false);
        enableButton.setFocusPainted(false);
        enableButton.setPreferredSize(new Dimension(90, 30));
        enableButton.setEnabled(false);
        enableButton.addActionListener(e -> toggleIntegration(true));

        disableButton = new JButton("Disable");
        disableButton.setFont(Constants.FONT_BUTTON);
        disableButton.setBackground(Constants.WARNING_COLOR);
        disableButton.setForeground(Color.WHITE);
        disableButton.setOpaque(true);
        disableButton.setBorderPainted(false);
        disableButton.setFocusPainted(false);
        disableButton.setPreferredSize(new Dimension(90, 30));
        disableButton.setEnabled(false);
        disableButton.addActionListener(e -> toggleIntegration(false));
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Toolbar
        JPanel toolbar = createToolbar();

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        // Table section
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(Constants.BG_WHITE);
        tableSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel tableTitle = new JLabel("External API Integrations");
        tableTitle.setFont(Constants.FONT_SUBTITLE);
        tableTitle.setForeground(Constants.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane scrollPane = new JScrollPane(integrationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        tableSection.add(tableTitle, BorderLayout.NORTH);
        tableSection.add(scrollPane, BorderLayout.CENTER);

        add(topSection, BorderLayout.NORTH);
        add(tableSection, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Integrations", totalLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Connected", connectedLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Disconnected", disconnectedLabel, Constants.TEXT_SECONDARY));
        panel.add(createSummaryCard("Errors", errorCountLabel, Constants.DANGER_COLOR));

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
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        toolbar.add(new JLabel("Actions:"));
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(testButton);
        toolbar.add(configureButton);
        toolbar.add(enableButton);
        toolbar.add(disableButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Map<String, Object>> integrations = integrationService.getExternalIntegrations();
        for (Map<String, Object> integration : integrations) {
            tableModel.addRow(new Object[]{
                integration.get("name"),
                integration.get("category"),
                integration.get("status"),
                integration.get("endpoint"),
                integration.get("lastCheck"),
                integration.get("apiVersion"),
                integration.get("rateLimitRemaining")
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        Map<String, Integer> counts = integrationService.getIntegrationCountByStatus();
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        totalLabel.setText(String.valueOf(total));
        connectedLabel.setText(String.valueOf(counts.getOrDefault("CONNECTED", 0)));
        disconnectedLabel.setText(String.valueOf(counts.getOrDefault("DISCONNECTED", 0)));
        errorCountLabel.setText(String.valueOf(counts.getOrDefault("ERROR", 0)));
    }

    private void updateButtonStates() {
        int row = integrationTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        testButton.setEnabled(hasSelection);
        configureButton.setEnabled(hasSelection);

        if (hasSelection) {
            String status = (String) tableModel.getValueAt(row, 2);
            enableButton.setEnabled("DISCONNECTED".equals(status));
            disableButton.setEnabled(!"DISCONNECTED".equals(status));
        } else {
            enableButton.setEnabled(false);
            disableButton.setEnabled(false);
        }
    }

    private void testConnection() {
        int row = integrationTable.getSelectedRow();
        if (row < 0) return;

        String name = (String) tableModel.getValueAt(row, 0);
        Map<String, Object> result = integrationService.testConnection(name);

        boolean success = Boolean.TRUE.equals(result.get("success"));
        String message = "Connection to " + name + ": " +
                (success ? "SUCCESS" : "FAILED") +
                "\nResponse Time: " + result.get("responseTime") +
                "\nMessage: " + result.get("message");

        if (success) {
            UIHelper.showSuccess(this, message);
        } else {
            UIHelper.showError(this, message);
        }
        loadData();
    }

    private void configureIntegration() {
        int row = integrationTable.getSelectedRow();
        if (row < 0) return;

        String name = (String) tableModel.getValueAt(row, 0);
        Map<String, Object> config = integrationService.getIntegrationConfig(name);
        if (config == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Configure: " + name, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(480, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField endpointField = new JTextField((String) config.getOrDefault("endpoint", ""));
        JTextField apiVersionField = new JTextField((String) config.getOrDefault("apiVersion", ""));
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"CRM", "ACCOUNTING", "PAYMENT", "E_COMMERCE", "EMAIL", "MESSAGING", "STORAGE"});
        categoryCombo.setSelectedItem(config.get("category"));
        JCheckBox enabledCheck = new JCheckBox("Enabled", Boolean.TRUE.equals(config.get("enabled")));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"CONNECTED", "DISCONNECTED", "ERROR"});
        statusCombo.setSelectedItem(config.get("status"));

        formPanel.add(new JLabel("Endpoint:"));
        formPanel.add(endpointField);
        formPanel.add(new JLabel("API Version:"));
        formPanel.add(apiVersionField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(enabledCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            Map<String, Object> newConfig = new HashMap<>();
            newConfig.put("endpoint", endpointField.getText().trim());
            newConfig.put("apiVersion", apiVersionField.getText().trim());
            newConfig.put("category", categoryCombo.getSelectedItem());
            newConfig.put("status", statusCombo.getSelectedItem());
            newConfig.put("enabled", enabledCheck.isSelected());
            newConfig.put("rateLimitRemaining", config.get("rateLimitRemaining"));

            if (integrationService.updateIntegrationConfig(name, newConfig)) {
                UIHelper.showSuccess(dialog, "Configuration updated successfully.");
                dialog.dispose();
                loadData();
            } else {
                UIHelper.showError(dialog, "Failed to update configuration.");
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void toggleIntegration(boolean enable) {
        int row = integrationTable.getSelectedRow();
        if (row < 0) return;

        String name = (String) tableModel.getValueAt(row, 0);
        String action = enable ? "enable" : "disable";

        if (UIHelper.showConfirm(this, action.substring(0, 1).toUpperCase() + action.substring(1) + " integration '" + name + "'?")) {
            Map<String, Object> config = integrationService.getIntegrationConfig(name);
            if (config != null) {
                config.put("enabled", enable);
                config.put("status", enable ? "CONNECTED" : "DISCONNECTED");
                integrationService.updateIntegrationConfig(name, config);
                UIHelper.showSuccess(this, "Integration " + action + "d successfully.");
                loadData();
            }
        }
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
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "CONNECTED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "DISCONNECTED":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "ERROR":
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
}
