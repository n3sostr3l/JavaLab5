package com.akira.command;

public interface Command {
    public void execute();
    public void describe();
    public int numberArgsRequired();
}