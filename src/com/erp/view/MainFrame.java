package com.erp.view;

import com.erp.exception.AuthException;
import com.erp.exception.ExceptionHandler;
import com.erp.session.UserSession;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.components.Sidebar;
import com.erp.view.panels.BasePanel;
import com.erp.view.panels.IntegratedDashboardPanel;
import com.erp.view.panels.facade.AccountingFacadePanel;
import com.erp.view.panels.facade.AnalyticsFacadePanel;
import com.erp.view.panels.facade.AutomationFacadePanel;
import com.erp.view.panels.facade.BIFacadePanel;
import com.erp.view.panels.facade.CRMFacadePanel;
import com.erp.view.panels.facade.FinanceFacadePanel;
import com.erp.view.panels.facade.IntegrationFacadePanel;
import com.erp.view.panels.facade.ManufacturingFacadePanel;
import com.erp.view.panels.facade.MarketingFacadePanel;
import com.erp.view.panels.facade.ProjectFacadePanel;
import com.erp.view.panels.facade.ReportingFacadePanel;
import com.erp.view.panels.facade.SalesFacadePanel;
import com.erp.view.panels.facade.SupplyChainFacadePanel;
import com.erp.view.panels.hr.HRHomePanel;
import com.erp.view.panels.orders.OrdersHomePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Primary application shell. Hosts the sidebar + header + module area
 * (CardLayout). Modules are created lazily by a Factory Method; access
 * is gated by RBAC using the active {@link UserSession}.
 */
public class MainFrame extends JFrame {

    private Sidebar sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final Map<String, BasePanel> panelCache = new HashMap<>();
    private JPanel headerBar;
    private JLabel currentModuleLabel;

    // RBAC matrix: which roles may open which modules. Dashboard is available to all.
    private static final Map<String, Set<String>> ACCESS = new HashMap<>();
    static {
        Set<String> all = new HashSet<>(Arrays.asList(
                UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER, UserSession.ROLE_EMPLOYEE,
                UserSession.ROLE_HR, UserSession.ROLE_SALES));
        ACCESS.put("dashboard", all);
        ACCESS.put("order", new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER, UserSession.ROLE_SALES, UserSession.ROLE_EMPLOYEE)));
        ACCESS.put("hr",    new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER, UserSession.ROLE_HR)));
        ACCESS.put("crm",   new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER, UserSession.ROLE_SALES)));
        ACCESS.put("sales", new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER, UserSession.ROLE_SALES)));
        ACCESS.put("inventory",     all);
        ACCESS.put("manufacturing", all);
        ACCESS.put("finance",     new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER)));
        ACCESS.put("accounting",  new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER)));
        ACCESS.put("project", all);
        ACCESS.put("reporting", all);
        ACCESS.put("analytics", all);
        ACCESS.put("bi", all);
        ACCESS.put("marketing",  new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER, UserSession.ROLE_SALES)));
        ACCESS.put("automation", all);
        ACCESS.put("integration", new HashSet<>(Arrays.asList(UserSession.ROLE_ADMIN, UserSession.ROLE_MANAGER)));
    }

    public MainFrame() {
        setupFrame();
        initializeComponents();
        setupNavigation();
        showPanel("dashboard");
    }

    private void setupFrame() {
        setTitle(Constants.APP_NAME);
        setSize(Constants.MAIN_SIZE);
        setMinimumSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UIHelper.centerWindow(this);
    }

    private void initializeComponents() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        sidebar = new Sidebar();
        headerBar = createHeaderBar();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Constants.BG_LIGHT);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(headerBar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(rightPanel, BorderLayout.CENTER);
        setContentPane(mainContainer);
    }

    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Constants.BG_WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(0, Constants.PADDING_LARGE, 0, Constants.PADDING_LARGE)));

        currentModuleLabel = new JLabel(Constants.MODULE_DASHBOARD);
        currentModuleLabel.setFont(Constants.FONT_SUBTITLE);
        currentModuleLabel.setForeground(Constants.TEXT_PRIMARY);

        header.add(currentModuleLabel, BorderLayout.WEST);
        header.add(createUserPanel(), BorderLayout.EAST);
        return header;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);

        UserSession session = UserSession.getInstance();
        String displayName = session.isValid() ? session.getDisplayName() : "User";
        String role = session.isValid() ? session.getRole() : "";

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(Constants.FONT_REGULAR);
        nameLabel.setForeground(Constants.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(Constants.FONT_SMALL);
        roleLabel.setForeground(Constants.TEXT_SECONDARY);
        roleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userInfo.add(nameLabel);
        userInfo.add(roleLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(Constants.FONT_SMALL);
        logoutButton.setForeground(Constants.DANGER_COLOR);
        logoutButton.setBackground(Constants.BG_WHITE);
        logoutButton.setBorder(BorderFactory.createLineBorder(Constants.DANGER_COLOR, 1));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.addActionListener(e -> handleLogout());

        panel.add(userInfo);
        panel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        panel.add(logoutButton);
        return panel;
    }

    private void setupNavigation() {
        sidebar.setMenuActionListener(this::handleNavigation);
    }

    private void handleNavigation(ActionEvent e) { showPanel(e.getActionCommand()); }

    private void showPanel(String command) {
        UserSession session = UserSession.getInstance();
        Set<String> allowed = ACCESS.get(command);
        if (allowed != null && session.isValid() && !allowed.contains(session.getRole())) {
            ExceptionHandler.handle(this,
                    AuthException.unauthorizedModule(command, session.getRole()));
            return;
        }

        if (!panelCache.containsKey(command)) {
            try {
                BasePanel panel = createPanelForCommand(command);
                panelCache.put(command, panel);
                contentPanel.add(panel, command);
            } catch (Exception e) {
                System.err.println("Error creating panel for: " + command);
                e.printStackTrace();
                return;
            }
        }
        BasePanel panel = panelCache.get(command);
        currentModuleLabel.setText(panel.getPanelTitle());
        panel.refreshData();
        cardLayout.show(contentPanel, command);
    }

    /**
     * Factory Method: routes command string -> correct BasePanel subclass.
     * Two deep-integration modules (orders, hr) + the integrated dashboard;
     * everything else is a rich facade mockup.
     */
    private BasePanel createPanelForCommand(String command) {
        switch (command) {
            case "dashboard":     return new IntegratedDashboardPanel();
            case "order":         return new OrdersHomePanel();
            case "hr":            return new HRHomePanel();
            case "crm":           return new CRMFacadePanel();
            case "sales":         return new SalesFacadePanel();
            case "inventory":     return new SupplyChainFacadePanel();
            case "manufacturing": return new ManufacturingFacadePanel();
            case "finance":       return new FinanceFacadePanel();
            case "accounting":    return new AccountingFacadePanel();
            case "project":       return new ProjectFacadePanel();
            case "reporting":     return new ReportingFacadePanel();
            case "analytics":     return new AnalyticsFacadePanel();
            case "bi":            return new BIFacadePanel();
            case "marketing":     return new MarketingFacadePanel();
            case "automation":    return new AutomationFacadePanel();
            case "integration":   return new IntegrationFacadePanel();
            default:              return new IntegratedDashboardPanel();
        }
    }

    private void handleLogout() {
        boolean confirm = UIHelper.showConfirm(this, "Are you sure you want to logout?");
        if (confirm) {
            UserSession.getInstance().end();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }
}
