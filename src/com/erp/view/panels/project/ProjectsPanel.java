package com.erp.view.panels.project;

import com.erp.model.Project;
import com.erp.service.mock.MockProjectService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.ProjectDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ProjectsPanel displays and manages projects.
 */
public class ProjectsPanel extends JPanel {

    private MockProjectService projectService;

    private JTable projectsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> statusFilter;

    private JLabel totalProjectsLabel;
    private JLabel activeProjectsLabel;
    private JLabel completedProjectsLabel;

    private static final String[] COLUMNS = {"Code", "Project Name", "Status", "Priority", "Progress", "Start Date", "End Date", "Budget"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ProjectsPanel() {
        projectService = MockProjectService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Status", "PLANNED", "IN_PROGRESS", "ON_HOLD", "COMPLETED", "CANCELLED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        projectsTable = new JTable(tableModel);
        projectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(projectsTable);

        // Column widths
        projectsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        projectsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        projectsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        projectsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        projectsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        projectsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        projectsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        projectsTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Custom renderers
        projectsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());
        projectsTable.getColumnModel().getColumn(3).setCellRenderer(new PriorityCellRenderer());
        projectsTable.getColumnModel().getColumn(4).setCellRenderer(new ProgressCellRenderer());

        // Summary labels
        totalProjectsLabel = createSummaryValue("0");
        activeProjectsLabel = createSummaryValue("0");
        completedProjectsLabel = createSummaryValue("0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        JPanel summaryPanel = createSummaryPanel();
        JPanel toolbar = createToolbar();

        JScrollPane scrollPane = new JScrollPane(projectsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Projects", totalProjectsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Active Projects", activeProjectsLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Completed", completedProjectsLabel, Constants.SUCCESS_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_MEDIUM,
                          Constants.PADDING_SMALL, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton addBtn = UIHelper.createPrimaryButton("New Project");
        addBtn.setPreferredSize(new Dimension(110, 30));
        addBtn.addActionListener(e -> addProject());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editProject());
        toolbar.add(editBtn);

        JButton deleteBtn = UIHelper.createSecondaryButton("Delete");
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        deleteBtn.addActionListener(e -> deleteProject());
        toolbar.add(deleteBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Project> projects = projectService.getAllProjects();
        String statusSelection = (String) statusFilter.getSelectedItem();

        int totalCount = 0;
        int activeCount = 0;
        int completedCount = 0;

        for (Project p : projects) {
            if (!"All Status".equals(statusSelection) && !statusSelection.equals(p.getStatus())) {
                continue;
            }

            totalCount++;
            if ("IN_PROGRESS".equals(p.getStatus())) activeCount++;
            if ("COMPLETED".equals(p.getStatus())) completedCount++;

            tableModel.addRow(new Object[]{
                p.getProjectCode(),
                p.getName(),
                p.getStatus(),
                p.getPriority(),
                p.getPercentComplete() + "%",
                p.getStartDate() != null ? p.getStartDate().format(DATE_FORMAT) : "",
                p.getEndDate() != null ? p.getEndDate().format(DATE_FORMAT) : "",
                p.getBudgetAmount() != null ? "$" + String.format("%,.0f", p.getBudgetAmount()) : ""
            });
        }

        totalProjectsLabel.setText(String.valueOf(totalCount));
        activeProjectsLabel.setText(String.valueOf(activeCount));
        completedProjectsLabel.setText(String.valueOf(completedCount));
    }

    private void addProject() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ProjectDialog dialog = new ProjectDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Project newProject = dialog.getProject();
            projectService.createProject(newProject);
            UIHelper.showSuccess(this, "Project created successfully.");
            loadData();
        }
    }

    private void editProject() {
        int row = projectsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a project to edit.");
            return;
        }

        String projectCode = (String) tableModel.getValueAt(row, 0);
        Project project = projectService.getProjectByCode(projectCode);

        if (project != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ProjectDialog dialog = new ProjectDialog(parentFrame, project);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                projectService.updateProject(dialog.getProject());
                UIHelper.showSuccess(this, "Project updated successfully.");
                loadData();
            }
        }
    }

    private void deleteProject() {
        int row = projectsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a project to delete.");
            return;
        }

        String projectCode = (String) tableModel.getValueAt(row, 0);
        String projectName = (String) tableModel.getValueAt(row, 1);
        Project project = projectService.getProjectByCode(projectCode);

        if (project != null) {
            boolean confirm = UIHelper.showConfirm(this, "Are you sure you want to delete project '" + projectName + "'?");
            if (confirm) {
                if (projectService.deleteProject(project.getProjectId())) {
                    UIHelper.showSuccess(this, "Project deleted successfully.");
                    loadData();
                }
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String status = value.toString();
                switch (status) {
                    case "COMPLETED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "IN_PROGRESS":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "PLANNED":
                        setBackground(new Color(226, 232, 240));
                        setForeground(new Color(71, 85, 105));
                        break;
                    case "ON_HOLD":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "CANCELLED":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Priority cell renderer
    private static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String priority = value.toString();
                switch (priority) {
                    case "CRITICAL":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "HIGH":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "MEDIUM":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "LOW":
                        setBackground(new Color(226, 232, 240));
                        setForeground(new Color(71, 85, 105));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Progress cell renderer
    private static class ProgressCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String progressStr = value.toString().replace("%", "");
                int progress = Integer.parseInt(progressStr);
                if (progress == 100) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else if (progress >= 50) {
                    setBackground(new Color(209, 236, 241));
                    setForeground(new Color(12, 84, 96));
                } else if (progress > 0) {
                    setBackground(new Color(255, 243, 205));
                    setForeground(new Color(133, 100, 4));
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    }
}
