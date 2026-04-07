package com.akira.server.commands;

import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.commands.interfaces.SystemCommand;

import java.util.ArrayList;

/**
 * Команда вывода справки по доступным командам.
 */
public class HelpCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        StringBuilder result = new StringBuilder("Доступные команды:\n");
        ArrayList<Command> allCommands = new ArrayList<>(CommandInvoker.getCommandsList().stream()
                .filter(command -> !(command instanceof SystemCommand)).toList());

        for (Command command : allCommands) {
            result.append(command.describe()).append("\n");
        }
        return new Response(result.toString(), true);
    }

    @Override
    public String describe() {
        return "help : вывести справку по доступным командам";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
