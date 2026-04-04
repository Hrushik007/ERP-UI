package com.erp.view.dialogs;

import com.erp.model.Campaign;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * CampaignDialog for creating and editing marketing campaigns.
 */
public class CampaignDialog extends JDialog {

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<String> typeCombo;
    private JComboBox<String> statusCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextField budgetField;
    private JTextField targetAudienceField;
    private JTextField leadTargetField;
    private JTextField channelField;
    private JTextArea notesArea;

    private boolean saved = false;
    private Campaign campaign;

    private static final String[] TYPES = {"EMAIL", "SOCIAL_MEDIA", "ADS", "EVENT", "CONTENT"};
    private static final String[] STATUSES = {"PLANNED", "ACTIVE", "PAUSED", "COMPLETED", "CANCELLED"};

    public CampaignDialog(Frame parent, Campaign existingCampaign) {
        super(parent, existingCampaign == null ? "New Campaign" : "Edit Campaign", true);
        this.campaign = existingCampaign;

        initializeComponents();
        layoutComponents();

        if (existingCampaign != null) {
            populateFields(existingCampaign);
        }

        setSize(500, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        nameField = new JTextField(20);
        nameField.setFont(Constants.FONT_REGULAR);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(Constants.FONT_REGULAR);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        typeCombo = new JComboBox<>(TYPES);
        typeCombo.setFont(Constants.FONT_REGULAR);

        statusCombo = new JComboBox<>(STATUSES);
        statusCombo.setFont(Constants.FONT_REGULAR);

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        startDateSpinner.setValue(new Date());
        startDateSpinner.setFont(Constants.FONT_REGULAR);

        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));
        endDateSpinner.setValue(Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateSpinner.setFont(Constants.FONT_REGULAR);

        budgetField = new JTextField(20);
        budgetField.setFont(Constants.FONT_REGULAR);
        budgetField.setText("0.00");

        targetAudienceField = new JTextField(20);
        targetAudienceField.setFont(Constants.FONT_REGULAR);

        leadTargetField = new JTextField(20);
        leadTargetField.setFont(Constants.FONT_REGULAR);
        leadTargetField.setText("0");

        channelField = new JTextField(20);
        channelField.setFont(Constants.FONT_REGULAR);

        notesArea = new JTextArea(2, 20);
        notesArea.setFont(Constants.FONT_REGULAR);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
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

        // Campaign Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Campaign Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        // Type
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(typeCombo, gbc);

        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(statusCombo, gbc);

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

        // Target Audience
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Target Audience:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(targetAudienceField, gbc);

        // Lead Target
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Lead Target:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(leadTargetField, gbc);

        // Channel
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Channel:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(channelField, gbc);

        // Description
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(200, 60));
        formPanel.add(descScroll, gbc);

        // Notes
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(200, 40));
        formPanel.add(notesScroll, gbc);

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
        saveBtn.addActionListener(e -> saveCampaign());

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

    private void populateFields(Campaign c) {
        nameField.setText(c.getName());
        descriptionArea.setText(c.getDescription() != null ? c.getDescription() : "");
        typeCombo.setSelectedItem(c.getType());
        statusCombo.setSelectedItem(c.getStatus());
        targetAudienceField.setText(c.getTargetAudience() != null ? c.getTargetAudience() : "");
        leadTargetField.setText(String.valueOf(c.getLeadTarget()));
        channelField.setText(c.getChannel() != null ? c.getChannel() : "");
        notesArea.setText(c.getNotes() != null ? c.getNotes() : "");

        if (c.getBudget() != null) {
            budgetField.setText(c.getBudget().toString());
        }
        if (c.getStartDate() != null) {
            startDateSpinner.setValue(Date.from(c.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        if (c.getEndDate() != null) {
            endDateSpinner.setValue(Date.from(c.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }

    private void saveCampaign() {
        // Validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Campaign name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
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

        int leadTarget = 0;
        try {
            if (!leadTargetField.getText().trim().isEmpty()) {
                leadTarget = Integer.parseInt(leadTargetField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid lead target. Must be a number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            leadTargetField.requestFocus();
            return;
        }

        // Create or update campaign
        if (campaign == null) {
            campaign = new Campaign();
        }

        campaign.setName(nameField.getText().trim());
        campaign.setDescription(descriptionArea.getText().trim());
        campaign.setType((String) typeCombo.getSelectedItem());
        campaign.setStatus((String) statusCombo.getSelectedItem());
        campaign.setBudget(budget);
        campaign.setTargetAudience(targetAudienceField.getText().trim());
        campaign.setLeadTarget(leadTarget);
        campaign.setChannel(channelField.getText().trim());
        campaign.setNotes(notesArea.getText().trim());

        Date startDate = (Date) startDateSpinner.getValue();
        campaign.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        Date endDate = (Date) endDateSpinner.getValue();
        campaign.setEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Campaign getCampaign() {
        return campaign;
    }
}
