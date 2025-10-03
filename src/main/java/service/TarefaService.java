package service;

import entities.Categoria;
import entities.Dia;
import entities.Task;
import repository.DiaRepository;

import java.util.*;

public class TarefaService {
    private final DiaRepository diaRepository = new DiaRepository();

    public void adicionarTarefa(Dia dia, Scanner sc) {
        System.out.print("Descrição da tarefa: ");
        String descricao = sc.nextLine();

        Categoria[] categorias = Categoria.values();
        System.out.println("Escolha a categoria:");
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + " - " + categorias[i]);
        }

        Categoria categoria = null;
        try {
            int catEscolha = Integer.parseInt(sc.nextLine()) - 1;
            if (catEscolha >= 0 && catEscolha < categorias.length) {
                categoria = categorias[catEscolha];
            } else {
                System.out.println("Opção inválida. Categoria será N/A.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Categoria será N/A.");
        }

        System.out.print("Cliente (opcional): ");
        String cliente = sc.nextLine();

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

        Task novaTarefa = new Task(descricao, categoria, cliente, duracao);
        diaRepository.insertTask(novaTarefa, dia.getId());
        dia.addTarefa(novaTarefa); // TreeSet já mantém ordenado
        System.out.println("Tarefa adicionada com sucesso!");
    }

    public void editarTarefa(Dia dia, Scanner sc) {
        List<Task> tarefas = new ArrayList<>(dia.getTarefas()); // copia do TreeSet
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

        System.out.println("Nova categoria (" + (tarefa.getCategoria() != null ? tarefa.getCategoria() : "N/A") + "): ");
        Categoria[] categorias = Categoria.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + " - " + categorias[i]);
        }
        String catInput = sc.nextLine();
        if (!catInput.isEmpty()) {
            try {
                int catEscolha = Integer.parseInt(catInput) - 1;
                if (catEscolha >= 0 && catEscolha < categorias.length) {
                    tarefa.setCategoria(categorias[catEscolha]);
                } else {
                    System.out.println("Opção inválida. Mantida a anterior.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Mantida a anterior.");
            }
        }

        System.out.print("Novo cliente (" + tarefa.getCliente() + "): ");
        String cliente = sc.nextLine();
        if (!cliente.isEmpty()) tarefa.setCliente(cliente);

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
        List<Task> tarefas = new ArrayList<>(dia.getTarefas()); // copia do TreeSet
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

        Task tarefa = tarefas.get(escolha);
        dia.getTarefas().remove(tarefa);
        diaRepository.deleteTaskById(tarefa.getId());

        System.out.println("Tarefa '" + tarefa.getDescricao() + "' removida com sucesso!");
    }
}