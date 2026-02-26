package com.akira.commands;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ExecuteCommand implements Modable {

    private ArrayList<String> args = new ArrayList<>();

    @Override
    public void execute() {
        String fileName = args.get(0);
        File file = new File(fileName);
        try{
            CommandInvoker.runFile(file);
        }catch (Exception e){
            System.out.println("Ошибка чтения файла. Убедитесь, что файл находится по указанному вами пути относительно рабочей директории.");
        }
    }

    @Override
    public void describe() {
        System.out.println("execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }

    @Override
    public void setArguments(ArrayList<String> args_) {
        args = args_;
    }
}
