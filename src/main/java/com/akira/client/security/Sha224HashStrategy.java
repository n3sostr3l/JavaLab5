package com.akira.client.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha224HashStrategy implements HashStrategy {
    private final MessageDigest md;

    public Sha224HashStrategy() {
        try {
            md = MessageDigest.getInstance("SHA-224");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-224 not available", e);
        }
    }

    @Override
    public synchronized String hash(String input) {
        byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(dig.length * 2);
        for (byte b : dig) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}
