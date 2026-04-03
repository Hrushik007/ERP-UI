package com.erp.view.dialogs;

import com.erp.model.Project;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * ProjectDialog for creating and editing projects.
 */
public class ProjectDialog extends JDialog {

    private JTextField projectCodeField;
    private JTextField projectNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> statusCombo;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> categoryCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextField budgetField;

    private boolean confirmed = false;
    private Project project;

    private static final String[] STATUSES = {"PLANNED", "IN_PROGRESS", "ON_HOLD", "COMPLETED", "CANCELLED"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
    private static final String[] CATEGORIES = {"INTERNAL", "CLIENT", "R&D", "MAINTENANCE"};

    public ProjectDialog(Frame parent, Project existingProject) {
        super(parent, existingProject == null ? "New Project" : "Edit Project", true);
        this.project = existingProject;

        initializeComponents();
        layoutComponents();

        if (existingProject != null) {
            populateFields(existingProject);
        } else {
            // Generate project code for new projects
            projectCodeField.setText("PRJ-" + System.currentTimeMillis() % 10000);
        }

        setSize(500, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        projectCodeField = new JTextField(20);
        projectCodeField.setFont(Constants.FONT_REGULAR);

        projectNameField = new JTextField(20);
        projectNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(Constants.FONT_REGULAR);

        priorityCombo = new JComboBox<>(PRIORITIES);
        priorityCombo.setSelectedItem("MEDIUM");
        priorityCombo.setFont(Constants.FONT_REGULAR);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(new Date());
        startDateSpinner.setFont(Constants.FONT_REGULAR);

        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        // Default end date: 3 months from now
        endDateSpinner.setValue(Date.from(LocalDate.now().plusMonths(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateSpinner.setFont(Constants.FONT_REGULAR);

        budgetField = new JTextField(20);
        budgetField.setFont(Constants.FONT_REGULAR);
        budgetField.setText("0.00");
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

        // Project Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Project Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(projectCodeField, gbc);

        // Project Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Project Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(projectNameField, gbc);

        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

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

        // End Date
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("End Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(endDateSpinner, gbc);

        // Budget
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Budget:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel budgetPanel = new JPanel(new BorderLayout(5, 0));
        budgetPanel.setBackground(Constants.BG_WHITE);
        budgetPanel.add(new JLabel("$"), BorderLayout.WEST);
        budgetPanel.add(budgetField, BorderLayout.CENTER);
        formPanel.add(budgetPanel, gbc);

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
        saveBtn.addActionListener(e -> saveProject());

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

    private void populateFields(Project p) {
        projectCodeField.setText(p.getProjectCode());
        projectCodeField.setEditable(false);
        projectNameField.setText(p.getName());
        descriptionArea.setText(p.getDescription() != null ? p.getDescription() : "");
        statusCombo.setSelectedItem(p.getStatus());
        priorityCombo.setSelectedItem(p.getPriority());
        categoryCombo.setSelectedItem(p.getCategory());

        if (p.getStartDate() != null) {
            startDateSpinner.setValue(Date.from(p.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (p.getEndDate() != null) {
            endDateSpinner.setValue(Date.from(p.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (p.getBudgetAmount() != null) {
            budgetField.setText(p.getBudgetAmount().toString());
        }
    }

    private void saveProject() {
        // Validation
        if (projectCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            projectCodeField.requestFocus();
            return;
        }

        if (projectNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            projectNameField.requestFocus();
            return;
        }

        BigDecimal budget = BigDecimal.ZERO;
        try {
            if (!budgetField.getText().trim().isEmpty()) {
                budget = new BigDecimal(budgetField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid budget amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            budgetField.requestFocus();
            return;
        }

        // Create or update project
        if (project == null) {
            project = new Project();
        }

        project.setProjectCode(projectCodeField.getText().trim());
        project.setName(projectNameField.getText().trim());
        project.setDescription(descriptionArea.getText().trim());
        project.setStatus((String) statusCombo.getSelectedItem());
        project.setPriority((String) priorityCombo.getSelectedItem());
        project.setCategory((String) categoryCombo.getSelectedItem());
        project.setBudgetAmount(budget);

        Date startDate = (Date) startDateSpinner.getValue();
        project.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        Date endDate = (Date) endDateSpinner.getValue();
        project.setEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Project getProject() {
        return project;
    }
}
