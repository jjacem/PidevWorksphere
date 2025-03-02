package esprit.tn.controllers;

import esprit.tn.entities.OffreEmploi;
import esprit.tn.services.ServiceBookmark;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherFavorisController {
    private ObservableList<OffreEmploi> favorisOffreList = FXCollections.observableArrayList();
    private OffreEmploi offreSelectionnee;

    @FXML
    private Button retourBtn;

    @FXML
    private Button supprimerFavori;

    @FXML
    private ListView<OffreEmploi> lv_favoris;
    
    private final Image bookmarkFilled = new Image(getClass().getResourceAsStream("/images/bookmark_filled.png"));

    @FXML
    void initialize() {
        supprimerFavori.setDisable(true);
        chargerFavoris();
        setupListView();
    }

    @FXML
    private void retourOffres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherOffreCandidat.fxml"));
            Parent root = loader.load();
            
            // Get the controller and call refreshData
            AfficherOffreCandidatController controller = loader.getController();
            controller.refreshData();

            Stage stage = (Stage) retourBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerFavori() {
        if (offreSelectionnee != null) {
            try {
                ServiceBookmark serviceBookmark = new ServiceBookmark();
                var currentUser = SessionManager.extractuserfromsession();
                
                if (currentUser != null) {
                    int userId = currentUser.getIdUser();
                    int offreId = offreSelectionnee.getIdOffre();
                    
                    // Remove bookmark from database
                    serviceBookmark.supprimerParUserEtOffre(userId, offreId);
                    
                    // Remember index for reselection
                    int currentIndex = lv_favoris.getSelectionModel().getSelectedIndex();
                    
                    // Remove from our list
                    favorisOffreList.remove(offreSelectionnee);
                    
                    // Reset the selection reference
                    offreSelectionnee = null;
                    
                    // Force refresh the list view (important step!)
                    lv_favoris.refresh();
                    
                    // Try to select an item near the one we just removed
                    if (!favorisOffreList.isEmpty()) {
                        int newIndex = Math.min(currentIndex, favorisOffreList.size() - 1);
                        if (newIndex >= 0) {
                            lv_favoris.getSelectionModel().clearSelection();
                            lv_favoris.getSelectionModel().select(newIndex);
                        }
                    } else {
                        // No more items, disable the button
                        supprimerFavori.setDisable(true);
                    }
                    
                    System.out.println("Item removed. Remaining items: " + favorisOffreList.size() + 
                                      ", Selected index: " + lv_favoris.getSelectionModel().getSelectedIndex());
                }
            } catch (SQLException e) {
                System.out.println("Error removing bookmark: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void setupListView() {
        lv_favoris.setCellFactory(lv -> new ListCell<OffreEmploi>() {
            private final ImageView bookmarkView = new ImageView(bookmarkFilled);
            private final HBox content = new HBox();
            private final Text text = new Text();

            {
                bookmarkView.setFitHeight(24);
                bookmarkView.setFitWidth(24);
                text.setWrappingWidth(500);
                
                VBox textBox = new VBox(text);
                HBox.setHgrow(textBox, Priority.ALWAYS);
                
                content.getChildren().addAll(bookmarkView, textBox);
                content.setSpacing(10);
            }
            
            @Override
            protected void updateItem(OffreEmploi offre, boolean empty) {
                super.updateItem(offre, empty);
                if (empty || offre == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    text.setText(formaterOffre(offre));
                    setGraphic(content);
                }
            }
        });

        lv_favoris.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                offreSelectionnee = newValue;
                supprimerFavori.setDisable(false);
                System.out.println("Selected: " + newValue.getTitre() + " (ID: " + newValue.getIdOffre() + ")");
            } else {
                offreSelectionnee = null;
                supprimerFavori.setDisable(true);
                System.out.println("No selection");
            }
        });
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

    // Méthode pour charger les offres favorites depuis la base de données
    void chargerFavoris() {
        ServiceBookmark serviceBookmark = new ServiceBookmark();
        try {
            // Get the current user
            var currentUser = SessionManager.extractuserfromsession();
            if (currentUser != null) {
                // Récupérer toutes les offres favorites de l'utilisateur
                List<OffreEmploi> favoris = serviceBookmark.recupererOffresBookmarkees(currentUser.getIdUser());
                favorisOffreList.clear();
                favorisOffreList.addAll(favoris);
                lv_favoris.setItems(favorisOffreList);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des favoris : " + e.getMessage());
        }
    }
    
    // Let's add a method to print the current state for debugging
    private void debugListState() {
        System.out.println("=== List State Debug ===");
        System.out.println("Items in list: " + favorisOffreList.size());
        System.out.println("Selected index: " + lv_favoris.getSelectionModel().getSelectedIndex());
        System.out.println("Selected item: " + (offreSelectionnee != null ? offreSelectionnee.getTitre() : "null"));
        System.out.println("Button disabled: " + supprimerFavori.isDisabled());
        System.out.println("=======================");
    }
}
