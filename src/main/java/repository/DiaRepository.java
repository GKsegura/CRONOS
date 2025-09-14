package repository;

import database.SQLiteConnection;
import entities.Categoria;
import entities.Dia;
import entities.Task;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DiaRepository {

    public Dia insertDia(Dia dia) {
        String sql = "INSERT INTO dias(data, inicioTrabalho, fimTrabalho, inicioAlmoco, fimAlmoco) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dia.getDataFormatada());
            ps.setString(2, dia.getInicioTrabalho() != null ? dia.getInicioTrabalho().toString() : null);
            ps.setString(3, dia.getFimTrabalho() != null ? dia.getFimTrabalho().toString() : null);
            ps.setString(4, dia.getInicioAlmoco() != null ? dia.getInicioAlmoco().toString() : null);
            ps.setString(5, dia.getFimAlmoco() != null ? dia.getFimAlmoco().toString() : null);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                dia.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dia;
    }

    public void updateDia(Dia dia) {
        String sql = "UPDATE dias SET inicioTrabalho=?, fimTrabalho=?, inicioAlmoco=?, fimAlmoco=? WHERE id=?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dia.getInicioTrabalho() != null ? dia.getInicioTrabalho().toString() : null);
            ps.setString(2, dia.getFimTrabalho() != null ? dia.getFimTrabalho().toString() : null);
            ps.setString(3, dia.getInicioAlmoco() != null ? dia.getInicioAlmoco().toString() : null);
            ps.setString(4, dia.getFimAlmoco() != null ? dia.getFimAlmoco().toString() : null);
            ps.setLong(5, dia.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Task insertTask(Task task, long diaId) {
        String sql = "INSERT INTO tarefas(descricao, categoria, cooperativa, duracaoMin, dia_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, task.getDescricao());
            ps.setString(2, task.getCategoria() != null ? task.getCategoria().name() : null);
            ps.setString(3, task.getCooperativa());
            ps.setObject(4, task.getDuracaoMin(), Types.BIGINT);
            ps.setLong(5, diaId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                task.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    public void updateTask(Task task) {
        String sql = "UPDATE tarefas SET descricao=?, categoria=?, cooperativa=?, duracaoMin=? WHERE id=?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, task.getDescricao());
            ps.setString(2, task.getCategoria() != null ? task.getCategoria().name() : null);
            ps.setString(3, task.getCooperativa());
            ps.setObject(4, task.getDuracaoMin(), Types.BIGINT);
            ps.setLong(5, task.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTaskById(long taskId) {
        String sql = "DELETE FROM tarefas WHERE id=?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Dia> findAll() {
        List<Dia> dias = new ArrayList<>();
        String sql = "SELECT * FROM dias";
        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dia dia = new Dia();
                dia.setId(rs.getLong("id"));
                dia.setData(rs.getString("data"));
                dia.setInicioTrabalho(rs.getString("inicioTrabalho") != null ? LocalTime.parse(rs.getString("inicioTrabalho")) : null);
                dia.setFimTrabalho(rs.getString("fimTrabalho") != null ? LocalTime.parse(rs.getString("fimTrabalho")) : null);
                dia.setInicioAlmoco(rs.getString("inicioAlmoco") != null ? LocalTime.parse(rs.getString("inicioAlmoco")) : null);
                dia.setFimAlmoco(rs.getString("fimAlmoco") != null ? LocalTime.parse(rs.getString("fimAlmoco")) : null);

                dias.add(dia);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Dia d : dias) {
            d.getTarefas().addAll(findTasksByDia(d.getId()));
        }

        return dias;
    }

    private List<Task> findTasksByDia(long diaId) {
        List<Task> tarefas = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE dia_id=?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, diaId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task t = new Task(
                        rs.getString("descricao"),
                        rs.getString("categoria") != null ? Categoria.valueOf(rs.getString("categoria")) : null,
                        rs.getString("cooperativa"),
                        rs.getObject("duracaoMin") != null ? rs.getLong("duracaoMin") : null
                );
                t.setId(rs.getLong("id"));
                tarefas.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarefas;
    }
}
