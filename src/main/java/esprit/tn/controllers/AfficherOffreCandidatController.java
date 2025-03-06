package esprit.tn.controllers;

import esprit.tn.entities.Candidature;
import esprit.tn.services.ServiceUser;
import esprit.tn.services.OCRService;
import esprit.tn.services.TranslationService;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.entities.Bookmark;
import esprit.tn.entities.Notification;
import esprit.tn.services.ServiceCandidature;
import esprit.tn.services.ServiceOffre;
import esprit.tn.services.ServiceBookmark;
import esprit.tn.services.NotificationService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;

public class AfficherOffreCandidatController {
    private ObservableList<OffreEmploi> offreList = FXCollections.observableArrayList();
    private ObservableList<OffreEmploi> filteredOffreList = FXCollections.observableArrayList();
    private OffreEmploi offreSelectionnee;
    private List<Integer> appliedOfferIds = new ArrayList<>();

    private List<Integer> bookmarkedOfferIds = new ArrayList<>();
    
    @FXML
    private Button voirCandidatures;
    @FXML
    private Button Postuler;
    @FXML
    private Button voirFavorisBtn;
    @FXML
    private ListView<OffreEmploi> lv_offre;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ImageView bookmarkIcon;
    @FXML
    private Button notificationsButton;
    @FXML
    private Label notificationCountLabel;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button translateButton;
    
    // Icons for bookmark states
    private final Image bookmarkEmpty = new Image(getClass().getResourceAsStream("/images/notSaved.png"));
    private final Image bookmarkFilled = new Image(getClass().getResourceAsStream("/images/Saved.png"));

    private NotificationService notificationService = new NotificationService();
    private OCRService ocrService = new OCRService();
    private TranslationService translationService = new TranslationService();
    private int unreadNotificationsCount = 0;

    @FXML
    void initialize() {
        Postuler.setDisable(true);

        bookmarkIcon.setImage(bookmarkEmpty);
        bookmarkIcon.setVisible(false);
        
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
        
        refreshData();

        loadNotificationCount();

        // Add listener for searching
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchOffres(newValue);
        });
        
        // Initialize the analyze and translate buttons
        analyzeButton.setDisable(true);
        translateButton.setDisable(true);

        // Add listener to enable/disable analyze and translate buttons based on selection
        lv_offre.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                offreSelectionnee = newValue;
                Postuler.setDisable(appliedOfferIds.contains(newValue.getIdOffre()));
                analyzeButton.setDisable(false);
                translateButton.setDisable(false);

                bookmarkIcon.setVisible(true);
                
                // Update bookmark icon based on status
                if (bookmarkedOfferIds.contains(newValue.getIdOffre())) {
                    bookmarkIcon.setImage(bookmarkFilled);
                } else {
                    bookmarkIcon.setImage(bookmarkEmpty);
                }
            } else {
                Postuler.setDisable(true);
                analyzeButton.setDisable(true);
                translateButton.setDisable(true);
                bookmarkIcon.setVisible(false);
            }
        });

        // Add double click handler for the ListView
        lv_offre.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && offreSelectionnee != null) {
                showOfferDetails(offreSelectionnee);
            }
        });
    }
    
    /**
     * Refresh all data from database
     * Call this when returning from another view
     */
    public void refreshData() {
        // Clear existing lists to avoid duplicates
        appliedOfferIds.clear();
        bookmarkedOfferIds.clear();
        
        // Load data from database
        loadAppliedOffers();
        loadBookmarkedOffers();
        chargerOffres();
        setupListView();
        
        // If an offer was selected before refresh, reselect it
        if (offreSelectionnee != null) {
            int idToSelect = offreSelectionnee.getIdOffre();
            for (OffreEmploi offre : filteredOffreList) {
                if (offre.getIdOffre() == idToSelect) {
                    lv_offre.getSelectionModel().select(offre);
                    break;
                }
            }
        }
        
        // Refresh notifications count
        loadNotificationCount();
    }
    
    // Apply sorting based on selected option
    private void applySorting(String sortOption) {
        switch (sortOption) {
            case "Plus récentes d'abord":
                filteredOffreList.sort((o1, o2) -> o2.getDatePublication().compareTo(o1.getDatePublication()));
                break;
            case "Plus anciennes d'abord":
                filteredOffreList.sort(Comparator.comparing(OffreEmploi::getDatePublication));
                break;
            case "Date limite proche":
                filteredOffreList.sort(Comparator.comparing(OffreEmploi::getDateLimite));
                break;
            case "Date limite éloignée":
                filteredOffreList.sort((o1, o2) -> o2.getDateLimite().compareTo(o1.getDateLimite()));
                break;
            default:
                break;
        }
        
        // Update the ListView with the sorted data
        lv_offre.setItems(filteredOffreList);
        lv_offre.refresh();
    }

    @FXML
    private void voirCandidatures() {
        try {
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser == null) return;

            ServiceCandidature serviceCandidature = new ServiceCandidature();
            List<Candidature> candidatures = serviceCandidature.getCandidaturesByUser(currentUser.getIdUser());

            if (candidatures.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Mes Candidatures", 
                    "Vous n'avez pas encore postulé à des offres.");
                return;
            }

            // Create dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Mes Candidatures");
            dialog.initModality(Modality.APPLICATION_MODAL);

            // Create content container
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white;");

            // Add each candidature
            for (Candidature candidature : candidatures) {
                VBox candidatureBox = new VBox(10);
                candidatureBox.setStyle("-fx-background-color: #f8f9fa; " +
                                     "-fx-padding: 15; " +
                                     "-fx-background-radius: 5; " +
                                     "-fx-border-color: #e0e0e0; " +
                                     "-fx-border-radius: 5;");

                // Offer title
                Text titleText = new Text(candidature.getIdOffre().getTitre());
                titleText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #2c3e50;");

//                // Status with color
//                Label statusLabel = new Label("Statut: " + candidature.getStatus());
//                statusLabel.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold;",
//                    getStatusColor(candidature.getStatus())));

//                // Date section
//                Text dateText = new Text("Date de candidature: " + candidature.getDateCandidature());
//                dateText.setStyle("-fx-fill: #7f8c8d;");

                // Documents section
                VBox documents = new VBox(5);
                documents.setStyle("-fx-padding: 10 0;");
                
                // CV Link
                Hyperlink cvLink = new Hyperlink("Voir CV");
                cvLink.setOnAction(e -> openDocument(candidature.getCv()));
                
                // Lettre Link
                Hyperlink lettreLink = new Hyperlink("Voir Lettre de motivation");
                lettreLink.setOnAction(e -> openDocument(candidature.getLettreMotivation()));

                documents.getChildren().addAll(
                    new Text("Documents:"),
                    cvLink,
                    lettreLink
                );

                // Add all elements to candidature box
                candidatureBox.getChildren().addAll(
                    titleText,
//                    statusLabel,
//                    dateText,
                    documents
                );

                content.getChildren().add(candidatureBox);
            }

            // Make content scrollable
            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setPrefWidth(600);

            // Set dialog content
            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Show dialog
            dialog.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger vos candidatures: " + e.getMessage());
        }
    }

    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "en attente" -> "#f39c12";
            case "acceptée" -> "#27ae60";
            case "rejetée" -> "#e74c3c";
            default -> "#2c3e50";
        };
    }

    private void openDocument(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Le document n'existe pas: " + filePath);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Impossible d'ouvrir le document: " + e.getMessage());
        }
    }

    @FXML
    private void voirFavoris() {
        try {
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser == null) return;

            ServiceBookmark serviceBookmark = new ServiceBookmark();
            ServiceOffre serviceOffre = new ServiceOffre();
            List<Integer> bookmarkIds = serviceBookmark.getBookmarkedOfferIds(currentUser.getIdUser());
            List<OffreEmploi> favoriteOffers = new ArrayList<>();

            // Get all bookmarked offers
            for (Integer id : bookmarkIds) {
                OffreEmploi offre = serviceOffre.getOffreById(id);
                if (offre != null) {
                    favoriteOffers.add(offre);
                }
            }

            if (favoriteOffers.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Mes Favoris", 
                    "Vous n'avez pas encore d'offres dans vos favoris.");
                return;
            }

            // Create dialog
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Mes Favoris");
            dialog.initModality(Modality.APPLICATION_MODAL);

            // Create main content container
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white;");

            // Add each favorite offer
            for (OffreEmploi offre : favoriteOffers) {
                VBox offerBox = new VBox(10);
                offerBox.setStyle("-fx-background-color: #f8f9fa; " +
                               "-fx-padding: 15; " +
                               "-fx-background-radius: 5; " +
                               "-fx-border-color: #e0e0e0; " +
                               "-fx-border-radius: 5;");

                // Title
                Text titleText = new Text(offre.getTitre());
                titleText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #2c3e50;");

                // Details Grid
                GridPane details = new GridPane();
                details.setHgap(30);
                details.setVgap(10);
                details.setPadding(new Insets(10));

//                // Add details
//                addDetailToGrid(details, "Type de contrat", offre.getTypeContrat(), 0, 0);
//                addDetailToGrid(details, "Lieu", offre.getLieuTravail(), 1, 0);
//                addDetailToGrid(details, "Salaire", offre.getSalaire() + " TND", 0, 1);
//                addDetailToGrid(details, "Date limite", offre.getDateLimite().toString(), 1, 1);

                // Status indicator
                Label statusLabel = new Label("■ " + offre.getStatutOffre());
                statusLabel.setStyle(String.format(
                    "-fx-text-fill: %s; -fx-font-weight: bold;",
                    offre.getStatutOffre().equalsIgnoreCase("Active") ? "#27ae60" : "#e74c3c"
                ));

                // Remove from favorites button
                Button removeButton = new Button("Retirer des favoris");
                removeButton.setStyle("-fx-text-fill: #e74c3c;");
                
                removeButton.setOnAction(e -> {
                    try {
                        serviceBookmark.supprimerParUserEtOffre(currentUser.getIdUser(), offre.getIdOffre());
                        content.getChildren().remove(offerBox);
                        bookmarkedOfferIds.remove(Integer.valueOf(offre.getIdOffre()));
                        lv_offre.refresh(); // Refresh main list view

                        if (content.getChildren().isEmpty()) {
                            dialog.close();
                            showAlert(Alert.AlertType.INFORMATION, "Mes Favoris", 
                                "Vous n'avez plus d'offres dans vos favoris.");
                        }
                    } catch (SQLException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "Impossible de retirer l'offre des favoris: " + ex.getMessage());
                    }
                });

                // Add all elements to offer box
                offerBox.getChildren().addAll(titleText, details, statusLabel, removeButton);
                content.getChildren().add(offerBox);
            }

            // Make content scrollable
            ScrollPane scrollPane = new ScrollPane(content);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(500);
            scrollPane.setPrefWidth(600);

            // Set dialog content
            dialog.getDialogPane().setContent(scrollPane);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            // Show dialog
            dialog.show();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                "Impossible de charger vos favoris: " + e.getMessage());
        }
    }

    @FXML
    private void toggleBookmark() {
        if (offreSelectionnee != null) {
            try {
                ServiceBookmark serviceBookmark = new ServiceBookmark();
                var currentUser = SessionManager.extractuserfromsession();
                
                if (currentUser != null) {
                    int userId = currentUser.getIdUser();
                    int offreId = offreSelectionnee.getIdOffre();
                    
                    if (bookmarkedOfferIds.contains(offreId)) {
                        // Remove bookmark
                        serviceBookmark.supprimerParUserEtOffre(userId, offreId);
                        bookmarkedOfferIds.remove(Integer.valueOf(offreId));
                        bookmarkIcon.setImage(bookmarkEmpty);
                    } else {
                        // Add bookmark
                        Bookmark bookmark = new Bookmark(userId, offreId);
                        serviceBookmark.ajouter(bookmark);
                        bookmarkedOfferIds.add(offreId);
                        bookmarkIcon.setImage(bookmarkFilled);
                    }
                    
                    // Refresh the list view to update styling
                    lv_offre.refresh();
                }
            } catch (SQLException e) {
                System.out.println("Error toggling bookmark: " + e.getMessage());
            }
        }
    }

    private void loadAppliedOffers() {
        try {
            ServiceCandidature serviceCandidature = new ServiceCandidature();
            
            // Get the current user (assuming it's a candidate)
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser != null) {
                // Load all offers this user has applied to
                List<Integer> appliedOffers = serviceCandidature.getAppliedOfferIds(currentUser.getIdUser());
                appliedOfferIds.addAll(appliedOffers);
            }
        } catch (SQLException e) {
            System.out.println("Error loading applied offers: " + e.getMessage());
        }
    }


    private void loadBookmarkedOffers() {
        try {
            ServiceBookmark serviceBookmark = new ServiceBookmark();
            
            // Get the current user
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser != null) {
                // Load all offers this user has bookmarked
                List<Integer> bookmarkedOffers = serviceBookmark.getBookmarkedOfferIds(currentUser.getIdUser());
                bookmarkedOfferIds.addAll(bookmarkedOffers);
            }
        } catch (SQLException e) {
            System.out.println("Error loading bookmarked offers: " + e.getMessage());
        }
    }

    private void setupListView() {
        lv_offre.setCellFactory(param -> new ListCell<OffreEmploi>() {
            @Override
            protected void updateItem(OffreEmploi offre, boolean empty) {
                super.updateItem(offre, empty);
                
                if (empty || offre == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(15);
                    card.setPadding(new Insets(20));
                    card.setPrefWidth(1000);
                    card.setStyle("-fx-background-color: white; " +
                                "-fx-border-color: #e0e0e0; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

                    // Title section
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

                    // Add these lines back - they were commented out before
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
                    
                    // Add special styling for applied/bookmarked offers
                    if (appliedOfferIds.contains(offre.getIdOffre())) {
                        card.setStyle(card.getStyle() + "; -fx-border-color: #3498db; -fx-border-width: 2;");
                    } else if (bookmarkedOfferIds.contains(offre.getIdOffre())) {
                        card.setStyle(card.getStyle() + "; -fx-border-color: #f1c40f; -fx-border-width: 2;");
                    }
                    
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

    // Add this helper method if it's missing
    private void addDetailToGrid(GridPane grid, String label, String value, int col, int row) {
        VBox container = new VBox(5);
        Text labelText = new Text(label);
        labelText.setStyle("-fx-font-weight: bold; -fx-fill: #7f8c8d; -fx-font-size: 12px;");
        Text valueText = new Text(value);
        valueText.setStyle("-fx-fill: #2c3e50; -fx-font-size: 14px;");
        container.getChildren().addAll(labelText, valueText);
        grid.add(container, col, row);
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
            filteredOffreList.clear();
            
            offreList.addAll(offres);
            filteredOffreList.addAll(offres);
            
            // Apply default sorting (most recent first)
            filteredOffreList.sort((o1, o2) -> o2.getDatePublication().compareTo(o1.getDatePublication()));
            lv_offre.setItems(filteredOffreList);
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

    // Méthode pour rechercher des offres en fonction du terme de recherche
    private void searchOffres(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            // If search term is empty, show all offers with current sorting
            filteredOffreList.clear();
            filteredOffreList.addAll(offreList);
            
            // Re-apply current sorting if any
            String currentSort = sortComboBox.getValue();
            if (currentSort != null) {
                applySorting(currentSort);
            }
        } else {
            try {
                ServiceOffre serviceOffre = new ServiceOffre();
                List<OffreEmploi> resultats = serviceOffre.rechercherOffres(searchTerm);
                
                filteredOffreList.clear();
                filteredOffreList.addAll(resultats);
                
                // Re-apply current sorting if any
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

    private void loadNotificationCount() {
        try {
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser != null) {
                unreadNotificationsCount = notificationService.countUnreadNotifications(currentUser.getIdUser());
                updateNotificationBadge();
            }
        } catch (SQLException e) {
            System.err.println("Error loading notifications: " + e.getMessage());
        }
    }
    
    private void updateNotificationBadge() {
        if (unreadNotificationsCount > 0) {
            notificationCountLabel.setText(String.valueOf(unreadNotificationsCount));
            notificationCountLabel.setVisible(true);
        } else {
            notificationCountLabel.setVisible(false);
        }
    }
    @FXML
    private void showNotifications() {
        try {
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser != null) {
                // Get all notifications, both read and unread
                List<Notification> notifications = notificationService.getAllNotifications(currentUser.getIdUser());
                
                if (notifications.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "Notifications", 
                            "Vous n'avez pas encore de notifications.");
                    return;
                }

          
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Notifications");
                
                // Create content
                VBox contentBox = new VBox(10);
                contentBox.setStyle("-fx-padding: 10px;");
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                for (Notification notification : notifications) {
                    // Create a visually distinct style for unread notifications
                    String style = notification.isRead() 
                        ? "-fx-background-color: #f8f8f8;" 
                        : "-fx-background-color: #e8f4f8; -fx-border-color: #3498db; -fx-border-width: 0 0 0 3px;";
                    
                    Label messageLabel = new Label(notification.getMessage());
                    messageLabel.setWrapText(true);
                    messageLabel.setStyle("-fx-font-weight: " + (notification.isRead() ? "normal" : "bold") + ";");
                    
                    Label dateLabel = new Label(dateFormat.format(notification.getCreatedAt()));
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
                    
                    // Add unread indicator
                    HBox headerBox = new HBox(5);
                    if (!notification.isRead()) {
                        Label unreadLabel = new Label("• NEW");
                        unreadLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                        headerBox.getChildren().addAll(dateLabel, unreadLabel);
                    } else {
                        headerBox.getChildren().add(dateLabel);
                    }
                    
                    // Add mark as read button for each unread notification
                    VBox notificationBox = new VBox(5);
                    notificationBox.getChildren().addAll(headerBox, messageLabel);
                    
                    if (!notification.isRead()) {
                        Button markAsReadBtn = new Button("Marquer comme lu");
                        markAsReadBtn.setStyle("-fx-font-size: 10px;");
                        markAsReadBtn.setOnAction(event -> {
                            try {
                                notificationService.markAsRead(notification.getId());
                                notification.setRead(true);

                                // Update the notification's appearance
                                notificationBox.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10px; -fx-border-color: #ddd; -fx-border-radius: 5px;");
                                messageLabel.setStyle("-fx-font-weight: normal;");
                                headerBox.getChildren().clear();
                                headerBox.getChildren().add(dateLabel);
                                notificationBox.getChildren().remove(markAsReadBtn);

                                // Update notification count
                                unreadNotificationsCount--;
                                updateNotificationBadge();
                            } catch (SQLException e) {
                                System.err.println("Error marking notification as read: " + e.getMessage());
                            }
                        });
                        notificationBox.getChildren().add(markAsReadBtn);
                    }
                    
                    notificationBox.setStyle(style + "-fx-padding: 10px; -fx-border-color: #ddd; -fx-border-radius: 5px;");
                    contentBox.getChildren().add(notificationBox);
                }
                
                // Add buttons for marking all as read and clearing notifications
                HBox actionButtons = new HBox(10);
                
                Button markAllReadButton = new Button("Marquer tout comme lu");
                markAllReadButton.setOnAction(e -> {
                    try {
                        notificationService.markAllAsRead(currentUser.getIdUser());
                        unreadNotificationsCount = 0;
                        updateNotificationBadge();
                        
                        // Update all notifications in the view
                        for (Node node : contentBox.getChildren()) {
                            if (node instanceof VBox notificationBox) {
                                notificationBox.setStyle("-fx-background-color: #f8f8f8; -fx-padding: 10px; -fx-border-color: #ddd; -fx-border-radius: 5px;");
                                
                                // Remove mark as read buttons and unread indicators
                                for (Node child : notificationBox.getChildren().toArray(new Node[0])) {
                                    if (child instanceof Button) {
                                        notificationBox.getChildren().remove(child);
                                    } else if (child instanceof HBox headerBox) {
                                        for (Node headerItem : headerBox.getChildren().toArray(new Node[0])) {
                                            if (headerItem instanceof Label label && label.getText().contains("NEW")) {
                                                headerBox.getChildren().remove(label);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error marking notifications as read: " + ex.getMessage());
                    }
                });
                
                Button clearAllButton = new Button("Supprimer toutes les notifications");
                clearAllButton.setStyle("-fx-text-fill: #e74c3c;");
                clearAllButton.setOnAction(e -> {
                    try {
                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirmation");
                        confirmation.setHeaderText("Supprimer toutes les notifications");
                        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer toutes vos notifications ?");
                        
                        Optional<ButtonType> result = confirmation.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            // Delete all notifications for this user
                            for (Notification notification : notifications) {
                                notificationService.deleteNotification(notification.getId());
                            }
                            
                            unreadNotificationsCount = 0;
                            updateNotificationBadge();
                            dialog.close();
                            
                            showAlert(Alert.AlertType.INFORMATION, "Notifications", 
                                "Toutes les notifications ont été supprimées.");
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error deleting notifications: " + ex.getMessage());
                    }
                });
                
                actionButtons.getChildren().addAll(markAllReadButton, clearAllButton);
                contentBox.getChildren().add(actionButtons);
                
                // Set content and buttons
                ScrollPane scrollPane = new ScrollPane(contentBox);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(400);
                scrollPane.setPrefWidth(500);
                
                dialog.getDialogPane().setContent(scrollPane);
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.getDialogPane().setPrefWidth(520);
                
                dialog.showAndWait();
                
                // After dialog closes, refresh notification count
                loadNotificationCount();
            }
        } catch (SQLException e) {
            System.err.println("Error showing notifications: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void analyzeOffre() {
        if (offreSelectionnee == null) {
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
                String description = offreSelectionnee.getDescription();
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
        if (offreSelectionnee == null) {
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
                    String description = offreSelectionnee.getDescription();
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

    // Add this new method for showing offer details
    private void showOfferDetails(OffreEmploi offre) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Détails de l'offre");
        dialog.initModality(Modality.APPLICATION_MODAL);

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

        // Make content scrollable
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setPrefWidth(600);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.show();
    }
}
