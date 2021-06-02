package com.serguni.messenger.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

    @Test
    void createHash() {
        String strHash = CryptoUtil.createHash("Привет, Мир!");
        assertTrue(CryptoUtil.checkSecretKey("Привет, Мир!", strHash));

        String strHash1 = CryptoUtil.createHash("Привет");
        assertFalse(CryptoUtil.checkSecretKey("Hello", strHash1));

    }
}