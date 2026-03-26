package com.akira.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Отвечает за загрузку и выполнение скриптов из файлов.
 * <p>
 * Хранит стек выполняемых скриптов для обнаружения рекурсии.
 * Стек общий для всей сессии, поэтому передаётся снаружи.
 * </p>
 */
public class ScriptExecutor {
    private final HashSet<String> executionStack;
    private final ClientSession session;

    /**
     * @param executionStack общий стек путей выполняемых скриптов
     * @param session        сессия, в рамках которой выполняется скрипт
     */
    public ScriptExecutor(HashSet<String> executionStack, ClientSession session) {
        this.executionStack = executionStack;
        this.session = session;
    }

    /**
     * Выполняет скрипт из указанного файла.
     * <p>
     * При обнаружении рекурсивного вызова выводит ошибку и возвращает управление.
     * </p>
     *
     * @param fileName путь или имя файла скрипта
     */
    public void execute(String fileName) {
        File file = new File(fileName);
        String absPath = file.getAbsolutePath();

        if (executionStack.contains(absPath)) {
            System.out.println("Ошибка: обнаружена рекурсия в скрипте: " + fileName);
            return;
        }

        executionStack.add(absPath);
        try (Scanner scriptScanner = new Scanner(file)) {
            com.akira.general.LabWorkReader oldReader = session.getReader();
            session.setReader(new com.akira.general.LabWorkReader(scriptScanner));
            
            session.processInput(scriptScanner, true);
            
            session.setReader(oldReader);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден: " + fileName);
        } finally {
            executionStack.remove(absPath);
        }
    }
}
