package com.akira.commands;

import java.util.ArrayList;

public interface Modable extends Command {
    public void setArguments(ArrayList<String> args);
}
