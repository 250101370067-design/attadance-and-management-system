package attendance.service;

import attendance.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    private final Path rootDir;
    private final Path attendanceDir;

    private final Path adminFile;
    private final Path teachersFile;
    private final Path studentsFile;
    private final Path subjectsFile;
    private final Path sectionsFile;
    private final Path assignmentsFile;

    public DataStore(String root) {
        this.rootDir = Paths.get(root);
        this.attendanceDir = rootDir.resolve("attendance");
        this.adminFile = rootDir.resolve("admin.dat");
        this.teachersFile = rootDir.resolve("teachers.dat");
        this.studentsFile = rootDir.resolve("students.dat");
        this.subjectsFile = rootDir.resolve("subjects.dat");
        this.sectionsFile = rootDir.resolve("sections.dat");
        this.assignmentsFile = rootDir.resolve("assignments.dat");
        init();
    }

    private void init() {
        try {
            Files.createDirectories(rootDir);
            Files.createDirectories(attendanceDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize data directories", e);
        }
    }

    public Path getAttendanceDir() { return attendanceDir; }

    // Generic read/write helpers
    @SuppressWarnings("unchecked")
    private <T> List<T> readList(Path file) {
        if (!Files.exists(file)) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof List) return (List<T>) obj;
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private <T> void writeList(Path file, List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(list);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + file, e);
        }
    }

    public List<Admin> readAdmins() { return readList(adminFile); }
    public void writeAdmins(List<Admin> admins) { writeList(adminFile, admins); }

    public List<Teacher> readTeachers() { return readList(teachersFile); }
    public void writeTeachers(List<Teacher> teachers) { writeList(teachersFile, teachers); }

    public List<Student> readStudents() { return readList(studentsFile); }
    public void writeStudents(List<Student> students) { writeList(studentsFile, students); }

    public List<Subject> readSubjects() { return readList(subjectsFile); }
    public void writeSubjects(List<Subject> subjects) { writeList(subjectsFile, subjects); }

    public List<Section> readSections() { return readList(sectionsFile); }
    public void writeSections(List<Section> sections) { writeList(sectionsFile, sections); }

    public List<Assignment> readAssignments() { return readList(assignmentsFile); }
    public void writeAssignments(List<Assignment> assignments) { writeList(assignmentsFile, assignments); }

    // Attendance per subject file: subject_<subjectId>.dat
    @SuppressWarnings("unchecked")
    public List<AttendanceSession> readAttendanceForSubject(String subjectId) {
        Path f = attendanceDir.resolve("subject_" + subjectId + ".dat");
        return readList(f);
    }

    public void writeAttendanceForSubject(String subjectId, List<AttendanceSession> sessions) {
        Path f = attendanceDir.resolve("subject_" + subjectId + ".dat");
        writeList(f, sessions);
    }
}


