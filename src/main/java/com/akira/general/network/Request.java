package com.akira.general.network;

import com.akira.general.commands.interfaces.Command;
import java.io.Serializable;

/**
 * Класс запроса. Передает объект команды от клиента на сервер.
 * Это соответствует требованию ТЗ: «Команды и их аргументы — объекты классов».
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Command command;

    public Request(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Request: " + (command != null ? command.getClass().getSimpleName() : "null");
    }
}
