package com.akira.client.gui;

import com.akira.client.NetworkManager;
import com.akira.general.network.Request;
import com.akira.general.network.Response;
import javax.swing.*;

/**
 * Главный класс GUI-клиента.
 * Запускает приложение с графическим интерфейсом.
 */
public class GuiClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12347;

    public static void main(String[] args) {
        UIStyle.applyGlobalStyle();

        NetworkManager network = new NetworkManager(DEFAULT_HOST, DEFAULT_PORT);

        // Инициализация сессии
        if (!initSession(network)) {
            JOptionPane.showMessageDialog(null,
                "Не удалось подключиться к серверу",
                "Ошибка подключения",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame(network, () -> {
                MainFrame mainFrame = new MainFrame(network);
                mainFrame.setVisible(true);
            });
        });
    }

    private static boolean initSession(NetworkManager network) {
        if (!network.connect()) {
            return false;
        }
        Request req = new Request("init");
        req.setInit(true);
        req.setRestore(true);
        req.setAdmin(false);
        Response resp = network.sendAndReceive(req);
        return resp != null;
    }
}