package com.akira.server.commands;

import java.util.ArrayList;
import java.util.HashSet;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.managers.CollectionManager;
import com.akira.general.network.Response;

/**
 * Команда выполнения команд из указанного файла.
 */
public class ExecuteCommand implements Modable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();
    private static HashSet<String> filesQuery = new HashSet<>();

    @Override
    public Response execute(CollectionManager collectionManager) {
        return new Response("Команда execute_script должна обрабатываться на стороне клиента.", true);
    }

    @Override
    public String describe() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла";
    }

    @Override
    public int numberArgsRequired() {
        return 1;
    }

    @Override
    public void setArguments(ArrayList<String> args_) {
        this.args = args_;
    }

    /**
     * Возвращает аргументы команды.
     * @return список аргументов
     */
    public ArrayList<String> getArguments() {
        return args;
    }

    /**
     * Возвращает очередь файлов для выполнения.
     * @return набор путей к файлам
     */
    public static HashSet<String> getFilesQuery() {
        return filesQuery;
    }
}
