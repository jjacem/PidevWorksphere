package esprit.tn.controllers;

/*import esprit.tn.services.ServiceSponsor;
import esprit.tn.services.ServiceEvenement;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;
import java.util.List;

public class AffectationSponsorEvent {
    @FXML
    private ComboBox<Integer> eventComboBox;
    @FXML
    private ComboBox<Integer> sponsorComboBox;

    private ServiceSponsor serviceSponsor = new ServiceSponsor();
    private ServiceEvenement serviceEvenement = new ServiceEvenement();

    @FXML
    public void initialize() {
        // Remplir les ComboBox avec les données des événements et des sponsors
        eventComboBox.getItems().addAll(serviceEvenement.getEventIds());
        sponsorComboBox.getItems().addAll(serviceSponsor.getSponsorIds());
    }

    @FXML
    private void associerEvenementASponsor() {
        Integer evenementId = eventComboBox.getValue();
        Integer sponsorId = sponsorComboBox.getValue();

        if (evenementId != null && sponsorId != null) {
            try {
                serviceSponsor.ajouterEvenementASponsor(sponsorId, evenementId);
                showAlert("Succès", "Événement associé avec succès.");
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de l'association de l'événement au sponsor.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un événement et un sponsor.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/


import esprit.tn.services.ServiceSponsor;
import esprit.tn.services.ServiceEvenement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AffectationSponsorEvent {
    @FXML
    private ComboBox<String> eventComboBox;  // Changer en ComboBox<String>
    @FXML
    private ComboBox<String> sponsorComboBox;  // Changer en ComboBox<String>

    private ServiceSponsor serviceSponsor = new ServiceSponsor();
    private ServiceEvenement serviceEvenement = new ServiceEvenement();

    @FXML
    public void initialize() {
        // Remplir les ComboBox avec les noms des événements et les emails des sponsors
        eventComboBox.getItems().addAll(serviceEvenement.getEventNames());
        sponsorComboBox.getItems().addAll(serviceSponsor.getSponsorEmails());
    }

    @FXML
    private void associerEvenementASponsor() {
        String evenementName = eventComboBox.getValue();
        String sponsorEmail = sponsorComboBox.getValue();

        if (evenementName != null && sponsorEmail != null) {
            try {
                // Récupérer les IDs correspondants à partir des noms/emails
                Integer evenementId = serviceEvenement.getEventIdByName(evenementName);
                Integer sponsorId = serviceSponsor.getSponsorIdByEmail(sponsorEmail);

                serviceSponsor.ajouterEvenementASponsor(sponsorId, evenementId);
                showAlert("Succès", "Événement associé avec succès.");
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de l'association de l'événement au sponsor.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un événement et un sponsor.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




    @FXML
    public void backbtn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Sponsor_events.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void retourdashRH(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardHR.fxml"));
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

