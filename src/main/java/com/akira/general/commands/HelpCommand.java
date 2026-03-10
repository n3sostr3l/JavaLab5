package com.akira.commands;

import java.util.ArrayList;

import com.akira.general.commands.interfaces.Command;

/**
 * Команда вывода справки по доступным командам.
 * <p>
 * При выполнении выводит описание всех зарегистрированных команд
 * с указанием их синтаксиса и назначения.
 * </p>
 */
public class HelpCommand implements Command {

    /**
     * Выполняет команду help.
     * <p>
     * Создаёт экземпляры всех доступных команд и вызывает метод {@code describe()}
     * для каждой из них, выводя справочную информацию в консоль.
     * </p>
     */
    @Override
    public void execute() {
        ArrayList<Command> allCommands = new ArrayList<>();
        allCommands.add(new HelpCommand());
        allCommands.add(new ClearCommand());
        allCommands.add(new InfoCommand());
        allCommands.add(new InsertCommand());
        allCommands.add(new UpdateCommand());
        allCommands.add(new ExitCommand());
        allCommands.add(new ExecuteCommand());
        allCommands.add(new ShowCommand());
        allCommands.add(new UniqueAuthorCommand());
        allCommands.add(new SaveCommand());
        allCommands.add(new RemoveCommand());
        allCommands.add(new GroupCountingByMaximumPointCommand());
        allCommands.add(new PrintFieldDescendingDifficultyCommand());
        allCommands.add(new RemoveLowerElementsCommand());
        allCommands.add(new ExecuteCommand());
        allCommands.add(new AddRandomCommand());
        for (Command c : allCommands) {
            c.describe();
        }
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("help : вывести справку по доступным командам");
    }

    /**
     * Возвращает количество требуемых аргументов.
     *
     * @return 0 — команда не требует аргументов
     */
    @Override
    public int numberArgsRequired() {
        return 0;
    }
}
