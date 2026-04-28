package com.akira.client;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class PasswordEncryptor {
    private PasswordEncryptor instance;

    PasswordEncryptor getInstance(){
        return instance!=null?instance:new PasswordEncryptor();
    }

    public String getPasswordHash(String password){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");

            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "Error";
        }
    }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
