package com.akira.client;

import java.util.Scanner;
import com.akira.general.LabWorkReader;

/**
 * Точка входа клиентского приложения.
 */
public class Main {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12347;

    /**
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NetworkManager network = new NetworkManager(DEFAULT_HOST, DEFAULT_PORT);
        ClientSession session = new ClientSession(network, new LabWorkReader(scanner));

        System.out.println("Клиент запущен. Введите 'help' для списка команд.");

        if (!scanner.hasNextLine()) { network.close(); return; }
        session.init();

        session.processInput(scanner, false);
        network.close();
    }
}
