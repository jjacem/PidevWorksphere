package esprit.tn.controllers;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import esprit.tn.entities.Equipe;
import esprit.tn.services.ServiceEquipe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AfficherEquipeController {

    @FXML
    private VBox equipesContainer;

    private ServiceEquipe serviceEquipe;

    public AfficherEquipeController() {
        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        // Charger la liste des équipes
        try {
            List<Equipe> equipes = serviceEquipe.afficherEquipe();
            afficherEquipes(equipes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherEquipes(List<Equipe> equipes) {
        equipesContainer.getChildren().clear(); // Vider le conteneur

        for (Equipe equipe : equipes) {
            // Créer une carte pour chaque équipe
            HBox card = new HBox(10);
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

            // Nom de l'équipe
            Label nomEquipeLabel = new Label(equipe.getNomEquipe());
            nomEquipeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            // Bouton Modifier avec icône
            Button modifierButton = new Button();
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit.png")));
            editIcon.setFitWidth(16); // Taille de l'icône
            editIcon.setFitHeight(16);
            modifierButton.setGraphic(editIcon);
            modifierButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            modifierButton.setOnAction(event -> modifierEquipe(equipe));

            // Bouton Supprimer avec icône
            Button supprimerButton = new Button();
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/delete.png")));
            deleteIcon.setFitWidth(16); // Taille de l'icône
            deleteIcon.setFitHeight(16);
            supprimerButton.setGraphic(deleteIcon);
            supprimerButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            supprimerButton.setOnAction(event -> supprimerEquipe(equipe));

            // Bouton Détails avec icône
            Button detailsButton = new Button();
            ImageView detailsIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/details.png")));
            detailsIcon.setFitWidth(16); // Taille de l'icône
            detailsIcon.setFitHeight(16);
            detailsButton.setGraphic(detailsIcon);
            detailsButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
            detailsButton.setOnAction(event -> afficherDetailsEquipe(equipe));

            // Ajouter les éléments à la carte
            card.getChildren().addAll(nomEquipeLabel, modifierButton, supprimerButton, detailsButton);
            equipesContainer.getChildren().add(card);
        }
    }

    @FXML
    public void redirectToAjouterEquipe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEquipe.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifierEquipe(Equipe equipe) {
        try {
            // Charger la vue de modification d'équipe
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEquipe.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la vue de modification
            ModifierEquipeController controller = loader.getController();

            // Passer l'équipe à modifier au contrôleur
            controller.setEquipeAModifier(equipe);

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre (stage)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modifier une équipe");

            // Afficher la fenêtre
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement de la page de modification");
            alert.setContentText("Une erreur s'est produite lors de l'ouverture de la page de modification.");
            alert.showAndWait();
        }
    }

    private void supprimerEquipe(Equipe equipe) {
        // Afficher une alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette équipe ?");
        alert.setContentText("Cette action est irréversible.");

        // Appliquer le style CSS à l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/stylesEquipe.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'équipe
                serviceEquipe.supprimerEquipe(equipe.getId());

                // Rafraîchir la liste des équipes
                List<Equipe> equipes = serviceEquipe.afficherEquipe();
                afficherEquipes(equipes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void afficherDetailsEquipe(Equipe equipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailsEquipe.fxml"));
            Parent root = loader.load();

            // Passer l'équipe sélectionnée au contrôleur des détails
            AfficherDetailsEquipeController controller = loader.getController();
            controller.setEquipe(equipe);

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'équipe");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
