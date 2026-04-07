package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.server.commands.interfaces.SystemCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetOMCommand implements SystemCommand {
    @Override
    public Response execute(CollectionManager collectionManager) {
        return new Response(String.format("%s", CommandInvoker.getCommandsMap().entrySet().stream()
                .filter(entry -> !(entry.getValue() instanceof SystemCommand))
                .filter(entry -> entry.getValue() instanceof ObjectModable)

                .map(Map.Entry::getKey)
                .toList()

                ), true);
    }

    @Override
    public String describe() {
        return "getomc : получить список всех команд, которые требуют введения объекта";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
