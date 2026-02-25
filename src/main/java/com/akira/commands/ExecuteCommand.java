package com.akira.commands;

import java.util.ArrayList;

public class ExecuteCommand implements Modable {

    private ArrayList<String> args = new ArrayList<>();

    @Override
    public void execute() {

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
