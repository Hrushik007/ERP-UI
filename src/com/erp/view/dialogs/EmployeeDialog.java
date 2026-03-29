package com.erp.view.dialogs;

import com.erp.model.Employee;
import com.erp.service.mock.MockHRService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * EmployeeDialog is a modal dialog for adding/editing employee information.
 *
 * This demonstrates:
 * 1. JDialog - A modal window that blocks input to other windows
 * 2. FORM VALIDATION - Checking input before saving
 * 3. GridBagLayout - A flexible grid-based layout manager
 * 4. EDIT vs ADD mode - Same dialog used for both operations
 *
 * Key Points:
 * - Modal dialogs block the parent window until closed
 * - GridBagLayout constraints control component positioning and sizing
 * - Validation provides user feedback before accepting data
 */
public class EmployeeDialog extends JDialog {

    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> departmentCombo;
    private JTextField positionField;
    private JTextField salaryField;
    private JTextField hireDateField;
    private JComboBox<String> statusCombo;
    private JTextArea addressArea;

    // Action buttons
    private JButton saveButton;
    private JButton cancelButton;

    // Data
    private Employee employee;
    private boolean isEditMode;
    private boolean saved = false;

    // Date format for display
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Creates a new EmployeeDialog.
     *
     * @param parent The parent frame
     * @param employee The employee to edit, or null for new employee
     */
    public EmployeeDialog(Frame parent, Employee employee) {
        super(parent, true); // true = modal
        this.employee = employee;
        this.isEditMode = (employee != null);

        setTitle(isEditMode ? "Edit Employee" : "Add New Employee");
        setSize(500, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeComponents();
        layoutComponents();

        if (isEditMode) {
            populateFields();
        }
    }

    private void initializeComponents() {
        // Text fields
        firstNameField = UIHelper.createTextField(20);
        lastNameField = UIHelper.createTextField(20);
        emailField = UIHelper.createTextField(20);
        phoneField = UIHelper.createTextField(20);
        positionField = UIHelper.createTextField(20);
        salaryField = UIHelper.createTextField(20);
        hireDateField = UIHelper.createTextField(20);
        hireDateField.setToolTipText("Format: YYYY-MM-DD");

        // Department combo
        departmentCombo = new JComboBox<>();
        departmentCombo.setFont(Constants.FONT_REGULAR);
        departmentCombo.addItem("IT");
        departmentCombo.addItem("HR");
        departmentCombo.addItem("Finance");
        departmentCombo.addItem("Sales");
        departmentCombo.addItem("Marketing");
        departmentCombo.addItem("Operations");
        departmentCombo.addItem("Administration");
        // Add any existing departments from database
        for (String dept : MockHRService.getInstance().getAllDepartments()) {
            boolean exists = false;
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                if (departmentCombo.getItemAt(i).equals(dept)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                departmentCombo.addItem(dept);
            }
        }
        departmentCombo.setEditable(true); // Allow custom departments

        // Status combo
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "ON_LEAVE", "TERMINATED"});
        statusCombo.setFont(Constants.FONT_REGULAR);

        // Address area
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(Constants.FONT_REGULAR);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        // Buttons
        saveButton = UIHelper.createPrimaryButton("Save");
        saveButton.addActionListener(e -> saveEmployee());

        cancelButton = UIHelper.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        // Default hire date to today for new employees
        if (!isEditMode) {
            hireDateField.setText(LocalDate.now().format(DATE_FORMAT));
        }
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
                                            Constants.PADDING_LARGE, Constants.PADDING_LARGE));
        mainPanel.setBackground(Constants.BG_WHITE);

        // Form panel using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.EAST;
        labelConstraints.insets = new Insets(5, 5, 5, 10);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 0, 5, 5);

        int row = 0;

        // First Name (required)
        addFormRow(formPanel, "First Name *:", firstNameField, row++, labelConstraints, fieldConstraints);

        // Last Name (required)
        addFormRow(formPanel, "Last Name *:", lastNameField, row++, labelConstraints, fieldConstraints);

        // Email (required)
        addFormRow(formPanel, "Email *:", emailField, row++, labelConstraints, fieldConstraints);

        // Phone
        addFormRow(formPanel, "Phone:", phoneField, row++, labelConstraints, fieldConstraints);

        // Department (required)
        addFormRow(formPanel, "Department *:", departmentCombo, row++, labelConstraints, fieldConstraints);

        // Position (required)
        addFormRow(formPanel, "Position *:", positionField, row++, labelConstraints, fieldConstraints);

        // Salary
        addFormRow(formPanel, "Salary:", salaryField, row++, labelConstraints, fieldConstraints);

        // Hire Date
        addFormRow(formPanel, "Hire Date:", hireDateField, row++, labelConstraints, fieldConstraints);

        // Status
        addFormRow(formPanel, "Status:", statusCombo, row++, labelConstraints, fieldConstraints);

        // Address
        labelConstraints.gridy = row;
        labelConstraints.gridx = 0;
        labelConstraints.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(createLabel("Address:"), labelConstraints);

        fieldConstraints.gridy = row;
        fieldConstraints.gridx = 1;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(200, 60));
        formPanel.add(addressScroll, fieldConstraints);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.PADDING_SMALL, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Required fields note
        JLabel requiredNote = new JLabel("* Required fields");
        requiredNote.setFont(Constants.FONT_SMALL);
        requiredNote.setForeground(Constants.TEXT_SECONDARY);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(requiredNote, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Helper method to add a form row.
     */
    private void addFormRow(JPanel panel, String labelText, JComponent field,
                           int row, GridBagConstraints labelC, GridBagConstraints fieldC) {
        labelC.gridy = row;
        labelC.gridx = 0;
        panel.add(createLabel(labelText), labelC);

        fieldC.gridy = row;
        fieldC.gridx = 1;
        panel.add(field, fieldC);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        return label;
    }

    /**
     * Populates form fields with existing employee data (edit mode).
     */
    private void populateFields() {
        if (employee == null) return;

        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone() != null ? employee.getPhone() : "");
        departmentCombo.setSelectedItem(employee.getDepartment());
        positionField.setText(employee.getPosition());

        if (employee.getSalary() != null) {
            salaryField.setText(employee.getSalary().toString());
        }

        if (employee.getHireDate() != null) {
            hireDateField.setText(employee.getHireDate().format(DATE_FORMAT));
        }

        statusCombo.setSelectedItem(employee.getEmploymentStatus());

        if (employee.getAddress() != null) {
            addressArea.setText(employee.getAddress());
        }
    }

    /**
     * Validates and saves the employee.
     */
    private void saveEmployee() {
        // Validate required fields
        if (!validateForm()) {
            return;
        }

        // Create or update employee object
        if (employee == null) {
            employee = new Employee();
        }

        employee.setFirstName(firstNameField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setDepartment((String) departmentCombo.getSelectedItem());
        employee.setPosition(positionField.getText().trim());
        employee.setEmploymentStatus((String) statusCombo.getSelectedItem());
        employee.setAddress(addressArea.getText().trim());

        // Parse salary
        String salaryText = salaryField.getText().trim();
        if (!salaryText.isEmpty()) {
            try {
                employee.setSalary(new BigDecimal(salaryText));
            } catch (NumberFormatException e) {
                UIHelper.showError(this, "Invalid salary format. Please enter a number.");
                salaryField.requestFocus();
                return;
            }
        }

        // Parse hire date
        String hireDateText = hireDateField.getText().trim();
        if (!hireDateText.isEmpty()) {
            try {
                employee.setHireDate(LocalDate.parse(hireDateText, DATE_FORMAT));
            } catch (DateTimeParseException e) {
                UIHelper.showError(this, "Invalid date format. Please use YYYY-MM-DD.");
                hireDateField.requestFocus();
                return;
            }
        }

        saved = true;
        dispose();
    }

    /**
     * Validates the form fields.
     *
     * @return true if valid, false otherwise
     */
    private boolean validateForm() {
        // First name
        if (firstNameField.getText().trim().isEmpty()) {
            UIHelper.showError(this, "First name is required.");
            firstNameField.requestFocus();
            return false;
        }

        // Last name
        if (lastNameField.getText().trim().isEmpty()) {
            UIHelper.showError(this, "Last name is required.");
            lastNameField.requestFocus();
            return false;
        }

        // Email
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            UIHelper.showError(this, "Email is required.");
            emailField.requestFocus();
            return false;
        }
        if (!email.contains("@") || !email.contains(".")) {
            UIHelper.showError(this, "Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        // Department
        if (departmentCombo.getSelectedItem() == null ||
            departmentCombo.getSelectedItem().toString().trim().isEmpty()) {
            UIHelper.showError(this, "Department is required.");
            departmentCombo.requestFocus();
            return false;
        }

        // Position
        if (positionField.getText().trim().isEmpty()) {
            UIHelper.showError(this, "Position is required.");
            positionField.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Returns whether the dialog was saved (not cancelled).
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Returns the employee data from the form.
     */
    public Employee getEmployee() {
        return employee;
    }
}
