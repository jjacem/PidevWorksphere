package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    @FXML
    private ImageView imagePreview;

    @FXML
    private StackPane imageContainer;

    @FXML
    private ComboBox<String> equipeComboBox;

    private ServiceProjet serviceProjet;
    private Projet projetAModifier;

    private String imagePath = "";

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

        if (projet.getImageProjet() != null && !projet.getImageProjet().isEmpty()) {
            String correctPath = "C:/xampp/htdocs/img/" + new File(projet.getImageProjet()).getName();
            File imageFile = new File(correctPath);
            if (imageFile.exists() && imageFile.isFile()) {
                imagePreview.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
            }
            imagePath = projet.getImageProjet(); // Conserver le chemin de l'image
        } else {
            imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
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

            // Mettre à jour les informations du projet
            projetAModifier.setNom(nom);
            projetAModifier.setDescription(description);
            projetAModifier.setDatecréation(dateCreation);
            projetAModifier.setDeadline(deadline);
            projetAModifier.setEtat(EtatProjet.valueOf(etat));
            projetAModifier.setEquipe(equipe);

            // Mettre à jour l'image du projet si une nouvelle image a été sélectionnée
            if (!imagePath.isEmpty()) {
                projetAModifier.setImageProjet(imagePath);
            }

            // Modifier le projet dans la base de données
            serviceProjet.modifierProjet(projetAModifier);

            // Afficher un message de succès
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Projet modifié avec succès !");
            applyAlertStyle(successAlert);
            successAlert.showAndWait();

            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

    @FXML
    //private void uploadImage(ActionEvent event) {
    private void uploadImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Définir le répertoire de destination (htdocs/images/)
                File uploadDir = new File("C:/xampp/htdocs/img");
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
                System.out.println("Erreur lors de l'ajout de la photo");
            }
        }
    }
}