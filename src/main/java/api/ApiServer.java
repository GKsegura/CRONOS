package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import repository.DiaRepository;
import spark.Spark;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ApiServer {

    public static void start() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (com.google.gson.JsonSerializer<LocalDate>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE)))
                .registerTypeAdapter(LocalTime.class, (com.google.gson.JsonSerializer<LocalTime>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_TIME)))
                .setPrettyPrinting()
                .create();

        DiaRepository repo = new DiaRepository();

        Spark.port(8080);

        Spark.get("/api/dias", (req, res) -> {
            res.type("application/json");
            try {
                return gson.toJson(repo.findAll());
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\":\"" + e.getMessage() + "\"}";
            }
        });

        Spark.post("/api/dias", (req, res) -> {
            res.type("application/json");
            var dia = gson.fromJson(req.body(), entities.Dia.class);
            repo.insertDia(dia);
            return gson.toJson(dia);
        });

        System.out.println("API rodando em http://localhost:8080");
    }
}
