package com.erp.service.interfaces;

import java.util.List;
import java.util.Map;

/**
 * AutomationService Interface - CONTRACT for Automation Module Backend Team
 *
 * Covers: Workflow Automation, Data Entry Automation, Document Management, Notifications
 */
public interface AutomationService {

    // ==================== WORKFLOW MANAGEMENT ====================

    /**
     * Get all workflow definitions.
     * @return List of workflow definitions
     */
    List<Map<String, Object>> getAllWorkflows();

    /**
     * Get active workflow instances.
     * @return List of running workflow instances
     */
    List<Map<String, Object>> getActiveWorkflowInstances();

    /**
     * Get workflow by ID.
     * @param workflowId The workflow ID
     * @return Workflow definition
     */
    Map<String, Object> getWorkflowById(String workflowId);

    /**
     * Create a new workflow definition.
     * @param workflow The workflow definition
     * @return Created workflow ID
     */
    String createWorkflow(Map<String, Object> workflow);

    /**
     * Update a workflow definition.
     * @param workflowId The workflow ID
     * @param workflow The updated definition
     * @return true if updated
     */
    boolean updateWorkflow(String workflowId, Map<String, Object> workflow);

    /**
     * Enable/disable a workflow.
     * @param workflowId The workflow ID
     * @param enabled Whether to enable or disable
     * @return true if successful
     */
    boolean setWorkflowEnabled(String workflowId, boolean enabled);

    /**
     * Delete a workflow.
     * @param workflowId The workflow ID
     * @return true if deleted
     */
    boolean deleteWorkflow(String workflowId);

    /**
     * Manually trigger a workflow.
     * @param workflowId The workflow ID
     * @param triggerData Data to pass to the workflow
     * @return Instance ID of the started workflow
     */
    String triggerWorkflow(String workflowId, Map<String, Object> triggerData);


    // ==================== APPROVAL WORKFLOWS ====================

    /**
     * Get pending approvals for a user.
     * @param userId The user/employee ID
     * @return List of pending approval items
     */
    List<Map<String, Object>> getPendingApprovals(int userId);

    /**
     * Approve an item.
     * @param approvalId The approval ID
     * @param approverId The approver's user ID
     * @param comments Approval comments
     * @return true if approved
     */
    boolean approve(String approvalId, int approverId, String comments);

    /**
     * Reject an item.
     * @param approvalId The approval ID
     * @param rejecterId The rejecter's user ID
     * @param reason Rejection reason
     * @return true if rejected
     */
    boolean reject(String approvalId, int rejecterId, String reason);

    /**
     * Get approval history for an item.
     * @param itemType Type of item (ORDER, PO, LEAVE_REQUEST, etc.)
     * @param itemId The item ID
     * @return List of approval actions
     */
    List<Map<String, Object>> getApprovalHistory(String itemType, String itemId);


    // ==================== NOTIFICATIONS ====================

    /**
     * Send a notification.
     * @param userId Target user ID
     * @param type Notification type (EMAIL, SMS, IN_APP)
     * @param subject Subject/title
     * @param message Message content
     * @return true if sent
     */
    boolean sendNotification(int userId, String type, String subject, String message);

    /**
     * Send bulk notifications.
     * @param userIds List of user IDs
     * @param type Notification type
     * @param subject Subject/title
     * @param message Message content
     * @return Number of notifications sent
     */
    int sendBulkNotification(List<Integer> userIds, String type, String subject, String message);

    /**
     * Get notifications for a user.
     * @param userId The user ID
     * @param unreadOnly Whether to return only unread
     * @return List of notifications
     */
    List<Map<String, Object>> getNotifications(int userId, boolean unreadOnly);

    /**
     * Mark notification as read.
     * @param notificationId The notification ID
     * @return true if marked
     */
    boolean markNotificationRead(String notificationId);

    /**
     * Get notification preferences for a user.
     * @param userId The user ID
     * @return Map of notification type to enabled status
     */
    Map<String, Boolean> getNotificationPreferences(int userId);

    /**
     * Update notification preferences.
     * @param userId The user ID
     * @param preferences The preferences map
     * @return true if updated
     */
    boolean updateNotificationPreferences(int userId, Map<String, Boolean> preferences);


    // ==================== SCHEDULED TASKS ====================

    /**
     * Get all scheduled tasks.
     * @return List of scheduled task definitions
     */
    List<Map<String, Object>> getScheduledTasks();

    /**
     * Create a scheduled task.
     * @param task The task definition (with schedule/cron expression)
     * @return Created task ID
     */
    String createScheduledTask(Map<String, Object> task);

    /**
     * Update a scheduled task.
     * @param taskId The task ID
     * @param task The updated definition
     * @return true if updated
     */
    boolean updateScheduledTask(String taskId, Map<String, Object> task);

    /**
     * Delete a scheduled task.
     * @param taskId The task ID
     * @return true if deleted
     */
    boolean deleteScheduledTask(String taskId);

    /**
     * Get task execution history.
     * @param taskId The task ID (null for all)
     * @param limit Number of records
     * @return List of execution records
     */
    List<Map<String, Object>> getTaskExecutionHistory(String taskId, int limit);
}
