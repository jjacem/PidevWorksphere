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
import java.util.Optional;

public class LoginContoller {


    @FXML
    private TextField mail;
    @FXML
    private PasswordField mdp;

    private final ServiceUser userService = new ServiceUser();
    @FXML
    private void handleLogin() throws SQLException {
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
if (checkbanned(email)){}
        else{

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
    }}

    /**
     * Checks if the user is banned and handles reclamation logic.
     *
     * @param email The email of the user to check.
     * @return true if the user is banned, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    private boolean checkbanned(String email) throws SQLException {
        if (userService.getbanned(email)) {
            // Create a dialog to inform the user they are banned
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Banned User");
            alert.setHeaderText("You are banned!");
            alert.setContentText("You have been banned from accessing the system.");

            // Apply light blue style to the dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
            dialogPane.getStyleClass().add("light-blue-dialog");

            // Check if the user has a reclamation
            if (!userService.hasReclamation(email)) {
                // Add a button to allow the user to add a reclamation
                ButtonType addReclamationButton = new ButtonType("Add Reclamation", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().add(addReclamationButton);

                // Show the dialog and wait for user input
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == addReclamationButton) {
                    // Open a text input dialog for the reclamation
                    TextInputDialog reclamationDialog = new TextInputDialog();
                    reclamationDialog.setTitle("Add Reclamation");
                    reclamationDialog.setHeaderText("Enter your reclamation");
                    reclamationDialog.setContentText("Reclamation:");

                    // Apply light blue style to the reclamation dialog
                    DialogPane reclamationDialogPane = reclamationDialog.getDialogPane();
                    reclamationDialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
                    reclamationDialogPane.getStyleClass().add("light-blue-dialog");

                    // Show the reclamation dialog and process the input
                    Optional<String> reclamationResult = reclamationDialog.showAndWait();
                    reclamationResult.ifPresent(reclam -> {
                        try {
                            userService.addReclamationByEmail(email, reclam);
                            showSuccessMessage("Reclamation added successfully!");
                        } catch (SQLException e) {
                            showErrorMessage("Failed to add reclamation: " + e.getMessage());
                        }
                    });
                }
            } else {
                // If the user already has a reclamation, just show the banned message
                alert.showAndWait();
            }

            // Return true because the user is banned
            return true;
        }

        // Return false because the user is not banned
        return false;
    }

    private void showSuccessMessage(String message) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        successAlert.setContentText(message);
        successAlert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
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
            System.out.println("Google login email: " + email);
            if(checkbanned(email)){}

else {
            User user = userService.findbyid(userService.findidbyemail(email));

            if (user != null) {
                System.out.println("User exists: " + email);
                String token = JwtUtil.generateToken(user.getIdUser(), user.getEmail(), user.getRole());
                SessionManager.setSession(token);
                navigate(user.getRole().name());
            } else {
                System.out.println("User does not exist. Redirecting to sign-up.");
                handlesignup(event);
            }}
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