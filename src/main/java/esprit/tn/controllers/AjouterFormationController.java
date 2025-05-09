package esprit.tn.controllers;

import esprit.tn.entities.Certifie;
import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.services.ServiceFormation;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjouterFormationController implements Initializable {

    @FXML
    private TextField titreID;
    @FXML
    private TextField descriptionID;
    @FXML
    private DatePicker dateID;
    @FXML
    private ChoiceBox<Typeformation> typeID;
    @FXML
    private TextField nbplaceID;
    @FXML
    private ChoiceBox<Certifie> certifieID;
    @FXML
    private TextField langueID;
    @FXML
    private Button ajouterfBTN;
    @FXML
    private AnchorPane FormAj;
    @FXML
    private ImageView photoPreview;
    @FXML
    private Button ajouterPhotoBtn;

    private String imagePath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeID.setItems(FXCollections.observableArrayList(Typeformation.values()));
        certifieID.setItems(FXCollections.observableArrayList(Certifie.values()));
    }

    @FXML
    public void OnAjouterFormation(ActionEvent event) {
        resetErrorMessages();

        try {
            String titre = titreID.getText();
            String description = descriptionID.getText();
            LocalDate date = dateID.getValue();
            String nbPlacesText = nbplaceID.getText();
            Typeformation typeFormation = typeID.getValue();
            Certifie certifie = certifieID.getValue();
            String langue = langueID.getText();

            if (titre.isEmpty()) {
                showErrorMessage(titreID, "Le titre ne peut pas être vide.");
                return;
            }

            if (description.isEmpty()) {
                showErrorMessage(descriptionID, "La description ne peut pas être vide.");
                return;
            }

            if (date == null || date.isBefore(LocalDate.now())) {
                showErrorMessage(dateID, "Veuillez sélectionner une date valide.");
                return;
            }

            int nbPlaces;
            try {
                nbPlaces = Integer.parseInt(nbPlacesText);
                if (nbPlaces <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showErrorMessage(nbplaceID, "Veuillez entrer un nombre valide pour les places.");
                return;
            }

            if (certifie == null) {
                showErrorMessage(certifieID, "Veuillez choisir la certification.");
                return;
            }

            if (langue.isEmpty()) {
                showErrorMessage(langueID, "Veuillez saisir la langue.");
                return;
            }

            if (imagePath.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez ajouter une photo.");
                return;
            }

            Formation formation = new Formation(titre, description, nbPlaces, typeFormation,SessionManager.extractuserfromsession().getIdUser(), date, imagePath,certifie, langue);
            new ServiceFormation().ajouterFormation(formation);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation ajoutée avec succès !");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void resetErrorMessages() {
        titreID.setStyle("");
        descriptionID.setStyle("");
        dateID.setStyle("");
        nbplaceID.setStyle("");
        certifieID.setStyle("");
        langueID.setStyle("");
        photoPreview.setStyle("");
    }

    private void showErrorMessage(Control control, String message) {
        showAlert(AlertType.ERROR, "Erreur", message);
        control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        applyAlertStyle(alert);
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

    @FXML
    public void OnAjouterPhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imagePath = fileName;
                photoPreview.setImage(new Image(destinationFile.toURI().toString()));
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Photo ajoutée avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la photo.");
            }
        }
    }

    @Deprecated
    public void retourdashRH(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardHR.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page : " + e.getMessage());
        }
    }
}