package com.akira.server.commands;

import com.akira.general.network.Response;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.SystemCommand;
import com.akira.server.managers.PostgresManager;

import java.util.ArrayList;

public class LoginCommand implements Command, Modable {
    private String login;
    private String passwordHash;

    @Override
    public Response execute(){

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

    }
}
