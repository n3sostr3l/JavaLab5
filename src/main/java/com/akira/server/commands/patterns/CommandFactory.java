package com.akira.server.commands.patterns;

import com.akira.server.commands.*;
import com.akira.server.commands.interfaces.Command;
import com.akira.server.commands.interfaces.AuthCommand;
import com.akira.server.commands.interfaces.SystemCommand;
import java.util.Map;
import java.util.HashMap;

/** Простая фабрика команд, применяющая декораторы. */
public class CommandFactory {
    private static final Map<String, Command> cache = new HashMap<>();

    public static Command create(String name) {
        return cache.computeIfAbsent(name, CommandFactory::instantiate);
    }

    private static Command instantiate(String name) {
        Command c;
        switch (name) {
            case "check": c = new CheckCommand(); break;
            case "help": c = new HelpCommand(); break;
            case "info": c = new InfoCommand(); break;
            case "show": c = new ShowCommand(); break;
            case "insert": c = new InsertCommand(); break;
            case "update": c = new UpdateCommand(); break;
            case "remove_key": c = new RemoveCommand(); break;
            case "clear": c = new ClearCommand(); break;
            case "exit": c = new ExitCommand(); break;
            case "execute_script": c = new ExecuteCommand(); break;
            case "replace_if_greater": c = new ReplaceGreatestCommand(); break;
            case "replace_if_lower": c = new ReplaceLowestCommand(); break;
            case "remove_lower_key": c = new RemoveLowerElementsCommand(); break;
            case "group_counting_by_maximum_point": c = new GroupCountingByMaximumPointCommand(); break;
            case "print_unique_author": c = new UniqueAuthorCommand(); break;
            case "print_field_descending_difficulty": c = new PrintFieldDescendingDifficultyCommand(); break;
            case "add_random": c = new AddRandomCommand(); break;
            case "exit_server": c = new ExitServerCommand(); break;
            case "login": c = new LoginCommand(); break;
            case "reg": c = new RegisterCommand(); break;
            case "reset_pwd": c = new ResetPasswordCommand(); break;
            default: c = null; break;
        }

        if (c == null) return null;

        boolean requiresAuth = !(c instanceof AuthCommand);

        c = new LoggingCommandDecorator(c);

        if (requiresAuth) {
            c = new AuthorizedCommandDecorator(c);
        }

        return c;
    }
}
