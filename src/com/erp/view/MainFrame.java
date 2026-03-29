package com.erp.view;

import com.erp.model.User;
import com.erp.service.AuthenticationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.components.Sidebar;
import com.erp.view.panels.BasePanel;
import com.erp.view.panels.DashboardPanel;
import com.erp.view.panels.PlaceholderPanel;
import com.erp.view.panels.hr.HRPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * MainFrame is the primary application window after login.
 *
 * This class demonstrates several important concepts:
 *
 * 1. COMPOSITION: MainFrame contains a Sidebar and multiple panels.
 *    "Favor composition over inheritance" is a key OOP principle.
 *
 * 2. MVC PATTERN: MainFrame acts as a View and Controller combination.
 *    - View: Displays the UI components
 *    - Controller: Handles navigation between panels
 *
 * 3. CARD LAYOUT: A powerful layout manager for switching between panels.
 *    Think of it like a deck of cards - only one card is visible at a time.
 *
 * 4. LAZY INITIALIZATION: Panels are created only when first accessed.
 *    This improves startup performance.
 *
 * 5. CACHING: Once created, panels are cached in a Map for reuse.
 */
public class MainFrame extends JFrame {

    // Sidebar navigation component
    private Sidebar sidebar;

    // Panel that holds the content (uses CardLayout)
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Cache of created panels - avoids recreating panels every time
    // Key: action command (e.g., "dashboard"), Value: the panel instance
    private Map<String, BasePanel> panelCache;

    // Top header bar component
    private JPanel headerBar;

    // Current module label in header
    private JLabel currentModuleLabel;

    /**
     * Constructor initializes the main application frame.
     */
    public MainFrame() {
        panelCache = new HashMap<>();

        setupFrame();
        initializeComponents();
        setupNavigation();

        // Show dashboard by default
        showPanel("dashboard");
    }

    /**
     * Configures the JFrame properties.
     */
    private void setupFrame() {
        setTitle(Constants.APP_NAME);
        setSize(Constants.MAIN_SIZE);
        setMinimumSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UIHelper.centerWindow(this);
    }

    /**
     * Creates and arranges all UI components.
     */
    private void initializeComponents() {
        // Main container
        JPanel mainContainer = new JPanel(new BorderLayout());

        // Create sidebar
        sidebar = new Sidebar();

        // Create header bar
        headerBar = createHeaderBar();

        // Create content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Constants.BG_LIGHT);

        // Right side panel (header + content)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(headerBar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        // Add to main container
        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);
    }

    /**
     * Creates the top header bar with user info and logout.
     */
    private JPanel createHeaderBar() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Constants.BG_WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            new EmptyBorder(0, Constants.PADDING_LARGE, 0, Constants.PADDING_LARGE)
        ));

        // Left side - current module name
        currentModuleLabel = new JLabel(Constants.MODULE_DASHBOARD);
        currentModuleLabel.setFont(Constants.FONT_SUBTITLE);
        currentModuleLabel.setForeground(Constants.TEXT_PRIMARY);

        // Right side - user info and logout
        JPanel userPanel = createUserPanel();

        header.add(currentModuleLabel, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Creates the user info panel with logout button.
     */
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);

        // Get current user
        User user = AuthenticationService.getInstance().getCurrentUser();
        String displayName = (user != null) ? user.getFullName() : "User";
        String role = (user != null) ? user.getRole() : "";

        // User info
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

        // Logout button
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

    /**
     * Sets up navigation by connecting sidebar to content switching.
     *
     * This demonstrates the OBSERVER/CALLBACK pattern:
     * - Sidebar fires events when items are clicked
     * - MainFrame listens and responds by switching content
     */
    private void setupNavigation() {
        sidebar.setMenuActionListener(this::handleNavigation);
    }

    /**
     * Handles navigation events from the sidebar.
     *
     * @param e The action event containing the module command
     */
    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        showPanel(command);
    }

    /**
     * Shows the panel for the given command.
     * Uses lazy initialization - creates panel only when first needed.
     *
     * @param command The module command (e.g., "dashboard", "hr", "crm")
     */
    private void showPanel(String command) {
        // Check if panel is already created
        if (!panelCache.containsKey(command)) {
            // Create and cache the panel
            BasePanel panel = createPanelForCommand(command);
            panelCache.put(command, panel);
            contentPanel.add(panel, command);
        }

        // Get the panel and update header
        BasePanel panel = panelCache.get(command);
        currentModuleLabel.setText(panel.getPanelTitle());

        // Refresh the panel data
        panel.refreshData();

        // Show the panel using CardLayout
        cardLayout.show(contentPanel, command);
    }

    /**
     * Factory method to create the appropriate panel for a command.
     *
     * This is a simple FACTORY METHOD pattern:
     * - Input: a string command
     * - Output: the appropriate BasePanel subclass
     *
     * As we implement more modules, we add more cases here.
     *
     * @param command The module command
     * @return The appropriate panel instance
     */
    private BasePanel createPanelForCommand(String command) {
        switch (command) {
            case "dashboard":
                return new DashboardPanel();

            // All other modules use PlaceholderPanel for now
            // As we implement them in future batches, we'll add cases here
            case "crm":
                return new PlaceholderPanel(Constants.MODULE_CRM);
            case "sales":
                return new PlaceholderPanel(Constants.MODULE_SALES);
            case "order":
                return new PlaceholderPanel(Constants.MODULE_ORDER);
            case "inventory":
                return new PlaceholderPanel(Constants.MODULE_INVENTORY);
            case "manufacturing":
                return new PlaceholderPanel(Constants.MODULE_MANUFACTURING);
            case "finance":
                return new PlaceholderPanel(Constants.MODULE_FINANCE);
            case "accounting":
                return new PlaceholderPanel(Constants.MODULE_ACCOUNTING);
            case "hr":
                return new HRPanel();
            case "project":
                return new PlaceholderPanel(Constants.MODULE_PROJECT);
            case "reporting":
                return new PlaceholderPanel(Constants.MODULE_REPORTING);
            case "analytics":
                return new PlaceholderPanel(Constants.MODULE_ANALYTICS);
            case "bi":
                return new PlaceholderPanel(Constants.MODULE_BI);
            case "marketing":
                return new PlaceholderPanel(Constants.MODULE_MARKETING);
            case "automation":
                return new PlaceholderPanel(Constants.MODULE_AUTOMATION);
            case "integration":
                return new PlaceholderPanel(Constants.MODULE_INTEGRATION);

            default:
                return new PlaceholderPanel("Unknown Module");
        }
    }

    /**
     * Handles user logout.
     */
    private void handleLogout() {
        // Confirm logout
        boolean confirm = UIHelper.showConfirm(this, "Are you sure you want to logout?");

        if (confirm) {
            // Logout from service
            AuthenticationService.getInstance().logout();

            // Show login frame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

            // Close this frame
            this.dispose();
        }
    }
}
