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

    @FXML
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
}