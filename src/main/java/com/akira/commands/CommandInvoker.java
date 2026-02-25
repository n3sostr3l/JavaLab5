package com.akira.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CommandInvoker {
    private static Scanner sc = new Scanner(System.in);

    public CommandInvoker() {
    }
    public static void stop(){

    }
    public static void run() {

        HashMap<String, Command> commands = new HashMap<String, Command>();
        commands.put("help", new HelpCommand()); //done
        commands.put("clear", new ClearCommand()); //done
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("exit", new ExitCommand());



        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] tokens = line.split(" ");
            String commandName = tokens[0];
            ArrayList<String> args = new ArrayList<>();
            if (tokens.length > 1) {
                for (int i = 1; i < tokens.length; i++) {
                    args.add(tokens[i]);
                }
            }
            Command command = commands.get(commandName);
            if (command instanceof Modable) {
                if(args.size()==command.numberArgsRequired())
                    ((Modable) command).setArguments(args);
                else
                    System.out.println("Слишком мало/много аргументов. Нужно %d аргументов.");
            }
            command.execute();

        }
    }
}