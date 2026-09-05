package gui;

import models.User;

import javax.swing.*;
import java.awt.*;

/**
 * Admin dashboard shown after a successful Admin login.
 * Tabs for Manage Medicines, Manage Suppliers, Manage Users, and Reports
 * will be added here as those modules are built.
 */
public class AdminDashboard extends JFrame {

    private final User loggedInUser;

    public AdminDashboard(User user) {
        this.loggedInUser = user;

        setTitle("HealthFirst PIMS - Admin Dashboard");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + loggedInUser.getFullName() + " (Admin)",
                SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        // Placeholder tabs remain for SupplierPanel, UserPanel, and
        // ReportsPanel until those are built.
        tabbedPane.addTab("Manage Medicines", new MedicinePanel());
        tabbedPane.addTab("Manage Suppliers", new JPanel());
        tabbedPane.addTab("Manage Users", new JPanel());
        tabbedPane.addTab("Reports", new JPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
