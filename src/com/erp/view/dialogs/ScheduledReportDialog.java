package com.erp.view.dialogs;

import com.erp.model.Report;
import com.erp.model.ScheduledReport;
import com.erp.service.mock.MockReportingService;
import com.erp.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ScheduledReportDialog for creating and editing scheduled reports.
 */
public class ScheduledReportDialog extends JDialog {

    private JComboBox<ReportItem> reportCombo;
    private JTextField scheduleNameField;
    private JComboBox<String> frequencyCombo;
    private JComboBox<String> dayOfWeekCombo;
    private JSpinner dayOfMonthSpinner;
    private JTextField timeField;
    private JComboBox<String> formatCombo;
    private JComboBox<String> deliveryMethodCombo;
    private JTextArea recipientsArea;
    private JCheckBox activeCheckbox;

    private JLabel dayOfWeekLabel;
    private JLabel dayOfMonthLabel;

    private boolean confirmed = false;
    private ScheduledReport scheduledReport;
    private MockReportingService reportingService;

    private static final String[] FREQUENCIES = {"DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"};
    private static final String[] DAYS_OF_WEEK = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
    private static final String[] FORMATS = {"PDF", "EXCEL", "CSV", "HTML"};
    private static final String[] DELIVERY_METHODS = {"EMAIL", "FOLDER", "FTP"};

    public ScheduledReportDialog(Frame parent, ScheduledReport existingSchedule) {
        super(parent, existingSchedule == null ? "Schedule Report" : "Edit Schedule", true);
        this.scheduledReport = existingSchedule;
        this.reportingService = MockReportingService.getInstance();

        initializeComponents();
        layoutComponents();

        if (existingSchedule != null) {
            populateFields(existingSchedule);
        }

        setSize(500, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        // Report combo
        reportCombo = new JComboBox<>();
        reportCombo.setFont(Constants.FONT_REGULAR);
        List<Report> reports = reportingService.getActiveReports();
        for (Report r : reports) {
            reportCombo.addItem(new ReportItem(r));
        }

        scheduleNameField = new JTextField(20);
        scheduleNameField.setFont(Constants.FONT_REGULAR);

        frequencyCombo = new JComboBox<>(FREQUENCIES);
        frequencyCombo.setFont(Constants.FONT_REGULAR);
        frequencyCombo.addActionListener(e -> updateFrequencyOptions());

        dayOfWeekCombo = new JComboBox<>(DAYS_OF_WEEK);
        dayOfWeekCombo.setFont(Constants.FONT_REGULAR);

        dayOfMonthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        dayOfMonthSpinner.setFont(Constants.FONT_REGULAR);

        timeField = new JTextField(8);
        timeField.setFont(Constants.FONT_REGULAR);
        timeField.setText("08:00");

        formatCombo = new JComboBox<>(FORMATS);
        formatCombo.setFont(Constants.FONT_REGULAR);

        deliveryMethodCombo = new JComboBox<>(DELIVERY_METHODS);
        deliveryMethodCombo.setFont(Constants.FONT_REGULAR);

        recipientsArea = new JTextArea(3, 20);
        recipientsArea.setFont(Constants.FONT_REGULAR);
        recipientsArea.setLineWrap(true);
        recipientsArea.setWrapStyleWord(true);

        activeCheckbox = new JCheckBox("Active", true);
        activeCheckbox.setFont(Constants.FONT_REGULAR);
        activeCheckbox.setBackground(Constants.BG_WHITE);

        dayOfWeekLabel = createLabel("Day of Week:");
        dayOfMonthLabel = createLabel("Day of Month:");
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

        // Report
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(createLabel("Report:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(reportCombo, gbc);

        // Schedule Name
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Schedule Name:*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(scheduleNameField, gbc);

        // Frequency
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Frequency:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(frequencyCombo, gbc);

        // Day of Week (for weekly)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(dayOfWeekLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dayOfWeekCombo, gbc);

        // Day of Month (for monthly)
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(dayOfMonthLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dayOfMonthSpinner, gbc);

        // Time
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Time (HH:mm):*"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(timeField, gbc);

        // Format
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Output Format:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(formatCombo, gbc);

        // Delivery Method
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel("Delivery Method:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(deliveryMethodCombo, gbc);

        // Active
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createLabel(""), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(activeCheckbox, gbc);

        // Recipients
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Recipients:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane recipientsScroll = new JScrollPane(recipientsArea);
        recipientsScroll.setPreferredSize(new Dimension(200, 60));
        formPanel.add(recipientsScroll, gbc);

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
        saveBtn.addActionListener(e -> saveSchedule());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Initial state
        updateFrequencyOptions();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        label.setPreferredSize(new Dimension(140, 25));
        label.setMinimumSize(new Dimension(140, 25));
        return label;
    }

    private void updateFrequencyOptions() {
        String frequency = (String) frequencyCombo.getSelectedItem();

        boolean showDayOfWeek = "WEEKLY".equals(frequency);
        boolean showDayOfMonth = "MONTHLY".equals(frequency) || "QUARTERLY".equals(frequency) || "YEARLY".equals(frequency);

        dayOfWeekLabel.setVisible(showDayOfWeek);
        dayOfWeekCombo.setVisible(showDayOfWeek);
        dayOfMonthLabel.setVisible(showDayOfMonth);
        dayOfMonthSpinner.setVisible(showDayOfMonth);
    }

    private void populateFields(ScheduledReport s) {
        // Select the report
        for (int i = 0; i < reportCombo.getItemCount(); i++) {
            if (reportCombo.getItemAt(i).getReport().getReportId() == s.getReportId()) {
                reportCombo.setSelectedIndex(i);
                break;
            }
        }

        scheduleNameField.setText(s.getScheduleName());
        frequencyCombo.setSelectedItem(s.getFrequency());

        if (s.getDayOfWeek() != null) {
            dayOfWeekCombo.setSelectedItem(s.getDayOfWeek());
        }
        if (s.getDayOfMonth() > 0) {
            dayOfMonthSpinner.setValue(s.getDayOfMonth());
        }

        timeField.setText(s.getTimeOfDay() != null ? s.getTimeOfDay() : "08:00");
        formatCombo.setSelectedItem(s.getOutputFormat());
        deliveryMethodCombo.setSelectedItem(s.getDeliveryMethod());
        recipientsArea.setText(s.getRecipients() != null ? s.getRecipients() : "");
        activeCheckbox.setSelected(s.isActive());

        updateFrequencyOptions();
    }

    private void saveSchedule() {
        // Validation
        if (reportCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a report.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (scheduleNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Schedule name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            scheduleNameField.requestFocus();
            return;
        }

        String time = timeField.getText().trim();
        if (!time.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Time must be in HH:mm format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            timeField.requestFocus();
            return;
        }

        // Create or update schedule
        if (scheduledReport == null) {
            scheduledReport = new ScheduledReport();
        }

        ReportItem selectedReport = (ReportItem) reportCombo.getSelectedItem();
        scheduledReport.setReportId(selectedReport.getReport().getReportId());
        scheduledReport.setScheduleName(scheduleNameField.getText().trim());
        scheduledReport.setFrequency((String) frequencyCombo.getSelectedItem());

        String frequency = (String) frequencyCombo.getSelectedItem();
        if ("WEEKLY".equals(frequency)) {
            scheduledReport.setDayOfWeek((String) dayOfWeekCombo.getSelectedItem());
        }
        if ("MONTHLY".equals(frequency) || "QUARTERLY".equals(frequency) || "YEARLY".equals(frequency)) {
            scheduledReport.setDayOfMonth((Integer) dayOfMonthSpinner.getValue());
        }

        scheduledReport.setTimeOfDay(time);
        scheduledReport.setOutputFormat((String) formatCombo.getSelectedItem());
        scheduledReport.setDeliveryMethod((String) deliveryMethodCombo.getSelectedItem());
        scheduledReport.setRecipients(recipientsArea.getText().trim());
        scheduledReport.setActive(activeCheckbox.isSelected());

        // Calculate next run date
        scheduledReport.setNextRunDate(LocalDateTime.now().plusDays(1));

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ScheduledReport getScheduledReport() {
        return scheduledReport;
    }

    // Helper class for report combo
    private static class ReportItem {
        private Report report;

        public ReportItem(Report report) {
            this.report = report;
        }

        public Report getReport() {
            return report;
        }

        @Override
        public String toString() {
            return report.getReportCode() + " - " + report.getName();
        }
    }
}
