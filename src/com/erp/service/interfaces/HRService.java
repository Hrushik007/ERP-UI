package com.erp.service.interfaces;

import com.erp.model.Employee;
import com.erp.model.Attendance;
import com.erp.model.LeaveRequest;
import com.erp.model.Payroll;

import java.time.LocalDate;
import java.util.List;

/**
 * HRService Interface - CONTRACT for HR Module Backend Team
 *
 * This interface defines ALL operations that the UI expects from the HR backend.
 * Backend team MUST implement this interface for integration.
 *
 * IMPORTANT FOR BACKEND TEAM:
 * - Implement all methods in a class called HRServiceImpl
 * - Return empty lists instead of null when no data found
 * - Throw appropriate exceptions for error conditions
 * - All IDs are positive integers; 0 or negative means "not found"
 */
public interface HRService {

    // ==================== EMPLOYEE MANAGEMENT ====================

    /**
     * Get all employees in the system.
     * @return List of all employees (empty list if none)
     */
    List<Employee> getAllEmployees();

    /**
     * Get employees filtered by department.
     * @param department The department name
     * @return List of employees in that department
     */
    List<Employee> getEmployeesByDepartment(String department);

    /**
     * Get employees filtered by status.
     * @param status ACTIVE, ON_LEAVE, or TERMINATED
     * @return List of employees with that status
     */
    List<Employee> getEmployeesByStatus(String status);

    /**
     * Get a single employee by ID.
     * @param employeeId The employee's unique ID
     * @return The Employee object, or null if not found
     */
    Employee getEmployeeById(int employeeId);

    /**
     * Search employees by name (partial match).
     * @param searchTerm The search string
     * @return List of matching employees
     */
    List<Employee> searchEmployees(String searchTerm);

    /**
     * Create a new employee.
     * @param employee The employee data (employeeId will be ignored/auto-generated)
     * @return The created Employee with assigned ID
     */
    Employee createEmployee(Employee employee);

    /**
     * Update an existing employee.
     * @param employee The employee data with ID set
     * @return true if updated successfully, false if employee not found
     */
    boolean updateEmployee(Employee employee);

    /**
     * Delete (or deactivate) an employee.
     * @param employeeId The employee ID to delete
     * @return true if deleted successfully
     */
    boolean deleteEmployee(int employeeId);

    /**
     * Get all unique departments.
     * @return List of department names
     */
    List<String> getAllDepartments();

    /**
     * Get all unique positions/job titles.
     * @return List of position names
     */
    List<String> getAllPositions();

    /**
     * Get employees managed by a specific manager.
     * @param managerId The manager's employee ID
     * @return List of employees reporting to this manager
     */
    List<Employee> getEmployeesByManager(int managerId);


    // ==================== ATTENDANCE MANAGEMENT ====================

    /**
     * Record attendance (check-in/check-out).
     * @param attendance The attendance record
     * @return The saved attendance record with ID
     */
    Attendance recordAttendance(Attendance attendance);

    /**
     * Get attendance for an employee on a specific date.
     * @param employeeId The employee ID
     * @param date The date to check
     * @return Attendance record, or null if not found
     */
    Attendance getAttendance(int employeeId, LocalDate date);

    /**
     * Get attendance records for an employee in a date range.
     * @param employeeId The employee ID
     * @param startDate Start of the range (inclusive)
     * @param endDate End of the range (inclusive)
     * @return List of attendance records
     */
    List<Attendance> getAttendanceRange(int employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * Get all attendance records for a specific date (all employees).
     * @param date The date to check
     * @return List of attendance records for that day
     */
    List<Attendance> getDailyAttendance(LocalDate date);

    /**
     * Update an attendance record.
     * @param attendance The attendance to update
     * @return true if updated successfully
     */
    boolean updateAttendance(Attendance attendance);


    // ==================== LEAVE MANAGEMENT ====================

    /**
     * Submit a new leave request.
     * @param leaveRequest The leave request data
     * @return The created leave request with ID
     */
    LeaveRequest submitLeaveRequest(LeaveRequest leaveRequest);

    /**
     * Get all leave requests for an employee.
     * @param employeeId The employee ID
     * @return List of leave requests
     */
    List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId);

    /**
     * Get pending leave requests (for manager approval).
     * @param approverId The manager's employee ID
     * @return List of pending requests needing approval
     */
    List<LeaveRequest> getPendingLeaveRequests(int approverId);

    /**
     * Approve a leave request.
     * @param leaveRequestId The request ID
     * @param approverId The approving manager's ID
     * @param comments Optional comments
     * @return true if approved successfully
     */
    boolean approveLeaveRequest(int leaveRequestId, int approverId, String comments);

    /**
     * Reject a leave request.
     * @param leaveRequestId The request ID
     * @param approverId The rejecting manager's ID
     * @param comments Reason for rejection
     * @return true if rejected successfully
     */
    boolean rejectLeaveRequest(int leaveRequestId, int approverId, String comments);

    /**
     * Get employee's leave balance.
     * @param employeeId The employee ID
     * @param leaveType The type of leave (ANNUAL, SICK, etc.)
     * @return Number of days remaining
     */
    int getLeaveBalance(int employeeId, String leaveType);


    // ==================== PAYROLL MANAGEMENT ====================

    /**
     * Generate payroll for an employee for a pay period.
     * @param employeeId The employee ID
     * @param payPeriod The pay period (e.g., "2024-03")
     * @return The generated Payroll record
     */
    Payroll generatePayroll(int employeeId, String payPeriod);

    /**
     * Get payroll records for an employee.
     * @param employeeId The employee ID
     * @return List of payroll records
     */
    List<Payroll> getPayrollByEmployee(int employeeId);

    /**
     * Get all payroll records for a pay period.
     * @param payPeriod The pay period
     * @return List of payroll records
     */
    List<Payroll> getPayrollByPeriod(String payPeriod);

    /**
     * Process payroll (mark as paid).
     * @param payrollId The payroll ID
     * @return true if processed successfully
     */
    boolean processPayroll(int payrollId);

    /**
     * Update payroll record.
     * @param payroll The payroll to update
     * @return true if updated successfully
     */
    boolean updatePayroll(Payroll payroll);
}
