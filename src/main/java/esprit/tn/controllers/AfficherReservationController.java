package esprit.tn.controllers;

import esprit.tn.entities.Reservation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AfficherReservationController {

    @FXML
    private ListView<Reservation> listReservation;
    @FXML
    private HBox Vrechcerche;
    @FXML
    private TextField Trecherche;
    @FXML
    private Button Btnrecherche;

    private final ServiceReservation reservationService = new ServiceReservation();

    @FXML
    public void initialize() {
        try {
            ObservableList<Reservation> reservationList = FXCollections.observableArrayList(reservationService.getListReservation());
            listReservation.setItems(reservationList);

            setupListView();
        } catch (SQLException e) {
            e.printStackTrace();
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

                            // Création des éléments d'affichage pour chaque réservation
                             //Label prenomLabel = new Label("Prénom: " + reservation.getUser().getPrenom().toString());
                           //  prenomLabel.setStyle("-fx-font-size: 14px;");

                           //  Label nomLabel = new Label("Nom: " + reservation.getUser().getNom().toString());
                           // nomLabel.setStyle("-fx-font-size: 14px;");

                           // Label mailLabel = new Label("Nom: " + reservation.getUser().getEmail().toString());
                           // nomLabel.setStyle("-fx-font-size: 14px;");

                            // Label formationLabel = new Label("Formation: " + reservation.getFormation().getTitre());
                           //  formationLabel.setStyle("-fx-font-size: 14px;");


                            // 📅 Label pour la date de réservation
                            Label dateLabel = new Label("Date: " + reservation.getDate().toString());
                            dateLabel.setStyle("-fx-font-size: 14px;");

                            Button modifierButton = new Button("Modifier");
                            modifierButton.setStyle("-fx-background-color: #ffc400; -fx-text-fill: white;");
                            modifierButton.setOnAction(event -> modifierButton(reservation));

                            Button supprimerButton = new Button("Supprimer");
                            supprimerButton.setStyle("-fx-background-color: #ff2600; -fx-text-fill: white;");
                            supprimerButton.setOnAction(event -> supprimerButton(reservation));

                            Region spacer = new Region();
                            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                            HBox buttonBox = new HBox(10, modifierButton, supprimerButton);
                            buttonBox.setAlignment(Pos.CENTER_RIGHT);

                            // Conteneur principal avec date + boutons
                            HBox mainBox = new HBox(10, dateLabel, spacer, buttonBox);
                            mainBox.setAlignment(Pos.CENTER_LEFT);
                            mainBox.setPadding(new Insets(10));

                            setGraphic(mainBox);
                        }
                    }

                    private void modifierButton(Reservation reservation) {
                    }

                    private void supprimerButton(Reservation reservation) {
                        ServiceReservation serviceReservation = new ServiceReservation();

                        //  Boîte de dialogue de confirmation
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation de suppression");
                        alert.setHeaderText(null);
                        alert.setContentText("Vous êtes sûr de vouloir supprimer cette réservation ?");

                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            try {
                                serviceReservation.supprimeReservation(reservation);
                                System.out.println("Suppression réussie pour la réservation ID : " + reservation.getId_r());

                                // 🔄 Mise à jour de la ListView après suppression
                                listReservation.getItems().remove(reservation);
                            } catch (SQLException e) {
                                System.err.println("Erreur lors de la suppression : " + e.getMessage());
                            }
                        } else {
                            System.out.println("Suppression annulée par l'utilisateur.");
                        }
                    }
                };
            }
        });

    }
}
