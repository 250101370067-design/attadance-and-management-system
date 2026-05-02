package attendance.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // unique session id
    private String sectionId;
    private String subjectId;
    private String teacherId;
    private LocalDateTime dateTime;
    private Map<String, Boolean> studentPresentById = new LinkedHashMap<>();

    public AttendanceSession() {}

    public AttendanceSession(String id, String sectionId, String subjectId, String teacherId, LocalDateTime dateTime) {
        this.id = id;
        this.sectionId = sectionId;
        this.subjectId = subjectId;
        this.teacherId = teacherId;
        this.dateTime = dateTime;
    }

    public String getId() { return id; }
    public String getSectionId() { return sectionId; }
    public String getSubjectId() { return subjectId; }
    public String getTeacherId() { return teacherId; }
    public LocalDateTime getDateTime() { return dateTime; }
    public Map<String, Boolean> getStudentPresentById() { return studentPresentById; }

    public void mark(String studentId, boolean present) {
        studentPresentById.put(studentId, present);
    }
}


