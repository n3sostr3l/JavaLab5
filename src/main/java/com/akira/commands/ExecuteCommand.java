package com.akira.commands;

import java.io.InputStreamReader;
import java.util.ArrayList;

public class ExecuteCommand implements Modable {

    private ArrayList<String> args = new ArrayList<>();

    @Override
    public void execute() {
        String fileName = args.get(0);

        contents = new InputStreamReader(S)
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
