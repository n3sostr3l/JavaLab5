package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.AuthCommand;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.managers.CollectionManager;
import com.akira.server.managers.PostgresManager;

import java.util.ArrayList;

public class ResetPasswordCommand implements Modable, AuthCommand {
    private ArrayList<String> args;
    private String passwordHash;
    @Override
    public String describe(){
        return "reset_pwd {login} {new_password} -- изменить пароль по логину";
    }

    @Override
    public Response execute(CollectionManager cm, String login){
        Response response = new Response("Не удалось изменить пароль, неправильный логин.", false);
        boolean result = PostgresManager.getInstance().resetPassword(args.get(0), args.get(1));
        if(result) response = new Response("Пароль успешно изменен.", true);
        return response;
    }

    @Override
    public int numberArgsRequired(){
        return 2;
    }

    @Override
    public void setArguments(ArrayList<String> args_){
        this.args = args_;
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = args.get(1);
    }
}
