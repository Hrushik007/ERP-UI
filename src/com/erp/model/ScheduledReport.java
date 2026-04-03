package com.erp.model;

import java.time.LocalDateTime;

/**
 * ScheduledReport model representing a scheduled report execution.
 */
public class ScheduledReport {
    private int scheduleId;
    private int reportId;
    private String scheduleName;
    private String frequency; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
    private String dayOfWeek; // For weekly: MONDAY, TUESDAY, etc.
    private int dayOfMonth; // For monthly: 1-31
    private String timeOfDay; // HH:mm format
    private String outputFormat; // PDF, EXCEL, CSV, HTML
    private String recipients; // Comma-separated email addresses
    private String deliveryMethod; // EMAIL, FOLDER, FTP
    private String deliveryPath; // File path or FTP path
    private boolean active;
    private LocalDateTime lastRunDate;
    private LocalDateTime nextRunDate;
    private String lastRunStatus; // SUCCESS, FAILED, PENDING
    private String lastRunMessage;
    private LocalDateTime createdDate;

    public ScheduledReport() {
        this.active = true;
        this.lastRunStatus = "PENDING";
        this.createdDate = LocalDateTime.now();
        this.outputFormat = "PDF";
        this.deliveryMethod = "EMAIL";
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getDeliveryPath() {
        return deliveryPath;
    }

    public void setDeliveryPath(String deliveryPath) {
        this.deliveryPath = deliveryPath;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(LocalDateTime lastRunDate) {
        this.lastRunDate = lastRunDate;
    }

    public LocalDateTime getNextRunDate() {
        return nextRunDate;
    }

    public void setNextRunDate(LocalDateTime nextRunDate) {
        this.nextRunDate = nextRunDate;
    }

    public String getLastRunStatus() {
        return lastRunStatus;
    }

    public void setLastRunStatus(String lastRunStatus) {
        this.lastRunStatus = lastRunStatus;
    }

    public String getLastRunMessage() {
        return lastRunMessage;
    }

    public void setLastRunMessage(String lastRunMessage) {
        this.lastRunMessage = lastRunMessage;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
