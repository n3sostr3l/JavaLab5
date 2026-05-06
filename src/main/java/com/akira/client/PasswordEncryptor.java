package com.akira.client;

import com.akira.client.security.HashStrategy;
import com.akira.client.security.Sha224HashStrategy;

public class PasswordEncryptor {

    private static PasswordEncryptor instance;
    private final HashStrategy strategy;

    private PasswordEncryptor() {
        this.strategy = new Sha224HashStrategy();
    }

    public static synchronized PasswordEncryptor getInstance() {
        if (instance == null) instance = new PasswordEncryptor();
        return instance;
    }

    public String getPasswordHash(String password) {
        return strategy.hash(password);
    }
}
