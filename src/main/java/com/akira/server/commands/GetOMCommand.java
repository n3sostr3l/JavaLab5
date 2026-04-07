package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.SystemCommand;

public class GetOMCommand implements SystemCommand {
    @Override
    public Response execute(CollectionManager collectionManager) {

        return new Response(String.format("%s", CommandInvoker.getCommandsList()), true);
    }

    @Override
    public String describe() {
        return "getomc : [системная команда, не доступная простым смертным]";
    }

    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
