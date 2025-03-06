package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ModifierCompteController {
    @FXML
    private Button savebutton;
    @FXML
    private TextField nom, prenom, email, adresse;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Button registerFaceButton;

    private String imagePath = "";
    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private int userId;

    public void initData(int userId) {
        this.userId = userId;
    }

    private boolean b = false;

    public void modparadmin(Boolean k) {
        this.b = k;
    }

    @FXML
    public void initialize() {
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);
        ServiceUser serviceUser = new ServiceUser();

        try {
            User u = b ? serviceUser.findbyid(userId) : serviceUser.findbyid(SessionManager.extractuserfromsession().getIdUser());
            if (u != null) {
                nom.setText(u.getNom());
                prenom.setText(u.getPrenom());
                email.setText(u.getEmail());
                adresse.setText(u.getAdresse());
                sexe.setValue(u.getSexe());
                imagePath = u.getImageProfil();
                imagePreview.setImage(new Image(new File(imagePath).toURI().toString()));
            }

            // Wait for the scene to be set before running animation
            savebutton.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    FadeTransition fade = new FadeTransition(Duration.millis(1000), newValue.getRoot());
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.play();
                }
            });

            // Real-time validation
            email.textProperty().addListener((obs, old, newVal) -> {
                if (!emailPattern.matcher(newVal).matches() && !newVal.isEmpty()) {
                    email.setStyle(email.getStyle() + "-fx-border-color: #EF4444;");
                } else {
                    email.setStyle(email.getStyle() + "-fx-border-color: #D1D5DB;");
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user data.");
        }
    }

    @FXML
    public void saveChanges() {
        ServiceUser serviceUser = new ServiceUser();

        if (!validateInputs()) {
            return;
        }

        try {
            User modifiedUser = SessionManager.extractuserfromsession();
            modifiedUser.setIdUser(b ? userId : SessionManager.extractuserfromsession().getIdUser());
            modifiedUser.setNom(nom.getText());
            modifiedUser.setPrenom(prenom.getText());
            modifiedUser.setEmail(email.getText());
            modifiedUser.setAdresse(adresse.getText());
            modifiedUser.setSexe(sexe.getValue());
            modifiedUser.setImageProfil(imagePath);

            serviceUser.modifier(modifiedUser);
            showAlert("Success", "Account updated successfully!");
            refreshWindow();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account.");
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = "htdocs/images/" + fileName;
                imagePreview.setImage(new Image(destinationFile.toURI().toString()));

            } catch (Exception e) {
                showAlert("File Error", "Failed to upload image.");
            }
        }
    }

    private void refreshWindow() {
        try {
            Stage stage = (Stage) savebutton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCompte.fxml"));
            Scene newScene = new Scene(loader.load());
            ModifierCompteController controller = loader.getController();
            controller.initData(b ? userId : SessionManager.extractuserfromsession().getIdUser());
            controller.modparadmin(b);
            stage.setScene(newScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to refresh the window.");
        }
    }

    private boolean validateInputs() {
        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                adresse.getText().isEmpty() || sexe.getValue() == null) {
            showAlert("Input Error", "Please fill in all required fields.");
            return false;
        }

        if (!emailPattern.matcher(email.getText()).matches()) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-background-radius: 10;");
        alert.showAndWait();
    }

    @FXML
    private void handleRegisterFaceButton() {
        openRegisterFaceWindow();
    }

    private void openRegisterFaceWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/faceregister.fxml"));
            Parent root = loader.load();
            FaceRegisterController registerFaceController = loader.getController();
            registerFaceController.setting(SessionManager.extractuserfromsession().getEmail());
            Stage stage = new Stage();
            stage.setTitle("Register Face");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}