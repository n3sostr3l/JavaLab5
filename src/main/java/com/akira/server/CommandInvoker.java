package com.akira.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.akira.server.commands.*;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.SystemCommand;
import com.akira.server.managers.CollectionManager;

/**
 * Класс для управления и выполнения команд приложения на сервере.
 */
public class CommandInvoker {
    private static final HashMap<String, Command> commands = new HashMap<>();

    static{

        commands.put("check", new CheckCommand());
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("insert", new InsertCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_key", new RemoveCommand());
        commands.put("clear", new ClearCommand());
        // commands.put("save", new SaveCommand());
        commands.put("exit", new ExitCommand());
        commands.put("execute_script", new ExecuteCommand());
        commands.put("replace_if_greater", new ReplaceGreatestCommand());
        commands.put("replace_if_lower", new ReplaceLowestCommand());
        commands.put("remove_lower_key", new RemoveLowerElementsCommand());
        commands.put("group_counting_by_maximum_point", new GroupCountingByMaximumPointCommand());
        commands.put("print_unique_author", new UniqueAuthorCommand());
        commands.put("print_field_descending_difficulty", new PrintFieldDescendingDifficultyCommand());
        commands.put("add_random", new AddRandomCommand());
        commands.put("exit_server", new ExitServerCommand());
        commands.put("login", new LoginCommand());
        commands.put("reg", new RegisterCommand());

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
                .filter(entry -> !(entry.getValue() instanceof SystemCommand))
                .filter(entry -> entry.getValue() instanceof ObjectModable)

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

        if((command instanceof SystemCommand) && !request.isSystemRequest())
            return new Response("Команда не доступна простым смертным, она системная (!)", false);

        if (command == null) {
            return new Response("Ошибка: команда '" + request.getCommandName() + "' не найдена.", false);
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

        return command.execute(collectionManager);

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
