package esprit.tn.controllers;

import esprit.tn.entities.Reclamation;
import esprit.tn.services.ServiceReclamation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AjouterReclamationController {

    @FXML
    private TextField titre;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<String> type;

    @FXML
    private Button ajouterButton;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();

    @FXML
    public void initialize() {
        System.out.println("AjouterReclamationController.initialize");
        List<String> types = Arrays.asList("Technique", "Administratif", "Autre");
        type.getItems().addAll(types);
    }

    @FXML
    private void ajouter(ActionEvent event) {

        if (titre.getText().isEmpty() || description.getText().isEmpty() || type.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }


        Reclamation reclamation = new Reclamation(
                "En attente",
                titre.getText(),
                description.getText(),
                type.getValue(),3
                ,
                13
        );

        try {

            serviceReclamation.ajouter(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation ajoutée avec succès !");
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible d'ajouter la réclamation.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        titre.clear();
        description.clear();
        type.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
