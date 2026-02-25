package com.akira.commands;

import java.util.ArrayList;

import com.akira.CollectionManager;
import com.akira.LabWork;
import com.akira.LabWorkReader;

/**
 * Команда обновления элемента коллекции по id.
 * <p>
 * Обновляет значение элемента, id которого совпадает с заданным.
 * Новые данные запрашиваются у пользователя в интерактивном режиме.
 * Оригинальные id и дата создания элемента сохраняются.
 * </p>
 */
public class UpdateCommand implements Command, Modable{
    /** Список аргументов команды */
    private ArrayList<String> args = new ArrayList<String>();

    /**
     * Выполняет команду update.
     * <p>
     * Парсит id из аргументов команды, проверяет существование элемента,
     * запрашивает новые данные и обновляет элемент в коллекции.
     * </p>
     */
    public void execute() {
        try {
            Long id = Long.parseLong(args.get(0));
            LabWork lab = LabWorkReader.readLabWork();
            if (!CollectionManager.existsById(id)) {
                System.out.println("Элемент с id " + id + " не найден.");
                return;
            }
            CollectionManager.updateById(id, lab);
            System.out.println("Обновление успешно!");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: id должен быть числом.");
        }
    }

    /**
     * Выводит описание команды.
     */
    public void describe() {
        System.out.println("update {id} : обновить значение элемента коллекции, id которого равен заданному");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 1 — команда требует один аргумент (id)
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
