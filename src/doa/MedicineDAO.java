import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the medicines table:
 * CRUD for Admin's "Manage Medicines" tab, plus queries used by
 * the Cashier's Stock Check and the Low Stock / Expiry reports.
 */
public class MedicineDAO {

    public boolean addMedicine(Medicine m) {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindMedicineParams(stmt, m);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedicine(Medicine m) {
        String sql = "UPDATE medicines SET name=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, " +
                     "reorder_level=?, expiry_date=?, supplier_id=? WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindMedicineParams(stmt, m);
            stmt.setInt(9, m.getMedicineId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, medicineId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Used by Cashier's Stock Check screen - find by name (partial match). */
    public List<Medicine> searchByName(String keyword) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** For the Low Stock report: quantity_in_stock <= reorder_level. */
    public List<Medicine> getLowStockMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE quantity_in_stock <= reorder_level";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** For the Expiry report: medicines expiring within the next month. */
    public List<Medicine> getMedicinesExpiringWithinDays(int days) {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void bindMedicineParams(PreparedStatement stmt, Medicine m) throws SQLException {
        stmt.setString(1, m.getName());
        stmt.setString(2, m.getCompany());
        stmt.setString(3, m.getMedicineType());
        stmt.setBigDecimal(4, m.getPrice());
        stmt.setInt(5, m.getQuantityInStock());
        stmt.setInt(6, m.getReorderLevel());
        stmt.setDate(7, m.getExpiryDate());
        stmt.setInt(8, m.getSupplierId());
    }

    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getInt("medicine_id"));
        m.setName(rs.getString("name"));
        m.setCompany(rs.getString("company"));
        m.setMedicineType(rs.getString("medicine_type"));
        m.setPrice(rs.getBigDecimal("price"));
        m.setQuantityInStock(rs.getInt("quantity_in_stock"));
        m.setReorderLevel(rs.getInt("reorder_level"));
        m.setExpiryDate(rs.getDate("expiry_date"));
        m.setSupplierId(rs.getInt("supplier_id"));
        return m;
    }

    // --- Simple quick test ---
    public static void main(String[] args) {
        MedicineDAO dao = new MedicineDAO();

        System.out.println("All medicines:");
        for (Medicine m : dao.getAllMedicines()) {
            System.out.println("  - " + m);
        }

        System.out.println("\nLow stock:");
        for (Medicine m : dao.getLowStockMedicines()) {
            System.out.println("  - " + m);
        }

        System.out.println("\nExpiring within 30 days:");
        for (Medicine m : dao.getMedicinesExpiringWithinDays(30)) {
            System.out.println("  - " + m + " expires " + m.getExpiryDate());
        }
    }
}
