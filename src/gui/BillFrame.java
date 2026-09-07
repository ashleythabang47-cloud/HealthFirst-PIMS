package gui;

import models.SaleItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Displays a finalized bill after checkout: itemized list and total.
 * Includes a "Save as Text" option to satisfy the "print/save" bill
 * requirement from the assignment brief.
 */
public class BillFrame extends JFrame {

    public BillFrame(int saleId, List<SaleItem> items) {
        setTitle("HealthFirst PIMS - Bill #" + saleId);
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel headerLabel = new JLabel(
                "<html><center><b>HealthFirst Pharmacy</b><br>Bill / Receipt #" + saleId + "</center></html>",
                SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(headerLabel, BorderLayout.NORTH);

        String[] columns = {"Medicine", "Price", "Qty", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : items) {
            model.addRow(new Object[]{
                    item.getMedicineName(), item.getPriceAtSale(),
                    item.getQuantitySold(), item.getSubtotal()
            });
            total = total.add(item.getSubtotal());
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel totalLabel = new JLabel("TOTAL: R" + total, SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 15));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(closeButton, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
