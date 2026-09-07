package gui;


import dao.MedicineDAO;
import dao.SalesDAO;
import models.Medicine;
import models.SaleItem;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cashier's Point of Sale screen. Lets the cashier pick a medicine,
 * choose a quantity, build up a cart, and check out - which creates
 * a real sale record and decrements stock via SalesDAO.
 */
public class POSPanel extends JPanel {

    private final MedicineDAO medicineDAO = new MedicineDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final User loggedInUser;

    private final List<Medicine> availableMedicines = new ArrayList<>();
    private final List<SaleItem> cart = new ArrayList<>();

    private JComboBox<String> medicineDropdown;
    private JSpinner quantitySpinner;
    private JLabel stockInfoLabel;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;

    public POSPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildAddItemPanel(), BorderLayout.NORTH);
        add(buildCartPanel(), BorderLayout.CENTER);
        add(buildCheckoutPanel(), BorderLayout.SOUTH);

        loadMedicines();
    }

    // ---------------------------------------------------------------
    // Top: pick a medicine + quantity, add to cart
    // ---------------------------------------------------------------
    private JPanel buildAddItemPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Add Item"));

        medicineDropdown = new JComboBox<>();
        medicineDropdown.addActionListener(e -> updateStockInfo());

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        stockInfoLabel = new JLabel("Stock: -");

        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(e -> handleAddToCart());

        JButton refreshButton = new JButton("Refresh Stock");
        refreshButton.addActionListener(e -> loadMedicines());

        panel.add(new JLabel("Medicine:"));
        panel.add(medicineDropdown);
        panel.add(new JLabel("Qty:"));
        panel.add(quantitySpinner);
        panel.add(stockInfoLabel);
        panel.add(addToCartButton);
        panel.add(refreshButton);

        return panel;
    }

    private void loadMedicines() {
        availableMedicines.clear();
        availableMedicines.addAll(medicineDAO.getAllMedicines());

        medicineDropdown.removeAllItems();
        for (Medicine m : availableMedicines) {
            medicineDropdown.addItem(m.getName() + " (R" + m.getPrice() + ")");
        }
        updateStockInfo();
    }

    private void updateStockInfo() {
        Medicine selected = getSelectedMedicine();
        if (selected != null) {
            stockInfoLabel.setText("Stock: " + selected.getQuantityInStock());
        } else {
            stockInfoLabel.setText("Stock: -");
        }
    }

    private Medicine getSelectedMedicine() {
        int index = medicineDropdown.getSelectedIndex();
        if (index < 0 || index >= availableMedicines.size()) return null;
        return availableMedicines.get(index);
    }

    // ---------------------------------------------------------------
    // Middle: the cart table
    // ---------------------------------------------------------------
    private JScrollPane buildCartPanel() {
        String[] columns = {"Medicine", "Price", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        return new JScrollPane(cartTable);
    }

    private void handleAddToCart() {
        Medicine selected = getSelectedMedicine();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No medicine selected.");
            return;
        }

        int requestedQty = (int) quantitySpinner.getValue();

        // Check how much of this medicine is already in the cart
        int alreadyInCart = 0;
        for (SaleItem item : cart) {
            if (item.getMedicineId() == selected.getMedicineId()) {
                alreadyInCart += item.getQuantitySold();
            }
        }

        if (alreadyInCart + requestedQty > selected.getQuantityInStock()) {
            JOptionPane.showMessageDialog(this,
                    "Not enough stock. Available: " + selected.getQuantityInStock() +
                            " (already " + alreadyInCart + " in cart).",
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If this medicine is already in the cart, just bump its quantity
        for (SaleItem item : cart) {
            if (item.getMedicineId() == selected.getMedicineId()) {
                item.setQuantitySold(item.getQuantitySold() + requestedQty);
                refreshCartTable();
                return;
            }
        }

        // Otherwise add a new line item
        SaleItem newItem = new SaleItem();
        newItem.setMedicineId(selected.getMedicineId());
        newItem.setMedicineName(selected.getName());
        newItem.setQuantitySold(requestedQty);
        newItem.setPriceAtSale(selected.getPrice());
        cart.add(newItem);

        refreshCartTable();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : cart) {
            cartTableModel.addRow(new Object[]{
                    item.getMedicineName(), item.getPriceAtSale(),
                    item.getQuantitySold(), item.getSubtotal()
            });
            total = total.add(item.getSubtotal());
        }
        totalLabel.setText("Total: R" + total);
    }

    // ---------------------------------------------------------------
    // Bottom: total + checkout/clear
    // ---------------------------------------------------------------
    private JPanel buildCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total: R0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));

        JPanel buttonPanel = new JPanel();
        JButton removeSelectedButton = new JButton("Remove Selected Item");
        JButton clearCartButton = new JButton("Clear Cart");
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        removeSelectedButton.addActionListener(e -> handleRemoveSelected());
        clearCartButton.addActionListener(e -> handleClearCart());
        checkoutButton.addActionListener(e -> handleCheckout());

        buttonPanel.add(removeSelectedButton);
        buttonPanel.add(clearCartButton);
        buttonPanel.add(checkoutButton);

        panel.add(totalLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void handleRemoveSelected() {
        int row = cartTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a cart item to remove first.");
            return;
        }
        cart.remove(row);
        refreshCartTable();
    }

    private void handleClearCart() {
        cart.clear();
        refreshCartTable();
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm checkout for " + cart.size() + " item(s)?",
                "Confirm Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int saleId = salesDAO.checkout(loggedInUser.getUserId(), cart);

        if (saleId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Checkout failed. Stock may have changed - please review the cart and try again.",
                    "Checkout Error", JOptionPane.ERROR_MESSAGE);
            loadMedicines(); // refresh in case stock changed underneath us
            return;
        }

        // Show the bill, then reset for the next customer
        new BillFrame(saleId, new ArrayList<>(cart)).setVisible(true);

        cart.clear();
        refreshCartTable();
        loadMedicines(); // stock has changed, refresh the dropdown
    }
}
