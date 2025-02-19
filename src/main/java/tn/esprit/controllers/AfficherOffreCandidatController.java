package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tn.esprit.entities.OffreEmploi;
import tn.esprit.services.ServiceCandidature;
import tn.esprit.services.ServiceOffre;
import tn.esprit.services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AfficherOffreCandidatController {
    private ObservableList<OffreEmploi> offreList = FXCollections.observableArrayList();
    private OffreEmploi offreSelectionnee;
    private List<Integer> appliedOfferIds = new ArrayList<>();
    @FXML
    private Button voirCandidatures;

    @FXML
    private Button Postuler;
    @FXML
    private ListView<OffreEmploi> lv_offre;

    @FXML
    void initialize() {
        Postuler.setDisable(true);
        loadAppliedOffers();
        chargerOffres();
        setupListView();
    }
    @FXML
    private void voirCandidatures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCandidature.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) voirCandidatures.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAppliedOffers() {
        try {
            ServiceUser serviceUser = new ServiceUser();
            ServiceCandidature serviceCandidature = new ServiceCandidature();
            
            // Get the current user (assuming it's a candidate)
            var currentUser = serviceUser.getCandidat();
            if (currentUser != null) {
                // Load all offers this user has applied to
                List<Integer> appliedOffers = serviceCandidature.getAppliedOfferIds(currentUser.getIdU());
                appliedOfferIds.addAll(appliedOffers);
            }
        } catch (SQLException e) {
            System.out.println("Error loading applied offers: " + e.getMessage());
        }
    }

    private void setupListView() {
        lv_offre.setCellFactory(lv -> new ListCell<OffreEmploi>() {
            @Override
            protected void updateItem(OffreEmploi offre, boolean empty) {
                super.updateItem(offre, empty);
                if (empty || offre == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(formaterOffre(offre));
                    if (appliedOfferIds.contains(offre.getIdOffre())) {
                        setStyle("-fx-background-color: #ADD8E6;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        lv_offre.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                offreSelectionnee = newValue;
                Postuler.setDisable(appliedOfferIds.contains(newValue.getIdOffre()));
            } else {
                Postuler.setDisable(true);
            }
        });
    }

    @FXML
    private void postulerOffre() {
        if (offreSelectionnee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCandidature.fxml"));
                Parent root = loader.load();

                AjouterCandidatureController candidatureController = loader.getController();
                candidatureController.setOffre(offreSelectionnee);

                Stage stage = (Stage) lv_offre.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour formater une offre d'emploi (pour l'affichage)
    private String formaterOffre(OffreEmploi offre) {
        return String.format(
                "-------------------------------\n" +
                        "Titre : %-20s\n" +
                        "Description : %-20s\n" +
                        "Type de contrat : %-15s\n" +
                        "Lieu de travail : %-15s\n" +
                        "Salaire : %-10s TND\n" +
                        "Statut : %-10s\n" +
                        "Expérience requise : %-5s (   / an  )\n" +
                        "Date de publication : %-15s\n" +
                        "Date limite : %-15s\n" +
                        "-------------------------------\n",
                offre.getTitre(),
                offre.getDescription().replaceAll("(.{40})","$1\n"),
                offre.getTypeContrat(),
                offre.getLieuTravail(),
                offre.getSalaire(),
                offre.getStatutOffre(),
                offre.getExperience(),
                offre.getDatePublication(),
                offre.getDateLimite()
        );
    }

    // Méthode pour charger toutes les offres depuis la base de données
    void chargerOffres() {
        ServiceOffre serviceOffreEmploi = new ServiceOffre();
        try {
            // Récupérer toutes les offres depuis la base de données
            List<OffreEmploi> offres = serviceOffreEmploi.recupererOffres();
            offreList.clear();
            offreList.addAll(offres);
            lv_offre.setItems(offreList);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des offres : " + e.getMessage());
        }
    }

    public void setOffrePostulee(int offrePostuleeId) {
        appliedOfferIds.add(offrePostuleeId);
        lv_offre.refresh();
        
        OffreEmploi selectedOffer = lv_offre.getSelectionModel().getSelectedItem();
        if (selectedOffer != null) {
            Postuler.setDisable(appliedOfferIds.contains(selectedOffer.getIdOffre()));
        }
    }
}
