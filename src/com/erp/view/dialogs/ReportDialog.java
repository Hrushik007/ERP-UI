package com.erp.view.dialogs;

import com.erp.model.Report;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ReportDialog for creating and editing report definitions.
 */
public class ReportDialog extends JDialog {

    private JTextField reportCodeField;
    private JTextField reportNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> typeCombo;
    private JComboBox<String> formatCombo;
    private JCheckBox activeCheckbox;

    private boolean confirmed = false;
    private Report report;

    private static final String[] CATEGORIES = {"SALES", "INVENTORY", "FINANCIAL", "HR", "PROJECT", "CUSTOM"};
    private static final String[] TYPES = {"TABLE", "CHART", "SUMMARY", "DETAILED"};
    private static final String[] FORMATS = {"PDF", "EXCEL", "CSV", "HTML"};

    public ReportDialog(Frame parent, Report existingReport) {
        super(parent, existingReport == null ? "New Report" : "Edit Report", true);
        this.report = existingReport;

        initializeComponents();
        layoutComponents();

        if (existingReport != null) {
            populateFields(existingReport);
        } else {
            reportCodeField.setText("RPT-" + System.currentTimeMillis() % 10000);
        }

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        reportCodeField = new JTextField(20);
        reportCodeField.setFont(Constants.FONT_REGULAR);

        reportNameField = new JTextField(20);
        reportNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        typeCombo = new JComboBox<>(TYPES);
        typeCombo.setFont(Constants.FONT_REGULAR);

        formatCombo = new JComboBox<>(FORMATS);
        formatCombo.setFont(Constants.FONT_REGULAR);

        activeCheckbox = new JCheckBox("Active", true);
        activeCheckbox.setFont(Constants.FONT_REGULAR);
        activeCheckbox.setBackground(Constants.BG_WHITE);
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

        // Report Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Report Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(reportCodeField, gbc);

        // Report Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Report Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(reportNameField, gbc);

        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

        // Type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Report Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(typeCombo, gbc);

        // Format
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Output Format:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(formatCombo, gbc);

        // Active
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel(""), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(activeCheckbox, gbc);

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
        saveBtn.addActionListener(e -> saveReport());

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

    private void populateFields(Report r) {
        reportCodeField.setText(r.getReportCode());
        reportCodeField.setEditable(false);
        reportNameField.setText(r.getName());
        descriptionArea.setText(r.getDescription() != null ? r.getDescription() : "");
        categoryCombo.setSelectedItem(r.getCategory());
        typeCombo.setSelectedItem(r.getReportType());
        formatCombo.setSelectedItem(r.getOutputFormat());
        activeCheckbox.setSelected(r.isActive());
    }

    private void saveReport() {
        // Validation
        if (reportCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Report code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            reportCodeField.requestFocus();
            return;
        }

        if (reportNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Report name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            reportNameField.requestFocus();
            return;
        }

        // Create or update report
        if (report == null) {
            report = new Report();
        }

        report.setReportCode(reportCodeField.getText().trim());
        report.setName(reportNameField.getText().trim());
        report.setDescription(descriptionArea.getText().trim());
        report.setCategory((String) categoryCombo.getSelectedItem());
        report.setReportType((String) typeCombo.getSelectedItem());
        report.setOutputFormat((String) formatCombo.getSelectedItem());
        report.setActive(activeCheckbox.isSelected());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Report getReport() {
        return report;
    }
}
