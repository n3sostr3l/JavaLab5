package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда сохранения коллекции.
 */
public class SaveCommand implements Command {
    /**
     * Выполняет сохранение коллекции в файл.
     * @param collectionManager менеджер коллекции
     * @return ответ с результатом сохранения
     */
    @Override
    public Response execute(CollectionManager collectionManager) {
        if (CollectionManager.save()) {
            return new Response("Коллекция сохранена.", true);
        } else {
            return new Response("Ошибка при сохранении коллекции.", false);
        }
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "save : сохранить коллекцию в файл (доступно админу)";
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
