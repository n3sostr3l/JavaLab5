package com.akira.server.managers;

import com.akira.general.network.Request;
import com.akira.general.network.Response;
import com.akira.server.CommandInvoker;
import com.akira.server.FileEditor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Класс для управления серверным сетевым взаимодействием на базе NIO.
 */
public class ServerManager {
    private static final Logger logger = LogManager.getLogger(ServerManager.class);
    private final int port;
    private final CommandInvoker commandInvoker;
    private final CollectionManager collectionManager;
    private Selector selector;

    /**
     * Конструктор сервера.
     * @param port порт для прослушивания входящих соединений
     */
    public ServerManager(int port) {
        this.port = port;
        this.collectionManager = new CollectionManager();
        this.commandInvoker = new CommandInvoker();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (CollectionManager.isSaveOnExit()) {
                logger.info("Получен сигнал завершения. Автосохранение коллекции...");
                if (CollectionManager.save()) {
                    logger.info("Коллекция успешно сохранена перед выходом.");
                } else {
                    logger.error("Ошибка при автосохранении коллекции.");
                }
            } else {
                logger.info("Получен сигнал завершения. Сохранение пропущено по требованию.");
            }
        }));
    }

    /**
     * Запускает основной цикл сервера.
     * Использует неблокирующий ввод-вывод (NIO) для обработки множества соединений в одном потоке.
     */
    public void start() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Сервер запущен на порту: {}", port);

            while (true) {
                if (selector.select() == 0) continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isAcceptable()) {
                        acceptConnection(serverChannel);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка сервера: {}", e.getMessage());
        }
    }

    /**
     * Принимает входящее подключение и регистрирует его в селекторе на чтение.
     * Прикрепляет новый ReadState для накопления байт.
     * @param serverChannel серверный канал сокета
     * @throws IOException если возникла ошибка при установке соединения
     */
    private void acceptConnection(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        SelectionKey key = clientChannel.register(selector, SelectionKey.OP_READ);
        key.attach(new ReadState());
    }

    /**
     * Внутреннее состояние чтения для одного клиентского соединения.
     * Накапливает байты между несколькими вызовами read() в non-blocking режиме.
     */
    private static class ReadState {
        /** Буфер для чтения размера сообщения (4 байта) */
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        /** Буфер для чтения самих данных сообщения */
        ByteBuffer dataBuffer = null;
    }

    /**
     * Обрабатывает данные, поступившие от клиента.
     * Накапливает байты в ReadState, прикреплённом к ключу, до получения полного пакета.
     * @param key ключ выбора из селектора
     */
    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ReadState state = (ReadState) key.attachment();
        if (state == null) {
            state = new ReadState();
            key.attach(state);
        }

        try {
            // Шаг 1: читаем 4 байта размера (может приходить по частям)
            if (state.sizeBuffer.hasRemaining()) {
                int read = clientChannel.read(state.sizeBuffer);
                if (read == -1) {
                    logger.info("Клиент отключился. Сохранение промежуточного состояния...");
                    FileEditor.saveToFile(FileEditor.DATA_FILE_NAME, CollectionManager.getCollection());
                    clientChannel.close();
                    key.cancel();
                    return;
                }
                if (state.sizeBuffer.hasRemaining()) return; // ещё не все 4 байта
            }

            // Шаг 2: инициализируем буфер данных
            if (state.dataBuffer == null) {
                state.sizeBuffer.flip();
                int size = state.sizeBuffer.getInt();
                if (size <= 0 || size > 64 * 1024 * 1024) { // защита от мусора (>64MB)
                    logger.warn("Получен некорректный размер пакета: {}", size);
                    clientChannel.close();
                    key.cancel();
                    return;
                }
                state.dataBuffer = ByteBuffer.allocate(size);
            }

            // Шаг 3: читаем данные (может приходить по частям)
            if (state.dataBuffer.hasRemaining()) {
                int read = clientChannel.read(state.dataBuffer);
                if (read == -1) {
                    clientChannel.close();
                    key.cancel();
                    return;
                }
                if (state.dataBuffer.hasRemaining()) return; // ещё не весь пакет
            }

            // Шаг 4: полный пакет получен — десериализуем и обрабатываем
            state.dataBuffer.flip();
            try (ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(state.dataBuffer.array(), 0, state.dataBuffer.limit()))) {
                Request request = (Request) ois.readObject();
                logger.info("Запрос: {}", request.getCommandName());
                Response response = commandInvoker.executeRequest(request, collectionManager);
                sendResponse(clientChannel, response);

                if (request.getCommandName().equalsIgnoreCase("exit_server") && response.isSuccess()) {
                    logger.info("Завершение работы сервера по команде админа.");
                    CollectionManager.setSaveOnExit(false);
                    System.exit(0);
                }
            }

            // Сбрасываем состояние для следующего запроса по тому же соединению
            state.sizeBuffer.clear();
            state.dataBuffer = null;

        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Ошибка при обработке клиента: {}", e.getMessage());
            try { clientChannel.close(); } catch (IOException ignored) {}
            key.cancel();
        }
    }

    /**
     * Отправляет объект ответа клиенту.
     * Сначала отправляется 4 байта (int) с размером ответа, затем сами байты объекта.
     * 
     * @param clientChannel канал связи с клиентом
     * @param response объект ответа сервера
     * @throws IOException если возникла ошибка при отправке данных
     */
    private void sendResponse(SocketChannel clientChannel, Response response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();
        byte[] data = baos.toByteArray();
        
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        
        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
    }
}
