package main.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {
    private PasswordHasher() {}
    public static String sha384(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-384 is not supported", e);
        }
    }
}
