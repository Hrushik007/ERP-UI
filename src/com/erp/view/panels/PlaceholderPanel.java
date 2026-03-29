package com.erp.view.panels;

import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * PlaceholderPanel is used for modules that are not yet implemented.
 *
 * This demonstrates POLYMORPHISM:
 * - PlaceholderPanel IS-A BasePanel (inheritance)
 * - It can be used anywhere a BasePanel is expected
 * - The MainFrame doesn't care if it's a DashboardPanel or PlaceholderPanel
 *   - it just works with BasePanel references
 *
 * When we implement actual modules (HR, CRM, etc.), they will also extend
 * BasePanel, and we can swap them in without changing MainFrame code.
 * This is the power of polymorphism - we program to interfaces/abstractions,
 * not concrete implementations.
 */
public class PlaceholderPanel extends BasePanel {

    private String moduleName;
    private JLabel iconLabel;
    private JLabel messageLabel;
    private JButton learnMoreButton;

    /**
     * Constructor creates a placeholder for a specific module.
     *
     * @param moduleName The name of the module being placeholded
     */
    public PlaceholderPanel(String moduleName) {
        super(moduleName);  // Call parent constructor
        this.moduleName = moduleName;
    }

    /**
     * Initialize placeholder components.
     * Required by abstract parent class.
     */
    @Override
    protected void initializeComponents() {
        // Icon/illustration placeholder
        iconLabel = new JLabel("\uD83D\uDEA7");  // Construction emoji
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Message
        messageLabel = new JLabel("<html><center>" +
            "<h2>Coming Soon</h2>" +
            "<p style='color: #7f8c8d; font-size: 14px;'>" +
            "The <b>" + moduleName + "</b> module is under development.<br>" +
            "This will be implemented in upcoming batches.</p>" +
            "</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(Constants.FONT_REGULAR);

        // Button
        learnMoreButton = UIHelper.createSecondaryButton("View Features");
        learnMoreButton.addActionListener(e -> showModuleFeatures());
    }

    /**
     * Layout the placeholder components centered in the panel.
     * Required by abstract parent class.
     */
    @Override
    protected void layoutComponents() {
        contentPanel.setLayout(new GridBagLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Center-align all components
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        learnMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(Constants.PADDING_LARGE));
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalStrut(Constants.PADDING_LARGE));
        centerPanel.add(learnMoreButton);
        centerPanel.add(Box.createVerticalGlue());

        contentPanel.add(centerPanel);
    }

    /**
     * Shows information about what features this module will have.
     * This is placeholder functionality for demonstration.
     */
    private void showModuleFeatures() {
        String features = getModuleDescription();
        JOptionPane.showMessageDialog(this,
            "<html><body style='width: 300px'>" + features + "</body></html>",
            moduleName + " - Planned Features",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Returns description of module features based on the module name.
     * This demonstrates a simple form of POLYMORPHIC behavior through data.
     */
    private String getModuleDescription() {
        switch (moduleName) {
            case Constants.MODULE_CRM:
                return "<h3>CRM Module Features:</h3>" +
                       "<ul><li>Customer Data Management</li>" +
                       "<li>Sales Force Automation</li>" +
                       "<li>Marketing Automation</li>" +
                       "<li>Customer Service & Support</li>" +
                       "<li>Analytics & Reporting</li></ul>";

            case Constants.MODULE_HR:
                return "<h3>HR Management Features:</h3>" +
                       "<ul><li>Employee Information Management</li>" +
                       "<li>Recruitment & Onboarding</li>" +
                       "<li>Attendance & Leave Management</li>" +
                       "<li>Payroll Processing</li>" +
                       "<li>Performance Management</li></ul>";

            case Constants.MODULE_FINANCE:
                return "<h3>Financial Management Features:</h3>" +
                       "<ul><li>General Ledger</li>" +
                       "<li>Accounts Payable/Receivable</li>" +
                       "<li>Cash Management</li>" +
                       "<li>Budgeting & Forecasting</li>" +
                       "<li>Financial Reporting</li></ul>";

            case Constants.MODULE_SALES:
                return "<h3>Sales Management Features:</h3>" +
                       "<ul><li>Lead Management</li>" +
                       "<li>Quotation Generation</li>" +
                       "<li>Order Management</li>" +
                       "<li>Pricing Management</li>" +
                       "<li>Sales Forecasting</li></ul>";

            case Constants.MODULE_INVENTORY:
                return "<h3>Supply Chain Features:</h3>" +
                       "<ul><li>Procurement Management</li>" +
                       "<li>Vendor Management</li>" +
                       "<li>Inventory Control</li>" +
                       "<li>Logistics & Distribution</li></ul>";

            case Constants.MODULE_MANUFACTURING:
                return "<h3>Manufacturing Features:</h3>" +
                       "<ul><li>Production Planning</li>" +
                       "<li>Bill of Materials (BOM)</li>" +
                       "<li>Shop Floor Control</li>" +
                       "<li>Quality Management</li></ul>";

            case Constants.MODULE_PROJECT:
                return "<h3>Project Management Features:</h3>" +
                       "<ul><li>Project Planning</li>" +
                       "<li>Task Management</li>" +
                       "<li>Resource Allocation</li>" +
                       "<li>Time Tracking</li>" +
                       "<li>Budget Management</li></ul>";

            case Constants.MODULE_REPORTING:
                return "<h3>Reporting Features:</h3>" +
                       "<ul><li>Standard Reports</li>" +
                       "<li>Custom Report Builder</li>" +
                       "<li>Interactive Dashboards</li>" +
                       "<li>Scheduled Reports</li></ul>";

            case Constants.MODULE_ANALYTICS:
                return "<h3>Data Analytics Features:</h3>" +
                       "<ul><li>Data Integration</li>" +
                       "<li>Descriptive Analytics</li>" +
                       "<li>Trend Analysis</li>" +
                       "<li>KPI Tracking</li></ul>";

            case Constants.MODULE_BI:
                return "<h3>Business Intelligence Features:</h3>" +
                       "<ul><li>Data Warehousing</li>" +
                       "<li>OLAP Cubes</li>" +
                       "<li>Predictive Analytics</li>" +
                       "<li>Executive Dashboards</li></ul>";

            case Constants.MODULE_ACCOUNTING:
                return "<h3>Accounting Features:</h3>" +
                       "<ul><li>General Ledger</li>" +
                       "<li>Journal Entries</li>" +
                       "<li>Bank Reconciliation</li>" +
                       "<li>Tax Compliance</li></ul>";

            case Constants.MODULE_MARKETING:
                return "<h3>Marketing Features:</h3>" +
                       "<ul><li>Campaign Management</li>" +
                       "<li>Customer Segmentation</li>" +
                       "<li>Lead Generation</li>" +
                       "<li>Marketing Analytics</li></ul>";

            case Constants.MODULE_ORDER:
                return "<h3>Order Processing Features:</h3>" +
                       "<ul><li>Order Capture</li>" +
                       "<li>Order Validation</li>" +
                       "<li>Fulfillment Tracking</li>" +
                       "<li>Returns Management</li></ul>";

            case Constants.MODULE_AUTOMATION:
                return "<h3>Automation Features:</h3>" +
                       "<ul><li>Workflow Automation</li>" +
                       "<li>Document Management</li>" +
                       "<li>Automated Notifications</li>" +
                       "<li>Scheduled Tasks</li></ul>";

            case Constants.MODULE_INTEGRATION:
                return "<h3>Integration Features:</h3>" +
                       "<ul><li>Inter-Module Connectivity</li>" +
                       "<li>API Integration</li>" +
                       "<li>Data Synchronization</li>" +
                       "<li>Third-Party Connectors</li></ul>";

            default:
                return "<h3>Module Features:</h3>" +
                       "<p>Feature details will be available soon.</p>";
        }
    }
}
