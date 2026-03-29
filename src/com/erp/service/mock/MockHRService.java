package com.erp.service.mock;

import com.erp.model.Attendance;
import com.erp.model.Employee;
import com.erp.model.LeaveRequest;
import com.erp.model.Payroll;
import com.erp.service.interfaces.HRService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockHRService provides sample data for UI development and testing.
 *
 * This demonstrates:
 * 1. INTERFACE IMPLEMENTATION - We implement the HRService interface
 * 2. MOCK/STUB PATTERN - Provides fake data for testing
 * 3. When real backend is ready, swap this with real implementation
 */
public class MockHRService implements HRService {

    private Map<Integer, Employee> employees;
    private Map<Integer, Attendance> attendanceRecords;
    private Map<Integer, LeaveRequest> leaveRequests;
    private Map<Integer, Payroll> payrollRecords;
    private int nextEmployeeId = 100;
    private int nextAttendanceId = 1000;
    private int nextLeaveRequestId = 2000;
    private int nextPayrollId = 3000;

    private static MockHRService instance;

    public static synchronized MockHRService getInstance() {
        if (instance == null) {
            instance = new MockHRService();
        }
        return instance;
    }

    private MockHRService() {
        employees = new HashMap<>();
        attendanceRecords = new HashMap<>();
        leaveRequests = new HashMap<>();
        payrollRecords = new HashMap<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        createSampleEmployee("John", "Smith", "IT", "Software Developer", "john.smith@company.com", 75000);
        createSampleEmployee("Sarah", "Johnson", "IT", "Senior Developer", "sarah.j@company.com", 95000);
        createSampleEmployee("Michael", "Brown", "HR", "HR Manager", "m.brown@company.com", 85000);
        createSampleEmployee("Emily", "Davis", "Finance", "Accountant", "emily.d@company.com", 65000);
        createSampleEmployee("David", "Wilson", "Sales", "Sales Representative", "d.wilson@company.com", 55000);
        createSampleEmployee("Jennifer", "Taylor", "Marketing", "Marketing Specialist", "j.taylor@company.com", 60000);
        createSampleEmployee("Robert", "Anderson", "IT", "System Administrator", "r.anderson@company.com", 70000);
        createSampleEmployee("Lisa", "Thomas", "HR", "Recruiter", "l.thomas@company.com", 50000);
        createSampleEmployee("James", "Jackson", "Finance", "Financial Analyst", "j.jackson@company.com", 72000);
        createSampleEmployee("Michelle", "White", "Operations", "Operations Manager", "m.white@company.com", 80000);

        // Sample attendance for today
        LocalDate today = LocalDate.now();
        for (Employee emp : employees.values()) {
            if (Math.random() > 0.2) {
                Attendance att = new Attendance();
                att.setAttendanceId(nextAttendanceId++);
                att.setEmployeeId(emp.getEmployeeId());
                att.setAttendanceDate(today);
                int hour = 8 + (int)(Math.random() * 2);
                int minute = (int)(Math.random() * 60);
                att.setCheckInTime(LocalDateTime.of(today, java.time.LocalTime.of(hour, minute)));
                if (Math.random() > 0.3) {
                    int outHour = 17 + (int)(Math.random() * 2);
                    int outMinute = (int)(Math.random() * 60);
                    att.setCheckOutTime(LocalDateTime.of(today, java.time.LocalTime.of(outHour, outMinute)));
                    att.setStatus("PRESENT");
                } else {
                    att.setStatus("CHECKED_IN");
                }
                attendanceRecords.put(att.getAttendanceId(), att);
            }
        }

        // Sample leave requests
        createSampleLeaveRequest(100, "VACATION", LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), "PENDING");
        createSampleLeaveRequest(101, "SICK", LocalDate.now().minusDays(2), LocalDate.now().minusDays(1), "APPROVED");
        createSampleLeaveRequest(102, "PERSONAL", LocalDate.now().plusDays(5), LocalDate.now().plusDays(5), "PENDING");
        createSampleLeaveRequest(104, "VACATION", LocalDate.now().plusDays(20), LocalDate.now().plusDays(25), "APPROVED");
    }

    private void createSampleEmployee(String firstName, String lastName, String dept, String position, String email, double salary) {
        Employee emp = new Employee();
        emp.setEmployeeId(nextEmployeeId++);
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setDepartment(dept);
        emp.setPosition(position);
        emp.setEmail(email);
        emp.setPhone("555-" + (1000 + (int)(Math.random() * 9000)));
        emp.setSalary(BigDecimal.valueOf(salary));
        emp.setHireDate(LocalDate.now().minusDays((int)(Math.random() * 1000) + 100));
        emp.setEmploymentStatus("ACTIVE");
        emp.setCreatedAt(LocalDate.now());
        employees.put(emp.getEmployeeId(), emp);
    }

    private void createSampleLeaveRequest(int empId, String type, LocalDate start, LocalDate end, String status) {
        LeaveRequest lr = new LeaveRequest();
        lr.setLeaveRequestId(nextLeaveRequestId++);
        lr.setEmployeeId(empId);
        lr.setLeaveType(type);
        lr.setStartDate(start);
        lr.setEndDate(end);
        lr.setStatus(status);
        lr.setReason("Sample leave request");
        lr.setCreatedAt(LocalDateTime.now().minusDays((int)(Math.random() * 10)));
        leaveRequests.put(lr.getLeaveRequestId(), lr);
    }

    // ==================== EMPLOYEE MANAGEMENT ====================

    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public List<Employee> getEmployeesByDepartment(String department) {
        return employees.values().stream()
                .filter(e -> department.equalsIgnoreCase(e.getDepartment()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> getEmployeesByStatus(String status) {
        return employees.values().stream()
                .filter(e -> status.equalsIgnoreCase(e.getEmploymentStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeById(int employeeId) {
        return employees.get(employeeId);
    }

    @Override
    public List<Employee> searchEmployees(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return employees.values().stream()
                .filter(e -> e.getFirstName().toLowerCase().contains(term) ||
                            e.getLastName().toLowerCase().contains(term) ||
                            e.getEmail().toLowerCase().contains(term) ||
                            e.getDepartment().toLowerCase().contains(term) ||
                            e.getPosition().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public Employee createEmployee(Employee employee) {
        employee.setEmployeeId(nextEmployeeId++);
        employee.setCreatedAt(LocalDate.now());
        employee.setUpdatedAt(LocalDate.now());
        if (employee.getEmploymentStatus() == null) {
            employee.setEmploymentStatus("ACTIVE");
        }
        employees.put(employee.getEmployeeId(), employee);
        return employee;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        if (employees.containsKey(employee.getEmployeeId())) {
            employee.setUpdatedAt(LocalDate.now());
            employees.put(employee.getEmployeeId(), employee);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEmployee(int employeeId) {
        return employees.remove(employeeId) != null;
    }

    @Override
    public List<String> getAllDepartments() {
        return employees.values().stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllPositions() {
        return employees.values().stream()
                .map(Employee::getPosition)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> getEmployeesByManager(int managerId) {
        return employees.values().stream()
                .filter(e -> e.getManagerId() == managerId)
                .collect(Collectors.toList());
    }

    // ==================== ATTENDANCE MANAGEMENT ====================

    @Override
    public Attendance recordAttendance(Attendance attendance) {
        attendance.setAttendanceId(nextAttendanceId++);
        attendanceRecords.put(attendance.getAttendanceId(), attendance);
        return attendance;
    }

    @Override
    public Attendance getAttendance(int employeeId, LocalDate date) {
        return attendanceRecords.values().stream()
                .filter(a -> a.getEmployeeId() == employeeId && a.getAttendanceDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Attendance> getAttendanceRange(int employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecords.values().stream()
                .filter(a -> a.getEmployeeId() == employeeId &&
                            !a.getAttendanceDate().isBefore(startDate) &&
                            !a.getAttendanceDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> getDailyAttendance(LocalDate date) {
        return attendanceRecords.values().stream()
                .filter(a -> a.getAttendanceDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateAttendance(Attendance attendance) {
        if (attendanceRecords.containsKey(attendance.getAttendanceId())) {
            attendanceRecords.put(attendance.getAttendanceId(), attendance);
            return true;
        }
        return false;
    }

    // Convenience methods for UI (not in interface)
    public Attendance checkIn(int employeeId) {
        Attendance att = new Attendance();
        att.setAttendanceId(nextAttendanceId++);
        att.setEmployeeId(employeeId);
        att.setAttendanceDate(LocalDate.now());
        att.setCheckInTime(LocalDateTime.now());
        att.setStatus("CHECKED_IN");
        attendanceRecords.put(att.getAttendanceId(), att);
        return att;
    }

    public Attendance checkOut(int employeeId) {
        Optional<Attendance> todayAtt = attendanceRecords.values().stream()
                .filter(a -> a.getEmployeeId() == employeeId &&
                            a.getAttendanceDate().equals(LocalDate.now()) &&
                            a.getCheckOutTime() == null)
                .findFirst();

        if (todayAtt.isPresent()) {
            Attendance att = todayAtt.get();
            att.setCheckOutTime(LocalDateTime.now());
            att.setStatus("PRESENT");
            return att;
        }
        return null;
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return getDailyAttendance(date);
    }

    public Map<String, Integer> getAttendanceSummary(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> summary = new HashMap<>();
        List<Attendance> records = attendanceRecords.values().stream()
                .filter(a -> !a.getAttendanceDate().isBefore(startDate) && !a.getAttendanceDate().isAfter(endDate))
                .collect(Collectors.toList());

        summary.put("totalRecords", records.size());
        summary.put("present", (int) records.stream().filter(a -> "PRESENT".equals(a.getStatus())).count());
        summary.put("late", (int) records.stream().filter(a -> "LATE".equals(a.getStatus())).count());
        summary.put("checkedIn", (int) records.stream().filter(a -> "CHECKED_IN".equals(a.getStatus())).count());

        return summary;
    }

    // ==================== LEAVE MANAGEMENT ====================

    @Override
    public LeaveRequest submitLeaveRequest(LeaveRequest request) {
        request.setLeaveRequestId(nextLeaveRequestId++);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        leaveRequests.put(request.getLeaveRequestId(), request);
        return request;
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployee(int employeeId) {
        return leaveRequests.values().stream()
                .filter(lr -> lr.getEmployeeId() == employeeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequest> getPendingLeaveRequests(int approverId) {
        return leaveRequests.values().stream()
                .filter(lr -> "PENDING".equals(lr.getStatus()))
                .collect(Collectors.toList());
    }

    // Convenience method
    public List<LeaveRequest> getPendingLeaveRequests() {
        return getPendingLeaveRequests(0);
    }

    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        return leaveRequests.values().stream()
                .filter(lr -> status.equals(lr.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean approveLeaveRequest(int leaveRequestId, int approverId, String comments) {
        LeaveRequest lr = leaveRequests.get(leaveRequestId);
        if (lr != null && "PENDING".equals(lr.getStatus())) {
            lr.setStatus("APPROVED");
            lr.setApproverId(approverId);
            lr.setApproverComments(comments);
            lr.setApprovedDate(LocalDateTime.now());
            lr.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public boolean rejectLeaveRequest(int leaveRequestId, int approverId, String reason) {
        LeaveRequest lr = leaveRequests.get(leaveRequestId);
        if (lr != null && "PENDING".equals(lr.getStatus())) {
            lr.setStatus("REJECTED");
            lr.setApproverId(approverId);
            lr.setApproverComments(reason);
            lr.setUpdatedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Override
    public int getLeaveBalance(int employeeId, String leaveType) {
        int base = 0;
        switch (leaveType) {
            case "VACATION": base = 15; break;
            case "SICK": base = 10; break;
            case "PERSONAL": base = 5; break;
            default: base = 0;
        }

        int used = (int) leaveRequests.values().stream()
                .filter(lr -> lr.getEmployeeId() == employeeId &&
                             leaveType.equals(lr.getLeaveType()) &&
                             "APPROVED".equals(lr.getStatus()))
                .mapToLong(lr -> java.time.temporal.ChronoUnit.DAYS.between(lr.getStartDate(), lr.getEndDate()) + 1)
                .sum();

        return base - used;
    }

    // Convenience method
    public Map<String, Integer> getLeaveBalance(int employeeId) {
        Map<String, Integer> balance = new HashMap<>();
        balance.put("VACATION", getLeaveBalance(employeeId, "VACATION"));
        balance.put("SICK", getLeaveBalance(employeeId, "SICK"));
        balance.put("PERSONAL", getLeaveBalance(employeeId, "PERSONAL"));
        return balance;
    }

    // ==================== PAYROLL MANAGEMENT ====================

    @Override
    public Payroll generatePayroll(int employeeId, String payPeriod) {
        Employee emp = employees.get(employeeId);
        if (emp == null) return null;

        Payroll payroll = new Payroll();
        payroll.setPayrollId(nextPayrollId++);
        payroll.setEmployeeId(employeeId);
        payroll.setPayPeriod(payPeriod);
        payroll.setGrossPay(emp.getSalary().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
        payroll.setNetPay(payroll.getGrossPay().multiply(BigDecimal.valueOf(0.75)));
        payroll.setStatus("DRAFT");
        payrollRecords.put(payroll.getPayrollId(), payroll);
        return payroll;
    }

    @Override
    public List<Payroll> getPayrollByEmployee(int employeeId) {
        return payrollRecords.values().stream()
                .filter(p -> p.getEmployeeId() == employeeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payroll> getPayrollByPeriod(String payPeriod) {
        return payrollRecords.values().stream()
                .filter(p -> payPeriod.equals(p.getPayPeriod()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean processPayroll(int payrollId) {
        Payroll p = payrollRecords.get(payrollId);
        if (p != null) {
            p.setStatus("PROCESSED");
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePayroll(Payroll payroll) {
        if (payrollRecords.containsKey(payroll.getPayrollId())) {
            payrollRecords.put(payroll.getPayrollId(), payroll);
            return true;
        }
        return false;
    }
}
