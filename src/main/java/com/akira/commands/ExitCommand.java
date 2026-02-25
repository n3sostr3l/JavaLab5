package com.akira.commands;

public class ExitCommand implements Command{
    @Override
    public void execute() {
        System.exit(200);
    }

    @Override
    public void describe() {
        System.out.println("exit : завершить программу (без сохранения в файл)");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}