
package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SponsorEventsController {

    @FXML private ListView<String> sponsorListView;
    @FXML private Label eventsLabel;
    @FXML private Button deleteButton; // Bouton pour supprimer l'association

    private ServiceSponsor serviceSponsor;

    public SponsorEventsController() {
        serviceSponsor = new ServiceSponsor();
    }

    public void initialize() {
        try {
            loadSponsorData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sponsorListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String sponsorName = newValue;
                try {
                    displayEventsForSponsor(sponsorName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Action pour supprimer l'association entre un sponsor et un événement
        deleteButton.setOnAction(event -> {
            try {
                removeSponsorFromEvent();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadSponsorData() throws SQLException {
        List<Sponsor> sponsors = serviceSponsor.afficher();
        ObservableList<String> sponsorNames = FXCollections.observableArrayList();

        for (Sponsor sponsor : sponsors) {
            sponsorNames.add(sponsor.getNomSponso());
        }

        sponsorListView.setItems(sponsorNames);
    }

    private void displayEventsForSponsor(String sponsorName) throws SQLException {
        // Trouver le sponsor en fonction de son nom
        Sponsor sponsor = serviceSponsor.getSponsorByName(sponsorName);

        // Récupérer les événements sponsorisés par ce sponsor
        List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsor.getIdSponsor());
        String eventsText = "Événements sponsorisés par " + sponsorName + ":\n" + String.join("\n", eventNames);

        eventsLabel.setText(eventsText);
    }

    /*private void removeSponsorFromEvent() throws SQLException {
        String selectedSponsorName = sponsorListView.getSelectionModel().getSelectedItem();

        if (selectedSponsorName != null) {
            Sponsor sponsor = serviceSponsor.getSponsorByName(selectedSponsorName);

            int sponsorId = sponsor.getIdSponsor();

            // Récupérer la liste des événements associés à ce sponsor
            List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsorId);

            if (eventNames.isEmpty()) {
                showAlert("Aucun événement associé", "Ce sponsor n'est associé à aucun événement.");
                return;
            }

            // Créer une boîte de dialogue avec les événements disponibles
            ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
            dialog.setTitle("Suppression d'association");
            dialog.setHeaderText("Choisissez l'événement à dissocier du sponsor :");
            dialog.setContentText("Événement :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String selectedEventName = result.get();
                int eventId = getEventIdFromEventName(selectedEventName);

                // Supprimer l'association
                serviceSponsor.removeEventFromSponsor(sponsorId, eventId);

                // Rafraîchir la liste des événements
                displayEventsForSponsor(selectedSponsorName);
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un sponsor.");
        }
    }*/
    private void removeSponsorFromEvent() throws SQLException {
        String selectedSponsorName = sponsorListView.getSelectionModel().getSelectedItem();

        if (selectedSponsorName != null) {
            Sponsor sponsor = serviceSponsor.getSponsorByName(selectedSponsorName);
            int sponsorId = sponsor.getIdSponsor();

            // Récupérer la liste des événements associés à ce sponsor
            List<String> eventNames = serviceSponsor.getEventNamesBySponsor(sponsorId);

            if (eventNames.isEmpty()) {
                showAlert("Aucun événement associé", "Ce sponsor n'est associé à aucun événement.");
                return;
            }

            // Créer une boîte de dialogue avec les événements disponibles
            ChoiceDialog<String> dialog = new ChoiceDialog<>(eventNames.get(0), eventNames);
            dialog.setTitle("Suppression d'association");
            dialog.setHeaderText("Choisissez l'événement à dissocier du sponsor :");
            dialog.setContentText("Événement :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String selectedEventName = result.get();
                ServiceEvenement serviceEvenement = new ServiceEvenement(); // Assure-toi que cette instance est bien initialisée
                int eventId = serviceEvenement.getEventIdByName(selectedEventName);

                // Supprimer l'association
                serviceSponsor.removeEventFromSponsor(sponsorId, eventId);

                // Rafraîchir la liste des événements
                displayEventsForSponsor(selectedSponsorName);
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner un sponsor.");
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private int getEventIdFromEventName(String eventName) {
        // Implémentez la logique pour obtenir l'ID de l'événement à partir de son nom
        // Cela pourrait être une méthode de ServiceSponsor ou autre.
        return 0; // Placeholder
    }



    @FXML
    public void AjouterAffectation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffecterSponsorEvent.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

