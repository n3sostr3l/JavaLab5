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

    /** Имя исполняемой команды */
    private final String commandName;
    /** Список строковых аргументов */
    private final ArrayList<String> args;
    /** Объектный аргумент команды (лабораторная работа) */
    private final LabWork objectArgument;
    /** Флаг прав администратора */
    private boolean isAdmin = false;
    /** Флаг инициализации сессии */
    private boolean isInit = false;
    /** Флаг восстановления сессии */
    private boolean restore = false;
    /** Флаг системного запроса */
    private boolean isSystemRequest = false;
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
     * Конструктор для системных запросов.
     * @param commandName имя команды
     * @param args список строковых аргументов
     * @param isSystemRequest флаг системного запроса
     */
    public Request(String commandName, ArrayList<String> args, boolean isSystemRequest){
        this(commandName, args, null);
        this.isSystemRequest = true;
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

    /**
     * Возвращает имя команды.
     * @return имя команды
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Возвращает список аргументов команды.
     * @return список аргументов
     */
    public ArrayList<String> getArgs() {
        return args;
    }

    /**
     * Возвращает объектный аргумент команды.
     * @return объект LabWork
     */
    public LabWork getObjectArgument() {
        return objectArgument;
    }

    /**
     * Проверяет, является ли пользователь администратором.
     * @return true, если администратор
     */
    public boolean isAdmin() { return isAdmin; }
    /**
     * Устанавливает флаг администратора.
     * @param admin флаг администратора
     */
    public void setAdmin(boolean admin) { isAdmin = admin; }

    /**
     * Проверяет, является ли запрос инициализацией.
     * @return true, если инициализация
     */
    public boolean isInit() { return isInit; }
    /**
     * Устанавливает флаг инициализации.
     * @param init флаг инициализации
     */
    public void setInit(boolean init) { isInit = init; }

    /**
     * Проверяет, является ли запрос восстановлением.
     * @return true, если восстановление
     */
    public boolean isRestore() { return restore; }
    /**
     * Устанавливает флаг восстановления.
     * @param restore флаг восстановления
     */
    public void setRestore(boolean restore) { this.restore = restore; }

    /**
     * Проверяет, является ли запрос системным.
     * @return true, если системный
     */
    public boolean isSystemRequest(){
        return this.isSystemRequest;
    }

    /**
     * Возвращает строковое представление запроса.
     * @return строковое представление
     */
    @Override
    public String toString() {
        return "Request[" + commandName + ", args=" + args + ", obj=" + (objectArgument != null ? "present" : "null") + "]";
    }
}
