package com.akira.commands;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.Person;

/**
 * Команда вывода уникальных авторов.
 * <p>
 * Выводит уникальные значения поля author всех элементов в коллекции.
 * Использует {@link HashSet} для автоматического устранения дубликатов.
 * </p>
 */
public class UniqueAuthorCommand implements Command{

    /**
     * Выполняет команду print_unique_author.
     * <p>
     * Извлекает всех авторов из элементов коллекции, добавляет их в множество
     * для устранения дубликатов и выводит уникальных авторов в консоль.
     * </p>
     */
    @Override
    public void execute() {
        Set<Person> unique_authors = new HashSet<>();
        Hashtable<Integer, LabWork> coll = CollectionManager.getCollection();
        for (LabWork lab : coll.values()){
            unique_authors.add(lab.getAuthor());
        }
        for (Person author : unique_authors){
            System.out.println(author);
        }
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("print_unique_author : вывести уникальные значения поля author всех элементов в коллекции");
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
