package esprit.tn.controllers;

import esprit.tn.entities.Equipe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceEquipe;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class AfficherEquipeEmployeController {

    @FXML
    private VBox equipesContainer;

    @FXML
    private TextField searchField;


    private ServiceEquipe serviceEquipe;


    public AfficherEquipeEmployeController() {

        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        try {
            // Récupérer l'utilisateur connecté
            User user = SessionManager.extractuserfromsession();

            // Récupérer les équipes de l'utilisateur
            List<Equipe> equipes = serviceEquipe.getEquipesByUserId(user.getIdUser());

            // Afficher les équipes de l'employé
            if (!equipes.isEmpty()) {
                afficherEquipes(equipes); // Utiliser la méthode existante pour afficher les équipes
            } else {
                Label messageLabel = new Label("Vous n'appartenez à aucune équipe.");
                messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-font-style: italic;");
                equipesContainer.getChildren().add(messageLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherEquipes(List<Equipe> equipes) {
        equipesContainer.getChildren().clear(); // Vider le conteneur

        if (equipes.isEmpty()) {
            Label messageLabel = new Label("Aucune équipe trouvée");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-font-style: italic;");
            equipesContainer.getChildren().add(messageLabel);
        } else {
            for (Equipe equipe : equipes) {
                HBox card = new HBox(20); // Conteneur principal pour la carte
                card.getStyleClass().add("card");
                card.setAlignment(Pos.CENTER_LEFT);

                // Ajouter l'image de l'équipe
                ImageView imageView = new ImageView();
                if (equipe.getImageEquipe() != null && !equipe.getImageEquipe().trim().isEmpty()) {
                    String correctPath = "C:/xampp/htdocs/img/" + new File(equipe.getImageEquipe()).getName();
                    File imageFile = new File(correctPath);
                    if (imageFile.exists() && imageFile.isFile()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                    }
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                }
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.getStyleClass().add("card-image");

                // Conteneur pour le nom de l'équipe et le nombre de projets
                VBox infoBox = new VBox(5); // Espacement de 5 entre les éléments
                infoBox.setAlignment(Pos.CENTER_LEFT);

                // Nom de l'équipe
                Label nomEquipeLabel = new Label(equipe.getNomEquipe());
                nomEquipeLabel.getStyleClass().add("card-label");

                // Nombre de projets
                Label nbrProjetLabel = new Label("Nombre de projets : " + equipe.getNbrProjet());
                nbrProjetLabel.getStyleClass().add("card-label");
                nbrProjetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

                // Ajouter le nom et le nombre de projets dans le VBox
                infoBox.getChildren().addAll(nomEquipeLabel, nbrProjetLabel);

                // Ajouter les éléments à la carte
                card.getChildren().addAll(imageView, infoBox);
                equipesContainer.getChildren().add(card);
            }
        }
    }

}

