package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AjouterUser {
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private TextField email;
    @FXML
    private PasswordField mdp;
    @FXML
    private TextField adresse;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu;
    @FXML
    private ImageView imagePreview;
    @FXML
    private MediaView mediaView;
    private String imagePath = "";

    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Pattern numericPattern = Pattern.compile("\\d+(\\.\\d+)?");

    @FXML
    public void initialize() {
        // Populate the ChoiceBox with Sexe enum values
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);

        // Load and play the video
        try {
            // Ensure the video file is in src/main/resources/videos/signup.mp4
            URL mediaUrl = getClass().getResource("/videos/signup.mp4");
            if (mediaUrl == null) {
                System.err.println("Error: Video file not found at /videos/signup.mp4");
                showAlert(Alert.AlertType.ERROR, "Video Error", "Video file not found in resources.");
                return;
            }

            System.out.println("Video found at: " + mediaUrl.toExternalForm());
            String videoPath = mediaUrl.toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Add listener to monitor MediaPlayer status
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                System.out.println("MediaPlayer Status: " + newStatus);
                if (newStatus == MediaPlayer.Status.STALLED || newStatus == MediaPlayer.Status.HALTED) {
                    System.err.println("Media playback failed: " + mediaPlayer.getError());
                    showAlert(Alert.AlertType.ERROR, "Playback Error", "Failed to play video: " + mediaPlayer.getError());
                }
            });

            // Configure MediaPlayer
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            // Bind MediaPlayer to MediaView
            mediaView.setMediaPlayer(mediaPlayer);
            mediaView.setPreserveRatio(true); // Maintain aspect ratio
        } catch (Exception e) {
            System.err.println("Failed to load or play media: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Media Error", "Failed to load video: " + e.getMessage());
        }
    }

    @FXML
    public void ajoutercandidat(ActionEvent actionEvent) {
        if (!validateInputs()) {
            return;
        }
        showCaptchaPopup(actionEvent);
    }

    private void showCaptchaPopup(ActionEvent actionEvent) {
        try {
            Stage captchaStage = new Stage();
            captchaStage.initModality(Modality.APPLICATION_MODAL);
            captchaStage.initStyle(StageStyle.UTILITY);
            captchaStage.setTitle("Complete hCaptcha");

            WebView captchaWebView = new WebView();
            WebEngine webEngine = captchaWebView.getEngine();
            webEngine.load("https://accounts.hcaptcha.com/demo?sitekey=3bde0e2e-31d0-4140-bf90-10b6a89c299c");

            Button verifyButton = new Button("Verify");
            verifyButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px;");
            verifyButton.setOnAction(e -> {
                String captchaResponse = (String) webEngine.executeScript("hcaptcha.getResponse();");
                if (captchaResponse == null || captchaResponse.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Captcha Error", "Please complete the captcha.");
                } else {
                    verifyHcaptcha(captchaResponse, captchaStage, actionEvent);
                }
            });

            VBox layout = new VBox(10, captchaWebView, verifyButton);
            layout.setPadding(new Insets(20));
            layout.setStyle("-fx-background-color: #f4f4f4;");

            Scene scene = new Scene(layout, 600, 450);
            captchaStage.setScene(scene);
            captchaStage.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load hCaptcha.");
        }
    }

    private void verifyHcaptcha(String captchaResponse, Stage captchaStage, ActionEvent actionEvent) {
        try {
            URL url = new URL("https://hcaptcha.com/siteverify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String postData = "secret=ES_5c4045e58ba8477298cf1864401501e5&response=" + captchaResponse;
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes());
                os.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getBoolean("success")) {
                captchaStage.close();
                registerUser(actionEvent);
            } else {
                showAlert(Alert.AlertType.ERROR, "Captcha Error", "Captcha verification failed.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Captcha Error", "Failed to verify captcha.");
        }
    }

    private void registerUser(ActionEvent actionEvent) {
        try {
            double salaire = Double.parseDouble(salaireAttendu.getText());
            User candidat = new User(
                    nom.getText(),
                    prenom.getText(),
                    email.getText(),
                    mdp.getText(),
                    adresse.getText(),
                    sexe.getValue(),
                    imagePath,
                    salaire
            );

            ServiceUser serviceUser = new ServiceUser();
            serviceUser.ajouter(candidat);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Votre compte a été créé!");
            alert.showAndWait();
            redirectToLogin(actionEvent);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add user: " + e.getMessage());
        }
    }

    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);

                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = "htdocs/images/" + fileName;
                imagePreview.setImage(new Image(destinationFile.toURI().toString()));
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Failed to upload image.");
            }
        }
    }

    private void redirectToLogin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login page.");
        }
    }

    private boolean validateInputs() {
        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                mdp.getText().isEmpty() || adresse.getText().isEmpty() || sexe.getValue() == null ||
                salaireAttendu.getText().isEmpty() || imagePath.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields.");
            return false;
        }

        if (!emailPattern.matcher(email.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return false;
        }

        if (!numericPattern.matcher(salaireAttendu.getText()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Salary", "Please enter a valid numerical salary.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void retourner(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}