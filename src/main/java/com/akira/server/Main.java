package com.akira.server;

/**
 * Главный класс серверного приложения.
 */
public class Main {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        ServerManager serverManager = new ServerManager(PORT);
        serverManager.start();
    }
}
