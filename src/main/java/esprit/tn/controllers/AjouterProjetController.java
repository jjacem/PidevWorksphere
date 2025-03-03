package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    @FXML
    private ImageView imagePreview;

    private String imagePath = "";

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

            // Si aucune image n'est sélectionnée, utiliser une image par défaut
            if (imagePath.isEmpty()) {
                imagePath = "images/profil.png";
            }

            // Créer un nouveau projet
            Projet projet = new Projet();
            projet.setNom(nom);
            projet.setDescription(description);
            projet.setDatecréation(dateCreation);
            projet.setDeadline(deadline);
            projet.setEtat(EtatProjet.valueOf(etat));
            projet.setEquipe(equipe);
            projet.setImageProjet(imagePath); // Ajouter l'image du projet

            // Ajouter le projet dans la base de données
            serviceProjet.ajouterProjet(projet);

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Projet ajouté avec succès !");
            applyAlertStyle(successAlert);
            successAlert.showAndWait();

            /*// Rediriger vers la page d'affichage des projets
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardManager.fxml"));
            Parent root = loader.load();
            DashboardManager dashboardController = loader.getController();
            dashboardController.loadPage("/AfficherProjet.fxml");*/

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
            //stage.setScene(new Scene(root));
            //stage.setTitle("Liste Projet");

        } catch (SQLException  e) {
            e.printStackTrace();
        }
    }


    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }


    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Définir le répertoire de destination (htdocs/images/)
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Générer un nom de fichier unique
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);

                // Copier le fichier vers la destination
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin relatif dans la variable
                imagePath = "img/" + fileName;

                // Afficher l'image dans l'ImageView
                imagePreview.setImage(new Image(destinationFile.toURI().toString()));

            } catch (Exception e) {
                System.out.println("erreur lors de l'ajout de la photo");
            }
        }
    }
}