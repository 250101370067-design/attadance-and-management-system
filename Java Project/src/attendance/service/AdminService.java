package attendance.service;

import attendance.model.Assignment;
import attendance.model.Section;
import attendance.model.Student;
import attendance.model.Subject;
import attendance.model.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdminService {
    private final DataStore dataStore;

    public AdminService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    // Teachers
    public Teacher addTeacher(String name, String username, String password, String email) {
        List<Teacher> teachers = new ArrayList<>(dataStore.readTeachers());
        ensureUniqueUsername(username, teachers.stream().map(Teacher::getUsername).toList());
        Teacher t = new Teacher(UUID.randomUUID().toString(), name, username, AuthService.hashPassword(password), email);
        teachers.add(t);
        dataStore.writeTeachers(teachers);
        return t;
    }

    public List<Teacher> listTeachers() { return dataStore.readTeachers(); }

    public void setTeacherPassword(String teacherId, String newPassword) {
        List<Teacher> teachers = new ArrayList<>(dataStore.readTeachers());
        boolean updated = false;
        for (Teacher t : teachers) {
            if (t.getId().equals(teacherId)) {
                t.setPasswordHash(AuthService.hashPassword(newPassword));
                updated = true;
                break;
            }
        }
        if (!updated) throw new IllegalArgumentException("Teacher not found");
        dataStore.writeTeachers(teachers);
    }

    // Students
    public Student addStudent(String name, String username, String password, String rollNumber, String sectionId) {
        List<Student> students = new ArrayList<>(dataStore.readStudents());
        ensureUniqueUsername(username, students.stream().map(Student::getUsername).toList());
        ensureUniqueRoll(rollNumber, students.stream().map(Student::getRollNumber).toList());
        Student s = new Student(UUID.randomUUID().toString(), name, username, AuthService.hashPassword(password), rollNumber, sectionId);
        students.add(s);
        dataStore.writeStudents(students);
        return s;
    }

    public List<Student> listStudents() { return dataStore.readStudents(); }

    public void setStudentPassword(String studentId, String newPassword) {
        List<Student> students = new ArrayList<>(dataStore.readStudents());
        boolean updated = false;
        for (Student s : students) {
            if (s.getId().equals(studentId)) {
                s.setPasswordHash(AuthService.hashPassword(newPassword));
                updated = true;
                break;
            }
        }
        if (!updated) throw new IllegalArgumentException("Student not found");
        dataStore.writeStudents(students);
    }

    // Subjects
    public Subject addSubject(String name) {
        List<Subject> subjects = new ArrayList<>(dataStore.readSubjects());
        boolean dup = subjects.stream().anyMatch(sub -> sub.getName().equalsIgnoreCase(name));
        if (dup) throw new IllegalArgumentException("Subject already exists");
        Subject sub = new Subject(UUID.randomUUID().toString(), name);
        subjects.add(sub);
        dataStore.writeSubjects(subjects);
        return sub;
    }

    public List<Subject> listSubjects() { return dataStore.readSubjects(); }

    // Sections
    public Section addSection(String name) {
        List<Section> sections = new ArrayList<>(dataStore.readSections());
        boolean dup = sections.stream().anyMatch(sec -> sec.getName().equalsIgnoreCase(name));
        if (dup) throw new IllegalArgumentException("Section already exists");
        Section sec = new Section(UUID.randomUUID().toString(), name);
        sections.add(sec);
        dataStore.writeSections(sections);
        return sec;
    }

    public List<Section> listSections() { return dataStore.readSections(); }

    // Assignment: teacher + section + subject
    public Assignment addAssignment(String teacherId, String sectionId, String subjectId) {
        List<Assignment> assignments = new ArrayList<>(dataStore.readAssignments());
        boolean exists = assignments.stream().anyMatch(a -> a.getTeacherId().equals(teacherId) && a.getSectionId().equals(sectionId) && a.getSubjectId().equals(subjectId));
        if (exists) throw new IllegalArgumentException("Assignment already exists");
        Assignment a = new Assignment(UUID.randomUUID().toString(), teacherId, sectionId, subjectId);
        assignments.add(a);
        dataStore.writeAssignments(assignments);
        return a;
    }

    public List<Assignment> listAssignments() { return dataStore.readAssignments(); }

    private void ensureUniqueUsername(String username, List<String> existing) {
        if (existing.stream().anyMatch(u -> u.equalsIgnoreCase(username))) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private void ensureUniqueRoll(String roll, List<String> existing) {
        if (existing.stream().anyMatch(r -> r.equalsIgnoreCase(roll))) {
            throw new IllegalArgumentException("Roll number already exists");
        }
    }
}


