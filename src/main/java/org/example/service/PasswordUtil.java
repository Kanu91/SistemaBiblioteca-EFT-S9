package org.example.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/** PBKDF2 estándar Java; admite contraseñas de texto legado de scripts de la asignatura. */
public final class PasswordUtil {
    private static final int ITERATIONS = 150_000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private PasswordUtil() {}

    public static String hash(String password) {
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Contraseña obligatoria");
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] digest = derive(password.toCharArray(), salt, ITERATIONS);
        return "p2$" + ITERATIONS + "$" + Base64.getEncoder().withoutPadding().encodeToString(salt)
                + "$" + Base64.getEncoder().withoutPadding().encodeToString(digest);
    }

    public static boolean verify(String rawPassword, String stored) {
        if (stored == null || rawPassword == null) return false;
        if (!stored.startsWith("p2$")) {
            // Compatibilidad con poblamiento docente; cambiar estas claves desde Usuarios al primer acceso.
            return MessageDigest.isEqual(rawPassword.getBytes(StandardCharsets.UTF_8),
                    stored.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = stored.split("\\$", -1);
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 10_000 || iterations > 2_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = derive(rawPassword.toCharArray(), salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo derivar la contraseña", e);
        } finally {
            spec.clearPassword();
            Arrays.fill(password, '\0');
        }
    }
}
