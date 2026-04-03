package com.erp.view.dialogs;

import com.erp.model.ChartConfig;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ChartDialog for creating and editing chart configurations.
 */
public class ChartDialog extends JDialog {

    private JTextField chartCodeField;
    private JTextField chartNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> chartTypeCombo;
    private JComboBox<String> categoryCombo;
    private JTextField xAxisField;
    private JTextField yAxisField;
    private JComboBox<String> timeRangeCombo;
    private JCheckBox showLegendCheckbox;
    private JCheckBox showLabelsCheckbox;
    private JCheckBox showGridCheckbox;
    private JCheckBox activeCheckbox;

    private boolean confirmed = false;
    private ChartConfig chartConfig;

    private static final String[] CHART_TYPES = {"BAR", "LINE", "PIE", "DOUGHNUT", "AREA", "SCATTER", "GAUGE", "TABLE"};
    private static final String[] CATEGORIES = {"SALES", "FINANCIAL", "OPERATIONS", "HR", "INVENTORY", "PROJECT", "CUSTOMER"};
    private static final String[] TIME_RANGES = {"LAST_7_DAYS", "LAST_30_DAYS", "LAST_90_DAYS", "THIS_YEAR", "CUSTOM"};

    public ChartDialog(Frame parent, ChartConfig existingChart) {
        super(parent, existingChart == null ? "New Chart" : "Edit Chart", true);
        this.chartConfig = existingChart;

        initializeComponents();
        layoutComponents();

        if (existingChart != null) {
            populateFields(existingChart);
        } else {
            chartCodeField.setText("CHT-" + System.currentTimeMillis() % 10000);
        }

        setSize(500, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        chartCodeField = new JTextField(20);
        chartCodeField.setFont(Constants.FONT_REGULAR);

        chartNameField = new JTextField(20);
        chartNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(2, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        chartTypeCombo = new JComboBox<>(CHART_TYPES);
        chartTypeCombo.setFont(Constants.FONT_REGULAR);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        xAxisField = new JTextField(20);
        xAxisField.setFont(Constants.FONT_REGULAR);

        yAxisField = new JTextField(20);
        yAxisField.setFont(Constants.FONT_REGULAR);

        timeRangeCombo = new JComboBox<>(TIME_RANGES);
        timeRangeCombo.setSelectedItem("LAST_30_DAYS");
        timeRangeCombo.setFont(Constants.FONT_REGULAR);

        showLegendCheckbox = new JCheckBox("Show Legend", true);
        showLegendCheckbox.setFont(Constants.FONT_REGULAR);
        showLegendCheckbox.setBackground(Constants.BG_WHITE);

        showLabelsCheckbox = new JCheckBox("Show Labels", true);
        showLabelsCheckbox.setFont(Constants.FONT_REGULAR);
        showLabelsCheckbox.setBackground(Constants.BG_WHITE);

        showGridCheckbox = new JCheckBox("Show Grid", true);
        showGridCheckbox.setFont(Constants.FONT_REGULAR);
        showGridCheckbox.setBackground(Constants.BG_WHITE);

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

        // Chart Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Chart Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(chartCodeField, gbc);

        // Chart Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Chart Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(chartNameField, gbc);

        // Chart Type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Chart Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(chartTypeCombo, gbc);

        // Category
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

        // X-Axis
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("X-Axis Field:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(xAxisField, gbc);

        // Y-Axis
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Y-Axis Field:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(yAxisField, gbc);

        // Time Range
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Time Range:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(timeRangeCombo, gbc);

        // Options panel
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Display Options:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        optionsPanel.setBackground(Constants.BG_WHITE);
        optionsPanel.add(showLegendCheckbox);
        optionsPanel.add(showLabelsCheckbox);
        optionsPanel.add(showGridCheckbox);
        optionsPanel.add(activeCheckbox);
        formPanel.add(optionsPanel, gbc);

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
        saveBtn.addActionListener(e -> saveChart());

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

    private void populateFields(ChartConfig c) {
        chartCodeField.setText(c.getChartCode());
        chartCodeField.setEditable(false);
        chartNameField.setText(c.getName());
        descriptionArea.setText(c.getDescription() != null ? c.getDescription() : "");
        chartTypeCombo.setSelectedItem(c.getChartType());
        categoryCombo.setSelectedItem(c.getCategory());
        xAxisField.setText(c.getXAxis() != null ? c.getXAxis() : "");
        yAxisField.setText(c.getYAxis() != null ? c.getYAxis() : "");
        timeRangeCombo.setSelectedItem(c.getTimeRange());
        showLegendCheckbox.setSelected(c.isShowLegend());
        showLabelsCheckbox.setSelected(c.isShowLabels());
        showGridCheckbox.setSelected(c.isShowGrid());
        activeCheckbox.setSelected(c.isActive());
    }

    private void saveChart() {
        // Validation
        if (chartCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chart code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            chartCodeField.requestFocus();
            return;
        }

        if (chartNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chart name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            chartNameField.requestFocus();
            return;
        }

        // Create or update chart
        if (chartConfig == null) {
            chartConfig = new ChartConfig();
        }

        chartConfig.setChartCode(chartCodeField.getText().trim());
        chartConfig.setName(chartNameField.getText().trim());
        chartConfig.setDescription(descriptionArea.getText().trim());
        chartConfig.setChartType((String) chartTypeCombo.getSelectedItem());
        chartConfig.setCategory((String) categoryCombo.getSelectedItem());
        chartConfig.setXAxis(xAxisField.getText().trim());
        chartConfig.setYAxis(yAxisField.getText().trim());
        chartConfig.setTimeRange((String) timeRangeCombo.getSelectedItem());
        chartConfig.setShowLegend(showLegendCheckbox.isSelected());
        chartConfig.setShowLabels(showLabelsCheckbox.isSelected());
        chartConfig.setShowGrid(showGridCheckbox.isSelected());
        chartConfig.setActive(activeCheckbox.isSelected());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ChartConfig getChartConfig() {
        return chartConfig;
    }
}
