package com.akira.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.akira.server.managers.ServerManager;

/**
 * Главный класс серверного приложения.
 * <p>
 * Запускает сетевой сервер на указанном порту и ожидает подключений клиентов.
 * Управление сервером осуществляется через административный клиент (admin.jar).
 * </p>
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final int PORT = 12345;

    /**
     * Точка входа серверного приложения.
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        ServerManager serverManager = new ServerManager(PORT);
        logger.info("Сервер запускается на порту: {}", PORT);
        serverManager.start();
    }
}
