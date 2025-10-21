package service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import entities.Dia;
import entities.Task;
import entities.Categoria;

public class RelatorioService {
    DiaService diaService = new DiaService();
    public void gerarRelatorioMarkdown(Dia dia) {
        StringBuilder md = new StringBuilder();

        md.append("# Relatório do Dia ").append(dia.getDataFormatada()).append("\n\n");

        md.append("## Horários\n");
        md.append("- Início do trabalho: ").append(formatHora(dia.getInicioTrabalho())).append("\n");
        md.append("- Início do almoço: ").append(formatHora(dia.getInicioAlmoco())).append("\n");
        md.append("- Fim do almoço: ").append(formatHora(dia.getFimAlmoco())).append("\n");
        md.append("- Fim do trabalho: ").append(formatHora(dia.getFimTrabalho())).append("\n");
        md.append("- Total de horas trabalhadas: ").append(dia.getHorasTrabalhadas()).append("\n\n");

        md.append("## Tarefas por Categoria\n");

        if (dia.getTarefas().isEmpty()) {
            md.append("_Nenhuma tarefa registrada._\n");
        } else {
            Map<Categoria, List<Task>> tarefasPorCategoria = new HashMap<>();
            for (Task t : dia.getTarefas()) {
                tarefasPorCategoria.computeIfAbsent(t.getCategoria(), k -> new ArrayList<>()).add(t);
            }

            List<Categoria> ordem = List.of(Categoria.SUPORTE, Categoria.REUNIAO, Categoria.SUPORTE_HORAS_PAGAS, Categoria.DESPESA_GERAL);

            for (Categoria categoria : ordem) {
                List<Task> tarefas = tarefasPorCategoria.get(categoria);
                if (tarefas == null || tarefas.isEmpty()) continue;

                String titulo = categoria.toString();
                if (categoria == Categoria.REUNIAO) {
                    long totalMinutos = tarefas.stream().filter(t -> t.getDuracaoMin() != null).mapToLong(Task::getDuracaoMin).sum();
                    titulo += " [" + formatarDuracao(totalMinutos) + "]";
                }

                md.append("### ").append(titulo).append("\n");

                for (Task t : tarefas) {
                    md.append("- ").append(t.getDescricao()).append(" [").append(t.getCategoria()).append("]").append(" [").append(t.getCliente()).append("]");
                    if (t.getDuracaoMin() != null) {
                        md.append(" [").append(formatarDuracao(t.getDuracaoMin())).append("]");
                    }
                    md.append("\n");
                }
                md.append("\n");
            }
        }

        try {
            Path pasta = Paths.get("relatorios");
            Files.createDirectories(pasta);

            LocalDate dataDoDia = dia.getData();
            String diaSemana = dataDoDia.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

            String nomeArquivoMD = "relatorio_" + dia.getDataParaArquivo() + "_" + diaSemana + ".md";
            Path caminhoMD = pasta.resolve(nomeArquivoMD);

            Files.writeString(caminhoMD, md);
            System.out.println("Relatório salvo com sucesso em: " + caminhoMD.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatório: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void gerarRelatoriosMarkdownMes(Dia dia) {
        LocalDate dataDoDia = dia.getData();
        int ano = dataDoDia.getYear();
        int mes = dataDoDia.getMonthValue();

        List<Dia> diasDoMes = Dia.getDiasDoMes(ano, mes);
        System.out.println("Gerando relatórios para " + diasDoMes.size() + " dias do mês " + mes + "/" + ano);
        for (Dia d : diasDoMes) {
            try {
                System.out.println("Gerando relatório para o dia: " + d.getDataFormatada());
                Dia diaCompleto = diaService.criarOuCarregarDia(d.getDataFormatada());
                gerarRelatorioMarkdown(diaCompleto);
                System.out.println("Relatório gerado para o dia: " + d.getDataFormatada());
            } catch (Exception e) {
                System.err.println("Erro ao gerar relatório do dia " + d.getDataFormatada() + ": " + e.getMessage());
            }
        }
        System.out.println("Relatórios do mês gerados com sucesso.");
    }

    public void excluirRelatorioMesPassado(Dia dia) {
        try {
            Path pasta = Paths.get("relatorios");
            Files.createDirectories(pasta);

            LocalDate dataDoDia = dia.getData();
            LocalDate mesPassado = dataDoDia.minusMonths(1);
            String mesPassadoStr = String.format("%04d-%02d", mesPassado.getYear(), mesPassado.getMonthValue());

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(pasta, "relatorio_" + mesPassadoStr + "-*.md")) {
                int count = 0;

                for (Path entry : stream) {
                    Files.delete(entry);
                    System.out.println("Relatório excluído: " + entry.getFileName());
                    count++;
                }

                if (count == 0) {
                    System.out.println("Nenhum relatório encontrado para o mês passado: " + mesPassadoStr);
                } else {
                    System.out.println(count + " relatório(s) do mês passado excluído(s): " + mesPassadoStr);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao excluir relatórios do mês passado", e);
        }
    }


    private String formatHora(java.time.LocalTime hora) {
        return hora == null ? "-" : hora.toString();
    }

    private String formatarDuracao(long minutos) {
        long h = minutos / 60;
        long m = minutos % 60;
        return String.format("%dh%02dm", h, m);
    }
}