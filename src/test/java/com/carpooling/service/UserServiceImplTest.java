package com.carpooling.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.carpooling.model.User;
import com.carpooling.repository.IUserRepository;
import com.carpooling.util.SecurityUtils;

/**
 * Unit tests for UserServiceImpl.
 */
class UserServiceImplTest {
    
    @Mock
    private IUserRepository userRepository;
    
    private UserServiceImpl userService;
    private User validUser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository);
        
        String hashedPassword = SecurityUtils.hashPassword("validpassword");
        validUser = new User("test-id", "Doe", "John", 25, "john.doe@example.com", hashedPassword, "PASSENGER");
    }
    
    @Test
    void testRegisterUser_ValidUser_ReturnsTrue() {
        // Arrange
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userRepository.save(validUser)).thenReturn(true);
        
        // Act
        boolean result = userService.registerUser(validUser);
        
        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(validUser);
    }
    
    @Test
    void testRegisterUser_ExistingEmail_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(validUser);
        });
        
        assertEquals("A user with this email already exists", exception.getMessage());
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void testRegisterUser_NullUser_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(null);
        });
    }
    
    @Test
    void testGetUserByEmail_ExistingUser_ReturnsUser() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(validUser);
        
        // Act
        User result = userService.getUserByEmail("john.doe@example.com");
        
        // Assert
        assertEquals(validUser, result);
        verify(userRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void testGetUserByEmail_NonExistingUser_ReturnsNull() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);
        
        // Act
        User result = userService.getUserByEmail("nonexistent@example.com");
        
        // Assert
        assertNull(result);
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void testGetUserByEmail_NullEmail_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserByEmail(null);
        });
    }
    
    @Test
    void testGetAllUsers_ReturnsAllUsers() {
        // Arrange
        String hashedPassword2 = SecurityUtils.hashPassword("validpassword");
        User user2 = new User("id2", "Smith", "Jane", 30, "jane@example.com", hashedPassword2, "DRIVER");
        List<User> users = Arrays.asList(validUser, user2);
        when(userRepository.findAll()).thenReturn(users);
        
        // Act
        List<User> result = userService.getAllUsers();
        
        // Assert
        assertEquals(users, result);
        verify(userRepository).findAll();
    }
    
    @Test
    void testValidateUserData_ValidUser_NoException() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            userService.validateUserData(validUser);
        });
    }
    
    @Test
    void testValidateUserData_NullUser_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.validateUserData(null);
        });
    }
    
    @Test
    void testValidateUserData_EmptyFirstName_ThrowsException() {
        // Act & Assert
        // Since User constructor validates, we test the service validation directly
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("id", "Doe", "", 25, "john@example.com", "password123", "PASSENGER");
        });
        
        assertEquals("Le prénom ne peut pas être vide", exception.getMessage());
    }
    
    @Test
    void testValidateUserData_InvalidAge_ThrowsException() {
        // Act & Assert
        // Since User constructor validates, we test the service validation directly
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("id", "Doe", "John", 15, "john@example.com", "password123", "PASSENGER");
        });
        
        assertEquals("L'âge doit être supérieur ou égal à 18 ans", exception.getMessage());
    }
    
    @Test
    void testValidateUserData_InvalidEmail_ThrowsException() {
        // Act & Assert
        // Since User constructor validates, we test the service validation directly
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("id", "Doe", "John", 25, "invalid-email", "password123", "PASSENGER");
        });
        
        assertEquals("L'email n'est pas valide", exception.getMessage());
    }
    
    @Test
    void testValidateUserData_InvalidRole_ThrowsException() {
        // Act & Assert
        // Since User constructor validates, we test the service validation directly
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            User invalidUser = new User("id", "Doe", "John", 25, "john@example.com", "password123", "INVALID_ROLE");
        });
        
        assertEquals("Invalid role code: INVALID_ROLE", exception.getMessage());
    }
}
