package com.erp.view.dialogs;

import com.erp.model.Project;
import com.erp.model.ProjectTask;
import com.erp.service.mock.MockProjectService;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * TaskDialog for creating and editing project tasks.
 */
public class TaskDialog extends JDialog {

    private JComboBox<ProjectItem> projectCombo;
    private JTextField taskNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusCombo;
    private JComboBox<String> priorityCombo;
    private JSpinner startDateSpinner;
    private JSpinner dueDateSpinner;
    private JTextField estimatedHoursField;
    private JSlider progressSlider;
    private JLabel progressLabel;

    private boolean confirmed = false;
    private ProjectTask task;
    private MockProjectService projectService;

    private static final String[] STATUSES = {"TODO", "IN_PROGRESS", "REVIEW", "COMPLETED", "BLOCKED"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};

    public TaskDialog(Frame parent, ProjectTask existingTask) {
        super(parent, existingTask == null ? "New Task" : "Edit Task", true);
        this.task = existingTask;
        this.projectService = MockProjectService.getInstance();

        initializeComponents();
        layoutComponents();

        if (existingTask != null) {
            populateFields(existingTask);
        }

        setSize(500, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        // Project combo
        projectCombo = new JComboBox<>();
        projectCombo.setFont(Constants.FONT_REGULAR);
        List<Project> projects = projectService.getAllProjects();
        for (Project p : projects) {
            projectCombo.addItem(new ProjectItem(p));
        }

        taskNameField = new JTextField(20);
        taskNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(Constants.FONT_REGULAR);

        priorityCombo = new JComboBox<>(PRIORITIES);
        priorityCombo.setSelectedItem("MEDIUM");
        priorityCombo.setFont(Constants.FONT_REGULAR);

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(new Date());
        startDateSpinner.setFont(Constants.FONT_REGULAR);

        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd"));
        dueDateSpinner.setValue(Date.from(LocalDate.now().plusWeeks(2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dueDateSpinner.setFont(Constants.FONT_REGULAR);

        estimatedHoursField = new JTextField(10);
        estimatedHoursField.setFont(Constants.FONT_REGULAR);
        estimatedHoursField.setText("8");

        // Progress slider
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setMajorTickSpacing(25);
        progressSlider.setMinorTickSpacing(5);
        progressSlider.setPaintTicks(true);
        progressSlider.setBackground(Constants.BG_WHITE);

        progressLabel = new JLabel("0%");
        progressLabel.setFont(Constants.FONT_REGULAR);

        progressSlider.addChangeListener(e -> {
            progressLabel.setText(progressSlider.getValue() + "%");
        });
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Constants.BG_WHITE);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Constants.BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Project
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Project:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(projectCombo, gbc);

        // Task Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Task Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(taskNameField, gbc);

        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusCombo, gbc);

        // Priority
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Priority:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(priorityCombo, gbc);

        // Start Date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Start Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(startDateSpinner, gbc);

        // Due Date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Due Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dueDateSpinner, gbc);

        // Estimated Hours
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Estimated Hours:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(estimatedHoursField, gbc);

        // Progress
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Progress:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel progressPanel = new JPanel(new BorderLayout(5, 0));
        progressPanel.setBackground(Constants.BG_WHITE);
        progressPanel.add(progressSlider, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.EAST);
        formPanel.add(progressPanel, gbc);

        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(200, 60));
        formPanel.add(descScroll, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Constants.BG_WHITE);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(Constants.FONT_BUTTON);
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(Constants.FONT_BUTTON);
        saveBtn.setBackground(Constants.PRIMARY_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(100, 35));
        saveBtn.addActionListener(e -> saveTask());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        label.setPreferredSize(new Dimension(140, 25));
        label.setMinimumSize(new Dimension(140, 25));
        return label;
    }

    private void populateFields(ProjectTask t) {
        // Select the project
        for (int i = 0; i < projectCombo.getItemCount(); i++) {
            if (projectCombo.getItemAt(i).getProject().getProjectId() == t.getProjectId()) {
                projectCombo.setSelectedIndex(i);
                break;
            }
        }

        taskNameField.setText(t.getName());
        descriptionArea.setText(t.getDescription() != null ? t.getDescription() : "");
        statusCombo.setSelectedItem(t.getStatus());
        priorityCombo.setSelectedItem(t.getPriority());

        if (t.getStartDate() != null) {
            startDateSpinner.setValue(Date.from(t.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (t.getDueDate() != null) {
            dueDateSpinner.setValue(Date.from(t.getDueDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        estimatedHoursField.setText(String.valueOf(t.getEstimatedHours()));
        progressSlider.setValue(t.getPercentComplete());
        progressLabel.setText(t.getPercentComplete() + "%");
    }

    private void saveTask() {
        // Validation
        if (projectCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a project.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (taskNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            taskNameField.requestFocus();
            return;
        }

        int estimatedHours = 0;
        try {
            if (!estimatedHoursField.getText().trim().isEmpty()) {
                estimatedHours = Integer.parseInt(estimatedHoursField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid estimated hours.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            estimatedHoursField.requestFocus();
            return;
        }

        // Create or update task
        if (task == null) {
            task = new ProjectTask();
        }

        ProjectItem selectedProject = (ProjectItem) projectCombo.getSelectedItem();
        task.setProjectId(selectedProject.getProject().getProjectId());
        task.setName(taskNameField.getText().trim());
        task.setDescription(descriptionArea.getText().trim());
        task.setStatus((String) statusCombo.getSelectedItem());
        task.setPriority((String) priorityCombo.getSelectedItem());
        task.setEstimatedHours(estimatedHours);
        task.setPercentComplete(progressSlider.getValue());

        Date startDate = (Date) startDateSpinner.getValue();
        task.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        Date dueDate = (Date) dueDateSpinner.getValue();
        task.setDueDate(dueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if ("COMPLETED".equals(task.getStatus())) {
            task.setPercentComplete(100);
            task.setCompletedDate(LocalDate.now());
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ProjectTask getTask() {
        return task;
    }

    // Helper class for project combo
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
            return project.getProjectCode() + " - " + project.getName();
        }
    }
}
