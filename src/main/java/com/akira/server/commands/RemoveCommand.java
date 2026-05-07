package com.akira.server.commands;

import java.util.ArrayList;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

/**
 * Команда удаления элемента из коллекции по ключу.
 */
public class RemoveCommand implements Modable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();

    /**
     * Выполняет удаление элемента по ключу.
     * @param collectionManager менеджер коллекции
     * @return ответ с результатом удаления
     */
    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        try {
            if (args.isEmpty()) {
                return new Response("Ошибка: не указан ключ.", false);
            }
            Integer key = Integer.parseInt(args.get(0));
            if (CollectionManager.getCollection().containsKey(key)) {
                boolean isSuccess = CollectionManager.removeByKey(login, key);
                if (!isSuccess) {
                    return new Response("Ошибка при удалении элемента. Не Ваш элемент", false);
                }
                return new Response("Элемент с ключом " + key + " успешно удален.", true);
            } else {
                return new Response("Ошибка: элемент с таким ключом не найден.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ключ должен быть целым числом.", false);
        }
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "remove_key {key} : удалить элемент из коллекции по его ключу";
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
}
