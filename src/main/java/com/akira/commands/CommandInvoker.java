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
        commands.put("help", new HelpCommand()); //done
        commands.put("clear", new ClearCommand()); //done
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("exit", new ExitCommand());
        commands.put("execute_file", new ExecuteCommand());
        commands.put("insert", new InsertCommand());
        commands.put("update", new UpdateCommand());
    }
    public static void stop(){

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
        if (command instanceof Modable) {
            if(args.size()==command.numberArgsRequired())
                ((Modable) command).setArguments(args);
            else
                System.out.println("Слишком мало/много аргументов. Нужно %d аргументов.");
        }
        command.execute();
    }
}