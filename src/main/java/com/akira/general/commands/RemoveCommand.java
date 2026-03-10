package com.akira.general.commands;

import java.util.ArrayList;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда удаления элемента из коллекции по ключу.
 */
public class RemoveCommand implements Modable {
    private ArrayList<String> args = new ArrayList<>();

    @Override
    public Response execute(CollectionManager collectionManager) {
        try {
            if (args.isEmpty()) {
                return new Response("Ошибка: не указан ключ.", false);
            }
            Integer key = Integer.parseInt(args.get(0));
            if (CollectionManager.getCollection().containsKey(key)) {
                CollectionManager.removeByKey(key);
                return new Response("Элемент с ключом " + key + " успешно удален.", true);
            } else {
                return new Response("Ошибка: элемент с таким ключом не найден.", false);
            }
        } catch (NumberFormatException e) {
            return new Response("Ошибка: ключ должен быть целым числом.", false);
        }
    }

    @Override
    public String describe() {
        return "remove_key {key} : удалить элемент из коллекции по его ключу";
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
