package com.akira.commands;

public interface Command {
    public void execute();

    public void describe();

    public int numberArgsRequired();
}