package api;

import com.google.gson.Gson;
import entities.Task;
import repository.DiaRepository;
import spark.Spark;

public class TaskController {

    private final DiaRepository repo = new DiaRepository();

    public TaskController(Gson gson) {
        Spark.post("/api/tarefas/:diaId", (req, res) -> {
            res.type("application/json");
            long diaId = Long.parseLong(req.params(":diaId"));
            Task task = gson.fromJson(req.body(), Task.class);
            Task novaTask = repo.insertTask(task, diaId);
            res.status(201);
            return gson.toJson(novaTask);
        });

        Spark.put("/api/tarefas/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Task task = gson.fromJson(req.body(), Task.class);
            task.setId(id);
            repo.updateTask(task);
            return gson.toJson(task);
        });

        Spark.delete("/api/tarefas/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            repo.deleteTaskById(id);
            return "{\"message\":\"Tarefa removida com sucesso\"}";
        });

        Spark.get("/api/tarefas/:diaId", (req, res) -> {
            res.type("application/json");
            long diaId = Long.parseLong(req.params(":diaId"));
            var dia = repo.findById(diaId);
            if (dia == null) {
                res.status(404);
                return "{\"error\":\"Dia não encontrado\"}";
            }
            return gson.toJson(dia.getTarefas());
        });

        System.out.println("Rotas de tarefas registradas");
    }
}
