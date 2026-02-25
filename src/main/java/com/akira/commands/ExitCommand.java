package com.akira.commands;

public class ExitCommand implements Command{
    @Override
    public void execute() {
        CommandInvoker.stop();
    }

    @Override
    public void describe() {

    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}