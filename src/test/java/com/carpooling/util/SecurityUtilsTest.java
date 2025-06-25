package com.carpooling.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for SecurityUtils class focusing on BCrypt password hashing and verification.
 */
class SecurityUtilsTest {

    @Test
    @DisplayName("Should hash password successfully using BCrypt")
    void testHashPassword() {
        // Given
        String password = "testPassword123";
        
        // When
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        // Then
        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertNotEquals(password, hashedPassword, "Hashed password should be different from original");
        assertTrue(hashedPassword.startsWith("$2a$"), "BCrypt hash should start with $2a$");
        assertTrue(hashedPassword.length() > 50, "BCrypt hash should be sufficiently long");
    }

    @Test
    @DisplayName("Should verify correct password against hash")
    void testVerifyPasswordCorrect() {
        // Given
        String password = "mySecurePassword456";
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        // When
        boolean isValid = SecurityUtils.verifyPassword(password, hashedPassword);
        
        // Then
        assertTrue(isValid, "Correct password should verify successfully");
    }

    @Test
    @DisplayName("Should reject incorrect password against hash")
    void testVerifyPasswordIncorrect() {
        // Given
        String correctPassword = "correctPassword789";
        String incorrectPassword = "wrongPassword123";
        String hashedPassword = SecurityUtils.hashPassword(correctPassword);
        
        // When
        boolean isValid = SecurityUtils.verifyPassword(incorrectPassword, hashedPassword);
        
        // Then
        assertFalse(isValid, "Incorrect password should not verify");
    }

    @Test
    @DisplayName("Should handle null password in hashing")
    void testHashPasswordNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> SecurityUtils.hashPassword(null),
            "Hashing null password should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Should handle null inputs in verification")
    void testVerifyPasswordNullInputs() {
        String validHash = SecurityUtils.hashPassword("test");
        
        // Test null password
        assertFalse(SecurityUtils.verifyPassword(null, validHash), 
            "Null password should return false");
        
        // Test null hash
        assertFalse(SecurityUtils.verifyPassword("test", null), 
            "Null hash should return false");
        
        // Test both null
        assertFalse(SecurityUtils.verifyPassword(null, null), 
            "Both null should return false");
    }

    @Test
    @DisplayName("Should handle invalid hash format in verification")
    void testVerifyPasswordInvalidHash() {
        // Given
        String password = "testPassword";
        String invalidHash = "invalidHashFormat";
        
        // When
        boolean isValid = SecurityUtils.verifyPassword(password, invalidHash);
        
        // Then
        assertFalse(isValid, "Invalid hash format should return false");
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void testDifferentHashesForSamePassword() {
        // Given
        String password = "samePassword123";
        
        // When
        String hash1 = SecurityUtils.hashPassword(password);
        String hash2 = SecurityUtils.hashPassword(password);
        
        // Then
        assertNotEquals(hash1, hash2, "Same password should generate different hashes due to different salts");
        
        // But both should verify correctly
        assertTrue(SecurityUtils.verifyPassword(password, hash1), "First hash should verify");
        assertTrue(SecurityUtils.verifyPassword(password, hash2), "Second hash should verify");
    }

    @Test
    @DisplayName("Should validate email format correctly")
    void testEmailValidation() {
        // Valid emails
        assertTrue(SecurityUtils.isValidEmail("test@example.com"));
        assertTrue(SecurityUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(SecurityUtils.isValidEmail("user+tag@example.org"));
        
        // Invalid emails
        assertFalse(SecurityUtils.isValidEmail(null));
        assertFalse(SecurityUtils.isValidEmail(""));
        assertFalse(SecurityUtils.isValidEmail("invalid-email"));
        assertFalse(SecurityUtils.isValidEmail("@example.com"));
        assertFalse(SecurityUtils.isValidEmail("user@"));
    }

    @Test
    @DisplayName("Should validate strong password correctly")
    void testStrongPasswordValidation() {
        // Strong passwords
        assertTrue(SecurityUtils.isStrongPassword("StrongPass123!"));
        assertTrue(SecurityUtils.isStrongPassword("MySecure@Password1"));
        
        // Weak passwords
        assertFalse(SecurityUtils.isStrongPassword(null));
        assertFalse(SecurityUtils.isStrongPassword("short"));
        assertFalse(SecurityUtils.isStrongPassword("nouppercase123!"));
        assertFalse(SecurityUtils.isStrongPassword("NOLOWERCASE123!"));
        assertFalse(SecurityUtils.isStrongPassword("NoNumbers!"));
        assertFalse(SecurityUtils.isStrongPassword("NoSpecialChars123"));
    }
}
