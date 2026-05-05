package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

/**
 * Команда завершения работы сервера (админская).
 */
public class ExitServerCommand implements Command {
    /**
     * Выполняет команду завершения сервера.
     * @param collectionManager менеджер коллекции
     * @return ответ с сообщением о завершении
     */
    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        // Логика завершения будет обработана в ServerManager или Main через специальный статус ответа
        // Но для протокола:
        return new Response("Сервер завершает работу по требованию администратора.", true);
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "exit_server : завершить работу сервера (только для админа)";
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
