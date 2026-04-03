package com.erp.view.panels.reporting;

import com.erp.model.ReportTemplate;
import com.erp.service.mock.MockReportingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.ReportTemplateDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportTemplatesPanel displays and manages report templates.
 */
public class ReportTemplatesPanel extends JPanel {

    private MockReportingService reportingService;

    private JTable templatesTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> categoryFilter;

    private JLabel totalTemplatesLabel;
    private JLabel activeTemplatesLabel;

    private static final String[] COLUMNS = {"ID", "Code", "Template Name", "Category", "Layout", "Paper Size", "Status", "Created"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportTemplatesPanel() {
        reportingService = MockReportingService.getInstance();
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
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "SALES", "INVENTORY", "FINANCIAL", "HR", "PROJECT", "CUSTOM"});
        categoryFilter.setFont(Constants.FONT_REGULAR);
        categoryFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        templatesTable = new JTable(tableModel);
        templatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(templatesTable);

        // Column widths
        templatesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        templatesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        templatesTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        templatesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        templatesTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        templatesTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        templatesTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        templatesTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Custom renderers
        templatesTable.getColumnModel().getColumn(3).setCellRenderer(new CategoryCellRenderer());
        templatesTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        // Summary labels
        totalTemplatesLabel = createSummaryValue("0");
        activeTemplatesLabel = createSummaryValue("0");
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

        JScrollPane scrollPane = new JScrollPane(templatesTable);
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

        panel.add(createSummaryCard("Total Templates", totalTemplatesLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Active Templates", activeTemplatesLabel, Constants.SUCCESS_COLOR));

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

        JButton addBtn = UIHelper.createPrimaryButton("New Template");
        addBtn.setPreferredSize(new Dimension(120, 30));
        addBtn.addActionListener(e -> addTemplate());
        toolbar.add(addBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editTemplate());
        toolbar.add(editBtn);

        JButton duplicateBtn = UIHelper.createSecondaryButton("Duplicate");
        duplicateBtn.setPreferredSize(new Dimension(90, 30));
        duplicateBtn.addActionListener(e -> duplicateTemplate());
        toolbar.add(duplicateBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(Constants.FONT_BUTTON);
        deleteBtn.setBackground(Constants.DANGER_COLOR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(80, 30));
        deleteBtn.addActionListener(e -> deleteTemplate());
        toolbar.add(deleteBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<ReportTemplate> templates = reportingService.getAllTemplates();
        String categorySelection = (String) categoryFilter.getSelectedItem();

        int totalCount = 0;
        int activeCount = 0;

        for (ReportTemplate t : templates) {
            if (!"All Categories".equals(categorySelection) && !categorySelection.equals(t.getCategory())) {
                continue;
            }

            totalCount++;
            if (t.isActive()) activeCount++;

            tableModel.addRow(new Object[]{
                t.getTemplateId(),
                t.getTemplateCode(),
                t.getName(),
                t.getCategory(),
                t.getLayout(),
                t.getPaperSize(),
                t.isActive() ? "Active" : "Inactive",
                t.getCreatedDate() != null ? t.getCreatedDate().format(DATE_FORMAT) : ""
            });
        }

        totalTemplatesLabel.setText(String.valueOf(totalCount));
        activeTemplatesLabel.setText(String.valueOf(activeCount));
    }

    private void addTemplate() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ReportTemplateDialog dialog = new ReportTemplateDialog(parentFrame, null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            ReportTemplate newTemplate = dialog.getTemplate();
            reportingService.createTemplate(newTemplate);
            UIHelper.showSuccess(this, "Template created successfully.");
            loadData();
        }
    }

    private void editTemplate() {
        int row = templatesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a template to edit.");
            return;
        }

        int templateId = (int) tableModel.getValueAt(row, 0);
        ReportTemplate template = reportingService.getTemplateById(templateId);

        if (template != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ReportTemplateDialog dialog = new ReportTemplateDialog(parentFrame, template);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                reportingService.updateTemplate(dialog.getTemplate());
                UIHelper.showSuccess(this, "Template updated successfully.");
                loadData();
            }
        }
    }

    private void duplicateTemplate() {
        int row = templatesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a template to duplicate.");
            return;
        }

        int templateId = (int) tableModel.getValueAt(row, 0);
        ReportTemplate original = reportingService.getTemplateById(templateId);

        if (original != null) {
            ReportTemplate copy = new ReportTemplate();
            copy.setTemplateCode("TPL-" + System.currentTimeMillis() % 10000);
            copy.setName(original.getName() + " (Copy)");
            copy.setDescription(original.getDescription());
            copy.setCategory(original.getCategory());
            copy.setLayout(original.getLayout());
            copy.setPaperSize(original.getPaperSize());
            copy.setHeaderContent(original.getHeaderContent());
            copy.setFooterContent(original.getFooterContent());
            copy.setColumns(original.getColumns());
            copy.setStyling(original.getStyling());
            copy.setIncludeHeader(original.isIncludeHeader());
            copy.setIncludeFooter(original.isIncludeFooter());
            copy.setIncludePageNumbers(original.isIncludePageNumbers());
            copy.setIncludeDateStamp(original.isIncludeDateStamp());
            copy.setActive(true);

            reportingService.createTemplate(copy);
            UIHelper.showSuccess(this, "Template duplicated successfully.");
            loadData();
        }
    }

    private void deleteTemplate() {
        int row = templatesTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a template to delete.");
            return;
        }

        int templateId = (int) tableModel.getValueAt(row, 0);
        String templateName = (String) tableModel.getValueAt(row, 2);

        boolean confirm = UIHelper.showConfirm(this, "Delete template '" + templateName + "'? This cannot be undone.");
        if (confirm) {
            if (reportingService.deleteTemplate(templateId)) {
                UIHelper.showSuccess(this, "Template deleted successfully.");
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
                    case "SALES":
                        setBackground(new Color(209, 236, 241));
                        setForeground(new Color(12, 84, 96));
                        break;
                    case "INVENTORY":
                        setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                        break;
                    case "FINANCIAL":
                        setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                        break;
                    case "HR":
                        setBackground(new Color(230, 230, 250));
                        setForeground(new Color(75, 0, 130));
                        break;
                    case "PROJECT":
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

    // Status cell renderer
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && !isSelected) {
                String status = value.toString();
                if ("Active".equals(status)) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                } else {
                    setBackground(new Color(248, 215, 218));
                    setForeground(new Color(114, 28, 36));
                }
            }
            return this;
        }
    }
}
