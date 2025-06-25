package com.carpooling.service;

import java.util.List;

import com.carpooling.model.User;
import com.carpooling.repository.IUserRepository;
import com.carpooling.util.SecurityUtils;

/**
 * Implementation of the user service.
 * Handles user registration, retrieval, and validation with business logic.
 */
public class UserServiceImpl implements IUserService {
    
    private final IUserRepository userRepository;
    
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        validateUserData(user);
        
        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists");
        }
        
        // Hash the password before saving to database
        String hashedPassword = SecurityUtils.hashPassword(user.getPassword());
        User userWithHashedPassword = new User(
            user.getId(),
            user.getNom(),
            user.getPrenom(),
            user.getAge(),
            user.getEmail(),
            hashedPassword,
            user.getRole()  // getRole() already returns the string code
        );
        
        return userRepository.save(userWithHashedPassword);
    }
    
    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        return userRepository.findByEmail(email);
    }
    
    @Override
    public User getUserByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        return userRepository.findByName(name);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public void validateUserData(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (user.getNom() == null || user.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        
        if (user.getPrenom() == null || user.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        
        if (user.getAge() < 16 || user.getAge() > 120) {
            throw new IllegalArgumentException("Age must be between 16 and 120");
        }
        
        if (!SecurityUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (!SecurityUtils.isValidRole(user.getRole())) {
            throw new IllegalArgumentException("Invalid user role");
        }
    }
}
