package com.akira.client;

import com.akira.general.LabWorkReader;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Главный класс клиентского приложения.
 */
public class Main {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NetworkManager networkManager = new NetworkManager(DEFAULT_HOST, DEFAULT_PORT);
        LabWorkReader labWorkReader = new LabWorkReader(scanner);

        System.out.println("Клиент запущен. Введите 'help' для получения списка команд.");

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String commandName = tokens[0].toLowerCase();
            ArrayList<String> commandArgs = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

            if (commandName.equals("exit")) {
                System.out.println("Завершение работы клиента.");
                break;
            }
            
            if (commandName.equals("save")) {
                System.out.println("Ошибка: команда 'save' доступна только на сервере.");
                continue;
            }

            // Формируем запрос
            Request request;
            if (isObjectRequired(commandName)) {
                LabWork lab = labWorkReader.readLabWork();
                request = new Request(commandName, commandArgs, lab);
            } else {
                request = new Request(commandName, commandArgs);
            }

            // Отправляем и получаем ответ (с механизмом повторных попыток)
            Response response = networkManager.sendAndReceive(request);
            int attempts = 0;
            while (response == null && attempts < 3) {
                System.out.println("Сервер недоступен. Повторная попытка подключения...");
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                response = networkManager.sendAndReceive(request);
                attempts++;
            }

            if (response != null) {
                System.out.println(response.getMessage());
            } else {
                System.out.println("Ошибка: не удалось получить ответ от сервера.");
            }
        }
        networkManager.close();
    }

    private static boolean isObjectRequired(String commandName) {
        return commandName.equals("insert") || 
               commandName.equals("update") || 
               commandName.equals("replace_if_greater") || 
               commandName.equals("replace_if_lowe");
    }
}
