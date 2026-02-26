package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.LabWorkReader;

/**
 * Команда добавления нового элемента в коллекцию.
 * <p>
 * Добавляет лабораторную работу в коллекцию с указанным ключом.
 * Данные элемента запрашиваются у пользователя в интерактивном режиме.
 * </p>
 */
public class InsertCommand implements Modable{
    /** Список аргументов команды */
    private ArrayList<String> args = new ArrayList<String>();

    /**
     * Выполняет команду insert.
     * <p>
     * Парсит ключ из аргументов команды, запрашивает у пользователя
     * данные лабораторной работы и добавляет созданный объект в коллекцию.
     * </p>
     */
    public void execute() {
        try {
            Integer key = Integer.parseInt(args.get(0));
            LabWork lab = LabWorkReader.readLabWork();
            CollectionManager.insert(key, lab);
            System.out.println("Элемент успешно добавлен с ключом: " + key);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ключ должен быть целым числом.");
        }
    }

    /**
     * Выводит описание команды.
     */
    public void describe() {
        System.out.println("insert {key} : добавить новый элемент с заданным ключом");
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
