package service;

import entities.Dia;
import entities.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class RelatorioService {

    public String gerarRelatorioMarkdown(Dia dia) {
        StringBuilder md = new StringBuilder();

        md.append("# Relatório do Dia ").append(dia.getDataFormatada()).append("\n\n");

        md.append("## Horários\n");
        md.append("- Início do trabalho: ").append(formatHora(dia.getInicioTrabalho())).append("\n");
        md.append("- Início do almoço: ").append(formatHora(dia.getInicioAlmoco())).append("\n");
        md.append("- Fim do almoço: ").append(formatHora(dia.getFimAlmoco())).append("\n");
        md.append("- Fim do trabalho: ").append(formatHora(dia.getFimTrabalho())).append("\n");
        md.append("- Total de horas trabalhadas: ").append(dia.getHorasTrabalhadas()).append("\n\n");

        md.append("## Tarefas\n");
        if (dia.getTarefas().isEmpty()) {
            md.append("_Nenhuma tarefa registrada._\n");
        } else {
            for (Task task : dia.getTarefas()) {
                md.append("- **").append(task.getDescricao()).append("** ");
                md.append(" [").append(task.getCategoria() != null ? task.getCategoria() : "N/A").append("]");
                md.append(" [").append(task.getCooperativa() != null && !task.getCooperativa().isEmpty() ? task.getCooperativa() : "N/A").append("]");
                if (task.getDuracaoMin() != null) {
                    md.append(" [").append(task.getDuracaoHora()).append("]");
                }
                md.append("\n");
            }
        }

        try {
            Path pasta = Paths.get("relatorios");
            Files.createDirectories(pasta);

            String nomeArquivo = "relatorio_" + dia.getDataParaArquivo() + ".md";
            Files.writeString(pasta.resolve(nomeArquivo), md);

            System.out.println("Relatório salvo com sucesso em: " + pasta.resolve(nomeArquivo).toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatório: " + e.getMessage());
        }


        return md.toString();
    }

    private String formatHora(java.time.LocalTime hora) {
        return hora == null ? "-" : hora.toString();
    }
}
