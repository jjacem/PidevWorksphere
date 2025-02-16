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

    @FXML
    private TextField searchField;

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

            // Bouton Détails
            Button detailsButton = new Button("Détails");
            detailsButton.setStyle("-fx-background-color: #0086b3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
            detailsButton.setOnAction(event -> afficherDetailsEquipe(equipe));

            // Bouton Modifier
            Button modifierButton = new Button("Modifier");
            modifierButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
            modifierButton.setOnAction(event -> modifierEquipe(equipe));

            // Bouton Supprimer
            Button supprimerButton = new Button("Supprimer");
            supprimerButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
            supprimerButton.setOnAction(event -> supprimerEquipe(equipe));

            // Ajouter les éléments à la carte
            card.getChildren().addAll(nomEquipeLabel, detailsButton, modifierButton, supprimerButton);
            equipesContainer.getChildren().add(card);
        }
    }

    @FXML
    public void redirectToAjouterEquipe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEquipe.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Ajouter une équipe");

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

            // Obtenir la scène actuelle
            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            // Remplacer le contenu de la scène actuelle avec la nouvelle vue
            stage.getScene().setRoot(root);
            stage.setTitle("Modifier une équipe");

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

            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.setTitle("Détails de l'équipe");
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void rechercherEquipe() {
        String nomEquipe = searchField.getText().trim();
        try {
            // Appeler la méthode rechercherEquipe avec le texte de recherche
            List<Equipe> equipes = serviceEquipe.rechercherEquipe(nomEquipe);
            afficherEquipes(equipes);  // Mettre à jour l'affichage avec les résultats
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void supprimerToutesEquipes() {
        // Afficher une alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer toutes les équipes ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEquipe.supprimerToutesEquipes(); // Méthode pour supprimer toutes les équipes
                List<Equipe> equipes = serviceEquipe.afficherEquipe(); // Rafraîchir la liste
                afficherEquipes(equipes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
