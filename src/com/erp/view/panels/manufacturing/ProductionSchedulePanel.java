package com.erp.view.panels.manufacturing;

import com.erp.model.WorkOrder;
import com.erp.service.mock.MockManufacturingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ProductionSchedulePanel displays production scheduling and planning.
 */
public class ProductionSchedulePanel extends JPanel {

    private MockManufacturingService manufacturingService;

    private JTable scheduleTable;
    private DefaultTableModel tableModel;

    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;

    private JLabel scheduledOrdersLabel;
    private JLabel totalQuantityLabel;

    private static final String[] COLUMNS = {"WO Number", "Product", "Quantity", "Status", "Priority", "Planned Start", "Planned End", "Actual Start"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ProductionSchedulePanel() {
        manufacturingService = MockManufacturingService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Date range spinners
        LocalDate today = LocalDate.now();
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        fromDateSpinner.setValue(java.sql.Date.valueOf(today));

        toDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));
        toDateSpinner.setValue(java.sql.Date.valueOf(today.plusDays(30)));

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(scheduleTable);

        // Column widths
        scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(120); // WO Number
        scheduleTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Product
        scheduleTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Quantity
        scheduleTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        scheduleTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Priority
        scheduleTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Planned Start
        scheduleTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Planned End
        scheduleTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Actual Start

        // Status rendering
        scheduleTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        scheduledOrdersLabel = createSummaryValue("0");
        totalQuantityLabel = createSummaryValue("0");
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Top - Summary and filters
        JPanel topPanel = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topPanel.setOpaque(false);

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, Constants.PADDING_MEDIUM, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        summaryPanel.add(createSummaryCard("Scheduled Orders", scheduledOrdersLabel, Constants.PRIMARY_COLOR));
        summaryPanel.add(createSummaryCard("Total Quantity", totalQuantityLabel, Constants.SUCCESS_COLOR));

        // Toolbar
        JPanel toolbar = createToolbar();

        topPanel.add(summaryPanel, BorderLayout.NORTH);
        topPanel.add(toolbar, BorderLayout.SOUTH);

        // Table
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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

        toolbar.add(new JLabel("From:"));
        toolbar.add(fromDateSpinner);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        toolbar.add(new JLabel("To:"));
        toolbar.add(toDateSpinner);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton loadBtn = UIHelper.createPrimaryButton("Load Schedule");
        loadBtn.setPreferredSize(new Dimension(120, 30));
        loadBtn.addActionListener(e -> loadData());
        toolbar.add(loadBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        java.util.Date fromDate = (java.util.Date) fromDateSpinner.getValue();
        java.util.Date toDate = (java.util.Date) toDateSpinner.getValue();

        LocalDate from = new java.sql.Date(fromDate.getTime()).toLocalDate();
        LocalDate to = new java.sql.Date(toDate.getTime()).toLocalDate();

        List<WorkOrder> workOrders = manufacturingService.getScheduledWorkOrders(from, to);

        int totalQty = 0;

        for (WorkOrder wo : workOrders) {
            totalQty += wo.getQuantity();

            tableModel.addRow(new Object[]{
                wo.getWorkOrderNumber(),
                wo.getProductName(),
                wo.getQuantity(),
                wo.getStatus(),
                wo.getPriority(),
                wo.getScheduledStartDate() != null ? wo.getScheduledStartDate().format(DATE_FORMAT) : "",
                wo.getScheduledEndDate() != null ? wo.getScheduledEndDate().format(DATE_FORMAT) : "",
                wo.getActualStartDate() != null ? wo.getActualStartDate().format(DATE_FORMAT) : ""
            });
        }

        scheduledOrdersLabel.setText(String.valueOf(workOrders.size()));
        totalQuantityLabel.setText(String.valueOf(totalQty));
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
                        case "RELEASED":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "IN_PROGRESS":
                            setBackground(new Color(226, 217, 243));
                            setForeground(new Color(73, 54, 103));
                            break;
                        case "COMPLETED":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
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
