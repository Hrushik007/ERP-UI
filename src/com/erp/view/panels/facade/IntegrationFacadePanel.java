package com.erp.view.panels.facade;

import com.erp.util.Constants;
import com.erp.view.components.DashboardCard;
import com.erp.view.components.FakeChartPanel;

import javax.swing.*;
import java.awt.*;

public class IntegrationFacadePanel extends FacadePanelBase {

    public IntegrationFacadePanel() { super("Integration"); }

    @Override
    protected JComponent buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel stats = statRow(
                new DashboardCard("External Systems",  "11",   "all reachable", Constants.SUCCESS_COLOR),
                new DashboardCard("Sync Jobs (today)", "842",  "9 retrying",    Constants.PRIMARY_COLOR),
                new DashboardCard("Avg Latency",       "240ms","healthy",       Constants.ACCENT_COLOR),
                new DashboardCard("Webhooks Failed",   "3",    "last 24h",      Constants.DANGER_COLOR)
        );

        JPanel charts = chartRow(
                new FakeChartPanel("Sync Volume by System", FakeChartPanel.Style.BAR,
                        new int[]{240, 182, 145, 98, 72, 65},
                        new String[]{"MES","CRM","GST","SAP","Bank","Courier"}),
                new FakeChartPanel("Latency (ms) Trend", FakeChartPanel.Style.LINE,
                        new int[]{260, 245, 238, 250, 235, 240},
                        new String[]{"Mon","Tue","Wed","Thu","Fri","Sat"})
        );

        JPanel toolbar = toolbar(
                stubButton("Register Endpoint"),
                secondaryStubButton("Manual Sync"),
                secondaryStubButton("Rotate API Key"),
                secondaryStubButton("View Health")
        );

        String[] cols = {"System", "Type", "Direction", "Last Sync", "Status"};
        Object[][] data = {
                {"MES Gateway",      "REST",      "Inbound",       "2026-04-12 07:05", "OK"},
                {"Dealer CRM",       "REST",      "Bi-directional","2026-04-12 06:58", "OK"},
                {"GST Portal",       "SOAP",      "Outbound",      "2026-04-12 06:00", "RETRY"},
                {"SAP Finance",      "IDoc",      "Outbound",      "2026-04-12 05:45", "OK"},
                {"Bank (HDFC)",      "SFTP",      "Inbound",       "2026-04-12 04:30", "OK"},
                {"Courier (BlueDart)","REST",     "Outbound",      "2026-04-12 07:10", "OK"},
        };

        body.add(stats);
        body.add(Box.createVerticalStrut(12));
        body.add(charts);
        body.add(Box.createVerticalStrut(12));
        body.add(sectionCard("External Systems", fakeTable(cols, data)));
        body.add(Box.createVerticalStrut(10));
        body.add(toolbar);
        return new JScrollPane(body,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
