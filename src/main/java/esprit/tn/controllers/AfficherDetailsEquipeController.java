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

    private Equipe equipe;
    private ServiceEquipe serviceEquipe = new ServiceEquipe();

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        afficherDetails();
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

        // Afficher les membres de l'équipe
        for (User user : equipe.getEmployes()) {
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

    @FXML
    private void rechercherEmployee() {
        try {
            String searchText = rechercheField.getText().trim();
            List<User> resultats = serviceEquipe.rechercherEmployee(equipe.getId(), searchText);

            // Vider le conteneur avant d'ajouter de nouveaux éléments
            membresContainer.getChildren().clear();

            for (User user : resultats) {
                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

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


                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
                imageView.setClip(new Circle(25, 25, 25));

                Label nomPrenomLabel = new Label(user.getNom() + " " + user.getPrenom());
                nomPrenomLabel.getStyleClass().add("member-name");

                Label emailLabel = new Label(user.getEmail());
                emailLabel.getStyleClass().add("member-email");

                // Conteneur pour les informations du membre
                VBox infoBox = new VBox(5);
                infoBox.getChildren().addAll(nomPrenomLabel, emailLabel);
                card.getChildren().addAll(imageView, infoBox);
                membresContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}