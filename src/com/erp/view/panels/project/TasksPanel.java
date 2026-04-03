package com.erp.view.panels.project;

import com.erp.model.Project;
import com.erp.model.ProjectTask;
import com.erp.service.mock.MockProjectService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.TaskDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TasksPanel displays and manages tasks across all projects.
 */
public class TasksPanel extends JPanel {

    private MockProjectService projectService;

    private JTable tasksTable;
    private DefaultTableModel tableModel;

    private JComboBox<ProjectItem> projectFilter;
    private JComboBox<String> statusFilter;

    private JLabel totalTasksLabel;
    private JLabel inProgressLabel;
    private JLabel overdueLabel;

    private static final String[] COLUMNS = {"ID", "Task Name", "Project", "Status", "Priority", "Assignee", "Due Date", "Progress"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TasksPanel() {
        projectService = MockProjectService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Project filter
        projectFilter = new JComboBox<>();
        projectFilter.setFont(Constants.FONT_REGULAR);
        projectFilter.addItem(new ProjectItem(null)); // All projects
        for (Project p : projectService.getAllProjects()) {
            projectFilter.addItem(new ProjectItem(p));
        }
        projectFilter.addActionListener(e -> loadData());

        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Status", "TODO", "IN_PROGRESS", "REVIEW", "COMPLETED", "BLOCKED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tasksTable = new JTable(tableModel);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(tasksTable);

        // Column widths
        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Custom renderers
        tasksTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        tasksTable.getColumnModel().getColumn(4).setCellRenderer(new PriorityCellRenderer());
        tasksTable.getColumnModel().getColumn(7).setCellRenderer(new ProgressCellRenderer());

        // Summary labels
        totalTasksLabel = createSummaryValue("0");
        inProgressLabel = createSummaryValue("0");
        overdueLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Tasks", totalTasksLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("In Progress", inProgressLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Overdue", overdueLabel, Constants.DANGER_COLOR));

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

        toolbar.add(new JLabel("Project:"));
        projectFilter.setPreferredSize(new Dimension(180, 25));
        toolbar.add(projectFilter);

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        JButton addBtn = UIHelper.createPrimaryButton("New Task");
        addBtn.setPreferredSize(new Dimension(100, 30));
        addBtn.addActionListener(e -> addTask());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editTask());
        toolbar.add(editBtn);

        JButton completeBtn = new JButton("Complete");
        completeBtn.setFont(Constants.FONT_BUTTON);
        completeBtn.setBackground(Constants.SUCCESS_COLOR);
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setOpaque(true);
        completeBtn.setBorderPainted(false);
        completeBtn.setFocusPainted(false);
        completeBtn.setPreferredSize(new Dimension(90, 30));
        completeBtn.addActionListener(e -> completeTask());
        toolbar.add(completeBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        ProjectItem selectedProject = (ProjectItem) projectFilter.getSelectedItem();
        int projectId = selectedProject != null && selectedProject.getProject() != null ?
                selectedProject.getProject().getProjectId() : 0;

        List<ProjectTask> tasks;
        if (projectId > 0) {
            tasks = projectService.getTasksByProject(projectId);
        } else {
            tasks = projectService.getAllTasks();
        }

        String statusSelection = (String) statusFilter.getSelectedItem();

        int totalCount = 0;
        int inProgressCount = 0;
        int overdueCount = 0;

        for (ProjectTask t : tasks) {
            if (!"All Status".equals(statusSelection) && !statusSelection.equals(t.getStatus())) {
                continue;
            }

            totalCount++;
            if ("IN_PROGRESS".equals(t.getStatus())) inProgressCount++;
            if (t.isOverdue()) overdueCount++;

            Project project = projectService.getProjectById(t.getProjectId());
            String projectName = project != null ? project.getName() : "";

            tableModel.addRow(new Object[]{
                t.getTaskId(),
                t.getName(),
                projectName,
                t.getStatus(),
                t.getPriority(),
                "Employee " + t.getAssignedToId(),
                t.getDueDate() != null ? t.getDueDate().format(DATE_FORMAT) : "",
                t.getPercentComplete() + "%"
            });
        }

        totalTasksLabel.setText(String.valueOf(totalCount));
        inProgressLabel.setText(String.valueOf(inProgressCount));
        overdueLabel.setText(String.valueOf(overdueCount));
        overdueLabel.setForeground(overdueCount > 0 ? Constants.DANGER_COLOR : Constants.PRIMARY_COLOR);
    }

    private void addTask() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        TaskDialog dialog = new TaskDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ProjectTask newTask = dialog.getTask();
            projectService.createTask(newTask);
            UIHelper.showSuccess(this, "Task created successfully.");
            loadData();
        }
    }

    private void editTask() {
        int row = tasksTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a task to edit.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(row, 0);
        ProjectTask task = projectService.getTaskById(taskId);

        if (task != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            TaskDialog dialog = new TaskDialog(parentFrame, task);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                projectService.updateTask(dialog.getTask());
                UIHelper.showSuccess(this, "Task updated successfully.");
                loadData();
            }
        }
    }

    private void completeTask() {
        int row = tasksTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a task to complete.");
            return;
        }

        int taskId = (int) tableModel.getValueAt(row, 0);
        String taskName = (String) tableModel.getValueAt(row, 1);

        boolean confirm = UIHelper.showConfirm(this, "Mark task '" + taskName + "' as completed?");
        if (confirm) {
            if (projectService.updateTaskStatus(taskId, "COMPLETED")) {
                UIHelper.showSuccess(this, "Task completed successfully.");
                loadData();
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Helper class for project filter combo
    private static class ProjectItem {
        private Project project;

        public ProjectItem(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        @Override
        public String toString() {
            return project == null ? "All Projects" : project.getProjectCode() + " - " + project.getName();
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
                    case "COMPLETED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "IN_PROGRESS":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "TODO":
                        setBackground(new Color(226, 232, 240));
                        setForeground(new Color(71, 85, 105));
                        break;
                    case "REVIEW":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "BLOCKED":
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

    // Priority cell renderer
    private static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String priority = value.toString();
                switch (priority) {
                    case "CRITICAL":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "HIGH":
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

    // Progress cell renderer
    private static class ProgressCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}
