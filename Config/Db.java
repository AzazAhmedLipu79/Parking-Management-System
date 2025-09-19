package Config;

import java.sql.*;

public class Db {
    private static final String URL = "jdbc:mysql://localhost:3306/test_chando?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    // Get database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Run a SELECT query
    public static ResultSet executeQuery(String query) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query); // caller must close manually
    }

    // Run UPDATE/DELETE/INSERT without needing ID
    public static int executeUpdate(String query) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(query);
        }
    }

    // INSERT and return generated ID
    public static int executeInsert(String query) throws SQLException {
        int generatedId = -1;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            int affectedRows = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        }
        return generatedId;
    }
}
