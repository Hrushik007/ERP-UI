package com.erp.view.panels.crm;

import com.erp.model.Activity;
import com.erp.service.mock.MockCRMService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * ActivitiesPanel displays and manages CRM activities (calls, meetings, tasks).
 */
public class ActivitiesPanel extends JPanel {

    private MockCRMService crmService;

    private JTable activitiesTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private JTextField searchField;

    // Summary labels
    private JLabel totalActivitiesLabel;
    private JLabel plannedLabel;
    private JLabel overdueLabel;
    private JLabel completedLabel;

    private JButton editButton;
    private JButton completeButton;

    private static final String[] COLUMNS = {"ID", "Type", "Subject", "Related To", "Due Date", "Priority", "Status", "Assigned To"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ActivitiesPanel() {
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
        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "CALL", "MEETING", "EMAIL", "TASK", "NOTE"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by subject");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activitiesTable = new JTable(tableModel);
        activitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(activitiesTable);

        // Column widths
        activitiesTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        activitiesTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Type
        activitiesTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Subject
        activitiesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Related To
        activitiesTable.getColumnModel().getColumn(4).setPreferredWidth(130); // Due Date
        activitiesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Priority
        activitiesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        activitiesTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Assigned

        // Type column renderer
        activitiesTable.getColumnModel().getColumn(1).setCellRenderer(new TypeCellRenderer());
        // Status column renderer
        activitiesTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        activitiesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Summary labels
        totalActivitiesLabel = createSummaryValue("0");
        plannedLabel = createSummaryValue("0");
        overdueLabel = createSummaryValue("0");
        completedLabel = createSummaryValue("0");

        // Buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editActivity());

        completeButton = new JButton("Mark Complete");
        completeButton.setFont(Constants.FONT_BUTTON);
        completeButton.setBackground(Constants.SUCCESS_COLOR);
        completeButton.setForeground(Color.WHITE);
        completeButton.setOpaque(true);
        completeButton.setBorderPainted(false);
        completeButton.setFocusPainted(false);
        completeButton.setPreferredSize(new Dimension(120, 30));
        completeButton.setEnabled(false);
        completeButton.addActionListener(e -> completeActivity());
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
        JScrollPane scrollPane = new JScrollPane(activitiesTable);
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

        panel.add(createSummaryCard("Total Activities", totalActivitiesLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Planned", plannedLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Overdue", overdueLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Completed", completedLabel, Constants.SUCCESS_COLOR));

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
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
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

        JButton addBtn = UIHelper.createPrimaryButton("Add Activity");
        addBtn.setPreferredSize(new Dimension(110, 30));
        addBtn.addActionListener(e -> addActivity());
        actionPanel.add(addBtn);

        actionPanel.add(editButton);
        actionPanel.add(completeButton);

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

        List<Activity> activities = crmService.getAllActivities();
        String typeSelection = (String) typeFilter.getSelectedItem();
        String statusSelection = (String) statusFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (Activity a : activities) {
            // Filter by type
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(a.getType())) {
                continue;
            }

            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(a.getStatus())) {
                continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = a.getSubject().toLowerCase().contains(searchTerm);
                if (!matches) continue;
            }

            String relatedTo = a.getRelatedType() != null ? a.getRelatedType() + " #" + a.getRelatedId() : "";

            tableModel.addRow(new Object[]{
                a.getActivityId(),
                a.getType(),
                a.getSubject(),
                relatedTo,
                a.getDueDate() != null ? a.getDueDate().format(DATE_FORMAT) : "",
                a.getPriority(),
                a.getStatus(),
                a.getAssignedToName() != null ? a.getAssignedToName() : "Unassigned"
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        List<Activity> all = crmService.getAllActivities();

        int total = all.size();
        long planned = all.stream().filter(a -> "PLANNED".equals(a.getStatus())).count();
        long overdue = all.stream().filter(Activity::isOverdue).count();
        long completed = all.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();

        totalActivitiesLabel.setText(String.valueOf(total));
        plannedLabel.setText(String.valueOf(planned));
        overdueLabel.setText(String.valueOf(overdue));
        completedLabel.setText(String.valueOf(completed));
    }

    private void updateButtonStates() {
        int row = activitiesTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);

        if (hasSelection) {
            String status = (String) tableModel.getValueAt(row, 6);
            completeButton.setEnabled(!"COMPLETED".equals(status) && !"CANCELLED".equals(status));
        } else {
            completeButton.setEnabled(false);
        }
    }

    private void addActivity() {
        showActivityDialog(null, "Add New Activity");
    }

    private void editActivity() {
        int row = activitiesTable.getSelectedRow();
        if (row < 0) return;

        int activityId = (int) tableModel.getValueAt(row, 0);
        Activity activity = crmService.getActivityById(activityId);

        if (activity != null) {
            showActivityDialog(activity, "Edit Activity");
        }
    }

    private void completeActivity() {
        int row = activitiesTable.getSelectedRow();
        if (row < 0) return;

        int activityId = (int) tableModel.getValueAt(row, 0);
        String subject = (String) tableModel.getValueAt(row, 2);

        String outcome = JOptionPane.showInputDialog(this,
            "Complete activity '" + subject + "'\n\nEnter outcome/result:",
            "");

        if (outcome != null) {
            if (crmService.completeActivity(activityId, outcome.trim())) {
                UIHelper.showSuccess(this, "Activity completed.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to complete activity.");
            }
        }
    }

    private void showActivityDialog(Activity existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"CALL", "MEETING", "EMAIL", "TASK", "NOTE"});
        JTextField subjectField = new JTextField(existing != null ? existing.getSubject() : "");
        JTextArea descArea = new JTextArea(existing != null && existing.getDescription() != null ? existing.getDescription() : "");
        descArea.setRows(2);

        JComboBox<String> relatedTypeCombo = new JComboBox<>(new String[]{"", "CUSTOMER", "LEAD", "OPPORTUNITY", "CONTACT"});
        JSpinner relatedIdSpinner = new JSpinner(new SpinnerNumberModel(
            existing != null ? existing.getRelatedId() : 0, 0, 10000, 1));

        // Due date (simplified)
        JTextField dueDateField = new JTextField(
            existing != null && existing.getDueDate() != null ?
            existing.getDueDate().format(DATE_FORMAT) :
            LocalDateTime.now().plusDays(1).format(DATE_FORMAT));

        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"});

        if (existing != null) {
            typeCombo.setSelectedItem(existing.getType());
            relatedTypeCombo.setSelectedItem(existing.getRelatedType() != null ? existing.getRelatedType() : "");
            priorityCombo.setSelectedItem(existing.getPriority());
            statusCombo.setSelectedItem(existing.getStatus());
        }

        JTextField locationField = new JTextField(existing != null && existing.getLocation() != null ? existing.getLocation() : "");

        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Related Type:"));
        formPanel.add(relatedTypeCombo);
        formPanel.add(new JLabel("Related ID:"));
        formPanel.add(relatedIdSpinner);
        formPanel.add(new JLabel("Due Date (yyyy-MM-dd HH:mm):"));
        formPanel.add(dueDateField);
        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityCombo);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            // Validation
            if (subjectField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Subject is required.");
                return;
            }

            // Parse due date
            LocalDateTime dueDate;
            try {
                dueDate = LocalDateTime.parse(dueDateField.getText().trim(), DATE_FORMAT);
            } catch (Exception ex) {
                UIHelper.showError(dialog, "Invalid date format. Use yyyy-MM-dd HH:mm.");
                return;
            }

            if (existing == null) {
                Activity newActivity = new Activity();
                newActivity.setType((String) typeCombo.getSelectedItem());
                newActivity.setSubject(subjectField.getText().trim());
                newActivity.setDescription(descArea.getText().trim());
                String relType = (String) relatedTypeCombo.getSelectedItem();
                if (relType != null && !relType.isEmpty()) {
                    newActivity.setRelatedType(relType);
                    newActivity.setRelatedId((Integer) relatedIdSpinner.getValue());
                }
                newActivity.setDueDate(dueDate);
                newActivity.setPriority((String) priorityCombo.getSelectedItem());
                newActivity.setStatus((String) statusCombo.getSelectedItem());
                newActivity.setLocation(locationField.getText().trim());
                crmService.createActivity(newActivity);
            } else {
                existing.setType((String) typeCombo.getSelectedItem());
                existing.setSubject(subjectField.getText().trim());
                existing.setDescription(descArea.getText().trim());
                String relType = (String) relatedTypeCombo.getSelectedItem();
                if (relType != null && !relType.isEmpty()) {
                    existing.setRelatedType(relType);
                    existing.setRelatedId((Integer) relatedIdSpinner.getValue());
                } else {
                    existing.setRelatedType(null);
                    existing.setRelatedId(0);
                }
                existing.setDueDate(dueDate);
                existing.setPriority((String) priorityCombo.getSelectedItem());
                existing.setStatus((String) statusCombo.getSelectedItem());
                existing.setLocation(locationField.getText().trim());
                crmService.updateActivity(existing);
            }

            UIHelper.showSuccess(dialog, "Activity saved successfully.");
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

    // Type cell renderer
    private static class TypeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String type = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (type) {
                        case "CALL":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "MEETING":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
                            break;
                        case "EMAIL":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "TASK":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "NOTE":
                            setBackground(new Color(230, 230, 230));
                            setForeground(new Color(80, 80, 80));
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
                        case "PLANNED":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "IN_PROGRESS":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "COMPLETED":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
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
            }
            return this;
        }
    }
}
