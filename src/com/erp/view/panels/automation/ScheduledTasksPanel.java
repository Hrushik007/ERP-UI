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
 * ScheduledTasksPanel displays and manages scheduled/automated tasks.
 *
 * Split into two sections:
 * - Top: Task definitions with CRUD and enable/disable
 * - Bottom: Execution history log
 */
public class ScheduledTasksPanel extends JPanel {

    private MockAutomationService automationService;

    // Task definitions table
    private JTable tasksTable;
    private DefaultTableModel tasksModel;

    // Execution history table
    private JTable historyTable;
    private DefaultTableModel historyModel;

    // Summary labels
    private JLabel totalTasksLabel;
    private JLabel enabledTasksLabel;
    private JLabel lastRunSuccessLabel;
    private JLabel lastRunFailedLabel;

    private JButton editButton;
    private JButton deleteButton;
    private JButton enableButton;
    private JButton disableButton;

    private static final String[] TASK_COLUMNS = {"ID", "Name", "Type", "Schedule", "Enabled", "Last Run"};
    private static final String[] HISTORY_COLUMNS = {"Task", "Status", "Executed At", "Duration (s)", "Message"};

    public ScheduledTasksPanel() {
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
        // Tasks table
        tasksModel = new DefaultTableModel(TASK_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tasksTable = new JTable(tasksModel);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(tasksTable);

        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Enabled column renderer
        tasksTable.getColumnModel().getColumn(4).setCellRenderer(new EnabledCellRenderer());

        tasksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // History table
        historyModel = new DefaultTableModel(HISTORY_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(historyTable);

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        // Status column renderer for history
        historyTable.getColumnModel().getColumn(1).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalTasksLabel = createSummaryValue("0");
        enabledTasksLabel = createSummaryValue("0");
        lastRunSuccessLabel = createSummaryValue("0");
        lastRunFailedLabel = createSummaryValue("0");

        // Action buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editTask());

        deleteButton = new JButton("Delete");
        deleteButton.setFont(Constants.FONT_BUTTON);
        deleteButton.setBackground(Constants.DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(90, 30));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteTask());

        enableButton = new JButton("Enable");
        enableButton.setFont(Constants.FONT_BUTTON);
        enableButton.setBackground(Constants.SUCCESS_COLOR);
        enableButton.setForeground(Color.WHITE);
        enableButton.setOpaque(true);
        enableButton.setBorderPainted(false);
        enableButton.setFocusPainted(false);
        enableButton.setPreferredSize(new Dimension(90, 30));
        enableButton.setEnabled(false);
        enableButton.addActionListener(e -> toggleTask(true));

        disableButton = new JButton("Disable");
        disableButton.setFont(Constants.FONT_BUTTON);
        disableButton.setBackground(Constants.WARNING_COLOR);
        disableButton.setForeground(Color.WHITE);
        disableButton.setOpaque(true);
        disableButton.setBorderPainted(false);
        disableButton.setFocusPainted(false);
        disableButton.setPreferredSize(new Dimension(90, 30));
        disableButton.setEnabled(false);
        disableButton.addActionListener(e -> toggleTask(false));
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

        // Tasks section
        JPanel tasksSection = new JPanel(new BorderLayout());
        tasksSection.setBackground(Constants.BG_WHITE);
        tasksSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel tasksTitle = new JLabel("Scheduled Tasks");
        tasksTitle.setFont(Constants.FONT_SUBTITLE);
        tasksTitle.setForeground(Constants.TEXT_PRIMARY);
        tasksTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane tasksScroll = new JScrollPane(tasksTable);
        tasksScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tasksScroll.getViewport().setBackground(Constants.BG_WHITE);

        tasksSection.add(tasksTitle, BorderLayout.NORTH);
        tasksSection.add(tasksScroll, BorderLayout.CENTER);

        // History section
        JPanel historySection = new JPanel(new BorderLayout());
        historySection.setBackground(Constants.BG_WHITE);
        historySection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel historyTitle = new JLabel("Execution History");
        historyTitle.setFont(Constants.FONT_SUBTITLE);
        historyTitle.setForeground(Constants.TEXT_PRIMARY);
        historyTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        historyScroll.getViewport().setBackground(Constants.BG_WHITE);

        historySection.add(historyTitle, BorderLayout.NORTH);
        historySection.add(historyScroll, BorderLayout.CENTER);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tasksSection, historySection);
        splitPane.setDividerLocation(230);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Tasks", totalTasksLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Enabled", enabledTasksLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Last Runs OK", lastRunSuccessLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Last Runs Failed", lastRunFailedLabel, Constants.DANGER_COLOR));

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

        JButton addBtn = UIHelper.createPrimaryButton("Add Task");
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.addActionListener(e -> addTask());
        toolbar.add(addBtn);

        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(enableButton);
        toolbar.add(disableButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        // Load tasks
        tasksModel.setRowCount(0);
        List<Map<String, Object>> tasks = automationService.getScheduledTasks();

        int enabledCount = 0;
        for (Map<String, Object> t : tasks) {
            boolean enabled = Boolean.TRUE.equals(t.get("enabled"));
            if (enabled) enabledCount++;

            tasksModel.addRow(new Object[]{
                t.get("id"),
                t.get("name"),
                t.get("taskType"),
                t.get("schedule"),
                enabled ? "Yes" : "No",
                t.get("lastRun")
            });
        }

        // Load execution history
        historyModel.setRowCount(0);
        List<Map<String, Object>> history = automationService.getTaskExecutionHistory(null, 20);

        int successCount = 0;
        int failedCount = 0;
        for (Map<String, Object> h : history) {
            String status = (String) h.get("status");
            if ("SUCCESS".equals(status)) successCount++;
            else failedCount++;

            historyModel.addRow(new Object[]{
                h.get("taskName"),
                status,
                h.get("executedAt"),
                h.get("durationSeconds"),
                h.get("message")
            });
        }

        totalTasksLabel.setText(String.valueOf(tasks.size()));
        enabledTasksLabel.setText(String.valueOf(enabledCount));
        lastRunSuccessLabel.setText(String.valueOf(successCount));
        lastRunFailedLabel.setText(String.valueOf(failedCount));

        updateButtonStates();
    }

    private void updateButtonStates() {
        int row = tasksTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);

        if (hasSelection) {
            String enabled = (String) tasksModel.getValueAt(row, 4);
            enableButton.setEnabled("No".equals(enabled));
            disableButton.setEnabled("Yes".equals(enabled));
        } else {
            enableButton.setEnabled(false);
            disableButton.setEnabled(false);
        }
    }

    private void addTask() {
        showTaskDialog(null, "Add Scheduled Task");
    }

    private void editTask() {
        int row = tasksTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) tasksModel.getValueAt(row, 0);

        // Find the task data
        List<Map<String, Object>> tasks = automationService.getScheduledTasks();
        Map<String, Object> task = tasks.stream()
                .filter(t -> id.equals(t.get("id")))
                .findFirst().orElse(null);

        if (task != null) {
            showTaskDialog(task, "Edit Scheduled Task");
        }
    }

    private void deleteTask() {
        int row = tasksTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) tasksModel.getValueAt(row, 0);
        String name = (String) tasksModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Are you sure you want to delete task '" + name + "'?")) {
            if (automationService.deleteScheduledTask(id)) {
                UIHelper.showSuccess(this, "Task deleted successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to delete task.");
            }
        }
    }

    private void toggleTask(boolean enable) {
        int row = tasksTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) tasksModel.getValueAt(row, 0);
        String name = (String) tasksModel.getValueAt(row, 1);

        // Find task and update enabled status
        List<Map<String, Object>> tasks = automationService.getScheduledTasks();
        for (Map<String, Object> t : tasks) {
            if (id.equals(t.get("id"))) {
                t.put("enabled", enable);
                String action = enable ? "enabled" : "disabled";
                UIHelper.showSuccess(this, "Task '" + name + "' " + action + " successfully.");
                loadData();
                return;
            }
        }
        UIHelper.showError(this, "Failed to update task.");
    }

    private void showTaskDialog(Map<String, Object> existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 320);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField nameField = new JTextField(existing != null ? (String) existing.get("name") : "");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "REPORT_GENERATION", "DATA_BACKUP", "NOTIFICATION", "AUDIT", "HEALTH_CHECK", "CLEANUP"
        });
        JTextField scheduleField = new JTextField(existing != null ? (String) existing.get("schedule") : "");
        scheduleField.setToolTipText("e.g., Every day at 8:00 AM");
        JCheckBox enabledCheck = new JCheckBox("Enabled", existing == null || Boolean.TRUE.equals(existing.get("enabled")));

        if (existing != null) {
            typeCombo.setSelectedItem(existing.get("taskType"));
        }

        formPanel.add(new JLabel("Task Name:*"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Schedule:*"));
        formPanel.add(scheduleField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(enabledCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Task name is required.");
                return;
            }
            if (scheduleField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Schedule is required.");
                return;
            }

            Map<String, Object> taskData = new HashMap<>();
            taskData.put("name", nameField.getText().trim());
            taskData.put("taskType", typeCombo.getSelectedItem());
            taskData.put("schedule", scheduleField.getText().trim());
            taskData.put("cronExpression", "");
            taskData.put("enabled", enabledCheck.isSelected());

            if (existing == null) {
                automationService.createScheduledTask(taskData);
            } else {
                automationService.updateScheduledTask((String) existing.get("id"), taskData);
            }

            UIHelper.showSuccess(dialog, "Task saved successfully.");
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

    // Status cell renderer for execution history
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                if ("SUCCESS".equals(value.toString())) {
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
}
