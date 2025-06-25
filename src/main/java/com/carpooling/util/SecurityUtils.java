package com.carpooling.util;

import java.security.SecureRandom;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    /**
     * Hashes a password using BCrypt with automatically generated salt.
     * BCrypt includes salt generation and storage in the hash itself.
     * 
     * @param password The plain text password to hash
     * @return The BCrypt hashed password (includes salt)
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    /**
     * Verifies a password against a BCrypt hash.
     * 
     * @param password The plain text password to verify
     * @param hashedPassword The BCrypt hash to verify against
     * @return true if password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * @deprecated Use hashPassword(String) instead. BCrypt handles salt automatically.
     */
    @Deprecated
    public static String generateSalt() {
        StringBuilder salt = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            salt.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return salt.toString();
    }

    /**
     * @deprecated Use hashPassword(String) instead. BCrypt handles salt automatically.
     */
    @Deprecated
    public static String hashPasswordWithSalt(String password, String salt) {
        // For backward compatibility, fallback to BCrypt
        return hashPassword(password);
    }

    /**
     * @deprecated Use verifyPassword(String, String) instead.
     */
    @Deprecated
    public static boolean verifyPasswordWithSalt(String password, String hashedPassword, String salt) {
        // For backward compatibility, use BCrypt verification
        return verifyPassword(password, hashedPassword);
    }

    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"'&]", "");
    }

    public static boolean isValidRole(String role) {
        return role != null && (
            role.equals(Constants.ROLE_ADMIN) ||
            role.equals(Constants.ROLE_DRIVER) ||
            role.equals(Constants.ROLE_PASSENGER)
        );
    }
} 