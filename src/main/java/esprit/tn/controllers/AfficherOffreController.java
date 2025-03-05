package esprit.tn.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.services.ServiceOffre;
import esprit.tn.controllers.AjouterOffreController.RefreshCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class AfficherOffreController {
    private ObservableList<String> offreList = FXCollections.observableArrayList();
    private ObservableList<String> filteredOffreList = FXCollections.observableArrayList();
    private OffreEmploi offreSelectionnee;
    
    @FXML
    private ListView<String> lv_offre;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    void initialize() {
        // Initialize sort options
        sortComboBox.setItems(FXCollections.observableArrayList(
            "Plus récentes d'abord", 
            "Plus anciennes d'abord",
            "Date limite proche",
            "Date limite éloignée"
        ));
        
        // Set up sort combo box listener
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applySorting(newValue);
            }
        });

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

        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
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
            List<OffreEmploi> offres = serviceOffreEmploi.recupererOffres();
            
            // Apply default sorting (most recent first)
            offres.sort((o1, o2) -> o2.getDatePublication().compareTo(o1.getDatePublication()));
            
            offreList.clear();
            filteredOffreList.clear();
            
            for (OffreEmploi offre : offres) {
                String formattedOffre = formaterOffre(offre);
                offreList.add(formattedOffre);
                filteredOffreList.add(formattedOffre);
            }
            
            lv_offre.setItems(filteredOffreList);
            
            // Restore selection if possible
            if (offreSelectionnee != null) {
                String formattedSelected = formaterOffre(offreSelectionnee);
                int index = filteredOffreList.indexOf(formattedSelected);
                if (index >= 0) {
                    lv_offre.getSelectionModel().select(index);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des offres : " + e.getMessage());
        }
    }

    // Add sorting functionality
    private void applySorting(String sortOption) {
        ServiceOffre serviceOffre = new ServiceOffre();
        try {
            List<OffreEmploi> offres = serviceOffre.recupererOffres();
            
            // Sort based on selected option
            switch (sortOption) {
                case "Plus récentes d'abord":
                    offres.sort((o1, o2) -> o2.getDatePublication().compareTo(o1.getDatePublication()));
                    break;
                case "Plus anciennes d'abord":
                    offres.sort(Comparator.comparing(OffreEmploi::getDatePublication));
                    break;
                case "Date limite proche":
                    offres.sort(Comparator.comparing(OffreEmploi::getDateLimite));
                    break;
                case "Date limite éloignée":
                    offres.sort((o1, o2) -> o2.getDateLimite().compareTo(o1.getDateLimite()));
                    break;
            }
            
            // Update the ListView
            offreList.clear();
            filteredOffreList.clear();
            
            for (OffreEmploi offre : offres) {
                String formattedOffre = formaterOffre(offre);
                offreList.add(formattedOffre);
                filteredOffreList.add(formattedOffre);
            }
            
            lv_offre.setItems(filteredOffreList);
            
        } catch (SQLException e) {
            System.out.println("Erreur lors du tri des offres : " + e.getMessage());
        }
    }

    // Modify the search method to maintain sorting
    private void searchOffres() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            filteredOffreList.setAll(offreList);
        } else {
            ServiceOffre serviceOffre = new ServiceOffre();
            try {
                List<OffreEmploi> resultats = serviceOffre.rechercherOffres(searchTerm);
                filteredOffreList.clear();
                
                for (OffreEmploi offre : resultats) {
                    filteredOffreList.add(formaterOffre(offre));
                }
                
                // Maintain current sorting if any
                String currentSort = sortComboBox.getValue();
                if (currentSort != null) {
                    applySorting(currentSort);
                }
            } catch (SQLException e) {
                System.out.println("Erreur lors de la recherche : " + e.getMessage());
            }
        }
        
        lv_offre.setItems(filteredOffreList);
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
    private void goToAjouterOffre(ActionEvent event) {
        try {
            // Charger la vue du formulaire d'ajout
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AjouterOffre.fxml"));
            Parent root = fxmlLoader.load();

            // Créer une nouvelle fenêtre (Stage) pour le formulaire d'ajout
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter une Offre d'Emploi");
            dialogStage.initModality(Modality.WINDOW_MODAL); // Make it modal
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow()); // Set owner
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            // Get controller to set callback
            AjouterOffreController controller = fxmlLoader.getController();
            controller.setRefreshCallback(() -> {
                // Refresh the offer list after adding
                chargerOffres();
            });
            
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur de chargement FXML: " + e.getMessage());
        }
    }

    @FXML
    private void goToModifierOffre(ActionEvent event) {
        if (offreSelectionnee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierOffre.fxml"));
                Parent root = loader.load();

                // Passer l'offre sélectionnée au contrôleur de modification
                ModifierOffreController modifierOffreController = loader.getController();
                modifierOffreController.remplirChamps(offreSelectionnee);
                
                // Set callback to refresh the offers list after modification
                modifierOffreController.setRefreshCallback(new RefreshCallback() {
                    @Override
                    public void refresh() {
                        // This will update the ListView with the modified offer
                        chargerOffres();
                    }
                });

                // Create a new stage for the modification form
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Modifier une Offre d'Emploi");
                dialogStage.initModality(Modality.WINDOW_MODAL); // Make it modal
                dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow()); // Set owner
                
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                
                // Show the dialog and wait for it to close
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                          "Erreur lors du chargement de l'interface de modification: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", 
                      "Veuillez sélectionner une offre à modifier.");
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showTextArea(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);
        
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }

    @FXML
    private void viewCandidatures(ActionEvent event) {
        String selectedItem = lv_offre.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setContentText("Veuillez sélectionner une offre pour voir les candidatures.");
            alert.showAndWait();
            return;
        }

        try {
            // Get the selected offer
            OffreEmploi selectedOffer = recupererOffreParAffichage(selectedItem);
            if (selectedOffer == null) {
                throw new Exception("Offre non trouvée");
            }

            // Load the candidature view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherTousCandidature.fxml"));
            Parent root = loader.load();

            // Pass the selected offer ID to the controller
            AfficherTousCandidatureController controller = loader.getController();
            controller.loadCandidaturesForOffer(selectedOffer.getIdOffre());

            // Switch to the candidature view
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des candidatures: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Impossible de charger les candidatures: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
