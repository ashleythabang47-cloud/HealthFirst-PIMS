package gui;

import models.User;

import javax.swing.*;
import java.awt.*;

/**
 * Cashier dashboard shown after a successful Cashier login.
 * The POS panel (checkout, cart, billing) and Stock Check panel
 */
public class CashierDashboard extends JFrame {

    private final User loggedInUser;

    public CashierDashboard(User user) {
        this.loggedInUser = user;

        setTitle("HealthFirst PIMS - Cashier Dashboard");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + loggedInUser.getFullName() + " (Cashier)",
                SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Point of Sale", new POSPanel(loggedInUser));
        tabbedPane.addTab("Stock Check", new StockCheckPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}