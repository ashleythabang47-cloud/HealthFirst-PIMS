package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that provides a single reusable method for obtaining
 * a JDBC connection to the pims_db MySQL database.
 *
 * Every DAO class (UserDAO, MedicineDAO, SupplierDAO, etc.) should call
 * DBConnection.getConnection() rather than opening its own connection.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/pims_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // <-- fill this in

    /**
     * Opens and returns a new connection to the pims_db database.
     * Caller is responsible for closing the connection (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Quick manual test - run this file directly to confirm the driver
     * and credentials work before building any DAO classes.
     */
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connected to pims_db successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }
}