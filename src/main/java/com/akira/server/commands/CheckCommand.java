package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;

import java.util.ArrayList;
import java.util.Map;

public class CheckCommand implements SystemCommand, Modable {
    private ArrayList<String> args = new ArrayList<>();

    @Override
    public Response execute(CollectionManager collectionManager) {
        String updateKey = CommandInvoker.getCommandsMap().entrySet().stream()
                .filter(e -> e.getValue() instanceof UpdateCommand )
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
        if(args.get(0).equals(updateKey)) return ifIdIsTaken()?new Response("id занят",true):new Response("id свободен", true);
        return new Response("",true);
    }

    @Override
    public String describe() {
        return "check {ObjectModable command} {id/key} : проверка корректности команды {command}";
    }

    @Override
    public int numberArgsRequired() {
        return 2;
    }

    @Override
    public void setArguments(ArrayList<String> args_){
        this.args=args_;
    }

    private boolean ifIdIsTaken(){
        Long id = Long.parseLong(args.get(1));
        if(!CollectionManager.getCollection().entrySet().stream()
                .filter(integerLabWorkEntry -> integerLabWorkEntry.getValue().getId().equals(id))
                .toList()
                .isEmpty()){
            return true;
        }
        return false;
    }
}
