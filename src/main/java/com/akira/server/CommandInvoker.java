package com.akira.server;

import com.akira.general.commands.*;
import com.akira.general.commands.interfaces.Command;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.commands.interfaces.ObjectModable;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import java.util.HashMap;

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
        commands.put("replace_if_lowe", new ReplaceLowestCommand());
        commands.put("remove_lower_key", new RemoveLowerElementsCommand());
        commands.put("group_counting_by_maximum_point", new GroupCountingByMaximumPointCommand());
        commands.put("print_unique_author", new UniqueAuthorCommand());
        commands.put("print_field_descending_difficulty", new PrintFieldDescendingDifficultyCommand());
        commands.put("add_random", new AddRandomCommand());
    }

    public Response executeRequest(Request request, CollectionManager collectionManager) {
        Command command = commands.get(request.getCommandName().toLowerCase());
        if (command == null) {
            return new Response("Ошибка: команда '" + request.getCommandName() + "' не найдена.", false);
        }

        if (command instanceof Modable) {
            ((Modable) command).setArguments(request.getArgs());
        }
        if (command instanceof ObjectModable) {
            ((ObjectModable) command).setObject(request.getObjectArgument());
        }

        return command.execute(collectionManager);
    }
}
