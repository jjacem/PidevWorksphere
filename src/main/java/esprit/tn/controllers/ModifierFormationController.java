package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.services.ServiceFormation;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ModifierFormationController implements Initializable {
    @FXML
    private TextField idtitre;
    @FXML
    private TextField iddesc;
    @FXML
    private ChoiceBox<Typeformation> idtype;
    @FXML
    private DatePicker iddate;
    @FXML
    private ImageView photoPreview;
    @FXML
    private Button modifierfBTN;

    private Formation formation;
    private final ServiceFormation serviceFormation = new ServiceFormation();
    private String imagePath = "";
    @FXML
    private TextField idnbplace;
    @FXML
    private AnchorPane FormMod;
    @FXML
    private TextField idlangue;
    @FXML
    private Button ajouterPhotoBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idtype.setItems(FXCollections.observableArrayList(Typeformation.values()));
    }

    public void setFormation(Formation formation) {
        this.formation = formation;

        idtitre.setText(formation.getTitre());
        iddesc.setText(formation.getDescription());
        idtype.setValue(formation.getType());
        iddate.setValue(formation.getDate());

        if (formation.getPhoto() != null && !formation.getPhoto().trim().isEmpty()) {
            String correctPath = "C:/xampp/htdocs/img/" + new File(formation.getPhoto()).getName();
            File imageFile = new File(correctPath);

            if (imageFile.exists() && imageFile.isFile()) {
                imagePath = correctPath;
                photoPreview.setImage(null);
                photoPreview.setImage(new Image(new File(imagePath).toURI().toString()));
            } else {
                photoPreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
            }
        } else {
            photoPreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
        }
    }

    @FXML
    public void Onmodifierformation(ActionEvent event) {
        try {
            String titre = idtitre.getText();
            String description = iddesc.getText();
            String langue = idlangue.getText();
            Typeformation type = idtype.getValue();
            LocalDate date = iddate.getValue();

            if (titre.isEmpty() || description.isEmpty() || type == null || date == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            if (date.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date ne doit pas être inférieure à aujourd'hui.");
                return;
            }

            formation.setTitre(titre);
            formation.setDescription(description);
            formation.setType(type);
            formation.setDate(date);
            formation.setLangue(langue);
            formation.setPhoto(imagePath);
            formation.setId_user(SessionManager.extractuserfromsession().getIdUser());

            serviceFormation.modifierFormation(formation);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "La formation a été modifiée avec succès.");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur SQL est survenue : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
        applyAlertStyle(alert);
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }

    @FXML
    public void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                File uploadDir = new File("C:/xampp/htdocs/img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = "img/" + fileName;
                photoPreview.setImage(new Image(destinationFile.toURI().toString()));
            } catch (Exception e) {
                System.out.println("Erreur lors de l'ajout de la photo");
            }
        }
    }
}