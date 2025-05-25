package backend.model;
import java.util.UUID;

public class User {
    private final String id;
    private final String username;
    private final String password;


    public User(String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;

    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }



}