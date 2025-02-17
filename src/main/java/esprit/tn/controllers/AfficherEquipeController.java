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

        if (equipes.isEmpty()) {
            Label messageLabel = new Label("Aucune équipe enregistrée");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-font-style: italic;");
            equipesContainer.getChildren().add(messageLabel);
        } else {
            // Affichage te3 les equipes + nom + les boutons
            for (Equipe equipe : equipes) {
                // hne creation te3 card pour chaque equipe
                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

                Label nomEquipeLabel = new Label(equipe.getNomEquipe());
                nomEquipeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

                Button detailsButton = new Button("Détails");
                detailsButton.setStyle("-fx-background-color: #0086b3; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                detailsButton.setOnAction(event -> afficherDetailsEquipe(equipe));


                Button modifierButton = new Button("Modifier");
                modifierButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                modifierButton.setOnAction(event -> modifierEquipe(equipe));


                Button supprimerButton = new Button("Supprimer");
                supprimerButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 10;");
                supprimerButton.setOnAction(event -> supprimerEquipe(equipe));

                // Ajout te3 les elements lel card
                card.getChildren().addAll(nomEquipeLabel, detailsButton, modifierButton, supprimerButton);
                equipesContainer.getChildren().add(card);
            }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEquipe.fxml"));
            Parent root = loader.load();
            ModifierEquipeController controller = loader.getController();
            controller.setEquipeAModifier(equipe);
            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Modifier une équipe");

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    private void supprimerEquipe(Equipe equipe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette équipe ?");
        alert.setContentText("Cette action est irréversible.");
        applyAlertStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEquipe.supprimerEquipe(equipe.getId());
                // refresh te3 liste
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
            List<Equipe> equipes = serviceEquipe.rechercherEquipe(nomEquipe);
            afficherEquipes(equipes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void supprimerToutesEquipes() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer toutes les équipes ?");
        alert.setContentText("Cette action est irréversible.");
        applyAlertStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEquipe.supprimerToutesEquipes();
                List<Equipe> equipes = serviceEquipe.afficherEquipe();
                afficherEquipes(equipes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}
