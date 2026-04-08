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
    /**
     * Выводит все элементы коллекции.
     * @param collectionManager менеджер коллекции
     * @return ответ со всеми элементами
     */
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

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "show : вывести все элементы коллекции";
    }

    /**
     * Возвращает количество аргументов.
     * @return 0
     */
    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
