package com.akira.commands;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import com.akira.CollectionManager;
import com.akira.LabWork;

public class GroupCountingByMaximumPointCommand implements Command {

    @Override
    public void execute() {
        Hashtable<String, LabWork> coll = CollectionManager.getCollection();
        Map<Long, Integer> groups = new TreeMap<>();

        for (LabWork lab : coll.values()) {
            long mp = lab.getMaximumPoint();
            groups.put(mp, groups.getOrDefault(mp, 0) + 1);
        }

        for (Map.Entry<Long, Integer> entry : groups.entrySet()) {
            System.out.println("maximumPoint = " + entry.getKey() + " : " + entry.getValue() + " элемент(ов)");
        }
    }

    @Override
    public void describe() {
        System.out.println("group_counting_by_maximum_point : сгруппировать элементы коллекции по значению поля maximumPoint, вывести количество элементов в каждой группе");
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
