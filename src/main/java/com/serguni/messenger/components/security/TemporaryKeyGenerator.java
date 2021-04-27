package com.serguni.messenger.components.security;

import java.util.Random;

public class TemporaryKeyGenerator {
    private final static int KEY_LEN = 5;
    private final static char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public String getTemporaryKey() {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < KEY_LEN; i++) {
            key.append(CHARS[random.nextInt(CHARS.length)]);
        }

        return key.toString();
    }
}
