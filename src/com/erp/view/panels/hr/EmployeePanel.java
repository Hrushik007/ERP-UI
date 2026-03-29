package com.erp.view.panels.hr;

import com.erp.model.Employee;
import com.erp.service.mock.MockHRService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;
import com.erp.view.dialogs.EmployeeDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * EmployeePanel displays and manages employee data.
 *
 * This demonstrates:
 * 1. JTable - A component that displays data in a two-dimensional table
 * 2. DefaultTableModel - The data model that JTable uses
 * 3. TableRowSorter - Enables sorting and filtering of table rows
 * 4. DELEGATION - UI delegates data operations to the service layer
 *
 * Key Swing Concepts:
 * - Table Models separate data from display (MVC in action)
 * - Row Sorter handles sorting without modifying underlying data
 * - Selection listeners respond to user row selections
 */
public class EmployeePanel extends JPanel {

    // Service reference
    private MockHRService hrService;

    // Table components
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Toolbar components
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    // Column definitions
    private static final String[] COLUMNS = {
        "ID", "First Name", "Last Name", "Department", "Position", "Email", "Status"
    };

    public EmployeePanel() {
        hrService = MockHRService.getInstance();
        setLayout(new BorderLayout(0, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Create table model (non-editable cells)
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct cell editing - use dialog instead
            }
        };

        // Create table
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(Constants.FONT_REGULAR);
        employeeTable.setRowHeight(30);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setFont(Constants.FONT_REGULAR);
        employeeTable.getTableHeader().setBackground(Constants.PRIMARY_COLOR);
        employeeTable.getTableHeader().setForeground(Constants.TEXT_LIGHT);
        employeeTable.setGridColor(new Color(230, 230, 230));
        employeeTable.setShowGrid(true);

        // Set column widths
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(100); // First Name
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Last Name
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Department
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Position
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(180); // Email
        employeeTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status

        // Enable sorting
        rowSorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(rowSorter);

        // Double-click to edit
        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedEmployee();
                }
            }
        });

        // Selection listener to enable/disable buttons
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = employeeTable.getSelectedRow() >= 0;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });

        // Search field
        searchField = UIHelper.createTextField(20);
        searchField.setToolTipText("Search by name, email, or department");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        // Department filter
        departmentFilter = new JComboBox<>();
        departmentFilter.setFont(Constants.FONT_REGULAR);
        departmentFilter.addItem("All Departments");
        hrService.getAllDepartments().forEach(departmentFilter::addItem);
        departmentFilter.addActionListener(e -> filterTable());

        // Action buttons
        addButton = UIHelper.createPrimaryButton("Add Employee");
        addButton.addActionListener(e -> addEmployee());

        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editSelectedEmployee());

        deleteButton = new JButton("Delete");
        deleteButton.setFont(Constants.FONT_BUTTON);
        deleteButton.setForeground(Constants.DANGER_COLOR);
        deleteButton.setBackground(Constants.BG_WHITE);
        deleteButton.setBorder(BorderFactory.createLineBorder(Constants.DANGER_COLOR, 1));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

        refreshButton = UIHelper.createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadData());
    }

    private void layoutComponents() {
        // Top toolbar
        JPanel toolbar = new JPanel(new BorderLayout(Constants.PADDING_MEDIUM, 0));
        toolbar.setOpaque(false);

        // Left side - search and filter
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(Constants.FONT_REGULAR);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        searchPanel.add(new JLabel("Department:"));
        searchPanel.add(departmentFilter);

        // Right side - action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.PADDING_SMALL, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        // Table in scroll pane
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Status bar
        JPanel statusBar = createStatusBar();

        // Add to panel
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setOpaque(false);

        JLabel countLabel = new JLabel("Total: " + hrService.getAllEmployees().size() + " employees");
        countLabel.setFont(Constants.FONT_SMALL);
        countLabel.setForeground(Constants.TEXT_SECONDARY);
        statusBar.add(countLabel);

        return statusBar;
    }

    /**
     * Loads employee data into the table.
     */
    public void loadData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get employees from service
        List<Employee> employees = hrService.getAllEmployees();

        // Add each employee as a row
        for (Employee emp : employees) {
            Object[] row = {
                emp.getEmployeeId(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getDepartment(),
                emp.getPosition(),
                emp.getEmail(),
                emp.getEmploymentStatus()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Filters the table based on search text and department selection.
     */
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        String selectedDept = (String) departmentFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // Department filter
                if (!"All Departments".equals(selectedDept)) {
                    String dept = entry.getStringValue(3); // Department column
                    if (!selectedDept.equals(dept)) {
                        return false;
                    }
                }

                // Search filter
                if (!searchText.isEmpty()) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).toLowerCase().contains(searchText)) {
                            return true;
                        }
                    }
                    return false;
                }

                return true;
            }
        };

        rowSorter.setRowFilter(filter);
    }

    /**
     * Opens dialog to add a new employee.
     */
    private void addEmployee() {
        Window window = SwingUtilities.getWindowAncestor(this);
        EmployeeDialog dialog = new EmployeeDialog((Frame) window, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Employee newEmployee = dialog.getEmployee();
            hrService.createEmployee(newEmployee);
            loadData(); // Refresh table
            UIHelper.showSuccess(this, "Employee added successfully!");
        }
    }

    /**
     * Opens dialog to edit the selected employee.
     */
    private void editSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showError(this, "Please select an employee to edit.");
            return;
        }

        // Convert view row to model row (important when sorted/filtered)
        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        int employeeId = (int) tableModel.getValueAt(modelRow, 0);

        Employee employee = hrService.getEmployeeById(employeeId);
        if (employee == null) {
            UIHelper.showError(this, "Employee not found.");
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);
        EmployeeDialog dialog = new EmployeeDialog((Frame) window, employee);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Employee updatedEmployee = dialog.getEmployee();
            hrService.updateEmployee(updatedEmployee);
            loadData(); // Refresh table
            UIHelper.showSuccess(this, "Employee updated successfully!");
        }
    }

    /**
     * Deletes the selected employee after confirmation.
     */
    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            UIHelper.showError(this, "Please select an employee to delete.");
            return;
        }

        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        int employeeId = (int) tableModel.getValueAt(modelRow, 0);
        String name = tableModel.getValueAt(modelRow, 1) + " " + tableModel.getValueAt(modelRow, 2);

        boolean confirm = UIHelper.showConfirm(this,
            "Are you sure you want to delete employee: " + name + "?");

        if (confirm) {
            hrService.deleteEmployee(employeeId);
            loadData(); // Refresh table
            UIHelper.showSuccess(this, "Employee deleted successfully!");
        }
    }

    /**
     * Refreshes the data in this panel.
     */
    public void refreshData() {
        loadData();
    }
}
