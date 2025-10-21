package ui;

import entities.Dia;
import service.DiaService;
import service.RelatorioService;
import service.TarefaService;
import utils.ConsoleUtils;

import java.time.LocalTime;
import java.util.Scanner;

public class MenuHandler {

    private final Scanner sc;
    private final DiaService diaService;
    private final TarefaService tarefaService;
    private final RelatorioService relatorioService;
    private Dia dia;
    private final String dataHoje;

    public MenuHandler(Scanner sc, DiaService diaService, TarefaService tarefaService,
                       RelatorioService relatorioService, String dataHoje) {
        this.sc = sc;
        this.diaService = diaService;
        this.tarefaService = tarefaService;
        this.relatorioService = relatorioService;
        this.dataHoje = dataHoje;
    }

    public void iniciarMenu() {
        int opcao;

        // Menu inicial - Seleção de data
        while (dia == null || dia.getData() == null) {
            ConsoleUtils.titulo();
            System.out.println("1 - Definir Data");
            System.out.println("2 - Utilizar Data de hoje (" + dataHoje + ")");
            System.out.println("0 - Sair");

            opcao = ConsoleUtils.lerInteiro("Digite um número: ", 0, 2, sc);

            switch (opcao) {
                case 1:
                    definirDataManual();
                    break;
                case 2:
                    dia = diaService.criarOuCarregarDia(dataHoje);
                    break;
                case 0:
                    System.out.println("Obrigado por usar nosso sistema!");
                    return;
            }
        }

        // Menu principal
        do {
            ConsoleUtils.titulo();
            int tam = dia.getTarefas().size() < 10 ? 58 : 59;
            System.out.println("-".repeat(tam));
            System.out.println(resumoDia());
            System.out.println("-".repeat(tam));
            mostrarMenuPrincipal();
            opcao = ConsoleUtils.lerInteiro("Digite um número: ", 0, 12, sc);
            executarOpcao(opcao);
            if (opcao != 0) ConsoleUtils.pausar(sc);
        } while (opcao != 0);

        System.out.println("Obrigado por usar nosso sistema!");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("1 - Definir Data");
        System.out.println("2 - Registrar Início do Trabalho");
        System.out.println("3 - Registrar Fim do Trabalho");
        System.out.println("4 - Registrar Início do Almoço");
        System.out.println("5 - Registrar Fim do Almoço");
        System.out.println("6 - Adicionar Tarefa");
        System.out.println("7 - Ver Resumo do Dia");
        System.out.println("8 - Editar Tarefa");
        System.out.println("9 - Remover Tarefa");
        System.out.println("10 - Gerar relatório md do dia");
        System.out.println("11 - Gerar relatórios md do mês");
        System.out.println("12 - Excluir relatórios md do mês passado");
        System.out.println("0 - Sair");
    }

    private void executarOpcao(int opcao) {
        ConsoleUtils.titulo();

        switch (opcao) {
            case 1:
                definirDataManual();
                break;

            case 2:
                LocalTime inicio = ConsoleUtils.lerHorario(
                        "Início do trabalho (HH:mm): ",
                        null,
                        dia.getFimTrabalho(),
                        null,
                        "Não pode ser depois do fim do trabalho",
                        sc
                );
                diaService.atualizarInicioTrabalho(dia, inicio);
                break;

            case 3:
                LocalTime fim = ConsoleUtils.lerHorario(
                        "Fim do trabalho (HH:mm): ",
                        dia.getInicioTrabalho(),
                        null,
                        "Não pode ser antes do início do trabalho",
                        null,
                        sc
                );
                diaService.atualizarFimTrabalho(dia, fim);
                break;

            case 4:
                LocalTime almocoIni = ConsoleUtils.lerHorario(
                        "Início do almoço (HH:mm): ",
                        dia.getInicioTrabalho(),
                        dia.getFimAlmoco(),
                        "Almoço não pode ser antes do início do trabalho",
                        "Início do almoço não pode ser depois do fim do almoço",
                        sc
                );
                diaService.atualizarInicioAlmoco(dia, almocoIni);
                break;

            case 5:
                LocalTime almocoFim = ConsoleUtils.lerHorario(
                        "Fim do almoço (HH:mm): ",
                        dia.getInicioAlmoco(),
                        dia.getFimTrabalho(),
                        "Fim do almoço não pode ser antes do início do almoço",
                        "Não pode ser depois do fim do trabalho",
                        sc
                );
                diaService.atualizarFimAlmoco(dia, almocoFim);
                break;

            case 6:
                tarefaService.adicionarTarefa(dia, sc);
                break;

            case 7:
                System.out.println(dia.toString());
                break;

            case 8:
                tarefaService.editarTarefa(dia, sc);
                break;

            case 9:
                tarefaService.removerTarefa(dia, sc);
                break;

            case 10:
                relatorioService.gerarRelatorioMarkdown(dia);
                break;

            case 11:
                relatorioService.gerarRelatoriosMarkdownMes(dia);
                break;

            case 12:
                relatorioService.excluirRelatorioMesPassado(dia);
                break;

            case 0:
                // Sai do menu
                break;

            default:
                System.out.println("Selecione uma opção válida!");
                break;
        }
    }

    private void definirDataManual() {
        dia = diaService.criarOuCarregarDia(ConsoleUtils.lerData("Informe a data (dd/MM/yyyy): ", sc));
    }

    private String resumoDia() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data: ").append(dia.getDataFormatada())
                .append(" | Início: ").append(ConsoleUtils.formatHora(dia.getInicioTrabalho()))
                .append(" | Fim: ").append(ConsoleUtils.formatHora(dia.getFimTrabalho()))
                .append(" | Tarefas: ").append(dia.getTarefas() == null ? 0 : dia.getTarefas().size());

        if (dia.getInicioAlmoco() != null || dia.getFimAlmoco() != null) {
            sb.append("\nAlmoço: ")
                    .append(ConsoleUtils.formatHora(dia.getInicioAlmoco()))
                    .append(" - ")
                    .append(ConsoleUtils.formatHora(dia.getFimAlmoco()));

            if (dia.getHorasTrabalhadas() != null && !"00:00".equals(dia.getHorasTrabalhadas())) {
                sb.append(" | Horas trabalhadas: ").append(dia.getHorasTrabalhadas());
            }
        }

        return sb.toString();
    }
}