package com.erp.integration;

import com.erp.exception.IntegrationException;
import com.erp.model.dto.EmployeeDTO;
import com.erp.model.dto.OrderDTO;
import com.erp.model.dto.UserSessionDTO;
import com.erp.session.UserSession;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory reference implementation of IUIService.
 *
 * Simulates a network with ~150 ms latency. A test hook {@link #setFailNext(boolean)}
 * forces the next call to throw an IntegrationException so retry flows can be
 * demonstrated without needing real infrastructure.
 *
 * Seeds realistic car-manufacturing-ERP data: ~15 orders and ~20 employees.
 */
public class MockUIService implements IUIService {

    private final List<OrderDTO> orders = new ArrayList<>();
    private final List<EmployeeDTO> employees = new ArrayList<>();
    private final List<String[]> leaveRequests = new ArrayList<>(); // id,empId,type,from,to,status
    private final List<String[]> attendanceLog = new ArrayList<>(); // id,empId,checkIn,checkOut,overtime
    private final List<String[]> activityLog = new ArrayList<>();   // timestamp, message

    private final AtomicInteger orderSeq = new AtomicInteger(1000);
    private final AtomicInteger leaveSeq = new AtomicInteger(500);
    private final AtomicInteger attendanceSeq = new AtomicInteger(900);

    private volatile boolean failNext = false;
    private volatile long latencyMs = 150L;

    public MockUIService() {
        seedOrders();
        seedEmployees();
        seedLeave();
        seedAttendance();
    }

    // ==================== Test hooks ====================
    public void setFailNext(boolean v) { this.failNext = v; }
    public void setLatencyMs(long ms) { this.latencyMs = ms; }

    // ==================== IUIService ====================

    @Override
    @SuppressWarnings("unchecked")
    public <T> T fetchData(String endpoint, Map<String, Object> params, Class<T> resultType)
            throws IntegrationException {
        simulate(endpoint, true);
        Map<String, Object> p = params == null ? new HashMap<>() : params;
        try {
            switch (endpoint) {
                case ORDERS_LIST:      return (T) filterOrders(p);
                case ORDERS_STATS:     return (T) orderStats();
                case HR_EMPLOYEES:     return (T) filterEmployees(p);
                case HR_RECRUITMENT:   return (T) recruitmentPipeline();
                case HR_ONBOARDING:    return (T) onboardingList();
                case HR_PAYROLL:       return (T) payrollList();
                case HR_ATTENDANCE:    return (T) new ArrayList<>(attendanceLog);
                case HR_LEAVE:         return (T) new ArrayList<>(leaveRequests);
                case HR_PERFORMANCE:   return (T) performanceList();
                case HR_STATS:         return (T) hrStats();
                default:
                    throw IntegrationException.fetchFailed(endpoint, "Unknown endpoint");
            }
        } catch (IntegrationException ie) { throw ie; }
          catch (Exception e) { throw IntegrationException.fetchFailed(endpoint, e.getMessage()); }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R sendData(String endpoint, Object payload, Class<R> resultType)
            throws IntegrationException {
        simulate(endpoint, false);
        try {
            switch (endpoint) {
                case AUTH_LOGIN:              return (R) login((Map<String, Object>) payload);
                case ORDERS_CREATE:           return (R) createOrder((OrderDTO) payload);
                case ORDERS_APPROVE:          return (R) updateStatus((String) payload, OrderDTO.APPROVED, "Approved");
                case ORDERS_REJECT:           return (R) updateStatus((String) payload, OrderDTO.REJECTED, "Rejected");
                case ORDERS_REVISION:         return (R) updateStatus((String) payload, OrderDTO.REVISION, "Sent back for revision");
                case ORDERS_SHIP:             return (R) ship((Map<String, Object>) payload);
                case ORDERS_PAY:              return (R) pay((Map<String, Object>) payload);
                case ORDERS_CANCEL:           return (R) cancel((Map<String, Object>) payload);
                case HR_EMPLOYEE_UPDATE:      return (R) updateEmployee((EmployeeDTO) payload);
                case HR_RECRUITMENT_STAGE:    return (R) moveRecruitmentStage((Map<String, Object>) payload);
                case HR_ONBOARDING_UPDATE:    return (R) updateEmployee((EmployeeDTO) payload);
                case HR_PAYROLL_TRANSFER:     return (R) transferSalary((String) payload);
                case HR_ATTENDANCE_LOG:       return (R) logAttendance((Map<String, Object>) payload);
                case HR_LEAVE_ACTION:         return (R) leaveAction((Map<String, Object>) payload);
                default:
                    throw IntegrationException.sendFailed(endpoint, "Unknown endpoint");
            }
        } catch (IntegrationException ie) { throw ie; }
          catch (Exception e) { throw IntegrationException.sendFailed(endpoint, e.getMessage()); }
    }

    private void simulate(String endpoint, boolean fetch) throws IntegrationException {
        try { Thread.sleep(latencyMs); } catch (InterruptedException ignored) {}
        if (failNext) {
            failNext = false;
            if (fetch) throw IntegrationException.fetchFailed(endpoint, "Simulated network failure");
            else throw IntegrationException.sendFailed(endpoint, "Simulated network failure");
        }
    }

    // ==================== Auth ====================

    private UserSessionDTO login(Map<String, Object> creds) {
        String u = str(creds, "username");
        String p = str(creds, "password");
        String role = str(creds, "role");
        Map<String, String[]> db = new HashMap<>();
        db.put("admin",    new String[]{"admin123",    "System Administrator", UserSession.ROLE_ADMIN});
        db.put("manager",  new String[]{"manager123",  "John Manager",         UserSession.ROLE_MANAGER});
        db.put("employee", new String[]{"emp123",      "Jane Employee",        UserSession.ROLE_EMPLOYEE});
        db.put("hr",       new String[]{"hr123",       "Helena HR",            UserSession.ROLE_HR});
        db.put("sales",    new String[]{"sales123",    "Sam Sales",            UserSession.ROLE_SALES});
        String[] row = db.get(u == null ? "" : u.toLowerCase());
        if (row == null || !row[0].equals(p)) {
            return new UserSessionDTO(null, null, null, false);
        }
        String effectiveRole = (role == null || role.isEmpty()) ? row[2] : role;
        return new UserSessionDTO(u, row[1], effectiveRole, true);
    }

    // ==================== Orders ====================

    private List<OrderDTO> filterOrders(Map<String, Object> p) {
        String status = str(p, "status");
        String q = str(p, "q");
        List<OrderDTO> out = new ArrayList<>();
        for (OrderDTO o : orders) {
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase(o.getStatus())) continue;
            if (q != null && !q.isEmpty()) {
                String hay = (o.getOrderId() + " " + o.getCustomerName() + " "
                        + o.getCarVIN() + " " + o.getCarModel()).toLowerCase();
                if (!hay.contains(q.toLowerCase())) continue;
            }
            out.add(o);
        }
        return out;
    }

    private Map<String, Integer> orderStats() {
        Map<String, Integer> s = new HashMap<>();
        s.put("total", orders.size());
        s.put("pending", 0); s.put("approved", 0); s.put("inTransit", 0);
        s.put("delivered", 0); s.put("cancelled", 0);
        for (OrderDTO o : orders) {
            switch (o.getStatus()) {
                case OrderDTO.PENDING:    s.merge("pending", 1, Integer::sum); break;
                case OrderDTO.APPROVED:   s.merge("approved", 1, Integer::sum); break;
                case OrderDTO.IN_TRANSIT: s.merge("inTransit", 1, Integer::sum); break;
                case OrderDTO.DELIVERED:  s.merge("delivered", 1, Integer::sum); break;
                case OrderDTO.CANCELLED:  s.merge("cancelled", 1, Integer::sum); break;
                default: break;
            }
        }
        return s;
    }

    private OrderDTO findOrder(String id) {
        for (OrderDTO o : orders) if (o.getOrderId().equals(id)) return o;
        throw IntegrationException.sendFailed("orders", "Order not found: " + id);
    }

    private OrderDTO createOrder(OrderDTO dto) {
        dto.setOrderId("ORD-" + orderSeq.incrementAndGet());
        if (dto.getDate() == null) dto.setDate(LocalDate.now());
        dto.setStatus(OrderDTO.PENDING);
        dto.setPaymentStatus(OrderDTO.PAY_PENDING);
        if (dto.getAmountPaid() == null) dto.setAmountPaid(BigDecimal.ZERO);
        orders.add(0, dto);
        activity("Order created: " + dto.getOrderId() + " (" + dto.getCustomerName() + ")");
        return dto;
    }

    private OrderDTO updateStatus(String orderId, String status, String action) {
        OrderDTO o = findOrder(orderId);
        o.setStatus(status);
        activity("Order " + orderId + ": " + action);
        return o;
    }

    private OrderDTO ship(Map<String, Object> p) {
        OrderDTO o = findOrder(str(p, "orderId"));
        o.setStatus(OrderDTO.IN_TRANSIT);
        o.setCourier(str(p, "courier"));
        o.setTrackingNumber(str(p, "tracking"));
        activity("Order " + o.getOrderId() + " shipped via " + o.getCourier());
        return o;
    }

    private OrderDTO pay(Map<String, Object> p) {
        OrderDTO o = findOrder(str(p, "orderId"));
        BigDecimal amt = (BigDecimal) p.get("amount");
        boolean fail = Boolean.TRUE.equals(p.get("simulateFail"));
        if (fail) {
            o.setPaymentStatus(OrderDTO.PAY_FAILED);
            activity("Payment FAILED for " + o.getOrderId());
            return o;
        }
        BigDecimal paid = o.getAmountPaid() == null ? BigDecimal.ZERO : o.getAmountPaid();
        paid = paid.add(amt == null ? BigDecimal.ZERO : amt);
        o.setAmountPaid(paid);
        if (paid.compareTo(o.getAmount()) >= 0) {
            o.setPaymentStatus(OrderDTO.PAY_PAID);
        } else {
            o.setPaymentStatus(OrderDTO.PAY_PARTIAL);
        }
        activity("Payment " + amt + " recorded for " + o.getOrderId() + " (" + o.getPaymentStatus() + ")");
        return o;
    }

    private OrderDTO cancel(Map<String, Object> p) {
        OrderDTO o = findOrder(str(p, "orderId"));
        o.setStatus(OrderDTO.CANCELLED);
        o.setCancellationReason(str(p, "reason"));
        if (OrderDTO.PAY_PAID.equals(o.getPaymentStatus()) || OrderDTO.PAY_PARTIAL.equals(o.getPaymentStatus())) {
            o.setPaymentStatus(OrderDTO.PAY_REFUNDED);
        }
        activity("Order " + o.getOrderId() + " cancelled: " + o.getCancellationReason());
        return o;
    }

    // ==================== HR ====================

    private List<EmployeeDTO> filterEmployees(Map<String, Object> p) {
        String dept = str(p, "department");
        String status = str(p, "status");
        String q = str(p, "q");
        List<EmployeeDTO> out = new ArrayList<>();
        for (EmployeeDTO e : employees) {
            if (e.getRecruitmentStage() != null && !"SELECTED".equals(e.getRecruitmentStage())
                    && !"HIRED".equals(e.getRecruitmentStage())) continue; // skip recruitment-only rows
            if (dept != null && !dept.isEmpty() && !dept.equalsIgnoreCase(e.getDepartment())) continue;
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase(e.getStatus())) continue;
            if (q != null && !q.isEmpty()) {
                String hay = (e.getEmployeeId() + " " + e.getName() + " " + e.getRole()).toLowerCase();
                if (!hay.contains(q.toLowerCase())) continue;
            }
            out.add(e);
        }
        return out;
    }

    private EmployeeDTO updateEmployee(EmployeeDTO dto) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId().equals(dto.getEmployeeId())) {
                dto.recalcNetPay();
                employees.set(i, dto);
                activity("Employee updated: " + dto.getEmployeeId());
                return dto;
            }
        }
        throw IntegrationException.sendFailed("hr", "Employee not found: " + dto.getEmployeeId());
    }

    private List<EmployeeDTO> recruitmentPipeline() {
        List<EmployeeDTO> out = new ArrayList<>();
        for (EmployeeDTO e : employees)
            if (e.getRecruitmentStage() != null) out.add(e);
        return out;
    }

    private EmployeeDTO moveRecruitmentStage(Map<String, Object> p) {
        String id = str(p, "employeeId");
        String stage = str(p, "stage");
        Integer score = (Integer) p.get("score");
        for (EmployeeDTO e : employees) {
            if (e.getEmployeeId().equals(id)) {
                e.setRecruitmentStage(stage);
                if (score != null) e.setInterviewScore(score);
                activity("Candidate " + id + " moved to " + stage);
                return e;
            }
        }
        throw IntegrationException.sendFailed("hr", "Candidate not found: " + id);
    }

    private List<EmployeeDTO> onboardingList() {
        List<EmployeeDTO> out = new ArrayList<>();
        for (EmployeeDTO e : employees)
            if (EmployeeDTO.STATUS_NEW.equals(e.getStatus()) || "SELECTED".equals(e.getRecruitmentStage()))
                out.add(e);
        return out;
    }

    private List<EmployeeDTO> payrollList() {
        List<EmployeeDTO> out = new ArrayList<>();
        for (EmployeeDTO e : employees)
            if (e.getGrossSalary() != null) out.add(e);
        return out;
    }

    private String transferSalary(String employeeId) {
        activity("Salary transferred for " + employeeId);
        return "OK";
    }

    private String logAttendance(Map<String, Object> p) {
        String id = "ATT-" + attendanceSeq.incrementAndGet();
        attendanceLog.add(0, new String[]{id,
                str(p, "employeeId"), str(p, "checkIn"), str(p, "checkOut"), str(p, "overtime")});
        activity("Attendance logged for " + str(p, "employeeId"));
        return id;
    }

    private String leaveAction(Map<String, Object> p) {
        String id = str(p, "id");
        String action = str(p, "action"); // APPROVED / REJECTED
        for (String[] r : leaveRequests) {
            if (r[0].equals(id)) {
                r[5] = action;
                activity("Leave " + id + " " + action);
                return "OK";
            }
        }
        throw IntegrationException.sendFailed("hr", "Leave request not found: " + id);
    }

    private List<EmployeeDTO> performanceList() {
        List<EmployeeDTO> out = new ArrayList<>();
        for (EmployeeDTO e : employees)
            if (e.getStatus() != null && EmployeeDTO.STATUS_ACTIVE.equals(e.getStatus())) out.add(e);
        return out;
    }

    private Map<String, Integer> hrStats() {
        int total = 0, active = 0, newJoiners = 0, onLeave = 0, pendingLeave = 0;
        for (EmployeeDTO e : employees) {
            if (e.getStatus() == null) continue;
            total++;
            if (EmployeeDTO.STATUS_ACTIVE.equals(e.getStatus())) active++;
            if (EmployeeDTO.STATUS_NEW.equals(e.getStatus())) newJoiners++;
            if (EmployeeDTO.STATUS_ON_LEAVE.equals(e.getStatus())) onLeave++;
        }
        for (String[] r : leaveRequests) if ("PENDING".equals(r[5])) pendingLeave++;
        Map<String, Integer> s = new HashMap<>();
        s.put("total", total); s.put("active", active); s.put("newJoiners", newJoiners);
        s.put("onLeave", onLeave); s.put("pendingLeave", pendingLeave);
        return s;
    }

    // ==================== Activity log ====================

    public List<String[]> getActivityLog() { return new ArrayList<>(activityLog); }

    private void activity(String msg) {
        activityLog.add(0, new String[]{java.time.LocalDateTime.now().toString(), msg});
        if (activityLog.size() > 50) activityLog.remove(activityLog.size() - 1);
    }

    // ==================== Seed data ====================

    private void seedOrders() {
        String[][] seed = {
                {"ORD-1001", "Rakesh Industries",   "VIN-AX1001", "Model-S Sedan",    "Steel A1",  "2450000", "PENDING",    "PENDING"},
                {"ORD-1002", "Tata Motors Dealer",  "VIN-AX1002", "Model-X SUV",      "Alloy B2",  "3900000", "APPROVED",   "PARTIAL"},
                {"ORD-1003", "Mahindra Showroom",   "VIN-AX1003", "Model-T Truck",    "Reinforced","5100000", "IN_TRANSIT", "PAID"},
                {"ORD-1004", "Pearson Motors",      "VIN-AX1004", "Model-S Sedan",    "Steel A1",  "2475000", "DELIVERED",  "PAID"},
                {"ORD-1005", "Gupta Automobiles",   "VIN-AX1005", "Model-EV Electric","Aluminum",  "4200000", "PENDING",    "PENDING"},
                {"ORD-1006", "Sharma & Sons",       "VIN-AX1006", "Model-X SUV",      "Alloy B2",  "3850000", "APPROVED",   "PENDING"},
                {"ORD-1007", "Orion Logistics",     "VIN-AX1007", "Model-T Truck",    "Reinforced","5250000", "IN_TRANSIT", "PARTIAL"},
                {"ORD-1008", "Blue Horizon Cars",   "VIN-AX1008", "Model-S Sedan",    "Steel A1",  "2510000", "APPROVED",   "PAID"},
                {"ORD-1009", "Nova Fleet Services", "VIN-AX1009", "Model-EV Electric","Aluminum",  "4350000", "REVISION",   "PENDING"},
                {"ORD-1010", "Metro Auto Hub",      "VIN-AX1010", "Model-X SUV",      "Alloy B2",  "3920000", "DELIVERED",  "PAID"},
                {"ORD-1011", "Sunrise Motors",      "VIN-AX1011", "Model-S Sedan",    "Steel A1",  "2460000", "PENDING",    "PENDING"},
                {"ORD-1012", "Capital Cars Ltd",    "VIN-AX1012", "Model-T Truck",    "Reinforced","5180000", "CANCELLED",  "REFUNDED"},
                {"ORD-1013", "Eastern Autos",       "VIN-AX1013", "Model-X SUV",      "Alloy B2",  "3880000", "APPROVED",   "PARTIAL"},
                {"ORD-1014", "GreenDrive Co",       "VIN-AX1014", "Model-EV Electric","Aluminum",  "4400000", "PENDING",    "PENDING"},
                {"ORD-1015", "Highway Dealers",     "VIN-AX1015", "Model-S Sedan",    "Steel A1",  "2495000", "IN_TRANSIT", "PAID"},
        };
        for (String[] s : seed) {
            OrderDTO o = new OrderDTO(s[0], s[1], s[2], s[3], s[4],
                    new BigDecimal(s[5]), LocalDate.now().minusDays(seed.length - orders.size()),
                    s[6], s[7]);
            if ("PAID".equals(s[7])) o.setAmountPaid(o.getAmount());
            else if ("PARTIAL".equals(s[7])) o.setAmountPaid(o.getAmount().divide(new BigDecimal("2")));
            else o.setAmountPaid(BigDecimal.ZERO);
            if ("IN_TRANSIT".equals(s[6])) {
                o.setCourier("BlueDart Auto"); o.setTrackingNumber("BD" + (100000 + orders.size()));
            }
            orders.add(o);
        }
        orderSeq.set(1015);
    }

    private void seedEmployees() {
        String[] depts = {"Manufacturing", "Quality", "Assembly", "R&D", "HR", "Sales", "Finance"};
        String[] lines = {"Line-A", "Line-B", "Line-C", "Line-D", "N/A"};
        String[] shifts = {"Morning (06-14)", "Afternoon (14-22)", "Night (22-06)", "General (09-18)"};
        String[][] seed = {
                {"EMP-001", "Arjun Verma",     "Line Supervisor",     "Manufacturing", "ACTIVE"},
                {"EMP-002", "Priya Singh",     "Quality Inspector",   "Quality",       "ACTIVE"},
                {"EMP-003", "Rohan Das",       "Assembly Technician", "Assembly",      "ACTIVE"},
                {"EMP-004", "Meera Nair",      "R&D Engineer",        "R&D",           "ACTIVE"},
                {"EMP-005", "Vikram Shah",     "HR Executive",        "HR",            "ACTIVE"},
                {"EMP-006", "Anita Rao",       "Payroll Manager",     "HR",            "ACTIVE"},
                {"EMP-007", "Karan Mehta",     "Sales Associate",     "Sales",         "ACTIVE"},
                {"EMP-008", "Sneha Kapoor",    "Accountant",          "Finance",       "ACTIVE"},
                {"EMP-009", "Deepak Iyer",     "Welder",              "Manufacturing", "ACTIVE"},
                {"EMP-010", "Ritu Sharma",     "QA Lead",             "Quality",       "ACTIVE"},
                {"EMP-011", "Ahmed Khan",      "Paint Specialist",    "Assembly",      "ON_LEAVE"},
                {"EMP-012", "Latha Menon",     "Project Lead",        "R&D",           "ACTIVE"},
                {"EMP-013", "Suresh Patil",    "Logistics Coord.",    "Manufacturing", "ACTIVE"},
                {"EMP-014", "Kavita Joshi",    "Recruitment Lead",    "HR",            "ACTIVE"},
                {"EMP-015", "Imran Ali",       "Sales Manager",       "Sales",         "ACTIVE"},
                {"EMP-016", "Pooja Bansal",    "Finance Analyst",     "Finance",       "ACTIVE"},
                {"EMP-017", "Gagan Chauhan",   "Assembly Technician", "Assembly",      "NEW"},
                {"EMP-018", "Rhea D'Souza",    "Quality Analyst",     "Quality",       "NEW"},
                {"EMP-019", "Nikhil Bhat",     "Welder",              "Manufacturing", "NEW"},
                {"EMP-020", "Tanvi Shetty",    "R&D Intern",          "R&D",           "NEW"},
        };
        int i = 0;
        for (String[] s : seed) {
            EmployeeDTO e = new EmployeeDTO(s[0], s[1], s[2], s[3],
                    lines[i % lines.length], shifts[i % shifts.length],
                    LocalDate.now().minusDays(30 + i * 15), s[4]);
            BigDecimal gross = new BigDecimal(45000 + (i * 1700));
            e.setGrossSalary(gross);
            e.setDeductions(gross.multiply(new BigDecimal("0.08")));
            e.setTaxRecord(gross.multiply(new BigDecimal("0.12")));
            e.recalcNetPay();
            e.setPerformanceRating(3 + (i % 3));
            e.setPerformanceFeedback("Consistent contribution to " + s[3].toLowerCase() + " team.");
            e.setPromotionStatus(i % 5 == 0 ? "Under Review" : "Stable");
            if (EmployeeDTO.STATUS_NEW.equals(s[4])) {
                e.setBackgroundCheckPassed(i % 2 == 0);
                e.setDocumentsVerified(i % 3 != 0);
                e.setOnboardingVerified(false);
            } else {
                e.setBackgroundCheckPassed(true);
                e.setDocumentsVerified(true);
                e.setOnboardingVerified(true);
            }
            employees.add(e);
            i++;
        }

        // Recruitment-only pipeline candidates
        String[][] candidates = {
                {"CAN-101", "Ankit Jain",      "Assembly Technician", "Assembly",      "APPLIED"},
                {"CAN-102", "Divya Hegde",     "Quality Inspector",   "Quality",       "SHORTLISTED"},
                {"CAN-103", "Farhan Ahmed",    "R&D Engineer",        "R&D",           "INTERVIEW"},
                {"CAN-104", "Isha Kulkarni",   "HR Executive",        "HR",            "INTERVIEW"},
                {"CAN-105", "Manoj Pillai",    "Welder",              "Manufacturing", "APPLIED"},
                {"CAN-106", "Nina George",     "Sales Associate",     "Sales",         "SELECTED"},
        };
        int j = 0;
        for (String[] c : candidates) {
            EmployeeDTO e = new EmployeeDTO(c[0], c[1], c[2], c[3],
                    "N/A", "General (09-18)", LocalDate.now().plusDays(5), "NEW");
            e.setRecruitmentStage(c[4]);
            e.setInterviewScore("INTERVIEW".equals(c[4]) || "SELECTED".equals(c[4]) ? 70 + (j * 3) : null);
            employees.add(e);
            j++;
        }
    }

    private void seedLeave() {
        String[][] seed = {
                {"LV-500", "EMP-003", "Casual",  "2026-04-20", "2026-04-22", "PENDING"},
                {"LV-501", "EMP-007", "Sick",    "2026-04-15", "2026-04-16", "APPROVED"},
                {"LV-502", "EMP-011", "Earned",  "2026-04-10", "2026-04-25", "APPROVED"},
                {"LV-503", "EMP-002", "Casual",  "2026-04-18", "2026-04-18", "PENDING"},
                {"LV-504", "EMP-015", "Sick",    "2026-04-12", "2026-04-13", "REJECTED"},
                {"LV-505", "EMP-009", "Earned",  "2026-05-01", "2026-05-07", "PENDING"},
        };
        leaveRequests.addAll(Arrays.asList(seed));
        leaveSeq.set(505);
    }

    private void seedAttendance() {
        String[][] seed = {
                {"ATT-901", "EMP-001", "06:05", "14:10", "10m"},
                {"ATT-902", "EMP-003", "06:00", "15:30", "1h30m"},
                {"ATT-903", "EMP-009", "14:02", "22:15", "15m"},
                {"ATT-904", "EMP-013", "22:00", "06:05", "05m"},
                {"ATT-905", "EMP-002", "09:00", "18:30", "30m"},
        };
        attendanceLog.addAll(Arrays.asList(seed));
        attendanceSeq.set(905);
    }

    // ==================== helpers ====================
    private static String str(Map<String, Object> m, String k) {
        Object v = m == null ? null : m.get(k);
        return v == null ? null : v.toString();
    }
}
