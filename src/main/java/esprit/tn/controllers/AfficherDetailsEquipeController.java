package esprit.tn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import esprit.tn.entities.Equipe;
import esprit.tn.entities.User;

import java.io.IOException;

public class AfficherDetailsEquipeController {

    @FXML
    private Label nomEquipeLabel;

    @FXML
    private TextField rechercheField;

    @FXML
    private VBox membresContainer;

    private Equipe equipe;

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        afficherDetails();
    }

    private void afficherDetails() {
        // Afficher le nom de l'équipe
        nomEquipeLabel.setText(equipe.getNomEquipe());

        // Afficher les membres de l'équipe
        for (User user : equipe.getEmployes()) {
            HBox card = new HBox(10);
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

            Label nomPrenomLabel = new Label(user.getNom() + " " + user.getPrenom());
            nomPrenomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Label emailLabel = new Label(user.getEmail());
            emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

            card.getChildren().addAll(nomPrenomLabel, emailLabel);
            membresContainer.getChildren().add(card);
        }
    }

    @FXML
    public void retour() {
        try {
            // Charger l'interface de la liste des équipes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
            Parent root = loader.load();

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) membresContainer.getScene().getWindow();
            stage.close();

            // Ouvrir la fenêtre de la liste des équipes
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Liste des équipes");
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}