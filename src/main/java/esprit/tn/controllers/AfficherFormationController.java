package esprit.tn.controllers;

import esprit.tn.entities.Formation;
import esprit.tn.services.ServiceFormation;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherFormationController {

    @FXML
    private ListView<Formation> listformationid;

    @FXML
    private Button btnajouterID;

    private final ServiceFormation formationService = new ServiceFormation();
    @FXML
    private HBox Vrechcerche;
    @FXML
    private Button Btnrecherche;
    @FXML
    private TextField Trecherche;

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


                            Button modifierButton = new Button("Modifier");
                            modifierButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
                            modifierButton.setOnAction(event -> {
                                // Charger la page de modification
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFormation.fxml"));
                                    Parent root = loader.load();

                                    ModifierFormationController controller = loader.getController();
                                    controller.setFormation(formation);  // Passer l'objet formation à la page de modification

                                    Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                    stage.getScene().setRoot(root);
                                } catch (IOException e) {
                                    System.out.println("Erreur de chargement de la page de modification : " + e.getMessage());
                                }
                            });

                            Button supprimerButton = new Button("Supprimer");
                            supprimerButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
                            supprimerButton.setOnAction(event -> deleteFormation(formation));


                            // Conteneur pour aligner les boutons à droite
                            HBox buttonContainer = new HBox(10, modifierButton, supprimerButton);
                            buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                            buttonContainer.setPadding(new Insets(30, 10, 10, 850)); // Marges autour des boutons

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




    private void modifyFormation(Formation formation) {
        System.out.println("Modification de la formation: " + formation.getTitre());
        // Implémente la modification
    }

    private void deleteFormation(Formation formation) {
        ServiceFormation serviceFormation = new ServiceFormation();

        // Création de l'alerte de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Vous êtes sûr de vouloir supprimer cette formation ?");

        // Attente de la réponse de l'utilisateur
        Optional<ButtonType> result = alert.showAndWait();

        // Vérifier si l'utilisateur a cliqué sur OK
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceFormation.supprimeFormation(formation);
                System.out.println("Suppression réussie pour la formation : " + formation.getTitre());

                // Mise à jour de la ListView
                listformationid.getItems().remove(formation);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression : " + e.getMessage());
            }
        } else {
            System.out.println("Suppression annulée par l'utilisateur.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void Onajouterformation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFormation.fxml"));
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
        listformationid.setItems(observableList);  // formationListView est votre ListView ou TableView
    }


}
