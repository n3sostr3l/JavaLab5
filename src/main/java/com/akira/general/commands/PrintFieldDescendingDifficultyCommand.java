package com.akira.commands;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.datas.LabWork;
import com.akira.server.CollectionManager;

/**
 * Команда вывода значений сложности в порядке убывания.
 * <p>
 * Выводит значения поля difficulty всех элементов коллекции
 * в порядке убывания. Элементы без значения сложности пропускаются.
 * </p>
 */
public class PrintFieldDescendingDifficultyCommand implements Command {

    /**
     * Выполняет команду print_field_descending_difficulty.
     * <p>
     * Извлекает элементы с установленной сложностью, сортирует их
     * по убыванию сложности и выводит в консоль.
     * </p>
     */
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

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("print_field_descending_difficulty : вывести значения поля difficulty всех элементов в порядке убывания");
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
