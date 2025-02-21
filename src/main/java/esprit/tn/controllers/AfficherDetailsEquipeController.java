package esprit.tn.controllers;

import esprit.tn.services.ServiceEquipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import esprit.tn.entities.Equipe;
import esprit.tn.entities.User;

import java.io.IOException;
import java.net.URL;
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
    private ServiceEquipe serviceEquipe = new ServiceEquipe();

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
        afficherDetails();
    }


    private void afficherDetails() {
        nomEquipeLabel.setText(equipe.getNomEquipe());

        for (User user : equipe.getEmployes()) {
            HBox card = new HBox(10);
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

            // creation te3 image view
            ImageView imageView = new ImageView();
            String imageProfil = user.getImageProfil();

            if (imageProfil != null && !imageProfil.isEmpty()) {
                try {
                    // chargement te3 image mel url
                    Image image = new Image(imageProfil);
                    imageView.setImage(image);
                } catch (Exception e) {

                    Image defaultImage = new Image(getClass().getResource("/images/profil.png").toExternalForm());
                    imageView.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResource("/images/profil.png").toExternalForm());
                imageView.setImage(defaultImage);
            }

            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            imageView.setClip(new Circle(25, 25, 25));

            Label nomPrenomLabel = new Label(user.getNom() + " " + user.getPrenom());
            nomPrenomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Label emailLabel = new Label(user.getEmail());
            emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

            card.getChildren().addAll(imageView, nomPrenomLabel, emailLabel);
            membresContainer.getChildren().add(card);
        }
    }

    @FXML
    public void retour() {
        try {
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardManager.fxml"));
            Parent root = loader.load();
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

            membresContainer.getChildren().clear();

            for (User user : resultats) {
                HBox card = new HBox(10);
                card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");


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
                nomPrenomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");

                card.getChildren().addAll(imageView, nomPrenomLabel);
                membresContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}