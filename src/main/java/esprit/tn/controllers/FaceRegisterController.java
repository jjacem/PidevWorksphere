package esprit.tn.controllers;

import esprit.tn.utils.Imageutil;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import java.io.File;
import javafx.stage.Stage;

public class FaceRegisterController {

    private String mail;

    public void setting(String email){
        this.mail = email;
    }

    @FXML
    private ImageView cameraView;

    private VideoCapture capture;
    private Mat frame;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @FXML
    public void initialize() {
        capture = new VideoCapture(0);
        frame = new Mat();
        startCamera();
    }

    private void startCamera() {
        Thread cameraThread = new Thread(() -> {
            while (true) {
                if (capture.isOpened()) {
                    capture.read(frame);
                    if (!frame.empty()) {
                        Image image = Imageutil.mat2Image(frame);
                        javafx.application.Platform.runLater(() -> cameraView.setImage(image));
                    }
                }
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    @FXML
    private void registerFace() {
        if (!capture.isOpened()) return;

        capture.read(frame);
        String outputPath = "C:\\xampp\\htdocs\\faces/" + mail + ".png";

        File dir = new File("C:\\xampp\\htdocs\\faces/");
        if (!dir.exists()) dir.mkdirs();

        Imgcodecs.imwrite(outputPath, frame);
        System.out.println("Face registered: " + outputPath);

        // Close the window
        Stage stage = (Stage) cameraView.getScene().getWindow();
        stop(); // Release camera resources
        stage.close();
    }

    public void stop() {
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }
}