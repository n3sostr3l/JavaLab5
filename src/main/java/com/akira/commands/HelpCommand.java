package com.akira.commands;

import java.util.ArrayList;

public class HelpCommand implements Command {

    @Override
    public void execute() {
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
        allCommands.add(new RemoveLowerElementsCommand());
        allCommands.add(new ExecuteCommand());
        for (Command c : allCommands) {
            c.describe();
        }
    }

    @Override
    public void describe() {
        System.out.println("help : вывести справку по доступным командам");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
