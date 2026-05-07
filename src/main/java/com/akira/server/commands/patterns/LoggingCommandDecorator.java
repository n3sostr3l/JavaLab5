package com.akira.server.commands.patterns;

import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.AuthCommand;
import com.akira.server.commands.interfaces.Modable;
import com.akira.server.commands.interfaces.ObjectModable;
import com.akira.server.commands.interfaces.DelegatingCommand;
import com.akira.general.datas.LabWork;
import java.util.ArrayList;
import com.akira.general.network.Response;
import com.akira.server.managers.CollectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingCommandDecorator implements Command, AuthCommand, Modable, ObjectModable, DelegatingCommand {
    private final Command delegate;
    private final Logger logger = LogManager.getLogger(LoggingCommandDecorator.class);

    public LoggingCommandDecorator(Command delegate) {
        this.delegate = delegate;
    }

    @Override
    public Response execute(CollectionManager collectionManager, String login) {
        logger.info("Executing command: {} by user {}", delegate.describe(), login);
        Response r = delegate.execute(collectionManager, login);
        logger.info("Result: success={}, message={}", r.isSuccess(), r.getMessage());
        return r;
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

    @Override
    public void setPasswordHash(String passwordHash) {
        if (delegate instanceof AuthCommand auth) {
            auth.setPasswordHash(passwordHash);
        }
    }
}
