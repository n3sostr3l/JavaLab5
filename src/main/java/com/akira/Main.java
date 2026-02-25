package com.akira;

import com.akira.commands.CommandInvoker;

public class Main {
    public static void main(String[] args) {

        CommandInvoker commandInvoker = new CommandInvoker();
        commandInvoker.run();

    }
}