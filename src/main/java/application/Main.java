package application;

import database.TableCreator;
import entities.Dia;
import service.DiaService;
import service.RelatorioService;
import service.TarefaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    private final Scanner sc = new Scanner(System.in);
    private final DiaService diaService = new DiaService();
    private final TarefaService tarefaService = new TarefaService();
    private final RelatorioService relatorioService = new RelatorioService();

    private Dia dia;
    private final String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    public static void main(String[] args) {
        TableCreator.criarTabelas();
        new Main().menu();
    }

    public void menu() {
        int opcao;
        while (dia == null || dia.getData() == null) {
            titulo();
            System.out.println(" 1 - Definir Data");
            System.out.println(" 2 - Utilizar Data de hoje (" + dataHoje + ")");
            System.out.println(" 0 - Sair");
            System.out.print(" Digite um número: ");
            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(" Opção inválida! Digite apenas números.");
                continue;
            }
            if (opcao == 1) {
                definirDataManual();
            } else if (opcao == 2) {
                this.dia = diaService.criarOuCarregarDia(dataHoje);
            } else if (opcao == 0) {
                System.out.println(" Obrigado por usar nosso sistema!");
                return;
            } else {
                System.out.println(" Opção inválida! Escolha 0, 1 ou 2.");
            }
        }

        do {
            titulo();
            int tam = dia.getTarefas().size() < 10 ? 58 : 59;
            System.out.println(" " + "-".repeat(tam));
            System.out.println(resumo());
            System.out.println(" " + "-".repeat(tam));
            System.out.println(" 1 - Definir Data");
            System.out.println(" 2 - Registrar Início do Trabalho");
            System.out.println(" 3 - Registrar Fim do Trabalho");
            System.out.println(" 4 - Registrar Início do Almoço");
            System.out.println(" 5 - Registrar Fim do Almoço");
            System.out.println(" 6 - Adicionar Tarefa");
            System.out.println(" 7 - Ver Resumo do Dia");
            System.out.println(" 8 - Editar Tarefa");
            System.out.println(" 9 - Remover Tarefa");
            System.out.println(" 10 - Gerar relatório md");
            System.out.println(" 0 - Sair");
            System.out.print(" Digite um número: ");
            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(" Opção inválida! Digite apenas números.");
                opcao = -1;
            }
            switch (opcao) {
                case 1:
                    definirDataManual();
                    break;
                case 2:
                    LocalTime inicio = lerHorario(" Início do trabalho (HH:mm): ", null, dia.getFimTrabalho(), null, "Não pode ser depois do fim do trabalho");
                    diaService.atualizarInicioTrabalho(dia, inicio);
                    break;
                case 3:
                    LocalTime fim = lerHorario(" Fim do trabalho (HH:mm): ", dia.getInicioTrabalho(), null, "Não pode ser antes do início do trabalho", null);
                    diaService.atualizarFimTrabalho(dia, fim);
                    break;
                case 4:
                    LocalTime almocoIni = lerHorario(" Início do almoço (HH:mm): ", dia.getInicioTrabalho(), dia.getFimAlmoco(), "Almoço não pode ser antes do início do trabalho", "Início do almoço não pode ser depois do fim do almoço");
                    diaService.atualizarInicioAlmoco(dia, almocoIni);
                    break;
                case 5:
                    LocalTime almocoFim = lerHorario(" Fim do almoço (HH:mm): ", dia.getInicioAlmoco(), dia.getFimTrabalho(), "Fim do almoço não pode ser antes do início do almoço", "Não pode ser depois do fim do trabalho");
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
                default:
                    if (opcao != 0) System.out.println(" Selecione uma opção válida!");
                    break;
            }
            if (opcao != 0) {
                System.out.print(" Pressione Enter para continuar...");
                sc.nextLine();
            }
        } while (opcao != 0);
        System.out.println(" Obrigado por usar nosso sistema!");
    }

    private static String formatHora(LocalTime hora) {
        return (hora == null) ? "-" : hora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void definirDataManual() {
        boolean dataValida = false;
        while (!dataValida) {
            System.out.print(" Informe a data (dd/MM/yyyy): ");
            String inputData = sc.nextLine();
            try {
                this.dia = diaService.criarOuCarregarDia(inputData);
                dataValida = true;
            } catch (DateTimeParseException e) {
                System.out.println(" Data inválida! Use o formato dd/MM/yyyy. Tente novamente.");
            } catch (Exception e) {
                System.out.println(" Erro ao carregar/criar dia: " + e.getMessage());
            }
        }
    }

    private LocalTime lerHorario(String mensagem, LocalTime limiteMin, LocalTime limiteMax, String msgAntes, String msgDepois) {
        LocalTime horario = null;
        boolean valido = false;
        do {
            System.out.print(mensagem);
            String input = sc.nextLine();
            try {
                horario = LocalTime.parse(input, Dia.getFmtHora());
                if (limiteMin != null && horario.isBefore(limiteMin)) {
                    if (msgAntes != null) System.out.println(msgAntes);
                    continue;
                }
                if (limiteMax != null && horario.isAfter(limiteMax)) {
                    if (msgDepois != null) System.out.println(msgDepois);
                    continue;
                }
                valido = true;
            } catch (DateTimeParseException e) {
                System.out.println(" Formato inválido! Use HH:mm, ex: 08:30");
            }
        } while (!valido);
        return horario;
    }

    public String resumo() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Data: ").append(dia.getDataFormatada()).append(" | Início: ").append(formatOrPlaceholder(dia.getInicioTrabalho())).append(" | Fim: ").append(formatOrPlaceholder(dia.getFimTrabalho())).append(" | Tarefas: ").append(dia.getTarefas() == null ? 0 : dia.getTarefas().size());

        if (dia.getInicioAlmoco() != null || dia.getFimAlmoco() != null) {
            sb.append("\n Almoço: ").append(formatOrPlaceholder(dia.getInicioAlmoco())).append(" - ").append(formatOrPlaceholder(dia.getFimAlmoco()));
            if (!"00:00".equals(dia.getHorasTrabalhadas())) {
                sb.append(" | Horas trabalhadas: ").append(dia.getHorasTrabalhadas());
            }
        }
        return sb.toString();
    }

    private String formatOrPlaceholder(LocalTime time) {
        return time == null ? "--:--" : formatHora(time);
    }

    public void titulo() {
        limparTela();
        textoCentralizado("=".repeat(58),58);
        textoCentralizado(" ".repeat(58),58);
        textoCentralizado("CCCCC   RRRRR   OOOOO   N   N   OOOOO   SSSSS",58);
        textoCentralizado("C       R   R   O   O   NN  N   O   O   S    ",58);
        textoCentralizado("C       RRRRR   O   O   N N N   O   O   SSSSS",58);
        textoCentralizado("C       R  R    O   O   N  NN   O   O       S",58);
        textoCentralizado("CCCCC   R   R   OOOOO   N   N   OOOOO   SSSSS",58);
        textoCentralizado(" ".repeat(58),58);
        textoCentralizado("=".repeat(58),58);
        textoCentralizado("GKsegura - 2025", 58);
    }

    public void textoCentralizado(String txt, int tamanho) {
        int tamanhoTxt = txt.length();
        if (tamanhoTxt >= tamanho) {
            System.out.println(" " + txt);
            return;
        }
        int espacos = (tamanho - tamanhoTxt) / 2;
        String padding = " ".repeat(espacos);
        System.out.println(" " + padding + txt + padding);
    }

    private void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println(" Não foi possível limpar a tela.");
        }
    }

}