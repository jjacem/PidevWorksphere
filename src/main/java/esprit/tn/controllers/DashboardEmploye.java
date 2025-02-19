package esprit.tn.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
public class DashboardEmploye {
    @FXML
    private AnchorPane contentPane;

    private void loadPage(String page) {
        try {
            AnchorPane newPage = FXMLLoader.load(getClass().getResource(page));
            contentPane.getChildren().setAll(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadProfile() {
        loadPage("/ModifierCompte.fxml");
    }

    @FXML
    private void loadPassword() {
        System.out.println("loadPassword");
     }

    @FXML
    private void loadOffreEmploi() {
        System.out.println("does work");

    }

    @FXML
    private void loadReclamation() {
        loadPage("/AfficherReclamations.fxml");
    }
}
