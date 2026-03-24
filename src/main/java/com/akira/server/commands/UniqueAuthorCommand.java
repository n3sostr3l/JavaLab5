package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import java.util.stream.Collectors;

/**
 * Команда вывода уникальных авторов.
 */
public class UniqueAuthorCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        String result = CollectionManager.getCollection().values().stream()
                .map(lw -> lw.getAuthor())
                .distinct()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        
        return new Response("Уникальные авторы:\n" + result, true);
    }

    @Override
    public String describe() {
        return "print_unique_author : вывести уникальные значения поля author всех элементов";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
