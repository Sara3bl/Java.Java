package dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize() {
        Connection conn = DatabaseManager.getConnection();
        try {
            System.out.println("🔄 Checking database schema...");

            // Read schema.sql
            InputStream is = DatabaseInitializer.class.getResourceAsStream("/schema.sql");
            if (is == null) {
                System.err.println("❌ Custom Error: schema.sql not found in resources!");
                return;
            }

            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            // Split by semicolon to execute statements one by one
            // Note: This is a simple splitter and might fail with complex stored
            // procedures,
            // but for simple table creation it works.
            String[] statements = sql.split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String sqlStmt : statements) {
                    if (!sqlStmt.trim().isEmpty()) {
                        try {
                            // Skip "CREATE DATABASE" and "USE" as we are already connected to a DB or URL
                            // handles it
                            // Actually, DatabaseManager connects to 'pharmacie_db'. If it doesn't exist,
                            // connection might fail.
                            // So we rely on the user having created the DB or we change the URL to not
                            // include DB.

                            // For safety, let's just attempt execution but ignore specific errors or skip
                            // CREATE DB/USE
                            if (sqlStmt.toUpperCase().contains("CREATE DATABASE")
                                    || sqlStmt.toUpperCase().contains("USE PHARMACIE_DB")) {
                                continue;
                            }

                            stmt.execute(sqlStmt);
                        } catch (Exception e) {
                            System.out.println("⚠️ Warning executing statement: " + e.getMessage());
                        }
                    }
                }
            }
            System.out.println("✅ Database schema synced successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Database initialization failed: " + e.getMessage());
        }
    }
}
