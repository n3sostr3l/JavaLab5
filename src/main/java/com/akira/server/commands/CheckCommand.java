package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;
import com.akira.server.managers.CollectionManager;
import com.akira.server.dao.DatabaseFacade;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Системная команда для проверки существования ключа или ID в коллекции.
 */
public class CheckCommand implements SystemCommand, Modable {
    /** Аргументы команды */
    private ArrayList<String> args = new ArrayList<>();
    
    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        String cmd = args.get(0);
        String ident = args.get(1);

        if (cmd.equals("insert")){
            HashSet<Integer> keys = new HashSet<>(collectionManager.getCollection().keySet());
            if (keys.size() >= 40000) return new Response("Добавление не удалось, переполнение памяти, удалите лабораторные", false);
            try {
                Integer key = Integer.parseInt(ident);
                String owner = DatabaseFacade.getInstance().getOwnerLoginByKey(key);
                if (owner == null) return new Response("ключ свободен", true);
                if (!owner.equals(login)) return new Response("Элемент не принадлежит вашему логину", false);
                return new Response("ключ занят", true);
            } catch (NumberFormatException e){
                return new Response("Неверный формат ключа", false);
            }
        }

        if (cmd.equals("update")){
            try {
                Long id = Long.parseLong(ident);
                String owner = DatabaseFacade.getInstance().getOwnerLoginById(id);
                if (owner == null) return new Response("id свободен", true);
                if (!owner.equals(login)) return new Response("Элемент не принадлежит вашему логину", false);
                return new Response("id занят", true);
            } catch (NumberFormatException e){
                return new Response("Неверный формат id", false);
            }
        }

        // default: work with key (remove/replace/etc.)
        try {
            Integer key = Integer.parseInt(ident);
            String owner = DatabaseFacade.getInstance().getOwnerLoginByKey(key);
            if (owner == null) return new Response("ключ свободен", true);
            if (!owner.equals(login)) return new Response("Элемент не принадлежит вашему логину", false);
            return new Response("ключ занят", true);
        } catch (NumberFormatException e){
            return new Response("Неверный формат ключа", false);
        }
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
