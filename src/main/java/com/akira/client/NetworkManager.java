package com.akira.client;

import com.akira.general.network.Request;
import com.akira.general.network.Response;
import java.io.*;
import java.net.Socket;

/**
 * Класс для управления сетевым взаимодействием на стороне клиента.
 */
public class NetworkManager {
    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public NetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Устанавливает соединение с сервером.
     * @return true, если соединение установлено
     */
    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Отправляет запрос на сервер и получает ответ.
     * @param request объект запроса
     * @return объект ответа или null при ошибке
     */
    public Response sendAndReceive(Request request) {
        try {
            if (socket == null || socket.isClosed()) {
                if (!connect()) return null;
            }
            out.writeObject(request);
            out.flush();
            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            close();
            return null;
        }
    }

    /**
     * Закрывает соединение.
     */
    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
