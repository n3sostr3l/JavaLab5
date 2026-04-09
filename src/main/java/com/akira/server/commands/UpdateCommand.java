package com.akira.server.commands;

import java.util.ArrayList;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.server.managers.CollectionManager;
import com.akira.general.datas.LabWork;
import com.akira.general.network.Response;

/**
 * Команда обновления элемента коллекции по id.
 */
public class UpdateCommand implements Modable, ObjectModable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();
    /** Объект лабораторной работы для обновления */
    private LabWork labWork;

    /**
     * Выполняет обновление элемента по его ID.
     * @param collectionManager менеджер коллекции
     * @return ответ с результатом обновления
     */
    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            if (args.isEmpty()) {
                return new Response("Ошибка: не указан id.", false);
            }
            Long id = Long.parseLong(args.get(0));
            if (labWork == null) {
                return new Response("Ошибка: объект для обновления не получен.", false);
            }
            
            Integer key = CollectionManager.getCollection().entrySet().stream()
                    .filter(entry -> entry.getValue().getId().equals(id))
                    .map(entry -> entry.getKey())
                    .findFirst()
                    .orElse(null);

            if (key == null) {
                return new Response("Ошибка: элемент с таким id не найден.", false);
            }

            if (CollectionManager.update(key, labWork)) {
                return new Response("Элемент с id " + id + " успешно обновлен.", true);
            } else {
                return new Response("Ошибка при обновлении элемента.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: id должен быть числом.", false);
        }
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
    }

    /**
     * Возвращает количество аргументов.
     * @return 1
     */
    @Override
    public int numberArgsRequired() {
        return 1;
    }

    /**
     * Устанавливает аргументы команды.
     * @param ar список аргументов
     */
    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }

    /**
     * Устанавливает объект для обновления.
     * @param labWork объект лабораторной работы
     */
    @Override
    public void setObject(LabWork labWork) {
        this.labWork = labWork;
    }
}
