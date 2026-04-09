package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

import java.util.stream.Collectors;

/**
 * Команда вывода уникальных авторов.
 */
public class UniqueAuthorCommand implements Command {
    /**
     * Выводит уникальных авторов элементов коллекции.
     * @param collectionManager менеджер коллекции
     * @return ответ со списком авторов
     */
    @Override
    public Response execute(CollectionManager collectionManager) {
        String result = CollectionManager.getCollection().values().stream()
                .map(lw -> lw.getAuthor())
                .distinct()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
        
        return new Response("Уникальные авторы:\n" + result, true);
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "print_unique_author : вывести уникальные значения поля author всех элементов";
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
