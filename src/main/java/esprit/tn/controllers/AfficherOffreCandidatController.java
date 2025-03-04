package esprit.tn.controllers;

import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.entities.Bookmark;
import esprit.tn.entities.Notification;
import esprit.tn.services.ServiceCandidature;
import esprit.tn.services.ServiceOffre;
import esprit.tn.services.ServiceBookmark;
import esprit.tn.services.NotificationService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AfficherOffreCandidatController {
    private ObservableList<OffreEmploi> offreList = FXCollections.observableArrayList();
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
    private ImageView bookmarkIcon;
    @FXML
    private Button notificationsButton;
    @FXML
    private Label notificationCountLabel;
    
    // Icons for bookmark states
    private final Image bookmarkEmpty = new Image(getClass().getResourceAsStream("/images/notSaved.png"));
    private final Image bookmarkFilled = new Image(getClass().getResourceAsStream("/images/Saved.png"));

    private NotificationService notificationService = new NotificationService();
    private int unreadNotificationsCount = 0;

    @FXML
    void initialize() {
        Postuler.setDisable(true);

        bookmarkIcon.setImage(bookmarkEmpty);
        bookmarkIcon.setVisible(false);
        
        refreshData();

        loadNotificationCount();

        // Add listener for searching
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchOffres(newValue);
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
            for (OffreEmploi offre : offreList) {
                if (offre.getIdOffre() == idToSelect) {
                    lv_offre.getSelectionModel().select(offre);
                    break;
                }
            }
        }
        
        // Refresh notifications count
        loadNotificationCount();
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

    @FXML
    private void voirFavoris() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherFavoris.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) voirFavorisBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

                        setStyle("-fx-background-color: #ADD8E6;"); // Light blue for applied
                    } else if (bookmarkedOfferIds.contains(offre.getIdOffre())) {
                        setStyle("-fx-background-color: #FFFACD;"); // Light yellow for bookmarked
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

                bookmarkIcon.setVisible(true);
                
                // Update bookmark icon based on status
                if (bookmarkedOfferIds.contains(newValue.getIdOffre())) {
                    bookmarkIcon.setImage(bookmarkFilled);
                } else {
                    bookmarkIcon.setImage(bookmarkEmpty);
                }
            } else {
                Postuler.setDisable(true);
                bookmarkIcon.setVisible(false);
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

    // Méthode pour rechercher des offres en fonction du terme de recherche
    private void searchOffres(String searchTerm) {
        ServiceOffre serviceOffre = new ServiceOffre();

        try {
            List<OffreEmploi> resultats = serviceOffre.rechercherOffres(searchTerm);
            offreList.clear();

            for (OffreEmploi offre : resultats) {
                offreList.add(offre);
            }

            lv_offre.setItems(offreList);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
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
}
