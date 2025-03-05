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
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.services.ServiceOffre;
import esprit.tn.controllers.AjouterOffreController.RefreshCallback;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

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

        // Setup custom ListView cell factory
        setupListView();

        // Add double click handler
        lv_offre.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && offreSelectionnee != null) {
                showOfferDetails(offreSelectionnee);
            }
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
            // Clear existing list to avoid duplicates
            offreList.clear();
            
            // Get all offers from the database
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
            
            // Restore selection if possible
            if (offreSelectionnee != null) {
                // Try to find the updated offer in the list
                for (int i = 0; i < offreList.size(); i++) {
                    String formattedOffre = offreList.get(i);
                    OffreEmploi offre = recupererOffreParAffichage(formattedOffre);
                    if (offre != null && offre.getIdOffre() == offreSelectionnee.getIdOffre()) {
                        lv_offre.getSelectionModel().select(i);
                        break;
                    }
                }
            }

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

    private void setupListView() {
        lv_offre.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Get the actual OffreEmploi object for this item
                    OffreEmploi offre = recupererOffreParAffichage(item);
                    if (offre == null) return;

                    VBox card = new VBox(15);
                    card.setPadding(new Insets(20));
                    card.setPrefWidth(1000);
                    card.setStyle("-fx-background-color: white; " +
                                "-fx-border-color: #e0e0e0; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

                    // Title section - using direct object properties instead of parsing
                    HBox titleBox = new HBox(5);
                    Text titleLabel = new Text("Titre: ");
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-fill: #2c3e50;");
                    Text titleValue = new Text(offre.getTitre());
                    titleValue.setStyle("-fx-font-size: 18px; -fx-fill: #2c3e50;");
                    titleBox.getChildren().addAll(titleLabel, titleValue);

                    // Details Grid
                    GridPane details = new GridPane();
                    details.setHgap(50);
                    details.setVgap(15);
                    details.setPadding(new Insets(15));
                    details.setStyle("-fx-background-color: white; -fx-border-color: #eee; " +
                                   "-fx-border-radius: 5; -fx-background-radius: 5;");

                    // Add details using direct object properties
                    addDetailToGrid(details, "Type de contrat", offre.getTypeContrat(), 0, 0);
                    addDetailToGrid(details, "Lieu de travail", offre.getLieuTravail(), 0, 1);
                    addDetailToGrid(details, "Salaire", offre.getSalaire() + " TND", 1, 0);
                    addDetailToGrid(details, "Expérience", offre.getExperience(), 1, 1);
                    addDetailToGrid(details, "Date publication", offre.getDatePublication().toString(), 2, 0);
                    addDetailToGrid(details, "Date limite", offre.getDateLimite().toString(), 2, 1);

                    // Status indicator
                    Label statusLabel = new Label("■ " + offre.getStatutOffre());
                    statusLabel.setStyle(String.format(
                        "-fx-text-fill: %s; -fx-font-weight: bold; -fx-padding: 5 10;",
                        offre.getStatutOffre().equalsIgnoreCase("Active") ? "#27ae60" : "#e74c3c"
                    ));

                    // Add all sections to the card
                    card.getChildren().addAll(titleBox, details, statusLabel);
                    
                    // Hover effect
                    String baseStyle = card.getStyle();
                    card.setOnMouseEntered(e -> 
                        card.setStyle(baseStyle + "-fx-background-color: #f8f9fa;"));
                    card.setOnMouseExited(e -> 
                        card.setStyle(baseStyle + "-fx-background-color: white;"));

                    setGraphic(card);
                }
            }
        });
    }

    private void addDetailToGrid(GridPane grid, String label, String value, int col, int row) {
        VBox container = new VBox(5);
        Text labelText = new Text(label);
        labelText.setStyle("-fx-font-weight: bold; -fx-fill: #7f8c8d; -fx-font-size: 12px;");
        Text valueText = new Text(value);
        valueText.setStyle("-fx-fill: #2c3e50; -fx-font-size: 14px;");
        container.getChildren().addAll(labelText, valueText);
        grid.add(container, col, row);
    }

    private String extractValue(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
    }

    private void showOfferDetails(OffreEmploi offre) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails de l'offre");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Create a VBox for the content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        // Title
        Text titleText = new Text(offre.getTitre());
        titleText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;");

        // Description
        VBox descriptionBox = new VBox(5);
        descriptionBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        Text descLabel = new Text("Description");
        descLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: #34495e;");
        TextFlow descValue = new TextFlow();
        Text descText = new Text(offre.getDescription());
        descText.setStyle("-fx-font-size: 14px; -fx-fill: #2c3e50;");
        descValue.getChildren().add(descText);
        descValue.setMaxWidth(550);
        descValue.setLineSpacing(1.5);
        descriptionBox.getChildren().addAll(descLabel, descValue);

        // Details Grid
        GridPane details = new GridPane();
        details.setHgap(30);
        details.setVgap(15);
        details.setPadding(new Insets(15));
        details.setStyle("-fx-background-color: white; -fx-border-color: #eee; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;");

        // Add details to grid
        int row = 0;
        addDetailToGrid(details, "Type de contrat", offre.getTypeContrat(), 0, row);
        addDetailToGrid(details, "Lieu de travail", offre.getLieuTravail(), 1, row++);
        addDetailToGrid(details, "Salaire", offre.getSalaire() + " TND", 0, row);
        addDetailToGrid(details, "Expérience requise", offre.getExperience(), 1, row++);
        addDetailToGrid(details, "Date de publication", offre.getDatePublication().toString(), 0, row);
        addDetailToGrid(details, "Date limite", offre.getDateLimite().toString(), 1, row++);

        // Status
        Label statusLabel = new Label("■ " + offre.getStatutOffre());
        statusLabel.setStyle(String.format(
            "-fx-text-fill: %s; -fx-font-weight: bold; -fx-padding: 10;",
            offre.getStatutOffre().equalsIgnoreCase("Active") ? "#27ae60" : "#e74c3c"
        ));

        // Add all elements to content
        content.getChildren().addAll(titleText, descriptionBox, details, statusLabel);

        // Set dialog content and make it scrollable
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setPrefWidth(600);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }
}
