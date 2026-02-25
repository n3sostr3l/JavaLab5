package com.akira.commands;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.LabWorkReader;

import java.util.ArrayList;

public class UpdateCommand implements Command, Modable{
    private ArrayList<String> args = new ArrayList<String>();

    public void execute() {
        try {
            Long id = Long.parseLong(args.get(0));
            if (!CollectionManager.existsById(id)) {
                System.out.println("Элемент с id " + id + " не найден.");
                return;
            }
            LabWork lab = LabWorkReader.readLabWork();
            CollectionManager.updateById(id, lab);
            System.out.println("Обновление успешно!");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: id должен быть числом.");
        }
    }

    public void describe() {
        System.out.println("update {id} : обновить значение элемента коллекции, id которого равен заданному");
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
