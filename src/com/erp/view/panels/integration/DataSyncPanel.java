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
 * DataSyncPanel displays and manages data synchronization between ERP modules.
 *
 * Split into two sections:
 * - Top: Sync status summary cards and action buttons
 * - Bottom: Sync configurations table with sync/resync actions, and events table
 */
public class DataSyncPanel extends JPanel {

    private MockIntegrationService integrationService;

    // Sync configurations table
    private JTable syncTable;
    private DefaultTableModel syncModel;

    // Events table
    private JTable eventTable;
    private DefaultTableModel eventModel;

    // Summary labels
    private JLabel totalSyncsLabel;
    private JLabel syncedLabel;
    private JLabel pendingLabel;
    private JLabel errorLabel;

    private JButton syncButton;
    private JButton resyncButton;
    private JButton ackButton;

    private static final String[] SYNC_COLUMNS = {"Source", "Target", "Data Type", "Status", "Last Sync", "Records Synced", "Pending"};
    private static final String[] EVENT_COLUMNS = {"Event ID", "Type", "Source Module", "Status", "Created At"};

    public DataSyncPanel() {
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
        // Sync configurations table
        syncModel = new DefaultTableModel(SYNC_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        syncTable = new JTable(syncModel);
        syncTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(syncTable);

        syncTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        syncTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        syncTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        syncTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        syncTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        syncTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        syncTable.getColumnModel().getColumn(6).setPreferredWidth(70);

        // Status column renderer
        syncTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        syncTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // Events table
        eventModel = new DefaultTableModel(EVENT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        eventTable = new JTable(eventModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(eventTable);

        eventTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        eventTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        eventTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        eventTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        eventTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        eventTable.getColumnModel().getColumn(3).setCellRenderer(new EventStatusCellRenderer());

        eventTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // Summary labels
        totalSyncsLabel = createSummaryValue("0");
        syncedLabel = createSummaryValue("0");
        pendingLabel = createSummaryValue("0");
        errorLabel = createSummaryValue("0");

        // Action buttons
        syncButton = UIHelper.createPrimaryButton("Sync Now");
        syncButton.setPreferredSize(new Dimension(110, 30));
        syncButton.setEnabled(false);
        syncButton.addActionListener(e -> syncNow());

        resyncButton = new JButton("Force Resync");
        resyncButton.setFont(Constants.FONT_BUTTON);
        resyncButton.setBackground(Constants.WARNING_COLOR);
        resyncButton.setForeground(Color.WHITE);
        resyncButton.setOpaque(true);
        resyncButton.setBorderPainted(false);
        resyncButton.setFocusPainted(false);
        resyncButton.setPreferredSize(new Dimension(120, 30));
        resyncButton.setEnabled(false);
        resyncButton.addActionListener(e -> forceResync());

        ackButton = new JButton("Acknowledge");
        ackButton.setFont(Constants.FONT_BUTTON);
        ackButton.setBackground(Constants.SUCCESS_COLOR);
        ackButton.setForeground(Color.WHITE);
        ackButton.setOpaque(true);
        ackButton.setBorderPainted(false);
        ackButton.setFocusPainted(false);
        ackButton.setPreferredSize(new Dimension(120, 30));
        ackButton.setEnabled(false);
        ackButton.addActionListener(e -> acknowledgeEvent());
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

        // Toolbar
        JPanel toolbar = createToolbar();

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        // Sync configurations section
        JPanel syncSection = new JPanel(new BorderLayout());
        syncSection.setBackground(Constants.BG_WHITE);
        syncSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel syncTitle = new JLabel("Module Sync Status");
        syncTitle.setFont(Constants.FONT_SUBTITLE);
        syncTitle.setForeground(Constants.TEXT_PRIMARY);
        syncTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane syncScroll = new JScrollPane(syncTable);
        syncScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        syncScroll.getViewport().setBackground(Constants.BG_WHITE);

        syncSection.add(syncTitle, BorderLayout.NORTH);
        syncSection.add(syncScroll, BorderLayout.CENTER);

        // Events section
        JPanel eventSection = new JPanel(new BorderLayout());
        eventSection.setBackground(Constants.BG_WHITE);
        eventSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel eventTitle = new JLabel("Event Queue");
        eventTitle.setFont(Constants.FONT_SUBTITLE);
        eventTitle.setForeground(Constants.TEXT_PRIMARY);
        eventTitle.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        eventScroll.getViewport().setBackground(Constants.BG_WHITE);

        eventSection.add(eventTitle, BorderLayout.NORTH);
        eventSection.add(eventScroll, BorderLayout.CENTER);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, syncSection, eventSection);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        add(topSection, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Syncs", totalSyncsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Synced", syncedLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Pending", pendingLabel, Constants.WARNING_COLOR));
        panel.add(createSummaryCard("Errors", errorLabel, Constants.DANGER_COLOR));

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
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        toolbar.add(new JLabel("Actions:"));
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(syncButton);
        toolbar.add(resyncButton);
        toolbar.add(ackButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        // Load sync configs
        syncModel.setRowCount(0);
        List<Map<String, Object>> syncs = integrationService.getAllSyncConfigs();
        for (Map<String, Object> sync : syncs) {
            syncModel.addRow(new Object[]{
                sync.get("sourceModule"),
                sync.get("targetModule"),
                sync.get("dataType"),
                sync.get("status"),
                sync.get("lastSync"),
                sync.get("recordsSynced"),
                sync.get("pendingChanges")
            });
        }

        // Load events
        eventModel.setRowCount(0);
        List<Map<String, Object>> allEvents = integrationService.getAllEvents();
        for (Map<String, Object> event : allEvents) {
            eventModel.addRow(new Object[]{
                event.get("id"),
                event.get("eventType"),
                event.get("sourceModule"),
                event.get("status"),
                event.get("createdAt")
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        Map<String, Integer> counts = integrationService.getSyncCountByStatus();
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        totalSyncsLabel.setText(String.valueOf(total));
        syncedLabel.setText(String.valueOf(counts.getOrDefault("SYNCED", 0)));
        pendingLabel.setText(String.valueOf(counts.getOrDefault("PENDING", 0)));
        errorLabel.setText(String.valueOf(counts.getOrDefault("ERROR", 0)));
    }

    private void updateButtonStates() {
        int syncRow = syncTable.getSelectedRow();
        syncButton.setEnabled(syncRow >= 0);
        resyncButton.setEnabled(syncRow >= 0);

        int eventRow = eventTable.getSelectedRow();
        if (eventRow >= 0) {
            String status = (String) eventModel.getValueAt(eventRow, 3);
            ackButton.setEnabled("PENDING".equals(status));
        } else {
            ackButton.setEnabled(false);
        }
    }

    private void syncNow() {
        int row = syncTable.getSelectedRow();
        if (row < 0) return;

        String source = (String) syncModel.getValueAt(row, 0);
        String target = (String) syncModel.getValueAt(row, 1);
        String dataType = (String) syncModel.getValueAt(row, 2);

        int records = integrationService.syncData(source, target, dataType);
        UIHelper.showSuccess(this, "Sync completed: " + records + " records synced from " + source + " to " + target);
        loadData();
    }

    private void forceResync() {
        int row = syncTable.getSelectedRow();
        if (row < 0) return;

        String source = (String) syncModel.getValueAt(row, 0);
        String target = (String) syncModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Force resync from " + source + " to " + target + "? This may take longer than a regular sync.")) {
            if (integrationService.forceResync(source, target)) {
                UIHelper.showSuccess(this, "Force resync completed successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to force resync.");
            }
        }
    }

    private void acknowledgeEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) return;

        String eventId = (String) eventModel.getValueAt(row, 0);
        String eventType = (String) eventModel.getValueAt(row, 1);

        if (integrationService.acknowledgeEvent(eventId, true)) {
            UIHelper.showSuccess(this, "Event '" + eventType + "' acknowledged successfully.");
            loadData();
        } else {
            UIHelper.showError(this, "Failed to acknowledge event.");
        }
    }

    public void refreshData() {
        loadData();
    }

    // Status cell renderer for sync status
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "SYNCED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "PENDING":
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

    // Status cell renderer for events
    private static class EventStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "DELIVERED":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "PENDING":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "FAILED":
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
