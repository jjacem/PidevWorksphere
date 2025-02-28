package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Typeformation;
import esprit.tn.services.ServiceFormation;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ModifierFormationController implements Initializable {
    @FXML
    private TextField idtitre;
    @FXML
    private TextField iddesc;
    @FXML
    private ChoiceBox<Typeformation> idtype;
    @FXML
    private TextField idheuredebut;
    @FXML
    private TextField idnbplace;
    @FXML
    private DatePicker iddate;
    @FXML
    private TextField idheurefin;


    private Formation formation;
    private final ServiceFormation serviceFormation = new ServiceFormation();
    @FXML
    private AnchorPane FormMod;
    @FXML
    private Button modifierfBTN;
    @FXML
    private ImageView photoPreview;
    @FXML
    private Button ajouterPhotoBtn;

    private String imagePath; // Stocke le chemin de l'image sélectionnée
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idtype.setItems(FXCollections.observableArrayList(Typeformation.values()));
    }
    public void setFormation(Formation formation) {
        this.formation = formation;

        idtitre.setText(formation.getTitre());
        iddesc.setText(formation.getDescription());
        idtype.setValue(formation.getType());
        idheuredebut.setText(formation.getHeure_debut().toString());
        idheurefin.setText(formation.getHeure_fin().toString());
        idnbplace.setText(String.valueOf(formation.getNb_place()));
        iddate.setValue(formation.getDate());

        // Gestion de l'image de la formation
        if (formation.getPhoto() != null && !formation.getPhoto().trim().isEmpty()) {
            String correctPath = "C:/xampp/htdocs/img/" + new File(formation.getPhoto()).getName();
            File imageFile = new File(correctPath);

            if (imageFile.exists() && imageFile.isFile()) {
                photoPreview.setImage(new Image(imageFile.toURI().toString())); // Afficher l'image existante
            } else {
                photoPreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png"))); // Image par défaut
            }
            imagePath = formation.getPhoto(); // Conserver le chemin de l'image
        } else {
            photoPreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png"))); // Image par défaut
        }
    }

    @FXML
    public void OnModifierPhoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            imagePath = file.getAbsolutePath(); // Met à jour le chemin de l'image
            photoPreview.setImage(new Image(file.toURI().toString())); // Afficher la nouvelle image
        }
    }

    @FXML
    public void Onmodifierformation(ActionEvent event) {
        try {
            String titre = idtitre.getText();
            String description = iddesc.getText();
            Typeformation type = idtype.getValue();
            LocalDate date = iddate.getValue();
            LocalTime heureDebut = LocalTime.parse(idheuredebut.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime heureFin = LocalTime.parse(idheurefin.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            int nbPlace = Integer.parseInt(idnbplace.getText());

            if (titre.isEmpty() || description.isEmpty() || type == null || date == null || heureDebut == null || heureFin == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date ne doit pas être inférieure à aujourd'hui.");
                return;
            }

            // Mise à jour de la formation
            formation.setTitre(titre);
            formation.setDescription(description);
            formation.setType(type);
            formation.setDate(date);
            formation.setHeure_debut(heureDebut);
            formation.setHeure_fin(heureFin);
            formation.setNb_place(nbPlace);
            formation.setPhoto(imagePath);
            formation.setId_user(SessionManager.extractuserfromsession().getIdUser());
            serviceFormation.modifierFormation(formation);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "La formation a été modifiée avec succès.");

            // Redirection
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFormation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide pour les places et respecter le format HH:mm pour les heures.");
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


}
