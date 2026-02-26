package com.akira.commands;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Person> uniqueAuthors = CollectionManager.getCollection().values().stream().map(LabWork::getAuthor).filter(Objects::nonNull).collect(Collectors.toSet());
        uniqueAuthors.forEach(System.out::println);
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
