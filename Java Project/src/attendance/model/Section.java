package attendance.model;

import java.io.Serializable;
import java.util.Objects;

public class Section implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name; // e.g., CSE-A

    public Section() {}

    public Section(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section that = (Section) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}


