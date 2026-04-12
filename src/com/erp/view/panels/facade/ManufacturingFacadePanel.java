package com.erp.view.panels.facade;

import com.erp.util.Constants;
import com.erp.view.components.DashboardCard;
import com.erp.view.components.FakeChartPanel;

import javax.swing.*;
import java.awt.*;

public class ManufacturingFacadePanel extends FacadePanelBase {

    public ManufacturingFacadePanel() { super("Manufacturing"); }

    @Override
    protected JComponent buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel stats = statRow(
                new DashboardCard("Active Work Orders", "46", "8 critical",  Constants.PRIMARY_COLOR),
                new DashboardCard("OEE (today)",         "82%", "+3 pts",      Constants.SUCCESS_COLOR),
                new DashboardCard("Units/hr (avg)",      "18",  "target 20",   Constants.WARNING_COLOR),
                new DashboardCard("Downtime (min)",      "37",  "Line-C issue", Constants.DANGER_COLOR)
        );

        JPanel charts = chartRow(
                new FakeChartPanel("Units Produced by Line", FakeChartPanel.Style.BAR,
                        new int[]{124, 168, 92, 145},
                        new String[]{"Line-A","Line-B","Line-C","Line-D"}),
                new FakeChartPanel("OEE Trend", FakeChartPanel.Style.LINE,
                        new int[]{74, 76, 79, 78, 82, 80},
                        new String[]{"W1","W2","W3","W4","W5","W6"})
        );

        JPanel toolbar = toolbar(
                stubButton("New Work Order"),
                secondaryStubButton("Release to Shop Floor"),
                secondaryStubButton("Quality Hold"),
                secondaryStubButton("BOM Explorer")
        );

        String[] cols = {"WO #", "Model", "Line", "Qty", "Start", "Status"};
        Object[][] data = {
                {"WO-7801", "Model-S Sedan",      "Line-A", "25", "2026-04-11", "IN_PROGRESS"},
                {"WO-7802", "Model-X SUV",        "Line-B", "18", "2026-04-10", "IN_PROGRESS"},
                {"WO-7803", "Model-T Truck",      "Line-D", "10", "2026-04-09", "QUALITY_HOLD"},
                {"WO-7804", "Model-EV Electric",  "Line-C", "12", "2026-04-12", "PLANNED"},
                {"WO-7805", "Model-S Sedan",      "Line-A", "30", "2026-04-13", "RELEASED"},
                {"WO-7806", "Model-X SUV",        "Line-B", "15", "2026-04-14", "PLANNED"},
        };

        body.add(stats);
        body.add(Box.createVerticalStrut(12));
        body.add(charts);
        body.add(Box.createVerticalStrut(12));
        body.add(sectionCard("Active Work Orders", fakeTable(cols, data)));
        body.add(Box.createVerticalStrut(10));
        body.add(toolbar);
        return new JScrollPane(body,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
