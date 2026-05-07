package com.akira.client;

import java.util.*;

import com.akira.client.reader.LabWorkReader;
import com.akira.client.security.Sha224HashStrategy;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

/**
 * Управляет интерактивной сессией обычного клиента.
 * Отвечает за:
 * <ul>
 *   <li>Основной цикл ввода команд.</li>
 *   <li>Маршрутизацию: клиентские команды ({@code exit}, {@code execute_script})
 *       обрабатываются локально; все остальные — отправляются на сервер.</li>
 *   <li>Блокировку серверной команды {@code save} для обычного клиента.</li>
 * </ul>
 */
public class ClientSession {
    private ArrayList<String> OBJECT_COMMANDS = new ArrayList<>();

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

    /**
     * Возвращает объект для чтения лабораторных работ.
     * @return объект чтения
     */
    public LabWorkReader getReader() { return reader; }
    /**
     * Устанавливает объект для чтения лабораторных работ.
     * @param reader объект чтения
     */
    public void setReader(LabWorkReader reader) { this.reader = reader; }

    /**
     * Инициализирует сессию на сервере (восстановление / чистый старт).
     */
    public void init() {
        Request req = new Request("init");
        req.setInit(true);
        req.setRestore(true);
        req.setAdmin(false);

        Response resp = network.sendAndReceive(req);
        if (resp != null) {
            System.out.println("Сервер: Сессия успешно инициализирована.");
        } else {
            System.out.println("Сервер не ответил на запрос инициализации сессии.");
        }
        
        OBJECT_COMMANDS.addAll(Arrays.asList(resp.getMessage().substring(1,resp.getMessage().length()-1).split(",")).stream()
                        .map(command -> command.trim())
                .toList());

        System.out.println("Войдите (login <login> <password>) или зарегистрируйтесь (reg <login> <password>)");
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
                System.out.print("Вы действительно хотите выйти? (y/n) (по умолчанию - n): ");
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

            Request request = null;
            Integer key = null;
            
            if (OBJECT_COMMANDS.contains(cmd)){
                if (cmdArgs.size() == 1){
                    Request rq = new Request("check", new ArrayList<>(List.of(cmd, cmdArgs.get(0))), true);
                    UserRegisty urCheck = UserRegisty.getInstance();
                    if (urCheck.getUserLogin() != null && urCheck.getPasswordHash() != null) {
                        rq.setLogin(urCheck.getUserLogin());
                        rq.setPasswordHash(urCheck.getPasswordHash());
                    }
                
                    Response response = network.sendAndReceive(rq);
                    if (response.getMessage().contains("Элемент не принадлежит вашему логину")){
                        System.out.println(response.getMessage());
                        continue;
                    }
                    
                    if (!response.isSuccess()){
                        System.out.println(response.getMessage());
                        continue;
                    }
                    if (cmd.equals("insert") && response.getMessage().contains("занят")) {
                        System.out.println("Такой ключ уже существует. Согласны перезаписать? (y/n) (по умолчанию - n): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();
                        if (!confirm.equals("y") && !confirm.equals("yes")) {
                            System.out.println("Операция отменена.");
                            continue;
                        }
                    }
                    if (response.getMessage().contains("свободен") && !cmd.equals("insert")) {
                        System.out.println("такого ключа нет, попробуйте другой (show для вывода всей коллекции)");
                        continue;
                    }

                    key = Integer.valueOf(cmdArgs.get(0));
                    request = new Request(cmd, cmdArgs, reader.readLabWork());
                }
                else{
                    System.out.println("Ошибка: неверное кол-во аргументов, смотри help.");
                    continue;
                }
            }
            else{
                request = new Request(cmd, cmdArgs);
            }

            if (request != null) {
                request.setAdmin(false);
            }

            // Attach authentication credentials to the request.
            String passArg = "";
            String loginArg = "";
            if (cmd.equals("login") || cmd.equals("reg")) {
                if (cmdArgs.size() >= 2) {
                    loginArg = cmdArgs.get(0);
                    passArg = cmdArgs.get(1);
                    String hash = PasswordEncryptor.getInstance().getPasswordHash(passArg);
                    request.setLogin(loginArg);
                    request.setPasswordHash(hash);
                    UserRegisty.getInstance().setUserLogin(loginArg).setPasswordHash(hash);
                }
            } else {
                UserRegisty ur = UserRegisty.getInstance();
                if (ur.getUserLogin() != null && ur.getPasswordHash() != null) {
                    request.setLogin(ur.getUserLogin());
                    request.setPasswordHash(ur.getPasswordHash());
                }
            }

            Response response = network.sendAndReceive(request);
            if (response != null){
                System.out.println(response.getMessage());

                if(response.getMessage().contains("Хотите изменить пароль")){
                    String line = scanner.nextLine();

                    Response resetResponse;
                    if(line.trim().equals("y")){
                        String passwordHash = new Sha224HashStrategy().hash(passArg);

                        Request resetRequest = new Request("reset_pwd", new ArrayList(Arrays.asList(loginArg, passArg)));

                        resetRequest.setLogin(loginArg);
                        resetRequest.setPasswordHash(passwordHash);
                        resetResponse = network.sendAndReceive(resetRequest);
                        System.out.println(resetResponse.getMessage());
                    
                    }
                    else{
                        System.out.println("Сброс пароля отменен.");
                        return;
                    }
                }
                continue;
            }
            System.out.println("Ошибка: нет ответа от сервера.");
        }
    }
}
