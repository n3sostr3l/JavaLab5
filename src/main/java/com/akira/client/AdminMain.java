package com.akira.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

/**
 * Входной пункт административного клиента.
 */
public class AdminMain {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;
    private static NetworkManager networkManager;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        networkManager = new NetworkManager(DEFAULT_HOST, DEFAULT_PORT);

        System.out.println("Запущен Админ-клиент. Доступные команды: save, exit_server, exit");

        // Админ просто подключается, не выбирая сессию (это должен делать обычный пользователь)
        Request initRequest = new Request("init");
        initRequest.setInit(true);
        initRequest.setAdmin(true);
        
        networkManager.sendAndReceive(initRequest);

        while (true) {
            System.out.print("admin> ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String commandName = tokens[0].toLowerCase();

            if (commandName.equals("exit")) {
                break;
            }

            if (commandName.equals("save")) {
                Request req = new Request("save");
                req.setAdmin(true);
                Response resp = networkManager.sendAndReceive(req);
                if (resp != null) System.out.println(resp.getMessage());
                continue;
            }

            if (commandName.equals("exit_server")) {
                System.out.print("Вы уверены, что хотите завершить работу сервера? (y/n). Все клиенты не смогут восстановить текущую сессию. Введите save для сохранения коллекции: ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("save")) {
                    Request sreq = new Request("save");
                    sreq.setAdmin(true);
                    networkManager.sendAndReceive(sreq);
                    System.out.println("Коллекция сохранена.");
                    
                    Request exreq = new Request("exit_server");
                    exreq.setAdmin(true);
                    Response resp = networkManager.sendAndReceive(exreq);
                    if (resp != null) System.out.println(resp.getMessage());
                    break;
                } else if (confirm.equals("y") || confirm.equals("yes")) {
                    Request exreq = new Request("exit_server");
                    exreq.setAdmin(true);
                    Response resp = networkManager.sendAndReceive(exreq);
                    if (resp != null) System.out.println(resp.getMessage());
                    break;
                } else {
                    System.out.println("Отмена.");
                    continue;
                }
            }

            System.out.println("Ошибка: Админу разрешены только команды: save, exit_server, exit");
        }
        
        networkManager.close();
    }
}
