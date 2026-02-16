package com.akira;

public class ClearCommand implements Command {

    @Override
    public void execute() {
        CollectionManager.clear();
    }

    @Override
    public void describe() {
        System.out.println("clear : очистить коллекцию");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
