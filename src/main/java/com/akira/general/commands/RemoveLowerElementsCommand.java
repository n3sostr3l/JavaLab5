package com.akira.general.commands;

import java.util.ArrayList;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда удаления элементов с ключами меньше заданного.
 */
public class RemoveLowerElementsCommand implements Modable {
    private ArrayList<String> args = new ArrayList<>();

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

    @Override
    public String describe() {
        return "remove_lower_key {key} : удалить из коллекции все элементы, ключ которых меньше, чем заданный";
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }

    @Override
    public void setArguments(ArrayList<String> ar) {
        this.args = ar;
    }
}
