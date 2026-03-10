package com.akira.general.commands;

import com.akira.general.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда вывода информации о коллекции.
 */
public class InfoCommand implements Command {
    /**
     * Конструктор по умолчанию.
     */
    public InfoCommand() {}

    @Override
    public Response execute(CollectionManager collectionManager) {
        String result = String.format(
                """
                Информация о коллекции:
                Тип: %s
                Дата создания: %s
                Количество элементов в коллекции: %d
                """,
                CollectionManager.getCollection().getClass().getSimpleName(),
                CollectionManager.getCollectionCreationTime(),
                CollectionManager.getCollection().size()
        );
        return new Response(result, true);
    }

    @Override
    public String describe() {
        return "info : вывести информацию о коллекции";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
