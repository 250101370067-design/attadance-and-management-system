package attendance.model;

public class Teacher extends User {
    private static final long serialVersionUID = 1L;

    private String email;

    public Teacher() {}

    public Teacher(String id, String name, String username, String passwordHash, String email) {
        super(id, name, username, passwordHash);
        this.email = email;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}


