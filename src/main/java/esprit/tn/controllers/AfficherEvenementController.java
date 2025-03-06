

package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.Role;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.utils.DateUtilEvent;
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
import java.time.LocalDate;
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
            // on a une ListView<Evenement> (lv_event), qui doit afficher une liste d'événements
            // récupérés depuis la base de données.
            //Cependant, une simple List<Evenement> ne permettrait pas d'actualiser
            // l'affichage automatiquement si des événements sont modifiés.
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

                        // Calculer le message des jours restants ou si l'événement est passé


                        //evenement.getDateEvent() retourne une date sous forme de java.sql.Date
                        // (qui est utilisée pour stocker des dates dans une base de données SQL).
                        //.toLocalDate() convertit cette java.sql.Date en un LocalDate, qui est une
                        // classe plus moderne de Java (Java 8+ avec java.time API).
                        //eventDate contiendra donc la date de l'événement sous forme de LocalDate
                        LocalDate eventDate = evenement.getDateEvent().toLocalDate(); // Supposons que getDateEvent() retourne un java.sql.Date
                        //Cette méthode calcule le nombre de jours restants avant l'événement et
                        // retourne un message formaté sous forme de String.
                        //Si aujourd'hui on est le 6 mars 2025 et que l'événement est le 10 mars 2025, alors joursRestantsMessage pourrait être :
                        //👉 "L'événement aura lieu dans 4 jours"
                        String joursRestantsMessage = DateUtilEvent.getDaysRemainingMessage(eventDate);
                        Text textJoursRestants = new Text(joursRestantsMessage);

                        // Appliquer un style différent en fonction du message
                        //La méthode getStyleClass() en JavaFX est utilisée pour accéder
                        // et manipuler la liste des classes CSS associées à un élément graphique
                        if (joursRestantsMessage.contains("passé")) {
                            textJoursRestants.getStyleClass().add("event-passed-text"); // Style pour les événements passés
                        } else if (joursRestantsMessage.contains("aujourd'hui")) {
                            textJoursRestants.getStyleClass().add("event-today-text"); // Style pour les événements aujourd'hui
                        } else {
                            textJoursRestants.getStyleClass().add("event-remaining-text"); // Style pour les événements à venir
                        }

                        // Ajouter les éléments au VBox
                        vbox.getChildren().addAll(nomEvent, descEvent, dateEvent, lieuEvent, capaciteEvent, textJoursRestants);

                        // Créer un HBox pour les boutons
                        //Le nombre 10 passé à HBox(10) représente l'espacement entre les éléments enfants de la HBox.
                        HBox hboxButtons = new HBox(10);
                        hboxButtons.setAlignment(Pos.CENTER_RIGHT);

                        // Bouton "Afficher sur la carte"
                        Button btnAfficherCarte = new Button("\uD83D\uDCCD");
                        btnAfficherCarte.setStyle("-fx-background-color: #0086b3; -fx-text-fill: white;");

                        btnAfficherCarte.setOnAction(event -> {
                            try {
                                //charger lmap
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherCarte.fxml"));
                                //root récupère l'élément racine de l'interface définie dans le fichier FXML
                                Parent root = loader.load();
                                //permet de récupérer le contrôleur associé au fichier FXML
                                AfficherCarteController carteController = loader.getController();
                                if (carteController == null) {
                                    System.out.println("Erreur : Le contrôleur AfficherCarteController est null !");
                                    return;
                                }

                                String coor = NominatimAPI.getCoordinates(evenement.getLieuEvent());
                                System.out.println("Coordonnées récupérées pour " + evenement.getLieuEvent() + " : " + coor);

                                if (coor != null && !coor.isEmpty()) {
                                    //Cette étape consiste à nettoyer la chaîne de caractères contenant les coordonnées
                                    // en supprimant les termes "Latitude: " et " Longitude: " ainsi que les espaces. Ensuite,
                                    // les coordonnées formatées sont affichées dans la console pour faciliter le débogage.
                                    coor = coor.replace("Latitude: ", "").replace(" Longitude: ", "").replace(" ", "");
                                    System.out.println("Coordonnées formatées pour la carte : " + coor);
                                    //passer les coordonnees au controlleur
                                    carteController.initData(coor);
                                } else {
                                    System.out.println("Erreur : Impossible de récupérer les coordonnées du lieu.");
                                }

                                Stage mapStage = new Stage();
                                mapStage.setTitle("Carte de l'événement");
                                mapStage.setScene(new Scene(root));
                                //La méthode initModality(APPLICATION_MODAL) assure que la fenêtre de la carte soit modale,
                                // c'est-à-dire que
                                // l'utilisateur doit la fermer avant de pouvoir interagir avec les autres fenêtres de l'application.
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
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvenement.fxml"));
                                    Parent root = loader.load();

                                    ModifierEvenementController modifierController = loader.getController();
                                    modifierController.initData(evenement);

                                    Stage popupStage = new Stage();
                                    popupStage.setTitle("Modifier l'Événement");
                                    popupStage.setScene(new Scene(root));
                                    popupStage.initModality(Modality.APPLICATION_MODAL);
                                    popupStage.showAndWait();

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
            popupStage.setTitle("➕ Ajouter un Événement");
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
        //Récupération du texte de recherche+supprime les espaces inutiles au début et à la fin du texte saisi.
        String searchText = txtRechercheEvent.getText().trim();
        ServiceEvenement serviceEvent = new ServiceEvenement();
        List<Evenement> allEvenement = serviceEvent.afficher();
        //Collecte les résultats filtrés dans une nouvelle liste filteredEvenement.
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