package esprit.tn.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import javafx.stage.Window;

public class Router {

    public static void navigate() {
        Platform.runLater(() -> {
            Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
            String role = SessionManager.getRole();
            System.out.println("User role: " + role);

            String fxmlFile = switch (role) {
                case "CANDIDAT" -> "/DashboardCandidat.fxml";
                case "MANAGER" -> "/DashboardManager.fxml";
                case "RH" -> "/DashboardHR.fxml";
                case "EMPLOYE" -> "/DashboardEmploye.fxml";
                default -> {
                    showAlert("Navigation Error", "Rôle inconnu: " + role);
                    yield null;
                }
            };

            if (fxmlFile == null) return;

            try {
                FXMLLoader loader = new FXMLLoader(Router.class.getResource(fxmlFile));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(role + " Dashboard");
                stage.show();
            } catch (IOException e) {
                showAlert("Loading Error", "Erreur de chargement de la page: " + e.getMessage());
                System.err.println("Error loading FXML: " + e.getMessage());
            }
        });
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
