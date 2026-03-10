package com.akira.server;

import com.akira.general.network.Request;
import com.akira.general.network.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Класс для управления серверным сетевым взаимодействием на базе NIO.
 */
public class ServerManager {
    private static final Logger logger = LogManager.getLogger(ServerManager.class);
    private final int port;
    private final CommandInvoker commandInvoker;
    private final CollectionManager collectionManager;

    public ServerManager(int port) {
        this.port = port;
        this.collectionManager = new CollectionManager();
        this.commandInvoker = new CommandInvoker();
        // Регистрация хука на завершение работы (сохранение в файл)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Завершение работы сервера. Сохранение коллекции...");
            CollectionManager.save();
        }));
    }

    public void start() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            logger.info("Сервер запущен на порту: {}", port);

            while (true) {
                SocketChannel clientChannel = serverChannel.accept();
                if (clientChannel != null) {
                    logger.info("Новое подключение: {}", clientChannel.getRemoteAddress());
                    handleClient(clientChannel);
                }
                // Простая реализация: однопоточность (согласно ТЗ)
                // В неблокирующем режиме мы могли бы использовать Selector, 
                // но для простоты (один клиент за раз) этого достаточно.
            }
        } catch (IOException e) {
            logger.error("Ошибка при работе сервера: {}", e.getMessage());
        }
    }

    private void handleClient(SocketChannel clientChannel) {
        try {
            // ТЗ: Для обмена данными на клиенте использовать потоки ввода-вывода
            // Для обмена на сервере - каналы. Мы можем обернуть канал в потоки.
            InputStream is = clientChannel.socket().getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            
            Request request = (Request) ois.readObject();
            logger.info("Получен запрос: {}", request.getCommandName());

            Response response = commandInvoker.executeRequest(request, collectionManager);
            
            OutputStream os = clientChannel.socket().getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(response);
            oos.flush();
            logger.info("Отправлен ответ для: {}", request.getCommandName());

        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Ошибка при обработке клиента: {}", e.getMessage());
        } finally {
            try { clientChannel.close(); } catch (IOException ignored) {}
        }
    }
}
