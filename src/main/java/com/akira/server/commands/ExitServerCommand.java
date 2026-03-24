package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда завершения работы сервера (админская).
 */
public class ExitServerCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        // Логика завершения будет обработана в ServerManager или Main через специальный статус ответа
        // Но для протокола:
        return new Response("Сервер завершает работу по требованию администратора.", true);
    }

    @Override
    public String describe() {
        return "exit_server : завершить работу сервера (только для админа)";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
