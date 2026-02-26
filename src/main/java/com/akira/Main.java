package com.akira;

import com.akira.commands.CommandInvoker;

/**
 * Главный класс приложения.
 * <p>
 * Служит точкой входа в программу. Создаёт объект {@link CommandInvoker}
 * и запускает интерактивный режим управления коллекцией.
 * </p>
 */
public class Main {
    /**
     * Точка входа в приложение.
     * <p>
     * Создаёт invoking команд и запускает интерактивный режим.
     * </p>
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {

        CommandInvoker commandInvoker = new CommandInvoker();
        commandInvoker.run();

    }
}