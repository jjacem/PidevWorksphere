package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.fxml.FXML;
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
    private ComboBox<Equipe> equipeComboBox; // Utiliser ComboBox<Equipe> pour stocker des objets Equipe

    private ServiceProjet serviceProjet;

    public AjouterProjetController() {
        serviceProjet = new ServiceProjet();
    }

    @FXML
    public void initialize() {
        // Initialiser les états du projet
        etatComboBox.getItems().addAll("Terminé", "Annulé", "EnCours");

        // Charger les équipes depuis la base de données
        try {
            List<Equipe> equipes = serviceProjet.getEquipes();
            equipeComboBox.getItems().addAll(equipes);

            // Personnaliser l'affichage des équipes dans la ComboBox
            equipeComboBox.setCellFactory(param -> new ListCell<Equipe>() {
                @Override
                protected void updateItem(Equipe equipe, boolean empty) {
                    super.updateItem(equipe, empty);
                    if (empty || equipe == null) {
                        setText(null);
                    } else {
                        setText(equipe.getNomEquipe()); // Afficher uniquement le nom de l'équipe
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
                        setText(equipe.getNomEquipe()); // Afficher uniquement le nom de l'équipe
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*@FXML
    private void AjouterProjet(ActionEvent event) {
        try {
            // Récupérer les données du formulaire
            String nom = nomField.getText();
            String description = descriptionField.getText();
            Date dateCreation = java.sql.Date.valueOf(dateCreationPicker.getValue());
            Date deadline = java.sql.Date.valueOf(deadlinePicker.getValue());
            EtatProjet etat = EtatProjet.valueOf(etatComboBox.getValue());

            // Récupérer l'équipe sélectionnée
            Equipe equipe = equipeComboBox.getValue();

            // Créer un objet Projet
            Projet projet = new Projet();
            projet.setNom(nom);
            projet.setDescription(description);
            projet.setDatecréation(dateCreation);
            projet.setDeadline(deadline);
            projet.setEtat(etat);
            projet.setEquipe(equipe);
            // Ajouter le projet à la base de données
            serviceProjet.ajouterProjet(projet);

            // Retourner à la page AfficherProjet.fxml
                // Charger la page AfficherProjet.fxml
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/AfficherProjet.fxml"));
                // Récupérer la scène actuelle
                Stage stage = (Stage) nomField.getScene().getWindow();

                // Remplacer la scène actuelle par la nouvelle scène
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des Projets");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    @FXML
    private void AjouterProjet(ActionEvent event) {
        try {
            // Récupérer les données du formulaire
            String nom = nomField.getText();
            String description = descriptionField.getText();
            LocalDate dateCreationLocal = dateCreationPicker.getValue();
            LocalDate deadlineLocal = deadlinePicker.getValue();
            String etat = etatComboBox.getValue();
            Equipe equipe = equipeComboBox.getValue();

            // Vérifier si tous les champs sont remplis
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

            // Vérifier si la date de création est postérieure à la deadline
            if (dateCreation.after(deadline)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur de date");
                alert.setHeaderText(null);
                alert.setContentText("La date de création ne peut pas être postérieure à la deadline.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            // Vérifier si un projet avec le même nom existe déjà
            if (serviceProjet.projetExiste(nom)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nom de projet existant");
                alert.setHeaderText(null);
                alert.setContentText("Un projet avec ce nom existe déjà. Veuillez choisir un autre nom.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            // Créer un objet Projet
            Projet projet = new Projet();
            projet.setNom(nom);
            projet.setDescription(description);
            projet.setDatecréation(dateCreation);
            projet.setDeadline(deadline);
            projet.setEtat(EtatProjet.valueOf(etat));
            projet.setEquipe(equipe);

            // Ajouter le projet à la base de données
            serviceProjet.ajouterProjet(projet);

            // Afficher une alerte de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Projet ajouté avec succès !");
            applyAlertStyle(successAlert);
            successAlert.showAndWait();

            // Retourner à la page AfficherProjet.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherProjet.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Projets");

        } catch (SQLException | IOException e) {
            e.printStackTrace();

            // Afficher une alerte d'erreur
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Erreur lors de l'ajout du projet");
            errorAlert.setContentText("Une erreur s'est produite lors de l'ajout du projet : " + e.getMessage());
            applyAlertStyle(errorAlert);
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void Retour(ActionEvent event) {
        try {
            // Charger la page précédente
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