package entities;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dia {
    private Long id;
    private LocalDate data;
    private LocalTime inicioTrabalho;
    private LocalTime fimTrabalho;
    private LocalTime inicioAlmoco;
    private LocalTime fimAlmoco;
    private final List<Task> tarefas = new ArrayList<>();

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Dia() {}

    public Dia(LocalDate data, LocalTime inicioTrabalho, LocalTime fimTrabalho,
               LocalTime inicioAlmoco, LocalTime fimAlmoco) {
        this.data = data;
        this.inicioTrabalho = inicioTrabalho;
        this.fimTrabalho = fimTrabalho;
        this.inicioAlmoco = inicioAlmoco;
        this.fimAlmoco = fimAlmoco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static DateTimeFormatter getFmtHora() {
        return FMT_HORA;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data = LocalDate.parse(data, FMT_DATA);
    }

    public String getDataFormatada() {
        return data != null ? data.format(FMT_DATA) : "";
    }

    public LocalTime getInicioTrabalho() {
        return inicioTrabalho;
    }

    public void setInicioTrabalho(LocalTime inicioTrabalho) {
        this.inicioTrabalho = inicioTrabalho;
    }

    public LocalTime getFimTrabalho() {
        return fimTrabalho;
    }

    public void setFimTrabalho(LocalTime fimTrabalho) {
        this.fimTrabalho = fimTrabalho;
    }

    public LocalTime getInicioAlmoco() {
        return inicioAlmoco;
    }

    public void setInicioAlmoco(LocalTime inicioAlmoco) {
        this.inicioAlmoco = inicioAlmoco;
    }

    public LocalTime getFimAlmoco() {
        return fimAlmoco;
    }

    public void setFimAlmoco(LocalTime fimAlmoco) {
        this.fimAlmoco = fimAlmoco;
    }

    public List<Task> getTarefas() {
        return tarefas;
    }

    public void addTarefa(Task tarefa) {
        tarefas.add(tarefa);
        Collections.sort(tarefas);
    }

    public String getDataParaArquivo() {
        return data != null ? data.format(DateTimeFormatter.ISO_LOCAL_DATE) : "sem-data";
    }

    public String getHorasTrabalhadas() {
        if (inicioTrabalho == null || fimTrabalho == null) return "00:00";

        long totalMin = Duration.between(inicioTrabalho, fimTrabalho).toMinutes();
        long almocoMin = (inicioAlmoco != null && fimAlmoco != null)
                ? Duration.between(inicioAlmoco, fimAlmoco).toMinutes()
                : 0;

        long liquido = totalMin - almocoMin;
        long horas = liquido / 60;
        long minutos = liquido % 60;

        return String.format("%02d:%02d", horas, minutos);
    }

    public static String formatarHora(LocalTime hora) {
        return (hora == null) ? "-" : hora.format(FMT_HORA);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data: ").append(getDataFormatada()).append("\n");
        sb.append("Início do Trabalho: ").append(formatarHora(inicioTrabalho)).append("\n");
        sb.append("Fim do Trabalho: ").append(formatarHora(fimTrabalho)).append("\n");
        sb.append("Início do Almoço: ").append(formatarHora(inicioAlmoco)).append("\n");
        sb.append("Fim do Almoço: ").append(formatarHora(fimAlmoco)).append("\n");
        sb.append("Horas Trabalhadas: ").append(getHorasTrabalhadas()).append("\n");
        sb.append("Tarefas:\n");
        for (Task t : tarefas) {
            sb.append(" - ").append(t).append("\n");
        }
        return sb.toString();
    }
}
