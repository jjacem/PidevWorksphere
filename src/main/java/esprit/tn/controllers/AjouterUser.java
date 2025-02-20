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
import javafx.stage.Stage;

import java.io.IOException;
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
    private TextField mdp;
    @FXML
    private TextField adresse;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu;
    @FXML
    private TextField imageProfil;


    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Pattern numericPattern = Pattern.compile("\\d+(\\.\\d+)?");
    private final Pattern imageUrlPattern = Pattern.compile("^https://.*$");
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
                    imageProfil.getText(),
                    salaire
            );

            serviceUser.ajouter(candidat);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("User added successfully!");
            alert.showAndWait();
            redirectToLogin(actionEvent);

            return candidat;


        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add user: " + e.getMessage());
            return null;
        }
    }

    private void redirectToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
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
                salaireAttendu.getText().isEmpty() || imageProfil.getText().isEmpty()) {
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

        if (!imageUrlPattern.matcher(imageProfil.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Image URL", "Image URL must start with 'https://' and end with a valid image format (.jpg, .png, etc.).");
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
