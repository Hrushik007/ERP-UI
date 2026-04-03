package com.erp.view.dialogs;

import com.erp.model.ReportTemplate;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ReportTemplateDialog for creating and editing report templates.
 */
public class ReportTemplateDialog extends JDialog {

    private JTextField templateCodeField;
    private JTextField templateNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> layoutCombo;
    private JComboBox<String> paperSizeCombo;
    private JCheckBox includeHeaderCheckbox;
    private JCheckBox includeFooterCheckbox;
    private JCheckBox includePageNumbersCheckbox;
    private JCheckBox includeDateStampCheckbox;
    private JCheckBox activeCheckbox;

    private boolean confirmed = false;
    private ReportTemplate template;

    private static final String[] CATEGORIES = {"SALES", "INVENTORY", "FINANCIAL", "HR", "PROJECT", "CUSTOM"};
    private static final String[] LAYOUTS = {"PORTRAIT", "LANDSCAPE"};
    private static final String[] PAPER_SIZES = {"A4", "LETTER", "LEGAL", "A3"};

    public ReportTemplateDialog(Frame parent, ReportTemplate existingTemplate) {
        super(parent, existingTemplate == null ? "New Template" : "Edit Template", true);
        this.template = existingTemplate;

        initializeComponents();
        layoutComponents();

        if (existingTemplate != null) {
            populateFields(existingTemplate);
        } else {
            templateCodeField.setText("TPL-" + System.currentTimeMillis() % 10000);
        }

        setSize(500, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        templateCodeField = new JTextField(20);
        templateCodeField.setFont(Constants.FONT_REGULAR);

        templateNameField = new JTextField(20);
        templateNameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setFont(Constants.FONT_REGULAR);

        layoutCombo = new JComboBox<>(LAYOUTS);
        layoutCombo.setFont(Constants.FONT_REGULAR);

        paperSizeCombo = new JComboBox<>(PAPER_SIZES);
        paperSizeCombo.setFont(Constants.FONT_REGULAR);

        includeHeaderCheckbox = new JCheckBox("Include Header", true);
        includeHeaderCheckbox.setFont(Constants.FONT_REGULAR);
        includeHeaderCheckbox.setBackground(Constants.BG_WHITE);

        includeFooterCheckbox = new JCheckBox("Include Footer", true);
        includeFooterCheckbox.setFont(Constants.FONT_REGULAR);
        includeFooterCheckbox.setBackground(Constants.BG_WHITE);

        includePageNumbersCheckbox = new JCheckBox("Include Page Numbers", true);
        includePageNumbersCheckbox.setFont(Constants.FONT_REGULAR);
        includePageNumbersCheckbox.setBackground(Constants.BG_WHITE);

        includeDateStampCheckbox = new JCheckBox("Include Date Stamp", true);
        includeDateStampCheckbox.setFont(Constants.FONT_REGULAR);
        includeDateStampCheckbox.setBackground(Constants.BG_WHITE);

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

        // Template Code
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Template Code:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(templateCodeField, gbc);

        // Template Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Template Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(templateNameField, gbc);

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

        // Paper Size
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Paper Size:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(paperSizeCombo, gbc);

        // Options panel
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Options:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel optionsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        optionsPanel.setBackground(Constants.BG_WHITE);
        optionsPanel.add(includeHeaderCheckbox);
        optionsPanel.add(includeFooterCheckbox);
        optionsPanel.add(includePageNumbersCheckbox);
        optionsPanel.add(includeDateStampCheckbox);
        optionsPanel.add(activeCheckbox);
        formPanel.add(optionsPanel, gbc);

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
        saveBtn.addActionListener(e -> saveTemplate());

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

    private void populateFields(ReportTemplate t) {
        templateCodeField.setText(t.getTemplateCode());
        templateCodeField.setEditable(false);
        templateNameField.setText(t.getName());
        descriptionArea.setText(t.getDescription() != null ? t.getDescription() : "");
        categoryCombo.setSelectedItem(t.getCategory());
        layoutCombo.setSelectedItem(t.getLayout());
        paperSizeCombo.setSelectedItem(t.getPaperSize());
        includeHeaderCheckbox.setSelected(t.isIncludeHeader());
        includeFooterCheckbox.setSelected(t.isIncludeFooter());
        includePageNumbersCheckbox.setSelected(t.isIncludePageNumbers());
        includeDateStampCheckbox.setSelected(t.isIncludeDateStamp());
        activeCheckbox.setSelected(t.isActive());
    }

    private void saveTemplate() {
        // Validation
        if (templateCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Template code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            templateCodeField.requestFocus();
            return;
        }

        if (templateNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Template name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            templateNameField.requestFocus();
            return;
        }

        // Create or update template
        if (template == null) {
            template = new ReportTemplate();
        }

        template.setTemplateCode(templateCodeField.getText().trim());
        template.setName(templateNameField.getText().trim());
        template.setDescription(descriptionArea.getText().trim());
        template.setCategory((String) categoryCombo.getSelectedItem());
        template.setLayout((String) layoutCombo.getSelectedItem());
        template.setPaperSize((String) paperSizeCombo.getSelectedItem());
        template.setIncludeHeader(includeHeaderCheckbox.isSelected());
        template.setIncludeFooter(includeFooterCheckbox.isSelected());
        template.setIncludePageNumbers(includePageNumbersCheckbox.isSelected());
        template.setIncludeDateStamp(includeDateStampCheckbox.isSelected());
        template.setActive(activeCheckbox.isSelected());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ReportTemplate getTemplate() {
        return template;
    }
}
