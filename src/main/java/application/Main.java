package application;

import api.ApiServer;
import database.TableCreator;
import service.DiaService;
import service.RelatorioService;
import service.TarefaService;
import ui.MenuHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TableCreator.criarTabelas();

        boolean modoApi = false;
        boolean modoTerminal = false;

        if (args.length > 0) {
            String modo = args[0].toLowerCase();
            if (modo.equals("api")) {
                modoApi = true;
                modoTerminal = false;
            } else if (modo.equals("hibrido")) {
                modoApi = true;
                modoTerminal = true;
            }
        } else {
            modoTerminal = true;
        }

        Thread apiThread = null;

        if (modoApi) {
            apiThread = new Thread(() -> {
                System.out.println("Iniciando servidor API CRONOS...");
                ApiServer.start();
            });

            if (modoTerminal) {
                apiThread.setDaemon(true);
            }

            ApiServer.start();
        }

        if (modoTerminal) {
            System.out.println("Iniciando CRONOS em modo terminal...");
            iniciarModoTerminal();
        } else if (modoApi) {
            System.out.println("Modo API iniciado. Pressione CTRL+C para encerrar.");
            try {
                apiThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void iniciarModoTerminal() {
        Scanner sc = new Scanner(System.in);
        DiaService diaService = new DiaService();
        TarefaService tarefaService = new TarefaService();
        RelatorioService relatorioService = new RelatorioService();
        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        MenuHandler menuHandler = new MenuHandler(sc, diaService, tarefaService, relatorioService, dataHoje);
        menuHandler.iniciarMenu();

        sc.close();
    }
}