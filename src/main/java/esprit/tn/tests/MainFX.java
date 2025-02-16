package esprit.tn.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args); // Lance l'application JavaFX
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
       // FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEquipe.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjet.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProjet.fxml"));
        Parent root = loader.load();

        // Configurer la scène
        Scene scene = new Scene(root, 600, 400); // Taille de la fenêtre
        primaryStage.setTitle("worksphere"); // Titre de la fenêtre
        primaryStage.setScene(scene);

        // Afficher la fenêtre
        primaryStage.show();
    }
}