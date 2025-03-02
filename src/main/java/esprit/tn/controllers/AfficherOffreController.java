package esprit.tn.controllers;

import esprit.tn.services.OCRService;
import esprit.tn.services.TranslationService;
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
import esprit.tn.entities.OffreEmploi;
import esprit.tn.services.ServiceOffre;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AfficherOffreController {

    private ObservableList<String> offreList = FXCollections.observableArrayList(); // Liste observable pour les offres
    private OffreEmploi offreSelectionnee; // Variable pour stocker l'offre sélectionnée
    @FXML
    private ListView<String> lv_offre;
    @FXML
    private TextField searchField;

    private OCRService ocrService = new OCRService();
    private TranslationService translationService = new TranslationService();

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

    @FXML
    private void analyzeOffre() {
        OffreEmploi selectedOffre = lv_offre.getSelectionModel().getSelectedItem() != null ? 
            recupererOffreParAffichage(lv_offre.getSelectionModel().getSelectedItem()) : null;
            
        if (selectedOffre == null) {
            showAlert(Alert.AlertType.WARNING, "Select an offer", "Please select an offer to analyze.");
            return;
        }

        ProgressIndicator progress = new ProgressIndicator();
        VBox loadingBox = new VBox(10, new Label("Analyzing..."), progress);
        loadingBox.setAlignment(Pos.CENTER);

        Stage loadingStage = new Stage(StageStyle.UNDECORATED);
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setScene(new Scene(loadingBox, 200, 100));
        loadingStage.show();

        CompletableFuture.supplyAsync(() -> {
            try {
                String description = selectedOffre.getDescription();
                return String.format("Description Analysis:\n%s", 
                    ocrService.extractTextFromPDF(description, 
                        progress1 -> Platform.runLater(() -> progress.setProgress(progress1)))
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).whenComplete((result, error) -> {
            Platform.runLater(() -> {
                loadingStage.close();
                if (error != null) {
                    showAlert(Alert.AlertType.ERROR, "Analysis Error", error.getMessage());
                } else {
                    showTextArea("Offer Analysis", result);
                }
            });
        });
    }

    @FXML
    private void translateOffre() {
        OffreEmploi selectedOffre = lv_offre.getSelectionModel().getSelectedItem() != null ? 
            recupererOffreParAffichage(lv_offre.getSelectionModel().getSelectedItem()) : null;
            
        if (selectedOffre == null) {
            showAlert(Alert.AlertType.WARNING, "Select an offer", "Please select an offer to translate.");
            return;
        }

        ChoiceDialog<String> langDialog = new ChoiceDialog<>("English", 
            Arrays.asList("English", "Spanish", "German", "Arabic"));
        langDialog.setTitle("Choose Target Language");
        langDialog.setHeaderText("Select translation language:");
        
        langDialog.showAndWait().ifPresent(targetLang -> {
            String langCode = switch (targetLang) {
                case "English" -> "en";
                case "Spanish" -> "es";
                case "German" -> "de";
                case "Arabic" -> "ar";
                default -> "en";
            };

            ProgressIndicator progress = new ProgressIndicator();
            VBox loadingBox = new VBox(10, new Label("Translating..."), progress);
            loadingBox.setAlignment(Pos.CENTER);

            Stage loadingStage = new Stage(StageStyle.UNDECORATED);
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.setScene(new Scene(loadingBox, 200, 100));
            loadingStage.show();

            CompletableFuture.supplyAsync(() -> {
                try {
                    String description = selectedOffre.getDescription();
                    return String.format("Translated Description:\n%s",
                        translationService.translate(description, "fr", langCode, 
                            p -> Platform.runLater(() -> progress.setProgress(p)))
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((result, error) -> {
                Platform.runLater(() -> {
                    loadingStage.close();
                    if (error != null) {
                        showAlert(Alert.AlertType.ERROR, "Translation Error", error.getMessage());
                    } else {
                        showTextArea("Translated Offer", result);
                    }
                });
            });
        });
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
}
