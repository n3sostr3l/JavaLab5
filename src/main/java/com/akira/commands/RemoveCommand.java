package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;

public class RemoveCommand implements Command, Modable{
    private ArrayList<String> args;
    @Override
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            CollectionManager.removeByKey(key);
            System.out.println("Коллекция стала свободнее!");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    @Override
    public void describe() {
        System.out.println("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }

    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }
}
