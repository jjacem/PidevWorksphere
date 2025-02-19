package esprit.tn.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProjet.fxml"));

        Parent root = loader.load();


        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("worksphere");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}