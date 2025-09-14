package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {

    private static final String URL = "jdbc:sqlite:database.db";
    private static Connection conn;

    private SQLiteConnection() {}

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver SQLite não encontrado!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            synchronized (SQLiteConnection.class) {
                if (conn == null || conn.isClosed()) {
                    conn = DriverManager.getConnection(URL);
                }
            }
        }
        return conn;
    }
}