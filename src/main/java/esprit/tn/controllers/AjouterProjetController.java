package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.fxml.FXML;
<<<<<<< Updated upstream
=======
import javafx.application.Platform;
>>>>>>> Stashed changes
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.sql.SQLException;
import java.util.Date;

public class AjouterProjetController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker dateCreationPicker;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private ComboBox<Equipe> equipeComboBox;

    private ServiceProjet serviceProjet;

    public AjouterProjetController() {
        serviceProjet = new ServiceProjet();
    }

    @FXML
    public void initialize() {
        // hne initialisation te3 les etats
        etatComboBox.getItems().addAll("Terminé", "Annulé", "EnCours");

        // chargement te3 les equipes mel base
        try {
            List<Equipe> equipes = serviceProjet.getEquipes();
            equipeComboBox.getItems().addAll(equipes);

            // hne personnalisation te3 affichage te3 equipe
            equipeComboBox.setCellFactory(param -> new ListCell<Equipe>() {
                @Override
                protected void updateItem(Equipe equipe, boolean empty) {
                    super.updateItem(equipe, empty);
                    if (empty || equipe == null) {
                        setText(null);
                    } else {
                        setText(equipe.getNomEquipe());
                    }
                }
            });

            // Personnaliser l'affichage de l'élément sélectionné dans la ComboBox
            equipeComboBox.setButtonCell(new ListCell<Equipe>() {
                @Override
                protected void updateItem(Equipe equipe, boolean empty) {
                    super.updateItem(equipe, empty);
                    if (empty || equipe == null) {
                        setText(null);
                    } else {
                        setText(equipe.getNomEquipe());
                    }
                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setMaximized(true);
        });
    }


    @FXML
    private void AjouterProjet(ActionEvent event) {
        try {

            String nom = nomField.getText();
            String description = descriptionField.getText();
            LocalDate dateCreationLocal = dateCreationPicker.getValue();
            LocalDate deadlineLocal = deadlinePicker.getValue();
            String etat = etatComboBox.getValue();
            Equipe equipe = equipeComboBox.getValue();

            if (nom.isEmpty() || description.isEmpty() || dateCreationLocal == null || deadlineLocal == null || etat == null || equipe == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs manquants");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez remplir tous les champs.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            // Convertir LocalDate en java.sql.Date
            Date dateCreation = java.sql.Date.valueOf(dateCreationLocal);
            Date deadline = java.sql.Date.valueOf(deadlineLocal);

            // hne verif te3 date
            if (dateCreation.after(deadline)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur de date");
                alert.setHeaderText(null);
                alert.setContentText("La date de création ne peut pas être postérieure à la deadline.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            // hne verif si fama projet b nafs esm
            if (serviceProjet.projetExiste(nom)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nom de projet existant");
                alert.setHeaderText(null);
                alert.setContentText("Un projet avec ce nom existe déjà. Veuillez choisir un autre nom.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            // hne creation te3 projet
            Projet projet = new Projet();
            projet.setNom(nom);
            projet.setDescription(description);
            projet.setDatecréation(dateCreation);
            projet.setDeadline(deadline);
            projet.setEtat(EtatProjet.valueOf(etat));
            projet.setEquipe(equipe);

            // Ajout te3 projet fi bdd
            serviceProjet.ajouterProjet(projet);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Projet ajouté avec succès !");
            applyAlertStyle(successAlert);
            successAlert.showAndWait();

            Parent root = FXMLLoader.load(getClass().getResource("/AfficherProjet.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Projets");

        } catch (SQLException | IOException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void Retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherProjet.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}