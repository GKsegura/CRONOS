package utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUtils {

    public static void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception ignored) {}
    }

    public static String textoCentralizado(String txt, int tamanho) {
        int espacos = (tamanho - txt.length()) / 2;
        return " ".repeat(Math.max(0, espacos)) + txt;
    }

    public static int lerInteiro(String msg, int min, int max, Scanner sc) {
        int valor;
        while (true) {
            System.out.print(msg);
            try {
                valor = Integer.parseInt(sc.nextLine());
                if (valor < min || valor > max) {
                    System.out.printf("Digite um número entre %d e %d.%n", min, max);
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números.");
            }
        }
    }

    public static LocalTime lerHorario(String msg, Scanner sc) {
        while (true) {
            System.out.print(msg);
            try {
                return LocalTime.parse(sc.nextLine(), DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido! Use HH:mm");
            }
        }
    }

    public static LocalTime lerHorario(String msg, LocalTime limiteMin, LocalTime limiteMax,
                                       String msgAntes, String msgDepois, Scanner sc) {
        LocalTime horario;
        while (true) {
            System.out.print(msg);
            try {
                horario = LocalTime.parse(sc.nextLine(), DateTimeFormatter.ofPattern("HH:mm"));

                if (limiteMin != null && horario.isBefore(limiteMin)) {
                    if (msgAntes != null) System.out.println(msgAntes);
                    continue;
                }

                if (limiteMax != null && horario.isAfter(limiteMax)) {
                    if (msgDepois != null) System.out.println(msgDepois);
                    continue;
                }

                return horario;
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido! Use HH:mm, ex: 08:30");
            }
        }
    }

    public static String lerData(String msg, Scanner sc) {
        while (true) {
            System.out.print(msg);
            try {
                LocalDate data = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida! Use o formato dd/MM/yyyy.");
            }
        }
    }

    public static void titulo() {
        limparTela();
        System.out.println(textoCentralizado("=".repeat(58), 58));
        System.out.println(textoCentralizado(" ".repeat(58), 58));
        System.out.println(textoCentralizado("CCCCC   RRRRR   OOOOO   N   N   OOOOO   SSSSS", 58));
        System.out.println(textoCentralizado("C       R   R   O   O   NN  N   O   O   S    ", 58));
        System.out.println(textoCentralizado("C       RRRRR   O   O   N N N   O   O   SSSSS", 58));
        System.out.println(textoCentralizado("C       R  R    O   O   N  NN   O   O       S", 58));
        System.out.println(textoCentralizado("CCCCC   R   R   OOOOO   N   N   OOOOO   SSSSS", 58));
        System.out.println(textoCentralizado(" ".repeat(58), 58));
        System.out.println(textoCentralizado("=".repeat(58), 58));
        System.out.println(textoCentralizado("GKsegura - 2025", 58));
    }

    public static void pausar(Scanner sc) {
        System.out.print("Pressione Enter para continuar...");
        sc.nextLine();
    }

    public static String formatHora(LocalTime hora) {
        return (hora == null) ? "--:--" : hora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}