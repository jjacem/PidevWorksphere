package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
<<<<<<< Updated upstream
=======
import javafx.application.Platform;
import java.io.File;
>>>>>>> Stashed changes
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ModifierProjetController {

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

   /* @FXML
    private ComboBox<Equipe> equipeComboBox;*/

    @FXML
    private ComboBox<String> equipeComboBox;

    private ServiceProjet serviceProjet;
    private Projet projetAModifier;

    public ModifierProjetController() {
        serviceProjet = new ServiceProjet();
    }

    public void setProjetAModifier(Projet projet) {
        this.projetAModifier = projet;

        // Convertir java.sql.Date en java.time.LocalDate
        LocalDate dateCreation = new java.sql.Date(projet.getDatecréation().getTime()).toLocalDate();
        LocalDate deadline = new java.sql.Date(projet.getDeadline().getTime()).toLocalDate();

        // Initialiser les champs avec les données du projet
        nomField.setText(projet.getNom());
        descriptionField.setText(projet.getDescription());
        dateCreationPicker.setValue(dateCreation);
        deadlinePicker.setValue(deadline);
        etatComboBox.setValue(projet.getEtat().name());

        // Sélectionner l'équipe associée au projet dans la ComboBox
        if (projet.getEquipe() != null) {
            equipeComboBox.setValue(projet.getEquipe().getNomEquipe());
        }
    }
    @FXML
    public void initialize() {
        // Initialiser les états du projet
        etatComboBox.getItems().addAll("Terminé", "Annulé", "EnCours");

        // Chargement des noms te3 equipe mel bdd
        try {
            List<Equipe> equipes = serviceProjet.getEquipes();
            for (Equipe equipe : equipes) {
                equipeComboBox.getItems().add(equipe.getNomEquipe());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setMaximized(true);
        });
    }

    @FXML
    private void ModifierProjet(ActionEvent event) {
        try {

            String nom = nomField.getText();
            String description = descriptionField.getText();
            LocalDate dateCreationLocal = dateCreationPicker.getValue();
            LocalDate deadlineLocal = deadlinePicker.getValue();
            String etat = etatComboBox.getValue();
            String nomEquipe = equipeComboBox.getValue();

            Equipe equipe = null;
            List<Equipe> equipes = serviceProjet.getEquipes();
            for (Equipe e : equipes) {
                if (e.getNomEquipe().equals(nomEquipe)) {
                    equipe = e;
                    break;
                }
            }


            if (nom.isEmpty() || description.isEmpty() || dateCreationLocal == null || deadlineLocal == null || etat == null || equipe == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champs manquants");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez remplir tous les champs.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }


            Date dateCreation = java.sql.Date.valueOf(dateCreationLocal);
            Date deadline = java.sql.Date.valueOf(deadlineLocal);


            if (dateCreation.after(deadline)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur de date");
                alert.setHeaderText(null);
                alert.setContentText("La date de création ne peut pas être postérieure à la deadline.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            if (serviceProjet.projetExiste(nom) && !nom.equals(projetAModifier.getNom())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nom de projet existant");
                alert.setHeaderText(null);
                alert.setContentText("Un projet avec ce nom existe déjà. Veuillez choisir un autre nom.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            projetAModifier.setNom(nom);
            projetAModifier.setDescription(description);
            projetAModifier.setDatecréation(dateCreation);
            projetAModifier.setDeadline(deadline);
            projetAModifier.setEtat(EtatProjet.valueOf(etat));
            projetAModifier.setEquipe(equipe);

            serviceProjet.modifierProjet(projetAModifier);


            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Projet modifié avec succès !");
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