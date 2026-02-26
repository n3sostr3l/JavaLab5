package com.akira.commands;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import com.akira.CollectionManager;
import com.akira.LabWork;

/**
 * Команда группировки элементов по максимальному баллу.
 * <p>
 * Сгруппирует элементы коллекции по значению поля maximumPoint
 * и выводит количество элементов в каждой группе.
 * Использует {@link TreeMap} для автоматической сортировки групп.
 * </p>
 */
public class GroupCountingByMaximumPointCommand implements Command {

    /**
     * Выполняет команду group_counting_by_maximum_point.
     * <p>
     * Группирует элементы коллекции по значению поля maximumPoint,
     * подсчитывает количество элементов в каждой группе и выводит
     * результат в консоль.
     * </p>
     */
    @Override
    public void execute() {
        Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
        Map<Long, Integer> groups = new TreeMap<>();

        for (LabWork lab : coll.values()) {
            long mp = lab.getMaximumPoint();
            groups.put(mp, groups.getOrDefault(mp, 0) + 1);
        }

        for (Map.Entry<Long, Integer> entry : groups.entrySet()) {
            System.out.println("maximumPoint = " + entry.getKey() + " : " + entry.getValue() + " элемент(ов)");
        }
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("group_counting_by_maximum_point : сгруппировать элементы коллекции по значению поля maximumPoint, вывести количество элементов в каждой группе");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 0 — команда не требует аргументов
     */
    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
