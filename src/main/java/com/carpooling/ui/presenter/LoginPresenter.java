package com.carpooling.ui.presenter;

import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.carpooling.model.User;
import com.carpooling.service.IAuthService;
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.ui.view.ILoginView;

/**
 * Presenter for the Login screen, handling all business logic and user interactions.
 */
public class LoginPresenter {
    private static final Logger LOGGER = Logger.getLogger(LoginPresenter.class.getName());
    
    private final ILoginView view;
    private final IAuthService authService;
    private final IUserService userService;
    private final ITripService tripService;
    
    public LoginPresenter(ILoginView view, IAuthService authService, IUserService userService, ITripService tripService) {
        this.view = view;
        this.authService = authService;
        this.userService = userService;
        this.tripService = tripService;
    }
    
    /**
     * Handles the login button click event.
     */
    public void onLoginClicked() {
        String email = view.getEmail();
        String password = view.getPassword();
        
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            view.showLoginError("Veuillez saisir votre adresse email.");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            view.showLoginError("Veuillez saisir votre mot de passe.");
            return;
        }
        
        // Show loading state
        view.setLoading(true);
        view.setFormEnabled(false);
        
        // Perform authentication in background thread
        SwingUtilities.invokeLater(() -> {
            try {
                boolean isAuthenticated = authService.authenticate(email.trim(), password);
                
                SwingUtilities.invokeLater(() -> {
                    view.setLoading(false);
                    view.setFormEnabled(true);
                    
                    if (isAuthenticated) {
                        User authenticatedUser = authService.getCurrentUser();
                        view.clearPassword();
                        view.showLoginSuccess("Connexion réussie !");
                        view.navigateToDashboard(authenticatedUser);
                    } else {
                        view.showLoginError("Email ou mot de passe incorrect.");
                    }
                });
            } catch (Exception e) {
                LOGGER.severe(() -> "Login error: " + e.getMessage());
                SwingUtilities.invokeLater(() -> {
                    view.setLoading(false);
                    view.setFormEnabled(true);
                    view.showLoginError("Erreur de connexion : " + e.getMessage());
                });
            }
        });
    }
    
    /**
     * Handles the register button click event.
     */
    public void onRegisterClicked() {
        view.navigateToRegister();
    }
    
    /**
     * Shows the login view.
     */
    public void showLogin() {
        view.showView();
    }
    
    /**
     * Gets the services for passing to dashboard views.
     */
    public IUserService getUserService() {
        return userService;
    }
    
    public ITripService getTripService() {
        return tripService;
    }
    
    public IAuthService getAuthService() {
        return authService;
    }
}
