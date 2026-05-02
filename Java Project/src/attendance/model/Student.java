package attendance.model;

public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String rollNumber;
    private String sectionId; // assigned section

    public Student() {}

    public Student(String id, String name, String username, String passwordHash, String rollNumber, String sectionId) {
        super(id, name, username, passwordHash);
        this.rollNumber = rollNumber;
        this.sectionId = sectionId;
    }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
}


