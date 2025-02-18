package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.JwtUtil;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginContoller {


    @FXML
    private TextField mail;
    @FXML
    private PasswordField mdp;

    private final ServiceUser userService = new ServiceUser(); // Dependency Injection

    @FXML
    private void handleLogin() {
        String email = mail.getText();
        String password = mdp.getText();

        try {
            User user = userService.login(email, password);
            if (user != null) {
                String token = JwtUtil.generateToken(user.getIdUser(), user.getEmail(), user.getRole());
                SessionManager.setSession(token, user.getEmail(), user.getRole().name());

                navigate(SessionManager.getRole());
            } else {
                showAlert("Login Failed", "Invalid email or password.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while trying to log in.");
            System.err.println("Login Error: " + e.getMessage());
        }
    }

    private void navigate(String role) {
        System.out.println(role);
        String fxmlFile = switch (role) {

            case "CANDIDAT" -> "/CandidatDashboard.fxml";
            case "MANAGER" -> "/ManagerDashboard.fxml";
            case "RH" -> "/RhDashboard.fxml";
            case "EMPLOYE" -> "/EmployeDashboard.fxml";
            default -> {
                showAlert("Navigation Error", "Unknown role: " + role);
                yield null;
            }
        };

        if (fxmlFile == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardCandidat.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(role + " Dashboard");
            stage.show();
        } catch (IOException e) {
            showAlert("Loading Error", "Error loading FXML: " + e.getMessage());
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
}