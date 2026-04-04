package com.erp.view.panels.automation;

import com.erp.service.mock.MockAutomationService;
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
 * WorkflowsPanel displays and manages workflow definitions and running instances.
 *
 * Split into two sections:
 * - Top: Workflow definitions table with CRUD and enable/disable
 * - Bottom: Active workflow instances table
 */
public class WorkflowsPanel extends JPanel {

    private MockAutomationService automationService;

    // Workflow definitions table
    private JTable workflowTable;
    private DefaultTableModel workflowModel;

    // Workflow instances table
    private JTable instanceTable;
    private DefaultTableModel instanceModel;

    // Summary labels
    private JLabel totalWorkflowsLabel;
    private JLabel enabledLabel;
    private JLabel disabledLabel;
    private JLabel activeInstancesLabel;

    private JButton editButton;
    private JButton deleteButton;
    private JButton enableButton;
    private JButton disableButton;
    private JButton triggerButton;

    private static final String[] WF_COLUMNS = {"ID", "Name", "Type", "Enabled", "Triggers", "Created"};
    private static final String[] INST_COLUMNS = {"Instance ID", "Workflow", "Status", "Trigger Ref", "Started At", "Current Step"};

    public WorkflowsPanel() {
        automationService = MockAutomationService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Workflow definitions table
        workflowModel = new DefaultTableModel(WF_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        workflowTable = new JTable(workflowModel);
        workflowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(workflowTable);

        workflowTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        workflowTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        workflowTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        workflowTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        workflowTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        workflowTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Enabled column renderer
        workflowTable.getColumnModel().getColumn(3).setCellRenderer(new EnabledCellRenderer());

        workflowTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // Instance table
        instanceModel = new DefaultTableModel(INST_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        instanceTable = new JTable(instanceModel);
        instanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(instanceTable);

        instanceTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        instanceTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        instanceTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        instanceTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        instanceTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        instanceTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Status column renderer for instances
        instanceTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalWorkflowsLabel = createSummaryValue("0");
        enabledLabel = createSummaryValue("0");
        disabledLabel = createSummaryValue("0");
        activeInstancesLabel = createSummaryValue("0");

        // Action buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editWorkflow());

        deleteButton = new JButton("Delete");
        deleteButton.setFont(Constants.FONT_BUTTON);
        deleteButton.setBackground(Constants.DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(90, 30));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteWorkflow());

        enableButton = new JButton("Enable");
        enableButton.setFont(Constants.FONT_BUTTON);
        enableButton.setBackground(Constants.SUCCESS_COLOR);
        enableButton.setForeground(Color.WHITE);
        enableButton.setOpaque(true);
        enableButton.setBorderPainted(false);
        enableButton.setFocusPainted(false);
        enableButton.setPreferredSize(new Dimension(90, 30));
        enableButton.setEnabled(false);
        enableButton.addActionListener(e -> toggleWorkflow(true));

        disableButton = new JButton("Disable");
        disableButton.setFont(Constants.FONT_BUTTON);
        disableButton.setBackground(Constants.WARNING_COLOR);
        disableButton.setForeground(Color.WHITE);
        disableButton.setOpaque(true);
        disableButton.setBorderPainted(false);
        disableButton.setFocusPainted(false);
        disableButton.setPreferredSize(new Dimension(90, 30));
        disableButton.setEnabled(false);
        disableButton.addActionListener(e -> toggleWorkflow(false));

        triggerButton = new JButton("Trigger");
        triggerButton.setFont(Constants.FONT_BUTTON);
        triggerButton.setBackground(new Color(111, 66, 193));
        triggerButton.setForeground(Color.WHITE);
        triggerButton.setOpaque(true);
        triggerButton.setBorderPainted(false);
        triggerButton.setFocusPainted(false);
        triggerButton.setPreferredSize(new Dimension(90, 30));
        triggerButton.setEnabled(false);
        triggerButton.addActionListener(e -> triggerWorkflow());
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

        // Workflow definitions section
        JPanel wfSection = new JPanel(new BorderLayout());
        wfSection.setBackground(Constants.BG_WHITE);
        wfSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel wfTitle = new JLabel("Workflow Definitions");
        wfTitle.setFont(Constants.FONT_SUBTITLE);
        wfTitle.setForeground(Constants.TEXT_PRIMARY);
        wfTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane wfScroll = new JScrollPane(workflowTable);
        wfScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        wfScroll.getViewport().setBackground(Constants.BG_WHITE);

        wfSection.add(wfTitle, BorderLayout.NORTH);
        wfSection.add(wfScroll, BorderLayout.CENTER);

        // Instance section
        JPanel instSection = new JPanel(new BorderLayout());
        instSection.setBackground(Constants.BG_WHITE);
        instSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel instTitle = new JLabel("Active Workflow Instances");
        instTitle.setFont(Constants.FONT_SUBTITLE);
        instTitle.setForeground(Constants.TEXT_PRIMARY);
        instTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane instScroll = new JScrollPane(instanceTable);
        instScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        instScroll.getViewport().setBackground(Constants.BG_WHITE);

        instSection.add(instTitle, BorderLayout.NORTH);
        instSection.add(instScroll, BorderLayout.CENTER);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, wfSection, instSection);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Workflows", totalWorkflowsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Enabled", enabledLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Disabled", disabledLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Active Instances", activeInstancesLabel, new Color(111, 66, 193)));

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

        JButton addBtn = UIHelper.createPrimaryButton("Add Workflow");
        addBtn.setPreferredSize(new Dimension(130, 30));
        addBtn.addActionListener(e -> addWorkflow());
        toolbar.add(addBtn);

        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(enableButton);
        toolbar.add(disableButton);
        toolbar.add(triggerButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        // Load workflow definitions
        workflowModel.setRowCount(0);
        List<Map<String, Object>> workflows = automationService.getAllWorkflows();
        for (Map<String, Object> wf : workflows) {
            workflowModel.addRow(new Object[]{
                wf.get("id"),
                wf.get("name"),
                wf.get("type"),
                Boolean.TRUE.equals(wf.get("enabled")) ? "Yes" : "No",
                wf.get("triggerCount"),
                wf.get("createdAt")
            });
        }

        // Load instances
        instanceModel.setRowCount(0);
        List<Map<String, Object>> instances = automationService.getAllWorkflowInstances();
        for (Map<String, Object> inst : instances) {
            instanceModel.addRow(new Object[]{
                inst.get("id"),
                inst.get("workflowName"),
                inst.get("status"),
                inst.get("triggerRef"),
                inst.get("startedAt"),
                inst.get("currentStep")
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        Map<String, Integer> counts = automationService.getWorkflowCounts();
        totalWorkflowsLabel.setText(String.valueOf(counts.getOrDefault("total", 0)));
        enabledLabel.setText(String.valueOf(counts.getOrDefault("enabled", 0)));
        disabledLabel.setText(String.valueOf(counts.getOrDefault("disabled", 0)));
        activeInstancesLabel.setText(String.valueOf(automationService.getActiveWorkflowInstances().size()));
    }

    private void updateButtonStates() {
        int row = workflowTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        triggerButton.setEnabled(hasSelection);

        if (hasSelection) {
            String enabled = (String) workflowModel.getValueAt(row, 3);
            enableButton.setEnabled("No".equals(enabled));
            disableButton.setEnabled("Yes".equals(enabled));
        } else {
            enableButton.setEnabled(false);
            disableButton.setEnabled(false);
        }
    }

    private void addWorkflow() {
        showWorkflowDialog(null, "Add New Workflow");
    }

    private void editWorkflow() {
        int row = workflowTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) workflowModel.getValueAt(row, 0);
        Map<String, Object> wf = automationService.getWorkflowById(id);
        if (wf != null) {
            showWorkflowDialog(wf, "Edit Workflow");
        }
    }

    private void deleteWorkflow() {
        int row = workflowTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) workflowModel.getValueAt(row, 0);
        String name = (String) workflowModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Are you sure you want to delete workflow '" + name + "'?")) {
            if (automationService.deleteWorkflow(id)) {
                UIHelper.showSuccess(this, "Workflow deleted successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to delete workflow.");
            }
        }
    }

    private void toggleWorkflow(boolean enable) {
        int row = workflowTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) workflowModel.getValueAt(row, 0);
        String name = (String) workflowModel.getValueAt(row, 1);
        String action = enable ? "enable" : "disable";

        if (UIHelper.showConfirm(this, action.substring(0, 1).toUpperCase() + action.substring(1) + " workflow '" + name + "'?")) {
            if (automationService.setWorkflowEnabled(id, enable)) {
                UIHelper.showSuccess(this, "Workflow " + action + "d successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to " + action + " workflow.");
            }
        }
    }

    private void triggerWorkflow() {
        int row = workflowTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) workflowModel.getValueAt(row, 0);
        String name = (String) workflowModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Manually trigger workflow '" + name + "'?")) {
            Map<String, Object> triggerData = new HashMap<>();
            triggerData.put("ref", "Manual-" + System.currentTimeMillis() % 10000);
            String instanceId = automationService.triggerWorkflow(id, triggerData);
            if (instanceId != null) {
                UIHelper.showSuccess(this, "Workflow triggered! Instance: " + instanceId);
                loadData();
            } else {
                UIHelper.showError(this, "Failed to trigger workflow.");
            }
        }
    }

    private void showWorkflowDialog(Map<String, Object> existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField nameField = new JTextField(existing != null ? (String) existing.get("name") : "");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"AUTO_APPROVE", "APPROVAL_CHAIN", "SEQUENTIAL", "PARALLEL", "CONDITIONAL"});
        JTextArea descArea = new JTextArea(existing != null ? (String) existing.getOrDefault("description", "") : "");
        descArea.setRows(2);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JTextArea defArea = new JTextArea(existing != null ? (String) existing.getOrDefault("definition", "") : "");
        defArea.setRows(2);
        defArea.setLineWrap(true);
        defArea.setWrapStyleWord(true);
        JCheckBox enabledCheck = new JCheckBox("Enabled", existing == null || Boolean.TRUE.equals(existing.get("enabled")));

        if (existing != null) {
            typeCombo.setSelectedItem(existing.get("type"));
        }

        formPanel.add(new JLabel("Name:*"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Definition:"));
        formPanel.add(new JScrollPane(defArea));
        formPanel.add(new JLabel("Status:"));
        formPanel.add(enabledCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Workflow name is required.");
                return;
            }

            Map<String, Object> wfData = new HashMap<>();
            wfData.put("name", nameField.getText().trim());
            wfData.put("type", typeCombo.getSelectedItem());
            wfData.put("description", descArea.getText().trim());
            wfData.put("definition", defArea.getText().trim());
            wfData.put("enabled", enabledCheck.isSelected());

            if (existing == null) {
                automationService.createWorkflow(wfData);
            } else {
                automationService.updateWorkflow((String) existing.get("id"), wfData);
            }

            UIHelper.showSuccess(dialog, "Workflow saved successfully.");
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

    // Enabled cell renderer
    private static class EnabledCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                if ("Yes".equals(value.toString())) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                }
            }
            return this;
        }
    }

    // Status cell renderer for instances
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "IN_PROGRESS":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "COMPLETED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "WAITING":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "CANCELLED":
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
