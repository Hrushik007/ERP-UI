package com.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payroll entity for HR module.
 */
public class Payroll {

    private int payrollId;
    private int employeeId;
    private Employee employee;
    private String payPeriod; // e.g., "2024-03" for March 2024
    private LocalDate payPeriodStart;
    private LocalDate payPeriodEnd;
    private LocalDate paymentDate;
    private BigDecimal basicSalary;
    private BigDecimal overtime;
    private BigDecimal bonus;
    private BigDecimal allowances;
    private BigDecimal grossPay;
    private BigDecimal taxDeduction;
    private BigDecimal insuranceDeduction;
    private BigDecimal otherDeductions;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    private String status; // DRAFT, PROCESSED, PAID
    private String paymentMethod;
    private String bankAccount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payroll() {
        this.status = "DRAFT";
    }

    public void calculateTotals() {
        // Calculate gross
        this.grossPay = BigDecimal.ZERO;
        if (basicSalary != null) grossPay = grossPay.add(basicSalary);
        if (overtime != null) grossPay = grossPay.add(overtime);
        if (bonus != null) grossPay = grossPay.add(bonus);
        if (allowances != null) grossPay = grossPay.add(allowances);

        // Calculate deductions
        this.totalDeductions = BigDecimal.ZERO;
        if (taxDeduction != null) totalDeductions = totalDeductions.add(taxDeduction);
        if (insuranceDeduction != null) totalDeductions = totalDeductions.add(insuranceDeduction);
        if (otherDeductions != null) totalDeductions = totalDeductions.add(otherDeductions);

        // Net pay
        this.netPay = grossPay.subtract(totalDeductions);
    }

    // Getters and Setters
    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

    public LocalDate getPayPeriodStart() { return payPeriodStart; }
    public void setPayPeriodStart(LocalDate payPeriodStart) { this.payPeriodStart = payPeriodStart; }

    public LocalDate getPayPeriodEnd() { return payPeriodEnd; }
    public void setPayPeriodEnd(LocalDate payPeriodEnd) { this.payPeriodEnd = payPeriodEnd; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getOvertime() { return overtime; }
    public void setOvertime(BigDecimal overtime) { this.overtime = overtime; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal allowances) { this.allowances = allowances; }

    public BigDecimal getGrossPay() { return grossPay; }
    public void setGrossPay(BigDecimal grossPay) { this.grossPay = grossPay; }

    public BigDecimal getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(BigDecimal taxDeduction) { this.taxDeduction = taxDeduction; }

    public BigDecimal getInsuranceDeduction() { return insuranceDeduction; }
    public void setInsuranceDeduction(BigDecimal insuranceDeduction) { this.insuranceDeduction = insuranceDeduction; }

    public BigDecimal getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getNetPay() { return netPay; }
    public void setNetPay(BigDecimal netPay) { this.netPay = netPay; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Payroll{" + employeeId + ", period=" + payPeriod + ", net=" + netPay + "}";
    }
}
