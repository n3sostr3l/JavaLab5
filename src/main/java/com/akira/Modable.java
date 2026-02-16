package com.akira;

import java.util.ArrayList;

public interface Modable {
    public void setArguments(ArrayList<String> args);

    public int numberArgsRequired();
}
