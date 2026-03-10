package com.akira.server;

import com.akira.general.network.Request;
import com.akira.general.network.Response;
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
     * @param serverChannel серверный канал сокета
     * @throws IOException если возникла ошибка при установке соединения
     */
    private void acceptConnection(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Обрабатывает данные, поступившие от клиента.
     * Считывает размер пакета, затем сам десериализованный объект запроса.
     * @param key ключ выбора из селектора
     */
    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
            // Читаем размер
            ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
            int read = clientChannel.read(sizeBuffer);
            if (read == -1) { clientChannel.close(); return; }
            
            sizeBuffer.flip();
            int size = sizeBuffer.getInt();
            
            // Читаем данные
            ByteBuffer dataBuffer = ByteBuffer.allocate(size);
            int totalRead = 0;
            while (totalRead < size) {
                int r = clientChannel.read(dataBuffer);
                if (r == -1) break;
                totalRead += r;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(dataBuffer.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Request request = (Request) ois.readObject();
            logger.info("Запрос: {}", request.getCommandName());

            Response response = commandInvoker.executeRequest(request, collectionManager);
            sendResponse(clientChannel, response);

        } catch (IOException | ClassNotFoundException e) {
            try { clientChannel.close(); } catch (IOException ignored) {}
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
