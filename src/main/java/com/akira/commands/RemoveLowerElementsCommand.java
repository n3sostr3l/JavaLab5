package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;

public class RemoveLowerElementsCommand implements Command, Modable {
    private ArrayList<String> args = new ArrayList<>();

    @Override
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            int removed = CollectionManager.removeLowerKeys(key);
            System.out.println("Удалено элементов: " + removed);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    @Override
    public void describe() {
        System.out.println("remove_lower_key {key} : удалить из коллекции все элементы, ключ которых меньше, чем заданный");
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
