package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.Imageutil;
import esprit.tn.utils.JwtUtil;
import esprit.tn.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class FaceAuthController {

    @FXML
    private ImageView cameraView;

    private VideoCapture capture;
    private Mat frame;
    private CascadeClassifier faceDetector;
    private boolean running = true;

    private static final String FACE_DIRECTORY = "C:\\xampp\\htdocs\\faces\\";
    private static final double FACE_MATCH_THRESHOLD = 2000.0; // Adjust as needed

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @FXML
    public void initialize() {
        capture = new VideoCapture(0);
        frame = new Mat();
        faceDetector = new CascadeClassifier("C:\\Users\\yassi\\OneDrive\\Documents\\GitHub\\finalone\\safeone\\PidevWorksphere\\target\\classes\\haarcascade_frontalface_alt.xml");

        if (!faceDetector.empty()) {
            System.out.println("Face detector loaded successfully.");
        } else {
            System.out.println("Error loading face detector.");
        }

        startCamera();
        startAuthenticationLoop();
    }

    private void startCamera() {
        Thread cameraThread = new Thread(() -> {
            while (running) {
                if (capture.isOpened()) {
                    capture.read(frame);
                    if (!frame.empty()) {
                        Image image = Imageutil.mat2Image(frame);
                        Platform.runLater(() -> cameraView.setImage(image));
                    }
                }
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    private void startAuthenticationLoop() {
        Thread authThread = new Thread(() -> {
            while (running) {
                try {
                    authenticateUser();
                    Thread.sleep(3000); // Adjust delay as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        authThread.setDaemon(true);
        authThread.start();
    }

    private void authenticateUser() throws SQLException {
        if (!capture.isOpened()) return;

        capture.read(frame);
        File facesDir = new File(FACE_DIRECTORY);
        if (!facesDir.exists() || facesDir.listFiles() == null) {
            System.out.println("Face directory not found or empty!");
            return;
        }

        for (File file : facesDir.listFiles()) {
            Mat storedFace = Imgcodecs.imread(file.getAbsolutePath());
            if (compareFaces(storedFace, frame)) {
                String filename = file.getName(); // Extract file name
                System.out.println("Authentication successful: " + filename);
                handleLogin(filename);
                return;
            }
        }

        System.out.println("Authentication failed.");
    }

    private void handleLogin(String filename) throws SQLException {
        String email = filename.replace(".png", "").trim(); // Extract email from filename

        ServiceUser su = new ServiceUser();
        int userId = su.findidbyemail(email);
        User user = su.findbyid(userId);

        if (user == null) {
            System.out.println("User not found for email: " + email);
            return;
        }

        String token = JwtUtil.generateToken(user.getIdUser(), user.getEmail(), user.getRole());
        SessionManager.setSession(token);

        // Stop camera and close the window
        stop();
        closeWindow();

        // Navigate to dashboard
        navigate(user.getRole().name());


    }
    private void closeWindow() {
        Platform.runLater(() -> {
            Stage stage = (Stage) cameraView.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });
    }

    public void navigate(String role) {
        System.out.println("Navigating to: " + role);
        String fxmlFile = switch (role) {
            case "CANDIDAT" -> "/DashboardCandidat.fxml";
            case "MANAGER" -> "/DashboardManager.fxml";
            case "RH" -> "/DashboardHR.fxml";
            case "EMPLOYE" -> "/DashboardEmploye.fxml";
            default -> {
                showAlert("Navigation Error", "Unknown role: " + role);
                yield null;
            }
        };

        if (fxmlFile == null) return;

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                Parent root = loader.load();
                Stage stage = (Stage) cameraView.getScene().getWindow(); // Get the current stage
                stage.setScene(new Scene(root));
                stage.setTitle(role + " Dashboard");
                stage.show();
            } catch (IOException e) {
                showAlert("Loading Error", "Error loading FXML: " + e.getMessage());
                System.err.println("Error loading FXML: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private boolean compareFaces(Mat storedFace, Mat liveFace) {
        Mat storedGray = new Mat();
        Mat liveGray = new Mat();
        Imgproc.cvtColor(storedFace, storedGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(liveFace, liveGray, Imgproc.COLOR_BGR2GRAY);

        Rect[] storedFaces = detectFaces(storedGray);
        Rect[] liveFaces = detectFaces(liveGray);

        if (storedFaces.length == 0 || liveFaces.length == 0) return false;

        Mat storedROI = new Mat(storedGray, storedFaces[0]);
        Mat liveROI = new Mat(liveGray, liveFaces[0]);

        Imgproc.resize(storedROI, storedROI, new Size(100, 100));
        Imgproc.resize(liveROI, liveROI, new Size(100, 100));

        double diff = Core.norm(storedROI, liveROI);
        return diff < FACE_MATCH_THRESHOLD; // Adjust threshold as needed
    }

    private Rect[] detectFaces(Mat grayImage) {
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faces);
        return faces.toArray();
    }

    public void stop() {
        running = false;
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }

}
