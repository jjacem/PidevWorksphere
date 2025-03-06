package esprit.tn.controllers;

import esprit.tn.entities.Equipe;
import esprit.tn.entities.Projet;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceEquipe;
import esprit.tn.services.ServiceProjet;
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
import java.util.ArrayList;
import java.util.List;

public class AfficherProjetEmployeController {
    @FXML
    private VBox projetsContainer;

    @FXML
    private TextField searchField;

    private ServiceProjet serviceProjet;
    private ServiceEquipe serviceEquipe;

    public AfficherProjetEmployeController() {
        serviceProjet = new ServiceProjet();
        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        try {
            // Récupérer l'utilisateur connecté
            User user = SessionManager.extractuserfromsession();

            // Récupérer les équipes de l'utilisateur
            List<Equipe> equipes = serviceEquipe.getEquipesByUserId(user.getIdUser());

            if (!equipes.isEmpty()) {
                // Récupérer les projets de toutes les équipes de l'utilisateur
                List<Projet> projets = new ArrayList<>();
                for (Equipe equipe : equipes) {
                    projets.addAll(serviceProjet.getProjetsByEquipeId(equipe.getId()));
                }

                // Afficher les projets
                afficherProjets(projets);
            } else {
                Label messageLabel = new Label("Vous n'appartenez à aucun projet.");
                messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-font-style: italic;");
                projetsContainer.getChildren().add(messageLabel);
            }

            // Ajouter un Listener sur le TextField pour la recherche dynamique
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    // Appel de la recherche + mise à jour de la liste
                    List<Projet> projetsFiltres = serviceProjet.rechercherProjet(newValue);
                    afficherProjets(projetsFiltres);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherProjets(List<Projet> projets) {
        projetsContainer.getChildren().clear();

            for (Projet projet : projets) {
                // Créer une carte pour chaque projet
                HBox card = new HBox(20);
                card.getStyleClass().add("card");
                card.setAlignment(Pos.CENTER_LEFT);

                // Ajouter l'image du projet
                ImageView imageView = new ImageView();
                if (projet.getImageProjet() != null && !projet.getImageProjet().trim().isEmpty()) {
                    String correctPath = "C:/xampp/htdocs/img/" + new File(projet.getImageProjet()).getName();
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

                // Informations du projet
                VBox infoBox = new VBox(5);
                infoBox.setAlignment(Pos.CENTER_LEFT);

                Label nomLabel = new Label(projet.getNom());
                nomLabel.getStyleClass().add("card-label");

                Label dateCreationLabel = new Label("Créé le : " + projet.getDatecréation());
                dateCreationLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

                Label deadlineLabel = new Label("Deadline : " + projet.getDeadline());
                deadlineLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

                // Créer un Label pour l'état avec un style personnalisé
                Label etatLabel = new Label(projet.getEtat().name());
                etatLabel.getStyleClass().add("chip");

                // Appliquer un style en fonction de l'état
                switch (projet.getEtat()) {
                    case Terminé:
                        etatLabel.getStyleClass().add("chip-termine");
                        break;
                    case Annulé:
                        etatLabel.getStyleClass().add("chip-annule");
                        break;
                    case EnCours:
                        etatLabel.getStyleClass().add("chip-en-cours");
                        break;
                    default:
                        etatLabel.getStyleClass().add("chip-default");
                }

                infoBox.getChildren().addAll(nomLabel, dateCreationLabel, deadlineLabel, etatLabel);

                // Ajouter les éléments à la carte
                card.getChildren().addAll(imageView, infoBox);
                projetsContainer.getChildren().add(card);
            }

    }

    @FXML
    private void rechercherProjet() {
        String searchText = searchField.getText().trim();
        System.out.println("Recherche en cours avec le texte : " + searchText); // Log

        try {
            List<Projet> projets;
            if (searchText.isEmpty()) {
                projets = serviceProjet.afficherProjet();
            } else {
                projets = serviceProjet.rechercherProjet(searchText);
            }
            afficherProjets(projets);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}