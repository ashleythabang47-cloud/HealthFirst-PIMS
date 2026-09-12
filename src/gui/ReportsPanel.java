package gui;

import dao.MedicineDAO;
import dao.SalesDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin's "Reports" tab. Contains four sub-tabs:
 * Sales report, Item-Wise report, Low Stock report, Expiry report.
 * Each has its own Refresh button since they don't auto-update while
 * the tab is open (e.g. while a cashier processes a sale elsewhere).
 */
public class ReportsPanel extends JPanel {

    private final SalesDAO salesDAO = new SalesDAO();
    private final MedicineDAO medicineDAO = new MedicineDAO();

    public ReportsPanel() {
        setLayout(new BorderLayout());

        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.addTab("Sales Report", buildSalesReportTab());
        reportTabs.addTab("Item-Wise Report", buildItemWiseReportTab());
        reportTabs.addTab("Low Stock Report", buildLowStockReportTab());
        reportTabs.addTab("Expiry Report", buildExpiryReportTab());

        add(reportTabs, BorderLayout.CENTER);
    }

    private JPanel buildSalesReportTab() {
        String[] columns = {"Sale ID", "Date", "Cashier", "Total (R)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (SalesDAO.SaleSummary s : salesDAO.getSalesSummary()) {
                model.addRow(new Object[]{s.saleId, s.saleDate, s.cashierName, s.totalAmount});
            }
        };
        refresh.run();

        return buildReportTabPanel(model, refresh);
    }

    private JPanel buildItemWiseReportTab() {
        String[] columns = {"Medicine", "Total Qty Sold", "Total Revenue (R)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Runnable refresh = () -> {
            model.setRowCount(0);
            for (SalesDAO.ItemWiseSummary s : salesDAO.getItemWiseSummary()) {
                model.addRow(new Object[]{s.medicineName, s.totalQuantitySold, s.totalRevenue});
            }
        };
        refresh.run();

        return buildReportTabPanel(model, refresh);
    }

    private JPanel buildLowStockReportTab() {
        String[] columns = {"Medicine", "Current Stock", "Reorder Level", "Supplier ID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Runnable refresh = () -> {
            model.setRowCount(0);
            List<Medicine> lowStock = medicineDAO.getLowStockMedicines();
            for (Medicine m : lowStock) {
                model.addRow(new Object[]{
                        m.getName(), m.getQuantityInStock(), m.getReorderLevel(), m.getSupplierId()
                });
            }
        };
        refresh.run();

        return buildReportTabPanel(model, refresh);
    }


    private JPanel buildExpiryReportTab() {
        String[] columns = {"Medicine", "Expiry Date", "Current Stock"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        Runnable refresh = () -> {
            model.setRowCount(0);
            List<Medicine> expiring = medicineDAO.getMedicinesExpiringWithinDays(30);
            for (Medicine m : expiring) {
                model.addRow(new Object[]{m.getName(), m.getExpiryDate(), m.getQuantityInStock()});
            }
        };
        refresh.run();

        return buildReportTabPanel(model, refresh);
    }

    private JPanel buildReportTabPanel(DefaultTableModel model, Runnable refreshAction) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshAction.run());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}

