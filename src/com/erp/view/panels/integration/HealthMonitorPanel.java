package com.erp.view.panels.integration;

import com.erp.service.mock.MockIntegrationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * HealthMonitorPanel displays integration health status and logs.
 *
 * Features:
 * - Health status overview showing each integration's status
 * - Filterable log viewer with log level indicators
 * - Refresh and filter controls
 */
public class HealthMonitorPanel extends JPanel {

    private MockIntegrationService integrationService;

    // Health status table
    private JTable healthTable;
    private DefaultTableModel healthModel;

    // Logs table
    private JTable logTable;
    private DefaultTableModel logModel;

    // Filter
    private JComboBox<String> integrationFilter;
    private JComboBox<String> levelFilter;

    // Summary labels
    private JLabel healthyLabel;
    private JLabel warningLabel;
    private JLabel criticalLabel;
    private JLabel totalLogsLabel;

    private static final String[] HEALTH_COLUMNS = {"Integration", "Status"};
    private static final String[] LOG_COLUMNS = {"Log ID", "Integration", "Level", "Message", "Timestamp"};

    public HealthMonitorPanel() {
        integrationService = MockIntegrationService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Health table
        healthModel = new DefaultTableModel(HEALTH_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        healthTable = new JTable(healthModel);
        healthTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(healthTable);

        healthTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        healthTable.getColumnModel().getColumn(1).setPreferredWidth(120);

        healthTable.getColumnModel().getColumn(1).setCellRenderer(new HealthStatusCellRenderer());

        // Logs table
        logModel = new DefaultTableModel(LOG_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        logTable = new JTable(logModel);
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(logTable);

        logTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        logTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        logTable.getColumnModel().getColumn(3).setPreferredWidth(350);
        logTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        logTable.getColumnModel().getColumn(2).setCellRenderer(new LogLevelCellRenderer());

        // Filters
        integrationFilter = new JComboBox<>(new String[]{"All Integrations"});
        levelFilter = new JComboBox<>(new String[]{"All Levels", "INFO", "WARN", "ERROR"});

        // Summary labels
        healthyLabel = createSummaryValue("0");
        warningLabel = createSummaryValue("0");
        criticalLabel = createSummaryValue("0");
        totalLogsLabel = createSummaryValue("0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Health status section (left side of split)
        JPanel healthSection = new JPanel(new BorderLayout());
        healthSection.setBackground(Constants.BG_WHITE);
        healthSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel healthTitle = new JLabel("Integration Health Status");
        healthTitle.setFont(Constants.FONT_SUBTITLE);
        healthTitle.setForeground(Constants.TEXT_PRIMARY);
        healthTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane healthScroll = new JScrollPane(healthTable);
        healthScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        healthScroll.getViewport().setBackground(Constants.BG_WHITE);

        healthSection.add(healthTitle, BorderLayout.NORTH);
        healthSection.add(healthScroll, BorderLayout.CENTER);

        // Logs section
        JPanel logSection = new JPanel(new BorderLayout());
        logSection.setBackground(Constants.BG_WHITE);
        logSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JPanel logHeader = new JPanel(new BorderLayout());
        logHeader.setOpaque(false);
        logHeader.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JLabel logTitle = new JLabel("Integration Logs");
        logTitle.setFont(Constants.FONT_SUBTITLE);
        logTitle.setForeground(Constants.TEXT_PRIMARY);

        JPanel filterPanel = createFilterPanel();

        logHeader.add(logTitle, BorderLayout.WEST);
        logHeader.add(filterPanel, BorderLayout.EAST);

        JScrollPane logScroll = new JScrollPane(logTable);
        logScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        logScroll.getViewport().setBackground(Constants.BG_WHITE);

        logSection.add(logHeader, BorderLayout.NORTH);
        logSection.add(logScroll, BorderLayout.CENTER);

        // Split pane - health on top, logs on bottom
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, healthSection, logSection);
        splitPane.setDividerLocation(220);
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(null);

        add(summaryPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Healthy", healthyLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Warning", warningLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Critical", criticalLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Total Logs", totalLogsLabel, Constants.PRIMARY_COLOR));

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

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.PADDING_SMALL, 0));
        panel.setOpaque(false);

        panel.add(new JLabel("Integration:"));
        panel.add(integrationFilter);
        panel.add(new JLabel("Level:"));
        panel.add(levelFilter);

        JButton filterBtn = UIHelper.createSecondaryButton("Filter");
        filterBtn.setPreferredSize(new Dimension(80, 30));
        filterBtn.addActionListener(e -> applyFilter());
        panel.add(filterBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        panel.add(refreshBtn);

        return panel;
    }

    public void loadData() {
        // Load health status
        healthModel.setRowCount(0);
        Map<String, String> healthMap = integrationService.getIntegrationHealth();

        // Populate integration filter
        integrationFilter.removeAllItems();
        integrationFilter.addItem("All Integrations");

        int healthy = 0, warning = 0, critical = 0;
        for (Map.Entry<String, String> entry : healthMap.entrySet()) {
            healthModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            integrationFilter.addItem(entry.getKey());

            switch (entry.getValue()) {
                case "CONNECTED": healthy++; break;
                case "ERROR": warning++; break;
                case "DISCONNECTED": critical++; break;
            }
        }

        healthyLabel.setText(String.valueOf(healthy));
        warningLabel.setText(String.valueOf(warning));
        criticalLabel.setText(String.valueOf(critical));

        // Load logs
        loadLogs(null);
    }

    private void loadLogs(String integrationName) {
        logModel.setRowCount(0);
        List<Map<String, Object>> logs = integrationService.getIntegrationLogs(integrationName, 50);
        for (Map<String, Object> log : logs) {
            logModel.addRow(new Object[]{
                log.get("id"),
                log.get("integrationName"),
                log.get("level"),
                log.get("message"),
                log.get("timestamp")
            });
        }
        totalLogsLabel.setText(String.valueOf(logs.size()));
    }

    private void applyFilter() {
        String selectedIntegration = (String) integrationFilter.getSelectedItem();
        String selectedLevel = (String) levelFilter.getSelectedItem();

        String integrationName = "All Integrations".equals(selectedIntegration) ? null : selectedIntegration;

        logModel.setRowCount(0);
        List<Map<String, Object>> logs = integrationService.getIntegrationLogs(integrationName, 100);
        int count = 0;
        for (Map<String, Object> log : logs) {
            if ("All Levels".equals(selectedLevel) || selectedLevel.equals(log.get("level"))) {
                logModel.addRow(new Object[]{
                    log.get("id"),
                    log.get("integrationName"),
                    log.get("level"),
                    log.get("message"),
                    log.get("timestamp")
                });
                count++;
            }
        }
        totalLogsLabel.setText(String.valueOf(count));
    }

    public void refreshData() {
        loadData();
    }

    // Health status cell renderer
    private static class HealthStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "CONNECTED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "DISCONNECTED":
                        setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                        break;
                    case "ERROR":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    default:
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    // Log level cell renderer
    private static class LogLevelCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "INFO":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "WARN":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "ERROR":
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
}
