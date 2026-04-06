package com.erp.view.panels.integration;

import com.erp.service.mock.MockIntegrationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ImportExportPanel handles data import and export operations.
 *
 * Features:
 * - Import data form with data type, format selection
 * - Export data form with data type, format, and filter options
 * - History table showing past import/export operations
 */
public class ImportExportPanel extends JPanel {

    private MockIntegrationService integrationService;

    // History table
    private JTable historyTable;
    private DefaultTableModel historyModel;

    private static final String[] HISTORY_COLUMNS = {"Operation", "Data Type", "Format", "Total", "Success", "Errors", "Status", "Timestamp"};

    public ImportExportPanel() {
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
        // History table
        historyModel = new DefaultTableModel(HISTORY_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(historyTable);

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(130);

        // Operation column renderer
        historyTable.getColumnModel().getColumn(0).setCellRenderer(new OperationCellRenderer());
        // Status column renderer
        historyTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
    }

    private void layoutComponents() {
        // Action buttons panel (Import & Export)
        JPanel actionsPanel = createActionsPanel();

        // History section
        JPanel historySection = new JPanel(new BorderLayout());
        historySection.setBackground(Constants.BG_WHITE);
        historySection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JPanel historyHeader = new JPanel(new BorderLayout());
        historyHeader.setOpaque(false);
        historyHeader.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        JLabel historyTitle = new JLabel("Import / Export History");
        historyTitle.setFont(Constants.FONT_SUBTITLE);
        historyTitle.setForeground(Constants.TEXT_PRIMARY);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());

        historyHeader.add(historyTitle, BorderLayout.WEST);
        historyHeader.add(refreshBtn, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        historySection.add(historyHeader, BorderLayout.NORTH);
        historySection.add(scrollPane, BorderLayout.CENTER);

        add(actionsPanel, BorderLayout.NORTH);
        add(historySection, BorderLayout.CENTER);
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_MEDIUM, 0));

        panel.add(createImportCard());
        panel.add(createExportCard());

        return panel;
    }

    private JPanel createImportCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(Constants.SUCCESS_COLOR);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel title = new JLabel("Import Data");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setForeground(Constants.TEXT_PRIMARY);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setOpaque(false);

        JComboBox<String> dataTypeCombo = new JComboBox<>(new String[]{"CUSTOMERS", "PRODUCTS", "ORDERS", "VENDORS", "EMPLOYEES", "LEADS"});
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"CSV", "JSON", "XML"});

        formPanel.add(new JLabel("Data Type:"));
        formPanel.add(dataTypeCombo);
        formPanel.add(new JLabel("Format:"));
        formPanel.add(formatCombo);
        formPanel.add(new JLabel(""));

        JButton importBtn = UIHelper.createPrimaryButton("Import");
        importBtn.setPreferredSize(new Dimension(100, 30));
        importBtn.addActionListener(e -> {
            String dataType = (String) dataTypeCombo.getSelectedItem();
            String format = (String) formatCombo.getSelectedItem();

            if (UIHelper.showConfirm(this, "Import " + dataType + " from " + format + " format?")) {
                Map<String, Object> result = integrationService.importData(dataType, format, "sample_data");
                int imported = (int) result.getOrDefault("recordsImported", 0);
                int errors = (int) result.getOrDefault("errors", 0);
                String message = "Import completed!\nRecords imported: " + imported;
                if (errors > 0) {
                    message += "\nErrors: " + errors;
                }
                UIHelper.showSuccess(this, message);
                loadData();
            }
        });
        formPanel.add(importBtn);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 8));
        contentPanel.setOpaque(false);
        contentPanel.add(title, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createExportCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(Constants.PRIMARY_COLOR);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel title = new JLabel("Export Data");
        title.setFont(Constants.FONT_SUBTITLE);
        title.setForeground(Constants.TEXT_PRIMARY);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setOpaque(false);

        JComboBox<String> dataTypeCombo = new JComboBox<>(new String[]{"CUSTOMERS", "PRODUCTS", "ORDERS", "VENDORS", "EMPLOYEES", "TRANSACTIONS", "LEADS"});
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"CSV", "JSON", "XML"});

        formPanel.add(new JLabel("Data Type:"));
        formPanel.add(dataTypeCombo);
        formPanel.add(new JLabel("Format:"));
        formPanel.add(formatCombo);
        formPanel.add(new JLabel(""));

        JButton exportBtn = UIHelper.createPrimaryButton("Export");
        exportBtn.setPreferredSize(new Dimension(100, 30));
        exportBtn.addActionListener(e -> {
            String dataType = (String) dataTypeCombo.getSelectedItem();
            String format = (String) formatCombo.getSelectedItem();

            if (UIHelper.showConfirm(this, "Export " + dataType + " to " + format + " format?")) {
                String result = integrationService.exportData(dataType, format, new HashMap<>());
                UIHelper.showSuccess(this, result);
                loadData();
            }
        });
        formPanel.add(exportBtn);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 8));
        contentPanel.setOpaque(false);
        contentPanel.add(title, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    public void loadData() {
        historyModel.setRowCount(0);
        List<Map<String, Object>> history = integrationService.getImportExportHistory(50);
        for (Map<String, Object> record : history) {
            historyModel.addRow(new Object[]{
                record.get("operation"),
                record.get("dataType"),
                record.get("format"),
                record.get("totalRecords"),
                record.get("successRecords"),
                record.get("errorRecords"),
                record.get("status"),
                record.get("timestamp")
            });
        }
    }

    public void refreshData() {
        loadData();
    }

    // Operation column renderer
    private static class OperationCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                if ("IMPORT".equals(value.toString())) {
                    setBackground(new Color(209, 236, 241));
                    setForeground(new Color(12, 84, 96));
                } else {
                    setBackground(new Color(232, 218, 239));
                    setForeground(new Color(74, 20, 140));
                }
            }
            return this;
        }
    }

    // Status column renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                switch (value.toString()) {
                    case "SUCCESS":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "COMPLETED_WITH_ERRORS":
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
