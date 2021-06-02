package com.serguni.messenger.components.security;

import java.util.Random;

public class KeyGenerator {
    private final static char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public String getSecretKey() {
        int keyLen = 10;

        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < keyLen; i++) {
            key.append(CHARS[random.nextInt(CHARS.length)]);
        }

        return key.toString();
    }

    public String getTemporaryKey() {
        int keyLen = 5;

        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < keyLen; i++) {
            key.append(CHARS[random.nextInt(CHARS.length)]);
        }

        return key.toString();
    }
}
