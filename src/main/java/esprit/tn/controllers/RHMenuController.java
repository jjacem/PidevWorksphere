package esprit.tn.controllers;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
public class RHMenuController {
    @FXML private VBox contentArea;
    @FXML private TextField searchField;
    @FXML private Button btnProfile, btnChangeProfile, btnGestionSponsor, btnGestionUser, btnLogout;

    @FXML
    public void initialize() {
        // Set event handlers for buttons
        btnProfile.setOnAction(event -> loadPage("profile.fxml"));
        btnChangeProfile.setOnAction(event -> loadPage("change_profile.fxml"));
        btnGestionSponsor.setOnAction(event -> loadPage("gestion_sponsor.fxml"));
        btnGestionUser.setOnAction(event -> loadPage("gestion_user.fxml"));
        btnLogout.setOnAction(event -> logout());
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading page: " + fxmlFile);
        }
    }

    private void logout() {
        SessionManager.clearSession();
        // Add logout logic here (clear session, navigate to login screen)
    }

    public void goprofile(ActionEvent actionEvent) {

    }

    public void modiferprofime(ActionEvent actionEvent) {
    }

    public void gestionsponsor(ActionEvent actionEvent) {
    }

    public void gestionuser(ActionEvent actionEvent) {

    }
}