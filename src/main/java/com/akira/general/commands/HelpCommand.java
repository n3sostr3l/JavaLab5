package com.akira.general.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import java.util.ArrayList;

/**
 * Команда вывода справки по доступным командам.
 */
public class HelpCommand implements Command {
    /**
     * Конструктор по умолчанию.
     */
    public HelpCommand() {}

    @Override
    public Response execute(CollectionManager collectionManager) {
        StringBuilder result = new StringBuilder("Доступные команды:\n");
        ArrayList<Command> allCommands = new ArrayList<>();
        allCommands.add(new HelpCommand());
        allCommands.add(new ClearCommand());
        allCommands.add(new InfoCommand());
        allCommands.add(new InsertCommand());
        allCommands.add(new UpdateCommand());
        allCommands.add(new ExitCommand());
        allCommands.add(new ExecuteCommand());
        allCommands.add(new ShowCommand());
        allCommands.add(new UniqueAuthorCommand());
        allCommands.add(new SaveCommand());
        allCommands.add(new RemoveCommand());
        allCommands.add(new GroupCountingByMaximumPointCommand());
        allCommands.add(new PrintFieldDescendingDifficultyCommand());
        allCommands.add(new ReplaceGreatestCommand());
        allCommands.add(new ReplaceLowestCommand());
        allCommands.add(new RemoveLowerElementsCommand());
        allCommands.add(new AddRandomCommand());

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
