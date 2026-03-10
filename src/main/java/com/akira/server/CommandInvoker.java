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
        register(new HelpCommand());
        register(new InfoCommand());
        register(new ShowCommand());
        register(new InsertCommand());
        register(new UpdateCommand());
        register(new RemoveCommand());
        register(new ClearCommand());
        register(new SaveCommand());
        register(new ExitCommand());
        register(new ReplaceGreatestCommand());
        register(new ReplaceLowestCommand());
        register(new RemoveLowerElementsCommand());
        register(new GroupCountingByMaximumPointCommand());
        register(new UniqueAuthorCommand());
        register(new PrintFieldDescendingDifficultyCommand());
        register(new AddRandomCommand());
    }

    private void register(Command command) {
        String name = command.getClass().getSimpleName().replace("Command", "").toLowerCase();
        // Специальные имена для некоторых команд
        if (name.equals("remove")) name = "remove_key";
        if (name.equals("removelowerelements")) name = "remove_lower_key";
        if (name.equals("replacegreatest")) name = "replace_if_greater";
        if (name.equals("replacelowest")) name = "replace_if_lowe";
        if (name.equals("uniqueauthor")) name = "print_unique_author";
        if (name.equals("printfielddescendingdifficulty")) name = "print_field_descending_difficulty";
        
        commands.put(name, command);
    }

    public Response executeRequest(Request request, CollectionManager collectionManager) {
        Command command = commands.get(request.getCommandName());
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
