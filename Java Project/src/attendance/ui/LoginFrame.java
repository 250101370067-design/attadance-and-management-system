package attendance.ui;

import attendance.model.Admin;
import attendance.model.Student;
import attendance.model.Teacher;
import attendance.model.User;
import attendance.service.AuthService;
import attendance.service.DataStore;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final DataStore dataStore;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public LoginFrame(AuthService authService, DataStore dataStore) {
        super("Student Attendance - Login");
        this.authService = authService;
        this.dataStore = dataStore;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 240);
        setLocationRelativeTo(null);
        initUI();
        maybeFirstRunSetup();
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Role:", SwingConstants.RIGHT));
        roleBox = new JComboBox<>(new String[] { "Admin", "Teacher", "Student" });
        form.add(roleBox);

        form.add(new JLabel("Username:", SwingConstants.RIGHT));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:", SwingConstants.RIGHT));
        passwordField = new JPasswordField();
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        form.add(new JLabel());
        form.add(loginBtn);

        add(form, BorderLayout.CENTER);
    }

    private void maybeFirstRunSetup() {
        if (authService.isFirstRunNoAdmins()) {
            String name = JOptionPane.showInputDialog(this, "Create Admin - Name:");
            if (name == null || name.isBlank()) return;
            String username = JOptionPane.showInputDialog(this, "Create Admin - Username:");
            if (username == null || username.isBlank()) return;
            String password = JOptionPane.showInputDialog(this, "Create Admin - Password:");
            if (password == null || password.isBlank()) return;
            authService.createInitialAdmin(name.trim(), username.trim(), password);
            JOptionPane.showMessageDialog(this, "Admin created. You can now login.");
        }
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        AuthService.Role r = AuthService.Role.ADMIN;
        if ("Teacher".equals(role)) r = AuthService.Role.TEACHER;
        if ("Student".equals(role)) r = AuthService.Role.STUDENT;

        Optional<User> user = authService.login(username, password, r);
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setVisible(false);
        switch (r) {
            case ADMIN:
                new AdminDashboard(this, dataStore).setVisible(true);
                break;
            case TEACHER:
                new TeacherDashboard(this, dataStore, (Teacher) user.get()).setVisible(true);
                break;
            case STUDENT:
                new StudentDashboard(this, dataStore, (Student) user.get()).setVisible(true);
                break;
        }
    }
}


