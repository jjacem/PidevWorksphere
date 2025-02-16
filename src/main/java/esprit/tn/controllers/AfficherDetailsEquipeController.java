package esprit.tn.controllers;

import esprit.tn.services.ServiceEquipe;
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
import java.sql.SQLException;
import java.util.List;

public class AfficherDetailsEquipeController {

    @FXML
    private Label nomEquipeLabel;

    @FXML
    private TextField rechercheField;

    @FXML
    private VBox membresContainer;

    private Equipe equipe;
    private ServiceEquipe serviceEquipe = new ServiceEquipe(); // Instance du service

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
            // Obtenir la scène actuelle et remplacer son contenu
            Stage stage = (Stage) membresContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void rechercherEmployee() {
        try {
            String searchText = rechercheField.getText().trim();
            List<User> resultats = serviceEquipe.rechercherEmployee(equipe.getId(), searchText);

            membresContainer.getChildren().clear(); // Vider l'affichage

            for (User user : resultats) {
                HBox card = new HBox(10);
                Label nomPrenomLabel = new Label(user.getNom() + " " + user.getPrenom());
                card.getChildren().add(nomPrenomLabel);
                membresContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}