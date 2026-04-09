package com.akira.server.commands;

import java.util.ArrayList;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

/**
 * Команда удаления элементов с ключами меньше заданного.
 */
public class RemoveLowerElementsCommand implements Modable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();

    /**
     * Выполняет удаление элементов с ключами меньше заданного.
     * @param collectionManager менеджер коллекции
     * @return ответ с количеством удаленных элементов
     */
    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            if (args.isEmpty()) return new Response("Ошибка: не указан ключ.", false);
            Integer key = Integer.parseInt(args.get(0));
            
            java.util.List<Integer> keysToRemove = CollectionManager.getCollection().keySet().stream()
                    .filter(k -> k < key)
                    .collect(java.util.stream.Collectors.toList());
            
            keysToRemove.forEach(CollectionManager::removeByKey);
            
            return new Response("Удалено элементов: " + keysToRemove.size(), true);
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
        return "remove_lower_key {key} : удалить из коллекции все элементы, ключ которых меньше, чем заданный";
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
