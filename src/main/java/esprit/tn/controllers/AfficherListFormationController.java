package esprit.tn.controllers;
import esprit.tn.entities.Typeformation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceFormation;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Reservation;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML
    private Button btnajouterID;

    private final ServiceFormation formationService = new ServiceFormation();
    @FXML
    private Button Btnrecherche;
    @FXML
    private TextField Trecherche;
    @FXML
    private VBox formationsContainer;

    @FXML
    public void initialize() {
        populateFormations();
    }

    private void populateFormations() {
        formationsContainer.getChildren().clear();

        try {
            List<Formation> formations = formationService.getListFormation();
            for (Formation formation : formations) {
                HBox formationBox = createFormationBox(formation);
                formationsContainer.getChildren().add(formationBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createFormationBox(Formation formation) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);

        if (formation.getPhoto() != null) {
            imageView.setImage(new Image(formation.getPhoto().toString()));
        }

        Label titreLabel = new Label(formation.getTitre());
        titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");

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
            // Récupérer la formation sélectionnée (assurez-vous que `formationId` est disponible)
            try {
                // Charger la scène AjouterReservation.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur AjouterReservationController
                AjouterReservationController controller = loader.getController();
                controller.setUser(formation.getUser());

                // Passer l'ID de la formation et l'ID utilisateur
                controller.setFormationId(formation.getId_f());
                controller.setUserId(SessionManager.extractuserfromsession().getIdUser()); // ID utilisateur connecté

                // Création d'un nouveau Stage pour le popup
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
                popupStage.initStyle(StageStyle.UTILITY); // Style de la fenêtre
                popupStage.setTitle("Ajouter une réservation");

                // Définir la scène du popup
                Scene scene = new Scene(root);
                popupStage.setScene(scene);

                // Afficher le popup et attendre la fermeture avant de revenir à la fenêtre principale
                popupStage.showAndWait();

            } catch (IOException e) {
                System.out.println("Erreur de chargement de la page de réservation : " + e.getMessage());
            } catch (SQLException e) {
                throw new RuntimeException(e);
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

    private void afficherDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailFormation.fxml"));
            Parent root = loader.load();
            AfficherDetailFormationController controller = loader.getController();
            controller.setFormation(formation);
            Stage stage = new Stage();
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

    public void retourdashRH(ActionEvent actionEvent) {
    }
}

