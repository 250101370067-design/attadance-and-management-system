package attendance.service;

import attendance.model.AttendanceSession;
import attendance.model.Section;
import attendance.model.Student;
import attendance.model.Subject;
import attendance.model.Teacher;
import attendance.util.CsvUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private final DataStore dataStore;

    public ReportService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void exportTeachersCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Teacher t : dataStore.readTeachers()) rows.add(new String[] { t.getId(), t.getName(), t.getUsername(), t.getEmail() });
        CsvUtil.writeCsv(path, new String[] { "id", "name", "username", "email" }, rows);
    }

    public void exportStudentsCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Student s : dataStore.readStudents()) rows.add(new String[] { s.getId(), s.getName(), s.getUsername(), s.getRollNumber(), s.getSectionId() });
        CsvUtil.writeCsv(path, new String[] { "id", "name", "username", "roll", "sectionId" }, rows);
    }

    public void exportSubjectsCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Subject s : dataStore.readSubjects()) rows.add(new String[] { s.getId(), s.getName() });
        CsvUtil.writeCsv(path, new String[] { "id", "name" }, rows);
    }

    public void exportSectionsCsv(String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Section s : dataStore.readSections()) rows.add(new String[] { s.getId(), s.getName() });
        CsvUtil.writeCsv(path, new String[] { "id", "name" }, rows);
    }

    public void exportAttendanceBySubjectCsv(String subjectId, String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (AttendanceSession s : dataStore.readAttendanceForSubject(subjectId)) {
            for (String studentId : s.getStudentPresentById().keySet()) {
                rows.add(new String[] { s.getDateTime().toString(), s.getSectionId(), s.getSubjectId(), s.getTeacherId(), studentId, s.getStudentPresentById().get(studentId) ? "Present" : "Absent" });
            }
        }
        CsvUtil.writeCsv(path, new String[] { "date", "sectionId", "subjectId", "teacherId", "studentId", "status" }, rows);
    }

    public void exportStudentReportCsv(String studentId, String path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Subject subject : dataStore.readSubjects()) {
            int total = 0, attended = 0;
            for (AttendanceSession s : dataStore.readAttendanceForSubject(subject.getId())) {
                if (s.getStudentPresentById().containsKey(studentId)) {
                    total++;
                    if (Boolean.TRUE.equals(s.getStudentPresentById().get(studentId))) attended++;
                }
            }
            rows.add(new String[] { subject.getName(), Integer.toString(attended), Integer.toString(total) });
        }
        CsvUtil.writeCsv(path, new String[] { "subject", "attended", "total" }, rows);
    }
}


