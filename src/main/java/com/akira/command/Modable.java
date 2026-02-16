package com.akira.command;

import java.util.ArrayList;

public interface Modable {
    public void setArguments(ArrayList<String> args);
    public int numberArgsRequired();
}
