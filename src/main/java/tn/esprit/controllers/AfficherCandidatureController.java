package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tn.esprit.entities.Candidature;
import tn.esprit.entities.User;
import tn.esprit.services.ServiceCandidature;
import tn.esprit.services.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherCandidatureController implements Initializable {
    @FXML
    private ListView<Candidature> lv_candidatures;

    @FXML
    private Button retourButton;

    @FXML
    private Button supprimerButton;

    private ObservableList<Candidature> candidaturesList = FXCollections.observableArrayList();

    private void setupListView() {
        lv_candidatures.setCellFactory(lv -> new ListCell<Candidature>() {
            @Override
            protected void updateItem(Candidature candidature, boolean empty) {
                super.updateItem(candidature, empty);
                if (empty || candidature == null) {
                    setText(null);
                } else {
                    setText(formatCandidature(candidature));
                }
            }
        });
    }

    private String formatCandidature(Candidature candidature) {
        return String.format(
            "----------------------------------------\n" +
            "Offre: %s\n" +
            "----------------------------------------\n" +
            "CV:\n%s\n" +
            "----------------------------------------\n" +
            "Lettre de Motivation:\n%s\n" +
            "----------------------------------------\n",
            candidature.getIdOffre().getTitre(),
            candidature.getCv(),
            candidature.getLettreMotivation()
        );
    }

    // Méthode pour charger les candidatures depuis la base de données
    private void chargerCandidatures() {
        ServiceUser serviceUser = new ServiceUser();
        ServiceCandidature serviceCandidature = new ServiceCandidature();

        try {
            User currentUser = serviceUser.getCandidat();
            if (currentUser != null) {
                System.out.println("Current user ID: " + currentUser.getIdU()); // Debug line
                List<Candidature> candidatures = serviceCandidature.getCandidaturesByUser(currentUser.getIdU());
                System.out.println("Found " + candidatures.size() + " candidatures"); // Debug line
                
                candidaturesList.clear();
                candidaturesList.addAll(candidatures);
                lv_candidatures.setItems(candidaturesList);
                
                if (candidatures.isEmpty()) {
                    System.out.println("No candidatures found");
                } else {
                    for (Candidature c : candidatures) {
                        System.out.println("Candidature: " + c.getCv()); // Debug line
                    }
                }
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException e) {
            System.out.println("Error loading candidatures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void retourVersOffres(ActionEvent event) {
        try {
            // Get the source of the event
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffreCandidat.fxml"));
            Parent root = loader.load();
            
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading AfficherOffreCandidat.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerCandidature(ActionEvent event) {
        Candidature selectedCandidature = lv_candidatures.getSelectionModel().getSelectedItem();
        
        if (selectedCandidature == null) {
            // Show warning if no candidature is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une candidature à supprimer.");
            alert.showAndWait();
            return;
        }

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            ServiceCandidature serviceCandidature = new ServiceCandidature();
            try {
                serviceCandidature.supprimer(selectedCandidature.getIdCandidature());
                
                // Remove from the ListView
                candidaturesList.remove(selectedCandidature);
                
                // Show success message
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setContentText("La candidature a été supprimée avec succès.");
                success.showAndWait();
                
            } catch (SQLException e) {
                // Show error message
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setContentText("Une erreur est survenue lors de la suppression: " + e.getMessage());
                error.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerCandidatures();
        setupListView();
        
        // Enable/disable delete button based on selection
        lv_candidatures.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            supprimerButton.setDisable(newSelection == null);
        });
    }
}
