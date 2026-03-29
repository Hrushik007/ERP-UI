package com.erp.view;

import com.erp.service.AuthenticationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * LoginFrame is the entry point window for the application.
 *
 * This demonstrates:
 *
 * 1. COMPOSITION with JFrame - LoginFrame HAS-A JFrame (extends it).
 *
 * 2. EVENT HANDLING - Uses ActionListener for button clicks and
 *    KeyAdapter for keyboard events (Enter key to submit).
 *
 * 3. DELEGATION - Delegates authentication to AuthenticationService.
 *    The UI doesn't know HOW authentication works - it just asks the service.
 *
 * 4. SEPARATION OF CONCERNS - UI logic here, business logic in service layer.
 */
public class LoginFrame extends JFrame {

    // Form components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    // Reference to the service layer
    private AuthenticationService authService;

    /**
     * Constructor sets up the login window.
     */
    public LoginFrame() {
        // Get reference to authentication service (Singleton)
        authService = AuthenticationService.getInstance();

        // Configure the window
        setupFrame();

        // Create and layout components
        initializeComponents();
    }

    /**
     * Configures the JFrame properties.
     */
    private void setupFrame() {
        setTitle(Constants.APP_NAME + " - Login");
        setSize(Constants.LOGIN_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Center on screen
        UIHelper.centerWindow(this);
    }

    /**
     * Creates and arranges all UI components.
     */
    private void initializeComponents() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Constants.BG_WHITE);

        // Header section
        JPanel headerPanel = createHeaderPanel();

        // Form section
        JPanel formPanel = createFormPanel();

        // Footer section
        JPanel footerPanel = createFooterPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Creates the header panel with logo/branding.
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Constants.PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(Constants.PADDING_XLARGE, Constants.PADDING_XLARGE,
                                        Constants.PADDING_XLARGE, Constants.PADDING_XLARGE));

        // App name
        JLabel titleLabel = new JLabel("ERP System");
        titleLabel.setFont(Constants.FONT_TITLE);
        titleLabel.setForeground(Constants.TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Enterprise Resource Planning");
        subtitleLabel.setFont(Constants.FONT_REGULAR);
        subtitleLabel.setForeground(new Color(200, 220, 240));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);

        return panel;
    }

    /**
     * Creates the form panel with username/password fields.
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(new EmptyBorder(Constants.PADDING_XLARGE, Constants.PADDING_XLARGE,
                                        Constants.PADDING_MEDIUM, Constants.PADDING_XLARGE));

        // Login title
        JLabel loginTitle = new JLabel("Sign In");
        loginTitle.setFont(Constants.FONT_SUBTITLE);
        loginTitle.setForeground(Constants.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username field
        JLabel usernameLabel = UIHelper.createLabel("Username", Constants.FONT_REGULAR, Constants.TEXT_PRIMARY);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = UIHelper.createTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 40));

        // Password field
        JLabel passwordLabel = UIHelper.createLabel("Password", Constants.FONT_REGULAR, Constants.TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = UIHelper.createPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 40));

        // Error label (initially hidden)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Constants.FONT_SMALL);
        errorLabel.setForeground(Constants.DANGER_COLOR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        loginButton = UIHelper.createPrimaryButton("Login");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add action listeners
        setupEventListeners();

        // Add components
        panel.add(loginTitle);
        panel.add(Box.createVerticalStrut(Constants.PADDING_LARGE));
        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(Constants.PADDING_SMALL));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(Constants.PADDING_MEDIUM));
        panel.add(loginButton);

        return panel;
    }

    /**
     * Creates the footer panel with version info.
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Constants.BG_LIGHT);
        panel.setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, 0, Constants.PADDING_MEDIUM, 0));

        JLabel versionLabel = new JLabel("Version " + Constants.APP_VERSION);
        versionLabel.setFont(Constants.FONT_SMALL);
        versionLabel.setForeground(Constants.TEXT_SECONDARY);

        JLabel credentialsHint = new JLabel("Hint: admin/admin123, manager/manager123, employee/emp123");
        credentialsHint.setFont(Constants.FONT_SMALL);
        credentialsHint.setForeground(Constants.TEXT_SECONDARY);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        credentialsHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(credentialsHint);
        panel.add(Box.createVerticalStrut(5));
        panel.add(versionLabel);

        return panel;
    }

    /**
     * Sets up event listeners for form interaction.
     *
     * This demonstrates the Observer pattern through event listeners:
     * - The button/field (subject) doesn't know what happens on click
     * - The listener (observer) handles the event
     */
    private void setupEventListeners() {
        // Login button click
        loginButton.addActionListener(this::handleLogin);

        // Enter key in password field triggers login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin(null);
                }
            }
        });

        // Enter key in username field moves to password
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
    }

    /**
     * Handles the login action.
     *
     * This method:
     * 1. Validates input
     * 2. Delegates to AuthenticationService
     * 3. Handles success/failure
     *
     * @param e The action event (can be null if called from key listener)
     */
    private void handleLogin(ActionEvent e) {
        // Clear previous error
        errorLabel.setText(" ");

        // Get input values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (username.isEmpty()) {
            showError("Please enter your username");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            passwordField.requestFocus();
            return;
        }

        // Attempt authentication - delegate to service
        boolean success = authService.authenticate(username, password);

        if (success) {
            // Login successful - open main application
            openMainApplication();
        } else {
            // Login failed - show error
            showError("Invalid username or password");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    /**
     * Shows an error message in the form.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /**
     * Opens the main application window after successful login.
     */
    private void openMainApplication() {
        // Create and show main frame
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // Close login window
        this.dispose();
    }
}
