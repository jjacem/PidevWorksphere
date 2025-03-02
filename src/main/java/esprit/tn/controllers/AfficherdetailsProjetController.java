package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.*;
import esprit.tn.utils.PDFGenerator;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
public class AfficherdetailsProjetController {

    @FXML
    private Label nomProjetLabel;

    @FXML
    private ImageView imageProjetView;

    @FXML
    private Label descriptionProjetLabel;

    @FXML
    private Label nomEquipeLabel;

    @FXML
    private VBox employesContainer;

    private Projet projet;
    private ServiceProjet serviceProjet = new ServiceProjet();
    private ServiceEquipe serviceEquipe = new ServiceEquipe();


    public void setProjet(Projet projet) {
        this.projet = projet;
        afficherDetails();
    }

    private void afficherDetails() {
        if (projet != null) {
            // Afficher le nom du projet
            nomProjetLabel.setText(projet.getNom());

            // Afficher l'image du projet
            if (projet.getImageProjet() != null && !projet.getImageProjet().trim().isEmpty()) {
                String correctPath = "C:/xampp/htdocs/img/" + new File(projet.getImageProjet()).getName();
                File imageFile = new File(correctPath);
                if (imageFile.exists() && imageFile.isFile()) {
                    imageProjetView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("Fichier image introuvable ou chemin invalide : " + imageFile.getAbsolutePath());
                    imageProjetView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                }
            } else {
                System.out.println("Aucun chemin d'image fourni pour le projet : " + projet.getNom());
                imageProjetView.setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
            }

            // Afficher la description du projet
            descriptionProjetLabel.setText("Description : " + projet.getDescription());

            // Récupérer l'équipe et les employés associés au projet
            try {
                Equipe equipe = serviceProjet.getEquipeAvecEmployesParProjet(projet.getId());
                if (equipe != null) {
                    nomEquipeLabel.setText("Équipe : " + equipe.getNomEquipe());
                    afficherEmployesEquipe(equipe.getEmployes());
                } else {
                    nomEquipeLabel.setText("Équipe : Aucune équipe assignée");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Afficher les employés de l'équipe
    private void afficherEmployesEquipe(List<User> employes) {
        employesContainer.getChildren().clear(); // Vider le conteneur avant d'ajouter de nouveaux éléments

        if (employes != null && !employes.isEmpty()) {
            for (User employe : employes) {
                HBox card = new HBox(15);
                card.getStyleClass().add("member-card"); // Appliquer la classe CSS
                card.setAlignment(Pos.CENTER_LEFT);

                // Image de l'employé
                ImageView imageView = new ImageView();
                String imageProfil = employe.getImageProfil();

                if (imageProfil != null && !imageProfil.isEmpty()) {
                    try {
                        Image image = new Image(imageProfil);
                        imageView.setImage(image);
                    } catch (Exception e) {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                    }
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                }

                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                imageView.setClip(new Circle(30, 30, 30));
                imageView.getStyleClass().add("member-image"); // Appliquer la classe CSS

                // Informations de l'employé
                VBox infoBox = new VBox(5);
                infoBox.setAlignment(Pos.CENTER_LEFT);

                Label nomPrenomLabel = new Label(employe.getNom() + " " + employe.getPrenom());
                nomPrenomLabel.getStyleClass().add("member-name"); // Appliquer la classe CSS

                Label emailLabel = new Label(employe.getEmail());
                emailLabel.getStyleClass().add("member-email"); // Appliquer la classe CSS

                infoBox.getChildren().addAll(nomPrenomLabel, emailLabel);
                card.getChildren().addAll(imageView, infoBox);
                employesContainer.getChildren().add(card);
            }
        } else {
            employesContainer.getChildren().add(new Label("Aucun employé dans cette équipe."));
        }
    }
    
    @FXML
    private void convertirEnPDF() {
        try {
            // Générer le PDF
            byte[] pdfBytes = PDFGenerator.generateProjetPDF(projet);

            // Chemin complet pour enregistrer le PDF
            String filePath = "C:/xampp/htdocs/" + projet.getNom().replace(" ", "_") + "_details.pdf";

            // Écrire le PDF dans un fichier
            Files.write(Paths.get(filePath), pdfBytes);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Le PDF a été généré avec succès : " + filePath);
            applyAlertStyle(alert);
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de la génération du PDF.");
            applyAlertStyle(alert);
            alert.showAndWait();
        }
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

}