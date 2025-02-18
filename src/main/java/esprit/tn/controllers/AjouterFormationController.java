package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.services.ServiceFormation;
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

    @Deprecated
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeID.setItems(FXCollections.observableArrayList(Typeformation.values()));

    }

    @FXML
    public void OnAjouterFormation(ActionEvent event) {
        try {
            // Récupération des données
            String titre = titreID.getText();
            String description = descriptionID.getText();
            LocalDate date = dateID.getValue();
            LocalTime heureDebut = LocalTime.parse(heureDID.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime heureFin = LocalTime.parse(heureFID.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            int nbPlaces = Integer.parseInt(nbplaceID.getText());
            Typeformation typeFormation = typeID.getValue();

            // Vérification des champs obligatoires
            if (titre.isEmpty() || description.isEmpty() || date == null || typeFormation == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }

            // Gestion de l'URL de la photo
            URL photo = null;
            if (!photoID.getText().isEmpty()) {
                try {
                    photo = new URL(photoID.getText());
                } catch (MalformedURLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "URL de la photo invalide !");
                    return;
                }
            }

            // Remplacer 1 par l'ID utilisateur réel
            int userId = 1;

            // Création de l'objet Formation
            Formation formation = new Formation(description, titre, date, heureDebut, heureFin, nbPlaces, typeFormation, photo, userId);

            // Ajout via le service
            ServiceFormation serviceFormation = new ServiceFormation();
            serviceFormation.ajouterFormation(formation);

            // Affichage du message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Formation ajoutée avec succès !");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFormation.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide pour les places et respecter le format HH:mm pour les heures.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
