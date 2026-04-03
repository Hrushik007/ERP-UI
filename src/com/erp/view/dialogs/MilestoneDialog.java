package com.erp.view.dialogs;

import com.erp.model.Milestone;
import com.erp.model.Project;
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
 * MilestoneDialog for creating and editing project milestones.
 */
public class MilestoneDialog extends JDialog {

    private JComboBox<ProjectItem> projectCombo;
    private JTextField milestoneNameField;
    private JTextArea descriptionArea;
    private JSpinner targetDateSpinner;
    private JComboBox<String> statusCombo;
    private JCheckBox criticalCheckbox;

    private boolean confirmed = false;
    private Milestone milestone;
    private MockProjectService projectService;

    private static final String[] STATUSES = {"PENDING", "COMPLETED", "MISSED"};

    public MilestoneDialog(Frame parent, Milestone existingMilestone) {
        super(parent, existingMilestone == null ? "New Milestone" : "Edit Milestone", true);
        this.milestone = existingMilestone;
        this.projectService = MockProjectService.getInstance();

        initializeComponents();
        layoutComponents();

        if (existingMilestone != null) {
            populateFields(existingMilestone);
        }

        setSize(500, 400);
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

        milestoneNameField = new JTextField(20);
        milestoneNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        targetDateSpinner = new JSpinner(new SpinnerDateModel());
        targetDateSpinner.setEditor(new JSpinner.DateEditor(targetDateSpinner, "yyyy-MM-dd"));
        targetDateSpinner.setValue(Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        targetDateSpinner.setFont(Constants.FONT_REGULAR);

        statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(Constants.FONT_REGULAR);

        criticalCheckbox = new JCheckBox("Critical Milestone", false);
        criticalCheckbox.setFont(Constants.FONT_REGULAR);
        criticalCheckbox.setBackground(Constants.BG_WHITE);
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

        // Milestone Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Milestone Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(milestoneNameField, gbc);

        // Target Date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Target Date:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(targetDateSpinner, gbc);

        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusCombo, gbc);

        // Critical
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel(""), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(criticalCheckbox, gbc);

        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(200, 70));
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
        saveBtn.addActionListener(e -> saveMilestone());

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

    private void populateFields(Milestone m) {
        // Select the project
        for (int i = 0; i < projectCombo.getItemCount(); i++) {
            if (projectCombo.getItemAt(i).getProject().getProjectId() == m.getProjectId()) {
                projectCombo.setSelectedIndex(i);
                break;
            }
        }

        milestoneNameField.setText(m.getName());
        descriptionArea.setText(m.getDescription() != null ? m.getDescription() : "");
        statusCombo.setSelectedItem(m.getStatus());
        criticalCheckbox.setSelected(m.isCritical());

        if (m.getTargetDate() != null) {
            targetDateSpinner.setValue(Date.from(m.getTargetDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    private void saveMilestone() {
        // Validation
        if (projectCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a project.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (milestoneNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Milestone name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            milestoneNameField.requestFocus();
            return;
        }

        // Create or update milestone
        if (milestone == null) {
            milestone = new Milestone();
        }

        ProjectItem selectedProject = (ProjectItem) projectCombo.getSelectedItem();
        milestone.setProjectId(selectedProject.getProject().getProjectId());
        milestone.setName(milestoneNameField.getText().trim());
        milestone.setDescription(descriptionArea.getText().trim());
        milestone.setStatus((String) statusCombo.getSelectedItem());
        milestone.setCritical(criticalCheckbox.isSelected());

        Date targetDate = (Date) targetDateSpinner.getValue();
        milestone.setTargetDate(targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        if ("COMPLETED".equals(milestone.getStatus()) && milestone.getCompletedDate() == null) {
            milestone.setCompletedDate(LocalDate.now());
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Milestone getMilestone() {
        return milestone;
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
