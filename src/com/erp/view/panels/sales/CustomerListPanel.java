package com.erp.view.panels.sales;

import com.erp.model.Customer;
import com.erp.service.mock.MockSalesService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * CustomerListPanel displays customer directory for quick reference during sales.
 *
 * This is a read-only view for sales staff to look up customer information.
 */
public class CustomerListPanel extends JPanel {

    private MockSalesService salesService;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // Customer details
    private JLabel detailName;
    private JLabel detailContact;
    private JLabel detailEmail;
    private JLabel detailPhone;
    private JLabel detailType;
    private JLabel detailAddress;

    private static final String[] COLUMNS = {"ID", "Company", "Contact", "Email", "Type"};

    public CustomerListPanel() {
        salesService = MockSalesService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        searchField = UIHelper.createTextField(20);
        searchField.setToolTipText("Search by company name, contact, or email");
        searchField.addActionListener(e -> loadData());

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(customerTable);

        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showCustomerDetails();
            }
        });

        // Detail labels
        detailName = new JLabel("-");
        detailContact = new JLabel("-");
        detailEmail = new JLabel("-");
        detailPhone = new JLabel("-");
        detailType = new JLabel("-");
        detailAddress = new JLabel("-");
    }

    private void layoutComponents() {
        // Top toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Search:"));
        toolbar.add(searchField);
        JButton searchBtn = UIHelper.createSecondaryButton("Search");
        searchBtn.setPreferredSize(new Dimension(80, 30));
        searchBtn.addActionListener(e -> loadData());
        toolbar.add(searchBtn);

        // Table
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Details panel
        JPanel detailsPanel = createDetailsPanel();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, detailsPanel);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.7);

        add(toolbar, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Constants.BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                          Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)
        ));

        JLabel title = new JLabel("Customer Details");
        title.setFont(Constants.FONT_SUBTITLE);

        JPanel infoGrid = new JPanel(new GridLayout(6, 2, 10, 10));
        infoGrid.setOpaque(false);
        infoGrid.setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, 0, 0, 0));

        infoGrid.add(createLabel("Company:"));
        infoGrid.add(detailName);
        infoGrid.add(createLabel("Contact:"));
        infoGrid.add(detailContact);
        infoGrid.add(createLabel("Email:"));
        infoGrid.add(detailEmail);
        infoGrid.add(createLabel("Phone:"));
        infoGrid.add(detailPhone);
        infoGrid.add(createLabel("Type:"));
        infoGrid.add(detailType);
        infoGrid.add(createLabel("Address:"));
        infoGrid.add(detailAddress);

        panel.add(title, BorderLayout.NORTH);
        panel.add(infoGrid, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Constants.FONT_REGULAR);
        label.setForeground(Constants.TEXT_SECONDARY);
        return label;
    }

    private void loadData() {
        tableModel.setRowCount(0);

        String searchText = searchField.getText().trim();
        List<Customer> customers;

        if (searchText.isEmpty()) {
            customers = salesService.getAllCustomers();
        } else {
            customers = salesService.searchCustomers(searchText);
        }

        for (Customer c : customers) {
            Object[] row = {
                c.getCustomerId(),
                c.getCompanyName(),
                c.getContactName(),
                c.getEmail(),
                c.getCustomerType()
            };
            tableModel.addRow(row);
        }

        clearDetails();
    }

    private void showCustomerDetails() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            clearDetails();
            return;
        }

        int customerId = (int) tableModel.getValueAt(row, 0);
        Customer customer = salesService.getCustomerById(customerId);

        if (customer != null) {
            detailName.setText(customer.getCompanyName());
            detailName.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 14));
            detailContact.setText(customer.getContactName());
            detailEmail.setText(customer.getEmail());
            detailPhone.setText(customer.getPhone() != null ? customer.getPhone() : "-");
            detailType.setText(customer.getCustomerType());
            detailAddress.setText(customer.getFullAddress());
        }
    }

    private void clearDetails() {
        detailName.setText("-");
        detailContact.setText("-");
        detailEmail.setText("-");
        detailPhone.setText("-");
        detailType.setText("-");
        detailAddress.setText("-");
    }

    public void refreshData() {
        loadData();
    }
}
