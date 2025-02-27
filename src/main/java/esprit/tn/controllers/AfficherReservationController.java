package esprit.tn.controllers;

import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
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
    private ListView<Reservation> listReservation;

    private final ServiceReservation reservationService = new ServiceReservation();

    @FXML
    public void initialize() {
        try {
            // Récupérer l'utilisateur connecté
            User user = SessionManager.extractuserfromsession();
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Utilisateur non trouvé. Veuillez vous reconnecter.");
                return;
            }

            // Récupérer les réservations faites par l'utilisateur et celles sur ses formations
            ObservableList<Reservation> reservationList = FXCollections.observableArrayList(
                    reservationService.getReservationsByUser(user.getIdUser())
            );

            listReservation.setItems(reservationList);
            setupListView();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réservations.");
        }
    }

    private void setupListView() {
        listReservation.setCellFactory(new Callback<ListView<Reservation>, ListCell<Reservation>>() {
            @Override
            public ListCell<Reservation> call(ListView<Reservation> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Reservation reservation, boolean empty) {
                        super.updateItem(reservation, empty);

                        if (empty || reservation == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label dateLabel = new Label("📅 Date : " + reservation.getDate());
                            Label motifLabel = new Label("📝 Motif : " + reservation.getMotif());
                            Label attenteLabel = new Label("⏳ Statut : " + reservation.getAttente());

                            Label userLabel = new Label("👤 Réservé par ID : " + reservation.getUserId());
                            Label formationLabel = new Label("📚 Formation ID : " + reservation.getFormationId());

                            Button supprimerButton = new Button("❌ Supprimer");
                            supprimerButton.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white;");
                            supprimerButton.setOnAction(event -> supprimerReservation(reservation));

                            VBox vbox = new VBox(5, dateLabel, motifLabel, attenteLabel, userLabel, formationLabel, supprimerButton);
                            vbox.setPadding(new Insets(10));

                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
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
                listReservation.getItems().remove(reservation);
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
