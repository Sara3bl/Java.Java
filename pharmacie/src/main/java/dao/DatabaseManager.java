package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseManager {

    private static Connection connection;

    private DatabaseManager() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream in = DatabaseManager.class
                        .getResourceAsStream("/database.properties");
                props.load(in);

                connection = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.username"),
                        props.getProperty("db.password")
                );

                System.out.println("✅ JDBC connection successful");

            } catch (Exception e) {
                throw new RuntimeException("❌ JDBC connection failed", e);
            }
        }
        return connection;
    }
}
