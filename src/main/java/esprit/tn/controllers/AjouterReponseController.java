package esprit.tn.controllers;

import esprit.tn.entities.Reponse;
import esprit.tn.services.ServiceReponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AjouterReponseController {

    @FXML
    private TextArea txtMessage;

    @FXML
    private Button btnAjouter;

    private int id_user;
    private int id_reclamation;
    private final ServiceReponse serviceReponse = new ServiceReponse();

    public void setIds(int id_user, int id_reclamation) {
        this.id_user = id_user;
        this.id_reclamation = id_reclamation;
    }

    @FXML
    public void initialize() {
        // Disable button initially
        btnAjouter.setDisable(true);

        // Add validation listener
        txtMessage.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void validateFields() {
        String message = txtMessage.getText().trim();
        btnAjouter.setDisable(message.isEmpty() || message.length() < 5);
    }

    /**
     * This method is invoked when the "Ajouter Réponse" button is clicked.
     */
    @FXML
    public void ajouterReponse(ActionEvent event) {
        String message = txtMessage.getText().trim();

        if (message.isEmpty()) {
            showAlert("Erreur", "Le message ne peut pas être vide.");
            return;
        }
        if (message.length() < 5) {
            showAlert("Erreur", "Le message doit contenir au moins 5 caractères.");
            return;
        }

        try {
            Reponse reponse = new Reponse(message, id_user, id_reclamation, "En attente");
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

    /**
     * Utility method to show an alert dialog.
     *
     * @param title   the title of the alert
     * @param message the content text of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
