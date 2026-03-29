package com.erp.service.interfaces;

import com.erp.model.Project;
import com.erp.model.ProjectTask;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ProjectService Interface - CONTRACT for Project Management Module Backend Team
 *
 * Covers: Project Planning, Scheduling, Resource Management, Task Management, Budgeting
 */
public interface ProjectService {

    // ==================== PROJECT MANAGEMENT ====================

    /**
     * Get all projects.
     * @return List of all projects
     */
    List<Project> getAllProjects();

    /**
     * Get projects by status.
     * @param status PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED
     * @return List of projects
     */
    List<Project> getProjectsByStatus(String status);

    /**
     * Get projects for a customer.
     * @param customerId The customer ID
     * @return List of projects
     */
    List<Project> getProjectsByCustomer(int customerId);

    /**
     * Get projects managed by an employee.
     * @param projectManagerId The PM's employee ID
     * @return List of projects
     */
    List<Project> getProjectsByManager(int projectManagerId);

    /**
     * Get projects an employee is assigned to.
     * @param employeeId The employee ID
     * @return List of projects
     */
    List<Project> getProjectsByTeamMember(int employeeId);

    /**
     * Get overdue projects.
     * @return List of overdue projects
     */
    List<Project> getOverdueProjects();

    /**
     * Get a project by ID.
     * @param projectId The project ID
     * @return Project or null
     */
    Project getProjectById(int projectId);

    /**
     * Get project by code.
     * @param projectCode The project code
     * @return Project or null
     */
    Project getProjectByCode(String projectCode);

    /**
     * Create a new project.
     * @param project The project data
     * @return Created project with ID
     */
    Project createProject(Project project);

    /**
     * Update a project.
     * @param project The project to update
     * @return true if successful
     */
    boolean updateProject(Project project);

    /**
     * Update project status.
     * @param projectId The project ID
     * @param newStatus The new status
     * @return true if successful
     */
    boolean updateProjectStatus(int projectId, String newStatus);

    /**
     * Update project progress percentage.
     * @param projectId The project ID
     * @param percentComplete The completion percentage
     * @return true if successful
     */
    boolean updateProjectProgress(int projectId, int percentComplete);

    /**
     * Delete a project.
     * @param projectId The project ID
     * @return true if successful
     */
    boolean deleteProject(int projectId);


    // ==================== TASK MANAGEMENT ====================

    /**
     * Get all tasks for a project.
     * @param projectId The project ID
     * @return List of tasks
     */
    List<ProjectTask> getTasksByProject(int projectId);

    /**
     * Get tasks assigned to an employee.
     * @param employeeId The employee ID
     * @return List of tasks
     */
    List<ProjectTask> getTasksByAssignee(int employeeId);

    /**
     * Get tasks by status.
     * @param projectId The project ID (0 for all projects)
     * @param status TODO, IN_PROGRESS, REVIEW, COMPLETED, BLOCKED
     * @return List of tasks
     */
    List<ProjectTask> getTasksByStatus(int projectId, String status);

    /**
     * Get overdue tasks.
     * @param projectId The project ID (0 for all projects)
     * @return List of overdue tasks
     */
    List<ProjectTask> getOverdueTasks(int projectId);

    /**
     * Get a task by ID.
     * @param taskId The task ID
     * @return Task or null
     */
    ProjectTask getTaskById(int taskId);

    /**
     * Create a new task.
     * @param task The task data
     * @return Created task with ID
     */
    ProjectTask createTask(ProjectTask task);

    /**
     * Update a task.
     * @param task The task to update
     * @return true if successful
     */
    boolean updateTask(ProjectTask task);

    /**
     * Update task status.
     * @param taskId The task ID
     * @param newStatus The new status
     * @return true if successful
     */
    boolean updateTaskStatus(int taskId, String newStatus);

    /**
     * Assign task to employee.
     * @param taskId The task ID
     * @param employeeId The employee ID
     * @return true if successful
     */
    boolean assignTask(int taskId, int employeeId);

    /**
     * Log time on a task.
     * @param taskId The task ID
     * @param hours Hours worked
     * @param notes Work notes
     * @return true if successful
     */
    boolean logTime(int taskId, int hours, String notes);

    /**
     * Delete a task.
     * @param taskId The task ID
     * @return true if successful
     */
    boolean deleteTask(int taskId);


    // ==================== TEAM MANAGEMENT ====================

    /**
     * Get team members for a project.
     * @param projectId The project ID
     * @return List of employee IDs
     */
    List<Integer> getProjectTeam(int projectId);

    /**
     * Add team member to project.
     * @param projectId The project ID
     * @param employeeId The employee ID
     * @return true if successful
     */
    boolean addTeamMember(int projectId, int employeeId);

    /**
     * Remove team member from project.
     * @param projectId The project ID
     * @param employeeId The employee ID
     * @return true if successful
     */
    boolean removeTeamMember(int projectId, int employeeId);


    // ==================== PROJECT ANALYTICS ====================

    /**
     * Get project statistics.
     * @param projectId The project ID
     * @return Map with keys: totalTasks, completedTasks, hoursEstimated, hoursActual, etc.
     */
    Map<String, Object> getProjectStats(int projectId);

    /**
     * Get project budget status.
     * @param projectId The project ID
     * @return Map with keys: budget, spent, remaining, percentUsed
     */
    Map<String, BigDecimal> getBudgetStatus(int projectId);

    /**
     * Get employee utilization.
     * @param employeeId The employee ID
     * @param startDate Start date
     * @param endDate End date
     * @return Hours allocated vs capacity
     */
    Map<String, Integer> getEmployeeUtilization(int employeeId, LocalDate startDate, LocalDate endDate);
}
