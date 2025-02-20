package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.JwtUtil;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class LoginContoller {

    @FXML
    private TextField mail;
    @FXML
    private PasswordField mdp;

    private final ServiceUser userService = new ServiceUser();

    @FXML
    private void handleLogin() {
        String email = mail.getText().trim();
        String password = mdp.getText().trim();

        // Validate email and password
        if (!isValidEmail(email)) {
            showAlert("Validation Error", "Veuillez entrer un email valide.");
            return;
        }

        if (!isValidPassword(password)) {
            showAlert("Validation Error", "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        try {
            User user = userService.login(email, password);
            if (user != null) {
                String token = JwtUtil.generateToken(user.getIdUser(), user.getEmail(), user.getRole());
                SessionManager.setSession(token);

                navigate(SessionManager.getRole());
            } else {
                showAlert("Login Failed", "Email ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Une erreur est survenue lors de la connexion.");
            System.err.println("Login Error: " + e.getMessage());
        }
    }

    // Email Validation
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    // Password Validation (at least 6 characters)
    private boolean isValidPassword(String password) {
        return password.length() >= 3;
    }

    private void navigate(String role) {
        System.out.println(role);
        String fxmlFile = switch (role) {
            case "CANDIDAT" -> "/DashboardCandidat.fxml";
            case "MANAGER" -> "/DashboardManager.fxml";
            case "RH" -> "/DashboardHR.fxml";
            case "EMPLOYE" -> "/DashboardEmploye.fxml";
            default -> {
                showAlert("Navigation Error", "Rôle inconnu: " + role);
                yield null;
            }
        };

        if (fxmlFile == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) mail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(role + " Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Loading Error", "Erreur de chargement de la page: " + e.getMessage());
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handlesignup(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUser.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription");
            stage.show();
        } catch (IOException e) {
            showAlert("Loading Error", "Erreur de chargement: " + e.getMessage());
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }

    @FXML
    public void forgotpassword(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forgotpassword.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mot de passe oublié");
            stage.show();
        } catch (IOException e) {
            showAlert("Loading Error", "Erreur de chargement: " + e.getMessage());
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }
}
