package esprit.tn.controllers;

import esprit.tn.entities.Reponse;
import esprit.tn.services.ServiceReponse;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class ModifierReponseController {
    @FXML
    private TextField messageField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Button modifyButton;

    private int idReponse;
    private final ServiceReponse serviceReponse = new ServiceReponse();

    public void setReponse(Reponse reponse) {
        this.idReponse = reponse.getId_reponse();
        messageField.setText(reponse.getMessage());
        statusComboBox.setValue(reponse.getStatus());
    }

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll(Arrays.asList("Pending", "Approved", "Rejected"));
        modifyButton.setDisable(true);

        messageField.textProperty().addListener((obs, oldText, newText) -> validateFields());
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateFields());
    }

    private void validateFields() {
        boolean isValid = !messageField.getText().trim().isEmpty() && statusComboBox.getValue() != null;
        modifyButton.setDisable(!isValid);
    }

    @FXML
    private void modifyResponse() {
        String message = messageField.getText().trim();
        String status = statusComboBox.getValue();

        if (message.isEmpty()) {
            showAlert("Validation Error", "Le message ne peut pas être vide.");
            return;
        }

        try {
            Reponse reponse = new Reponse(message, SessionManager.extractuserfromsession().getIdUser(), 0, status);
            reponse.setId_reponse(idReponse);
            serviceReponse.modifier(reponse);
            showAlert("Succès", "Réponse modifiée avec succès!");
        } catch (SQLException e) {
            showAlert("Erreur", "Problème lors de la modification.");
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

    @FXML
    public void handleModify(ActionEvent actionEvent) {
        String message = messageField.getText().trim();
        String status = statusComboBox.getValue();

        // Validate inputs
        if (message.isEmpty()) {
            showAlert("Validation Error", "Le message ne peut pas être vide.");
            return;
        }

        try {
            // Retrieve current user ID from session
            int userId = SessionManager.extractuserfromsession().getIdUser();

            // Create and update response
            Reponse reponse = new Reponse(message, userId, 0, status);
            reponse.setId_reponse(idReponse);
            serviceReponse.modifier(reponse);

            // Show success message
            showAlert("Succès", "Réponse modifiée avec succès!");

            // Close current window after modification
            Stage stage = (Stage) modifyButton.getScene().getWindow();
            stage.close();

            // Optional: Refresh the previous scene if needed
        } catch (SQLException e) {
            showAlert("Erreur", "Une erreur s'est produite lors de la modification.");
            e.printStackTrace();
        }
    }

}
