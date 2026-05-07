package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.AuthCommand;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;
import com.akira.server.managers.CollectionManager;
import com.akira.server.managers.PostgresManager;

import java.util.ArrayList;

public class LoginCommand implements AuthCommand, Modable {
    private String login;
    private String passwordHash;

    @Override
    public Response execute(CollectionManager cm, String login){
        Response resp = PostgresManager.getInstance().loginUser(login, passwordHash)?new Response(String.format("Вы вошли под логином '%s'", login), true):
            new Response("Неверный логин или пароль. Попробуйте еще раз.", false);
        return resp;
    }
    @Override
    public String describe(){
        return "login {login} {password} --- залогиниться";
    }
    @Override
    public int numberArgsRequired(){
        return 2;
    }

    @Override
    public void setArguments(ArrayList<String> args) {
        if (args != null && args.size() >= 1) {
            this.login = args.get(0);
        }
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
