package attendance.model;

import java.io.Serializable;
import java.util.Objects;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;

    public Subject() {}

    public Subject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        Subject that = (Subject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}


