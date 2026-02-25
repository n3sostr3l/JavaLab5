package com.akira.commands;

public class ExitCommand implements Command{
    @Override
    public void execute(CommandInvoker i) {
        i.stop();
    }

    @Override
    public void describe() {

    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}