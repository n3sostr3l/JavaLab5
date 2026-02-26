package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;

/**
 * Команда удаления элемента из коллекции по ключу.
 * <p>
 * Удаляет элемент из коллекции с указанным ключом.
 * </p>
 */
public class RemoveCommand implements Modable{
    /** Список аргументов команды */
    private ArrayList<String> args;

    /**
     * Выполняет команду remove_key.
     * <p>
     * Парсит ключ из аргументов команды и удаляет соответствующий
     * элемент из коллекции.
     * </p>
     */
    @Override
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            CollectionManager.removeByKey(key);
            System.out.println("Коллекция стала свободнее!");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("remove_key {key} : удалить элемент из коллекции по его ключу");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 1 — команда требует один аргумент (ключ)
     */
    @Override
    public int numberArgsRequired() {
        return 1;
    }

    /**
     * Устанавливает аргументы команды.
     *
     * @param ar список аргументов командной строки
     */
    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }
}
