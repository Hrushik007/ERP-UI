package com.erp.view.panels.analytics;

import com.erp.model.Dashboard;
import com.erp.service.mock.MockAnalyticsService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.DashboardDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DashboardsPanel displays and manages analytics dashboards.
 */
public class DashboardsPanel extends JPanel {

    private MockAnalyticsService analyticsService;

    private JTable dashboardsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryFilter;

    private JLabel totalDashboardsLabel;
    private JLabel publicDashboardsLabel;

    private static final String[] COLUMNS = {"ID", "Code", "Dashboard Name", "Category", "Layout", "Columns", "Default", "Public", "Created"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DashboardsPanel() {
        analyticsService = MockAnalyticsService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Category filter
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "EXECUTIVE", "SALES", "OPERATIONS", "FINANCIAL", "HR", "CUSTOM"});
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dashboardsTable = new JTable(tableModel);
        dashboardsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(dashboardsTable);

        // Column widths
        dashboardsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        dashboardsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        dashboardsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        dashboardsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        dashboardsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        dashboardsTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        dashboardsTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        dashboardsTable.getColumnModel().getColumn(7).setPreferredWidth(70);
        dashboardsTable.getColumnModel().getColumn(8).setPreferredWidth(100);

        // Custom renderers
        dashboardsTable.getColumnModel().getColumn(3).setCellRenderer(new CategoryCellRenderer());
        dashboardsTable.getColumnModel().getColumn(6).setCellRenderer(new BooleanCellRenderer());
        dashboardsTable.getColumnModel().getColumn(7).setCellRenderer(new BooleanCellRenderer());

        // Summary labels
        totalDashboardsLabel = createSummaryValue("0");
        publicDashboardsLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(dashboardsTable);
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
        JPanel panel = new JPanel(new GridLayout(1, 2, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Dashboards", totalDashboardsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Public Dashboards", publicDashboardsLabel, Constants.SUCCESS_COLOR));

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

        toolbar.add(new JLabel("Category:"));
        toolbar.add(categoryFilter);

        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_SMALL));

        JButton addBtn = UIHelper.createPrimaryButton("New Dashboard");
        addBtn.setPreferredSize(new Dimension(130, 30));
        addBtn.addActionListener(e -> addDashboard());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editDashboard());
        toolbar.add(editBtn);

        JButton setDefaultBtn = new JButton("Set Default");
        setDefaultBtn.setFont(Constants.FONT_BUTTON);
        setDefaultBtn.setBackground(Constants.SUCCESS_COLOR);
        setDefaultBtn.setForeground(Color.WHITE);
        setDefaultBtn.setOpaque(true);
        setDefaultBtn.setBorderPainted(false);
        setDefaultBtn.setFocusPainted(false);
        setDefaultBtn.setPreferredSize(new Dimension(100, 30));
        setDefaultBtn.addActionListener(e -> setDefaultDashboard());
        toolbar.add(setDefaultBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(Constants.FONT_BUTTON);
        deleteBtn.setBackground(Constants.DANGER_COLOR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        deleteBtn.addActionListener(e -> deleteDashboard());
        toolbar.add(deleteBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Dashboard> dashboards = analyticsService.getAllDashboards();
        String categorySelection = (String) categoryFilter.getSelectedItem();

        int totalCount = 0;
        int publicCount = 0;

        for (Dashboard d : dashboards) {
            if (!"All Categories".equals(categorySelection) && !categorySelection.equals(d.getCategory())) {
                continue;
            }

            totalCount++;
            if (d.isPublic()) publicCount++;

            tableModel.addRow(new Object[]{
                d.getDashboardId(),
                d.getDashboardCode(),
                d.getName(),
                d.getCategory(),
                d.getLayout(),
                d.getColumns(),
                d.isDefault() ? "Yes" : "No",
                d.isPublic() ? "Yes" : "No",
                d.getCreatedDate() != null ? d.getCreatedDate().format(DATE_FORMAT) : ""
            });
        }

        totalDashboardsLabel.setText(String.valueOf(totalCount));
        publicDashboardsLabel.setText(String.valueOf(publicCount));
    }

    private void addDashboard() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        DashboardDialog dialog = new DashboardDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Dashboard newDashboard = dialog.getDashboard();
            analyticsService.createDashboard(newDashboard);
            UIHelper.showSuccess(this, "Dashboard created successfully.");
            loadData();
        }
    }

    private void editDashboard() {
        int row = dashboardsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a dashboard to edit.");
            return;
        }

        int dashboardId = (int) tableModel.getValueAt(row, 0);
        Dashboard dashboard = analyticsService.getDashboardById(dashboardId);

        if (dashboard != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            DashboardDialog dialog = new DashboardDialog(parentFrame, dashboard);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                analyticsService.updateDashboard(dialog.getDashboard());
                UIHelper.showSuccess(this, "Dashboard updated successfully.");
                loadData();
            }
        }
    }

    private void setDefaultDashboard() {
        int row = dashboardsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a dashboard to set as default.");
            return;
        }

        int dashboardId = (int) tableModel.getValueAt(row, 0);
        String dashboardName = (String) tableModel.getValueAt(row, 2);

        boolean confirm = UIHelper.showConfirm(this, "Set '" + dashboardName + "' as the default dashboard?");
        if (confirm) {
            if (analyticsService.setDefaultDashboard(dashboardId)) {
                UIHelper.showSuccess(this, "Default dashboard updated.");
                loadData();
            }
        }
    }

    private void deleteDashboard() {
        int row = dashboardsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a dashboard to delete.");
            return;
        }

        int dashboardId = (int) tableModel.getValueAt(row, 0);
        String dashboardName = (String) tableModel.getValueAt(row, 2);

        boolean confirm = UIHelper.showConfirm(this, "Delete dashboard '" + dashboardName + "'? This cannot be undone.");
        if (confirm) {
            if (analyticsService.deleteDashboard(dashboardId)) {
                UIHelper.showSuccess(this, "Dashboard deleted successfully.");
                loadData();
            }
        }
    }

    public void refreshData() {
        loadData();
    }

    // Category cell renderer
    private static class CategoryCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String category = value.toString();
                switch (category) {
                    case "EXECUTIVE":
                        setBackground(new Color(230, 230, 250));
                        setForeground(new Color(75, 0, 130));
                        break;
                    case "SALES":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "OPERATIONS":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "FINANCIAL":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "HR":
                        setBackground(new Color(255, 228, 225));
                        setForeground(new Color(139, 69, 19));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Boolean cell renderer
    private static class BooleanCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                if ("Yes".equals(value.toString())) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    }
}
