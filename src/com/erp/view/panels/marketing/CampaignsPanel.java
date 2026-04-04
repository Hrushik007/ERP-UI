package com.erp.view.panels.marketing;

import com.erp.model.Campaign;
import com.erp.service.mock.MockMarketingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.CampaignDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * CampaignsPanel displays and manages marketing campaigns.
 *
 * Demonstrates:
 * - JTable with custom cell renderer for status colors
 * - Summary cards pattern used across modules
 * - Toolbar with filters and action buttons
 * - Full CRUD operations via dialog
 */
public class CampaignsPanel extends JPanel {

    private MockMarketingService marketingService;

    private JTable campaignsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> statusFilter;
    private JComboBox<String> typeFilter;
    private JTextField searchField;

    // Summary labels
    private JLabel totalCampaignsLabel;
    private JLabel activeCampaignsLabel;
    private JLabel totalBudgetLabel;
    private JLabel totalSpendLabel;

    private JButton editButton;
    private JButton deleteButton;
    private JButton startButton;
    private JButton pauseButton;
    private JButton endButton;

    private static final String[] COLUMNS = {"ID", "Name", "Type", "Status", "Start Date", "End Date", "Budget", "Spent", "Leads", "Channel"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CampaignsPanel() {
        marketingService = MockMarketingService.getInstance();
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
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "PLANNED", "ACTIVE", "PAUSED", "COMPLETED", "CANCELLED"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "EMAIL", "SOCIAL_MEDIA", "ADS", "EVENT", "CONTENT"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by campaign name or target audience");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        campaignsTable = new JTable(tableModel);
        campaignsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(campaignsTable);

        // Column widths
        campaignsTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        campaignsTable.getColumnModel().getColumn(1).setPreferredWidth(180);  // Name
        campaignsTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Type
        campaignsTable.getColumnModel().getColumn(3).setPreferredWidth(90);   // Status
        campaignsTable.getColumnModel().getColumn(4).setPreferredWidth(90);   // Start
        campaignsTable.getColumnModel().getColumn(5).setPreferredWidth(90);   // End
        campaignsTable.getColumnModel().getColumn(6).setPreferredWidth(90);   // Budget
        campaignsTable.getColumnModel().getColumn(7).setPreferredWidth(90);   // Spent
        campaignsTable.getColumnModel().getColumn(8).setPreferredWidth(50);   // Leads
        campaignsTable.getColumnModel().getColumn(9).setPreferredWidth(100);  // Channel

        // Status column renderer
        campaignsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        campaignsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Summary labels
        totalCampaignsLabel = createSummaryValue("0");
        activeCampaignsLabel = createSummaryValue("0");
        totalBudgetLabel = createSummaryValue("$0");
        totalSpendLabel = createSummaryValue("$0");

        // Action buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editCampaign());

        deleteButton = new JButton("Delete");
        deleteButton.setFont(Constants.FONT_BUTTON);
        deleteButton.setBackground(Constants.DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(90, 30));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteCampaign());

        startButton = new JButton("Start");
        startButton.setFont(Constants.FONT_BUTTON);
        startButton.setBackground(Constants.SUCCESS_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(90, 30));
        startButton.setEnabled(false);
        startButton.addActionListener(e -> startCampaign());

        pauseButton = new JButton("Pause");
        pauseButton.setFont(Constants.FONT_BUTTON);
        pauseButton.setBackground(Constants.WARNING_COLOR);
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setOpaque(true);
        pauseButton.setBorderPainted(false);
        pauseButton.setFocusPainted(false);
        pauseButton.setPreferredSize(new Dimension(90, 30));
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> pauseCampaign());

        endButton = new JButton("End");
        endButton.setFont(Constants.FONT_BUTTON);
        endButton.setBackground(new Color(111, 66, 193));
        endButton.setForeground(Color.WHITE);
        endButton.setOpaque(true);
        endButton.setBorderPainted(false);
        endButton.setFocusPainted(false);
        endButton.setPreferredSize(new Dimension(90, 30));
        endButton.setEnabled(false);
        endButton.addActionListener(e -> endCampaign());
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Top - Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(campaignsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total Campaigns", totalCampaignsLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Active", activeCampaignsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Budget", totalBudgetLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Total Spend", totalSpendLabel, new Color(111, 66, 193)));

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
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        // Filters row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> loadData());
        filterPanel.add(searchBtn);

        // Actions row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));

        JButton addBtn = UIHelper.createPrimaryButton("Add Campaign");
        addBtn.setPreferredSize(new Dimension(130, 30));
        addBtn.addActionListener(e -> addCampaign());
        actionPanel.add(addBtn);

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(startButton);
        actionPanel.add(pauseButton);
        actionPanel.add(endButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        actionPanel.add(refreshBtn);

        toolbar.add(filterPanel, BorderLayout.NORTH);
        toolbar.add(actionPanel, BorderLayout.SOUTH);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Campaign> campaigns = marketingService.getAllCampaigns();
        String statusSelection = (String) statusFilter.getSelectedItem();
        String typeSelection = (String) typeFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (Campaign c : campaigns) {
            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(c.getStatus())) {
                continue;
            }

            // Filter by type
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(c.getType())) {
                continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = c.getName().toLowerCase().contains(searchTerm) ||
                                  (c.getTargetAudience() != null && c.getTargetAudience().toLowerCase().contains(searchTerm));
                if (!matches) continue;
            }

            tableModel.addRow(new Object[]{
                c.getCampaignId(),
                c.getName(),
                c.getType(),
                c.getStatus(),
                c.getStartDate() != null ? c.getStartDate().format(DATE_FORMAT) : "",
                c.getEndDate() != null ? c.getEndDate().format(DATE_FORMAT) : "",
                c.getBudget() != null ? "$" + c.getBudget().toPlainString() : "$0",
                c.getActualSpend() != null ? "$" + c.getActualSpend().toPlainString() : "$0",
                c.getLeadsGenerated(),
                c.getChannel() != null ? c.getChannel() : ""
            });
        }

        updateSummary();
        updateButtonStates();
    }

    private void updateSummary() {
        Map<String, Integer> counts = marketingService.getCampaignCountByStatus();
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();

        totalCampaignsLabel.setText(String.valueOf(total));
        activeCampaignsLabel.setText(String.valueOf(counts.getOrDefault("ACTIVE", 0)));
        totalBudgetLabel.setText("$" + marketingService.getTotalBudget().toPlainString());
        totalSpendLabel.setText("$" + marketingService.getTotalSpend().toPlainString());
    }

    private void updateButtonStates() {
        int row = campaignsTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);

        if (hasSelection) {
            String status = (String) tableModel.getValueAt(row, 3);
            startButton.setEnabled("PLANNED".equals(status) || "PAUSED".equals(status));
            pauseButton.setEnabled("ACTIVE".equals(status));
            endButton.setEnabled("ACTIVE".equals(status) || "PAUSED".equals(status));
        } else {
            startButton.setEnabled(false);
            pauseButton.setEnabled(false);
            endButton.setEnabled(false);
        }
    }

    private void addCampaign() {
        CampaignDialog dialog = new CampaignDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Campaign campaign = dialog.getCampaign();
            marketingService.createCampaign(campaign);
            UIHelper.showSuccess(this, "Campaign created successfully.");
            loadData();
        }
    }

    private void editCampaign() {
        int row = campaignsTable.getSelectedRow();
        if (row < 0) return;

        int campaignId = (int) tableModel.getValueAt(row, 0);
        Campaign campaign = marketingService.getCampaignById(campaignId);

        if (campaign != null) {
            CampaignDialog dialog = new CampaignDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this), campaign);
            dialog.setVisible(true);

            if (dialog.isSaved()) {
                marketingService.updateCampaign(campaign);
                UIHelper.showSuccess(this, "Campaign updated successfully.");
                loadData();
            }
        }
    }

    private void deleteCampaign() {
        int row = campaignsTable.getSelectedRow();
        if (row < 0) return;

        int campaignId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Are you sure you want to delete campaign '" + name + "'?")) {
            if (marketingService.deleteCampaign(campaignId)) {
                UIHelper.showSuccess(this, "Campaign deleted successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to delete campaign.");
            }
        }
    }

    private void startCampaign() {
        int row = campaignsTable.getSelectedRow();
        if (row < 0) return;

        int campaignId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Start campaign '" + name + "'?")) {
            if (marketingService.startCampaign(campaignId)) {
                UIHelper.showSuccess(this, "Campaign started successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to start campaign.");
            }
        }
    }

    private void pauseCampaign() {
        int row = campaignsTable.getSelectedRow();
        if (row < 0) return;

        int campaignId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "Pause campaign '" + name + "'?")) {
            if (marketingService.pauseCampaign(campaignId)) {
                UIHelper.showSuccess(this, "Campaign paused successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to pause campaign.");
            }
        }
    }

    private void endCampaign() {
        int row = campaignsTable.getSelectedRow();
        if (row < 0) return;

        int campaignId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        if (UIHelper.showConfirm(this, "End campaign '" + name + "'?\nThis will mark it as COMPLETED.")) {
            if (marketingService.endCampaign(campaignId)) {
                UIHelper.showSuccess(this, "Campaign ended successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to end campaign.");
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

            if (value != null) {
                String status = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (status) {
                        case "PLANNED":
                            setBackground(new Color(209, 236, 241));
                            setForeground(new Color(12, 84, 96));
                            break;
                        case "ACTIVE":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "PAUSED":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "COMPLETED":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
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
            }
            return this;
        }
    }
}
