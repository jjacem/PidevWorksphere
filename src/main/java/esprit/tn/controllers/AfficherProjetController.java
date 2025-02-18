package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AfficherProjetController {
    @FXML
    private ListView<Projet> projetListView;

    @FXML
    private TextField searchField;

    private ServiceProjet serviceProjet;

    public AfficherProjetController() {
        serviceProjet = new ServiceProjet();
    }

    @FXML
    public void initialize() {
        try {
            // Charger tous les projets
            List<Projet> projets = serviceProjet.afficherProjet();
            projetListView.getItems().addAll(projets);

            // Ajouter un Listener sur le TextField pour la recherche dynamique
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    //appel te3 recherche + maj liste
                    List<Projet> projetsFiltres = serviceProjet.rechercherProjet(newValue);

                    projetListView.getItems().clear();
                    projetListView.getItems().addAll(projetsFiltres);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Personnalisation te3 liste
            projetListView.setCellFactory(param -> new ListCell<Projet>() {
                private final AnchorPane cellContainer = new AnchorPane();
                private final VBox leftContent = new VBox(10);
                private final HBox buttonBox = new HBox(10);
                private final Label nomLabel = new Label();
                private final Label descriptionLabel = new Label();
                private final Label dateCreationLabel = new Label();
                private final Label deadlineLabel = new Label();
                private final Label etatLabel = new Label();
                private final Label equipeLabel = new Label();

                private final Button modifierBtn = new Button("Modifier");
                private final Button supprimerBtn = new Button("Supprimer");

       {

           leftContent.getChildren().addAll(nomLabel, descriptionLabel, equipeLabel, dateCreationLabel, deadlineLabel, etatLabel);


            modifierBtn.setStyle("-fx-background-color: #ffbb33; -fx-text-fill: white;");
             supprimerBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                    // Ajout te3 event lel btn supp w modif"
              supprimerBtn.setOnAction(event -> {
                        Projet projet = getItem();
                        if (projet != null) {
                            supprimerProjet(projet.getId());
                        }
                    });
                    modifierBtn.setOnAction(event -> {
                        Projet projet = getItem();
                        if (projet != null) {
                            try {

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
                                Parent root = loader.load();

                                ModifierProjetController modifierProjetController = loader.getController();
                                modifierProjetController.setProjetAModifier(projet);


                                Stage stage = (Stage) projetListView.getScene().getWindow();


                                stage.setScene(new Scene(root));
                                stage.setTitle("Modifier un Projet");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    buttonBox.getChildren().addAll(modifierBtn, supprimerBtn);

                    AnchorPane.setLeftAnchor(leftContent, 10.0);
                    AnchorPane.setRightAnchor(buttonBox, 10.0);
                    AnchorPane.setTopAnchor(leftContent, 10.0);
                    AnchorPane.setTopAnchor(buttonBox, 10.0);

                    cellContainer.getChildren().addAll(leftContent, buttonBox);
                    cellContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;"
                            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);");
                }

                @Override
                protected void updateItem(Projet projet, boolean empty) {
                    super.updateItem(projet, empty);

                    if (empty || projet == null) {
                        setText(null);
                        setGraphic(null);
                    } else {

                        nomLabel.setText(projet.getNom());
                        nomLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #0086b3;");

                        descriptionLabel.setText("Description : " + projet.getDescription());
                        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #555;");
                        equipeLabel.setText("Equipe : " + projet.getEquipe().getNomEquipe());

                        dateCreationLabel.setText("Créé le : " + projet.getDatecréation());
                        deadlineLabel.setText("Deadline : " + projet.getDeadline());
                        etatLabel.setText("État : " + projet.getEtat().name());

                        setGraphic(cellContainer);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void AjouterBTN() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/AjouterProjet.fxml"));
            Stage stage = (Stage) projetListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Projet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void rechercherProjet() {
        String searchText = searchField.getText().trim();
        System.out.println("Recherche en cours avec le texte : " + searchText); // Log

        try {
            List<Projet> projets;
            if (searchText.isEmpty()) {
                projets = serviceProjet.afficherProjet();
            } else {
                projets = serviceProjet.rechercherProjet(searchText);
            }
            projetListView.getItems().clear();
            projetListView.getItems().addAll(projets);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void supprimerProjet(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce projet ?");
        alert.setContentText("Cette action est irréversible.");

        applyAlertStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceProjet.supprimerProjet(id);

                // Rafraîchir la liste des projets
                List<Projet> projets = serviceProjet.afficherProjet();
                projetListView.getItems().clear();
                projetListView.getItems().addAll(projets);
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Projet supprimé avec succès !");
                applyAlertStyle(successAlert);
                successAlert.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void supprimerTousProjet() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer tous les projets ?");
        alert.setContentText("Cette action est irréversible.");

        applyAlertStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {

                serviceProjet.supprimerTousProjet();

                List<Projet> projets = serviceProjet.afficherProjet();
                projetListView.getItems().clear();
                projetListView.getItems().addAll(projets);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Tous les projets ont été supprimés avec succès !");
                applyAlertStyle(successAlert);
                successAlert.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
    }




    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}