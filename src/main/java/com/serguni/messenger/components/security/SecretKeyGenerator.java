package com.serguni.messenger.components.security;

import java.util.Random;

public class SecretKeyGenerator {
    private final static int KEY_LEN = 40;
    private final static char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789#@-_^/".toCharArray();

    public String getSecretKey() {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < KEY_LEN; i++) {
            key.append(CHARS[random.nextInt(CHARS.length)]);
        }

        return key.toString();
    }

}
