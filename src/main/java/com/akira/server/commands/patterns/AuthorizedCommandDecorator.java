package com.akira.server.commands.patterns;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.server.commands.interfaces.DelegatingCommand;
import com.akira.general.datas.LabWork;
import java.util.ArrayList;
import com.akira.general.network.Response;
import com.akira.server.managers.CollectionManager;

/** Простейший декоратор, проверяющий наличие логина перед выполнением команды. */
public class AuthorizedCommandDecorator implements Command, Modable, ObjectModable, DelegatingCommand {
    private final Command delegate;

    public AuthorizedCommandDecorator(Command delegate) {
        this.delegate = delegate;
    }

    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        if (login == null || login.isEmpty()) {
            return new Response("Ошибка: требуется аутентификация. Войдите или зарегистрируйтесь.", false);
        }
        return delegate.execute(collectionManager, login);
    }

    @Override
    public String describe() { return delegate.describe(); }

    @Override
    public int numberArgsRequired() { return delegate.numberArgsRequired(); }

    @Override
    public Command getDelegate() {
        return delegate;
    }

    @Override
    public void setArguments(ArrayList<String> args) {
        if (delegate instanceof Modable modable) {
            modable.setArguments(args);
        }
    }

    @Override
    public void setObject(LabWork labWork) {
        if (delegate instanceof ObjectModable objectModable) {
            objectModable.setObject(labWork);
        }
    }
}
