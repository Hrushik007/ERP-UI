package com.erp.view.panels.manufacturing;

import com.erp.controller.ManufacturingController;
import com.erp.model.dto.RoutingStepDTO;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Routing tab — loads a routing sequence. If the mock detects a gap in
 * sequence numbers (e.g. 1,2,4) it throws {@code ROUTING_STEP_GAP}; the
 * controller's retry path surfaces it via the exception dialog.
 */
public class RoutingTab extends JPanel
        implements ManufacturingController.ManufacturingListener,
                   ManufacturingHomePanel.Refreshable {

    private static final String[] COLUMNS = {"Seq", "Operation", "Work Center", "Setup (h)", "Run (h)"};

    private final ManufacturingController controller;
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField productField = new JTextField("PROD-001", 12);

    public RoutingTab(ManufacturingController controller) {
        this.controller = controller;
        controller.addListener(this);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(Constants.BG_LIGHT);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(new JLabel("Product ID:"));
        top.add(productField);
        JButton load = UIHelper.createPrimaryButton("Load Routing");
        load.addActionListener(e -> refresh());
        top.add(load);
        JLabel tip = new JLabel("<html><i>Try PROD-002 to trigger ROUTING_STEP_GAP.</i></html>");
        tip.setForeground(Constants.TEXT_SECONDARY);
        top.add(tip);

        UIHelper.styleTable(table);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refresh();
    }

    @Override public void refresh() { controller.loadRouting(this, productField.getText().trim()); }

    @Override
    public void onRoutingLoaded(List<RoutingStepDTO> list) {
        model.setRowCount(0);
        for (RoutingStepDTO s : list) {
            model.addRow(new Object[]{
                    s.getSequenceNumber(), s.getOperationName(), s.getWorkCenterId(),
                    s.getSetupTime(), s.getRunTime()
            });
        }
    }
}
