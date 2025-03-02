package esprit.tn.controllers;

import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Changermdp {


    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button btnChanger;
    @FXML
    private Button btnRetour;

    private final ServiceUser serviceUser = new ServiceUser();

    @FXML
    public void initialize() {
        btnChanger.setOnAction(event -> changePassword());
    }

    private void changePassword() {
        String newPass = newPasswordField.getText().trim();
        String confirmPass = confirmPasswordField.getText().trim();

        if ( newPass.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            errorLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        if (newPass.length() < 6) {
            errorLabel.setText("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        try {
            int userId = SessionManager.extractuserfromsession().getIdUser();
            boolean success = serviceUser.changepassword(userId, newPass );

            if (success) {
                showAlert("Succès", "Mot de passe modifié avec succès.");
                goBack();
            } else {
                errorLabel.setText("Ancien mot de passe incorrect.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors du changement de mot de passe.");
            e.printStackTrace();
        }
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PreviousWindow.fxml")); // Change to actual FXML
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
