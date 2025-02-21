package esprit.tn.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
        try {
            Parent root =fxmlLoader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("afficher sponsor");
            primaryStage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
