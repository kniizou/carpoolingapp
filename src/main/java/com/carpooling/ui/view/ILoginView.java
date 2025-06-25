package com.carpooling.ui.view;

/**
 * Interface for the Login view, defining the contract between the view and presenter.
 */
public interface ILoginView {
    
    /**
     * Gets the email entered by the user.
     * @return the email string
     */
    String getEmail();
    
    /**
     * Gets the password entered by the user.
     * @return the password string
     */
    String getPassword();
    
    /**
     * Clears the password field for security.
     */
    void clearPassword();
    
    /**
     * Shows an error message to the user.
     * @param message the error message to display
     */
    void showLoginError(String message);
    
    /**
     * Shows a success message to the user.
     * @param message the success message to display
     */
    void showLoginSuccess(String message);
    
    /**
     * Navigates to the appropriate dashboard based on user role.
     * @param user the authenticated user
     */
    void navigateToDashboard(com.carpooling.model.User user);
    
    /**
     * Navigates to the registration screen.
     */
    void navigateToRegister();
    
    /**
     * Shows/hides the loading state.
     * @param loading true to show loading, false to hide
     */
    void setLoading(boolean loading);
    
    /**
     * Enables/disables the login form.
     * @param enabled true to enable, false to disable
     */
    void setFormEnabled(boolean enabled);
    
    /**
     * Shows the login frame.
     */
    void showView();
    
    /**
     * Hides the login frame.
     */
    void hide();
    
    /**
     * Disposes of the login frame resources.
     */
    void dispose();
}
