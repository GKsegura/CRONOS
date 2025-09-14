package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableCreator {
    private static final Map<String, String> TABLES = new LinkedHashMap<>();
    static {
        TABLES.put("dias", "CREATE TABLE IF NOT EXISTS dias (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "data TEXT NOT NULL," +
                "inicioTrabalho TEXT," +
                "fimTrabalho TEXT," +
                "inicioAlmoco TEXT," +
                "fimAlmoco TEXT);");

        TABLES.put("tarefas", "CREATE TABLE IF NOT EXISTS tarefas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "descricao TEXT NOT NULL," +
                "categoria TEXT," +
                "cooperativa TEXT," +
                "duracaoMin INTEGER," +
                "dia_id INTEGER," +
                "FOREIGN KEY(dia_id) REFERENCES dias(id));");
    }

    public static void criarTabelas() {
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            TABLES.values().forEach(sql -> {
                try { stmt.execute(sql); } catch (SQLException e) { e.printStackTrace(); }
            });

            System.out.println("Tabelas criadas com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
