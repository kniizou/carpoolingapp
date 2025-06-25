package com.carpooling.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.carpooling.model.User;
import com.carpooling.service.IAuthService;
import com.carpooling.service.ITripService;
import com.carpooling.service.IUserService;
import com.carpooling.ui.AdminDashboard;
import com.carpooling.ui.DriverDashboard;
import com.carpooling.ui.PassengerDashboard;

import net.miginfocom.swing.MigLayout;

/**
 * Modern login and registration panel with switching functionality
 */
public class PanelLoginAndRegister extends JLayeredPane {

    private JCheckBox checkBoxConducteur;
    private JPanel login;
    private JPanel register;
    private final IAuthService authService;
    private final IUserService userService;
    private final ITripService tripService;
    
    public PanelLoginAndRegister(IAuthService authService, IUserService userService, ITripService tripService) {
        this.authService = authService;
        this.userService = userService;
        this.tripService = tripService;
        
        initComponents();
        initRegister();
        initLogin();
        login.setVisible(false);
        register.setVisible(true);
    }

    private void initRegister() {
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]10[]10[]25[]push"));
        JLabel label = new JLabel("Créer un compte");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(128, 15, 28));
        register.add(label);
        
        final MyTextField txtNom = new MyTextField();
        txtNom.setHint("Nom");
        register.add(txtNom, "w 60%");
        
        final MyTextField txtPrenom = new MyTextField();
        txtPrenom.setHint("Prénom");
        register.add(txtPrenom, "w 60%");
        
        final MyTextField txtEmail = new MyTextField();
        txtEmail.setHint("Email");
        register.add(txtEmail, "w 60%");
        
        final MyPasswordField txtPass = new MyPasswordField();
        txtPass.setHint("Mot de passe");
        register.add(txtPass, "w 60%");
        
        checkBoxConducteur = new JCheckBox("Je suis conducteur");
        checkBoxConducteur.setFont(new Font("sansserif", 0, 14));
        checkBoxConducteur.setBackground(new Color(255, 255, 255));
        checkBoxConducteur.setForeground(new Color(128, 15, 28));
        register.add(checkBoxConducteur, "w 60%");
        
        Button cmd = new Button();
        cmd.setBackground(new Color(128, 15, 28));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.setText("S'INSCRIRE");

        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nom = txtNom.getText();
                String prenom = txtPrenom.getText();
                String email = txtEmail.getText().toLowerCase();
                String password = new String(txtPass.getPassword());
                String typeUtilisateur;
                
                if (email.endsWith("@admin.com")) {
                    typeUtilisateur = "ADMIN";
                } else {
                    boolean isConducteur = checkBoxConducteur.isSelected();
                    typeUtilisateur = isConducteur ? "DRIVER" : "PASSENGER";
                }

                // Validation des champs
                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(register, 
                        "Veuillez remplir tous les champs",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Vérification admin
                if (email.endsWith("@admin.com")) {
                    String adminCode = JOptionPane.showInputDialog(register, 
                        "Code d'administration requis:", 
                        "Vérification Admin", JOptionPane.QUESTION_MESSAGE);

                    if (!"admincov".equals(adminCode)) {
                        JOptionPane.showMessageDialog(register, 
                            "Code admin incorrect", 
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                try {
                    // Create user object with age (default to 18 for simplicity)
                    User newUser = new User(nom, prenom, 18, email, password, typeUtilisateur);
                    
                    if (userService.registerUser(newUser)) {
                        JOptionPane.showMessageDialog(register, 
                            "Inscription réussie!",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Authenticate and redirect
                        if (authService.authenticate(email, password)) {
                            User currentUser = authService.getCurrentUser();
                            redirectUser(currentUser);
                        }
                    } else {
                        JOptionPane.showMessageDialog(register, 
                            "Erreur lors de l'inscription",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(register, 
                        "Erreur d'inscription: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        register.add(cmd, "w 40%, h 40");
    }

    public void initLogin() {
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel label = new JLabel("Se connecter");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(94, 11, 21));
        login.add(label);
        
        final MyTextField txtEmail = new MyTextField();
        txtEmail.setHint("Email");
        login.add(txtEmail, "w 60%");
        
        final MyPasswordField txtPass = new MyPasswordField();
        txtPass.setHint("Mot de passe");
        login.add(txtPass, "w 60%");
        
        Button cmd = new Button();
        cmd.setBackground(new Color(128, 15, 28));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.setText("SE CONNECTER");
    
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().toLowerCase();
                String password = new String(txtPass.getPassword());
                
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(login, 
                        "Veuillez remplir tous les champs", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (authService.authenticate(email, password)) {
                    User currentUser = authService.getCurrentUser();
                    JOptionPane.showMessageDialog(login, 
                        "Connexion réussie!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    redirectUser(currentUser);
                } else {
                    JOptionPane.showMessageDialog(login, 
                        "Email ou mot de passe incorrect", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        login.add(cmd, "w 40%, h 40");
    }
    
    private void redirectUser(User user) {
        Window win = SwingUtilities.getWindowAncestor(PanelLoginAndRegister.this);
        if (win != null) win.dispose();
        
        switch (user.getRole().toUpperCase()) {
            case "ADMIN":
                openAdminDashboard();
                break;
            case "DRIVER":
                openDriverDashboard();
                break;
            case "PASSENGER":
                openPassengerDashboard();
                break;
        }
    }
    
    private void openAdminDashboard() {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard adminDashboard = new AdminDashboard(authService, userService, tripService);
            adminDashboard.setVisible(true);
            adminDashboard.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        });
    }

    private void openDriverDashboard() {
        SwingUtilities.invokeLater(() -> {
            DriverDashboard driverDashboard = new DriverDashboard(authService, userService, tripService);
            driverDashboard.setVisible(true);
        });
    }

    private void openPassengerDashboard() {
        SwingUtilities.invokeLater(() -> {
            PassengerDashboard passengerDashboard = new PassengerDashboard(authService, userService, tripService);
            passengerDashboard.setVisible(true);
        });
    }
    
    public void showRegister(boolean show) {
        // Create smooth transition animation
        animateTransition(show);
    }
    
    private void animateTransition(boolean showRegister) {
        Timer timer = new Timer(16, null); // ~60 FPS for smooth animation
        final int totalSteps = 30; // Animation duration in frames
        final int panelWidth = getWidth() > 0 ? getWidth() : 500;
        
        timer.addActionListener(new ActionListener() {
            private int currentStep = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentStep++;
                
                // Calculate progress with easing
                double progress = (double) currentStep / totalSteps;
                double easedProgress = AnimationUtils.easeInOut(progress);
                
                if (showRegister) {
                    // Animate to show register panel (slide from right)
                    int loginX = (int) AnimationUtils.lerp(0, -panelWidth, easedProgress);
                    int registerX = (int) AnimationUtils.lerp(panelWidth, 0, easedProgress);
                    
                    login.setLocation(loginX, 0);
                    register.setLocation(registerX, 0);
                } else {
                    // Animate to show login panel (slide from left)
                    int registerX = (int) AnimationUtils.lerp(0, panelWidth, easedProgress);
                    int loginX = (int) AnimationUtils.lerp(-panelWidth, 0, easedProgress);
                    
                    register.setLocation(registerX, 0);
                    login.setLocation(loginX, 0);
                }
                
                repaint();
                
                if (currentStep >= totalSteps) {
                    timer.stop();
                    // Finalize positions and visibility
                    if (showRegister) {
                        register.setVisible(true);
                        login.setVisible(false);
                        register.setLocation(0, 0);
                    } else {
                        login.setVisible(true);
                        register.setVisible(false);
                        login.setLocation(0, 0);
                    }
                    repaint();
                }
            }
        });
        
        // Prepare panels for animation
        if (showRegister) {
            register.setVisible(true);
            register.setLocation(panelWidth, 0);
            login.setLocation(0, 0);
        } else {
            login.setVisible(true);
            login.setLocation(-panelWidth, 0);
            register.setLocation(0, 0);
        }
        
        timer.start();
    }
    
    private void initComponents() {
        login = new JPanel();
        register = new JPanel();

        setLayout(null); // Use absolute positioning for animation
        
        login.setBackground(new java.awt.Color(255, 255, 255));
        login.setBounds(0, 0, 500, 540);
        add(login);

        register.setBackground(new java.awt.Color(255, 255, 255));
        register.setBounds(0, 0, 500, 540);
        add(register);
    }
}
