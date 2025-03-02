package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AjouterUser {
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private TextField email;
    @FXML
    private PasswordField mdp;
    @FXML
    private TextField adresse;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu;
    @FXML
    private ImageView imagePreview;

    private String imagePath = ""; // Store uploaded image path

    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Pattern numericPattern = Pattern.compile("\\d+(\\.\\d+)?");

    @FXML
    public void initialize() {
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);
    }

    @FXML
    public User ajoutercandidat(ActionEvent actionEvent) {
        ServiceUser serviceUser = new ServiceUser();

        if (!validateInputs()) {
            return null;
        }

        try {
            double salaire = Double.parseDouble(salaireAttendu.getText());
            User candidat = new User(
                    nom.getText(),
                    prenom.getText(),
                    email.getText(),
                    mdp.getText(),
                    adresse.getText(),
                    sexe.getValue(),
                    imagePath, // Store uploaded image path
                    salaire
            );

            serviceUser.ajouter(candidat);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Votre compte a été créé!");
            alert.showAndWait();
            redirectToLogin(actionEvent);

            return candidat;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add user: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Define upload directory (htdocs/images/)
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Generate unique filename
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);

                // Copy file to the destination
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Store relative path in variable
                imagePath = "htdocs/images/" + fileName;

                // Display image in ImageView
                imagePreview.setImage(new Image(destinationFile.toURI().toString()));

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Failed to upload image.");
            }
        }
    }

    private void redirectToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login page.");
        }
    }

    private boolean validateInputs() {
        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                mdp.getText().isEmpty() || adresse.getText().isEmpty() || sexe.getValue() == null ||
                salaireAttendu.getText().isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
            return false;
        }

        if (!emailPattern.matcher(email.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return false;
        }

        if (!numericPattern.matcher(salaireAttendu.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Salary", "Please enter a valid numerical salary.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
