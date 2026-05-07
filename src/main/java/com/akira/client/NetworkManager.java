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
    private DataOutputStream out;
    private DataInputStream in;

    /**
     * Конструктор менеджера сетевого взаимодействия.
     * @param host адрес сервера (IP или доменное имя)
     * @param port порт сервера
     */
    public NetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Устанавливает соединение с сервером через блокирующий сокет.
     * @return {@code true}, если соединение установлено успешно, иначе {@code false}
     */
    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Отправляет объект запроса на сервер и получает ответ.
     * При временной недоступности сервера автоматически повторяет попытку подключения
     * до {@code MAX_RETRIES} раз с паузой {@code RETRY_DELAY_MS} мс.
     * 
     * @param request сериализуемый объект запроса
     * @return объект {@link Response} от сервера или {@code null} при исчерпании попыток
     */
    public Response sendAndReceive(Request request) {
        final int MAX_RETRIES = 3;
        final long RETRY_DELAY_MS = 4000;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (socket == null || socket.isClosed()) {
                    if (!connect()) {
                        System.out.println("Сервер недоступен. Повтор через 2 сек... (попытка " + attempt + "/" + MAX_RETRIES + ")");
                        Thread.sleep(RETRY_DELAY_MS);
                        continue;
                    }
                }

                // Сериализуем объект в массив байтов
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(request);
                oos.flush();
                byte[] data = baos.toByteArray();

                // Отправляем размер, затем данные
                out.writeInt(data.length);
                out.write(data);
                out.flush();

                // Читаем размер ответа
                int size = in.readInt();
                byte[] responseData = new byte[size];
                in.readFully(responseData);

                ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return (Response) ois.readObject();

            } catch (IOException e) {
                close();
                if (attempt < MAX_RETRIES) {
                    System.out.println("Соединение прервано. Повтор через 4 сек... (попытка " + attempt + "/" + MAX_RETRIES + ")");
                    try { Thread.sleep(RETRY_DELAY_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                } else {
                    System.out.println("Ошибка: не удалось связаться с сервером после " + MAX_RETRIES + " попытки.");
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Ошибка десериализации ответа.");
                close();
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }

    /**
     * Закрывает все открытые сетевые ресурсы (сокеты и потоки).
     */
    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        socket = null;
        out = null;
        in = null;
    }
}
