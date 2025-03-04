package esprit.tn.controllers;

import esprit.tn.entities.Reponse;
import esprit.tn.services.ServiceReponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Arrays;

public class AjouterReponseController {

    @FXML
    private TextArea txtMessage;

    @FXML
    private Button btnAjouter;

    @FXML
    private ChoiceBox<String> choiceStatus;

    private int id_user;
    private int id_reclamation;
    private final ServiceReponse serviceReponse = new ServiceReponse();

    public void setIds(int id_user, int id_reclamation) {
        this.id_user = id_user;
        this.id_reclamation = id_reclamation;
    }

    @FXML
    public void initialize() {
        // Initialize status options
        choiceStatus.getItems().addAll(Arrays.asList("En attente", "Résolu", "En cours"));
        choiceStatus.setValue("En attente"); // Default selection

        // Disable button initially
        btnAjouter.setDisable(true);

        // Add validation listeners
        txtMessage.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        choiceStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void validateFields() {
        String message = txtMessage.getText().trim();
        String status = choiceStatus.getValue();
        btnAjouter.setDisable(message.isEmpty() || message.length() < 5 || status == null || status.isEmpty());
    }

    @FXML
    public void ajouterReponse(ActionEvent event) {
        String message = txtMessage.getText().trim();
        String status = choiceStatus.getValue();

        if (message.isEmpty() || message.length() < 5) {
            showAlert("Erreur", "Le message doit contenir au moins 5 caractères.");
            return;
        }
        if (status == null || status.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }

        try {
            Reponse reponse = new Reponse(message, id_user, id_reclamation, status);
            serviceReponse.ajouter(reponse);
            showAlert("Succès", "Réponse ajoutée avec succès.");

            // Close the window after successful addition
            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la réponse.");
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