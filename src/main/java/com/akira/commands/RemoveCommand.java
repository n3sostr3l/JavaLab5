package com.akira.commands;

import com.akira.CollectionManager;

import java.util.ArrayList;

public class RemoveCommand implements Modable{

    ArrayList<String> args = new ArrayList<>();

    @Override
    public void execute() {
        CollectionManager.getCollection().remove(args.get(0));
    }

    @Override
    public void describe() {
        System.out.println("remove_key null : удалить элемент из коллекции по его ключу");
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }

    @Override
    public void setArguments(ArrayList<String> args_){
        args = args_;
    }
}
