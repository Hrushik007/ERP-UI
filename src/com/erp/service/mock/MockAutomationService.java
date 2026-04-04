package com.erp.service.mock;

import com.erp.service.interfaces.AutomationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MockAutomationService provides sample data for Automation Module UI.
 *
 * Uses Map-based data structures as defined in the AutomationService interface.
 * This is different from other modules that use dedicated model classes.
 *
 * Demonstrates the Singleton pattern - only one instance exists.
 */
public class MockAutomationService implements AutomationService {

    private List<Map<String, Object>> workflows;
    private List<Map<String, Object>> workflowInstances;
    private List<Map<String, Object>> approvals;
    private List<Map<String, Object>> notifications;
    private List<Map<String, Object>> scheduledTasks;
    private List<Map<String, Object>> taskExecutionHistory;

    private int nextWorkflowId = 1;
    private int nextInstanceId = 1;
    private int nextApprovalId = 1;
    private int nextNotificationId = 1;
    private int nextTaskId = 1;

    private static MockAutomationService instance;

    public static synchronized MockAutomationService getInstance() {
        if (instance == null) {
            instance = new MockAutomationService();
        }
        return instance;
    }

    private MockAutomationService() {
        workflows = new ArrayList<>();
        workflowInstances = new ArrayList<>();
        approvals = new ArrayList<>();
        notifications = new ArrayList<>();
        scheduledTasks = new ArrayList<>();
        taskExecutionHistory = new ArrayList<>();
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Sample workflows
        createSampleWorkflow("Purchase Order Approval", "AUTO_APPROVE", "Automatically routes POs for approval based on amount", true,
                "TRIGGER: PO Created | IF amount > 5000 THEN Manager Approval | ELSE Auto-Approve");
        createSampleWorkflow("Leave Request Routing", "APPROVAL_CHAIN", "Routes leave requests through management chain", true,
                "TRIGGER: Leave Request | ROUTE TO: Direct Manager | IF > 5 days THEN HR Approval");
        createSampleWorkflow("Invoice Processing", "SEQUENTIAL", "Processes invoices through validation and payment", true,
                "TRIGGER: Invoice Received | VALIDATE | MATCH TO PO | APPROVE | SCHEDULE PAYMENT");
        createSampleWorkflow("New Employee Onboarding", "PARALLEL", "Coordinates onboarding tasks across departments", false,
                "TRIGGER: Employee Created | PARALLEL: IT Setup, HR Docs, Facilities | NOTIFY Manager");
        createSampleWorkflow("Customer Complaint Escalation", "CONDITIONAL", "Escalates complaints based on severity", true,
                "TRIGGER: Ticket Priority HIGH | NOTIFY: Team Lead | IF not resolved 24h THEN ESCALATE Manager");

        // Sample workflow instances
        createSampleInstance("WF-1", "Purchase Order Approval", "IN_PROGRESS", "PO-2024-0042", LocalDateTime.now().minusHours(2));
        createSampleInstance("WF-1", "Purchase Order Approval", "COMPLETED", "PO-2024-0041", LocalDateTime.now().minusDays(1));
        createSampleInstance("WF-2", "Leave Request Routing", "IN_PROGRESS", "LR-2024-0015", LocalDateTime.now().minusHours(5));
        createSampleInstance("WF-3", "Invoice Processing", "WAITING", "INV-2024-0088", LocalDateTime.now().minusHours(1));

        // Sample approvals
        createSampleApproval("ORDER", "PO-2024-0042", "Purchase Order: Office Supplies - $7,500", "John Smith", "PENDING", 1);
        createSampleApproval("LEAVE_REQUEST", "LR-2024-0015", "Leave Request: Sarah Johnson - 5 days vacation", "Mike Brown", "PENDING", 1);
        createSampleApproval("ORDER", "PO-2024-0040", "Purchase Order: Server Equipment - $25,000", "Emily Davis", "APPROVED", 2);
        createSampleApproval("INVOICE", "INV-2024-0088", "Invoice: Vendor Payment - $12,000", "Alex Wilson", "PENDING", 1);
        createSampleApproval("LEAVE_REQUEST", "LR-2024-0014", "Leave Request: Chris Lee - 2 days sick leave", "Pat Taylor", "REJECTED", 3);

        // Sample notifications
        createSampleNotification(1, "IN_APP", "New approval pending", "You have a new purchase order awaiting approval: PO-2024-0042", false);
        createSampleNotification(1, "EMAIL", "Leave request submitted", "Sarah Johnson has submitted a leave request for your review", true);
        createSampleNotification(2, "IN_APP", "Task overdue", "Task 'Update Q2 Report' is overdue by 2 days", false);
        createSampleNotification(1, "IN_APP", "Invoice processed", "Invoice INV-2024-0087 has been processed and scheduled for payment", true);
        createSampleNotification(3, "SMS", "System alert", "Monthly backup completed successfully", true);
        createSampleNotification(1, "IN_APP", "Workflow completed", "Purchase Order PO-2024-0041 approval workflow completed", true);

        // Sample scheduled tasks
        createSampleTask("Daily Sales Report", "REPORT_GENERATION", "0 8 * * *", "Every day at 8:00 AM", true, LocalDateTime.now().minusHours(16));
        createSampleTask("Weekly Data Backup", "DATA_BACKUP", "0 2 * * 0", "Every Sunday at 2:00 AM", true, LocalDateTime.now().minusDays(3));
        createSampleTask("Monthly Invoice Reminder", "NOTIFICATION", "0 9 1 * *", "1st of every month at 9:00 AM", true, LocalDateTime.now().minusDays(15));
        createSampleTask("Quarterly Inventory Audit", "AUDIT", "0 6 1 1,4,7,10 *", "1st of Jan, Apr, Jul, Oct at 6:00 AM", false, LocalDateTime.now().minusDays(60));
        createSampleTask("Hourly System Health Check", "HEALTH_CHECK", "0 * * * *", "Every hour", true, LocalDateTime.now().minusMinutes(45));

        // Sample execution history
        createSampleExecution("TSK-1", "Daily Sales Report", "SUCCESS", LocalDateTime.now().minusHours(16), 45);
        createSampleExecution("TSK-1", "Daily Sales Report", "SUCCESS", LocalDateTime.now().minusHours(40), 42);
        createSampleExecution("TSK-2", "Weekly Data Backup", "SUCCESS", LocalDateTime.now().minusDays(3), 320);
        createSampleExecution("TSK-3", "Monthly Invoice Reminder", "SUCCESS", LocalDateTime.now().minusDays(15), 12);
        createSampleExecution("TSK-5", "Hourly System Health Check", "SUCCESS", LocalDateTime.now().minusMinutes(45), 5);
        createSampleExecution("TSK-5", "Hourly System Health Check", "FAILED", LocalDateTime.now().minusMinutes(105), 3);
    }

    private void createSampleWorkflow(String name, String type, String description, boolean enabled, String definition) {
        Map<String, Object> wf = new HashMap<>();
        wf.put("id", "WF-" + nextWorkflowId++);
        wf.put("name", name);
        wf.put("type", type);
        wf.put("description", description);
        wf.put("enabled", enabled);
        wf.put("definition", definition);
        wf.put("createdAt", LocalDateTime.now().minusDays((int)(Math.random() * 90) + 10)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        wf.put("triggerCount", (int)(Math.random() * 50) + 5);
        workflows.add(wf);
    }

    private void createSampleInstance(String workflowId, String workflowName, String status, String triggerRef, LocalDateTime startedAt) {
        Map<String, Object> inst = new HashMap<>();
        inst.put("id", "INST-" + nextInstanceId++);
        inst.put("workflowId", workflowId);
        inst.put("workflowName", workflowName);
        inst.put("status", status);
        inst.put("triggerRef", triggerRef);
        inst.put("startedAt", startedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        inst.put("currentStep", status.equals("COMPLETED") ? "Done" : "Awaiting Approval");
        workflowInstances.add(inst);
    }

    private void createSampleApproval(String itemType, String itemId, String description, String requestedBy, String status, int approverId) {
        Map<String, Object> approval = new HashMap<>();
        approval.put("id", "APR-" + nextApprovalId++);
        approval.put("itemType", itemType);
        approval.put("itemId", itemId);
        approval.put("description", description);
        approval.put("requestedBy", requestedBy);
        approval.put("status", status);
        approval.put("approverId", approverId);
        approval.put("createdAt", LocalDateTime.now().minusDays((int)(Math.random() * 7))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        approval.put("comments", "");
        approvals.add(approval);
    }

    private void createSampleNotification(int userId, String type, String subject, String message, boolean read) {
        Map<String, Object> notif = new HashMap<>();
        notif.put("id", "NTF-" + nextNotificationId++);
        notif.put("userId", userId);
        notif.put("type", type);
        notif.put("subject", subject);
        notif.put("message", message);
        notif.put("read", read);
        notif.put("createdAt", LocalDateTime.now().minusHours((int)(Math.random() * 72))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        notifications.add(notif);
    }

    private void createSampleTask(String name, String taskType, String cron, String schedule, boolean enabled, LocalDateTime lastRun) {
        Map<String, Object> task = new HashMap<>();
        task.put("id", "TSK-" + nextTaskId++);
        task.put("name", name);
        task.put("taskType", taskType);
        task.put("cronExpression", cron);
        task.put("schedule", schedule);
        task.put("enabled", enabled);
        task.put("lastRun", lastRun.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        task.put("createdAt", LocalDateTime.now().minusDays((int)(Math.random() * 60) + 10)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        scheduledTasks.add(task);
    }

    private void createSampleExecution(String taskId, String taskName, String status, LocalDateTime executedAt, int durationSeconds) {
        Map<String, Object> exec = new HashMap<>();
        exec.put("taskId", taskId);
        exec.put("taskName", taskName);
        exec.put("status", status);
        exec.put("executedAt", executedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        exec.put("durationSeconds", durationSeconds);
        exec.put("message", status.equals("SUCCESS") ? "Completed successfully" : "Error: Connection timeout");
        taskExecutionHistory.add(exec);
    }

    // ==================== WORKFLOW MANAGEMENT ====================

    @Override
    public List<Map<String, Object>> getAllWorkflows() {
        return new ArrayList<>(workflows);
    }

    @Override
    public List<Map<String, Object>> getActiveWorkflowInstances() {
        return workflowInstances.stream()
                .filter(i -> !"COMPLETED".equals(i.get("status")) && !"CANCELLED".equals(i.get("status")))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getWorkflowById(String workflowId) {
        return workflows.stream()
                .filter(w -> workflowId.equals(w.get("id")))
                .findFirst().orElse(null);
    }

    @Override
    public String createWorkflow(Map<String, Object> workflow) {
        String id = "WF-" + nextWorkflowId++;
        workflow.put("id", id);
        workflow.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        workflow.put("triggerCount", 0);
        if (!workflow.containsKey("enabled")) workflow.put("enabled", true);
        workflows.add(workflow);
        return id;
    }

    @Override
    public boolean updateWorkflow(String workflowId, Map<String, Object> workflow) {
        for (int i = 0; i < workflows.size(); i++) {
            if (workflowId.equals(workflows.get(i).get("id"))) {
                workflow.put("id", workflowId);
                workflow.put("createdAt", workflows.get(i).get("createdAt"));
                workflow.put("triggerCount", workflows.get(i).get("triggerCount"));
                workflows.set(i, workflow);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setWorkflowEnabled(String workflowId, boolean enabled) {
        for (Map<String, Object> wf : workflows) {
            if (workflowId.equals(wf.get("id"))) {
                wf.put("enabled", enabled);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteWorkflow(String workflowId) {
        return workflows.removeIf(w -> workflowId.equals(w.get("id")));
    }

    @Override
    public String triggerWorkflow(String workflowId, Map<String, Object> triggerData) {
        Map<String, Object> wf = getWorkflowById(workflowId);
        if (wf == null) return null;

        String instanceId = "INST-" + nextInstanceId++;
        Map<String, Object> inst = new HashMap<>();
        inst.put("id", instanceId);
        inst.put("workflowId", workflowId);
        inst.put("workflowName", wf.get("name"));
        inst.put("status", "IN_PROGRESS");
        inst.put("triggerRef", triggerData.getOrDefault("ref", "Manual Trigger"));
        inst.put("startedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        inst.put("currentStep", "Starting");
        workflowInstances.add(inst);

        wf.put("triggerCount", (int) wf.getOrDefault("triggerCount", 0) + 1);
        return instanceId;
    }

    // ==================== APPROVAL WORKFLOWS ====================

    @Override
    public List<Map<String, Object>> getPendingApprovals(int userId) {
        return approvals.stream()
                .filter(a -> (int) a.get("approverId") == userId && "PENDING".equals(a.get("status")))
                .collect(Collectors.toList());
    }

    @Override
    public boolean approve(String approvalId, int approverId, String comments) {
        for (Map<String, Object> a : approvals) {
            if (approvalId.equals(a.get("id"))) {
                a.put("status", "APPROVED");
                a.put("comments", comments);
                a.put("actionBy", approverId);
                a.put("actionAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean reject(String approvalId, int rejecterId, String reason) {
        for (Map<String, Object> a : approvals) {
            if (approvalId.equals(a.get("id"))) {
                a.put("status", "REJECTED");
                a.put("comments", reason);
                a.put("actionBy", rejecterId);
                a.put("actionAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getApprovalHistory(String itemType, String itemId) {
        return approvals.stream()
                .filter(a -> itemType.equals(a.get("itemType")) && itemId.equals(a.get("itemId")))
                .collect(Collectors.toList());
    }

    // ==================== NOTIFICATIONS ====================

    @Override
    public boolean sendNotification(int userId, String type, String subject, String message) {
        Map<String, Object> notif = new HashMap<>();
        notif.put("id", "NTF-" + nextNotificationId++);
        notif.put("userId", userId);
        notif.put("type", type);
        notif.put("subject", subject);
        notif.put("message", message);
        notif.put("read", false);
        notif.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        notifications.add(notif);
        return true;
    }

    @Override
    public int sendBulkNotification(List<Integer> userIds, String type, String subject, String message) {
        int count = 0;
        for (int userId : userIds) {
            if (sendNotification(userId, type, subject, message)) count++;
        }
        return count;
    }

    @Override
    public List<Map<String, Object>> getNotifications(int userId, boolean unreadOnly) {
        return notifications.stream()
                .filter(n -> (int) n.get("userId") == userId)
                .filter(n -> !unreadOnly || !(boolean) n.get("read"))
                .collect(Collectors.toList());
    }

    @Override
    public boolean markNotificationRead(String notificationId) {
        for (Map<String, Object> n : notifications) {
            if (notificationId.equals(n.get("id"))) {
                n.put("read", true);
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Boolean> getNotificationPreferences(int userId) {
        Map<String, Boolean> prefs = new HashMap<>();
        prefs.put("EMAIL", true);
        prefs.put("SMS", false);
        prefs.put("IN_APP", true);
        return prefs;
    }

    @Override
    public boolean updateNotificationPreferences(int userId, Map<String, Boolean> preferences) {
        return true;
    }

    // ==================== SCHEDULED TASKS ====================

    @Override
    public List<Map<String, Object>> getScheduledTasks() {
        return new ArrayList<>(scheduledTasks);
    }

    @Override
    public String createScheduledTask(Map<String, Object> task) {
        String id = "TSK-" + nextTaskId++;
        task.put("id", id);
        task.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        task.put("lastRun", "Never");
        if (!task.containsKey("enabled")) task.put("enabled", true);
        scheduledTasks.add(task);
        return id;
    }

    @Override
    public boolean updateScheduledTask(String taskId, Map<String, Object> task) {
        for (int i = 0; i < scheduledTasks.size(); i++) {
            if (taskId.equals(scheduledTasks.get(i).get("id"))) {
                task.put("id", taskId);
                task.put("createdAt", scheduledTasks.get(i).get("createdAt"));
                task.put("lastRun", scheduledTasks.get(i).get("lastRun"));
                scheduledTasks.set(i, task);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteScheduledTask(String taskId) {
        return scheduledTasks.removeIf(t -> taskId.equals(t.get("id")));
    }

    @Override
    public List<Map<String, Object>> getTaskExecutionHistory(String taskId, int limit) {
        return taskExecutionHistory.stream()
                .filter(e -> taskId == null || taskId.equals(e.get("taskId")))
                .sorted((a, b) -> ((String) b.get("executedAt")).compareTo((String) a.get("executedAt")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ==================== ADDITIONAL HELPERS (for UI) ====================

    /**
     * Get all approvals (not just pending).
     * @return List of all approvals
     */
    public List<Map<String, Object>> getAllApprovals() {
        return new ArrayList<>(approvals);
    }

    /**
     * Get all notifications (across all users).
     * @return List of all notifications
     */
    public List<Map<String, Object>> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    /**
     * Get all workflow instances.
     * @return List of all instances
     */
    public List<Map<String, Object>> getAllWorkflowInstances() {
        return new ArrayList<>(workflowInstances);
    }

    /**
     * Get workflow count by enabled status.
     * @return Map with enabled/disabled counts
     */
    public Map<String, Integer> getWorkflowCounts() {
        Map<String, Integer> counts = new HashMap<>();
        int enabled = 0, disabled = 0;
        for (Map<String, Object> wf : workflows) {
            if (Boolean.TRUE.equals(wf.get("enabled"))) enabled++;
            else disabled++;
        }
        counts.put("total", workflows.size());
        counts.put("enabled", enabled);
        counts.put("disabled", disabled);
        counts.put("instances", workflowInstances.size());
        return counts;
    }

    /**
     * Get approval count by status.
     * @return Map of status to count
     */
    public Map<String, Integer> getApprovalCountByStatus() {
        Map<String, Integer> result = new HashMap<>();
        result.put("PENDING", 0);
        result.put("APPROVED", 0);
        result.put("REJECTED", 0);
        for (Map<String, Object> a : approvals) {
            String status = (String) a.get("status");
            result.put(status, result.getOrDefault(status, 0) + 1);
        }
        return result;
    }
}
