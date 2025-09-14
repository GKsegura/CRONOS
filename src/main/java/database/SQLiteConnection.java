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
                    //LOGGER.info("Conexão SQLite aberta com sucesso!");
                }
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                //LOGGER.info("Conexão SQLite fechada com sucesso!");
            } catch (SQLException e) {
               //LOGGER.log(Level.SEVERE, "Erro ao fechar a conexão SQLite", e);
            } finally {
                conn = null;
            }
        }
    }
}