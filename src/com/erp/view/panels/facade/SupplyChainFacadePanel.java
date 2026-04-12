package com.erp.view.panels.facade;

import com.erp.util.Constants;
import com.erp.view.components.DashboardCard;
import com.erp.view.components.FakeChartPanel;

import javax.swing.*;
import java.awt.*;

public class SupplyChainFacadePanel extends FacadePanelBase {

    public SupplyChainFacadePanel() { super("Supply Chain"); }

    @Override
    protected JComponent buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel stats = statRow(
                new DashboardCard("SKUs Tracked",      "2,418", "+142 this month", Constants.PRIMARY_COLOR),
                new DashboardCard("Below Reorder",     "37",    "urgent 9",        Constants.DANGER_COLOR),
                new DashboardCard("Open POs",          "64",    "12 overdue",      Constants.WARNING_COLOR),
                new DashboardCard("On-Time Delivery",  "91%",   "+2 pts",          Constants.SUCCESS_COLOR)
        );

        JPanel charts = chartRow(
                new FakeChartPanel("Inventory by Warehouse", FakeChartPanel.Style.BAR,
                        new int[]{845, 612, 530, 431},
                        new String[]{"Chennai","Pune","Gurgaon","Kolkata"}),
                new FakeChartPanel("Vendor On-Time %", FakeChartPanel.Style.LINE,
                        new int[]{86, 88, 89, 90, 92, 91},
                        new String[]{"Nov","Dec","Jan","Feb","Mar","Apr"})
        );

        JPanel toolbar = toolbar(
                stubButton("New Purchase Order"),
                secondaryStubButton("Reorder Low Stock"),
                secondaryStubButton("Transfer Stock"),
                secondaryStubButton("Vendor Performance")
        );

        String[] cols = {"Part #", "Description", "Warehouse", "On Hand", "Reorder Pt", "Status"};
        Object[][] data = {
                {"PRT-0101", "Brake Pad Set",          "Chennai", "148", "120", "OK"},
                {"PRT-0214", "Chassis Steel Frame",    "Pune",    "22",  "30",  "REORDER"},
                {"PRT-0322", "Alloy Wheel 18\"",       "Gurgaon", "310", "200", "OK"},
                {"PRT-0418", "Headlight LED Assembly", "Kolkata", "8",   "40",  "URGENT"},
                {"PRT-0505", "EV Battery Pack 60kWh",  "Chennai", "45",  "30",  "OK"},
                {"PRT-0612", "Paint Primer (litres)",  "Pune",    "62",  "80",  "REORDER"},
        };

        body.add(stats);
        body.add(Box.createVerticalStrut(12));
        body.add(charts);
        body.add(Box.createVerticalStrut(12));
        body.add(sectionCard("Stock Levels", fakeTable(cols, data)));
        body.add(Box.createVerticalStrut(10));
        body.add(toolbar);
        return new JScrollPane(body,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
