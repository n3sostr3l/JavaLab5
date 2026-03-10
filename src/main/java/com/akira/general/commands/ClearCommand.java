package com.akira.general.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда очистки коллекции.
 */
public class ClearCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        CollectionManager.clear();
        return new Response("Коллекция успешно очищена.", true);
    }

    @Override
    public String describe() {
        return "clear : очистить коллекцию";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
