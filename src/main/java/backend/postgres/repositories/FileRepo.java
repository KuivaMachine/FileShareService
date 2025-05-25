package backend.postgres.repositories;

import backend.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileRepo {
    private static final Logger log = LoggerFactory.getLogger(FileRepo.class);
    private final DataSource dataSource;

    public FileRepo(DataSource dataSource) {
        this.dataSource = dataSource;
        createFilesTable();
    }

    public void executeSql(String sql) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public void createFilesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS files (id VARCHAR PRIMARY KEY, user_id VARCHAR REFERENCES users(id), name VARCHAR NOT NULL DEFAULT 'unknown', file_data BYTEA NOT NULL, size INT NOT NULL DEFAULT 0, created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, last_downloaded TIMESTAMP);";
        executeSql(sql);
    }


    public File findById(String id) throws SQLException {
        String sql = "SELECT * FROM files WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                File file = new File(
                        rs.getString("id"),
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getBytes("file_data"),
                        rs.getInt("size"),
                        rs.getTimestamp("created"),
                        Timestamp.valueOf(LocalDateTime.now()));
                String updateLastDownloaded = String.format("UPDATE files SET last_downloaded = '%s' WHERE id = '%s'", file.getLast_downloaded(), file.getId());
                executeSql(updateLastDownloaded);
                return file;
            }
            return null;
        }
    }

    public void saveFile(File file) {
        String sql = "INSERT INTO files VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, file.getId());
            stmt.setString(2, file.getUserId());
            stmt.setString(3, file.getFile_name());
            stmt.setBytes(4, file.getFile_data());
            stmt.setInt(5, file.getSize());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(7, file.getLast_downloaded());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public void cleanupExpiredFiles() {
        String sql = "DELETE FROM files WHERE last_downloaded IS NOT NULL AND (CURRENT_DATE - last_downloaded::date) > 30;";
        executeSql(sql);
    }

    public List<File> getAllFilesByUserId(String userId) {
        String sql = String.format("SELECT * FROM files WHERE user_id = '%s'", userId);
        List<File> files = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                File file = new File(
                        rs.getString("id"),
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getBytes("file_data"),
                        rs.getInt("size"),
                        rs.getTimestamp("created"),
                        rs.getTimestamp("last_downloaded"));
                files.add(file);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return files;
    }
}