package com.akira.server;

import java.util.HashMap;

import com.akira.general.commands.AddRandomCommand;
import com.akira.general.commands.ClearCommand;
import com.akira.general.commands.ExecuteCommand;
import com.akira.general.commands.ExitCommand;
import com.akira.general.commands.GroupCountingByMaximumPointCommand;
import com.akira.general.commands.HelpCommand;
import com.akira.general.commands.InfoCommand;
import com.akira.general.commands.InsertCommand;
import com.akira.general.commands.PrintFieldDescendingDifficultyCommand;
import com.akira.general.commands.RemoveCommand;
import com.akira.general.commands.RemoveLowerElementsCommand;
import com.akira.general.commands.ReplaceGreatestCommand;
import com.akira.general.commands.ReplaceLowestCommand;
import com.akira.general.commands.SaveCommand;
import com.akira.general.commands.ShowCommand;
import com.akira.general.commands.UniqueAuthorCommand;
import com.akira.general.commands.UpdateCommand;
import com.akira.general.commands.interfaces.Command;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.commands.interfaces.ObjectModable;
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
