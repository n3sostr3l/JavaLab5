package com.akira.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.akira.CollectionManager;
import com.akira.LabWork;

public class PrintFieldDescendingDifficultyCommand implements Command {

    @Override
    public void execute() {
        Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
        List<Map.Entry<Integer, LabWork>> entries = new ArrayList<>();

        for (Map.Entry<Integer, LabWork> entry : coll.entrySet()) {
            if (entry.getValue().getDifficulty() != null) {
                entries.add(entry);
            }
        }

        entries.sort((a, b) -> b.getValue().getDifficulty().compareTo(a.getValue().getDifficulty()));

        for (Map.Entry<Integer, LabWork> entry : entries) {
            System.out.println(entry.getValue().getDifficulty() + " - id: " + entry.getValue().getId());
        }
    }

    @Override
    public void describe() {
        System.out.println("print_field_descending_difficulty : вывести значения поля difficulty всех элементов в порядке убывания");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
