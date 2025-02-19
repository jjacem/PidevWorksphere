package esprit.tn.controllers;

import esprit.tn.entities.Reponse;
import esprit.tn.services.ServiceReponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AjouterReponseController {

    @FXML
    private TextArea txtMessage;

    private int id_user;
    private int id_reclamation;

    private final ServiceReponse serviceReponse = new ServiceReponse();

    public void setIds(int id_user, int id_reclamation) {
        this.id_user = id_user;
        this.id_reclamation = id_reclamation;
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

        try {
            // You can change the default status if needed.
            Reponse reponse = new Reponse(message, id_user, id_reclamation, "en attente");
            serviceReponse.ajouter(reponse);
            showAlert("Succès", "Réponse ajoutée avec succès.");

            // Close the window after successful addition
            Stage stage = (Stage) txtMessage.getScene().getWindow();
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
