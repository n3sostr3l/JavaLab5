package com.akira.server;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.NoSuchElementException;

/**
 * Главный класс серверного приложения.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // 1. Попытка восстановления из бэкапа
        try {
            if (FileEditor.hasBackup()) {
                System.out.println("Найдена несохраненная сессия (резервная копия).");
                System.out.print("Восстановить сессию? (y/n): ");
                Scanner setupScanner = new Scanner(System.in);
                if (System.console() != null) {
                    String response = setupScanner.nextLine().trim().toLowerCase();
                    if (response.equals("y") || response.equals("yes")) {
                        CollectionManager.restoreFromBackup();
                        System.out.println("Сессия успешно восстановлена.");
                    } else {
                        FileEditor.deleteBackup();
                        System.out.println("Резервная копия удалена.");
                    }
                } else {
                    System.out.println("Интерактивный консольный ввод недоступен. Пропускаем восстановление.");
                }
            }
        } catch (Exception e) {
            logger.warn("Предупреждение при обработке бэкапа: {}. Продолжаем запуск.", e.getMessage());
        }

        // 2. Инициализация и запуск сетевого менеджера
        ServerManager serverManager = new ServerManager(PORT);
        
        // Поток сервера НЕ должен быть daemon, чтобы JVM не закрылась при завершении main
        Thread serverThread = new Thread(serverManager::start, "ServerNetworkThread");
        serverThread.start();

        System.out.println("Сетевой сервер запущен на порту: " + PORT);
        System.out.println("Сервер готов к приему локальных команд (save_server, exit)...");
        logger.info("Серверный интерфейс командной строки активирован.");

        // 3. Основной цикл для обработки серверных консольных команд
        try (Scanner scanner = new Scanner(System.in)) {
            while (serverThread.isAlive()) {
                try {
                    // Проверяем наличие ввода, чтобы не блокироваться вечно, если System.in закрыт
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim().toLowerCase();
                        if (line.isEmpty()) continue;

                        if (line.equals("save_server")) {
                            if (CollectionManager.save()) {
                                System.out.println("Коллекция успешно сохранена (save_server).");
                                logger.info("Сервер: Коллекция сохранена вручную администратором.");
                            } else {
                                System.err.println("Ошибка при сохранении коллекции.");
                            }
                        } else if (line.equals("exit")) {
                            System.out.println("Завершение работы сервера...");
                            logger.info("Сервер: Получена команда exit.");
                            System.exit(0);
                        } else {
                            System.out.println("Неизвестная команда: " + line);
                            System.out.println("Доступные команды: save_server, exit");
                        }
                    } else {
                        // Если входной поток пуст или закрыт, просто ждем
                        Thread.sleep(1000);
                    }
                } catch (NoSuchElementException | IllegalStateException e) {
                    // Если ввод закрылся совсем (например, при запуске в фоне через &)
                    logger.info("Консольный ввод сервера закрыт. Консольные команды недоступны.");
                    break; 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Критическая ошибка в консольном потоке: {}", e.getMessage());
        }
        
        // Если консольный поток завершился (например, EOF), ждем завершения серверного потока
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            logger.error("Основной поток был прерван при ожидании сервера.");
        }
    }
}
