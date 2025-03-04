package esprit.tn.controllers;

import esprit.tn.entities.Reponse;
import esprit.tn.services.ServiceReponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Arrays;

public class ModifierReponseController {

    @FXML
    private TextArea txtMessage;

    @FXML
    private ChoiceBox<String> choiceStatus;

    @FXML
    private Button btnModifier;

    private Reponse reponse;
    private final ServiceReponse serviceReponse = new ServiceReponse();

    /**
     * Receives the response to modify.
     */
    public void setReponse(Reponse reponse) {
        this.reponse = reponse;

        // Populate fields with existing values
        txtMessage.setText(reponse.getMessage());
        choiceStatus.setValue(reponse.getStatus());
    }

    @FXML
    public void initialize() {
        // Initialize status options
        choiceStatus.getItems().addAll(Arrays.asList("En attente", "Résolu", "En cours"));

        // Add validation listener
        txtMessage.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        choiceStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFields());

        validateFields(); // Ensure button state updates correctly
    }

    private void validateFields() {
        String message = txtMessage.getText().trim();
        String status = choiceStatus.getValue();
        btnModifier.setDisable(message.isEmpty() || message.length() < 5 || status == null || status.isEmpty());
    }

    @FXML
    public void modifierReponse(ActionEvent event) {
        String message = txtMessage.getText().trim();
        String status = choiceStatus.getValue().toString();

        if (message.isEmpty() || message.length() < 5) {
            showAlert("Erreur", "Le message doit contenir au moins 5 caractères.");
            return;
        }
        if (status == null || status.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }

        try {
            // Update the response object
            reponse.setMessage(message);
            reponse.setStatus(status);

            // Save changes
            serviceReponse.modifier(reponse);
            showAlert("Succès", "Réponse modifiée avec succès.");

            // Close the window
            Stage stage = (Stage) btnModifier.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification de la réponse.");
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