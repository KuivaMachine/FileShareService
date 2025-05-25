package backend.postgres.repositories;

import backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

public class UserRepo {
    private static final Logger log = LoggerFactory.getLogger(UserRepo.class);
    private final DataSource dataSource;

    public UserRepo(DataSource dataSource) {
        this.dataSource = dataSource;
        createTable();
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (id VARCHAR PRIMARY KEY, username VARCHAR NOT NULL DEFAULT 'no name', password VARCHAR NOT NULL); ";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

    }

    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        }
    }

    public String authenticate(User user) {
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            String userId = null;
            if (rs.next()) {
                userId = rs.getString("id");
            }

            if (userId != null) {
                return userId;
            } else {
                save(user);
                return user.getId();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            return null;
        }
    }


    public boolean getUser(String userId) {
        String sql = "SELECT id FROM users WHERE username=?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            String result = null;
            if (rs.next()) {
                result = rs.getString("id");
            }
            if (result != null) {
                return true;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return false;
    }

}