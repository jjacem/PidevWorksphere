package esprit.tn.controllers;

import esprit.tn.utils.Imageutil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import java.io.File;

public class FaceRegisterController {

    @FXML
    private ImageView cameraView;
    @FXML
    private TextField usernameField;

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
        if (!capture.isOpened() || usernameField.getText().isEmpty()) return;

        capture.read(frame);
        String username = usernameField.getText();
        String outputPath = "faces/" + username + ".png";

        File dir = new File("faces/");
        if (!dir.exists()) dir.mkdirs();

        Imgcodecs.imwrite(outputPath, frame);
        System.out.println("Face registered: " + outputPath);
    }

    public void stop() {
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }
}
