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

public class DashboardCandidat {

    @FXML
    private VBox contentArea;

    @FXML
    private ImageView image;

    @FXML
    private Text name;

    @FXML
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
        loadPage("/AfficherCandidature.fxml");
    }

    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Parent newPage = loader.load();
            contentArea.getChildren().setAll(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        SessionManager.clearSession();
        try {
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

    public void offreemploi(ActionEvent actionEvent) {
        // Add logic if needed
    }

    public void reclamation(ActionEvent actionEvent) {
        loadPage("/AfficherReclamations.fxml");
    }

    public void candidature(ActionEvent actionEvent) {
        loadPage("/AfficherCandidature.fxml");
    }

    public void changemdp(ActionEvent actionEvent) {
        loadPage("/Changermdp.fxml");
    }

    // New reload function
    public void reloadDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardCandidat.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Candidat");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error reloading DashboardCandidat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Static method to reload from other controllers without an ActionEvent
    public static void reloadDashboardFromStage(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(DashboardManager.class.getResource("/DashboardManager.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard Manager");
            stage.setMaximized(true); // Set to full-screen (maximized)
            stage.show();
        } catch (IOException e) {
            System.err.println("Error reloading DashboardManager: " + e.getMessage());
            e.printStackTrace();
        }
    }}