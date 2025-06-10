package com.carpooling.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtils {
    private ValidationUtils() {
        // Empêcher l'instanciation
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " ne peut pas être vide");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_EMAIL);
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_PASSWORD);
        }
    }

    public static void validateAge(int age) {
        if (age < 18) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_AGE);
        }
    }

    public static void validateRole(String role) {
        if (role == null || (!role.equals(Constants.ROLE_ADMIN) && 
            !role.equals(Constants.ROLE_DRIVER) && 
            !role.equals(Constants.ROLE_PASSENGER))) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_ROLE);
        }
    }

    public static void validateDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(Constants.DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_DATE);
        }
    }

    public static void validateTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_TIME);
        }
    }

    public static void validatePrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_PRICE);
        }
    }

    public static void validateSeats(int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException(Constants.ERROR_INVALID_SEATS);
        }
    }

    public static void validatePassengerStatus(String status) {
        if (status == null || (!status.equals(Constants.STATUS_CONFIRMED) && 
            !status.equals(Constants.STATUS_PENDING) && 
            !status.equals(Constants.STATUS_REJECTED))) {
            throw new IllegalArgumentException("Le statut doit être CONFIRMED, PENDING ou REJECTED");
        }
    }

    public static String normalizeEmail(String email) {
        return email != null ? email.toLowerCase().trim() : null;
    }

    public static String normalizeString(String str) {
        return str != null ? str.trim() : null;
    }
} 