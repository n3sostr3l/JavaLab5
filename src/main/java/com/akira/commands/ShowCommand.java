package com.akira.commands;

import com.akira.CollectionManager;

public class ShowCommand implements Command{
    @Override
    public void execute() {
        System.out.println(CollectionManager.getCollection());
    }

    @Override
    public void describe() {
        System.out.println("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
