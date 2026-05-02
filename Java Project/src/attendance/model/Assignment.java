package attendance.model;

import java.io.Serializable;
import java.util.Objects;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // unique assignment id
    private String teacherId;
    private String sectionId;
    private String subjectId;

    public Assignment() {}

    public Assignment(String id, String teacherId, String sectionId, String subjectId) {
        this.id = id;
        this.teacherId = teacherId;
        this.sectionId = sectionId;
        this.subjectId = subjectId;
    }

    public String getId() { return id; }
    public String getTeacherId() { return teacherId; }
    public String getSectionId() { return sectionId; }
    public String getSubjectId() { return subjectId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}


