package com.akira.server;

import java.util.HashMap;

import com.akira.server.commands.AddRandomCommand;
import com.akira.server.commands.ClearCommand;
import com.akira.server.commands.ExecuteCommand;
import com.akira.server.commands.ExitCommand;
import com.akira.server.commands.GroupCountingByMaximumPointCommand;
import com.akira.server.commands.HelpCommand;
import com.akira.server.commands.InfoCommand;
import com.akira.server.commands.InsertCommand;
import com.akira.server.commands.PrintFieldDescendingDifficultyCommand;
import com.akira.server.commands.RemoveCommand;
import com.akira.server.commands.RemoveLowerElementsCommand;
import com.akira.server.commands.ReplaceGreatestCommand;
import com.akira.server.commands.ReplaceLowestCommand;
import com.akira.server.commands.SaveCommand;
import com.akira.server.commands.ShowCommand;
import com.akira.server.commands.UniqueAuthorCommand;
import com.akira.server.commands.UpdateCommand;
import com.akira.server.commands.ExitServerCommand;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

/**
 * Класс для управления и выполнения команд приложения на сервере.
 */
public class CommandInvoker {
    private final HashMap<String, Command> commands = new HashMap<>();

    public CommandInvoker() {
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("insert", new InsertCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_key", new RemoveCommand());
        commands.put("clear", new ClearCommand());
        commands.put("save", new SaveCommand());
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
    }

    public Response executeRequest(Request request, CollectionManager collectionManager) {
        if (request.isInit()) {
            CollectionManager.loadSession(request.isRestore());
            return new Response("Сессия " + (request.isRestore() ? "восстановлена" : "инициализирована") + " успешно.", true);
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
        if (command == null) {
            return new Response("Ошибка: команда '" + request.getCommandName() + "' не найдена.", false);
        }

        Response response;

        try {
            if (command instanceof Modable) {
                ((Modable) command).setArguments(request.getArgs());
            }
            if (command instanceof ObjectModable) {
                ((ObjectModable) command).setObject(request.getObjectArgument());
            }
            response  = command.execute(collectionManager);
        }catch (Exception e){
            response = new Response(String.format("Ошибка при задании аргументов команды. Команда требует %d арументов (см. help, anyway)", command.numberArgsRequired()), false);
        }



        return response;
    }
}
