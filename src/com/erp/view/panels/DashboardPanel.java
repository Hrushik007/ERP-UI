package com.erp.view.panels;

import com.erp.model.User;
import com.erp.service.AuthenticationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DashboardPanel is the main landing page after login.
 *
 * This class extends BasePanel, demonstrating INHERITANCE:
 * - It inherits all the structure and methods from BasePanel
 * - It MUST implement the abstract methods (initializeComponents, layoutComponents)
 * - It CAN override other methods if needed
 *
 * The dashboard provides:
 * - Welcome message with user info
 * - Quick stats/metrics (placeholder data for now)
 * - Overview of system modules
 */
public class DashboardPanel extends BasePanel {

    // Components specific to this panel
    private JLabel welcomeLabel;
    private JLabel roleLabel;
    private JLabel dateTimeLabel;
    private JPanel statsPanel;
    private JPanel quickActionsPanel;

    /**
     * Constructor - calls parent constructor which sets up the template.
     */
    public DashboardPanel() {
        super(Constants.MODULE_DASHBOARD);  // Call parent constructor with title
    }

    /**
     * Implementation of abstract method from BasePanel.
     * Creates all the UI components needed for this panel.
     */
    @Override
    protected void initializeComponents() {
        // Get current user info
        User user = AuthenticationService.getInstance().getCurrentUser();
        String userName = (user != null) ? user.getFullName() : "User";
        String userRole = (user != null) ? user.getRole() : "Unknown";

        // Create welcome message
        welcomeLabel = new JLabel("Welcome back, " + userName + "!");
        welcomeLabel.setFont(Constants.FONT_SUBTITLE);
        welcomeLabel.setForeground(Constants.TEXT_PRIMARY);

        roleLabel = new JLabel("Role: " + userRole);
        roleLabel.setFont(Constants.FONT_REGULAR);
        roleLabel.setForeground(Constants.TEXT_SECONDARY);

        // Date/time label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - HH:mm");
        dateTimeLabel = new JLabel(LocalDateTime.now().format(formatter));
        dateTimeLabel.setFont(Constants.FONT_SMALL);
        dateTimeLabel.setForeground(Constants.TEXT_SECONDARY);

        // Create stats panel
        statsPanel = createStatsPanel();

        // Create quick actions panel
        quickActionsPanel = createQuickActionsPanel();
    }

    /**
     * Implementation of abstract method from BasePanel.
     * Arranges all components in the content panel.
     */
    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new BorderLayout(0, Constants.PADDING_LARGE));

        // Top section with welcome message
        JPanel welcomeSection = new JPanel();
        welcomeSection.setLayout(new BoxLayout(welcomeSection, BoxLayout.Y_AXIS));
        welcomeSection.setOpaque(false);
        welcomeSection.add(welcomeLabel);
        welcomeSection.add(Box.createVerticalStrut(5));
        welcomeSection.add(roleLabel);
        welcomeSection.add(Box.createVerticalStrut(5));
        welcomeSection.add(dateTimeLabel);

        // Main content with stats and actions
        JPanel mainContent = new JPanel(new GridLayout(1, 2, Constants.PADDING_LARGE, 0));
        mainContent.setOpaque(false);
        mainContent.add(statsPanel);
        mainContent.add(quickActionsPanel);

        contentPanel.add(welcomeSection, BorderLayout.NORTH);
        contentPanel.add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Creates the statistics overview panel.
     * Shows key metrics in a card-based layout.
     */
    private JPanel createStatsPanel() {
        JPanel panel = createSection("System Overview");

        JPanel cardsContainer = new JPanel(new GridLayout(2, 2, Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        cardsContainer.setOpaque(false);

        // Create stat cards (placeholder data)
        cardsContainer.add(createStatCard("Total Employees", "156", Constants.PRIMARY_COLOR));
        cardsContainer.add(createStatCard("Active Projects", "23", Constants.ACCENT_COLOR));
        cardsContainer.add(createStatCard("Pending Orders", "47", Constants.WARNING_COLOR));
        cardsContainer.add(createStatCard("Open Tickets", "12", Constants.DANGER_COLOR));

        panel.add(cardsContainer, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a single stat card with value and label.
     *
     * @param title The stat name
     * @param value The stat value
     * @param color The accent color for this stat
     * @return A styled stat card panel
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        // Color accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(color);
        accentBar.setPreferredSize(new Dimension(0, 4));

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 32));
        valueLabel.setForeground(Constants.TEXT_PRIMARY);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout(0, 5));
        textPanel.setOpaque(false);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        textPanel.add(titleLabel, BorderLayout.SOUTH);

        card.add(accentBar, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Creates the quick actions panel with commonly used actions.
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = createSection("Quick Actions");

        JPanel actionsGrid = new JPanel(new GridLayout(3, 2, Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        actionsGrid.setOpaque(false);

        // Create action buttons for common tasks
        actionsGrid.add(createActionButton("New Employee", "Add a new employee"));
        actionsGrid.add(createActionButton("Create Order", "Create a new order"));
        actionsGrid.add(createActionButton("View Reports", "Access system reports"));
        actionsGrid.add(createActionButton("Manage Inventory", "Check inventory status"));
        actionsGrid.add(createActionButton("Customer Support", "View support tickets"));
        actionsGrid.add(createActionButton("Settings", "System settings"));

        panel.add(actionsGrid, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates an action button for the quick actions section.
     *
     * @param title The button title
     * @param description The button description tooltip
     * @return A styled action button
     */
    private JButton createActionButton(String title, String description) {
        JButton button = new JButton(title);
        button.setFont(Constants.FONT_REGULAR);
        button.setToolTipText(description);
        button.setBackground(Constants.BG_WHITE);
        button.setForeground(Constants.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Constants.PRIMARY_COLOR, 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.PRIMARY_LIGHT);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Constants.BG_WHITE);
            }
        });

        // Click handler (placeholder)
        button.addActionListener(e -> {
            UIHelper.showSuccess(this, title + " - This feature will be implemented in upcoming batches.");
        });

        return button;
    }

    /**
     * Override refresh to update the date/time.
     */
    @Override
    public void refreshData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy - HH:mm");
        dateTimeLabel.setText(LocalDateTime.now().format(formatter));
    }
}
