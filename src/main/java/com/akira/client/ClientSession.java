package com.akira.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.akira.general.LabWorkReader;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

/**
 * Управляет интерактивной сессией обычного клиента.
 * <p>
 * Отвечает за:
 * <ul>
 *   <li>Основной цикл ввода команд.</li>
 *   <li>Маршрутизацию: клиентские команды ({@code exit}, {@code execute_script})
 *       обрабатываются локально; все остальные — отправляются на сервер.</li>
 *   <li>Блокировку серверной команды {@code save} для обычного клиента.</li>
 * </ul>
 * </p>
 */
public class ClientSession {
    private static final Set<String> OBJECT_COMMANDS = Set.of(
            "insert", "update", "replace_if_greater", "replace_if_lower");

    private final NetworkManager network;
    private LabWorkReader reader;
    private final ScriptExecutor scriptExecutor;

    /**
     * @param network менеджер сетевого взаимодействия
     * @param reader  читатель объектов LabWork из консоли
     */
    public ClientSession(NetworkManager network, LabWorkReader reader) {
        this.network = network;
        this.reader = reader;
        this.scriptExecutor = new ScriptExecutor(new HashSet<>(), this);
    }

    public LabWorkReader getReader() { return reader; }
    public void setReader(LabWorkReader reader) { this.reader = reader; }

    /**
     * Инициализирует сессию на сервере (восстановление / чистый старт).
     *
     * @param restore true, если требуется восстановить предыдущую сессию
     */
    public void init(boolean restore) {
        Request req = new Request("init");
        req.setInit(true);
        req.setRestore(restore);
        req.setAdmin(false);
        Response resp = network.sendAndReceive(req);
        if (resp != null) System.out.println("Сервер: " + resp.getMessage());
    }

    /**
     * Основной цикл чтения и выполнения команд.
     *
     * @param scanner  источник ввода
     * @param isScript {@code true}, если ввод читается из скрипт-файла
     */
    public void processInput(Scanner scanner, boolean isScript) {
        while (true) {
            if (!isScript) System.out.print("> ");
            if (!scanner.hasNextLine()) break;

            String input = scanner.nextLine().trim();
            if (input.isEmpty() || input.startsWith("#")) continue;

            String[] tokens = input.split("\\s+");
            String cmd = tokens[0].toLowerCase();
            ArrayList<String> cmdArgs = new ArrayList<>(Arrays.asList(tokens).subList(1, tokens.length));

            // --- Клиентские команды ---
            if (cmd.equals("exit")) {
                if (isScript) break;
                System.out.print("Вы действительно хотите выйти? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("y") || confirm.equals("yes")) {
                    System.out.println("Завершение работы клиента.");
                    break;
                }
                continue;
            }

            if (cmd.equals("execute_script")) {
                if (cmdArgs.isEmpty()) { System.out.println("Ошибка: не указан файл."); continue; }
                scriptExecutor.execute(cmdArgs.get(0));
                continue;
            }

            // --- Запрещённые у клиента команды ---
            if (cmd.equals("save")) {
                System.out.println("Ошибка: команда 'save' доступна только в admin-клиенте.");
                continue;
            }

            // --- Всё остальное — на сервер ---
            Request request = OBJECT_COMMANDS.contains(cmd)
                    ? new Request(cmd, cmdArgs, reader.readLabWork())
                    : new Request(cmd, cmdArgs);
            request.setAdmin(false);

            Response response = network.sendAndReceive(request);
            System.out.println(response != null ? response.getMessage()
                    : "Ошибка: нет ответа от сервера.");
        }
    }
}
