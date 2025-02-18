/*
package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.services.ServiceEvenement;
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
    void initialize() {
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        try {
            ObservableList<Evenement> observableList = FXCollections.observableList(serviceEvenement.afficher());
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

                        Button btnModifierEvent = new Button("Modifier");
                        btnModifierEvent.getStyleClass().add("btn-modifierEvent");

                        // Action du bouton Modifier
                        btnModifierEvent.setOnAction(event -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenement.fxml"));
                                Parent root = loader.load();

                                ModifierEvenementController modifierController = loader.getController();
                                modifierController.initData(evenement); // Passer l'événement sélectionné

                                // Remplacer la scène actuelle avec la nouvelle scène de modification
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

                        // Action du bouton Supprimer
                        btnSupprimerEvent.setOnAction(event -> {
                            try {
                                // Afficher une boîte de confirmation
                                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                alertConfirmation.setTitle("Confirmation");
                                alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
                                alertConfirmation.setContentText("Cette action est irréversible.");

                                if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                    // Si l'utilisateur confirme, supprimer l'événement
                                    int eventId = evenement.getIdEvent();
                                    System.out.println("ID de l'événement à supprimer : " + eventId);

                                    serviceEvenement.supprimer(eventId);
                                    observableList.remove(evenement);

                                    // Afficher un message de succès
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Succès");
                                    alert.setHeaderText("Suppression réussie");
                                    alert.setContentText("L'événement a été supprimé avec succès.");
                                    alert.showAndWait();
                                }
                            } catch (SQLException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur lors de la suppression de l'événement");
                                alert.setContentText("Une erreur s'est produite : " + e.getMessage());
                                alert.showAndWait();
                            }
                        });


                        HBox hbox = new HBox(10, eventText, btnModifierEvent, btnSupprimerEvent);
                        hbox.getStyleClass().add("hbox-item");
                        setGraphic(hbox);
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }


    }
}
*/
package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.services.ServiceEvenement;
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
    private TextField searchField; // TextField for searching

    @FXML
    private Button searchButton;  // Search button

    private ObservableList<Evenement> observableList; // Store all events
    @FXML
    private TextField txtRechercheEvent;
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

                        Button btnModifierEvent = new Button("Modifier");
                        btnModifierEvent.getStyleClass().add("btn-modifierEvent");

                        // Action du bouton Modifier
                        btnModifierEvent.setOnAction(event -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenement.fxml"));
                                Parent root = loader.load();

                                ModifierEvenementController modifierController = loader.getController();
                                modifierController.initData(evenement); // Passer l'événement sélectionné

                                // Remplacer la scène actuelle avec la nouvelle scène de modification
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

                        // Action du bouton Supprimer
                        btnSupprimerEvent.setOnAction(event -> {
                            try {
                                // Afficher une boîte de confirmation
                                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                alertConfirmation.setTitle("Confirmation");
                                alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
                                alertConfirmation.setContentText("Cette action est irréversible.");

                                if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                    // Si l'utilisateur confirme, supprimer l'événement
                                    int eventId = evenement.getIdEvent();
                                    System.out.println("ID de l'événement à supprimer : " + eventId);

                                    serviceEvenement.supprimer(eventId);
                                    observableList.remove(evenement);

                                    // Afficher un message de succès
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Succès");
                                    alert.setHeaderText("Suppression réussie");
                                    alert.setContentText("L'événement a été supprimé avec succès.");
                                    alert.showAndWait();
                                }
                            } catch (SQLException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur lors de la suppression de l'événement");
                                alert.setContentText("Une erreur s'est produite : " + e.getMessage());
                                alert.showAndWait();
                            }
                        });

                        HBox hbox = new HBox(10, eventText, btnModifierEvent, btnSupprimerEvent);
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

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void OnchercherEvent(ActionEvent actionEvent) throws SQLException {
        String searchText = txtRechercheEvent.getText();
        ServiceEvenement serviceEvent=new ServiceEvenement();
        // Liste des Evenement (vous pouvez remplacer cela par la liste de Evenement réelle)
        List<Evenement> allEvenement = serviceEvent.afficher(); // Remplacez par votre méthode pour obtenir la liste des formations

        // Filtrer les Evenement avec Stream en fonction du texte de recherche
        List<Evenement> filteredEvenement = allEvenement.stream()
                .filter(Evenement -> Evenement.getNomEvent().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        // Mettre à jour l'affichage des résultats de la recherche
        updateFormationListView(filteredEvenement);

    }
    private void updateFormationListView(List<Evenement> filteredEvenement) {
        // Convertir la liste filtrée en ObservableList pour l'affichage
        ObservableList<Evenement> observableList = FXCollections.observableArrayList(filteredEvenement);
        lv_event.setItems(observableList);  // formationListView est votre ListView ou TableView
    }

}
