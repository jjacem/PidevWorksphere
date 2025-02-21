package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.entities.TypeEntretien;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class AjouterEntretineController {

    @FXML
    private ComboBox<String> cb_type_entretien;
    @FXML
    private TextField tf_titre;
    @FXML
    private DatePicker dp_date_entretien;
    @FXML
    private CheckBox cb_status;
    @FXML
    private TextField tf_description;
    @FXML
    private Button btnAjouter;
    @FXML
    private Spinner<Integer> sp_heure_entretien;

    private EntretienService entretienService = new EntretienService();

    @FXML
    public void initialize() {
        cb_type_entretien.getItems().addAll("EN_PRESENTIEL", "EN_VISIO");
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
        sp_heure_entretien.setValueFactory(valueFactory);
    }

    @FXML
    public void ajouterEntretien(ActionEvent actionEvent) throws SQLException {
        try {
            String titre = tf_titre.getText();
            String description = tf_description.getText();
            LocalDate date = dp_date_entretien.getValue();
            int heure = (int) sp_heure_entretien.getValue();
            String typeString = (String) cb_type_entretien.getValue();
            boolean status = cb_status.isSelected();

            String titrev = tf_titre.getText().trim();
            String descriptionv = tf_description.getText().trim();

            if (titre.isEmpty() || description.isEmpty() || date == null || typeString == null || typeString.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            if (titre.length() < 3) {
                showAlert("Erreur", "Le titre doit contenir au moins 3 caractères.");
                return;
            }

            if (description.length() < 5) {
                showAlert("Erreur", "La description doit contenir au moins 5 caractères.");
                return;
            }

            TypeEntretien type;
            try {
                type = TypeEntretien.valueOf(typeString);
            } catch (IllegalArgumentException e) {
                showAlert("Erreur", "Type d'entretien invalide.");
                return;
            }

            Date dateEntretien = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Time heureEntretien = Time.valueOf(LocalTime.of(heure, 0));

            Entretien entretien = new Entretien(titre, description, dateEntretien, heureEntretien, type, status);
            entretienService.ajouter(entretien);

            clearFields();
            fermerFenetre(actionEvent);
            ouvrirAffichageEntretien();

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            showAlert("Erreur SQL", "Erreur lors de l'ajout : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            showAlert("Erreur Inattendue", "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    private void clearFields() {
        tf_titre.clear();
        tf_description.clear();
        dp_date_entretien.setValue(null);
        cb_type_entretien.setValue(null);
        cb_status.setSelected(false);
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void fermerFenetre(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de fermer la fenêtre.");
        }
    }

    private void ouvrirAffichageEntretien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();

            AffichageEntretineController controller = loader.getController();
            controller.initialize();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Entretiens");
            stage.show();

        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'affichage : " + e.getMessage());
        }
    }
}
