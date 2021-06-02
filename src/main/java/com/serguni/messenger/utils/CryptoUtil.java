package com.serguni.messenger.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoUtil {
    private final static String HASH_ALGORITHM = System.getenv("HASH_ALGORITHM");
    private final static int HASH_ITERATIONS = Integer.parseInt(System.getenv("HASH_ITERATIONS"));
    private final static byte[] SALT = System.getenv("SALT").getBytes(StandardCharsets.UTF_8);
    private final static int HASH_LENGTH = Integer.parseInt(System.getenv("HASH_LENGTH"));

    public static String createHash(String password) {
        char[] passwordChar = password.toCharArray();
        byte[] hash = pbkdf2(passwordChar, HASH_LENGTH);
        return toHex(hash);
    }

    public static boolean checkSecretKey(String password, String goodHash) {
        char[] passwordChar = password.toCharArray();
        byte[] hash = fromHex(goodHash);

        byte[] testHash = pbkdf2(passwordChar, hash.length);
        return slowEquals(hash, testHash != null ? testHash : new byte[0]);
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, int bytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, CryptoUtil.SALT, CryptoUtil.HASH_ITERATIONS, bytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return new byte[0];
        }
    }

    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        return binary;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

}
