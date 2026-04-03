package com.erp.view.dialogs;

import com.erp.model.KPI;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

/**
 * KPIDialog for creating and editing Key Performance Indicators.
 */
public class KPIDialog extends JDialog {

    private JTextField kpiCodeField;
    private JTextField kpiNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> unitCombo;
    private JTextField targetValueField;
    private JTextField actualValueField;
    private JTextField minThresholdField;
    private JTextField maxThresholdField;
    private JComboBox<String> frequencyCombo;
    private JCheckBox activeCheckbox;

    private boolean confirmed = false;
    private KPI kpi;

    private static final String[] CATEGORIES = {"SALES", "FINANCIAL", "OPERATIONS", "HR", "CUSTOMER", "QUALITY"};
    private static final String[] UNITS = {"CURRENCY", "PERCENTAGE", "NUMBER", "TIME", "RATIO"};
    private static final String[] FREQUENCIES = {"DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"};

    public KPIDialog(Frame parent, KPI existingKPI) {
        super(parent, existingKPI == null ? "New KPI" : "Edit KPI", true);
        this.kpi = existingKPI;

        initializeComponents();
        layoutComponents();

        if (existingKPI != null) {
            populateFields(existingKPI);
        } else {
            kpiCodeField.setText("KPI-" + System.currentTimeMillis() % 10000);
        }

        setSize(500, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        kpiCodeField = new JTextField(20);
        kpiCodeField.setFont(Constants.FONT_REGULAR);

        kpiNameField = new JTextField(20);
        kpiNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(2, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        unitCombo = new JComboBox<>(UNITS);
        unitCombo.setFont(Constants.FONT_REGULAR);

        targetValueField = new JTextField(15);
        targetValueField.setFont(Constants.FONT_REGULAR);
        targetValueField.setText("0");

        actualValueField = new JTextField(15);
        actualValueField.setFont(Constants.FONT_REGULAR);
        actualValueField.setText("0");

        minThresholdField = new JTextField(15);
        minThresholdField.setFont(Constants.FONT_REGULAR);
        minThresholdField.setText("0");

        maxThresholdField = new JTextField(15);
        maxThresholdField.setFont(Constants.FONT_REGULAR);
        maxThresholdField.setText("0");

        frequencyCombo = new JComboBox<>(FREQUENCIES);
        frequencyCombo.setSelectedItem("MONTHLY");
        frequencyCombo.setFont(Constants.FONT_REGULAR);

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

        // KPI Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("KPI Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(kpiCodeField, gbc);

        // KPI Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("KPI Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(kpiNameField, gbc);

        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

        // Unit
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Unit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(unitCombo, gbc);

        // Target Value
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Target Value:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(targetValueField, gbc);

        // Actual Value
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Actual Value:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(actualValueField, gbc);

        // Min Threshold
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Min Threshold:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(minThresholdField, gbc);

        // Max Threshold
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Max Threshold:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(maxThresholdField, gbc);

        // Frequency
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Frequency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(frequencyCombo, gbc);

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
        descScroll.setPreferredSize(new Dimension(200, 50));
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
        saveBtn.addActionListener(e -> saveKPI());

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

    private void populateFields(KPI k) {
        kpiCodeField.setText(k.getKpiCode());
        kpiCodeField.setEditable(false);
        kpiNameField.setText(k.getName());
        descriptionArea.setText(k.getDescription() != null ? k.getDescription() : "");
        categoryCombo.setSelectedItem(k.getCategory());
        unitCombo.setSelectedItem(k.getUnit());
        targetValueField.setText(k.getTargetValue() != null ? k.getTargetValue().toString() : "0");
        actualValueField.setText(k.getActualValue() != null ? k.getActualValue().toString() : "0");
        minThresholdField.setText(k.getMinThreshold() != null ? k.getMinThreshold().toString() : "0");
        maxThresholdField.setText(k.getMaxThreshold() != null ? k.getMaxThreshold().toString() : "0");
        frequencyCombo.setSelectedItem(k.getFrequency());
        activeCheckbox.setSelected(k.isActive());
    }

    private void saveKPI() {
        // Validation
        if (kpiCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "KPI code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            kpiCodeField.requestFocus();
            return;
        }

        if (kpiNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "KPI name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            kpiNameField.requestFocus();
            return;
        }

        BigDecimal targetValue;
        BigDecimal actualValue;
        BigDecimal minThreshold;
        BigDecimal maxThreshold;

        try {
            targetValue = new BigDecimal(targetValueField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid target value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            targetValueField.requestFocus();
            return;
        }

        try {
            actualValue = new BigDecimal(actualValueField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid actual value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            actualValueField.requestFocus();
            return;
        }

        try {
            minThreshold = new BigDecimal(minThresholdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid min threshold.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            minThresholdField.requestFocus();
            return;
        }

        try {
            maxThreshold = new BigDecimal(maxThresholdField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid max threshold.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            maxThresholdField.requestFocus();
            return;
        }

        // Create or update KPI
        if (kpi == null) {
            kpi = new KPI();
        }

        kpi.setKpiCode(kpiCodeField.getText().trim());
        kpi.setName(kpiNameField.getText().trim());
        kpi.setDescription(descriptionArea.getText().trim());
        kpi.setCategory((String) categoryCombo.getSelectedItem());
        kpi.setUnit((String) unitCombo.getSelectedItem());
        kpi.setTargetValue(targetValue);
        kpi.setActualValue(actualValue);
        kpi.setMinThreshold(minThreshold);
        kpi.setMaxThreshold(maxThreshold);
        kpi.setFrequency((String) frequencyCombo.getSelectedItem());
        kpi.setActive(activeCheckbox.isSelected());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public KPI getKPI() {
        return kpi;
    }
}
