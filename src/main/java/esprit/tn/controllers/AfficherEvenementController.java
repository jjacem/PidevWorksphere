

package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.Role;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.utils.NominatimAPI;
import esprit.tn.utils.SessionManager;
import esprit.tn.utils.WeatherAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherEvenementController {

    @FXML
    private ListView<Evenement> lv_event;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TextField txtRechercheEvent;

    private ObservableList<Evenement> observableList;

    @FXML
    private Button btnAjouterEvent;

    @FXML
    private Button btnretourdashRH;

    @FXML
    void initialize() {
        ServiceEvenement serviceEvenement = new ServiceEvenement();

        // Vérifie si le rôle de l'utilisateur est RH, sinon cache le bouton
        if (!SessionManager.getRole().equals(Role.RH.name())) {
            btnAjouterEvent.setVisible(false);
            btnretourdashRH.setVisible(false);
        }

        try {
            List<Evenement> evenementList = serviceEvenement.afficher();
            observableList = FXCollections.observableList(evenementList);
            lv_event.setItems(observableList);

            // Configuration de la cellule personnalisée pour afficher les événements
            lv_event.setCellFactory(param -> new ListCell<Evenement>() {



               @Override
               protected void updateItem(Evenement evenement, boolean empty) {
                   super.updateItem(evenement, empty);
                   if (empty || evenement == null) {
                       setText(null);
                       setGraphic(null);
                   } else {
                       // Créer un VBox pour organiser les informations de l'événement
                       VBox vbox = new VBox(5);
                       vbox.setPadding(new Insets(10));

                       // Nom de l'événement
                       Text nomEvent = new Text("Nom: " + evenement.getNomEvent());
                       nomEvent.getStyleClass().add("event-text");

                       // Description de l'événement
                       Text descEvent = new Text("Description: " + evenement.getDescEvent());
                       descEvent.getStyleClass().add("event-text");

                       // Date de l'événement
                       Text dateEvent = new Text("Date: " + evenement.getDateEvent());
                       dateEvent.getStyleClass().add("event-text");

                       // Lieu de l'événement
                       Text lieuEvent = new Text("Lieu: " + evenement.getLieuEvent());
                       lieuEvent.getStyleClass().add("event-text");

                       // Capacité de l'événement
                       Text capaciteEvent = new Text("Capacité: " + evenement.getCapaciteEvent());
                       capaciteEvent.getStyleClass().add("event-text");

                       // Coordonnées géographiques du lieu
                       String coordinates = NominatimAPI.getCoordinates(evenement.getLieuEvent());
                       Text coordonneesText = new Text("Coordonnées: " + coordinates);
                       coordonneesText.getStyleClass().add("event-text");

                       // Ajouter les éléments au VBox
                       vbox.getChildren().addAll(nomEvent, descEvent, dateEvent, lieuEvent, capaciteEvent, coordonneesText);

                       // Créer un HBox pour les boutons
                       HBox hboxButtons = new HBox(10);
                       hboxButtons.setAlignment(Pos.CENTER_RIGHT);


                       Button btnAfficherCarte = new Button("Afficher sur la carte");
                       btnAfficherCarte.setOnAction(event -> {
                           try {
                               FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCarte.fxml"));
                               Parent root = loader.load();

                               AfficherCarteController carteController = loader.getController();
                               if (carteController == null) {
                                   System.out.println("Erreur : Le contrôleur AfficherCarteController est null !");
                                   return;
                               }

                               //carteController.initData("36.829875, 10.145411"); // Exemple de coordonnées
                               String coor = NominatimAPI.getCoordinates(evenement.getLieuEvent());
                               System.out.println("Coordonnées récupérées pour " + evenement.getLieuEvent() + " : " + coor);

                               if (coor != null && !coor.isEmpty()) {
                                   coor = coor.replace("Latitude: ", "").replace(" Longitude: ", "").replace(" ", "");
                                   System.out.println("Coordonnées formatées pour la carte : " + coor);
                                   carteController.initData(coor);
                               } else {
                                   System.out.println("Erreur : Impossible de récupérer les coordonnées du lieu.");
                               }


                               Stage mapStage = new Stage();
                               mapStage.setTitle("Carte de l'événement");
                               mapStage.setScene(new Scene(root));
                               mapStage.initModality(Modality.APPLICATION_MODAL);
                               mapStage.showAndWait();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       });


                       // Ajouter le bouton "Afficher sur la carte" à la HBox
                       hboxButtons.getChildren().add(btnAfficherCarte);

                       // Si l'utilisateur est RH, ajouter les boutons Modifier et Supprimer
                       if (SessionManager.getRole().equals(Role.RH.name())) {
                           // Bouton Modifier
                           Button btnModifierEvent = new Button("Modifier");
                           btnModifierEvent.getStyleClass().add("btn-modifierEvent");
                           btnModifierEvent.setOnAction(event -> {
                               try {
                                   // Charger le fichier FXML pour la modification de l'événement
                                   FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenement.fxml"));
                                   Parent root = loader.load();

                                   // Passer l'événement à modifier au contrôleur ModifierEvenementController
                                   ModifierEvenementController modifierController = loader.getController();
                                   modifierController.initData(evenement);

                                   // Créer une nouvelle scène et afficher le popup
                                   Stage popupStage = new Stage();
                                   popupStage.setTitle("Modifier l'Événement");
                                   popupStage.setScene(new Scene(root));
                                   popupStage.initModality(Modality.APPLICATION_MODAL); // Empêcher l'interaction avec la fenêtre principale
                                   popupStage.showAndWait(); // Attendre que l'utilisateur ferme la fenêtre de modification

                                   // Rafraîchir la liste des événements après la modification
                                   reloadEvenementsList();
                               } catch (IOException e) {
                                   e.printStackTrace();
                               }
                           });

                           // Bouton Supprimer
                           Button btnSupprimerEvent = new Button("Supprimer");
                           btnSupprimerEvent.getStyleClass().add("btn-supprimerEvent");
                           btnSupprimerEvent.setOnAction(event -> {
                               try {
                                   Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                   alertConfirmation.setTitle("Confirmation");
                                   alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer cet événement ?");
                                   alertConfirmation.setContentText("Cette action est irréversible.");

                                   if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                       int eventId = evenement.getIdEvent();
                                       serviceEvenement.supprimer(eventId);
                                       observableList.remove(evenement);

                                       Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                       alert.setTitle("Succès");
                                       alert.setHeaderText("Suppression réussie");
                                       alert.setContentText("L'événement a été supprimé avec succès.");
                                       alert.showAndWait();
                                   }
                               } catch (SQLException e) {
                                   Alert alert = new Alert(Alert.AlertType.ERROR);
                                   alert.setTitle("Erreur");
                                   alert.setHeaderText("Erreur lors de la suppression");
                                   alert.setContentText("Erreur : " + e.getMessage());
                                   alert.showAndWait();
                               }
                           });

                           hboxButtons.getChildren().addAll(btnModifierEvent, btnSupprimerEvent);
                           vbox.getChildren().add(hboxButtons);
                       }

                       // Appliquer le style au VBox
                       vbox.getStyleClass().add("hbox-item");
                       setGraphic(vbox);
                   }
               }
            });
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void OnajouterEvent(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML du formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvenement.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage popupStage = new Stage();
            popupStage.setTitle("Ajouter un Événement");
            popupStage.setScene(new Scene(root));

            // Empêcher l'interaction avec la fenêtre principale tant que la popup est ouverte
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // Afficher la popup
            popupStage.showAndWait();

            // Rafraîchir la liste des événements après la fermeture du popup
            reloadEvenementsList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadEvenementsList() {
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        try {
            List<Evenement> evenementList = serviceEvenement.afficher();
            observableList = FXCollections.observableList(evenementList);
            lv_event.setItems(observableList);
        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    @FXML
    public void OnchercherEvent(ActionEvent actionEvent) throws SQLException {
        String searchText = txtRechercheEvent.getText().trim();
        ServiceEvenement serviceEvent = new ServiceEvenement();
        List<Evenement> allEvenement = serviceEvent.afficher();

        List<Evenement> filteredEvenement = allEvenement.stream()
                .filter(e -> e.getNomEvent().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        updateFormationListView(filteredEvenement);
    }

    private void updateFormationListView(List<Evenement> filteredEvenement) {
        ObservableList<Evenement> observableList = FXCollections.observableArrayList(filteredEvenement);
        lv_event.setItems(observableList);
    }
}