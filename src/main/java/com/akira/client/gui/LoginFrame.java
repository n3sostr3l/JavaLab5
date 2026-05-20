package com.akira.client.gui;

import com.akira.client.NetworkManager;
import com.akira.client.PasswordEncryptor;
import com.akira.client.UserRegisty;
import com.akira.general.network.Request;
import com.akira.general.network.Response;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Окно авторизации и регистрации.
 * Поддерживает password-reset flow, если сервер возвращает "Хотите изменить пароль".
 */
public class LoginFrame extends JFrame {
    private final NetworkManager network;
    private final Runnable onSuccess;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JComboBox<String> langCombo;
    private JLabel titleLabel;
    private JLabel langLabel;
    private JLabel userLabel;
    private JLabel passLabel;

    public LoginFrame(NetworkManager network, Runnable onSuccess) {
        this.network = network;
        this.onSuccess = onSuccess;
        initComponents();
        pack();
        setMinimumSize(new Dimension(UIStyle.scale(480), UIStyle.scale(320)));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        setTitle(getLocalized("auth.title"));
        getContentPane().setBackground(UIStyle.BACKGROUND);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIStyle.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 16, 8, 16);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        titleLabel = new JLabel(getLocalized("auth.title"));
        titleLabel.setFont(UIStyle.FONT_TITLE);
        titleLabel.setForeground(UIStyle.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Language
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setBackground(UIStyle.BACKGROUND);
        langLabel = new JLabel(getLocalized("main.language"));
        UIStyle.styleLabel(langLabel);
        langCombo = new JComboBox<>(LocalizationManager.getLocaleNames());
        UIStyle.styleComboBox(langCombo);
        langCombo.addActionListener(e -> onLanguageChanged());
        langPanel.add(langLabel);
        langPanel.add(langCombo);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(langPanel, gbc);

        // Username
        userLabel = new JLabel(getLocalized("auth.username"));
        UIStyle.styleLabel(userLabel);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField(18);
        UIStyle.styleTextField(usernameField);
        usernameField.setFont(UIStyle.FONT);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(usernameField, gbc);

        // Password
        passLabel = new JLabel(getLocalized("auth.password"));
        UIStyle.styleLabel(passLabel);
        passLabel.setFont(UIStyle.FONT);
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(18);
        UIStyle.styleTextField(passwordField);
        passwordField.setFont(UIStyle.FONT);
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(UIStyle.BACKGROUND);

        loginBtn = new JButton(getLocalized("auth.login.btn"));
        UIStyle.styleButton(loginBtn);
        loginBtn.addActionListener(e -> doLogin());
        btnPanel.add(loginBtn);

        registerBtn = new JButton(getLocalized("auth.register.btn.register"));
        UIStyle.styleButton(registerBtn);
        registerBtn.addActionListener(e -> doRegister());
        btnPanel.add(registerBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        // Enter key = login
        passwordField.addActionListener(e -> doLogin());

        add(mainPanel, BorderLayout.CENTER);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    private void onLanguageChanged() {
        int idx = langCombo.getSelectedIndex();
        Locale[] locales = LocalizationManager.getSupportedLocales();
        if (idx >= 0 && idx < locales.length) {
            LocalizationManager.getInstance().setLocale(locales[idx]);
            updateTexts();
        }
    }

    private void updateTexts() {
        setTitle(getLocalized("auth.title"));
        titleLabel.setText(getLocalized("auth.title"));
        langLabel.setText(getLocalized("main.language"));
        userLabel.setText(getLocalized("auth.username"));
        passLabel.setText(getLocalized("auth.password"));
        loginBtn.setText(getLocalized("auth.login.btn"));
        registerBtn.setText(getLocalized("auth.register.btn.register"));
    }

    private String getLocalized(String key) {
        return LocalizationManager.getInstance().getString(key);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            DialogUtil.showError(this, getLocalized("auth.wrong"), getLocalized("msg.error"));
            return;
        }
        authenticate("login", username, password);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            DialogUtil.showError(this, getLocalized("auth.wrong"), getLocalized("msg.error"));
            return;
        }
        authenticate("reg", username, password);
    }

    private void authenticate(String cmd, String username, String password) {
        String hash = PasswordEncryptor.getInstance().getPasswordHash(password);

        // Сначала пытаемся авторизоваться
        ArrayList<String> args = new ArrayList<>(Arrays.asList(username, password));
        Request req = new Request(cmd, args);
        req.setLogin(username);
        req.setPasswordHash(hash);

        UserRegisty.getInstance().setUserLogin(username).setPasswordHash(hash);

        Response resp = network.sendAndReceive(req);
        if (resp == null) {
            DialogUtil.showError(this, getLocalized("msg.no.connection"), getLocalized("msg.error"));
            return;
        }

        // Если сервер предлагает сменить пароль
        if (!resp.isSuccess() && resp.getMessage().contains("Хотите изменить пароль")) {
            if (DialogUtil.showConfirm(this, 
                    "Неверный логин или пароль. Хотите изменить пароль?",
                    "Смена пароля")) {
                String newHash = PasswordEncryptor.getInstance().getPasswordHash(password);
                Request resetReq = new Request("reset_pwd", new ArrayList<>(Arrays.asList(username, newHash)));
                resetReq.setLogin(username);
                resetReq.setPasswordHash(newHash);
                Response resetResp = network.sendAndReceive(resetReq);
                if (resetResp != null && resetResp.isSuccess()) {
                    UserRegisty.getInstance().setPasswordHash(newHash);
                    DialogUtil.showMessage(this, "Пароль успешно изменён!", "Успех");
                    // Пробуем залогиниться снова с тем же паролем
                    Request retryReq = new Request("login", args);
                    retryReq.setLogin(username);
                    retryReq.setPasswordHash(newHash);
                    resp = network.sendAndReceive(retryReq);
                } else {
                    String msg = resetResp != null ? resetResp.getMessage() : "Ошибка соединения";
                    DialogUtil.showError(this, msg, "Ошибка");
                    return;
                }
            } else {
                return;
            }
        }

        if (resp != null && resp.isSuccess()) {
            dispose();
            onSuccess.run();
        } else if (resp != null) {
            DialogUtil.showError(this, resp.getMessage(), getLocalized("msg.error"));
        }
    }
}