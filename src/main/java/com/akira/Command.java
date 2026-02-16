package com.akira;

public interface Command {
    public void execute();

    public void describe();

    public int numberArgsRequired();
}