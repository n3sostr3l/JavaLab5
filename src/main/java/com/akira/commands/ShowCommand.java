package com.akira.commands;

import com.akira.CollectionManager;

/**
 * Команда вывода всех элементов коллекции.
 * <p>
 * Отображает строковое представление всех элементов,
 * хранящихся в коллекции лабораторных работ.
 * </p>
 */
public class ShowCommand implements Command{
    /**
     * Выполняет команду show.
     * <p>
     * Выводит в консоль строковое представление всей коллекции
     * путём вызова метода {@code toString()} над объектом коллекции.
     * </p>
     */
    @Override
    public void execute() {
        System.out.println(CollectionManager.getCollection());
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
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
