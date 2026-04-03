package com.erp.service.mock;

import com.erp.model.Milestone;
import com.erp.model.Project;
import com.erp.model.ProjectTask;
import com.erp.service.interfaces.ProjectService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mock implementation of ProjectService with sample data.
 */
public class MockProjectService implements ProjectService {

    private static MockProjectService instance;
    private List<Project> projects;
    private List<ProjectTask> tasks;
    private List<Milestone> milestones;
    private int nextProjectId = 1;
    private int nextTaskId = 1;
    private int nextMilestoneId = 1;

    private MockProjectService() {
        projects = new ArrayList<>();
        tasks = new ArrayList<>();
        milestones = new ArrayList<>();
        initializeSampleData();
    }

    public static MockProjectService getInstance() {
        if (instance == null) {
            instance = new MockProjectService();
        }
        return instance;
    }

    private void initializeSampleData() {
        // Project 1: ERP Implementation
        Project p1 = new Project(nextProjectId++, "PRJ-001", "ERP System Implementation");
        p1.setDescription("Complete ERP system implementation for client");
        p1.setStatus("IN_PROGRESS");
        p1.setPriority("HIGH");
        p1.setStartDate(LocalDate.now().minusMonths(2));
        p1.setEndDate(LocalDate.now().plusMonths(4));
        p1.setBudgetAmount(new BigDecimal("150000.00"));
        p1.setActualCost(new BigDecimal("65000.00"));
        p1.setPercentComplete(45);
        p1.setCategory("CLIENT");
        p1.setProjectManagerId(1);
        projects.add(p1);

        // Project 2: Website Redesign
        Project p2 = new Project(nextProjectId++, "PRJ-002", "Corporate Website Redesign");
        p2.setDescription("Redesign company website with modern UI/UX");
        p2.setStatus("IN_PROGRESS");
        p2.setPriority("MEDIUM");
        p2.setStartDate(LocalDate.now().minusWeeks(3));
        p2.setEndDate(LocalDate.now().plusWeeks(5));
        p2.setBudgetAmount(new BigDecimal("35000.00"));
        p2.setActualCost(new BigDecimal("12000.00"));
        p2.setPercentComplete(30);
        p2.setCategory("INTERNAL");
        p2.setProjectManagerId(2);
        projects.add(p2);

        // Project 3: Mobile App Development
        Project p3 = new Project(nextProjectId++, "PRJ-003", "Mobile App Development");
        p3.setDescription("Develop iOS and Android mobile applications");
        p3.setStatus("PLANNED");
        p3.setPriority("HIGH");
        p3.setStartDate(LocalDate.now().plusWeeks(2));
        p3.setEndDate(LocalDate.now().plusMonths(6));
        p3.setBudgetAmount(new BigDecimal("200000.00"));
        p3.setActualCost(BigDecimal.ZERO);
        p3.setPercentComplete(0);
        p3.setCategory("CLIENT");
        p3.setProjectManagerId(1);
        projects.add(p3);

        // Project 4: Completed Project
        Project p4 = new Project(nextProjectId++, "PRJ-004", "Security Audit");
        p4.setDescription("Annual security audit and compliance review");
        p4.setStatus("COMPLETED");
        p4.setPriority("CRITICAL");
        p4.setStartDate(LocalDate.now().minusMonths(3));
        p4.setEndDate(LocalDate.now().minusMonths(1));
        p4.setBudgetAmount(new BigDecimal("25000.00"));
        p4.setActualCost(new BigDecimal("23500.00"));
        p4.setPercentComplete(100);
        p4.setCategory("INTERNAL");
        p4.setProjectManagerId(3);
        projects.add(p4);

        // Tasks for Project 1
        addTask(1, "Requirements Gathering", "IN_PROGRESS", "HIGH", 1, 40, 35);
        addTask(1, "System Design", "COMPLETED", "HIGH", 2, 60, 55);
        addTask(1, "Database Design", "COMPLETED", "MEDIUM", 2, 30, 28);
        addTask(1, "Backend Development", "IN_PROGRESS", "HIGH", 3, 120, 45);
        addTask(1, "Frontend Development", "TODO", "MEDIUM", 4, 100, 0);
        addTask(1, "Testing", "TODO", "HIGH", 5, 80, 0);
        addTask(1, "Deployment", "TODO", "CRITICAL", 1, 20, 0);

        // Tasks for Project 2
        addTask(2, "Design Mockups", "COMPLETED", "HIGH", 4, 24, 22);
        addTask(2, "Content Review", "IN_PROGRESS", "MEDIUM", 5, 16, 8);
        addTask(2, "Frontend Implementation", "IN_PROGRESS", "HIGH", 4, 60, 25);
        addTask(2, "SEO Optimization", "TODO", "LOW", 5, 12, 0);

        // Milestones for Project 1
        addMilestone(1, "Requirements Sign-off", LocalDate.now().minusMonths(1), "COMPLETED", true);
        addMilestone(1, "Design Approval", LocalDate.now().minusWeeks(2), "COMPLETED", true);
        addMilestone(1, "Beta Release", LocalDate.now().plusMonths(2), "PENDING", true);
        addMilestone(1, "Go Live", LocalDate.now().plusMonths(4), "PENDING", true);

        // Milestones for Project 2
        addMilestone(2, "Design Approval", LocalDate.now().minusWeeks(1), "COMPLETED", false);
        addMilestone(2, "Content Freeze", LocalDate.now().plusWeeks(1), "PENDING", true);
        addMilestone(2, "Launch", LocalDate.now().plusWeeks(5), "PENDING", true);
    }

    private void addTask(int projectId, String name, String status, String priority,
                         int assigneeId, int estimatedHours, int actualHours) {
        ProjectTask task = new ProjectTask(nextTaskId++, projectId, name);
        task.setStatus(status);
        task.setPriority(priority);
        task.setAssignedToId(assigneeId);
        task.setEstimatedHours(estimatedHours);
        task.setActualHours(actualHours);
        task.setStartDate(LocalDate.now().minusWeeks(2));
        task.setDueDate(LocalDate.now().plusWeeks(2));
        if ("COMPLETED".equals(status)) {
            task.setPercentComplete(100);
            task.setCompletedDate(LocalDate.now().minusDays(3));
        } else if ("IN_PROGRESS".equals(status)) {
            task.setPercentComplete(50);
        }
        tasks.add(task);
    }

    private void addMilestone(int projectId, String name, LocalDate targetDate,
                              String status, boolean critical) {
        Milestone m = new Milestone(nextMilestoneId++, projectId, name);
        m.setTargetDate(targetDate);
        m.setStatus(status);
        m.setCritical(critical);
        if ("COMPLETED".equals(status)) {
            m.setCompletedDate(targetDate.minusDays(1));
        }
        milestones.add(m);
    }

    // ==================== Projects ====================

    @Override
    public List<Project> getAllProjects() {
        return new ArrayList<>(projects);
    }

    @Override
    public List<Project> getProjectsByStatus(String status) {
        return projects.stream()
                .filter(p -> status.equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getProjectsByCustomer(int customerId) {
        return projects.stream()
                .filter(p -> p.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getProjectsByManager(int projectManagerId) {
        return projects.stream()
                .filter(p -> p.getProjectManagerId() == projectManagerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getProjectsByTeamMember(int employeeId) {
        return projects.stream()
                .filter(p -> p.getTeamMemberIds().contains(employeeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getOverdueProjects() {
        return projects.stream()
                .filter(Project::isOverdue)
                .collect(Collectors.toList());
    }

    @Override
    public Project getProjectById(int projectId) {
        return projects.stream()
                .filter(p -> p.getProjectId() == projectId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Project getProjectByCode(String projectCode) {
        return projects.stream()
                .filter(p -> projectCode.equals(p.getProjectCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Project createProject(Project project) {
        project.setProjectId(nextProjectId++);
        projects.add(project);
        return project;
    }

    @Override
    public boolean updateProject(Project project) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectId() == project.getProjectId()) {
                projects.set(i, project);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateProjectStatus(int projectId, String newStatus) {
        Project project = getProjectById(projectId);
        if (project != null) {
            project.setStatus(newStatus);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateProjectProgress(int projectId, int percentComplete) {
        Project project = getProjectById(projectId);
        if (project != null) {
            project.setPercentComplete(percentComplete);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteProject(int projectId) {
        return projects.removeIf(p -> p.getProjectId() == projectId);
    }

    // ==================== Tasks ====================

    @Override
    public List<ProjectTask> getTasksByProject(int projectId) {
        return tasks.stream()
                .filter(t -> t.getProjectId() == projectId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectTask> getTasksByAssignee(int employeeId) {
        return tasks.stream()
                .filter(t -> t.getAssignedToId() == employeeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectTask> getTasksByStatus(int projectId, String status) {
        return tasks.stream()
                .filter(t -> (projectId == 0 || t.getProjectId() == projectId) && status.equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectTask> getOverdueTasks(int projectId) {
        return tasks.stream()
                .filter(t -> (projectId == 0 || t.getProjectId() == projectId) && t.isOverdue())
                .collect(Collectors.toList());
    }

    @Override
    public ProjectTask getTaskById(int taskId) {
        return tasks.stream()
                .filter(t -> t.getTaskId() == taskId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public ProjectTask createTask(ProjectTask task) {
        task.setTaskId(nextTaskId++);
        tasks.add(task);
        return task;
    }

    @Override
    public boolean updateTask(ProjectTask task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskId() == task.getTaskId()) {
                tasks.set(i, task);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateTaskStatus(int taskId, String newStatus) {
        ProjectTask task = getTaskById(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            if ("COMPLETED".equals(newStatus)) {
                task.setPercentComplete(100);
                task.setCompletedDate(LocalDate.now());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean assignTask(int taskId, int employeeId) {
        ProjectTask task = getTaskById(taskId);
        if (task != null) {
            task.setAssignedToId(employeeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean logTime(int taskId, int hours, String notes) {
        ProjectTask task = getTaskById(taskId);
        if (task != null) {
            task.setActualHours(task.getActualHours() + hours);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(int taskId) {
        return tasks.removeIf(t -> t.getTaskId() == taskId);
    }

    // ==================== Team Management ====================

    @Override
    public List<Integer> getProjectTeam(int projectId) {
        Project project = getProjectById(projectId);
        return project != null ? project.getTeamMemberIds() : new ArrayList<>();
    }

    @Override
    public boolean addTeamMember(int projectId, int employeeId) {
        Project project = getProjectById(projectId);
        if (project != null && !project.getTeamMemberIds().contains(employeeId)) {
            project.getTeamMemberIds().add(employeeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTeamMember(int projectId, int employeeId) {
        Project project = getProjectById(projectId);
        if (project != null) {
            return project.getTeamMemberIds().remove(Integer.valueOf(employeeId));
        }
        return false;
    }

    // ==================== Analytics ====================

    @Override
    public Map<String, Object> getProjectStats(int projectId) {
        Map<String, Object> stats = new HashMap<>();
        List<ProjectTask> projectTasks = getTasksByProject(projectId);

        stats.put("totalTasks", projectTasks.size());
        stats.put("completedTasks", projectTasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count());
        stats.put("hoursEstimated", projectTasks.stream().mapToInt(ProjectTask::getEstimatedHours).sum());
        stats.put("hoursActual", projectTasks.stream().mapToInt(ProjectTask::getActualHours).sum());

        return stats;
    }

    @Override
    public Map<String, BigDecimal> getBudgetStatus(int projectId) {
        Map<String, BigDecimal> budget = new HashMap<>();
        Project project = getProjectById(projectId);
        if (project != null) {
            budget.put("budget", project.getBudgetAmount());
            budget.put("spent", project.getActualCost());
            budget.put("remaining", project.getBudgetVariance());
        }
        return budget;
    }

    @Override
    public Map<String, Integer> getEmployeeUtilization(int employeeId, LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> utilization = new HashMap<>();
        int allocatedHours = tasks.stream()
                .filter(t -> t.getAssignedToId() == employeeId)
                .mapToInt(ProjectTask::getEstimatedHours)
                .sum();
        utilization.put("allocated", allocatedHours);
        utilization.put("capacity", 160); // Assume 160 hours/month
        return utilization;
    }

    // ==================== Milestones ====================

    @Override
    public List<Milestone> getAllMilestones() {
        return new ArrayList<>(milestones);
    }

    @Override
    public List<Milestone> getMilestonesByProject(int projectId) {
        return milestones.stream()
                .filter(m -> m.getProjectId() == projectId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Milestone> getUpcomingMilestones(int days) {
        LocalDate futureDate = LocalDate.now().plusDays(days);
        return milestones.stream()
                .filter(m -> !"COMPLETED".equals(m.getStatus()) &&
                        m.getTargetDate() != null &&
                        !m.getTargetDate().isAfter(futureDate))
                .collect(Collectors.toList());
    }

    @Override
    public Milestone getMilestoneById(int milestoneId) {
        return milestones.stream()
                .filter(m -> m.getMilestoneId() == milestoneId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Milestone createMilestone(Milestone milestone) {
        milestone.setMilestoneId(nextMilestoneId++);
        milestones.add(milestone);
        return milestone;
    }

    @Override
    public boolean updateMilestone(Milestone milestone) {
        for (int i = 0; i < milestones.size(); i++) {
            if (milestones.get(i).getMilestoneId() == milestone.getMilestoneId()) {
                milestones.set(i, milestone);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteMilestone(int milestoneId) {
        return milestones.removeIf(m -> m.getMilestoneId() == milestoneId);
    }

    @Override
    public boolean completeMilestone(int milestoneId) {
        Milestone milestone = getMilestoneById(milestoneId);
        if (milestone != null) {
            milestone.complete();
            return true;
        }
        return false;
    }

    // Helper method to get all tasks
    public List<ProjectTask> getAllTasks() {
        return new ArrayList<>(tasks);
    }
}
