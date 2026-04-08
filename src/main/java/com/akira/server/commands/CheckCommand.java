package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.CommandInvoker;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;


import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckCommand implements SystemCommand, Modable {
    private ArrayList<String> args = new ArrayList<>();
    
    @Override
    public Response execute(CollectionManager collectionManager) {
        String insertKey = CommandInvoker.getCommandsMap().entrySet().stream()
                .filter(e -> e.getValue() instanceof InsertCommand)
                .map(Map.Entry::getKey)
                .findFirst()
                .get();

//        List<String> replaceKeys = CommandInvoker.getCommandsMap().entrySet().stream()
//                .filter(e -> (e.getValue() instanceof ReplaceLowestCommand) || (e.getValue() instanceof ReplaceGreatestCommand) )
//                .map(Map.Entry::getKey)
//                .toList();

        String updateKey = CommandInvoker.getCommandsMap().entrySet().stream()
                .filter(e -> e.getValue() instanceof UpdateCommand )
                .map(Map.Entry::getKey)
                .findFirst()
                .get();

        String karg = "key";
        if(args.get(0).equals(insertKey)) return new Response(ifKeyIsTaken()?"ключ занят":"ключ свободен", true);
        if(args.get(0).equals(updateKey)) karg = "id";
        return switch (karg){
            case "id" -> new Response(ifIdIsTaken()?"id занят":"id свободен", true);
            case "key" -> new Response(ifKeyIsTaken()?"ключ занят":"ключ свободен", true);
            default -> new Response("",true);
        };
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
        if(CollectionManager.getCollection().entrySet().stream()
                .filter(integerLabWorkEntry -> integerLabWorkEntry.getValue().getId().equals(id))
                .toList()
                .isEmpty()){
            return false;
        }
        return true;
    }

    private boolean ifKeyIsTaken(){
        Integer key = Integer.parseInt(args.get(1));
        if(CollectionManager.getCollection().get(key)==null){
            return false;
        }
        return true;
    }
}
