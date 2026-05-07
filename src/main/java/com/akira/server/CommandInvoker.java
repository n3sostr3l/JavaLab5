package com.akira.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.akira.server.commands.*;
import com.akira.server.commands.interfaces.*;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import com.akira.server.managers.CollectionManager;
import com.akira.server.dao.DatabaseFacade;

/**
 * Класс для управления и выполнения команд приложения на сервере.
 */
public class CommandInvoker {
    private static final HashMap<String, Command> commands = new HashMap<>();

    static{
        // Populate map lazily via factory
        String[] names = new String[]{"check","help","info","show","insert","update","remove_key","clear","exit","execute_script","replace_if_greater","replace_if_lower","remove_lower_key","group_counting_by_maximum_point","print_unique_author","print_field_descending_difficulty","add_random","exit_server","login","reg"};
        for(String n: names) {
            com.akira.server.commands.interfaces.Command c = com.akira.server.commands.patterns.CommandFactory.create(n);
            if (c != null) commands.put(n, (Command) c);
        }
    }

    /**
     * Выполняет запрос, пришедший от клиента.
     * @param request объект запроса
     * @param collectionManager менеджер коллекции
     * @return объект ответа
     */
    public Response executeRequest(Request request, CollectionManager collectionManager) {
        if (request.isInit()) {
            return new Response(String.format("%s", CommandInvoker.getCommandsMap().entrySet().stream()
                .filter(entry -> !(unwrap(entry.getValue()) instanceof SystemCommand))
                .filter(entry -> unwrap(entry.getValue()) instanceof ObjectModable)

                .map(Map.Entry::getKey)
                .toList()
                ), true);
        }

        String commandName = request.getCommandName().toLowerCase();
        
        // Проверка прав админа
        if (request.isAdmin()) {
            if (!commandName.equals("save") && !commandName.equals("exit_server")) {
                return new Response("Ошибка: Админу разрешены только команды: save, exit_server", false);
            }
        } else {
            if (commandName.equals("exit_server") || commandName.equals("save")) {
                return new Response("Ошибка: У вас нет прав для выполнения этой команды.", false);
            }
        }



        Command command = commands.get(commandName);
        if (!commandName.equals("login") && !commandName.equals("reg") && !request.isValid()) {
            return new Response("Не задан логин и пароль. Войдите или зарегистрируйтесь для работы", false);
        }
        if ((unwrap(command) instanceof SystemCommand) && !request.isSystemRequest())
            return new Response("Команда не доступна простым смертным, она системная (!)", false);

        if (command == null) {
            return new Response("Ошибка: команда '" + request.getCommandName() + "' не найдена.", false);
        }

        if (!commandName.equals("login") && !commandName.equals("reg")) {
            if (!DatabaseFacade.getInstance().loginUser(request.getLogin(), request.getPasswordHash())) {
                return new Response("Неверный логин или пароль. Выполните вход заново.", false);
            }
        }

        try {
            if (command instanceof Modable) {
                ((Modable) command).setArguments(request.getArgs());
            }
        }catch (Exception e){
            return new Response(String.format("Ошибка при задании аргументов команды. Команда требует %d арументов (см. help, anyway)", command.numberArgsRequired()), true);
        }

        try {
            if (command instanceof ObjectModable) {
                ((ObjectModable) command).setObject(request.getObjectArgument());
            }
        }catch (Exception e){
            return new Response(String.format("Ошибка при задании аргументов команды. Команда требует %d арументов (см. help, anyway)", command.numberArgsRequired()), true);
        }

        // If command requires auth, inject password hash from request
        if (command instanceof AuthCommand) {
            try {
                ((AuthCommand) command).setPasswordHash(request.getPasswordHash());
            } catch (Exception ignore) {}
        }

        return command.execute(collectionManager, request.getLogin());

    }

    private static Command unwrap(Command command) {
        Command current = command;
        while (current instanceof DelegatingCommand delegating) {
            current = delegating.getDelegate();
        }
        return current;
    }

    /**
     * Возвращает список всех доступных команд.
     * @return список команд
     */
    public static ArrayList<Command> getCommandsList(){
        return new ArrayList<>(commands.values());
    }

    /**
     * Возвращает карту соответствия имен команд и их объектов.
     * @return карта команд
     */
    public static HashMap<String, Command> getCommandsMap(){
        return commands;
    }
}
