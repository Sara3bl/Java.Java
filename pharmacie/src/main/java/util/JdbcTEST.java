package util;

import dao.DatabaseManager;
import java.sql.Connection;

public class JdbcTEST {
    public static void main(String[] args) {
        Connection conn = DatabaseManager.getConnection();
        if (conn != null) {
            System.out.println("JDBC is connected!");
        }
    }
}
