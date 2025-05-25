package backend.postgres;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PostgresConfig {

        private static final String DB_URL = "jdbc:postgresql://localhost:5445/file_sharing_service";
        private static final String DB_USER = "file_sharing_service";
        private static final String DB_PASSWORD = "offer_from_doczilla";

        public static DataSource createDataSource() {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setMaximumPoolSize(10);
            return new HikariDataSource(config);
        }
    }
