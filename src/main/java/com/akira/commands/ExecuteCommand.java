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
