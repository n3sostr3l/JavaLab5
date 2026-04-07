package com.akira.server.commands;

import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда завершения работы.
 */
public class ExitCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {
        return new Response("Завершение работы...", true);
    }

    @Override
    public String describe() {
        return "exit : завершить программу";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
