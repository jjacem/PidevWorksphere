package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.Role;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherEvenementController {

    @FXML
    private ListView<Evenement> lv_event;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TextField txtRechercheEvent;

    private ObservableList<Evenement> observableList;

    @FXML
    void initialize() {
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
            List<Evenement> evenementList = serviceEvenement.afficher();
            observableList = FXCollections.observableList(evenementList);
            lv_event.setItems(observableList);

            lv_event.setCellFactory(param -> new ListCell<Evenement>() {
                @Override
                protected void updateItem(Evenement evenement, boolean empty) {
                    super.updateItem(evenement, empty);
                    if (empty || evenement == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Text eventText = new Text(evenement.getNomEvent() + " - " +
                                evenement.getDescEvent() + " - " +
                                evenement.getDateEvent() + " - " +
                                evenement.getLieuEvent() + " - Capacité: " +
                                evenement.getCapaciteEvent());

                        eventText.getStyleClass().add("event-text");

                        HBox hbox = new HBox(10);
                        hbox.getChildren().add(eventText);

                        if (SessionManager.getRole().equals(Role.RH) ) {
                            Button btnModifierEvent = new Button("Modifier");
                            btnModifierEvent.getStyleClass().add("btn-modifierEvent");

                            btnModifierEvent.setOnAction(event -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenement.fxml"));
                                    Parent root = loader.load();

                                    ModifierEvenementController modifierController = loader.getController();
                                    modifierController.initData(evenement);

                                    Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                    stage.setScene(new Scene(root));
                                    stage.setTitle("Modifier Événement");
                                    stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            Button btnSupprimerEvent = new Button("Supprimer");
                            btnSupprimerEvent.getStyleClass().add("btn-supprimerEvent");

                            btnSupprimerEvent.setOnAction(event -> {
                                try {
                                    Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertConfirmation.setTitle("Confirmation");
                                    alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
                                    alertConfirmation.setContentText("Cette action est irréversible.");

                                    if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                        int eventId = evenement.getIdEvent();
                                        serviceEvenement.supprimer(eventId);
                                        observableList.remove(evenement);

                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Succès");
                                        alert.setHeaderText("Suppression réussie");
                                        alert.setContentText("L'événement a été supprimé avec succès.");
                                        alert.showAndWait();
                                    }
                                } catch (SQLException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Erreur");
                                    alert.setHeaderText("Erreur lors de la suppression");
                                    alert.setContentText("Erreur : " + e.getMessage());
                                    alert.showAndWait();
                                }
                            });

                            hbox.getChildren().addAll(btnModifierEvent, btnSupprimerEvent);
                        }

                        hbox.getStyleClass().add("hbox-item");
                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void OnajouterEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvenement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void OnchercherEvent(ActionEvent actionEvent) throws SQLException {
        String searchText = txtRechercheEvent.getText().trim();
        ServiceEvenement serviceEvent = new ServiceEvenement();
        List<Evenement> allEvenement = serviceEvent.afficher();

        List<Evenement> filteredEvenement = allEvenement.stream()
                .filter(e -> e.getNomEvent().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        updateFormationListView(filteredEvenement);
    }

    private void updateFormationListView(List<Evenement> filteredEvenement) {
        ObservableList<Evenement> observableList = FXCollections.observableArrayList(filteredEvenement);
        lv_event.setItems(observableList);
    }
}