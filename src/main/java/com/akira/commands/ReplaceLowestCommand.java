package com.akira.commands;

import java.util.ArrayList;
import java.util.Hashtable;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.LabWorkReader;

public class ReplaceLowestCommand implements Command, Modable {
    private ArrayList<String> args = new ArrayList<>();

    @Override
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
            if (!coll.containsKey(key)) {
                System.out.println("Элемент с ключом " + key + " не найден.");
                return;
            }
            LabWork newLab = LabWorkReader.readLabWork();
            LabWork oldLab = coll.get(key);
            if (newLab.compareTo(oldLab) < 0) {
                newLab.setCreationDate(oldLab.getCreationDate());
                CollectionManager.update(key, newLab);
                System.out.println("Значение заменено.");
            } else {
                System.out.println("Новое значение не меньше старого. Замена не выполнена.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    @Override
    public void describe() {
        System.out.println("replace_if_lower {key} : заменить значение по ключу, если новое значение меньше старого");
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
