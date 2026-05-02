package attendance.service;

import attendance.model.Admin;
import attendance.model.Student;
import attendance.model.Teacher;
import attendance.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final DataStore dataStore;

    public enum Role { ADMIN, TEACHER, STUDENT }

    public AuthService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFirstRunNoAdmins() {
        return dataStore.readAdmins().isEmpty();
    }

    public Admin createInitialAdmin(String name, String username, String password) {
        List<Admin> admins = new ArrayList<>(dataStore.readAdmins());
        Admin admin = new Admin(UUID.randomUUID().toString(), name, username, hashPassword(password));
        admins.add(admin);
        dataStore.writeAdmins(admins);
        return admin;
    }

    public Optional<User> login(String username, String password, Role role) {
        String hash = hashPassword(password);
        switch (role) {
            case ADMIN:
                return dataStore.readAdmins().stream()
                    .filter(a -> a.getUsername().equals(username) && a.getPasswordHash().equals(hash))
                    .map(a -> (User) a)
                    .findFirst();
            case TEACHER:
                return dataStore.readTeachers().stream()
                    .filter(t -> t.getUsername().equals(username) && t.getPasswordHash().equals(hash))
                    .map(t -> (User) t)
                    .findFirst();
            case STUDENT:
                return dataStore.readStudents().stream()
                    .filter(s -> s.getUsername().equals(username) && s.getPasswordHash().equals(hash))
                    .map(s -> (User) s)
                    .findFirst();
            default:
                return Optional.empty();
        }
    }
}


