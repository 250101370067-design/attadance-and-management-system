package attendance.ui;

import attendance.model.Assignment;
import attendance.model.AttendanceSession;
import attendance.model.Section;
import attendance.model.Student;
import attendance.model.Subject;
import attendance.model.Teacher;
import attendance.service.DataStore;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.JFileChooser;
import attendance.service.ReportService;
import java.io.File;
import java.io.IOException;

public class TeacherDashboard extends JFrame {
    private final JFrame parentToReturn;
    private final DataStore dataStore;
    private final Teacher teacher;

    private JComboBox<String> assignmentBox;

    public TeacherDashboard(JFrame parentToReturn, DataStore dataStore, Teacher teacher) {
        super("Teacher Dashboard");
        this.parentToReturn = parentToReturn;
        this.dataStore = dataStore;
        this.teacher = teacher;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        List<Assignment> myAssignments = new ArrayList<>();
        for (Assignment a : dataStore.readAssignments()) if (a.getTeacherId().equals(teacher.getId())) myAssignments.add(a);

        assignmentBox = new JComboBox<>(myAssignments.stream().map(a -> labelFor(a)).toArray(String[]::new));
        JButton markBtn = new JButton("Mark Attendance");
        markBtn.addActionListener(e -> onMark(myAssignments));
        JButton exportBtn = new JButton("Export Attendance");
        exportBtn.addActionListener(e -> onExport(myAssignments));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); parentToReturn.setVisible(true); });

        JPanel top = new JPanel(new GridLayout(1, 4, 8, 8));
        top.add(assignmentBox);
        top.add(markBtn);
        top.add(exportBtn);
        top.add(logout);
        root.add(top, BorderLayout.NORTH);

        JTable sessionsTable = new JTable(new DefaultTableModel(new Object[] { "Date", "Section", "Subject" }, 0));
        loadRecentSessions((DefaultTableModel) sessionsTable.getModel(), myAssignments);
        root.add(new JScrollPane(sessionsTable), BorderLayout.CENTER);

        add(root);
    }

    private void onExport(List<Assignment> myAssignments) {
        if (myAssignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments");
            return;
        }
        Assignment a = myAssignments.get(assignmentBox.getSelectedIndex());
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                new ReportService(dataStore).exportAttendanceBySubjectCsv(a.getSubjectId(), file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exported to " + file.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String labelFor(Assignment a) {
        Section sec = dataStore.readSections().stream().filter(s -> s.getId().equals(a.getSectionId())).findFirst().orElse(null);
        Subject sub = dataStore.readSubjects().stream().filter(s -> s.getId().equals(a.getSubjectId())).findFirst().orElse(null);
        String sName = sec == null ? a.getSectionId() : sec.getName();
        String subName = sub == null ? a.getSubjectId() : sub.getName();
        return sName + " - " + subName;
    }

    private void onMark(List<Assignment> myAssignments) {
        if (myAssignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments");
            return;
        }
        Assignment a = myAssignments.get(assignmentBox.getSelectedIndex());
        List<Student> students = new ArrayList<>();
        for (Student s : dataStore.readStudents()) if (a.getSectionId().equals(s.getSectionId())) students.add(s);

        JPanel panel = new JPanel(new GridLayout(Math.max(1, students.size()), 2, 6, 6));
        List<JCheckBox> boxes = new ArrayList<>();
        for (Student s : students) {
            JCheckBox cb = new JCheckBox(s.getName() + " (" + s.getRollNumber() + ")", true);
            boxes.add(cb);
            panel.add(new JLabel(s.getRollNumber()));
            panel.add(cb);
        }
        int res = JOptionPane.showConfirmDialog(this, new JScrollPane(panel), "Mark Attendance", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            AttendanceSession session = new AttendanceSession(UUID.randomUUID().toString(), a.getSectionId(), a.getSubjectId(), a.getTeacherId(), LocalDateTime.now());
            for (int i = 0; i < students.size(); i++) {
                session.mark(students.get(i).getId(), boxes.get(i).isSelected());
            }
            List<AttendanceSession> sessions = dataStore.readAttendanceForSubject(a.getSubjectId());
            sessions.add(session);
            dataStore.writeAttendanceForSubject(a.getSubjectId(), sessions);
            JOptionPane.showMessageDialog(this, "Saved");
        }
    }

    private void loadRecentSessions(DefaultTableModel model, List<Assignment> myAssignments) {
        model.setRowCount(0);
        for (Assignment a : myAssignments) {
            for (AttendanceSession s : dataStore.readAttendanceForSubject(a.getSubjectId())) {
                Section sec = dataStore.readSections().stream().filter(x -> x.getId().equals(a.getSectionId())).findFirst().orElse(null);
                Subject sub = dataStore.readSubjects().stream().filter(x -> x.getId().equals(a.getSubjectId())).findFirst().orElse(null);
                model.addRow(new Object[] { s.getDateTime(), sec == null ? a.getSectionId() : sec.getName(), sub == null ? a.getSubjectId() : sub.getName() });
            }
        }
    }
}


