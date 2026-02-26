package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;

/**
 * Команда удаления элементов с ключами меньше заданного.
 * <p>
 * Удаляет из коллекции все элементы, ключ которых меньше, чем заданный.
 * </p>
 */
public class RemoveLowerElementsCommand implements Command, Modable {
    /** Список аргументов команды */
    private ArrayList<String> args = new ArrayList<>();

    /**
     * Выполняет команду remove_lower_key.
     * <p>
     * Парсит ключ из аргументов команды и удаляет все элементы
     * коллекции, ключ которых меньше заданного значения.
     * Выводит количество удалённых элементов.
     * </p>
     */
    @Override
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            int removed = CollectionManager.removeLowerKeys(key);
            System.out.println("Удалено элементов: " + removed);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("remove_lower_key {key} : удалить из коллекции все элементы, ключ которых меньше, чем заданный");
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
