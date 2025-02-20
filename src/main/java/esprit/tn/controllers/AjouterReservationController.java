package esprit.tn.controllers;

import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReservation;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterReservationController {
    @FXML
    private TextField prenomID;
    @FXML
    private TextField nomID;
    @FXML
    private DatePicker dateID;
    @FXML
    private TextField emailID;
    @FXML
    private Button btnReservation;
    private int userId;
    private int formationId;
    @FXML
    private Button btnback;
    @FXML
    public void initialize() {
        dateID.setValue(LocalDate.now());
    }

    // Setter pour l'utilisateur afin de remplir les champs du formulaire
    public void setUser(User user) {
        nomID.setText(user.getNom());
        prenomID.setText(user.getPrenom());
        emailID.setText(user.getEmail());
    }

    // Setter pour l'ID utilisateur
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Setter pour l'ID de la formation
    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }

    private final ServiceReservation reservationService = new ServiceReservation();

    @FXML
    public void Onajouterreservation(ActionEvent actionEvent) throws SQLException {
        // Vérification des champs
        if (prenomID.getText().isEmpty() || nomID.getText().isEmpty() || emailID.getText().isEmpty() || dateID.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        // Récupérer les valeurs des champs
        String prenom = prenomID.getText();
        String nom = nomID.getText();
        String email = emailID.getText();
        LocalDate date = dateID.getValue();

        // Créer une instance de réservation
        Reservation reservation = new Reservation();
        reservation.setDate(date);
        reservation.setUserId(SessionManager.extractuserfromsession().getIdUser()); // Utiliser l'ID de l'utilisateur passé par le contrôleur
        reservation.setFormationId(formationId); // Utiliser l'ID de la formation

        // Appel au service pour ajouter la réservation
        try {
            reservationService.ajouterReservation(reservation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès.");

            // Redirection vers ListReservation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReservation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnReservation.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }


    // Méthode pour afficher une alerte avec un type, un titre et un message
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherListFormation.fxml"));
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
