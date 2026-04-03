package com.erp.view.panels.project;

import com.erp.model.Milestone;
import com.erp.model.Project;
import com.erp.service.mock.MockProjectService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.MilestoneDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MilestonesPanel displays and manages project milestones.
 */
public class MilestonesPanel extends JPanel {

    private MockProjectService projectService;

    private JTable milestonesTable;
    private DefaultTableModel tableModel;

    private JComboBox<ProjectItem> projectFilter;
    private JComboBox<String> statusFilter;

    private JLabel totalMilestonesLabel;
    private JLabel pendingLabel;
    private JLabel upcomingLabel;

    private static final String[] COLUMNS = {"ID", "Milestone Name", "Project", "Target Date", "Status", "Critical", "Completed Date"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MilestonesPanel() {
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
        // Project filter
        projectFilter = new JComboBox<>();
        projectFilter.setFont(Constants.FONT_REGULAR);
        projectFilter.addItem(new ProjectItem(null)); // All projects
        for (Project p : projectService.getAllProjects()) {
            projectFilter.addItem(new ProjectItem(p));
        }
        projectFilter.addActionListener(e -> loadData());

        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Status", "PENDING", "COMPLETED", "MISSED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        milestonesTable = new JTable(tableModel);
        milestonesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(milestonesTable);

        // Column widths
        milestonesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        milestonesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        milestonesTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        milestonesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        milestonesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        milestonesTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        milestonesTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Custom renderers
        milestonesTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        milestonesTable.getColumnModel().getColumn(5).setCellRenderer(new CriticalCellRenderer());

        // Summary labels
        totalMilestonesLabel = createSummaryValue("0");
        pendingLabel = createSummaryValue("0");
        upcomingLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(milestonesTable);
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

        panel.add(createSummaryCard("Total Milestones", totalMilestonesLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Pending", pendingLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Due in 7 Days", upcomingLabel, Constants.DANGER_COLOR));

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

        toolbar.add(new JLabel("Project:"));
        projectFilter.setPreferredSize(new Dimension(180, 25));
        toolbar.add(projectFilter);

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        JButton addBtn = UIHelper.createPrimaryButton("New Milestone");
        addBtn.setPreferredSize(new Dimension(120, 30));
        addBtn.addActionListener(e -> addMilestone());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editMilestone());
        toolbar.add(editBtn);

        JButton completeBtn = new JButton("Complete");
        completeBtn.setFont(Constants.FONT_BUTTON);
        completeBtn.setBackground(Constants.SUCCESS_COLOR);
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setOpaque(true);
        completeBtn.setBorderPainted(false);
        completeBtn.setFocusPainted(false);
        completeBtn.setPreferredSize(new Dimension(90, 30));
        completeBtn.addActionListener(e -> completeMilestone());
        toolbar.add(completeBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        ProjectItem selectedProject = (ProjectItem) projectFilter.getSelectedItem();
        int projectId = selectedProject != null && selectedProject.getProject() != null ?
                selectedProject.getProject().getProjectId() : 0;

        List<Milestone> milestones;
        if (projectId > 0) {
            milestones = projectService.getMilestonesByProject(projectId);
        } else {
            milestones = projectService.getAllMilestones();
        }

        String statusSelection = (String) statusFilter.getSelectedItem();

        int totalCount = 0;
        int pendingCount = 0;
        int upcomingCount = 0;

        List<Milestone> upcomingMilestones = projectService.getUpcomingMilestones(7);

        for (Milestone m : milestones) {
            if (!"All Status".equals(statusSelection) && !statusSelection.equals(m.getStatus())) {
                continue;
            }

            totalCount++;
            if ("PENDING".equals(m.getStatus())) pendingCount++;

            Project project = projectService.getProjectById(m.getProjectId());
            String projectName = project != null ? project.getProjectCode() + " - " + project.getName() : "";

            tableModel.addRow(new Object[]{
                m.getMilestoneId(),
                m.getName(),
                projectName,
                m.getTargetDate() != null ? m.getTargetDate().format(DATE_FORMAT) : "",
                m.getStatus(),
                m.isCritical() ? "Yes" : "No",
                m.getCompletedDate() != null ? m.getCompletedDate().format(DATE_FORMAT) : ""
            });
        }

        upcomingCount = (int) upcomingMilestones.stream()
                .filter(m -> projectId == 0 || m.getProjectId() == projectId)
                .count();

        totalMilestonesLabel.setText(String.valueOf(totalCount));
        pendingLabel.setText(String.valueOf(pendingCount));
        upcomingLabel.setText(String.valueOf(upcomingCount));
        upcomingLabel.setForeground(upcomingCount > 0 ? Constants.WARNING_COLOR : Constants.PRIMARY_COLOR);
    }

    private void addMilestone() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        MilestoneDialog dialog = new MilestoneDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Milestone newMilestone = dialog.getMilestone();
            projectService.createMilestone(newMilestone);
            UIHelper.showSuccess(this, "Milestone created successfully.");
            loadData();
        }
    }

    private void editMilestone() {
        int row = milestonesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a milestone to edit.");
            return;
        }

        int milestoneId = (int) tableModel.getValueAt(row, 0);
        Milestone milestone = projectService.getMilestoneById(milestoneId);

        if (milestone != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            MilestoneDialog dialog = new MilestoneDialog(parentFrame, milestone);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                projectService.updateMilestone(dialog.getMilestone());
                UIHelper.showSuccess(this, "Milestone updated successfully.");
                loadData();
            }
        }
    }

    private void completeMilestone() {
        int row = milestonesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a milestone to complete.");
            return;
        }

        int milestoneId = (int) tableModel.getValueAt(row, 0);
        String milestoneName = (String) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 4);

        if ("COMPLETED".equals(status)) {
            UIHelper.showError(this, "Milestone is already completed.");
            return;
        }

        boolean confirm = UIHelper.showConfirm(this, "Mark milestone '" + milestoneName + "' as completed?");
        if (confirm) {
            if (projectService.completeMilestone(milestoneId)) {
                UIHelper.showSuccess(this, "Milestone completed successfully.");
                loadData();
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Helper class for project filter combo
    private static class ProjectItem {
        private Project project;

        public ProjectItem(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        @Override
        public String toString() {
            return project == null ? "All Projects" : project.getProjectCode() + " - " + project.getName();
        }
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
                    case "PENDING":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "MISSED":
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

    // Critical cell renderer
    private static class CriticalCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                if ("Yes".equals(value.toString())) {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    }
}
