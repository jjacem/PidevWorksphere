package esprit.tn.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DashboardHR.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Afficher Sponsor");

            // Ouvrir la fenêtre en plein écran
            primaryStage.setMaximized(true); // Maximiser la fenêtre
            // primaryStage.setFullScreen(true); // Décommenter cette ligne si tu veux le mode plein écran total

            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
