package esprit.tn.controllers;
import esprit.tn.entities.User;
import javafx.scene.Node;
import javafx.scene.Parent;

import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    public void initialize() throws SQLException {
        User u = SessionManager.extractuserfromsession();

        if (u.getImageProfil() != null && !u.getImageProfil().isEmpty()) {
            File imageFile = new File(u.getImageProfil());
            if (imageFile.exists()) {
                this.image.setImage(new Image(imageFile.toURI().toString()));
            } else {
                System.out.println("Image file not found: " + u.getImageProfil());
            }
        }

        name.setText(u.getNom() + " " + u.getPrenom());
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



    @FXML
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
    }

    public void goprofile(ActionEvent actionEvent) {
        loadPage("/ModifierCompte.fxml");
    }

    public void Effectuersponsor(ActionEvent actionEvent) {
        loadPage("/Sponsor_events.fxml");

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

    public void Formation(ActionEvent actionEvent) {
        loadPage("/AfficherFormation.fxml");
    }

    public void entretien(ActionEvent actionEvent) {
        loadPage("/AffichageEntretien.fxml");
    }

    public void offre(ActionEvent actionEvent) {
        loadPage("/AfficherOffre.fxml");


    }

    public void changemdp(MouseEvent mouseEvent) {
        loadPage("/Changermdp.fxml");

    }
}
