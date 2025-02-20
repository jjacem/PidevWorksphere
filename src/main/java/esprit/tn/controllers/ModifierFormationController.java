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
import javafx.stage.Stage;

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
    private TextField idphoto;
    @FXML
    private TextField idheurefin;

    private Formation formation;
    private final ServiceFormation serviceFormation = new ServiceFormation();
    @FXML
    private Button btnretour;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idtype.setItems(FXCollections.observableArrayList(Typeformation.values()));
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        // Remplissage des champs avec les valeurs actuelles
        idtitre.setText(formation.getTitre());
        iddesc.setText(formation.getDescription());
        idtype.setValue(formation.getType());
        idheuredebut.setText(formation.getHeure_debut().toString());
        idheurefin.setText(formation.getHeure_fin().toString());
        idnbplace.setText(String.valueOf(formation.getNb_place()));
        iddate.setValue(formation.getDate());
        idphoto.setText(formation.getPhoto() != null ? formation.getPhoto().toString() : "");
    }

    @FXML
    public void Onmodifierformation(ActionEvent event) {
        try {
            // Récupération des données mises à jour
            String titre = idtitre.getText();
            String description = iddesc.getText();
            Typeformation type = idtype.getValue();
            LocalDate date = iddate.getValue();
            LocalTime heureDebut = LocalTime.parse(idheuredebut.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime heureFin = LocalTime.parse(idheurefin.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            int nbPlace = Integer.parseInt(idnbplace.getText());
            String photoUrl = idphoto.getText();

            // Vérification des champs obligatoires
            if (titre.isEmpty() || description.isEmpty() || type == null || date == null || heureDebut == null || heureFin == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
                return;
            }
            if (date.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date ne doit pas être inférieure à aujourd'hui.");
                return;
            }



            // Vérification de l'URL de la photo
            URL photo = null;
            try {
                photo = new URL(photoUrl);
            } catch (MalformedURLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "URL de la photo invalide !");
                return;
            }

            int userId = 1;
            // Mise à jour de l'objet formation
            formation.setTitre(titre);
            formation.setDescription(description);
            formation.setType(type);
            formation.setDate(date);
            formation.setHeure_debut(heureDebut);
            formation.setHeure_fin(heureFin);
            formation.setNb_place(nbPlace);
            formation.setPhoto(photo);
            formation.setId_user(SessionManager.extractuserfromsession().getIdUser());

            // Appel du service pour modifier la formation
            serviceFormation.modifierFormation(formation);

            // Affichage du message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La formation a été modifiée avec succès.");

            // Redirection vers la liste des formations
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
    }

    @FXML
    public void Onback(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFormation.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page : " + e.getMessage());
        }
    }
}
