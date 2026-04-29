package com.akira.client;

import java.util.Scanner;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

/**
 * Точка входа административного клиента.
 * <p>
 * Обрабатывает команды: {@code save}, {@code exit_server}, {@code exit}.
 * </p>
 */
public class AdminMain {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12347;

    /**
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NetworkManager network = new NetworkManager(DEFAULT_HOST, DEFAULT_PORT);

        System.out.println("Запущен Админ-клиент. Доступные команды: save, exit_server, exit");
        sendAdmin(network, "init");

        while (true) {
            System.out.print("admin> ");
            if (!scanner.hasNextLine()) break;
            String cmd = scanner.nextLine().trim().toLowerCase();
            if (cmd.isEmpty()) continue;

            switch (cmd) {
                case "exit" -> { break; }
                case "save" -> print(sendAdmin(network, "save"));
                case "exit_server" -> {
                    System.out.print("Завершить сервер? Введите 'save' для сохранения, 'y' для выхода без сохранения, иное — отмена: ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("save")) {
                        print(sendAdmin(network, "save"));
                        print(sendAdmin(network, "exit_server"));
                        network.close(); return;
                    } else if (confirm.equals("y") || confirm.equals("yes")) {
                        print(sendAdmin(network, "exit_server"));
                        network.close(); return;
                    } else {
                        System.out.println("Отмена.");
                    }
                }
                default -> System.out.println("Ошибка: доступны только save, exit_server, exit.");
            }
            if (cmd.equals("exit")) break;
        }
        network.close();
        scanner.close();
    }

    /**
     * Отправляет административный запрос на сервер.
     * @param network менеджер сети
     * @param commandName имя команды
     * @return ответ от сервера
     */
    private static Response sendAdmin(NetworkManager network, String commandName) {
        Request req = new Request(commandName);
        req.setAdmin(true);
        if (commandName.equals("init")) req.setInit(true);
        return network.sendAndReceive(req);
    }

    /**
     * Выводит сообщение из ответа сервера в консоль.
     * @param resp ответ от сервера
     */
    private static void print(Response resp) {
        if (resp != null) System.out.println(resp.getMessage());
    }
}
