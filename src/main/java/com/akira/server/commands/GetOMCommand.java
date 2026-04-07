package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;

public class GetOMCommand implements Command {
    @Override
    public Response execute(CollectionManager collectionManager) {

        return new Response(String.format("%s", CommandInvoker.getCommandsList().stream()
                .filter(command -> !(command instanceof SystemCommand))
                .filter(command -> command instanceof Modable)
                        .map(command -> CommandInvoker.getCommandsMap().entrySet().stream()
                                .filter(entry -> command.equals(entry.getValue()))
                                .findFirst()
                                .get()
                                .getKey()
                        ).toList()

                ), true);
    }

    @Override
    public String describe() {
        return "";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
