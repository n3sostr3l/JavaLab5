package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.LabWorkReader;

public class InsertCommand implements Command, Modable{
    private ArrayList<String> args = new ArrayList<String>();

    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            LabWork lab = LabWorkReader.readLabWork();
            CollectionManager.insert(key, lab);
            System.out.println("Элемент успешно добавлен с ключом: " + key);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    public void describe() {
        System.out.println("insert {key} : добавить новый элемент с заданным ключом");
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
