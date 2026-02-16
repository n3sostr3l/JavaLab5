package com.akira.command;
import com.akira.command.Command;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements Command{

    @Override
    public void execute(){
        ArrayList<Command> allCommands = new ArrayList<>();
        allCommands.add(new HelpCommand());
        allCommands.add(new ClearCommand());

        for(Command c: allCommands){
            c.describe();
        }
    }
    @Override
    public void describe(){
        System.out.println("help : вывести справку по доступным командам");
    }
    @Override
    public int numberArgsRequired(){
        return 0;
    }
}
