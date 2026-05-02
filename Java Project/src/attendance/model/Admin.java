package attendance.model;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin() {}

    public Admin(String id, String name, String username, String passwordHash) {
        super(id, name, username, passwordHash);
    }
}


