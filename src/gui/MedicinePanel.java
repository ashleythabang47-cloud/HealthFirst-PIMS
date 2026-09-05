package gui;

import dao.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Admin's "Manage Medicines" tab.
 * Full CRUD: view all medicines in a table, add new ones, update the
 * selected one, or delete it - all backed by MedicineDAO.
 */
public class MedicinePanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField nameField, companyField, typeField, priceField,
            quantityField, reorderField, expiryField, supplierIdField;

    private int selectedMedicineId = -1; // -1 means no row selected

    public MedicinePanel() {
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
        String[] columns = {"ID", "Name", "Company", "Type", "Price",
                "Stock", "Reorder Lvl", "Expiry", "Supplier ID"};
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
        List<Medicine> medicines = medicineDAO.getAllMedicines();
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[]{
                    m.getMedicineId(), m.getName(), m.getCompany(), m.getMedicineType(),
                    m.getPrice(), m.getQuantityInStock(), m.getReorderLevel(),
                    m.getExpiryDate(), m.getSupplierId()
            });
        }
    }

    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        selectedMedicineId = (int) tableModel.getValueAt(row, 0);
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        companyField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        typeField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        priceField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        quantityField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        reorderField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        expiryField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        supplierIdField.setText(String.valueOf(tableModel.getValueAt(row, 8)));
    }

    // ---------------------------------------------------------------
    // Form (bottom section) - used for both Add and Update
    // ---------------------------------------------------------------
    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Medicine Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(15);
        companyField = new JTextField(15);
        typeField = new JTextField(10);
        priceField = new JTextField(8);
        quantityField = new JTextField(6);
        reorderField = new JTextField(6);
        expiryField = new JTextField(10); // format: YYYY-MM-DD
        supplierIdField = new JTextField(4);

        addFormRow(formPanel, gbc, 0, "Name:", nameField, "Company:", companyField);
        addFormRow(formPanel, gbc, 1, "Type:", typeField, "Price:", priceField);
        addFormRow(formPanel, gbc, 2, "Quantity:", quantityField, "Reorder Level:", reorderField);
        addFormRow(formPanel, gbc, 3, "Expiry (YYYY-MM-DD):", expiryField, "Supplier ID:", supplierIdField);

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

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row,
                             String label1, JTextField field1, String label2, JTextField field2) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label1), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(field1, gbc);
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel(label2), gbc);
        gbc.gridx = 3; gbc.gridy = row;
        panel.add(field2, gbc);
    }

    // ---------------------------------------------------------------
    // Button actions
    // ---------------------------------------------------------------
    private void handleAdd() {
        Medicine m = buildMedicineFromForm();
        if (m == null) return; // validation failed, message already shown

        boolean success = medicineDAO.addMedicine(m);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine added successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedMedicineId == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine from the table first.");
            return;
        }
        Medicine m = buildMedicineFromForm();
        if (m == null) return;
        m.setMedicineId(selectedMedicineId);

        boolean success = medicineDAO.updateMedicine(m);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine updated successfully.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedMedicineId == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine from the table first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this medicine?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = medicineDAO.deleteMedicine(selectedMedicineId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Medicine deleted.");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedMedicineId = -1;
        nameField.setText("");
        companyField.setText("");
        typeField.setText("");
        priceField.setText("");
        quantityField.setText("");
        reorderField.setText("");
        expiryField.setText("");
        supplierIdField.setText("");
        table.clearSelection();
    }

    /**
     * Reads the form fields, validates them, and builds a Medicine object.
     * Shows an error dialog and returns null if validation fails.
     */
    private Medicine buildMedicineFromForm() {
        try {
            String name = nameField.getText().trim();
            String company = companyField.getText().trim();
            String type = typeField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            BigDecimal price = new BigDecimal(priceField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int reorderLevel = Integer.parseInt(reorderField.getText().trim());
            Date expiryDate = Date.valueOf(expiryField.getText().trim()); // expects YYYY-MM-DD
            int supplierId = Integer.parseInt(supplierIdField.getText().trim());

            Medicine m = new Medicine();
            m.setName(name);
            m.setCompany(company);
            m.setMedicineType(type);
            m.setPrice(price);
            m.setQuantityInStock(quantity);
            m.setReorderLevel(reorderLevel);
            m.setExpiryDate(expiryDate);
            m.setSupplierId(supplierId);
            return m;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Price, Quantity, Reorder Level, and Supplier ID must be valid numbers.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Expiry date must be in format YYYY-MM-DD (e.g. 2027-06-30).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}
