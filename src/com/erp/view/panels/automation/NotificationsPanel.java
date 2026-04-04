package com.erp.view.panels.automation;

import com.erp.service.mock.MockAutomationService;
import com.erp.util.Constants;
import com.erp.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * NotificationsPanel displays and manages notifications.
 *
 * Shows all notifications with ability to filter by type and read status,
 * and mark notifications as read.
 */
public class NotificationsPanel extends JPanel {

    private MockAutomationService automationService;

    private JTable notificationsTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> typeFilter;
    private JComboBox<String> readFilter;

    // Summary labels
    private JLabel totalLabel;
    private JLabel unreadLabel;
    private JLabel emailLabel;
    private JLabel inAppLabel;

    private JButton markReadButton;
    private JButton sendButton;

    private static final String[] COLUMNS = {"ID", "Type", "Subject", "Message", "Read", "Created"};

    public NotificationsPanel() {
        automationService = MockAutomationService.getInstance();
        setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        setBackground(Constants.BG_LIGHT);
        setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                                  Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        initializeComponents();
        layoutComponents();
        loadData();
    }

    private void initializeComponents() {
        // Type filter
        typeFilter = new JComboBox<>(new String[]{"All Types", "IN_APP", "EMAIL", "SMS"});
        typeFilter.setFont(Constants.FONT_REGULAR);
        typeFilter.addActionListener(e -> loadData());

        // Read filter
        readFilter = new JComboBox<>(new String[]{"All", "Unread Only", "Read Only"});
        readFilter.setFont(Constants.FONT_REGULAR);
        readFilter.addActionListener(e -> loadData());

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        notificationsTable = new JTable(tableModel);
        notificationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UIHelper.styleTable(notificationsTable);

        notificationsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        notificationsTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        notificationsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        notificationsTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        notificationsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        notificationsTable.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Read column renderer
        notificationsTable.getColumnModel().getColumn(4).setCellRenderer(new ReadCellRenderer());

        notificationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        // Summary labels
        totalLabel = createSummaryValue("0");
        unreadLabel = createSummaryValue("0");
        emailLabel = createSummaryValue("0");
        inAppLabel = createSummaryValue("0");

        // Mark read button
        markReadButton = new JButton("Mark as Read");
        markReadButton.setFont(Constants.FONT_BUTTON);
        markReadButton.setBackground(Constants.SUCCESS_COLOR);
        markReadButton.setForeground(Color.WHITE);
        markReadButton.setOpaque(true);
        markReadButton.setBorderPainted(false);
        markReadButton.setFocusPainted(false);
        markReadButton.setPreferredSize(new Dimension(120, 30));
        markReadButton.setEnabled(false);
        markReadButton.addActionListener(e -> markAsRead());

        // Send notification button
        sendButton = UIHelper.createPrimaryButton("Send Notification");
        sendButton.setPreferredSize(new Dimension(140, 30));
        sendButton.addActionListener(e -> sendNotification());
    }

    private JLabel createSummaryValue(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Constants.PRIMARY_COLOR);
        return label;
    }

    private void layoutComponents() {
        // Summary cards
        JPanel summaryPanel = createSummaryPanel();

        // Toolbar
        JPanel toolbar = createToolbar();

        // Table
        JScrollPane scrollPane = new JScrollPane(notificationsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Constants.BG_WHITE);

        // Top section
        JPanel topSection = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        topSection.setOpaque(false);
        topSection.add(summaryPanel, BorderLayout.NORTH);
        topSection.add(toolbar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Constants.PADDING_MEDIUM, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, Constants.PADDING_SMALL, 0));

        panel.add(createSummaryCard("Total", totalLabel, Constants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Unread", unreadLabel, Constants.DANGER_COLOR));
        panel.add(createSummaryCard("Email", emailLabel, new Color(23, 162, 184)));
        panel.add(createSummaryCard("In-App", inAppLabel, new Color(111, 66, 193)));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Constants.BG_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_MEDIUM,
                          Constants.PADDING_SMALL, Constants.PADDING_MEDIUM)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 3));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Constants.FONT_SMALL);
        titleLabel.setForeground(Constants.TEXT_SECONDARY);

        card.add(colorBar, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(0, Constants.PADDING_SMALL));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(Constants.PADDING_SMALL, 0, Constants.PADDING_SMALL, 0));

        // Filters row
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);
        filterPanel.add(Box.createHorizontalStrut(Constants.PADDING_MEDIUM));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(readFilter);

        // Actions row
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Constants.PADDING_SMALL, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(new JLabel("Actions:"));
        actionPanel.add(Box.createHorizontalStrut(5));
        actionPanel.add(sendButton);
        actionPanel.add(markReadButton);

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

        List<Map<String, Object>> allNotifications = automationService.getAllNotifications();
        String typeSelection = (String) typeFilter.getSelectedItem();
        String readSelection = (String) readFilter.getSelectedItem();

        int unreadCount = 0;
        int emailCount = 0;
        int inAppCount = 0;

        for (Map<String, Object> n : allNotifications) {
            String type = (String) n.get("type");
            boolean read = Boolean.TRUE.equals(n.get("read"));

            // Count totals before filtering
            if (!read) unreadCount++;
            if ("EMAIL".equals(type)) emailCount++;
            if ("IN_APP".equals(type)) inAppCount++;

            // Filter by type
            if (!"All Types".equals(typeSelection) && !typeSelection.equals(type)) continue;

            // Filter by read status
            if ("Unread Only".equals(readSelection) && read) continue;
            if ("Read Only".equals(readSelection) && !read) continue;

            tableModel.addRow(new Object[]{
                n.get("id"),
                type,
                n.get("subject"),
                n.get("message"),
                read ? "Read" : "Unread",
                n.get("createdAt")
            });
        }

        totalLabel.setText(String.valueOf(allNotifications.size()));
        unreadLabel.setText(String.valueOf(unreadCount));
        emailLabel.setText(String.valueOf(emailCount));
        inAppLabel.setText(String.valueOf(inAppCount));

        updateButtonStates();
    }

    private void updateButtonStates() {
        int row = notificationsTable.getSelectedRow();
        if (row >= 0) {
            String readStatus = (String) tableModel.getValueAt(row, 4);
            markReadButton.setEnabled("Unread".equals(readStatus));
        } else {
            markReadButton.setEnabled(false);
        }
    }

    private void markAsRead() {
        int row = notificationsTable.getSelectedRow();
        if (row < 0) return;

        String id = (String) tableModel.getValueAt(row, 0);
        if (automationService.markNotificationRead(id)) {
            UIHelper.showSuccess(this, "Notification marked as read.");
            loadData();
        } else {
            UIHelper.showError(this, "Failed to mark notification as read.");
        }
    }

    private void sendNotification() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Send Notification", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField userIdField = new JTextField("1");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"IN_APP", "EMAIL", "SMS"});
        JTextField subjectField = new JTextField();
        JTextArea messageArea = new JTextArea();
        messageArea.setRows(3);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        formPanel.add(new JLabel("User ID:"));
        formPanel.add(userIdField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Subject:*"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Message:*"));
        formPanel.add(new JScrollPane(messageArea));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton sendBtn = UIHelper.createPrimaryButton("Send");
        sendBtn.addActionListener(e -> {
            if (subjectField.getText().trim().isEmpty() || messageArea.getText().trim().isEmpty()) {
                UIHelper.showError(dialog, "Subject and message are required.");
                return;
            }

            int userId;
            try {
                userId = Integer.parseInt(userIdField.getText().trim());
            } catch (NumberFormatException ex) {
                UIHelper.showError(dialog, "Invalid user ID.");
                return;
            }

            automationService.sendNotification(userId, (String) typeCombo.getSelectedItem(),
                    subjectField.getText().trim(), messageArea.getText().trim());
            UIHelper.showSuccess(dialog, "Notification sent successfully.");
            dialog.dispose();
            loadData();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(sendBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void refreshData() {
        loadData();
    }

    // Read status cell renderer
    private static class ReadCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                if ("Unread".equals(value.toString())) {
                    setBackground(new Color(255, 243, 205));
                    setForeground(new Color(133, 100, 4));
                    setFont(new Font(Constants.FONT_FAMILY, Font.BOLD, 12));
                } else {
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                    setFont(Constants.FONT_SMALL);
                }
            }
            return this;
        }
    }
}
