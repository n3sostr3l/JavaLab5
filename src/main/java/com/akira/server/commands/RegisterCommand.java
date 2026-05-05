package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.managers.CollectionManager;
import com.akira.server.managers.PostgresManager;

import java.util.ArrayList;

public class RegisterCommand implements Command, Modable {
    private String login;
    private String passwordHash;

    @Override
    public Response execute(CollectionManager cm, String login){
        Response resp = PostgresManager.getInstance().registerUser(login, passwordHash)?new Response(String.format("Вы зарегистрировались с логином %s", login), true)
                :new Response("Ошибка при регистрации. Измените логин и повторите попытку.", false);
    }
    @Override
    public String describe(){
        return "reg {login} {password} - зарегистрироваться";
    }
    @Override
    public void setArguments(ArrayList<String> args){
        login = args.get(0);
        passwordHash = args.get(1);
    }

    @Override
    public int numberArgsRequired(){
        return 2;
    }
}
