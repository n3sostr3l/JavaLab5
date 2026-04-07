package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.general.datas.LabWork;
import java.util.Hashtable;
import java.util.stream.Collectors;

/**
 * Команда вывода всех элементов коллекции.
 */
public class ShowCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        Hashtable<Integer, LabWork> collection = CollectionManager.getCollection();
        if (collection.isEmpty()) {
            return new Response("Коллекция пуста.", true);
        }
        
        String result = collection.entrySet().stream()
                .map(entry -> String.format("Ключ: %d, Значение: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
        
        return new Response(result, true, collection);
    }

    @Override
    public String describe() {
        return "show : вывести все элементы коллекции";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
