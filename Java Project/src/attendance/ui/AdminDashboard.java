package attendance.ui;

import attendance.model.Assignment;
import attendance.model.Section;
import attendance.model.Student;
import attendance.model.Subject;
import attendance.model.Teacher;
import attendance.service.AdminService;
import attendance.service.DataStore;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JFileChooser;
import attendance.service.ReportService;
import attendance.util.CsvUtil;
import java.io.File;
import java.io.IOException;

public class AdminDashboard extends JFrame {
    private final AdminService adminService;
    private final DataStore dataStore;
    private final JFrame parentToReturn;

    public AdminDashboard(JFrame parentToReturn, DataStore dataStore) {
        super("Admin Dashboard");
        this.parentToReturn = parentToReturn;
        this.dataStore = dataStore;
        this.adminService = new AdminService(dataStore);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));

        JPanel actions = new JPanel(new GridLayout(3, 4, 8, 8));

        JButton addTeacher = new JButton("Add Teacher");
        addTeacher.addActionListener(e -> onAddTeacher());
        JButton addStudent = new JButton("Add Student");
        addStudent.addActionListener(e -> onAddStudent());
        JButton addSubject = new JButton("Add Subject");
        addSubject.addActionListener(e -> onAddSubject());
        JButton addSection = new JButton("Add Section");
        addSection.addActionListener(e -> onAddSection());
        JButton assign = new JButton("Create Assignment");
        assign.addActionListener(e -> onAddAssignment());
        JButton refresh = new JButton("Refresh Lists");
        refresh.addActionListener(e -> refreshTables());
        JButton importStudents = new JButton("Import Students (CSV)");
        importStudents.addActionListener(e -> onImportStudents());
        JButton exportData = new JButton("Export Data (CSV)");
        exportData.addActionListener(e -> onExportData());
        JButton resetTeacherPwd = new JButton("Reset Teacher Password");
        resetTeacherPwd.addActionListener(e -> onResetTeacherPassword());
        JButton resetStudentPwd = new JButton("Reset Student Password");
        resetStudentPwd.addActionListener(e -> onResetStudentPassword());
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); parentToReturn.setVisible(true); });

        actions.add(addTeacher);
        actions.add(addStudent);
        actions.add(addSubject);
        actions.add(addSection);
        actions.add(assign);
        actions.add(refresh);
        actions.add(importStudents);
        actions.add(exportData);
        actions.add(resetTeacherPwd);
        actions.add(resetStudentPwd);
        actions.add(logout);

        root.add(actions, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(2, 2, 8, 8));
        tables.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTable teacherTable = new JTable(new DefaultTableModel(new Object[] { "ID", "Name", "Username", "Email" }, 0));
        JTable studentTable = new JTable(new DefaultTableModel(new Object[] { "ID", "Name", "Username", "Roll", "Section" }, 0));
        JTable subjectTable = new JTable(new DefaultTableModel(new Object[] { "ID", "Name" }, 0));
        JTable sectionTable = new JTable(new DefaultTableModel(new Object[] { "ID", "Name" }, 0));

        tables.add(new JScrollPane(teacherTable));
        tables.add(new JScrollPane(studentTable));
        tables.add(new JScrollPane(subjectTable));
        tables.add(new JScrollPane(sectionTable));

        root.add(tables, BorderLayout.CENTER);
        add(root);

        // initial load
        loadTeachers((DefaultTableModel) teacherTable.getModel());
        loadStudents((DefaultTableModel) studentTable.getModel());
        loadSubjects((DefaultTableModel) subjectTable.getModel());
        loadSections((DefaultTableModel) sectionTable.getModel());
    }

    private void refreshTables() {
        // Simply re-create the frame for simplicity
        dispose();
        new AdminDashboard(parentToReturn, dataStore).setVisible(true);
    }

    private void onImportStudents() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                List<String[]> rows = CsvUtil.readCsv(file.getAbsolutePath());
                // Expect header: name,username,password,roll,section
                boolean headerSkipped = false;
                for (String[] row : rows) {
                    if (!headerSkipped) { headerSkipped = true; continue; }
                    if (row.length < 5) continue;
                    String name = row[0].trim();
                    String username = row[1].trim();
                    String password = row[2].trim();
                    String roll = row[3].trim();
                    String sectionName = row[4].trim();
                    String sectionId = adminService.listSections().stream()
                            .filter(s -> s.getName().equalsIgnoreCase(sectionName))
                            .findFirst()
                            .map(Section::getId)
                            .orElseGet(() -> adminService.addSection(sectionName).getId());
                    adminService.addStudent(name, username, password, roll, sectionId);
                }
                JOptionPane.showMessageDialog(this, "Import completed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onExportData() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            ReportService rs = new ReportService(dataStore);
            try {
                rs.exportTeachersCsv(new File(dir, "teachers.csv").getAbsolutePath());
                rs.exportStudentsCsv(new File(dir, "students.csv").getAbsolutePath());
                rs.exportSubjectsCsv(new File(dir, "subjects.csv").getAbsolutePath());
                rs.exportSectionsCsv(new File(dir, "sections.csv").getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exported to " + dir.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onResetTeacherPassword() {
        List<Teacher> teachers = adminService.listTeachers();
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No teachers found");
            return;
        }
        JComboBox<String> tBox = new JComboBox<>(teachers.stream().map(t -> t.getName() + " (" + t.getUsername() + ")").toArray(String[]::new));
        javax.swing.JPasswordField password = new javax.swing.JPasswordField();
        Object[] msg = {
                new JLabel("Teacher:", SwingConstants.RIGHT), tBox,
                new JLabel("New Password (leave blank to auto-generate):", SwingConstants.RIGHT), password
        };
        int res = JOptionPane.showConfirmDialog(this, msg, "Reset Teacher Password", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String pwd = new String(password.getPassword());
            boolean generated = false;
            if (pwd.isBlank()) { pwd = generatePassword(); generated = true; }
            try {
                adminService.setTeacherPassword(teachers.get(tBox.getSelectedIndex()).getId(), pwd);
                JOptionPane.showMessageDialog(this, (generated ? "Generated password: " : "Password set: ") + pwd);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onResetStudentPassword() {
        List<Student> students = adminService.listStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students found");
            return;
        }
        JComboBox<String> sBox = new JComboBox<>(students.stream().map(s -> s.getName() + " (" + s.getRollNumber() + " - " + s.getUsername() + ")").toArray(String[]::new));
        javax.swing.JPasswordField password = new javax.swing.JPasswordField();
        Object[] msg = {
                new JLabel("Student:", SwingConstants.RIGHT), sBox,
                new JLabel("New Password (leave blank to auto-generate):", SwingConstants.RIGHT), password
        };
        int res = JOptionPane.showConfirmDialog(this, msg, "Reset Student Password", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String pwd = new String(password.getPassword());
            boolean generated = false;
            if (pwd.isBlank()) { pwd = generatePassword(); generated = true; }
            try {
                adminService.setStudentPassword(students.get(sBox.getSelectedIndex()).getId(), pwd);
                JOptionPane.showMessageDialog(this, (generated ? "Generated password: " : "Password set: ") + pwd);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onAddTeacher() {
        JTextField name = new JTextField();
        JTextField username = new JTextField();
        javax.swing.JPasswordField password = new javax.swing.JPasswordField();
        JTextField email = new JTextField();
        Object[] msg = { new JLabel("Name:", SwingConstants.RIGHT), name,
                new JLabel("Username:", SwingConstants.RIGHT), username,
                new JLabel("Password (leave blank to auto-generate):", SwingConstants.RIGHT), password,
                new JLabel("Email:", SwingConstants.RIGHT), email };
        int res = JOptionPane.showConfirmDialog(this, msg, "Add Teacher", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String pwd = new String(password.getPassword());
                boolean generated = false;
                if (pwd.isBlank()) {
                    pwd = generatePassword();
                    generated = true;
                }
                adminService.addTeacher(name.getText().trim(), username.getText().trim(), pwd, email.getText().trim());
                if (generated) {
                    JOptionPane.showMessageDialog(this, "Teacher added. Generated password: " + pwd);
                } else {
                    JOptionPane.showMessageDialog(this, "Teacher added");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generatePassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%";
        StringBuilder sb = new StringBuilder();
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    private void onAddStudent() {
        JTextField name = new JTextField();
        JTextField username = new JTextField();
        JTextField password = new JTextField();
        JTextField roll = new JTextField();
        List<Section> sections = adminService.listSections();
        JComboBox<String> sectionBox = new JComboBox<>(sections.stream().map(Section::getName).toArray(String[]::new));
        Object[] msg = { new JLabel("Name:", SwingConstants.RIGHT), name,
                new JLabel("Username:", SwingConstants.RIGHT), username,
                new JLabel("Password:", SwingConstants.RIGHT), password,
                new JLabel("Roll No:", SwingConstants.RIGHT), roll,
                new JLabel("Section:", SwingConstants.RIGHT), sectionBox };
        int res = JOptionPane.showConfirmDialog(this, msg, "Add Student", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String sectionId = sections.isEmpty() ? null : sections.get(sectionBox.getSelectedIndex()).getId();
                adminService.addStudent(name.getText().trim(), username.getText().trim(), password.getText().trim(), roll.getText().trim(), sectionId);
                JOptionPane.showMessageDialog(this, "Student added");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onAddSubject() {
        String name = JOptionPane.showInputDialog(this, "Subject name:");
        if (name == null || name.isBlank()) return;
        try {
            adminService.addSubject(name.trim());
            JOptionPane.showMessageDialog(this, "Subject added");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddSection() {
        String name = JOptionPane.showInputDialog(this, "Section name (e.g., CSE-A):");
        if (name == null || name.isBlank()) return;
        try {
            adminService.addSection(name.trim());
            JOptionPane.showMessageDialog(this, "Section added");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddAssignment() {
        List<Teacher> teachers = adminService.listTeachers();
        List<Section> sections = adminService.listSections();
        List<Subject> subjects = adminService.listSubjects();
        if (teachers.isEmpty() || sections.isEmpty() || subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Need at least 1 teacher, 1 section, 1 subject", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JComboBox<String> tBox = new JComboBox<>(teachers.stream().map(Teacher::getName).toArray(String[]::new));
        JComboBox<String> sBox = new JComboBox<>(sections.stream().map(Section::getName).toArray(String[]::new));
        JComboBox<String> subBox = new JComboBox<>(subjects.stream().map(Subject::getName).toArray(String[]::new));
        Object[] msg = { new JLabel("Teacher:", SwingConstants.RIGHT), tBox,
                new JLabel("Section:", SwingConstants.RIGHT), sBox,
                new JLabel("Subject:", SwingConstants.RIGHT), subBox };
        int res = JOptionPane.showConfirmDialog(this, msg, "Create Assignment", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                adminService.addAssignment(
                        teachers.get(tBox.getSelectedIndex()).getId(),
                        sections.get(sBox.getSelectedIndex()).getId(),
                        subjects.get(subBox.getSelectedIndex()).getId()
                );
                JOptionPane.showMessageDialog(this, "Assignment created");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTeachers(DefaultTableModel model) {
        model.setRowCount(0);
        for (Teacher t : adminService.listTeachers()) {
            model.addRow(new Object[] { t.getId(), t.getName(), t.getUsername(), t.getEmail() });
        }
    }

    private void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);
        for (Student s : adminService.listStudents()) {
            model.addRow(new Object[] { s.getId(), s.getName(), s.getUsername(), s.getRollNumber(), s.getSectionId() });
        }
    }

    private void loadSubjects(DefaultTableModel model) {
        model.setRowCount(0);
        for (Subject s : adminService.listSubjects()) {
            model.addRow(new Object[] { s.getId(), s.getName() });
        }
    }

    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);
        for (Section s : adminService.listSections()) {
            model.addRow(new Object[] { s.getId(), s.getName() });
        }
    }
}


