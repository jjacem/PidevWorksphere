package esprit.tn.controllers;

import esprit.tn.entities.Reclamation;
import esprit.tn.services.ServiceReclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ModifierReclamationController {

    @FXML
    private TextField titre;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<String> type;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private Reclamation reclamation;

    @FXML
    public void initialize() {
        List<String> types = Arrays.asList("Technique", "Administratif", "Autre");
        type.getItems().addAll(types);
        loadReclamation(24);
    }

    public void loadReclamation(int id) {
        try {
            reclamation = serviceReclamation.getReclamationById(id);
            if (reclamation != null) {
                titre.setText(reclamation.getTitre());
                description.setText(reclamation.getDescription());
                type.setValue(reclamation.getType());
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Réclamation introuvable.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de récupérer la réclamation.");
            e.printStackTrace();
        }
    }

    @FXML
    private void modifier(ActionEvent event) {
        if (reclamation == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune réclamation sélectionnée.");
            return;
        }

        if (titre.getText().isEmpty() || description.getText().isEmpty() || type.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        reclamation.setTitre(titre.getText());
        reclamation.setDescription(description.getText());
        reclamation.setType(type.getValue());

        try {
            serviceReclamation.modifier(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation modifiée avec succès !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de modifier la réclamation.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setReclamationId(int idReclamation) {
        System.out.println("works");
    }
}
