package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.services.ServiceFormation;
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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherFormationController {

    // Déclaration des éléments FXML
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
    private HBox mainContainer;
    @FXML
    private VBox listformationid;
    @FXML
    private ScrollPane scrollPane;

    // Initialisation de l'interface (remplissage des formations)
    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        listformationid.getStyleClass().addAll("list", "list-view");
        populateFormations();
    }

    // Méthode pour remplir la liste des formations
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

        // Création des boutons d'action pour chaque formation
        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().addAll("card-button", "details-button");
        detailButton.setOnAction(event -> afficherDetails(formation));

        Button modifierButton = new Button("Modifier");
        modifierButton.getStyleClass().addAll("card-button", "modifier-button");
        modifierButton.setOnAction(event -> modifierFormation(formation));

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().addAll("card-button", "supprimer-button");
        supprimerButton.setOnAction(event -> deleteFormation(formation));

        Button resButton = new Button("Reservation");
        resButton.getStyleClass().addAll("card-button", "res-button");
        resButton.setOnAction(event -> getUsersWhoReservedFormation(formation));

        // Conteneur pour les boutons
        HBox buttonContainer = new HBox(10, detailButton, modifierButton, supprimerButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(30, 10, 10, 300));

        // Conteneur principal pour chaque formation
        HBox formationBox = new HBox(10, imageView, infoBox, buttonContainer);
        formationBox.setAlignment(Pos.CENTER_LEFT);
        formationBox.setStyle("-fx-padding: 10px; -fx-border-color: lightgray; -fx-border-radius: 5px;");

        return formationBox;
    }

    private void getUsersWhoReservedFormation(Formation formation) {
    }

    // Afficher les détails de la formation dans une nouvelle fenêtre modale
    private void afficherDetails(Formation formation) {
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

    // Méthode pour modifier la formation
    private void modifierFormation(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFormation.fxml"));
            Parent root = loader.load();

            ModifierFormationController controller = loader.getController();
            controller.setFormation(formation);

            Stage stage = new Stage();
            stage.setTitle("Modifier Formation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer la formation
    private void deleteFormation(Formation formation) {
        ServiceFormation serviceFormation = new ServiceFormation();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Vous êtes sûr de vouloir supprimer cette formation ?");
        applyAlertStyle(alert);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceFormation.supprimeFormation(formation);
                System.out.println("Suppression réussie pour la formation : " + formation.getTitre());
                populateFormations();  // Recharger la liste après la suppression
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression annulée par l'utilisateur.");
        }
    }
    @FXML
    public void Onajouterformation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFormation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode de recherche de formation par titre
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

    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Appliquer un style personnalisé aux alertes
    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }


    @FXML
    public void retourdashRH() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardHR.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnajouterID.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
