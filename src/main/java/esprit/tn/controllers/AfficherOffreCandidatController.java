package esprit.tn.controllers;

import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import esprit.tn.entities.OffreEmploi;
import esprit.tn.entities.Bookmark;
import esprit.tn.services.ServiceCandidature;
import esprit.tn.services.ServiceOffre;
import esprit.tn.services.ServiceBookmark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    
    // Icons for bookmark states
    private final Image bookmarkEmpty = new Image(getClass().getResourceAsStream("/images/notSaved.png"));
    private final Image bookmarkFilled = new Image(getClass().getResourceAsStream("/images/Saved.png"));

    @FXML
    void initialize() {
        Postuler.setDisable(true);

        bookmarkIcon.setImage(bookmarkEmpty);
        bookmarkIcon.setVisible(false);
        
        refreshData();
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
}
