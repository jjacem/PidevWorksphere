package esprit.tn.controllers;

import esprit.tn.services.ServiceEquipe;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import esprit.tn.entities.Equipe;
import esprit.tn.entities.*;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AfficherDetailsEquipeController {

    @FXML
    private Label nomEquipeLabel;

    @FXML
    private TextField rechercheField;

    @FXML
    private VBox membresContainer;

    @FXML
    private ImageView imageEquipeView;
    private List<User> membresInitiaux;
    private Equipe equipe;
    private ServiceEquipe serviceEquipe = new ServiceEquipe();

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        afficherDetails();
    }


    @FXML
    public void initialize() {
        // Ajouter un écouteur sur le champ de recherche
        rechercheField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                // Si le champ de recherche est vide, restaurer la liste initiale
                afficherMembres(membresInitiaux);
            }
        });
    }

    private void afficherDetails() {
        nomEquipeLabel.setText(equipe.getNomEquipe());

        // Charger l'image de l'équipe
        if (equipe.getImageEquipe() != null && !equipe.getImageEquipe().trim().isEmpty()) {
            String correctPath = "C:/xampp/htdocs/img/" + new File(equipe.getImageEquipe()).getName();
            File imageFile = new File(correctPath);
            if (imageFile.exists() && imageFile.isFile()) {
                imageEquipeView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                System.out.println("Fichier image introuvable ou chemin invalide : " + imageFile.getAbsolutePath());
                imageEquipeView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
            }
        } else {
            System.out.println("Aucun chemin d'image fourni pour l'équipe : " + equipe.getNomEquipe());
            imageEquipeView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
        }

        // Stocker la liste initiale des membres
        membresInitiaux = new ArrayList<>(equipe.getEmployes());

        // Afficher les membres de l'équipe
        afficherMembres(membresInitiaux);

        // Afficher les projets de l'équipe
        Label projetsLabel = new Label("Projets associés :");
        projetsLabel.getStyleClass().add("projets-label");
        membresContainer.getChildren().add(projetsLabel);

        for (Projet projet : equipe.getProjets()) {
            Label projetLabel = new Label(projet.getNom());
            projetLabel.getStyleClass().add("projet-name");
            membresContainer.getChildren().add(projetLabel);
        }
    }
    private void afficherMembres(List<User> membres) {
        membresContainer.getChildren().clear(); // Vider le conteneur

        for (User user : membres) {
            HBox card = new HBox(15);
            card.getStyleClass().add("member-card");
            card.setAlignment(Pos.CENTER_LEFT);

            // Création de l'image view pour le membre
            ImageView imageView = new ImageView();
            String imageProfil = user.getImageProfil();

            if (imageProfil != null && !imageProfil.trim().isEmpty()) {
                String correctPath = "C:/xampp/htdocs/img/" + new File(imageProfil).getName();
                System.out.println(correctPath); // Debug : afficher le chemin
                File imageFile = new File(correctPath);
                if (imageFile.exists() && imageFile.isFile()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("Image file not found or invalid path: " + imageFile.getAbsolutePath());
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                }
            } else {
                System.out.println("No image path provided.");
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
            }

            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);
            imageView.setClip(new Circle(30, 30, 30));
            imageView.getStyleClass().add("member-image");

            VBox infoBox = new VBox(5);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Label nomPrenomLabel = new Label(user.getNom() + " " + user.getPrenom());
            nomPrenomLabel.getStyleClass().add("member-name");

            Label emailLabel = new Label(user.getEmail());
            emailLabel.getStyleClass().add("member-email");

            infoBox.getChildren().addAll(nomPrenomLabel, emailLabel);
            card.getChildren().addAll(imageView, infoBox);
            membresContainer.getChildren().add(card);
        }
    }

    @FXML
    private void rechercherEmployee() {
        String searchText = rechercheField.getText().trim();

        if (searchText.isEmpty()) {
            // Si le champ de recherche est vide, restaurer la liste initiale
            afficherMembres(membresInitiaux);
        } else {
            try {
                List<User> resultats = serviceEquipe.rechercherEmployee(equipe.getId(), searchText);

                // Vider le conteneur avant d'ajouter de nouveaux éléments
                membresContainer.getChildren().clear();

                // Afficher les employés trouvés
                afficherMembres(resultats);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Afficher les projets associés à l'équipe
        Label projetsLabel = new Label("Projets associés :");
        projetsLabel.getStyleClass().add("projets-label");
        membresContainer.getChildren().add(projetsLabel);

        for (Projet projet : equipe.getProjets()) {
            Label projetLabel = new Label(projet.getNom());
            projetLabel.getStyleClass().add("projet-name");
            membresContainer.getChildren().add(projetLabel);
        }
    }

}