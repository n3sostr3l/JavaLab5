package com.akira.server.managers;

import com.akira.general.network.Request;
import com.akira.general.network.Response;

import com.akira.server.CommandInvoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class ServerManager {
    private static final Logger logger = LogManager.getLogger(ServerManager.class);

    private final int port;
    private Selector selector;

    //  Пулы потоков
    private final ForkJoinPool readPool = new ForkJoinPool();
    private final ForkJoinPool handlePool = new ForkJoinPool();
    private final ExecutorService sendPool = Executors.newFixedThreadPool(8);

    //  Новая архитектура
    private final CommandInvoker CommandInvoker = new CommandInvoker();

    public ServerManager(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            selector = Selector.open();

            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Сервер запущен на порту: {}", port);

            while (true) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        accept(serverChannel);
                    } else if (key.isReadable()) {
                        //  ЧТЕНИЕ В ПУЛЕ
                        readPool.execute(() -> handleRead(key));
                    }

                    iter.remove();
                }
            }

        } catch (IOException e) {
            logger.error("Ошибка сервера: {}", e.getMessage());
        }
    }

    private void accept(ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);

        SelectionKey key = client.register(selector, SelectionKey.OP_READ);
        key.attach(new ReadState());
    }

    private static class ReadState {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        ByteBuffer dataBuffer = null;
        boolean processing = false;
    }

    private void handleRead(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ReadState state = (ReadState) key.attachment();

        try {
            synchronized (state) {
                if (state.processing) return;

                // читаем размер
                if (state.sizeBuffer.hasRemaining()) {
                    int read = client.read(state.sizeBuffer);
                    if (read == -1) {
                        close(client, key);
                        return;
                    }
                    if (state.sizeBuffer.hasRemaining()) return;
                }

                // создаём буфер
                if (state.dataBuffer == null) {
                    state.sizeBuffer.flip();
                    int size = state.sizeBuffer.getInt();

                    if (size <= 0 || size > 10_000_000) {
                        close(client, key);
                        return;
                    }

                    state.dataBuffer = ByteBuffer.allocate(size);
                }

                // читаем тело
                if (state.dataBuffer.hasRemaining()) {
                    int read = client.read(state.dataBuffer);
                    if (read == -1) {
                        close(client, key);
                        return;
                    }
                    if (state.dataBuffer.hasRemaining()) return;
                }

                //  ПОЛНЫЙ ЗАПРОС → ОБРАБОТКА В ПУЛЕ
                state.dataBuffer.flip();
                state.processing = true;
            }

            handlePool.execute(() -> processRequest(client, state));

        } catch (IOException e) {
            close(client, key);
        }
    }

    private void processRequest(SocketChannel client, ReadState state) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(state.dataBuffer.array(), 0, state.dataBuffer.limit()))) {

            Request request = (Request) ois.readObject();
            logger.info("Запрос: {}", request.getCommandName());

            Response response = CommandInvoker.executeRequest(request, new CollectionManager());

            //  ОТПРАВКА В ОТДЕЛЬНОМ ПУЛЕ
            sendPool.submit(() -> sendResponse(client, response));

            // reset state
            synchronized (state) {
                state.sizeBuffer.clear();
                state.dataBuffer = null;
                state.processing = false;
            }

        } catch (Exception e) {
            logger.warn("Ошибка обработки: {}", e.getMessage());
        }
    }

    private void sendResponse(SocketChannel client, Response response) {
        try {
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
                client.write(buffer);
            }

        } catch (IOException e) {
            logger.warn("Ошибка отправки: {}", e.getMessage());
        }
    }

    private void close(SocketChannel client, SelectionKey key) {
        try {
            client.close();
        } catch (IOException ignored) {}
        key.cancel();
    }
}