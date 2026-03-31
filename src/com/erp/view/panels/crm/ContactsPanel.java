package com.erp.view.panels.crm;

import com.erp.model.Contact;
import com.erp.model.Customer;
import com.erp.service.mock.MockCRMService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ContactsPanel displays and manages customer contacts.
 */
public class ContactsPanel extends JPanel {

    private MockCRMService crmService;

    private JTable contactsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> customerFilter;
    private JTextField searchField;

    private JButton editButton;
    private JButton deleteButton;

    private static final String[] COLUMNS = {"ID", "Name", "Title", "Company", "Email", "Phone", "Primary", "Active"};
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ContactsPanel() {
        crmService = MockCRMService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Customer filter
        customerFilter = new JComboBox<>();
        customerFilter.setFont(Constants.FONT_REGULAR);
        customerFilter.addItem("All Customers");
        for (Customer c : crmService.getAllCustomers()) {
            customerFilter.addItem(c.getCustomerId() + " - " + c.getCompanyName());
        }
        customerFilter.addActionListener(e -> loadData());

        // Search field
        searchField = UIHelper.createTextField(15);
        searchField.setToolTipText("Search by name or email");
        searchField.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contactsTable = new JTable(tableModel);
        contactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(contactsTable);

        // Column widths
        contactsTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        contactsTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Name
        contactsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Title
        contactsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Company
        contactsTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Email
        contactsTable.getColumnModel().getColumn(5).setPreferredWidth(110); // Phone
        contactsTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Primary
        contactsTable.getColumnModel().getColumn(7).setPreferredWidth(70);  // Active

        // Primary column renderer
        contactsTable.getColumnModel().getColumn(6).setCellRenderer(new BooleanCellRenderer());
        contactsTable.getColumnModel().getColumn(7).setCellRenderer(new BooleanCellRenderer());

        contactsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Buttons
        editButton = UIHelper.createSecondaryButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editContact());

        deleteButton = UIHelper.createSecondaryButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.setPreferredSize(new Dimension(90, 30));
        deleteButton.addActionListener(e -> deleteContact());
    }

    private void layoutComponents() {
        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(contactsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        // Filters row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Customer:"));
        filterPanel.add(customerFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> loadData());
        filterPanel.add(searchBtn);

        // Actions row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));

        JButton addBtn = UIHelper.createPrimaryButton("Add Contact");
        addBtn.setPreferredSize(new Dimension(110, 30));
        addBtn.addActionListener(e -> addContact());
        actionPanel.add(addBtn);

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        JButton refreshBtn = UIHelper.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> loadData());
        actionPanel.add(refreshBtn);

        toolbar.add(filterPanel, BorderLayout.NORTH);
        toolbar.add(actionPanel, BorderLayout.SOUTH);

        return toolbar;
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Contact> contacts = crmService.getAllContacts();
        String customerSelection = (String) customerFilter.getSelectedItem();
        String searchTerm = searchField.getText().toLowerCase().trim();

        for (Contact c : contacts) {
            // Filter by customer
            if (!"All Customers".equals(customerSelection)) {
                int custId = Integer.parseInt(customerSelection.split(" - ")[0]);
                if (c.getCustomerId() != custId) continue;
            }

            // Filter by search
            if (!searchTerm.isEmpty()) {
                boolean matches = c.getFullName().toLowerCase().contains(searchTerm) ||
                                  c.getEmail().toLowerCase().contains(searchTerm);
                if (!matches) continue;
            }

            // Get customer name
            String companyName = "";
            Customer customer = crmService.getCustomerById(c.getCustomerId());
            if (customer != null) {
                companyName = customer.getCompanyName();
            }

            tableModel.addRow(new Object[]{
                c.getContactId(),
                c.getFullName(),
                c.getTitle() != null ? c.getTitle() : "",
                companyName,
                c.getEmail(),
                c.getPhone(),
                c.isPrimary() ? "Yes" : "No",
                c.isActive() ? "Yes" : "No"
            });
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        int row = contactsTable.getSelectedRow();
        boolean hasSelection = row >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    private void addContact() {
        showContactDialog(null, "Add New Contact");
    }

    private void editContact() {
        int row = contactsTable.getSelectedRow();
        if (row < 0) return;

        int contactId = (int) tableModel.getValueAt(row, 0);
        Contact contact = crmService.getContactById(contactId);

        if (contact != null) {
            showContactDialog(contact, "Edit Contact");
        }
    }

    private void deleteContact() {
        int row = contactsTable.getSelectedRow();
        if (row < 0) return;

        int contactId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete contact '" + name + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (crmService.deleteContact(contactId)) {
                UIHelper.showSuccess(this, "Contact deleted successfully.");
                loadData();
            } else {
                UIHelper.showError(this, "Failed to delete contact.");
            }
        }
    }

    private void showContactDialog(Contact existing, String title) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField firstNameField = new JTextField(existing != null ? existing.getFirstName() : "");
        JTextField lastNameField = new JTextField(existing != null ? existing.getLastName() : "");
        JTextField titleField = new JTextField(existing != null && existing.getTitle() != null ? existing.getTitle() : "");
        JTextField emailField = new JTextField(existing != null ? existing.getEmail() : "");
        JTextField phoneField = new JTextField(existing != null && existing.getPhone() != null ? existing.getPhone() : "");
        JTextField mobileField = new JTextField(existing != null && existing.getMobilePhone() != null ? existing.getMobilePhone() : "");

        // Customer combo
        JComboBox<String> customerCombo = new JComboBox<>();
        customerCombo.addItem("-- Select Customer --");
        for (Customer c : crmService.getAllCustomers()) {
            customerCombo.addItem(c.getCustomerId() + " - " + c.getCompanyName());
        }
        if (existing != null && existing.getCustomerId() > 0) {
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                String item = customerCombo.getItemAt(i);
                if (item.startsWith(existing.getCustomerId() + " - ")) {
                    customerCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        JCheckBox primaryCheck = new JCheckBox("Primary Contact");
        primaryCheck.setSelected(existing != null && existing.isPrimary());

        JCheckBox activeCheck = new JCheckBox("Active");
        activeCheck.setSelected(existing == null || existing.isActive());

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerCombo);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Mobile:"));
        formPanel.add(mobileField);
        formPanel.add(new JLabel(""));
        formPanel.add(primaryCheck);
        formPanel.add(new JLabel(""));
        formPanel.add(activeCheck);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = UIHelper.createPrimaryButton("Save");
        saveBtn.addActionListener(e -> {
            // Validation
            if (firstNameField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "First name is required.");
                return;
            }
            if (lastNameField.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Last name is required.");
                return;
            }
            if (customerCombo.getSelectedIndex() == 0) {
                UIHelper.showError(dialog, "Please select a customer.");
                return;
            }

            // Parse customer ID
            String custSelection = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(custSelection.split(" - ")[0]);

            if (existing == null) {
                Contact newContact = new Contact();
                newContact.setFirstName(firstNameField.getText().trim());
                newContact.setLastName(lastNameField.getText().trim());
                newContact.setTitle(titleField.getText().trim());
                newContact.setCustomerId(customerId);
                newContact.setEmail(emailField.getText().trim());
                newContact.setPhone(phoneField.getText().trim());
                newContact.setMobilePhone(mobileField.getText().trim());
                newContact.setPrimary(primaryCheck.isSelected());
                newContact.setActive(activeCheck.isSelected());
                crmService.createContact(newContact);
            } else {
                existing.setFirstName(firstNameField.getText().trim());
                existing.setLastName(lastNameField.getText().trim());
                existing.setTitle(titleField.getText().trim());
                existing.setCustomerId(customerId);
                existing.setEmail(emailField.getText().trim());
                existing.setPhone(phoneField.getText().trim());
                existing.setMobilePhone(mobileField.getText().trim());
                existing.setPrimary(primaryCheck.isSelected());
                existing.setActive(activeCheck.isSelected());
                crmService.updateContact(existing);
            }

            UIHelper.showSuccess(dialog, "Contact saved successfully.");
            dialog.dispose();
            loadData();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        loadData();
    }

    // Boolean cell renderer
    private static class BooleanCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);
            if (value != null && "Yes".equals(value.toString())) {
                if (!isSelected) {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                }
            } else {
                if (!isSelected) {
                    setBackground(table.getBackground());
                    setForeground(Constants.TEXT_SECONDARY);
                }
            }
            return this;
        }
    }
}
