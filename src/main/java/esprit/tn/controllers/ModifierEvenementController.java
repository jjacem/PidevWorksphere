/*package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.services.ServiceEvenement;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ModifierEvenementController {

    @FXML
    private TextField tfNomEvent;
    @FXML
    private TextField tfDescEvent;
    @FXML
    private DatePicker dpDateEvent;
    @FXML
    private TextField tfHeureEvent;

    @FXML

    private TextField tfLieuEvent;
    @FXML
    private TextField tfCapaciteEvent;
    @FXML
    private Button btnModifier;

    private Evenement evenement;
    private final ServiceEvenement serviceEvenement = new ServiceEvenement();

    public void initData(Evenement evenement) {
        this.evenement = evenement;
        tfNomEvent.setText(evenement.getNomEvent());
        tfDescEvent.setText(evenement.getDescEvent());
        dpDateEvent.setValue(evenement.getDateEvent().toLocalDate());
        tfHeureEvent.setText(evenement.getDateEvent().toLocalTime().toString());
        tfLieuEvent.setText(evenement.getLieuEvent());
        tfCapaciteEvent.setText(String.valueOf(evenement.getCapaciteEvent()));
    }

    @FXML
    public void initialize() {
        btnModifier.setOnAction(event -> modifierEvenement());
    }

    private void modifierEvenement() {
        try {
            evenement.setNomEvent(tfNomEvent.getText());
            evenement.setDescEvent(tfDescEvent.getText());
            evenement.setDateEvent(dpDateEvent.getValue().atStartOfDay());
            evenement.setDateEvent(dpDateEvent.getValue().atTime(LocalTime.parse(tfHeureEvent.getText())));
            evenement.setLieuEvent(tfLieuEvent.getText());
            evenement.setCapaciteEvent(Integer.parseInt(tfCapaciteEvent.getText()));

            serviceEvenement.modifier(evenement);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Modification réussie");
            alert.setContentText("L'événement a été mis à jour avec succès !");
            alert.showAndWait();

            ((Stage) btnModifier.getScene().getWindow()).close(); // Fermer la fenêtre

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la modification");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
*/
package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.services.ServiceEvenement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;

public class ModifierEvenementController {

    @FXML
    private TextField tfNomEvent;
    @FXML
    private TextField tfDescEvent;
    @FXML
    private DatePicker dpDateEvent;
    @FXML
    private TextField tfHeureEvent;
    @FXML
    private TextField tfLieuEvent;
    @FXML
    private TextField tfCapaciteEvent;
    @FXML
    private Button btnModifier;

    // Labels d'erreur
    @FXML private Label lblErrorNom;
    @FXML private Label lblErrorDesc;
    @FXML private Label lblErrorDate;
    @FXML private Label lblErrorHeure;
    @FXML private Label lblErrorLieu;
    @FXML private Label lblErrorCapacite;

    private Evenement evenement;
    private final ServiceEvenement serviceEvenement = new ServiceEvenement();

    public void initData(Evenement evenement) {
        this.evenement = evenement;
        tfNomEvent.setText(evenement.getNomEvent());
        tfDescEvent.setText(evenement.getDescEvent());
        dpDateEvent.setValue(evenement.getDateEvent().toLocalDate());
        tfHeureEvent.setText(evenement.getDateEvent().toLocalTime().toString());
        tfLieuEvent.setText(evenement.getLieuEvent());
        tfCapaciteEvent.setText(String.valueOf(evenement.getCapaciteEvent()));
    }

    @FXML
    public void initialize() {
        btnModifier.setOnAction(event -> modifierEvenement());
    }

    private void modifierEvenement() {
        // Masquer tous les labels d'erreur au début
        resetErrorLabels();

        // Vérifier la description
        if (tfDescEvent.getText().length() < 10) {
            lblErrorDesc.setText("La description doit contenir au moins 10 caractères.");
            lblErrorDesc.setVisible(true);
            return;
        }

        // Vérifier la date de l'événement
        if (dpDateEvent.getValue() == null || dpDateEvent.getValue().isBefore(LocalDate.now())) {
            lblErrorDate.setText("La date doit être dans le futur.");
            lblErrorDate.setVisible(true);
            return;
        }

        // Vérifier l'heure
        if (!isValidTime(tfHeureEvent.getText())) {
            lblErrorHeure.setText("L'heure doit être au format HH:mm:ss.");
            lblErrorHeure.setVisible(true);
            return;
        }

        // Vérifier la capacité
        int capacite;
        try {
            capacite = Integer.parseInt(tfCapaciteEvent.getText());
        } catch (NumberFormatException e) {
            lblErrorCapacite.setText("La capacité doit être un nombre entier.");
            lblErrorCapacite.setVisible(true);
            return;
        }

        // Mettre à jour l'objet evenement avec les nouvelles valeurs
        try {
            evenement.setNomEvent(tfNomEvent.getText());
            evenement.setDescEvent(tfDescEvent.getText());
            evenement.setDateEvent(dpDateEvent.getValue().atTime(LocalTime.parse(tfHeureEvent.getText())));
            evenement.setLieuEvent(tfLieuEvent.getText());
            evenement.setCapaciteEvent(capacite);

            serviceEvenement.modifier(evenement);

            // Afficher un message de succès et fermer la fenêtre
            showSuccessMessage();
            ((Stage) btnModifier.getScene().getWindow()).close();
        } catch (SQLException e) {
            lblErrorNom.setText("Erreur lors de la modification.");
            lblErrorNom.setVisible(true);
        }
    }

    // Méthode pour vérifier si l'heure est valide (format HH:mm:ss et heures/minutes valides)
    private boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59 || seconds < 0 || seconds > 59) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Méthode pour réinitialiser tous les labels d'erreur
    private void resetErrorLabels() {
        lblErrorNom.setVisible(false);
        lblErrorDesc.setVisible(false);
        lblErrorDate.setVisible(false);
        lblErrorHeure.setVisible(false);
        lblErrorLieu.setVisible(false);
        lblErrorCapacite.setVisible(false);
    }

    // Méthode pour afficher un message de succès
    private void showSuccessMessage() {
        lblErrorNom.setText("L'événement a été mis à jour avec succès !");
        lblErrorNom.setTextFill(javafx.scene.paint.Color.GREEN);
        lblErrorNom.setVisible(true);
    }
}
