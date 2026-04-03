package com.erp.view.dialogs;

import com.erp.model.Dashboard;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DashboardDialog for creating and editing analytics dashboards.
 */
public class DashboardDialog extends JDialog {

    private JTextField dashboardCodeField;
    private JTextField dashboardNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> layoutCombo;
    private JSpinner columnsSpinner;
    private JSpinner refreshIntervalSpinner;
    private JCheckBox publicCheckbox;

    private boolean confirmed = false;
    private Dashboard dashboard;

    private static final String[] CATEGORIES = {"EXECUTIVE", "SALES", "OPERATIONS", "FINANCIAL", "HR", "CUSTOM"};
    private static final String[] LAYOUTS = {"GRID", "FREEFORM"};

    public DashboardDialog(Frame parent, Dashboard existingDashboard) {
        super(parent, existingDashboard == null ? "New Dashboard" : "Edit Dashboard", true);
        this.dashboard = existingDashboard;

        initializeComponents();
        layoutComponents();

        if (existingDashboard != null) {
            populateFields(existingDashboard);
        } else {
            dashboardCodeField.setText("DASH-" + System.currentTimeMillis() % 10000);
        }

        setSize(500, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        dashboardCodeField = new JTextField(20);
        dashboardCodeField.setFont(Constants.FONT_REGULAR);

        dashboardNameField = new JTextField(20);
        dashboardNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        layoutCombo = new JComboBox<>(LAYOUTS);
        layoutCombo.setFont(Constants.FONT_REGULAR);

        columnsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        columnsSpinner.setFont(Constants.FONT_REGULAR);

        refreshIntervalSpinner = new JSpinner(new SpinnerNumberModel(300, 0, 3600, 60));
        refreshIntervalSpinner.setFont(Constants.FONT_REGULAR);

        publicCheckbox = new JCheckBox("Public Dashboard", false);
        publicCheckbox.setFont(Constants.FONT_REGULAR);
        publicCheckbox.setBackground(Constants.BG_WHITE);
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

        // Dashboard Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Dashboard Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dashboardCodeField, gbc);

        // Dashboard Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Dashboard Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dashboardNameField, gbc);

        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

        // Layout
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Layout:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(layoutCombo, gbc);

        // Columns
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Columns:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(columnsSpinner, gbc);

        // Refresh Interval
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Refresh (seconds):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(refreshIntervalSpinner, gbc);

        // Public
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel(""), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(publicCheckbox, gbc);

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
        saveBtn.addActionListener(e -> saveDashboard());

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

    private void populateFields(Dashboard d) {
        dashboardCodeField.setText(d.getDashboardCode());
        dashboardCodeField.setEditable(false);
        dashboardNameField.setText(d.getName());
        descriptionArea.setText(d.getDescription() != null ? d.getDescription() : "");
        categoryCombo.setSelectedItem(d.getCategory());
        layoutCombo.setSelectedItem(d.getLayout());
        columnsSpinner.setValue(d.getColumns());
        refreshIntervalSpinner.setValue(d.getRefreshInterval());
        publicCheckbox.setSelected(d.isPublic());
    }

    private void saveDashboard() {
        // Validation
        if (dashboardCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dashboard code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            dashboardCodeField.requestFocus();
            return;
        }

        if (dashboardNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dashboard name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            dashboardNameField.requestFocus();
            return;
        }

        // Create or update dashboard
        if (dashboard == null) {
            dashboard = new Dashboard();
        }

        dashboard.setDashboardCode(dashboardCodeField.getText().trim());
        dashboard.setName(dashboardNameField.getText().trim());
        dashboard.setDescription(descriptionArea.getText().trim());
        dashboard.setCategory((String) categoryCombo.getSelectedItem());
        dashboard.setLayout((String) layoutCombo.getSelectedItem());
        dashboard.setColumns((Integer) columnsSpinner.getValue());
        dashboard.setRefreshInterval((Integer) refreshIntervalSpinner.getValue());
        dashboard.setPublic(publicCheckbox.isSelected());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }
}
