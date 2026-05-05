package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

/**
 * Команда вывода информации о коллекции.
 */
public class InfoCommand implements Command {
    /**
     * Выводит информацию о коллекции.
     * @param collectionManager менеджер коллекции
     * @return ответ с информацией
     */
    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        String result = String.format(
                """
                Информация о коллекции:
                Тип: %s
                Дата создания: %s (до момента последнего перезапуска сервера)
                Количество элементов в коллекции: %d
                """,
                CollectionManager.getCollection().getClass().getSimpleName(),
                CollectionManager.getCollectionCreationTime(),
                CollectionManager.getCollection().size()
        );
        return new Response(result, true);
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "info : вывести информацию о коллекции";
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
