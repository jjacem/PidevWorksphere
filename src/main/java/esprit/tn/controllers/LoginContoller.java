package esprit.tn.controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.model.Userinfo;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.GoogleAuthUtil;
import esprit.tn.utils.JwtUtil;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 3;
    }

    public void navigate(String role) {
        System.out.println(role);
        String fxmlFile = switch (role) {
            case "CANDIDAT" -> "/DashboardCandidat.fxml";
            case "MANAGER" -> "/DashboardManager.fxml";
            case "RH" -> "/DashboardHR.fxml";
            case "EMPLOYE" -> "/DashboardEmploye.fxml";
            default -> {
                showAlert("Navigation Error", "Unknown role: " + role);
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
    @FXML
    public void handlesignup(ActionEvent actionEvent)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterUser.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            showAlert("Loading Error", "Error loading FXML: " + e.getMessage());
            System.err.println("Error loading FXML: " + e.getMessage());
        }

    }
    @FXML
    public void forgotpassword(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forgotpassword.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Forgot Password");
            stage.show();
        } catch (IOException e) {
            showAlert("Loading Error", "Error loading FXML: " + e.getMessage());
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }
    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        try {
            Credential credential = GoogleAuthUtil.authenticate();
            Userinfo userInfo = GoogleAuthUtil.getUserInfo(credential);

            String email = userInfo.getEmail();
            String name = userInfo.getName();
            System.out.println("Google login email: " + email); // Debug log

            User user = userService.findbyid(userService.findidbyemail(email));

            if (user != null) {
                System.out.println("User exists: " + email);
                String token = JwtUtil.generateToken(user.getIdUser(), user.getEmail(), user.getRole());
                SessionManager.setSession(token);
                navigate(user.getRole().name());
            } else {
                System.out.println("User does not exist. Redirecting to sign-up.");
                handlesignup(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to authenticate with Google.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private Button faceIdLoginBtn;
    @FXML
    private void handleFaceIdLogin(ActionEvent event) {
        try {
            // Load the FXML file for the Face ID authentication view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/faceauth.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) from the event source
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene to the current stage
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.setTitle("Authentification Face ID"); // Optional: Set a new title
            currentStage.show(); // Show the updated scene
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeWindow() {
        Stage stage = (Stage) mail.getScene().getWindow();
        stage.close();
    }


    private void performFaceIdAuthentication() {
        // Face ID logic: this should be replaced with actual biometric authentication
        System.out.println("Face ID authentication triggered...");
    }


}