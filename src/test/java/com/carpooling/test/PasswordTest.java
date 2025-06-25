package com.carpooling.test;

import com.carpooling.util.SecurityUtils;

public class PasswordTest {
    public static void main(String[] args) {
        String plainPassword = "admin2025";
        String storedHash = "$2a$10$GRVUpKD9wmLUAe3p/AQuSurKtadteSGqWOSBRxAY2Gr3v04WvPPKW";
        
        System.out.println("Testing password verification:");
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Stored hash: " + storedHash);
        
        boolean isValid = SecurityUtils.verifyPassword(plainPassword, storedHash);
        System.out.println("Password verification result: " + isValid);
        
        // Test with wrong password
        boolean isWrongValid = SecurityUtils.verifyPassword("wrongpassword", storedHash);
        System.out.println("Wrong password verification result: " + isWrongValid);
        
        // Test creating a new hash
        String newHash = SecurityUtils.hashPassword(plainPassword);
        System.out.println("New hash: " + newHash);
        boolean newHashValid = SecurityUtils.verifyPassword(plainPassword, newHash);
        System.out.println("New hash verification result: " + newHashValid);
    }
}
