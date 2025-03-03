package esprit.tn.controllers;

import esprit.tn.services.ServiceEquipe;
import esprit.tn.utils.CloudinaryUploader;
import esprit.tn.utils.PDFGenerator;
import esprit.tn.utils.QRCodeGenerator;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Base64;
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
    @FXML
    private ImageView qrCodeImageView;

    private Equipe equipe;
    private ServiceEquipe serviceEquipe = new ServiceEquipe();

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        afficherDetails();
        generateQRCodeForEquipe(equipe);
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

            membresContainer.getChildren().clear();

            for (User user : resultats) {
                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");


                // Image du membre
                ImageView imageView = new ImageView();
                String imageProfil = user.getImageProfil();

                if (imageProfil != null && !imageProfil.isEmpty()) {
                    try {

                        Image image = new Image(imageProfil);
                        imageView.setImage(image);
                    } catch (Exception e) {
                        URL imageUrl = getClass().getResource("/images/profil.png");
                        if (imageUrl != null) {
                            Image defaultImage = new Image(imageUrl.toExternalForm());
                            imageView.setImage(defaultImage);
                        }
                    }
                } else {
                    URL imageUrl = getClass().getResource("/images/profil.png");
                    if (imageUrl != null) {
                        Image defaultImage = new Image(imageUrl.toExternalForm());
                        imageView.setImage(defaultImage);
                    }
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

    /*public String generateJsonForEquipe(Equipe equipe) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        // Ajouter le nom de l'équipe
        jsonObject.addProperty("nomEquipe", equipe.getNomEquipe());
        // Ajouter les membres de l'équipe
        JsonArray membresArray = new JsonArray();
        for (User user : equipe.getEmployes()) {
            JsonObject membreObject = new JsonObject();
            membreObject.addProperty("nom", user.getNom() + " " + user.getPrenom());
            membreObject.addProperty("email", user.getEmail());
            membresArray.add(membreObject);
        }
        jsonObject.add("membres", membresArray);

        // Ajouter les projets de l'équipe
        JsonArray projetsArray = new JsonArray();
        for (Projet projet : equipe.getProjets()) {
            JsonObject projetObject = new JsonObject();
            projetObject.addProperty("nomProjet", projet.getNom());
            projetObject.addProperty("description", projet.getDescription());
            projetsArray.add(projetObject);
        }
        jsonObject.add("projets", projetsArray);

        // Convertir l'objet JSON en chaîne de caractères
        return gson.toJson(jsonObject);
    }*/

    /*public void generateQRCodeForEquipe(Equipe equipe) {
        try {
            // Générer le JSON pour l'équipe
            String jsonData = generateJsonForEquipe(equipe);

            // Générer le QR code à partir du JSON
            byte[] qrCodeImageData = QRCodeGenerator.generateQRCode(jsonData);

            // Convertir les données binaires en une image JavaFX
            ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeImageData);
            Image qrCodeImage = new Image(inputStream);

            // Afficher le QR code dans l'ImageView
            qrCodeImageView.setImage(qrCodeImage);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // Afficher une image par défaut en cas d'erreur
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/profil.png"));
            qrCodeImageView.setImage(defaultImage);
        }
    }*/

    public String generateFormattedStringForEquipe(Equipe equipe) {
        StringBuilder formattedString = new StringBuilder();

        // Ajouter le nom de l'équipe
        formattedString.append("Equipe: ").append(equipe.getNomEquipe()).append("\n\n");

        // Ajouter les membres de l'équipe
        formattedString.append("Membres:\n");
        for (User user : equipe.getEmployes()) {
            formattedString.append("- ").append(user.getNom()).append(" ").append(user.getPrenom())
                    .append(" (").append(user.getEmail()).append(")\n");
        }

        // Ajouter les projets de l'équipe
        formattedString.append("\nProjets:\n");
        for (Projet projet : equipe.getProjets()) {
            formattedString.append("- ").append(projet.getNom()).append(": ")
                    .append(projet.getDescription()).append("\n");
        }

        return formattedString.toString();
    }
   /* hedii te3 ktiba mech f json public void generateQRCodeForEquipe(Equipe equipe) {
        try {
            // Générer la chaîne formatée pour l'équipe
            String formattedData = generateFormattedStringForEquipe(equipe);

            // Générer le QR code à partir de la chaîne formatée
            byte[] qrCodeImageData = QRCodeGenerator.generateQRCode(formattedData);

            // Convertir les données binaires en une image JavaFX
            ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeImageData);
            Image qrCodeImage = new Image(inputStream);

            // Afficher le QR code dans l'ImageView
            qrCodeImageView.setImage(qrCodeImage);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // Afficher une image par défaut en cas d'erreur
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/profil.png"));
            qrCodeImageView.setImage(defaultImage);
        }
    }*/

    public void generateQRCodeForEquipe(Equipe equipe) {
        try {
            // Générer le PDF pour l'équipe
            byte[] pdfData = PDFGenerator.generateEquipePDF(equipe);

            // Upload the PDF to Cloudinary and get the shareable link
            String pdfUrl = CloudinaryUploader.uploadPdfToCloudinary(pdfData);

            // Debug: Print the PDF URL
            System.out.println("PDF URL: " + pdfUrl);

            // Générer le QR code à partir de l'URL du PDF
            byte[] qrCodeImageData = QRCodeGenerator.generateQRCode(pdfUrl);

            // Convertir les données binaires en une image JavaFX
            ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeImageData);
            Image qrCodeImage = new Image(inputStream);

            // Afficher le QR code dans l'ImageView
            qrCodeImageView.setImage(qrCodeImage);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // Afficher une image par défaut en cas d'erreur
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/profil.png"));
            qrCodeImageView.setImage(defaultImage);
        }
    }
}