package attendance.ui;

import attendance.model.Assignment;
import attendance.model.AttendanceSession;
import attendance.model.Section;
import attendance.model.Student;
import attendance.model.Subject;
import attendance.service.DataStore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import attendance.service.ReportService;
import java.io.File;
import java.io.IOException;

public class StudentDashboard extends JFrame {
    private final JFrame parentToReturn;
    private final DataStore dataStore;
    private final Student student;

    public StudentDashboard(JFrame parentToReturn, DataStore dataStore, Student student) {
        super("Student Dashboard");
        this.parentToReturn = parentToReturn;
        this.dataStore = dataStore;
        this.student = student;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); parentToReturn.setVisible(true); });
        JButton export = new JButton("Export My Report");
        export.addActionListener(e -> onExport());
        JPanel top = new JPanel(new BorderLayout());
        top.add(logout, BorderLayout.WEST);
        top.add(export, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        JTable daily = new JTable(new DefaultTableModel(new Object[] { "Date", "Subject", "Present" }, 0));
        JTable bySubject = new JTable(new DefaultTableModel(new Object[] { "Subject", "Attended", "Total" }, 0));

        loadDaily((DefaultTableModel) daily.getModel());
        loadSubjectSummary((DefaultTableModel) bySubject.getModel());

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(new JScrollPane(daily), BorderLayout.CENTER);
        center.add(new JScrollPane(bySubject), BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);

        add(root);
    }

    private void onExport() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                new ReportService(dataStore).exportStudentReportCsv(student.getId(), file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Exported to " + file.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadDaily(DefaultTableModel model) {
        model.setRowCount(0);
        for (Subject subject : dataStore.readSubjects()) {
            List<AttendanceSession> sessions = dataStore.readAttendanceForSubject(subject.getId());
            for (AttendanceSession s : sessions) {
                Boolean present = s.getStudentPresentById().get(student.getId());
                if (present != null) {
                    model.addRow(new Object[] { s.getDateTime(), subject.getName(), present ? "Present" : "Absent" });
                }
            }
        }
    }

    private void loadSubjectSummary(DefaultTableModel model) {
        model.setRowCount(0);
        Map<String, int[]> counters = new HashMap<>(); // subjectId -> [attended, total]
        for (Subject subject : dataStore.readSubjects()) {
            List<AttendanceSession> sessions = dataStore.readAttendanceForSubject(subject.getId());
            int attended = 0, total = 0;
            for (AttendanceSession s : sessions) {
                if (s.getStudentPresentById().containsKey(student.getId())) {
                    total++;
                    if (Boolean.TRUE.equals(s.getStudentPresentById().get(student.getId()))) attended++;
                }
            }
            counters.put(subject.getId(), new int[] { attended, total });
            model.addRow(new Object[] { subject.getName(), attended, total });
        }
    }
}


