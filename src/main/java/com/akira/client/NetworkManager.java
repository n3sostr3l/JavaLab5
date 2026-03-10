package com.akira.client;

import com.akira.general.network.Request;
import com.akira.general.network.Response;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Класс для управления сетевым взаимодействием на стороне клиента.
 */
public class NetworkManager {
    private final String host;
    private final int port;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public NetworkManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

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

    public Response sendAndReceive(Request request) {
        try {
            if (socket == null || socket.isClosed()) {
                if (!connect()) return null;
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
            
        } catch (IOException | ClassNotFoundException e) {
            close();
            return null;
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
