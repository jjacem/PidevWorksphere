package esprit.tn.controllers;
import esprit.tn.entities.User;
import esprit.tn.utils.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DashboardEmploye {
    @FXML
    private VBox contentArea;
    @FXML
    private ImageView image;

    @FXML
    private Text name;
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnGestionUser11;
    @FXML
    private Button btnGestionUser1;
    @FXML
    private Button btnGestionUser;
    @FXML
    private Button btnGestionUser111;
    @FXML
    private HBox navbar;
    @FXML
    private VBox sidebar;
    @FXML
    private Button btnGestionFormation;
    @FXML
    private Button btnChangeProfile;

    public void initialize() throws SQLException {

        Platform.runLater(() -> {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setMaximized(true);
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


    private void loadPage(String page) {
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
    public void formation(ActionEvent actionEvent) {
        loadPage("/AfficherListFormation.fxml");

    }

    public void gestionsponsor(ActionEvent actionEvent) {
        loadPage("/AfficherSponsor.fxml");
    }

    public void gestionuser(ActionEvent actionEvent) {
        loadPage("/AfficherUser.fxml");
    }

    public void Effectuerevennement(ActionEvent actionEvent) {
        loadPage("/AfficherEvenement.fxml");
    }
@FXML
public void Formation(ActionEvent actionEvent) {
        loadPage("/AfficherListFormation.fxml");
    }

    @FXML
    public void Reclamation(ActionEvent actionEvent) {
        loadPage("/AfficherReclamations.fxml");
    }

    public void evenement(ActionEvent actionEvent) {
        loadPage("/AfficherEvenement.fxml");
    }

    @FXML
    public void entretien(ActionEvent actionEvent) {
        loadPage("/AffichageEntretienbyemployeeId.fxml");
    }

    @FXML
    public void chng(ActionEvent actionEvent) {
        loadPage("/Changermdp.fxml");
    }
    @FXML
    public void mesreservastion(ActionEvent actionEvent) {
        loadPage("/AfficherReservation.fxml");
    }
}
