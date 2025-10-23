package api;

import com.google.gson.Gson;
import entities.Dia;
import repository.DiaRepository;
import spark.Spark;

public class DiaController {

    private final DiaRepository repo = new DiaRepository();

    public DiaController(Gson gson) {
        Spark.get("/api/dias", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(repo.findAll());
            } catch (Exception e) {
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        Spark.post("/api/dias", (req, res) -> {
            res.type("application/json");
            try {
                Dia dia = gson.fromJson(req.body(), Dia.class);
                System.out.println("JSON recebido: " + req.body());
                System.out.println("Objeto convertido: " + dia);

                repo.insertDia(dia);
                return gson.toJson(dia);
            } catch (Exception e) {
                System.out.println("🔥 ERRO AO CRIAR DIA 🔥");
                e.printStackTrace(); // imprime o erro completo no terminal
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });


        Spark.get("/api/dias/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Dia dia = repo.findById(id);
            if (dia == null) {
                res.status(404);
                return "{\"error\":\"Dia não encontrado\"}";
            }
            return gson.toJson(dia);
        });

        Spark.put("/api/dias/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            Dia dia = gson.fromJson(req.body(), Dia.class);
            dia.setId(id);
            repo.updateDia(dia);
            return gson.toJson(dia);
        });

        Spark.delete("/api/dias/:id", (req, res) -> {
            res.type("application/json");
            long id = Long.parseLong(req.params(":id"));
            boolean deleted = repo.deleteDia(id);
            if (!deleted) {
                res.status(404);
                return "{\"error\":\"Dia não encontrado\"}";
            }
            return "{\"message\":\"Dia removido com sucesso\"}";
        });

        System.out.println("Rotas de dias registradas");
    }
}
