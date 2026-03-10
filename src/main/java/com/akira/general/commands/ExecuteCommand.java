package com.akira.general.commands;

import java.util.ArrayList;
import java.util.HashSet;
import com.akira.general.commands.interfaces.Modable;
import com.akira.general.network.Response;
import com.akira.server.CollectionManager;

/**
 * Команда выполнения команд из указанного файла.
 */
public class ExecuteCommand implements Modable {
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

    public ArrayList<String> getArguments() {
        return args;
    }

    public static HashSet<String> getFilesQuery() {
        return filesQuery;
    }
}
