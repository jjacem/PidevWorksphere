package esprit.tn.controllers;
import esprit.tn.entities.User;
import javafx.scene.Node;
import javafx.scene.Parent;

import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

    @FXML
    public void goprofile(ActionEvent actionEvent) {
        loadPage("/ModifierCompte.fxml");
    }

    @FXML
    public void Effectuersponsor(ActionEvent actionEvent) {
        loadPage("/Sponsor_events.fxml");

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
}