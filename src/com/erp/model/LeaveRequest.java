package com.erp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * LeaveRequest entity for HR module.
 */
public class LeaveRequest {

    private int leaveRequestId;
    private int employeeId;
    private Employee employee;
    private String leaveType; // ANNUAL, SICK, PERSONAL, MATERNITY, PATERNITY, UNPAID
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalDays;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private int approverId; // Manager who approves
    private LocalDateTime approvedDate;
    private String approverComments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LeaveRequest() {
        this.status = "PENDING";
    }

    public void calculateTotalDays() {
        if (startDate != null && endDate != null) {
            this.totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
    }

    // Getters and Setters
    public int getLeaveRequestId() { return leaveRequestId; }
    public void setLeaveRequestId(int leaveRequestId) { this.leaveRequestId = leaveRequestId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateTotalDays();
    }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        calculateTotalDays();
    }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getApproverId() { return approverId; }
    public void setApproverId(int approverId) { this.approverId = approverId; }

    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }

    public String getApproverComments() { return approverComments; }
    public void setApproverComments(String approverComments) { this.approverComments = approverComments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "LeaveRequest{" + employeeId + ", " + leaveType + ", status=" + status + "}";
    }
}
