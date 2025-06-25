package com.carpooling.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.carpooling.model.User;
import com.carpooling.repository.IUserRepository;
import com.carpooling.repository.SqliteUserRepository;
import com.carpooling.util.SecurityUtils;

/**
 * Unit tests for AuthServiceImpl.
 */
class AuthServiceImplTest {
    
    @Mock
    private IUserRepository userRepository;
    
    private AuthServiceImpl authService;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository);
        
        // Create a test user with BCrypt hashed password
        String hashedPassword = SecurityUtils.hashPassword("testpassword");
        testUser = new User("test-id", "Doe", "John", 25, "john.doe@example.com", hashedPassword, "PASSENGER");
    }
    
    @Test
    void testAuthenticate_ValidCredentials_ReturnsTrue() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        
        // Act
        boolean result = authService.authenticate("john.doe@example.com", "testpassword");
        
        // Assert
        assertTrue(result);
        assertEquals(testUser, authService.getCurrentUser());
        verify(userRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void testAuthenticate_InvalidPassword_ReturnsFalse() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        
        // Act
        boolean result = authService.authenticate("john.doe@example.com", "wrongpassword");
        
        // Assert
        assertFalse(result);
        assertNull(authService.getCurrentUser());
        verify(userRepository).findByEmail("john.doe@example.com");
    }
    
    @Test
    void testAuthenticate_UserNotFound_ReturnsFalse() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);
        
        // Act
        boolean result = authService.authenticate("nonexistent@example.com", "password");
        
        // Assert
        assertFalse(result);
        assertNull(authService.getCurrentUser());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
    
    @Test
    void testAuthenticate_NullEmail_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate(null, "password");
        });
    }
    
    @Test
    void testAuthenticate_EmptyEmail_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate("", "password");
        });
    }
    
    @Test
    void testAuthenticate_NullPassword_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authService.authenticate("john.doe@example.com", null);
        });
    }
    
    @Test
    void testLogout_ClearsCurrentUser() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        authService.authenticate("john.doe@example.com", "testpassword");
        
        // Act
        authService.logout();
        
        // Assert
        assertNull(authService.getCurrentUser());
        assertFalse(authService.isAuthenticated());
    }
    
    @Test
    void testIsAuthenticated_WithUser_ReturnsTrue() {
        // Arrange
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(testUser);
        authService.authenticate("john.doe@example.com", "testpassword");
        
        // Act & Assert
        assertTrue(authService.isAuthenticated());
    }
    
    @Test
    void testIsAuthenticated_WithoutUser_ReturnsFalse() {
        // Act & Assert
        assertFalse(authService.isAuthenticated());
    }
    
    @Test
    void testRegister_ValidUser_ReturnsUserWithHashedPassword() {
        // Arrange
        when(userRepository.findByEmail("new.user@example.com")).thenReturn(null);
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(true);
        
        // Act
        User result = authService.register("Dupont", "Marie", 28, "new.user@example.com", "plainPassword", "DRIVER");
        
        // Assert
        assertEquals("Dupont", result.getNom());
        assertEquals("Marie", result.getPrenom());
        assertEquals("new.user@example.com", result.getEmail());
        assertTrue(result.getPassword().startsWith("$2")); // BCrypt hash starts with $2
        assertTrue(SecurityUtils.verifyPassword("plainPassword", result.getPassword()));
        verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
    }
    
    @Test
    void testRegister_ExistingEmail_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail("existing@example.com")).thenReturn(testUser);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register("Test", "User", 25, "existing@example.com", "password", "PASSENGER"));
        
        assertEquals("A user with this email already exists", exception.getMessage());
    }
    
    @Test
    void testRegister_NullEmail_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register("Test", "User", 25, null, "password", "PASSENGER"));
        
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void testRegister_EmptyPassword_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.register("Test", "User", 25, "test@example.com", "", "PASSENGER"));
        
        assertEquals("Password cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void testClearAllUserData_ValidConfirmation_ClearsData() {
        // Arrange
        String validConfirmation = "ADMIN_CLEAR_DATABASE";
        
        // Act (this should not throw an exception)
        authService.clearAllUserData(validConfirmation);
        
        // Assert - current user should be null after clearing
        assertNull(authService.getCurrentUser());
    }
    
    @Test
    void testClearAllUserData_InvalidConfirmation_ThrowsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> authService.clearAllUserData("WRONG_CONFIRMATION"));
        
        assertEquals("Invalid admin confirmation for database clearing", exception.getMessage());
    }
    
    @Test
    void testCompleteUserRegistrationAndAuthenticationFlow() {
        // Arrange - Clear database and use real repository (not mocked)
        SqliteUserRepository realRepository = new SqliteUserRepository();
        AuthServiceImpl realAuthService = new AuthServiceImpl(realRepository);
        
        // Clear any existing data
        realAuthService.clearAllUserData("ADMIN_CLEAR_DATABASE");
        
        String email = "integration.test@example.com";
        String plainPassword = "mySecureTestPassword123";
        
        // Act 1 - Register new user
        User registeredUser = realAuthService.register("TestNom", "TestPrenom", 25, email, plainPassword, "PASSENGER");
        
        // Assert 1 - Verify user was created with hashed password
        assertEquals("TestNom", registeredUser.getNom());
        assertEquals("TestPrenom", registeredUser.getPrenom());
        assertEquals(email, registeredUser.getEmail());
        assertTrue(registeredUser.getPassword().startsWith("$2"), "Password should be BCrypt hashed (starts with $2)");
        assertTrue(registeredUser.getPassword().length() > 50, "BCrypt hash should be substantial length");
        
        // Act 2 - Verify password was hashed correctly by checking direct verification
        assertTrue(SecurityUtils.verifyPassword(plainPassword, registeredUser.getPassword()), 
                  "Original password should verify against stored hash");
        
        // Act 3 - Test authentication with correct password
        boolean authSuccess = realAuthService.authenticate(email, plainPassword);
        assertTrue(authSuccess, "Authentication with correct password should succeed");
        assertEquals(registeredUser.getEmail(), realAuthService.getCurrentUser().getEmail());
        
        // Act 4 - Test authentication with wrong password
        realAuthService.logout();
        boolean authFailure = realAuthService.authenticate(email, "wrongPassword");
        assertFalse(authFailure, "Authentication with wrong password should fail");
        assertNull(realAuthService.getCurrentUser(), "No user should be authenticated after failed login");
        
        // Act 5 - Verify password is stored correctly in database
        User retrievedUser = realRepository.findByEmail(email);
        assertEquals(registeredUser.getPassword(), retrievedUser.getPassword(), 
                    "Stored password hash should match registered user hash");
        
        // Clean up
        realAuthService.clearAllUserData("ADMIN_CLEAR_DATABASE");
    }
    
    @Test
    void testUserRegistrationPersistsHashedPassword() {
        // Arrange - Use real repository to test actual database persistence
        SqliteUserRepository realRepository = new SqliteUserRepository();
        AuthServiceImpl realAuthService = new AuthServiceImpl(realRepository);
        
        // Clear database first
        realAuthService.clearAllUserData("ADMIN_CLEAR_DATABASE");
        
        String testEmail = "persistence.test@example.com";
        String testPassword = "testPassword123!";
        
        // Act - Register user
        User registeredUser = realAuthService.register("PersistTest", "User", 28, testEmail, testPassword, "DRIVER");
        
        // Assert - Verify the registration created proper hash
        assertTrue(registeredUser.getPassword().startsWith("$2"), "Password should be BCrypt hashed");
        assertTrue(registeredUser.getPassword().length() >= 59, "BCrypt hash should be at least 59 characters");
        
        // Verify the hash validates correctly
        assertTrue(SecurityUtils.verifyPassword(testPassword, registeredUser.getPassword()), 
                  "Password should validate against its hash");
        
        // Verify wrong password fails
        assertFalse(SecurityUtils.verifyPassword("wrongPassword", registeredUser.getPassword()), 
                   "Wrong password should not validate");
        
        // Verify user can authenticate successfully
        assertTrue(realAuthService.authenticate(testEmail, testPassword), 
                  "User should authenticate with correct password");
        
        // Verify authentication fails with wrong password
        realAuthService.logout();
        assertFalse(realAuthService.authenticate(testEmail, "wrongPassword"), 
                   "User should not authenticate with wrong password");
        
        // Leave user in database for manual verification (don't clean up in this test)
        System.out.println("Test user created with email: " + testEmail);
        System.out.println("BCrypt hash: " + registeredUser.getPassword());
    }
}
