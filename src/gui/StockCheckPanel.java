package gui;

import dao.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Cashier's "Stock Check" tab.
 * Read-only: lets a cashier quickly look up a medicine's price and
 * availability without making a sale. Cashiers cannot add or edit
 * medicines here - that's Admin-only (see MedicinePanel).
 */
public class StockCheckPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();

    private JTextField searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public StockCheckPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildSearchPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadAllMedicines(); // show everything by default
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search Medicine"));

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");

        // Allow pressing Enter in the search field too
        searchField.addActionListener(e -> handleSearch());
        searchButton.addActionListener(e -> handleSearch());
        showAllButton.addActionListener(e -> loadAllMedicines());

        panel.add(new JLabel("Medicine name:"));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(showAllButton);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Name", "Company", "Type", "Price", "Stock Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // strictly read-only - no editing here
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMedicines();
            return;
        }
        List<Medicine> results = medicineDAO.searchByName(keyword);
        populateTable(results);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No medicines found matching \"" + keyword + "\".");
        }
    }

    private void loadAllMedicines() {
        populateTable(medicineDAO.getAllMedicines());
    }

    private void populateTable(List<Medicine> medicines) {
        tableModel.setRowCount(0);
        for (Medicine m : medicines) {
            String stockDisplay = m.getQuantityInStock() + (m.isLowStock() ? " (LOW STOCK)" : "");
            tableModel.addRow(new Object[]{
                    m.getName(), m.getCompany(), m.getMedicineType(),
                    "R" + m.getPrice(), stockDisplay
            });
        }
    }
}