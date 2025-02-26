package esprit.tn.controllers;

import esprit.tn.entities.Langue;
import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReservation;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjouterReservationController {

    @FXML private TextField prenomID;
    @FXML private TextField nomID;
    @FXML private DatePicker dateID;
    @FXML private TextField emailID;
    @FXML private Button btnReservation;
    @FXML private Button btnback;
    @FXML private TextArea attenteID;
    @FXML private ComboBox<Langue> langid;
    @FXML private TextArea motifID;

    private int userId;
    private int formationId;

    private final ServiceReservation reservationService = new ServiceReservation();


    @FXML
    public void initialize() {
        langid.setItems(FXCollections.observableArrayList(Langue.values()));

        // S'assurer que le texte de l'énumération est bien affiché
        langid.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Langue langue, boolean empty) {
                super.updateItem(langue, empty);
                setText(empty ? null : langue.name());
            }
        });

        langid.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Langue langue, boolean empty) {
                super.updateItem(langue, empty);
                setText(empty ? "Sélectionner une langue" : langue.name());
            }
        });
    }

    // Setter pour remplir le formulaire avec les infos de l'utilisateur
    public void setUser(User user) {
        if (user != null) {
            nomID.setText(user.getNom());
            prenomID.setText(user.getPrenom());
            emailID.setText(user.getEmail());
            dateID.setValue(LocalDate.now());
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }

    @FXML
    public void Onajouterreservation(ActionEvent actionEvent) throws SQLException {
        // Vérifier si un utilisateur est bien connecté
        User user = SessionManager.extractuserfromsession();
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé. Veuillez vous reconnecter.");
            return;
        }

        // Vérification des champs obligatoires
        if (prenomID.getText().isEmpty() || nomID.getText().isEmpty() || emailID.getText().isEmpty() || dateID.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Vérification de la langue sélectionnée
        if (langid.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une langue.");

            applyAlertStyle(alert); // Appliquer le style avant d'afficher l'alerte

            alert.showAndWait();
            return;
        }
        // Création de l'objet réservation
        Reservation reservation = new Reservation();
        reservation.setDate(dateID.getValue());
        reservation.setUserId(user.getIdUser());
        reservation.setFormationId(formationId);
        reservation.setMotif(motifID.getText());
        reservation.setAttente(attenteID.getText());
        reservation.setLang(langid.getValue());

        // Ajout de la réservation
        try {
            reservationService.ajouterReservation(reservation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès.");
            redirigerVersListeReservations(actionEvent);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    @FXML
    public void Onback(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherListFormation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page : " + e.getMessage());
        }
    }

    private void redirigerVersListeReservations(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReservation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {

            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des réservations : " + e.getMessage());

        }
    }

    // Méthode utilitaire pour afficher une alerte
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
