package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.CollectionManager;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;

import java.sql.Array;
import java.util.ArrayList;

public class CheckCommand implements SystemCommand, Modable {
    private ArrayList<String> args = new ArrayList<>();

    @Override
    public Response execute(CollectionManager collectionManager) {
        return switch (args.getFirst()){
            case "id" -> new Response(ifIdIsTaken()?"id занят":"", true);
            case "key" -> new Response(ifKeyIsTaken()?"ключ занят":"", true);
            default -> new Response("",true);
        };
    }

    @Override
    public String describe() {
        return "";
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
            return true;
        }
        return false;
    }

    private boolean ifKeyIsTaken(){
        Integer key = Integer.parseInt(args.get(1));
        if(CollectionManager.getCollection().get(key)!=null){
            return true;
        }
        return false;
    }
}
