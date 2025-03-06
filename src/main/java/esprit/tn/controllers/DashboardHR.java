package esprit.tn.controllers;
import esprit.tn.entities.User;
import esprit.tn.services.QuoteFetcher;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;

import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DashboardHR {

    @FXML
    private VBox contentArea;
    @FXML
    private ImageView image;

    @FXML
    private Text name;
    @FXML
    private Button btnLogout;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnGestionSponsor;
    @FXML
    private Button btnGestionUser;
    @FXML
    private Button btnChangeProfile111;
    @FXML
    private Button btnentretien;
    @FXML
    private HBox navbar;
    @FXML
    private VBox sidebar;
    @FXML
    private Button btnChangeProfile2;
    @FXML
    private Button btnChangeProfile;
    @FXML
    private Button btnChangeProfile1;
    @FXML
    private Button btnChangeProfile11;

    public void setRoute(String s){
        if(s=="evenement"){
            loadPage("/AfficherEvenement.fxml");
        }
    }

    public void initialize() throws SQLException {


        Platform.runLater(() -> {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setMaximized(true);

            // Afficher le popup avec la citation
            showQuotePopup();
        });
        User u = SessionManager.extractuserfromsession();

        if (u != null) {
            name.setText(u.getNom() + " " + u.getPrenom());

            if (u.getImageProfil() != null && !u.getImageProfil().trim().isEmpty()) {
                String correctPath = "C:/xampp/htdocs/img/" + new File(u.getImageProfil()).getName();
                System.out.println(correctPath);
                File imageFile = new File(correctPath);
                if (imageFile.exists() && imageFile.isFile()) {
                    image.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    System.out.println("Image file not found or invalid path: " + imageFile.getAbsolutePath());
                    image.setImage(new Image("/Images/user.png"));
                }
            } else {
                System.out.println("No image path provided.");
                image.setImage(new Image("/Images/user.png"));
            }
        } else {
            System.out.println("No user found in session.");
        }


    }


    public void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Parent newPage = loader.load(); // Use Parent instead of AnchorPane
            contentArea.getChildren().setAll(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*@FXML
    private void logout(ActionEvent event) {
        SessionManager.clearSession();
        try {


            // Get reference to current window using the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

    @FXML
    private void logout(ActionEvent event) {
        try {
            // Nettoyer la session utilisateur
            SessionManager.clearSession();

            // Charger la nouvelle interface de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Scene newScene = new Scene(root);

            // Récupérer la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Appliquer la nouvelle scène
            stage.setScene(newScene);
            stage.setTitle("Login");

            // Utiliser un Platform.runLater() pour s'assurer que la fenêtre est bien affichée avant de modifier sa taille
            Platform.runLater(() -> {
                stage.setMaximized(false); // Désactiver temporairement la maximisation
                stage.setWidth(800); // Taille par défaut
                stage.setHeight(600);
                stage.centerOnScreen();
            });

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }





    @FXML
    public void goprofile(ActionEvent actionEvent) {
        loadPage("/ModifierCompte.fxml");
    }

    @FXML
    public void Effectuersponsor(ActionEvent actionEvent) {
        loadPage("/EvenementSponsor.fxml");

    }

    @FXML
    public void gestionsponsor(ActionEvent actionEvent) {
        loadPage("/AfficherSponsor.fxml");
    }

    @FXML
    public void gestionuser(ActionEvent actionEvent) {
        loadPage("/AfficherUser.fxml");
    }

    @FXML
    public void Effectuerevennement(ActionEvent actionEvent) {
        loadPage("/AfficherEvenement.fxml");
    }

    @FXML
    public void Formation(ActionEvent actionEvent) {
        loadPage("/AfficherFormation.fxml");
    }

    @FXML
    public void entretien(ActionEvent actionEvent) {
        loadPage("/AffichageEntretien.fxml");
    }

    @FXML
    public void offre(ActionEvent actionEvent) {
        loadPage("/AfficherOffre.fxml");


    }

    @FXML
    public void changemdp(MouseEvent mouseEvent) {
        loadPage("/Changermdp.fxml");

    }

    public  void setroute(String s){
        if (s=="event"){
            loadPage("/AfficherEvenement.fxml");


        }
        if (s=="ajoutevent"){
            loadPage("/AjouterEvenement.fxml");


        }
    }

    private void showQuotePopup() {
        // Créer une nouvelle fenêtre (Stage)
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Citation du Jour");

        // Créer un Label pour afficher la citation
        Label quoteLabel = new Label();
        quoteLabel.setWrapText(true);
        quoteLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-style: italic; -fx-padding: 20px;");

        // Créer un Label pour l'auteur de la citation
        Label authorLabel = new Label();
        authorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e0f7fa; -fx-font-weight: bold; -fx-padding: 10px;");

        // Récupérer une citation aléatoire
        String quote = QuoteFetcher.fetchRandomQuote().toString();
        quoteLabel.setText(quote);

        // Simuler un auteur (vous pouvez adapter cela selon vos besoins)
        authorLabel.setText("- " + "Auteur Inconnu");

        // Créer un bouton pour fermer la fenêtre
        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #0288d1; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-radius: 5px;");
        closeButton.setOnAction(e -> popupStage.close());

        // Créer un layout pour le popup
        VBox popupLayout = new VBox(20);
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.getChildren().addAll(quoteLabel, authorLabel, closeButton);
        popupLayout.setStyle("-fx-padding: 30px; -fx-background-color: linear-gradient(to bottom, #039be5, #01579b); -fx-border-color: #0288d1; -fx-border-width: 2px; -fx-border-radius: 10px;");

        // Ajouter une icône ou une image (optionnel)
        ImageView iconView = new ImageView(new Image("file:quote_icon.png")); // Remplacez par le chemin de votre icône
        iconView.setFitHeight(50);
        iconView.setFitWidth(50);
        popupLayout.getChildren().add(0, iconView);

        // Créer une scène et l'ajouter à la fenêtre
        Scene popupScene = new Scene(popupLayout, 500, 300);
        popupStage.setScene(popupScene);

        // Animation de translation (faire glisser la fenêtre depuis le bas)
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), popupLayout);
        translateTransition.setFromY(300); // Départ en bas
        translateTransition.setToY(0);    // Arrivée à la position normale
        translateTransition.play();

        // Animation de zoom (faire grossir la fenêtre)
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), popupLayout);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();

        // Animation de rotation (légère rotation pour un effet visuel)
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.5), popupLayout);
        rotateTransition.setFromAngle(-10); // Rotation de départ
        rotateTransition.setToAngle(0);    // Rotation finale
        rotateTransition.play();

        // Afficher le popup
        popupStage.showAndWait();
    }

}