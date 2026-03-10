package com.akira.commands;

import com.akira.general.commands.interfaces.Command;

/**
 * Команда завершения работы программы.
 * <p>
 * Завершает выполнение программы без сохранения коллекции в файл.
 * </p>
 */
public class ExitCommand implements Command{
    /**
     * Выполняет команду exit.
     * <p>
     * Завершает работу приложения с кодом возврата 200.
     * Коллекция не сохраняется в файл перед выходом.
     * </p>
     */
    @Override
    public void execute() {
        System.exit(200);
    }

    /**
     * Выводит описание команды.
     */
    @Override
    public void describe() {
        System.out.println("exit : завершить программу (без сохранения в файл)");
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
