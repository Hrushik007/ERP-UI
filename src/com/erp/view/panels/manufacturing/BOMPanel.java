package com.erp.view.panels.manufacturing;

import com.erp.model.BillOfMaterials;
import com.erp.model.BOMItem;
import com.erp.service.mock.MockManufacturingService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * BOMPanel displays and manages Bills of Materials.
 */
public class BOMPanel extends JPanel {

    private MockManufacturingService manufacturingService;

    private JTable bomsTable;
    private DefaultTableModel bomsTableModel;

    private JTable componentsTable;
    private DefaultTableModel componentsTableModel;

    private JComboBox<String> statusFilter;

    private static final String[] BOM_COLUMNS = {"BOM ID", "BOM Code", "Product ID", "Version", "Status", "Output Qty", "Components"};
    private static final String[] COMPONENT_COLUMNS = {"Component ID", "Component Name", "Quantity", "Unit", "Sequence"};

    public BOMPanel() {
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
        // Status filter
        statusFilter = new JComboBox<>(new String[]{"All Statuses", "ACTIVE", "DRAFT", "OBSOLETE"});
        statusFilter.setFont(Constants.FONT_REGULAR);
        statusFilter.addActionListener(e -> loadData());

        // BOMs table
        bomsTableModel = new DefaultTableModel(BOM_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bomsTable = new JTable(bomsTableModel);
        bomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(bomsTable);

        bomsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        bomsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadComponentsForSelectedBOM();
            }
        });

        // Components table
        componentsTableModel = new DefaultTableModel(COMPONENT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        componentsTable = new JTable(componentsTableModel);
        UIHelper.styleTable(componentsTable);
    }

    private void layoutComponents() {
        // Toolbar
        JPanel toolbar = createToolbar();

        // Split pane with BOMs and Components
        JScrollPane bomsScrollPane = new JScrollPane(bomsTable);
       bomsScrollPane.setBorder(BorderFactory.createTitledBorder("Bills of Materials"));

        JScrollPane componentsScrollPane = new JScrollPane(componentsTable);
        componentsScrollPane.setBorder(BorderFactory.createTitledBorder("BOM Components"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bomsScrollPane, componentsScrollPane);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.5);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        toolbar.setBackground(Constants.BG_LIGHT);

        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));

        JButton createBtn = UIHelper.createPrimaryButton("Create BOM");
        createBtn.setPreferredSize(new Dimension(110, 30));
        createBtn.addActionListener(e -> createBOM());
        toolbar.add(createBtn);

        JButton editBtn = UIHelper.createSecondaryButton("Edit");
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.addActionListener(e -> editBOM());
        toolbar.add(editBtn);

        JButton activateBtn = UIHelper.createSecondaryButton("Activate");
        activateBtn.setPreferredSize(new Dimension(90, 30));
        activateBtn.addActionListener(e -> activateBOM());
        toolbar.add(activateBtn);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        return toolbar;
    }

    public void loadData() {
        bomsTableModel.setRowCount(0);
        componentsTableModel.setRowCount(0);

        List<BillOfMaterials> boms = manufacturingService.getAllBOMs();
        String statusSelection = (String) statusFilter.getSelectedItem();

        for (BillOfMaterials bom : boms) {
            if (!"All Statuses".equals(statusSelection) && !statusSelection.equals(bom.getStatus())) {
                continue;
            }

            bomsTableModel.addRow(new Object[]{
                bom.getBomId(),
                bom.getBomCode(),
                bom.getProductId(),
                bom.getVersion(),
                bom.getStatus(),
                bom.getOutputQuantity(),
                bom.getComponents().size()
            });
        }
    }

    private void loadComponentsForSelectedBOM() {
        componentsTableModel.setRowCount(0);

        int row = bomsTable.getSelectedRow();
        if (row < 0) return;

        int bomId = (int) bomsTableModel.getValueAt(row, 0);
        BillOfMaterials bom = manufacturingService.getBOMById(bomId);

        if (bom != null && bom.getComponents() != null) {
            for (BOMItem item : bom.getComponents()) {
                componentsTableModel.addRow(new Object[]{
                    item.getComponentProductId(),
                    item.getComponentName(),
                    item.getQuantity(),
                    item.getUnitOfMeasure(),
                    item.getSequence()
                });
            }
        }
    }

    private void createBOM() {
        UIHelper.showSuccess(this, "Create BOM functionality will be implemented with full form.");
    }

    private void editBOM() {
        UIHelper.showSuccess(this, "Edit BOM functionality will be implemented with full form.");
    }

    private void activateBOM() {
        int row = bomsTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Please select a BOM to activate.");
            return;
        }

        int bomId = (int) bomsTableModel.getValueAt(row, 0);
        String bomCode = (String) bomsTableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Activate BOM '" + bomCode + "'?\nThis will obsolete other active versions for the same product.",
            "Confirm Activation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (manufacturingService.activateBOM(bomId)) {
                UIHelper.showSuccess(this, "BOM activated successfully.");
                loadData();
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
                        case "ACTIVE":
                            setBackground(new Color(212, 237, 218));
                            setForeground(new Color(21, 87, 36));
                            break;
                        case "DRAFT":
                            setBackground(new Color(255, 243, 205));
                            setForeground(new Color(133, 100, 4));
                            break;
                        case "OBSOLETE":
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
