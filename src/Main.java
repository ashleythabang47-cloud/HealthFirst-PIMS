import gui.LoginFrame;

import javax.swing.*;

/**
 * Entry point for HealthFirst PIMS.
 * Launches the login screen.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
