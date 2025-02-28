package esprit.tn.controllers;
import esprit.tn.entities.Typeformation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceFormation;

import esprit.tn.entities.Formation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherListFormationController {

    private final ServiceFormation formationService = new ServiceFormation();
    @FXML
    private Button Btnrecherche;
    @FXML
    private TextField Trecherche;
    @FXML
    private VBox formationsContainer;
    @FXML
    private HBox mainContainer;
    @FXML
    private VBox listformationid;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        listformationid.getStyleClass().addAll("list", "list-view");
        populateFormations();
    }

    private void populateFormations() {
        listformationid.getChildren().clear();  // Nettoyer avant de recharger

        try {
            List<Formation> formations = formationService.getListFormation();
            for (Formation formation : formations) {
                HBox formationBox = createFormationBox(formation);
                formationBox.getStyleClass().addAll("list", "list-cell");
                listformationid.getChildren().add(formationBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour créer la boîte de chaque formation avec ses informations
    private HBox createFormationBox(Formation formation) {
        // Création de l'ImageView
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);

        // Vérification si le chemin de l'image est valide
        try {
            // Chemin de l'image sur le serveur
            String imagePath = "http://localhost/img/" + formation.getPhoto();
            Image image = new Image(imagePath, true); // 'true' permet le chargement en arrière-plan
            imageView.setImage(image);
        } catch (Exception e) {
            // En cas d'erreur, charger l'image par défaut
            Image defaultImage = new Image(getClass().getResourceAsStream("/img/default.png"));
            imageView.setImage(defaultImage);
        }

        // Création des labels pour les informations de la formation
        Label titreLabel = new Label(formation.getTitre());
        titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #22859c;");

        Label descriptionLabel = new Label("Description: " + formation.getDescription());
        Label dateLabel = new Label("Date: " + formation.getDate().toString());
        Label heureDebutLabel = new Label("Heure de Début: " + formation.getHeure_debut().toString());
        Label heureFinLabel = new Label("Heure de Fin: " + formation.getHeure_fin().toString());
        Label nbPlacesLabel = new Label("Nombre de Places: " + formation.getNb_place());

        VBox infoBox = new VBox(5, titreLabel, descriptionLabel, dateLabel, heureDebutLabel, heureFinLabel, nbPlacesLabel);
        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().addAll("card-button", "details-button");
        detailButton.setOnAction(event -> afficherDetails(formation));

        Button reserverButton = new Button("Reserver");
        reserverButton.getStyleClass().addAll("card-button", "button-reserver");
        reserverButton.setOnAction(event -> {
            try {
                // Charger la scène AjouterReservation.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur AjouterReservationController
                AjouterReservationController controller = loader.getController();

                // Passer l'ID de la formation
                controller.setFormationId(formation.getId_f());

                // Créer une nouvelle fenêtre (Stage)
                Stage popupStage = new Stage();
                popupStage.setTitle("Ajouter une réservation");

                // Empêcher l'interaction avec la fenêtre principale tant que le popup est ouvert
                popupStage.initModality(Modality.APPLICATION_MODAL);

                // Ajouter le formulaire au stage et l'afficher
                Scene scene = new Scene(root);
                popupStage.setScene(scene);
                popupStage.showAndWait(); // Attendre la fermeture de la fenêtre
            } catch (IOException e) {
                System.out.println("Erreur de chargement du popup : " + e.getMessage());
            }
        });

        HBox buttonContainer = new HBox(10,detailButton, reserverButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(10));

        HBox formationBox = new HBox(10, imageView, infoBox, buttonContainer);
        formationBox.setAlignment(Pos.CENTER_LEFT);
        formationBox.setStyle("-fx-padding: 10px; -fx-border-color: lightgray; -fx-border-radius: 5px;");
        return formationBox;
    }

    public void afficherDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailFormation.fxml"));
            Parent root = loader.load();

            AfficherDetailFormationController controller = loader.getController();
            controller.setFormation(formation);

            Stage stage = new Stage();
            stage.setTitle("Détails de la Formation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void OnchercherFormation() {
        String searchText = Trecherche.getText().toLowerCase();

        try {
            List<Formation> allFormations = formationService.getListFormation();
            List<Formation> filteredFormations = allFormations.stream()
                    .filter(formation -> formation.getTitre().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            formationsContainer.getChildren().clear();
            for (Formation formation : filteredFormations) {
                formationsContainer.getChildren().add(createFormationBox(formation));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void retourdashRH(ActionEvent actionEvent) {
    }
}

