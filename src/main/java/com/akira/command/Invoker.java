package com.akira.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Invoker {
    public void run(){

        HashMap<String, Command> commands= new HashMap<String, Command>();
        commands.put("help", new HelpCommand());
        commands.put("clear", new ClearCommand());

        Scanner sc = new Scanner(System.in);

        while(sc.hasNext()){
            String line = sc.nextLine();
            String[] tokens = line.split(" ");
            String commandName = tokens[0];
            ArrayList<String> args = new ArrayList<>();
            if(tokens.length > 1){
                for(int i = 1;i<tokens.length;i++) {
                    args.add(tokens[i]);
                }
            }
            Command command = commands.get(commandName);
            if(command instanceof Modable){
                ((Modable) command).setArguments(args);
            }
            command.execute();

        }
    }
}