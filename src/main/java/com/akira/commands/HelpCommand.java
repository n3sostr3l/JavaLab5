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
