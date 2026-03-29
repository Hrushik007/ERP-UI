package com.erp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance entity for HR module.
 */
public class Attendance {

    private int attendanceId;
    private int employeeId;
    private Employee employee;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // PRESENT, ABSENT, LATE, HALF_DAY, ON_LEAVE, HOLIDAY
    private int totalMinutes;
    private int overtimeMinutes;
    private String shift; // MORNING, EVENING, NIGHT
    private String notes;
    private LocalDateTime createdAt;

    public Attendance() {
        this.attendanceDate = LocalDate.now();
        this.status = "PRESENT";
    }

    public void calculateTotalTime() {
        if (checkInTime != null && checkOutTime != null) {
            long minutes = java.time.Duration.between(checkInTime, checkOutTime).toMinutes();
            this.totalMinutes = (int) minutes;
        }
    }

    // Getters and Setters
    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
        calculateTotalTime();
    }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
        calculateTotalTime();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public int getOvertimeMinutes() { return overtimeMinutes; }
    public void setOvertimeMinutes(int overtimeMinutes) { this.overtimeMinutes = overtimeMinutes; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Attendance{" + employeeId + ", date=" + attendanceDate + ", status=" + status + "}";
    }
}
