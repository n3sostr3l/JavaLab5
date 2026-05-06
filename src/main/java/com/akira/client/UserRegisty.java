package com.akira.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserRegisty {
    private String userLogin;
    private String passwordHash;
    private static UserRegisty instance;
    private MessageDigest messageDigest;

    private UserRegisty() {
        try {
            messageDigest = MessageDigest.getInstance("SHA-224");
        } catch (NoSuchAlgorithmException e) {
            messageDigest = null;
        }
    }

    public static synchronized UserRegisty getInstance(){
        if (instance == null) instance = new UserRegisty();
        return instance;
    }

    public UserRegisty setMessageDigest(MessageDigest md){
        if (md != null) messageDigest = md;
        return this;
    }

    public UserRegisty setPasswordHash(String hash){
        if (hash == null) {
            System.err.println("Ошибка при установке пароля: null");
            return this;
        }
        if (messageDigest != null) {
            int expectedHexLen = messageDigest.getDigestLength() * 2;
            if (hash.length() != expectedHexLen) {
                System.err.println("Ошибка при установке пароля, длина хэша не совпадает с ожидаемой.");
                return this;
            }
        }
        if (!hash.matches("^[0-9a-fA-F]+$")){
            System.err.println("Ошибка при установке пароля, переданный аргумент не является хешем.");
            return this;
        }
        passwordHash = hash;
        return this;
    }

    public UserRegisty setUserLogin(String login){
        if(login!=null) userLogin = login;
        else System.err.println("Пустой логин:(");
        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public MessageDigest getMessageDigest(){
        return messageDigest;
    }

}
