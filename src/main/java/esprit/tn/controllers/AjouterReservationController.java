package esprit.tn.controllers;

import esprit.tn.entities.Langue;
import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReservation;
import esprit.tn.services.ServiceFormation;
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
import java.sql.SQLException;
import java.time.LocalDate;

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
    private int nbPlacesMax; // Nombre de places disponibles
    private final ServiceReservation reservationService = new ServiceReservation();
    private final ServiceFormation serviceFormation = new ServiceFormation(); // Service pour récupérer les formations

    @FXML
    public void initialize() {
        try {
            // Récupérer l'utilisateur connecté
            User user = SessionManager.extractuserfromsession();
            if (user != null) {
                nomID.setText(user.getNom());
                prenomID.setText(user.getPrenom());
                emailID.setText(user.getEmail());
                dateID.setValue(LocalDate.now());
                userId = user.getIdUser();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé. Veuillez vous reconnecter.");
            }

            // Remplir la ComboBox avec les langues disponibles
            langid.setItems(FXCollections.observableArrayList(Langue.values()));
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

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'initialisation : " + e.getMessage());
        }
    }

    public void setFormationId(int formationId) {
        this.formationId = formationId;
        checkAvailability(); // Vérifie la disponibilité dès qu'on définit la formation
    }

    private void checkAvailability() {
        try {
            // Récupérer le nombre total de places pour la formation
            nbPlacesMax = serviceFormation.getNombrePlaces(formationId);

            // Récupérer le nombre de réservations déjà effectuées pour cette formation
            int reservationsCount = reservationService.getNombreReservations(formationId);

            // Désactiver le bouton si le nombre de réservations atteint le maximum
            if (reservationsCount >= nbPlacesMax) {
                btnReservation.setDisable(true);
                showAlert(Alert.AlertType.WARNING, "Complet", "Cette formation est complète, aucune réservation possible.");
            } else {
                btnReservation.setDisable(false);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Impossible de vérifier la disponibilité de la formation : " + e.getMessage());
        }
    }

    @FXML
    public void Onajouterreservation(ActionEvent actionEvent) {
        try {
            if (userId == 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé. Veuillez vous reconnecter.");
                return;
            }

            // Vérification des champs obligatoires
            if (prenomID.getText().isEmpty() || nomID.getText().isEmpty() || emailID.getText().isEmpty() || dateID.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            if (langid.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une langue.");
                return;
            }

            // Création de l'objet réservation
            Reservation reservation = new Reservation();
            reservation.setDate(dateID.getValue());
            reservation.setUserId(userId);
            reservation.setFormationId(formationId);
            reservation.setMotif(motifID.getText());
            reservation.setAttente(attenteID.getText());
            reservation.setLang(langid.getValue());

            // Ajout de la réservation
            reservationService.ajouterReservation(reservation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation ajoutée avec succès.");

            // Vérifier à nouveau la disponibilité après l'ajout
            checkAvailability();

            redirigerVersListeReservations(actionEvent);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur SQL s'est produite : " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    @FXML
    public void Onback(ActionEvent actionEvent) {
        chargerPage(actionEvent, "/AfficherListFormation.fxml", "Impossible de charger la page des formations.");
    }

    private void redirigerVersListeReservations(ActionEvent actionEvent) {
        chargerPage(actionEvent, "/AfficherReservation.fxml", "Impossible de charger la liste des réservations.");
    }

    private void chargerPage(ActionEvent actionEvent, String fxmlPath, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", errorMessage + " " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyAlertStyle(alert);
        alert.showAndWait();
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}
