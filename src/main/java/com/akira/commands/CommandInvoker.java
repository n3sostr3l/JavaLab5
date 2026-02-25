package com.akira.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class CommandInvoker {
    private static Scanner sc = new Scanner(System.in);
    private static HashMap<String, Command> commands = new HashMap<String, Command>();

    public CommandInvoker() {
        commands.put("help", new HelpCommand()); //done 1
        commands.put("clear", new ClearCommand()); //done 1
        commands.put("info", new InfoCommand()); // done 1
        commands.put("show", new ShowCommand()); // done 1
        commands.put("exit", new ExitCommand()); // done 1
        commands.put("execute_file", new ExecuteCommand()); // done
        commands.put("insert", new InsertCommand()); // done 1
        commands.put("update", new UpdateCommand()); // done 1
        commands.put("print_unique_author", new UniqueAuthorCommand()); // done + 1
        commands.put("save", new SaveCommand()); // done 1
        commands.put("remove_key", new RemoveCommand()); // done
        commands.put("print_field_descending_difficulty", new PrintFieldDescendingDifficultyCommand()); // done
        commands.put("group_counting_by_maximum_point", new GroupCountingByMaximumPointCommand()); // done
        commands.put("remove_lower_key", new RemoveLowerElementsCommand()); // done
        commands.put("replace_if_greater", new ReplaceGreatestCommand()); // done
        commands.put("replace_if_lower", new ReplaceLowestCommand()); // done

    }
    public void run() {
        while (sc.hasNext()) {
            String line = sc.nextLine();
            runParticularCommand(line);
        }
    }

    public static void runFile(File f) {

        try (FileInputStream fis = new FileInputStream(f);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
             BufferedReader br = new BufferedReader(isr)){

            String line;
            while((line = br.readLine())!=null){
                runParticularCommand(line);
            }

        }catch(Exception e){

        }
    }

    public static Scanner getScanner() {
        return sc;
    }

    private static void runParticularCommand(String line){
        String[] tokens = line.split(" ");
        String commandName = tokens[0];
        ArrayList<String> args = new ArrayList<>();
        if (tokens.length > 1) {
            for (int i = 1; i < tokens.length; i++) {
                args.add(tokens[i]);
            }
        }
        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Неизвестная команда: " + commandName);
            return;
        }
        if (command instanceof Modable) {
            if(args.size()==command.numberArgsRequired())
                ((Modable) command).setArguments(args);
            else {
                System.out.println("Слишком мало/много аргументов. Нужно " + command.numberArgsRequired() + " аргументов.");
                return;
            }
        }
        command.execute();
    }
}