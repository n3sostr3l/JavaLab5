package com.akira.general.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда сохранения коллекции.
 */
public class SaveCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        if (CollectionManager.save()) {
            return new Response("Коллекция сохранена.", true);
        } else {
            return new Response("Ошибка при сохранении коллекции.", false);
        }
    }

    @Override
    public String describe() {
        return "save : сохранить коллекцию в файл (только сервер)";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
