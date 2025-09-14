package service;

import entities.Categoria;
import entities.Dia;
import entities.Task;
import repository.DiaRepository;

import java.util.List;
import java.util.Scanner;

public class TarefaService {
    private final DiaRepository diaRepository = new DiaRepository();

    public void adicionarTarefa(Dia dia, Scanner sc) {
        System.out.print("Descrição da tarefa: ");
        String descricao = sc.nextLine();

        System.out.print("Categoria (SUPORTE, REUNIAO, DESPESA_GERAL, SUPORTE_HORAS_PAGAS): ");
        Categoria categoria = null;
        try {
            categoria = Categoria.valueOf(sc.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Categoria inválida. Será considerada N/A.");
        }

        System.out.print("Cooperativa (opcional): ");
        String cooperativa = sc.nextLine();

        System.out.print("Duração em minutos (opcional): ");
        String duracaoInput = sc.nextLine();
        Long duracao = null;
        if (!duracaoInput.isEmpty()) {
            try {
                duracao = Long.parseLong(duracaoInput);
            } catch (NumberFormatException e) {
                System.out.println("Duração inválida, será ignorada.");
            }
        }

        Task novaTarefa = new Task(descricao, categoria, cooperativa, duracao);
        diaRepository.insertTask(novaTarefa, dia.getId());
        dia.addTarefa(novaTarefa);
        System.out.println("Tarefa adicionada com sucesso!");
    }

    public void editarTarefa(Dia dia, Scanner sc) {
        List<Task> tarefas = dia.getTarefas();
        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para editar.");
            return;
        }

        System.out.println("Escolha a tarefa que deseja editar:");
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println((i + 1) + " - " + tarefas.get(i));
        }

        int escolha = -1;
        try {
            escolha = Integer.parseInt(sc.nextLine()) - 1;
        } catch (NumberFormatException ignored) {}
        if (escolha < 0 || escolha >= tarefas.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Task tarefa = tarefas.get(escolha);

        System.out.print("Nova descrição (" + tarefa.getDescricao() + "): ");
        String descricao = sc.nextLine();
        if (!descricao.isEmpty()) tarefa.setDescricao(descricao);

        System.out.print("Nova categoria (" + (tarefa.getCategoria() != null ? tarefa.getCategoria() : "N/A") + "): ");
        String catInput = sc.nextLine();
        if (!catInput.isEmpty()) {
            try {
                tarefa.setCategoria(Categoria.valueOf(catInput.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Categoria inválida. Mantida a anterior.");
            }
        }

        System.out.print("Nova cooperativa (" + tarefa.getCooperativa() + "): ");
        String coop = sc.nextLine();
        if (!coop.isEmpty()) tarefa.setCooperativa(coop);

        System.out.print("Nova duração em minutos (" + (tarefa.getDuracaoMin() != null ? tarefa.getDuracaoMin() : "N/A") + "): ");
        String durInput = sc.nextLine();
        if (!durInput.isEmpty()) {
            try {
                tarefa.setDuracaoMin(Long.parseLong(durInput));
            } catch (NumberFormatException e) {
                System.out.println("Duração inválida. Mantida a anterior.");
            }
        }

        diaRepository.updateTask(tarefa);
        System.out.println("Tarefa atualizada com sucesso!");
    }

    public void removerTarefa(Dia dia, Scanner sc) {
        List<Task> tarefas = dia.getTarefas();
        if (tarefas.isEmpty()) {
            System.out.println("Não há tarefas para remover.");
            return;
        }

        System.out.println("Escolha a tarefa que deseja remover:");
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.println((i + 1) + " - " + tarefas.get(i));
        }

        int escolha = -1;
        try {
            escolha = Integer.parseInt(sc.nextLine()) - 1;
        } catch (NumberFormatException ignored) {}
        if (escolha < 0 || escolha >= tarefas.size()) {
            System.out.println("Opção inválida.");
            return;
        }

        Task tarefa = tarefas.remove(escolha);
        diaRepository.deleteTaskById(tarefa.getId());
        System.out.println("Tarefa '" + tarefa.getDescricao() + "' removida com sucesso!");
    }
}
