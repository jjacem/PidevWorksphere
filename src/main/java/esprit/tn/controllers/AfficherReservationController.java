package esprit.tn.controllers;

import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
import esprit.tn.services.JitsiMeetService;
import esprit.tn.services.ServiceReservation;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.Optional;

public class AfficherReservationController {

    @FXML
    private VBox listReservation;

    private final ServiceReservation reservationService = new ServiceReservation();
    private final JitsiMeetController jit = new JitsiMeetController();
    private final JitsiMeetService meet = new JitsiMeetService();
    @FXML
    public void initialize() {
        try {
            // Récupérer l'utilisateur connecté
            User user = SessionManager.extractuserfromsession();
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé. Veuillez vous reconnecter.");
                return;
            }

            // Récupérer les réservations faites par l'utilisateur
            ObservableList<Reservation> reservationList = FXCollections.observableArrayList(
                    reservationService.getReservationsByUser(user.getIdUser())
            );

            afficherReservations(reservationList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réservations.");
        }
    }

    private void afficherReservations(ObservableList<Reservation> reservations) {
        listReservation.getChildren().clear();
        for (Reservation reservation : reservations) {
            VBox reservationBox = createReservationBox(reservation);
            listReservation.getChildren().add(reservationBox);
        }
    }

    private VBox createReservationBox(Reservation reservation) {
        Label dateLabel = new Label("📅 Date : " + reservation.getDate());
        dateLabel.getStyleClass().addAll("label-detail", "label-detail");
        Label motifLabel = new Label("📝 Motif : " + reservation.getMotif());
        motifLabel.getStyleClass().addAll("label-detail", "label-detail");
        Label attenteLabel = new Label("⏳ Attente de la formation : " + reservation.getAttente());
        attenteLabel.getStyleClass().addAll("label-detail", "label-detail");

        // Afficher le nom de l'utilisateur au lieu de son ID
        Label userLabel = new Label("👤 Réservé par : " + reservation.getUser().getNom() +" "+ reservation.getUser().getPrenom());
        userLabel.getStyleClass().addAll("label-detail", "label-detail");

        // Afficher le titre de la formation au lieu de son ID
        Label formationLabel = new Label("📚 Formation : " + reservation.getFormation().getTitre());
        formationLabel.getStyleClass().addAll("label-detail", "label-detail");

        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().addAll("card-button", "supprimer-button");
        supprimerButton.setOnAction(event -> supprimerReservation(reservation));

        Button rejoindreButton = new Button("Rejoindre le meet");
        rejoindreButton.getStyleClass().addAll("card-button", "details-button");
        rejoindreButton.setOnAction(event -> jit.rejoindreMeeting(reservation.getId_r()));

        VBox vbox = new VBox(5, dateLabel, motifLabel, attenteLabel, userLabel, formationLabel, supprimerButton ,rejoindreButton);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-radius: 5px;");

        return vbox;
    }

    private void supprimerReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer cette réservation ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                reservationService.supprimeReservation(reservation);
                listReservation.getChildren().removeIf(node -> node instanceof VBox && ((VBox) node).getChildren().contains(new Label("📅 Date : " + reservation.getDate())));
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la réservation.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}