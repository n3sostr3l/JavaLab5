package com.akira.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import com.akira.general.LabWorkReader;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

/**
 * Главный класс клиентского приложения.
 * <p>
 * Обеспечивает интерактивный ввод команд, чтение скриптов и сетевое взаимодействие с сервером.
 * </p>
 */
public class Main {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;
    private static final HashSet<String> scriptStack = new HashSet<>();
    private static LabWorkReader labWorkReader;
    private static NetworkManager networkManager;

    /**
     * Точка входа в клиентское приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        networkManager = new NetworkManager(DEFAULT_HOST, DEFAULT_PORT);
        labWorkReader = new LabWorkReader(scanner);

        System.out.println("Клиент запущен. Введите 'help' для получения списка команд.");

        processInput(scanner, false);
        
        networkManager.close();
    }

    /**
     * Основной цикл обработки ввода.
     * @param scanner источник ввода
     * @param isScript флаг, указывающий, является ли ввод скриптом
     */
    private static void processInput(Scanner scanner, boolean isScript) {
        while (true) {
            if (!isScript) System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.startsWith("#")) continue;

            String[] tokens = input.split("\\s+");
            String commandName = tokens[0].toLowerCase();
            ArrayList<String> commandArgs = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

            if (commandName.equals("exit")) {
                if (!isScript) System.out.println("Завершение работы клиента.");
                break;
            }
            
            if (commandName.equals("execute_script")) {
                if (commandArgs.isEmpty()) {
                    System.out.println("Ошибка: не указано имя файла.");
                    continue;
                }
                executeScript(commandArgs.get(0));
                continue;
            }

            if (commandName.equals("save")) {
                System.out.println("Ошибка: команда 'save' доступна только на сервере.");
                continue;
            }

            Request request;
            if (isObjectRequired(commandName)) {
                LabWork lab = labWorkReader.readLabWork();
                request = new Request(commandName, commandArgs, lab);
            } else {
                request = new Request(commandName, commandArgs);
            }

            Response response = networkManager.sendAndReceive(request);
            if (response != null) {
                System.out.println(response.getMessage());
            } else {
                System.out.println("Ошибка: не удалось получить ответ от сервера.");
            }
        }
    }

    /**
     * Выполняет скрипт из указанного файла.
     * @param fileName имя файла или путь к нему
     */
    private static void executeScript(String fileName) {
        File file = new File(fileName);
        String absolutePath = file.getAbsolutePath();

        if (scriptStack.contains(absolutePath)) {
            System.out.println("Ошибка: Обнаружена рекурсия в скрипте: " + fileName);
            return;
        }

        scriptStack.add(absolutePath);
        try (Scanner scriptScanner = new Scanner(file)) {
            LabWorkReader oldReader = labWorkReader;
            labWorkReader = new LabWorkReader(scriptScanner);
            
            processInput(scriptScanner, true);
            
            labWorkReader = oldReader;
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: Файл не найден: " + fileName);
        } finally {
            scriptStack.remove(absolutePath);
        }
    }

    /**
     * Проверяет, требует ли команда ввод сложного объекта.
     * @param commandName имя команды
     * @return true, если требуется объект LabWork
     */
    private static boolean isObjectRequired(String commandName) {
        return commandName.equals("insert") || 
               commandName.equals("update") || 
               commandName.equals("replace_if_greater") || 
               commandName.equals("replace_if_lower");
    }
}
