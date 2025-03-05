package esprit.tn.controllers;
import esprit.tn.entities.Favoris;
import esprit.tn.entities.Typeformation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceFavori;
import esprit.tn.services.ServiceFormation;

import esprit.tn.entities.Formation;

import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherListFormationController {

    private final ServiceFormation formationService = new ServiceFormation();
    private final ServiceFavori favori = new ServiceFavori();
    private final List<Formation> favoriteList = new ArrayList<>();
    @FXML
    private Button Btnrecherche;
    @FXML
    private TextField Trecherche;
    @FXML
    private VBox formationsContainer;
    @FXML
    private HBox mainContainer;
    @FXML
    private VBox listformationid;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button Btnfavori;
    @FXML
    private ComboBox <String> typeFilter;
    @FXML
    private ComboBox <String>  dateFilter;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        listformationid.getStyleClass().addAll("list", "list-view");
        populateFormations();
        initializeFilters();
    }
    private void initializeFilters() {
        typeFilter.setItems(FXCollections.observableArrayList("Tous", "Distanciel", "Présentiel"));
        typeFilter.setValue("Tous");

        dateFilter.setItems(FXCollections.observableArrayList(
                "Toutes dates", "Semaine prochaine", "15 jours prochains", "Mois prochain"
        ));
        dateFilter.setValue("Toutes dates");

        typeFilter.setOnAction(event -> filterFormations());
        dateFilter.setOnAction(event -> filterFormations());
    }
    private void filterFormations() {
        String searchText = Trecherche.getText().toLowerCase();
        String selectedType = typeFilter.getValue();
        String selectedDate = dateFilter.getValue();
        LocalDate today = LocalDate.now();

        try {
            List<Formation> formations = formationService.getListFormation();

            List<Formation> filteredFormations = formations.stream()
                    .filter(f -> searchText.isEmpty() || f.getTitre().toLowerCase().contains(searchText))
                    .filter(f -> {
                        if ("Tous".equals(selectedType)) return true;
                        return "Distanciel".equals(selectedType) ? f.getType() == Typeformation.Distanciel : f.getType() == Typeformation.Présentiel;
                    })
                    .filter(f -> {
                        LocalDate formationDate = f.getDate();
                        long daysBetween = ChronoUnit.DAYS.between(today, formationDate);
                        switch (selectedDate) {
                            case "Semaine prochaine": return daysBetween >= 0 && daysBetween <= 7;
                            case "15 jours prochains": return daysBetween >= 0 && daysBetween <= 15;
                            case "Mois prochain": return daysBetween >= 0 && daysBetween <= 30;
                            default: return true;
                        }
                    })
                    .collect(Collectors.toList());

            displayFormations(filteredFormations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void displayFormations(List<Formation> formations) {
        listformationid.getChildren().clear();
        for (Formation formation : formations) {
            listformationid.getChildren().add(createFormationBox(formation));
        }
    }

    private void populateFormations() {
        listformationid.getChildren().clear();  // Nettoyer avant de recharger

        try {
            List<Formation> formations = formationService.getListFormation();
            for (Formation formation : formations) {
                HBox formationBox = createFormationBox(formation);
                formationBox.getStyleClass().addAll("list", "list-cell");
                listformationid.getChildren().add(formationBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour créer la boîte de chaque formation avec ses informations
    private HBox createFormationBox(Formation formation) {

        // Ajouter l'image du projet
        ImageView imageView = new ImageView();
        if (formation.getPhoto() != null && !formation.getPhoto().trim().isEmpty()) {
            String correctPath = "C:/xampp/htdocs/img/" + new File(formation.getPhoto()).getName();
            File imageFile = new File(correctPath);
            if (imageFile.exists() && imageFile.isFile()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
            }
        } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
        }
        imageView.setFitHeight(150);
        imageView.setFitWidth(200);

        // Création des labels pour les informations de la formation
        Label titreLabel = new Label(formation.getTitre());
        titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #22859c;");

        Label descriptionLabel = new Label("Description: " + formation.getDescription());
        Label dateLabel = new Label("Date: " + formation.getDate().toString());
        Label heureDebutLabel = new Label("Heure de Début: " + formation.getHeure_debut().toString());
        Label heureFinLabel = new Label("Heure de Fin: " + formation.getHeure_fin().toString());
        Label nbPlacesLabel = new Label("Nombre de Places: " + formation.getNb_place());


        // Calculer le temps restant avant la formation
        LocalDate today = LocalDate.now();
        //LocalDate formationDate = reservation.getDate();
        LocalDate formationDate = formation.getDate();

        long totalDays = ChronoUnit.DAYS.between(today, formationDate);
        long maxDays = 30; // Durée max pour la barre de progression
        double progress = (totalDays <= 0) ? 1.0 : 1.0 - ((double) totalDays / maxDays);

        // Créer la ProgressBar et le Label
        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.getStyleClass().add("progress-bar");

        Label progressLabel = new Label(totalDays > 0 ? totalDays + " jours restants" : "Formation commencée !");
        progressLabel.getStyleClass().add("progress-text");




        VBox infoBox = new VBox(5, titreLabel, descriptionLabel, dateLabel, heureDebutLabel, heureFinLabel, nbPlacesLabel , progressBar,progressLabel);
        Button detailButton = new Button("Detail");
        detailButton.getStyleClass().addAll("card-button", "details-button");
        detailButton.setOnAction(event -> afficherDetails(formation));

        Button reserverButton = new Button("Reserver");
        reserverButton.getStyleClass().addAll("card-button", "button-reserver");
        reserverButton.setOnAction(event -> {
            try {
                // Charger la scène AjouterReservation.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur AjouterReservationController
                AjouterReservationController controller = loader.getController();

                // Passer l'ID de la formation
                controller.setFormationId(formation.getId_f());

                // Créer une nouvelle fenêtre (Stage)
                Stage popupStage = new Stage();
                popupStage.setTitle("Ajouter une réservation");

                // Empêcher l'interaction avec la fenêtre principale tant que le popup est ouvert
                popupStage.initModality(Modality.APPLICATION_MODAL);

                // Ajouter le formulaire au stage et l'afficher
                Scene scene = new Scene(root);
                popupStage.setScene(scene);
                popupStage.showAndWait(); // Attendre la fermeture de la fenêtre
            } catch (IOException e) {
                System.out.println("Erreur de chargement du popup : " + e.getMessage());
            }
        });
        Button favButton = new Button("❤");
        favButton.getStyleClass().add("button-favheart");
        updateFavoriteButtonStyle(favButton, formation);

        favButton.setOnAction(event -> toggleFavorite(formation, favButton));

        HBox buttonContainer = new HBox(10,detailButton, reserverButton , favButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(10));

        HBox formationBox = new HBox(10, imageView, infoBox, buttonContainer);
        formationBox.setAlignment(Pos.CENTER_LEFT);
        formationBox.setStyle("-fx-padding: 10px; -fx-border-color: lightgray; -fx-border-radius: 5px;");
        return formationBox;
    }

    private void updateFavoriteButtonStyle(Button favButton, Formation formation) {
        try {
            User user = SessionManager.extractuserfromsession();
            int userId = user.getIdUser();
            if (favori.estFavori(userId, formation.getId_f())) {
                favButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            }
            else {
                favButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void toggleFavorite(Formation formation, Button favButton) {
        try {
            User user = SessionManager.extractuserfromsession();
            int userId = user.getIdUser();

            // Créer un objet Favoris avec l'utilisateur et la formation
            Favoris favoris = new Favoris(userId, formation.getId_f());

            if (favori.estFavori(userId, formation.getId_f())) {
                favori.supprimerFavori(userId, formation.getId_f());
                showAlert("Favoris", "Formation supprimée des favoris !");
            } else {
                favori.ajouterFavori(userId, formation.getId_f());
                showAlert("Favoris", "Formation ajoutée aux favoris !");
            }

            // Met à jour le style du bouton pour refléter l'état des favoris
            updateFavoriteButtonStyle(favButton, formation);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherDetails(Formation formation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailFormation.fxml"));
            Parent root = loader.load();

            AfficherDetailFormationController controller = loader.getController();
            controller.setFormation(formation);

            Stage stage = new Stage();
            stage.setTitle("Détails de la Formation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void OnchercherFormation() {
        String searchText = Trecherche.getText().toLowerCase();

        try {
            List<Formation> allFormations = formationService.getListFormation();
            List<Formation> filteredFormations = allFormations.stream()
                    .filter(formation -> formation.getTitre().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            formationsContainer.getChildren().clear();
            for (Formation formation : filteredFormations) {
                formationsContainer.getChildren().add(createFormationBox(formation));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void OnAfficherFavori(ActionEvent actionEvent) {
        try {
            User user = SessionManager.extractuserfromsession();
            int userId = user.getIdUser();

            // Récupère la liste des favoris sous forme d'objets 'Favoris'
            List<Favoris> favoris = favori.getFavoris(userId);

            // Crée une nouvelle fenêtre pour afficher les favoris
            Stage favStage = new Stage();
            favStage.setTitle("Favoris");

            VBox vbox = new VBox(10);
            ListView<Formation> listView = new ListView<>();

            // Ajout des formations aux favoris
            List<Formation> formations = favoris.stream()
                    .map(Favoris::getFormation) // Récupère la formation de chaque objet Favoris
                    .collect(Collectors.toList());

            listView.getItems().addAll(formations);  // Ajout des formations dans le ListView

            // Définir le comportement des cellules dans le ListView
            listView.setCellFactory(param -> new ListCell<Formation>() {
                private final Button deleteButton = new Button("Supprimer");

                {
                    // Style du bouton de suppression
                    deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> {
                        Formation item = getItem();
                        if (item != null) {
                            try {
                                // Supprimer le favori de la base de données
                                favori.supprimerFavori(userId, item.getId_f());

                                // Mettre à jour la vue pour enlever l'élément supprimé
                                listView.getItems().remove(item);
                                showAlert("Favoris", "Formation supprimée des favoris !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                protected void updateItem(Formation formation, boolean empty) {
                    super.updateItem(formation, empty);
                    if (empty || formation == null) {
                        setGraphic(null);
                    } else {
                        // Afficher les informations de la formation et le bouton de suppression
                        Label titleLabel = new Label("Titre: " + formation.getTitre());
                        Label dateLabel = new Label("Date: " + formation.getDate().toString());
                        Label hdLabel = new Label("Heure de début: " + formation.getHeure_debut().toString());
                        Label hfLabel = new Label("Heure de fin: " + formation.getHeure_fin().toString());

                        HBox hbox = new HBox(10, titleLabel, dateLabel, hdLabel, hfLabel, deleteButton);
                        setGraphic(hbox);
                    }
                }
            });

            // Ajouter le ListView au VBox
            vbox.getChildren().add(listView);

            // Créer la scène et afficher la fenêtre modale
            Scene scene = new Scene(vbox, 550, 300);
            favStage.setScene(scene);
            favStage.initModality(Modality.APPLICATION_MODAL);
            favStage.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyAlertStyle(alert);
        alert.showAndWait();

    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }



    @Deprecated
    public void retourdashRH(ActionEvent actionEvent) {
    }

}
