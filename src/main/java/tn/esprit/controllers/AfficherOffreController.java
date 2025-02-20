package tn.esprit.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.OffreEmploi;
import tn.esprit.services.ServiceOffre;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherOffreController {

    private ObservableList<String> offreList = FXCollections.observableArrayList(); // Liste observable pour les offres
    private OffreEmploi offreSelectionnee; // Variable pour stocker l'offre sélectionnée
    @FXML
    private ListView<String> lv_offre;
    @FXML
    private TextField searchField;

    @FXML
    void initialize() {
        // Appeler la méthode pour charger toutes les offres depuis la base de données
        chargerOffres();
        // Désactiver le bouton de suppression par défaut
        //SupprimerOffre.setDisable(true);
        // Ajouter un listener pour récupérer l'offre sélectionnée dans la ListView
        lv_offre.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Récupérer l'offre correspondante
                offreSelectionnee = recupererOffreParAffichage(newValue);
            }
        });

        // Add listener for real-time search
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchOffres();
        });
    }

    // Méthode pour récupérer une offre d'emploi à partir de l'affichage (recherche de l'ID par exemple)
    private OffreEmploi recupererOffreParAffichage(String affichageOffre) {
        ServiceOffre serviceOffre = new ServiceOffre();
        try {
            // Récupérer toutes les offres pour trouver celle qui correspond à l'affichage sélectionné
            List<OffreEmploi> offres = serviceOffre.recupererOffres();
            for (OffreEmploi offre : offres) {
                // Comparer l'affichage de l'offre avec celui sélectionné
                String formattedOffre = formaterOffre(offre);
                if (formattedOffre.equals(affichageOffre)) {
                    System.out.println("ID de l'offre sélectionnée : " + offre.getIdOffre()); // Vérifie si l'ID est correct
                    return offre; // Retourner l'offre correspondante
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des offres : " + e.getMessage());
        }
        return null;
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
                        "Expérience requise : %-5s\n" +
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

    // Méthode appelée lors du clic sur le bouton SUPPRIMER
    @FXML
    private void SupprimerOffre() {
        String selectedItem = lv_offre.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une offre à supprimer.");
            alert.showAndWait();
            return;
        }

        if (offreSelectionnee != null) {
            System.out.println("Suppression de l'offre avec ID : " + offreSelectionnee.getIdOffre());
            ServiceOffre serviceOffre = new ServiceOffre();
            try {
                // Supprimer l'offre de la base de données en utilisant l'ID
                serviceOffre.supprimerOffre(offreSelectionnee.getIdOffre());

                // Supprimer l'offre de la liste observable
                offreList.remove(formaterOffre(offreSelectionnee));

                // Réinitialiser l'offre sélectionnée
                offreSelectionnee = null;
                System.out.println("Offre supprimée avec succès.");

            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression de l'offre : " + e.getMessage());
            }
        } else {
            System.out.println("Aucune offre sélectionnée pour la suppression.");
        }
    }

    // Méthode pour charger toutes les offres depuis la base de données
    void chargerOffres() {
        ServiceOffre serviceOffreEmploi = new ServiceOffre();
        try {
            // Récupérer toutes les offres depuis la base de données
            List<OffreEmploi> offres = serviceOffreEmploi.recupererOffres();

            // Ajouter chaque offre à la liste observable

            for (OffreEmploi offre : offres) {
                String formattedOffre = String.format(
                        "-------------------------------\n" +
                                "Titre : %-20s\n" +
                                "Description : %-20s\n" +
                                "Type de contrat : %-15s\n" +
                                "Lieu de travail : %-15s\n" +
                                "Salaire : %-10s TND\n" +
                                "Statut : %-10s\n" +
                                "Expérience requise : %-5s\n" +
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
                offreList.add(formattedOffre);

            }

            // Mettre à jour la ListView avec la liste des offres
            lv_offre.setItems(offreList);

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des offres : " + e.getMessage());
        }
    }

    public void ajouterOffre(OffreEmploi offre) {
//        if (offre != null) {
//            System.out.println("ID de l'offre ajoutée : " + offre.getIdOffre()); // Afficher l'ID pour vérifier
//            String formattedOffre = String.format(
//                    "-------------------------------\n" +
//                            "Titre : %-20s\n" +
//                            "Description : %-20s\n" +
//                            "Type de contrat : %-15s\n" +
//                            "Lieu de travail : %-15s\n" +
//                            "Salaire : %-10s TND\n" +
//                            "Statut : %-10s\n" +
//                            "Expérience requise : %-5s ans\n" +
//                            "Date de publication : %-15s\n" +
//                            "Date limite : %-15s\n" +
//                            "-------------------------------\n",
//                    offre.getTitre(),
//                    offre.getDescription(),
//                    offre.getTypeContrat(),
//                    offre.getLieuTravail(),
//                    offre.getSalaire(),
//                    offre.getStatutOffre(),
//                    offre.getExperience(),
//                    offre.getDatePublication(),
//                    offre.getDateLimite()
//            );
//
//            offreList.add(formattedOffre); // Ajoute la chaîne formatée à la liste observable
//        }
    }

    @FXML
    @Deprecated
    private void goToAjouterOffre(ActionEvent event) {
        try {
            // Charger la vue du formulaire d'ajout
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = fxmlLoader.load();

            /*// Créer une nouvelle fenêtre (Stage) pour le formulaire d'ajout
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Offre d'Emploi");
            stage.setScene(new Scene(root));
            stage.show();*/
            // Obtenir la scène actuelle depuis le bouton "Ajouter Offre"
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer le contenu de la scène par la nouvelle vue
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();  // Afficher l'erreur dans la console
            System.out.println("Erreur de chargement FXML: " + e.getMessage());  // Message d'erreur détaillé
        }
    }

    @FXML
    @Deprecated
    private void goToModifierOffre(ActionEvent event) {
        if (offreSelectionnee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierOffre.fxml"));
                Parent root = loader.load();

                // Passer l'offre sélectionnée au contrôleur de modification
                ModifierOffreController modifierOffreController = loader.getController();
                modifierOffreController.remplirChamps(offreSelectionnee);

                // Changer de scène
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Veuillez sélectionner une offre à modifier.");
        }
    }

    private void searchOffres() {
        String searchTerm = searchField.getText().trim();
        ServiceOffre serviceOffre = new ServiceOffre();

        try {
            List<OffreEmploi> resultats = serviceOffre.rechercherOffres(searchTerm);
            offreList.clear();

            for (OffreEmploi offre : resultats) {
                offreList.add(formaterOffre(offre));
            }

            lv_offre.setItems(offreList);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
    }
}


