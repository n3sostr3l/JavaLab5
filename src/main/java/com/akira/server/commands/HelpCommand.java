package com.akira.server.commands;

import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.SystemCommand;
import com.akira.server.managers.CollectionManager;

import java.util.ArrayList;

/**
 * Команда вывода справки по доступным командам.
 */
public class HelpCommand implements Command {
    /**
     * Выводит справку по командам.
     * @param collectionManager менеджер коллекции
     * @return ответ со списком команд
     */
    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        StringBuilder result = new StringBuilder("Доступные команды:\n");
        ArrayList<Command> allCommands = new ArrayList<>(CommandInvoker.getCommandsList().stream()
                .filter(command -> !(command instanceof SystemCommand)).toList());

        for (Command command : allCommands) {
            result.append(command.describe()).append("\n");
        }
        return new Response(result.toString(), true);
    }

    /**
     * Возвращает описание команды.
     * @return строка описания
     */
    @Override
    public String describe() {
        return "help : вывести справку по доступным командам";
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
