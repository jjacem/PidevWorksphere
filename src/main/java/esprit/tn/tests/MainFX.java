package esprit.tn.tests;

import esprit.tn.controllers.FaceAuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

public class MainFX extends Application {

    static {
        System.load("C:\\Users\\yassi\\OneDrive\\Desktop\\opencv\\build\\java\\x64\\opencv_java490.dll");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
//            Parent root = FXMLLoader.load(getClass().getResource("/faceregister.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Face Authentication");

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        FaceAuthController authController = new FaceAuthController();
        authController.stop();
    }
}



















//
//package esprit.tn.tests;
//
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//
//public class MainFX extends Application {
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
//        try {
//            Parent root =fxmlLoader.load();
//            Scene scene = new Scene(root);
//            primaryStage.setScene(scene);
//            primaryStage.setTitle("afficher sponsor");
//            primaryStage.show();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//
//    }
//}
//
//

