package com.akira.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserRegisty {
    private String userLogin;
    private String passwordHash;
    private static UserRegisty instance;
    private MessageDigest messageDigest;

    public static UserRegisty getInstance(){
        return instance!=null?instance:new UserRegisty();
    }

    public UserRegisty setMessageDigest(MessageDigest instance){
        if(instance == null) {
            try{
                messageDigest = MessageDigest.getInstance("SHA-224");
            }catch (NoSuchAlgorithmException e){
                System.err.println("Ошибка при установке пароля. Установленного алгоритма шифрования нет. Сообщите об ошибке администраторам");
            }
        }
        messageDigest = instance;
        return this;
    }

    public UserRegisty setPasswordHash(String hash){
        if(hash.length()!=messageDigest.getDigestLength()||(hash == null)){
            System.err.println("Ошибка при установке пароля, длина хэша не совпадает с ожидаемой. Сообщите об ошибке администраторам");
        }else if(!hash.matches("^[0-9a-fA-F]+$")){
            System.err.println("Ошибка при установке пароля, переданный аргумент не является хешем.");
        }else{
            passwordHash = hash;
        }
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
