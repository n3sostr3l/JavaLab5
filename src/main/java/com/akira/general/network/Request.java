package com.akira.general.network;

import com.akira.general.datas.LabWork;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Класс запроса. Передает информацию о команде от клиента на сервер.
 * Согласно ТЗ: «Команды и их аргументы должны представлять из себя объекты классов».
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final ArrayList<String> args;
    private final LabWork objectArgument;

    /**
     * Конструктор для команд без аргументов.
     * @param commandName имя команды
     */
    public Request(String commandName) {
        this(commandName, new ArrayList<>(), null);
    }

    /**
     * Конструктор для команд со строковыми аргументами.
     * @param commandName имя команды
     * @param args список строковых аргументов
     */
    public Request(String commandName, ArrayList<String> args) {
        this(commandName, args, null);
    }

    /**
     * Конструктор для команд с объектом и строковыми аргументами.
     * @param commandName имя команды
     * @param args список строковых аргументов
     * @param objectArgument объект LabWork
     */
    public Request(String commandName, ArrayList<String> args, LabWork objectArgument) {
        this.commandName = commandName;
        this.args = args;
        this.objectArgument = objectArgument;
    }

    /**
     * Конструктор для команд с объектом без строковых аргументов.
     * @param commandName имя команды
     * @param objectArgument объект LabWork
     */
    public Request(String commandName, LabWork objectArgument) {
        this(commandName, new ArrayList<>(), objectArgument);
    }

    public String getCommandName() {
        return commandName;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public LabWork getObjectArgument() {
        return objectArgument;
    }

    @Override
    public String toString() {
        return "Request[" + commandName + ", args=" + args + ", obj=" + (objectArgument != null ? "present" : "null") + "]";
    }
}
