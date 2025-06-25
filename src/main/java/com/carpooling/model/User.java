package com.carpooling.model;

public class User {
    private String id;
    private String nom;
    private String prenom;
    private int age;
    private String email;
    private String password;
    private UserRole role;

    public User(String id, String nom, String prenom, int age, String email, String password, String role) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        if (age < 18) {
            throw new IllegalArgumentException("L'âge doit être supérieur ou égal à 18 ans");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email n'est pas valide");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }

        this.id = id;
        this.nom = nom.trim();
        this.prenom = prenom.trim();
        this.age = age;
        this.email = email.toLowerCase().trim();
        this.password = password;
        this.role = UserRole.fromCode(role);
    }

    public User(String nom, String prenom, int age, String email, String password, String role) {
        this(java.util.UUID.randomUUID().toString(), nom, prenom, age, email, password, role);
    }
    
    /**
     * Constructor with UserRole enum.
     */
    public User(String id, String nom, String prenom, int age, String email, String password, UserRole role) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        if (age < 18) {
            throw new IllegalArgumentException("L'âge doit être supérieur ou égal à 18 ans");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email n'est pas valide");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }
        if (role == null) {
            throw new IllegalArgumentException("Le rôle ne peut pas être null");
        }

        this.id = id;
        this.nom = nom.trim();
        this.prenom = prenom.trim();
        this.age = age;
        this.email = email.toLowerCase().trim();
        this.password = password;
        this.role = role;
    }
    
    /**
     * Constructor with UserRole enum (auto-generates ID).
     */
    public User(String nom, String prenom, int age, String email, String password, UserRole role) {
        this(java.util.UUID.randomUUID().toString(), nom, prenom, age, email, password, role);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role.getCode();
    }
    
    /**
     * Gets the user role as an enum.
     * 
     * @return The UserRole enum value
     */
    public UserRole getUserRole() {
        return role;
    }

    public String getFullName() {
        return nom + " " + prenom;
    }

    public String getName() {
        return getFullName();
    }

    // Setters avec validation
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.nom = nom.trim();
    }

    public void setPrenom(String prenom) {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }
        this.prenom = prenom.trim();
    }

    public void setAge(int age) {
        if (age < 18) {
            throw new IllegalArgumentException("L'âge doit être supérieur ou égal à 18 ans");
        }
        this.age = age;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email n'est pas valide");
        }
        this.email = email.toLowerCase().trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }
        this.password = password;
    }

    public void setRole(String role) {
        this.role = UserRole.fromCode(role);
    }
    
    /**
     * Sets the user role using an enum.
     * 
     * @param role The UserRole enum value
     */
    public void setUserRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Le rôle ne peut pas être null");
        }
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 