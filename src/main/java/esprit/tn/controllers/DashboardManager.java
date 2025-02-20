package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DashboardManager {

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

    public void loadPage(String page) {
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

    public void Equipe(ActionEvent actionEvent) {
        loadPage("/AfficherEquipe.fxml");

    }

    public void Projet(ActionEvent actionEvent) {
        loadPage("/AfficherProjet.fxml");
    }


    public void chagermdp(ActionEvent actionEvent) {
        loadPage("/Changermdp.fxml");

    }
}
