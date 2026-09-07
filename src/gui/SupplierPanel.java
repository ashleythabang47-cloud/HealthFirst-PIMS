package gui;

import dao.SupplierDAO;
import models.Supplier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin's "Manage Suppliers" tab.
 * Full CRUD: view all suppliers in a table, add new ones, update the
 * selected one, or delete it - all backed by SupplierDAO.
 */
public class SupplierPanel extends JPanel {

    private final SupplierDAO supplierDAO = new SupplierDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField nameField, contactPersonField, phoneField, emailField;
    private JTextArea addressArea;

    private int selectedSupplierId = -1; // -1 means no row selected

    public SupplierPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    // ---------------------------------------------------------------
    // Table (top/center section)
    // ---------------------------------------------------------------
    private JScrollPane buildTablePanel() {
        String[] columns = {"ID", "Name", "Contact Person", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only; edits happen via the form below
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });

        return new JScrollPane(table);
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // clear existing rows
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{
                    s.getSupplierId(), s.getName(), s.getContactPerson(),
                    s.getPhone(), s.getEmail(), s.getAddress()
            });
        }
    }

    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        selectedSupplierId = (int) tableModel.getValueAt(row, 0);
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        contactPersonField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        emailField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        addressArea.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    // ---------------------------------------------------------------
    // Form (bottom section) - used for both Add and Update
    // ---------------------------------------------------------------
    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        contactPersonField = new JTextField(15);
        phoneField = new JTextField(12);
        emailField = new JTextField(18);
        addressArea = new JTextArea(2, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(contactPersonField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(phoneField, gbc);
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        formPanel.add(addressScroll, gbc);
        gbc.gridwidth = 1;

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add New");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear Form");

        addButton.addActionListener(e -> handleAdd());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    // ---------------------------------------------------------------
    // Button actions
    // ---------------------------------------------------------------
    private void handleAdd() {
        Supplier s = buildSupplierFromForm();
        if (s == null) return; // validation failed, message already shown

        boolean success = supplierDAO.addSupplier(s);
        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier added successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add supplier.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedSupplierId == -1) {
            JOptionPane.showMessageDialog(this, "Select a supplier from the table first.");
            return;
        }
        Supplier s = buildSupplierFromForm();
        if (s == null) return;
        s.setSupplierId(selectedSupplierId);

        boolean success = supplierDAO.updateSupplier(s);
        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update supplier.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedSupplierId == -1) {
            JOptionPane.showMessageDialog(this, "Select a supplier from the table first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this supplier?\n(Any medicines linked to it will have their supplier cleared.)",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = supplierDAO.deleteSupplier(selectedSupplierId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Supplier deleted.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete supplier.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedSupplierId = -1;
        nameField.setText("");
        contactPersonField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        table.clearSelection();
    }

    /**
     * Reads the form fields, validates them, and builds a Supplier object.
     * Shows an error dialog and returns null if validation fails.
     */
    private Supplier buildSupplierFromForm() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Supplier s = new Supplier();
        s.setName(name);
        s.setContactPerson(contactPersonField.getText().trim());
        s.setPhone(phoneField.getText().trim());
        s.setEmail(emailField.getText().trim());
        s.setAddress(addressArea.getText().trim());
        return s;
    }
}
