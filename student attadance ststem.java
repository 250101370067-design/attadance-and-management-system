package attendance;

import attendance.service.AuthService;
import attendance.service.DataStore;
import attendance.ui.LoginFrame;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStore dataStore = new DataStore("data");
            AuthService authService = new AuthService(dataStore);
            LoginFrame loginFrame = new LoginFrame(authService, dataStore);
            loginFrame.setVisible(true);
        });
    }
}


