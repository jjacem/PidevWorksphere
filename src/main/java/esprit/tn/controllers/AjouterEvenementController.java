
package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AjouterEvenementController {

    @FXML
    private TextField nomEventTextField;
    @FXML
    private TextArea descEventTextArea;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeTextField;
    @FXML
    private TextField lieuEventTextField;
    @FXML
    private TextField capaciteEventTextField;
    @FXML
    private Button ajouterBtn;

    // Labels d'erreur
    @FXML
    private Label nomEventErrorLabel;
    @FXML
    private Label descEventErrorLabel;
    @FXML
    private Label dateEventErrorLabel;
    @FXML
    private Label timeEventErrorLabel;
    @FXML
    private Label lieuEventErrorLabel;
    @FXML
    private Label capaciteEventErrorLabel;

    @FXML
    public void ajouterEvenement(ActionEvent event) {
        // Réinitialiser les erreurs avant de commencer
        resetErrorMessages();

        try {
            // Récupérer les données saisies
            String nomEvent = nomEventTextField.getText();
            if (nomEvent.isEmpty()) {
                nomEventErrorLabel.setText("Le nom ne peut pas être vide.");
                nomEventErrorLabel.setVisible(true);
                return;
            }

            String descEvent = descEventTextArea.getText();
            if (descEvent.isEmpty()) {
                descEventErrorLabel.setText("La description ne peut pas être vide.");
                descEventErrorLabel.setVisible(true);
                return;
            }

            // Validation de la date : la date doit être supérieure à aujourd'hui
            LocalDate date = datePicker.getValue();
            if (date == null) {
                dateEventErrorLabel.setText("Veuillez sélectionner une date.");
                dateEventErrorLabel.setVisible(true);
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                dateEventErrorLabel.setText("La date doit être supérieure à aujourd'hui.");
                dateEventErrorLabel.setVisible(true);
                return;
            }

            // Validation de l'heure : le format doit être HH:mm:ss et les heures, minutes et secondes valides
            String timeText = timeTextField.getText();
            LocalTime time;
            try {
                time = LocalTime.parse(timeText, DateTimeFormatter.ofPattern("HH:mm:ss"));
                if (time.getHour() < 0 || time.getHour() > 23 || time.getMinute() < 0 || time.getMinute() > 59 || time.getSecond() < 0 || time.getSecond() > 59) {
                    throw new Exception();
                }
            } catch (Exception e) {
                timeEventErrorLabel.setText("L'heure doit être au format HH:mm:ss et dans les valeurs valides.");
                timeEventErrorLabel.setVisible(true);
                return;
            }

            String lieuEvent = lieuEventTextField.getText();
            if (lieuEvent.isEmpty()) {
                lieuEventErrorLabel.setText("Le lieu ne peut pas être vide.");
                lieuEventErrorLabel.setVisible(true);
                return;
            }

            int capaciteEvent;
            try {
                capaciteEvent = Integer.parseInt(capaciteEventTextField.getText());
                if (capaciteEvent <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                capaciteEventErrorLabel.setText("La capacité doit être un entier positif.");
                capaciteEventErrorLabel.setVisible(true);
                return;
            }

            // Créer l'événement


            User u =SessionManager.extractuserfromsession();
            Evenement evenement = new Evenement(nomEvent, descEvent, LocalDateTime.of(date, time), lieuEvent, capaciteEvent,u.getIdUser() ); // 1 est l'ID de l'utilisateur

            // Appeler le service pour ajouter l'événement
            ServiceEvenement serviceEvenement = new ServiceEvenement();
            serviceEvenement.ajouter(evenement);

            // Afficher un message de succès
            showAlert(AlertType.INFORMATION, "Succès", "Événement ajouté avec succès !");
// Fermer la fenêtre de popup ajout
           // ((Stage) ajouterBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    private void resetErrorMessages() {
        // Réinitialiser les messages d'erreur
        nomEventErrorLabel.setVisible(false);
        descEventErrorLabel.setVisible(false);
        dateEventErrorLabel.setVisible(false);
        timeEventErrorLabel.setVisible(false);
        lieuEventErrorLabel.setVisible(false);
        capaciteEventErrorLabel.setVisible(false);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

