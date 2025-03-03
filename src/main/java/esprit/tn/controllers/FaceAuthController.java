package esprit.tn.controllers;

import esprit.tn.utils.Imageutil;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import java.io.File;

public class FaceAuthController {

    @FXML
    private ImageView cameraView;

    private VideoCapture capture;
    private Mat frame;
    private CascadeClassifier faceDetector;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @FXML
    public void initialize() {
        capture = new VideoCapture(0);
        frame = new Mat();
        faceDetector = new CascadeClassifier("C:\\Users\\yassi\\OneDrive\\Documents\\GitHub\\finalone\\safeone\\PidevWorksphere\\target\\classes\\haarcascade_frontalface_alt.xml");
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
    private void authenticateUser() {
        if (!capture.isOpened()) return;

        capture.read(frame);
        File facesDir = new File("faces");
        if (!facesDir.exists()) {
            System.out.println("Face directory not found!");
            return;
        }

        for (File file : facesDir.listFiles()) {
            Mat storedFace = Imgcodecs.imread(file.getAbsolutePath());
            if (compareFaces(storedFace, frame)) {
                System.out.println("Authentication successful: " + file.getName());
                return;
            }
        }

        System.out.println("Authentication failed.");
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
        return diff < 2000; // Adjust threshold as needed
    }

    private Rect[] detectFaces(Mat grayImage) {
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faces);
        return faces.toArray();
    }

    public void stop() {
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }
}
