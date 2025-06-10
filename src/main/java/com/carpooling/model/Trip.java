package com.carpooling.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.Arrays;

public class Trip {
    private String id;
    private User driver;
    private String departure;
    private String destination;
    private String date;
    private String time;
    private int availableSeats;
    private double price;
    private String tripType;
    private List<String> recurringDays;
    private List<User> passengers;
    private List<User> pendingPassengers;

    public Trip(String id, User driver, String departure, String destination, String date, String time, int availableSeats, double price) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        if (driver == null) {
            throw new IllegalArgumentException("Le conducteur ne peut pas être null");
        }
        if (departure == null || departure.trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de départ ne peut pas être vide");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("La destination ne peut pas être vide");
        }
        if (availableSeats <= 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être supérieur à 0");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        
        // Validation de la date
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez le format dd/MM/yyyy");
        }
        
        // Validation de l'heure
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format d'heure invalide. Utilisez le format HH:mm");
        }

        this.id = id;
        this.driver = driver;
        this.departure = departure.trim();
        this.destination = destination.trim();
        this.date = date;
        this.time = time;
        this.availableSeats = availableSeats;
        this.price = price;
        this.tripType = "Occasionnel";
        this.recurringDays = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.pendingPassengers = new ArrayList<>();
    }

    public Trip(User driver, String departure, String destination, String date, String time, int availableSeats, double price) {
        if (driver == null) {
            throw new IllegalArgumentException("Le conducteur ne peut pas être null");
        }
        if (departure == null || departure.trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de départ ne peut pas être vide");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("La destination ne peut pas être vide");
        }
        if (availableSeats <= 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être supérieur à 0");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        
        // Validation de la date
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez le format dd/MM/yyyy");
        }
        
        // Validation de l'heure
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format d'heure invalide. Utilisez le format HH:mm");
        }

        this.id = UUID.randomUUID().toString();
        this.driver = driver;
        this.departure = departure.trim();
        this.destination = destination.trim();
        this.date = date;
        this.time = time;
        this.availableSeats = availableSeats;
        this.price = price;
        this.tripType = "Occasionnel";
        this.recurringDays = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.pendingPassengers = new ArrayList<>();
    }

    public Trip(User driver, String departure, String destination, String date, String time, int availableSeats, double price, String tripType, String recurringDays) {
        if (driver == null) {
            throw new IllegalArgumentException("Le conducteur ne peut pas être null");
        }
        if (departure == null || departure.trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de départ ne peut pas être vide");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("La destination ne peut pas être vide");
        }
        if (availableSeats <= 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être supérieur à 0");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        if (tripType == null || tripType.trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de trajet ne peut pas être vide");
        }
        
        // Validation de la date
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez le format dd/MM/yyyy");
        }
        
        // Validation de l'heure
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format d'heure invalide. Utilisez le format HH:mm");
        }

        this.id = UUID.randomUUID().toString();
        this.driver = driver;
        this.departure = departure.trim();
        this.destination = destination.trim();
        this.date = date;
        this.time = time;
        this.availableSeats = availableSeats;
        this.price = price;
        this.tripType = tripType;
        this.recurringDays = new ArrayList<>();
        if (recurringDays != null && !recurringDays.isEmpty()) {
            this.recurringDays.addAll(Arrays.asList(recurringDays.split(",")));
        }
        this.passengers = new ArrayList<>();
        this.pendingPassengers = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public User getDriver() {
        return driver;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getPrice() {
        return price;
    }

    public String getTripType() {
        return tripType;
    }

    public List<String> getRecurringDays() {
        return recurringDays;
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public List<User> getPendingPassengers() {
        return pendingPassengers;
    }

    // Setters
    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public void setDeparture(String departure) {
        if (departure == null || departure.trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de départ ne peut pas être vide");
        }
        this.departure = departure.trim();
    }

    public void setDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("La destination ne peut pas être vide");
        }
        this.destination = destination.trim();
    }

    public void setDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            this.date = date;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez le format dd/MM/yyyy");
        }
    }

    public void setTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            this.time = time;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format d'heure invalide. Utilisez le format HH:mm");
        }
    }

    public void setAvailableSeats(int availableSeats) {
        if (availableSeats < 0) {
            throw new IllegalArgumentException("Le nombre de places disponibles ne peut pas être négatif");
        }
        this.availableSeats = availableSeats;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        this.price = price;
    }

    public void setRecurringDays(List<String> recurringDays) {
        this.recurringDays = recurringDays;
    }

    public boolean isRecurring() {
        return !recurringDays.isEmpty();
    }

    // Méthodes pour gérer les passagers
    public void addPassenger(User passenger) {
        if (!passengers.contains(passenger) && !pendingPassengers.contains(passenger)) {
            pendingPassengers.add(passenger);
        }
    }

    public void confirmPassenger(User passenger) {
        if (pendingPassengers.remove(passenger)) {
            passengers.add(passenger);
            availableSeats--;
        }
    }

    public void rejectPassenger(User passenger) {
        pendingPassengers.remove(passenger);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id.equals(trip.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 