package com.akira.commands;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Команда выполнения команд из указанного файла.
 * <p>
 * Считывает и исполняет скрипт из указанного файла.
 * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь
 * в интерактивном режиме.
 * </p>
 */
public class ExecuteCommand implements Modable {

    /** Список аргументов команды */
    private ArrayList<String> args = new ArrayList<>();
    private static HashSet<String> filesQuery = new HashSet<String>();

    /**
     * Выполняет команду execute_file.
     * <p>
     * Читает имя файла из аргументов команды и передаёт его
     * в {@link CommandInvoker#runFile(File)} для выполнения.
     * </p>
     */
    @Override
    public void execute() {
        String fileName = args.get(0);
        Path path = Paths.get(fileName);

        filesQuery.add(path.toString());

        try{
            CommandInvoker.runFile(path);
        }catch (Exception e){
            System.out.println("Ошибка чтения файла. Убедитесь, что файл находится по указанному вами пути относительно рабочей директории.");
        }

        filesQuery.remove(path.toString());
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("execute_file file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 1 — команда требует один аргумент (имя файла)
     */
    @Override
    public int numberArgsRequired() {
        return 1;
    }

    /**
     * Устанавливает аргументы команды.
     *
     * @param args_ список аргументов командной строки
     */
    @Override
    public void setArguments(ArrayList<String> args_) {
        args = args_;
    }

    public ArrayList<String> getArguments(){
        return args;
    }

    public static HashSet<String> getFilesQuery(){
        return filesQuery;
    }
}
