package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.services.ServiceFormation;
import esprit.tn.utils.SessionManager;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AjouterFormationController implements Initializable {

    @FXML
    private TextField titreID;
    @FXML
    private TextField descriptionID;
    @FXML
    private DatePicker dateID;
    @FXML
    private TextField heureDID;
    @FXML
    private TextField heureFID;
    @FXML
    private ChoiceBox<Typeformation> typeID;
    @FXML
    private TextField nbplaceID;
    @FXML
    private Button ajouterfBTN;
    @FXML
    private AnchorPane FormAj;
    @FXML
    private TextField photoID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeID.setItems(FXCollections.observableArrayList(Typeformation.values()));

        Platform.runLater(() -> {
           Stage stage = (Stage) FormAj.getScene().getWindow();
           stage.setMaximized(true);
        });
    }

    @FXML
    public void OnAjouterFormation(ActionEvent event) {
        resetErrorMessages();

        try {
            String titre = titreID.getText();
            String description = descriptionID.getText();
            LocalDate date = dateID.getValue();
            String heureDebutText = heureDID.getText();
            String heureFinText = heureFID.getText();
            String nbPlacesText = nbplaceID.getText();
            Typeformation typeFormation = typeID.getValue();

            if (titre.isEmpty()) {
                showErrorMessage(titreID, "Le titre ne peut pas être vide.");
                return;
            }

            if (description.isEmpty()) {
                showErrorMessage(descriptionID, "La description ne peut pas être vide.");
                return;
            }

            if (date == null) {
                showErrorMessage(dateID, "Veuillez sélectionner une date.");
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                showErrorMessage(dateID, "La date ne doit pas être inférieure à aujourd'hui.");
                return;
            }

            LocalTime heureDebut;
            LocalTime heureFin;
            try {
                heureDebut = LocalTime.parse(heureDebutText, DateTimeFormatter.ofPattern("HH:mm"));
                heureFin = LocalTime.parse(heureFinText, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                showErrorMessage(heureDID, "Format d'heure invalide (HH:mm). Veuillez corriger.");
                return;
            }

            int nbPlaces;
            try {
                nbPlaces = Integer.parseInt(nbPlacesText);
                if (nbPlaces <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showErrorMessage(nbplaceID, "Veuillez entrer un nombre valide et positif pour les places.");
                return;
            }

            URL photo = null;
            if (!photoID.getText().isEmpty()) {
                try {
                    photo = new URL(photoID.getText());
                } catch (MalformedURLException e) {
                    showErrorMessage(photoID, "URL de la photo invalide !");
                    return;
                }
            }


            Formation formation = new Formation(description, titre, date, heureDebut, heureFin, nbPlaces, typeFormation, photo, SessionManager.extractuserfromsession().getIdUser());

            ServiceFormation serviceFormation = new ServiceFormation();
            serviceFormation.ajouterFormation(formation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation ajoutée avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFormation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void resetErrorMessages() {
        titreID.setStyle("");
        descriptionID.setStyle("");
        dateID.setStyle("");
        heureDID.setStyle("");
        heureFID.setStyle("");
        nbplaceID.setStyle("");
        photoID.setStyle("");
    }

    private void showErrorMessage(Control control, String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message);
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
    public void retourdashRH(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardHR.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page : " + e.getMessage());
        }
    }
}
