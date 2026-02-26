package com.akira.commands;

public class ExitCommand implements Command{
    @Override
    public void execute() {
        CommandInvoker.stop();
    }

    @Override
    public void describe() {
        System.out.println("exit : завершить программу (без сохранения в файл)\n");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}