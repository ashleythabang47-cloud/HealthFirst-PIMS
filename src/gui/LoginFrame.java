package gui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Login screen for HealthFirst PIMS.
 * On successful login, redirects to AdminDashboard or CashierDashboard
 * based on the logged-in user's role.
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("HealthFirst PIMS - Login");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("HealthFirst Pharmacy", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(statusLabel, gbc);

        add(panel);

        loginButton.addActionListener(this::handleLogin);
        // Allow pressing Enter in the password field to log in too
        passwordField.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        User user = userDAO.login(username, password);

        if (user == null) {
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
            return;
        }

        // Successful login - route based on role
        this.dispose(); // close the login window

        if (user.isAdmin()) {
            new AdminDashboard(user).setVisible(true);
        } else {
            new CashierDashboard(user).setVisible(true);
        }
    }

    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread, as Swing requires
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
