package service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
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

    /**
     * Calcula as horas trabalhadas na semana atual
     * @param diaReferencia Dia de referência para calcular a semana
     * @return String formatada com o relatório da semana
     */
    public String calcularHorasSemana(Dia diaReferencia) {
        LocalDate dataRef = diaReferencia.getData();

        // Obtém segunda e sexta da semana
        LocalDate segunda = dataRef.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sexta = dataRef.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<DiaInfo> diasSemana = new ArrayList<>();
        int totalMinutos = 0;
        int diasTrabalhados = 0;

        // Percorre todos os dias da semana (segunda a sexta)
        LocalDate dataAtual = segunda;
        while (!dataAtual.isAfter(sexta)) {
            String dataFormatada = dataAtual.format(formatter);
            Dia dia = diaService.buscarDia(dataFormatada);

            DiaInfo info = new DiaInfo();
            info.data = dataAtual;
            info.dataFormatada = dataFormatada;
            info.diaSemana = obterDiaSemana(dataAtual.getDayOfWeek());

            if (dia != null && dia.getInicioTrabalho() != null && dia.getFimTrabalho() != null) {
                int minutosDia = calcularMinutosTrabalhados(dia);
                info.minutosTrabalhados = minutosDia;
                info.horasFormatadas = formatarMinutosParaHoras(minutosDia);
                totalMinutos += minutosDia;
                diasTrabalhados++;
            } else {
                info.minutosTrabalhados = 0;
                info.horasFormatadas = "--:--";
            }

            diasSemana.add(info);
            dataAtual = dataAtual.plusDays(1);
        }

        // Formata resultado
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════════════╗\n");
        sb.append("║     RELATÓRIO SEMANAL DE HORAS TRABALHADAS         ║\n");
        sb.append("╠════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Período: %s a %s          ║\n",
                segunda.format(formatter),
                sexta.format(formatter)));
        sb.append("╠════════════════════════════════════════════════════════╣\n");

        // Lista dias da semana - 56 caracteres internos
        for (DiaInfo info : diasSemana) {
            String linha = String.format(" %s - %s │ %s",
                    info.diaSemana.trim(),
                    info.dataFormatada,
                    info.horasFormatadas);
            // Completa com espaços até 56 caracteres
            int espacosFaltando = 56 - linha.length();
            sb.append("║").append(linha).append(" ".repeat(espacosFaltando)).append("║\n");
        }

        sb.append("╠════════════════════════════════════════════════════════╣\n");

        // Dias trabalhados - 56 caracteres internos
        String linhaDias = String.format(" Dias trabalhados: %d", diasTrabalhados);
        sb.append("║").append(linhaDias).append(" ".repeat(56 - linhaDias.length())).append("║\n");

        // Total de horas - 56 caracteres internos
        String linhaTotal = String.format(" Total de horas: %s", formatarMinutosParaHoras(totalMinutos));
        sb.append("║").append(linhaTotal).append(" ".repeat(56 - linhaTotal.length())).append("║\n");

        // Calcula média diária - 56 caracteres internos
        if (diasTrabalhados > 0) {
            int mediaDiaria = totalMinutos / diasTrabalhados;
            String linhaMedia = String.format(" Média diária: %s", formatarMinutosParaHoras(mediaDiaria));
            sb.append("║").append(linhaMedia).append(" ".repeat(56 - linhaMedia.length())).append("║\n");
        }

        sb.append("╚════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Calcula os minutos trabalhados em um dia
     */
    private int calcularMinutosTrabalhados(Dia dia) {
        if (dia.getInicioTrabalho() == null || dia.getFimTrabalho() == null) {
            return 0;
        }

        int minutosTotais = (int) dia.getInicioTrabalho().until(dia.getFimTrabalho(),
                java.time.temporal.ChronoUnit.MINUTES);

        // Subtrai tempo de almoço
        if (dia.getInicioAlmoco() != null && dia.getFimAlmoco() != null) {
            int minutosAlmoco = (int) dia.getInicioAlmoco().until(dia.getFimAlmoco(),
                    java.time.temporal.ChronoUnit.MINUTES);
            minutosTotais -= minutosAlmoco;
        }

        return Math.max(0, minutosTotais);
    }

    /**
     * Formata minutos para formato HH:mm
     */
    private String formatarMinutosParaHoras(int minutos) {
        int horas = minutos / 60;
        int mins = minutos % 60;
        return String.format("%02d:%02d", horas, mins);
    }

    /**
     * Retorna o nome do dia da semana em português
     */
    private String obterDiaSemana(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return  "Segunda";
            case TUESDAY: return "Terça  ";
            case WEDNESDAY: return "Quarta ";
            case THURSDAY: return "Quinta ";
            case FRIDAY: return "Sexta  ";
            case SATURDAY: return "Sábado ";
            case SUNDAY: return "Domingo";
            default: return "       ";
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

    /**
     * Classe auxiliar para armazenar informações do dia
     */
    private static class DiaInfo {
        LocalDate data;
        String dataFormatada;
        String diaSemana;
        int minutosTrabalhados;
        String horasFormatadas;
    }
}