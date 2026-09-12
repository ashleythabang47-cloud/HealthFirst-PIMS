package dao;

import database.DBConnection;
import models.Sale;
import models.SaleItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for sales and sale_items.
 * checkout() is the key method: it wraps the whole transaction
 * (insert sale, insert line items, decrement stock) so that either
 * everything succeeds together or nothing is saved at all.
 */
public class SalesDAO {

    /**
     * Processes a checkout: creates the sale header, all line items,
     * and decrements medicine stock - as a single transaction.
     *
     * @param userId  the cashier processing the sale
     * @param cartItems the items in the cart (medicineId, quantity, priceAtSale set on each)
     * @return the generated sale_id if successful, or -1 if the transaction failed
     */
    public int checkout(int userId, List<SaleItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return -1;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (SaleItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // start transaction

            // 1. Insert the sale header
            int saleId;
            String saleSql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
            try (PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setBigDecimal(1, total);
                saleStmt.setInt(2, userId);
                saleStmt.executeUpdate();

                ResultSet keys = saleStmt.getGeneratedKeys();
                if (keys.next()) {
                    saleId = keys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated sale_id.");
                }
            }

            // 2. Insert each line item and decrement stock
            String itemSql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";
            String stockSql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ? AND quantity_in_stock >= ?";

            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                 PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {

                for (SaleItem item : cartItems) {
                    itemStmt.setInt(1, saleId);
                    itemStmt.setInt(2, item.getMedicineId());
                    itemStmt.setInt(3, item.getQuantitySold());
                    itemStmt.setBigDecimal(4, item.getPriceAtSale());
                    itemStmt.addBatch();

                    stockStmt.setInt(1, item.getQuantitySold());
                    stockStmt.setInt(2, item.getMedicineId());
                    stockStmt.setInt(3, item.getQuantitySold()); // guards against overselling
                    int updated = stockStmt.executeUpdate();

                    if (updated == 0) {
                        // Not enough stock for this item - abort the whole transaction
                        throw new SQLException("Insufficient stock for medicine_id " + item.getMedicineId());
                    }
                }
                itemStmt.executeBatch();
            }

            conn.commit();
            return saleId;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // undo everything if any step failed
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return -1;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    /** Used by the Sales report. */
    public List<Sale> getAllSales() {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Sale s = new Sale();
                s.setSaleId(rs.getInt("sale_id"));
                s.setSaleDate(rs.getTimestamp("sale_date"));
                s.setTotalAmount(rs.getBigDecimal("total_amount"));
                s.setUserId(rs.getInt("user_id"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SaleItem> getItemsForSale(int saleId) {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT si.*, m.name AS medicine_name FROM sale_items si " +
                "JOIN medicines m ON si.medicine_id = m.medicine_id WHERE si.sale_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SaleItem item = new SaleItem();
                item.setSaleItemId(rs.getInt("sale_item_id"));
                item.setSaleId(rs.getInt("sale_id"));
                item.setMedicineId(rs.getInt("medicine_id"));
                item.setMedicineName(rs.getString("medicine_name"));
                item.setQuantitySold(rs.getInt("quantity_sold"));
                item.setPriceAtSale(rs.getBigDecimal("price_at_sale"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class SaleSummary {
        public int saleId;
        public Timestamp saleDate;
        public String cashierName;
        public BigDecimal totalAmount;
    }

    public List<SaleSummary> getSalesSummary() {
        List<SaleSummary> list = new ArrayList<>();
        String sql = "SELECT s.sale_id, s.sale_date, s.total_amount, u.full_name " +
                "FROM sales s JOIN users u ON s.user_id = u.user_id " +
                "ORDER BY s.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SaleSummary row = new SaleSummary();
                row.saleId = rs.getInt("sale_id");
                row.saleDate = rs.getTimestamp("sale_date");
                row.totalAmount = rs.getBigDecimal("total_amount");
                row.cashierName = rs.getString("full_name");
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class ItemWiseSummary {
        public String medicineName;
        public int totalQuantitySold;
        public BigDecimal totalRevenue;
    }

    public List<ItemWiseSummary> getItemWiseSummary() {
        List<ItemWiseSummary> list = new ArrayList<>();
        String sql = "SELECT m.name, SUM(si.quantity_sold) AS total_qty, " +
                "SUM(si.quantity_sold * si.price_at_sale) AS total_revenue " +
                "FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id " +
                "GROUP BY m.medicine_id, m.name ORDER BY total_revenue DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ItemWiseSummary row = new ItemWiseSummary();
                row.medicineName = rs.getString("name");
                row.totalQuantitySold = rs.getInt("total_qty");
                row.totalRevenue = rs.getBigDecimal("total_revenue");
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}