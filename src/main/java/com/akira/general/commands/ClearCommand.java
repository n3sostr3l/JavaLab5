package com.akira.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.server.CollectionManager;

/**
 * Команда очистки коллекции.
 * <p>
 * Удаляет все элементы из коллекции лабораторных работ.
 * </p>
 */
public class ClearCommand implements Command {

    /**
     * Выполняет команду clear.
     * <p>
     * Удаляет все элементы из коллекции путём вызова метода
     * {@link CollectionManager#clear()}.
     * </p>
     */
    @Override
    public void execute() {
        CollectionManager.clear();
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("clear : очистить коллекцию");
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
