package com.erp.view.panels.marketing;

import com.erp.model.Campaign;
import com.erp.service.mock.MockMarketingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * LeadTrackingPanel displays lead generation metrics per campaign.
 *
 * Shows a table of campaigns with their lead targets, actual leads,
 * conversion rates, and progress bars for visual tracking.
 */
public class LeadTrackingPanel extends JPanel {

    private MockMarketingService marketingService;

    private JTable leadTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> statusFilter;

    // Summary labels
    private JLabel totalLeadsLabel;
    private JLabel totalTargetLabel;
    private JLabel overallRateLabel;
    private JLabel activeCampaignsLabel;

    private static final String[] COLUMNS = {"ID", "Campaign", "Type", "Status", "Lead Target", "Leads Generated", "Conversion %", "Progress"};

    public LeadTrackingPanel() {
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

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leadTable = new JTable(tableModel);
        leadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(leadTable);

        // Column widths
        leadTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        leadTable.getColumnModel().getColumn(1).setPreferredWidth(180);  // Campaign
        leadTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Type
        leadTable.getColumnModel().getColumn(3).setPreferredWidth(90);   // Status
        leadTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // Target
        leadTable.getColumnModel().getColumn(5).setPreferredWidth(100);  // Generated
        leadTable.getColumnModel().getColumn(6).setPreferredWidth(90);   // Conversion %
        leadTable.getColumnModel().getColumn(7).setPreferredWidth(120);  // Progress

        // Progress column renderer
        leadTable.getColumnModel().getColumn(7).setCellRenderer(new ProgressBarRenderer());

        // Summary labels
        totalLeadsLabel = createSummaryValue("0");
        totalTargetLabel = createSummaryValue("0");
        overallRateLabel = createSummaryValue("0%");
        activeCampaignsLabel = createSummaryValue("0");
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
        JScrollPane scrollPane = new JScrollPane(leadTable);
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

        panel.add(createSummaryCard("Total Leads", totalLeadsLabel, Constants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Target", totalTargetLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Overall Rate", overallRateLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("Active Campaigns", activeCampaignsLabel, new Color(111, 66, 193)));

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

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Campaign> campaigns = marketingService.getAllCampaigns();
        String statusSelection = (String) statusFilter.getSelectedItem();

        int totalLeads = 0;
        int totalTarget = 0;
        int activeCount = 0;

        for (Campaign c : campaigns) {
            // Filter by status
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(c.getStatus())) {
                continue;
            }

            double conversionRate = c.getLeadTarget() > 0
                    ? (double) c.getLeadsGenerated() / c.getLeadTarget() * 100 : 0;
            int progressPercent = c.getLeadTarget() > 0
                    ? Math.min((int) ((double) c.getLeadsGenerated() / c.getLeadTarget() * 100), 100) : 0;

            tableModel.addRow(new Object[]{
                c.getCampaignId(),
                c.getName(),
                c.getType(),
                c.getStatus(),
                c.getLeadTarget(),
                c.getLeadsGenerated(),
                String.format("%.1f%%", conversionRate),
                progressPercent
            });

            totalLeads += c.getLeadsGenerated();
            totalTarget += c.getLeadTarget();
            if ("ACTIVE".equals(c.getStatus())) activeCount++;
        }

        totalLeadsLabel.setText(String.valueOf(totalLeads));
        totalTargetLabel.setText(String.valueOf(totalTarget));
        overallRateLabel.setText(totalTarget > 0
                ? String.format("%.1f%%", (double) totalLeads / totalTarget * 100) : "0%");
        activeCampaignsLabel.setText(String.valueOf(activeCount));
    }

    public void refreshData() {
        loadData();
    }

    // Custom progress bar renderer for the table
    private static class ProgressBarRenderer extends JProgressBar implements javax.swing.table.TableCellRenderer {

        public ProgressBarRenderer() {
            setMinimum(0);
            setMaximum(100);
            setStringPainted(true);
            setFont(Constants.FONT_SMALL);
            setBorderPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            int progress = (value instanceof Integer) ? (int) value : 0;
            setValue(progress);
            setString(progress + "%");

            if (progress >= 80) {
                setForeground(new Color(39, 174, 96));
            } else if (progress >= 50) {
                setForeground(new Color(41, 128, 185));
            } else if (progress >= 25) {
                setForeground(new Color(241, 196, 15));
            } else {
                setForeground(new Color(231, 76, 60));
            }

            return this;
        }
    }
}
