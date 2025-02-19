package esprit.tn.controllers;
import esprit.tn.entities.Typeformation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceFormation;

import esprit.tn.entities.Formation;
import esprit.tn.entities.Reservation;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherListFormationController {
    @FXML
    private ListView listformationid;
    @FXML
    private HBox Vrechcerche;
    @FXML
    private TextField Trecherche;
    @FXML
    private Button Btnrecherche;

    private final ServiceFormation formationService = new ServiceFormation();
    @FXML
    private Button btnD;
    @FXML
    private Button btnP;

    @FXML
    public void initialize() {
        try {
            ObservableList<Formation> formationsList = FXCollections.observableArrayList(formationService.getListFormation());
            listformationid.setItems(formationsList);

            setupListView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupListView() {
        listformationid.setCellFactory(new Callback<ListView<Formation>, ListCell<Formation>>() {
            @Override
            public ListCell<Formation> call(ListView<Formation> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Formation formation, boolean empty) {
                        super.updateItem(formation, empty);

                        if (empty || formation == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Création des éléments d'affichage
                            ImageView imageView = new ImageView();
                            imageView.setFitHeight(150);
                            imageView.setFitWidth(200);

                            if (formation.getPhoto() != null) {
                                imageView.setImage(new Image(formation.getPhoto().toString()));
                            }

                            Label titreLabel = new Label( formation.getDescription());
                            titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");

                            Label descriptionLabel = new Label("Description:"+formation.getTitre());
                            descriptionLabel.setStyle("-fx-font-size: 14px");
                            Label dateLabel = new Label("Date: " + formation.getDate().toString());
                            dateLabel.setStyle("-fx-font-size: 14px");
                            Label heureDebutLabel = new Label("Heure de Début: " + formation.getHeure_debut().toString());
                            heureDebutLabel.setStyle("-fx-font-size: 14px");
                            Label heureFinLabel = new Label("Heure de Fin: " + formation.getHeure_fin().toString());
                            heureFinLabel.setStyle("-fx-font-size: 14px");
                            Label nbPlacesLabel = new Label("Nombre de Places: " + formation.getNb_place());
                            nbPlacesLabel.setStyle("-fx-font-size: 14px");

                            Button reservationButton = new Button("Reserver");
                            reservationButton .setStyle("-fx-background-color: #22859c; -fx-text-fill: white;");
                            reservationButton.setOnAction(event -> {
                                // Récupérer la formation sélectionnée (assurez-vous que `formationId` est disponible)
                                try {
                                    // Charger la scène AjouterReservation.fxml
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
                                    Parent root = loader.load();

                                    // Récupérer le contrôleur AjouterReservationController
                                    AjouterReservationController controller = loader.getController();
                                    controller.setUser(formation.getUser());
                                    // Passer l'ID de la formation et l'ID utilisateur
                                    controller.setFormationId(formation.getId_f());  // Passer l'ID de la formation
                                    controller.setUserId(1); // Remplacer par l'ID de l'utilisateur connecté
                                    // Changer de scène
                                    Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                    stage.getScene().setRoot(root);
                                } catch (IOException e) {
                                    System.out.println("Erreur de chargement de la page de réservation : " + e.getMessage());
                                }
                            });

                            // Conteneur pour aligner les boutons à droite
                            HBox buttonContainer = new HBox(10,reservationButton);
                            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                            buttonContainer.setPadding(new Insets(30, 10, 10, 50)); // Marges autour des boutons

                            // Conteneur pour les infos
                            VBox infoBox = new VBox(5, titreLabel, descriptionLabel, dateLabel, heureDebutLabel, heureFinLabel, nbPlacesLabel);

                            // Conteneur principal avec l'image, les infos et les boutons
                            HBox mainBox = new HBox(10, imageView, infoBox);
                            mainBox.setAlignment(Pos.CENTER_LEFT);
                            mainBox.setPadding(new Insets(10));

                            // Ajout du conteneur des boutons à droite
                            HBox fullBox = new HBox(10, mainBox, buttonContainer);
                            fullBox.setAlignment(Pos.CENTER_LEFT); // Laisse les boutons à droite automatiquement

                            setGraphic(fullBox);
                        }
                    }
                };
            }
        });
    }


    private void ajouterreservation(Reservation reservation) {}


    public void setReservation(Reservation reservation) {

    }


    public void OnchercherFormation(ActionEvent actionEvent) throws SQLException {
        String searchText = Trecherche.getText();

        // Liste des formations (vous pouvez remplacer cela par la liste de formations réelle)
        List<Formation> allFormations = formationService.getListFormation(); // Remplacez par votre méthode pour obtenir la liste des formations

        // Filtrer les formations avec Stream en fonction du texte de recherche
        List<Formation> filteredFormations = allFormations.stream()
                .filter(formation -> formation.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        // Mettre à jour l'affichage des résultats de la recherche
        updateFormationListView(filteredFormations);

    }

    private void updateFormationListView(List<Formation> filteredFormations) {
        // Convertir la liste filtrée en ObservableList pour l'affichage
        ObservableList<Formation> observableList = FXCollections.observableArrayList(filteredFormations);
        listformationid.setItems(observableList);
    }

    @FXML
    public void Ondis(ActionEvent actionEvent) throws SQLException {
        List<Formation> allFormations = formationService.getListFormation();

        // Filtrer les formations en présentiel
        List<Formation> filteredFormations = allFormations.stream()
                .filter(formation -> formation.getType() == Typeformation.Présentiel)
                .collect(Collectors.toList());

        // Mettre à jour l'affichage
        updateFormationListView(filteredFormations);
    }

    @FXML
    public void Onpres(ActionEvent actionEvent) throws SQLException {
        List<Formation> allFormations = formationService.getListFormation();

        // Filtrer les formations en distanciel
        List<Formation> filteredFormations = allFormations.stream()
                .filter(formation -> formation.getType() == Typeformation.Distanciel)
                .collect(Collectors.toList());

        // Mettre à jour l'affichage
        updateFormationListView(filteredFormations);
    }


}
