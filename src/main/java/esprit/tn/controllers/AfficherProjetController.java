package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AfficherProjetController {
    @FXML
    private ListView<Projet> projetListView;

    @FXML
    private VBox projetsContainer;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> etatFilter;

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


            // Initialiser le filtre par état
            etatFilter.getItems().addAll("Tous", "EN_COURS", "Terminé","Annulé");
            etatFilter.setValue("Tous"); // Valeur par défaut

            // Initialiser le filtre par équipe
            List<Equipe> equipes = serviceProjet.getEquipes();

            // Ajouter des Listener sur les ComboBox pour la recherche dynamique
            etatFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
                appliquerFiltre();
            });


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
                //private final Label descriptionLabel = new Label();
                private final Label dateCreationLabel = new Label();
                private final Label deadlineLabel = new Label();
                private final Label etatLabel = new Label();
                private final Label equipeLabel = new Label();

                private final Button modifierBtn = new Button("Modifier");
                private final Button supprimerBtn = new Button("Supprimer");
                private final Button detailsBtn = new Button("Détails");

                {

                    leftContent.getChildren().addAll(nomLabel, equipeLabel, dateCreationLabel, deadlineLabel, etatLabel);


                    modifierBtn.setStyle("-fx-background-color: #ffbb33; -fx-text-fill: white;");
                    supprimerBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                    detailsBtn.setStyle("-fx-background-color: #44b1ff; -fx-text-fill: white;");

                    // Ajout te3 event lel btn supp w modif"
                    supprimerBtn.setOnAction(event -> {
                        Projet projet = getItem();
                        if (projet != null) {
                            supprimerProjet(projet.getId());
                        }
                    });
                    detailsBtn.setOnAction(event -> {
                        Projet projet = getItem();
                        if (projet != null) {
                            afficherDetailsProjet(projet);
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

                                // Créer une nouvelle Stage (fenêtre modale)
                                Stage stage = new Stage();
                                stage.initModality(Modality.APPLICATION_MODAL); // Rend la fenêtre modale
                                stage.setTitle("Modifier un Projet");
                                stage.setScene(new Scene(root));
                                stage.showAndWait(); // Affiche la fenêtre et attend sa fermeture

                                // Rafraîchir la liste des projets après la modification
                                try {
                                    List<Projet> projets = serviceProjet.afficherProjet();
                                    projetListView.getItems().clear();
                                    projetListView.getItems().addAll(projets);
                                } catch (SQLException e) {
                                    // Gérer l'exception SQL
                                    e.printStackTrace();
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Erreur de base de données");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Une erreur s'est produite lors du chargement des projets. Veuillez réessayer.");
                                    alert.showAndWait();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur de chargement");
                                alert.setHeaderText(null);
                                alert.setContentText("Une erreur s'est produite lors du chargement de la page de modification.");
                                alert.showAndWait();
                            }
                        }
                    });

                    buttonBox.getChildren().addAll(modifierBtn, supprimerBtn, detailsBtn);

                    AnchorPane.setLeftAnchor(leftContent, 10.0);
                    AnchorPane.setRightAnchor(buttonBox, 10.0);
                    AnchorPane.setTopAnchor(leftContent, 10.0);
                    AnchorPane.setTopAnchor(buttonBox, 10.0);
                    AnchorPane.setLeftAnchor(leftContent, 10.0);
                    AnchorPane.setRightAnchor(buttonBox, 10.0);


                    cellContainer.getChildren().addAll(leftContent, buttonBox);

                }

                @Override
                protected void updateItem(Projet projet, boolean empty) {
                    super.updateItem(projet, empty);

                    if (empty || projet == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Mettre à jour les labels
                        nomLabel.setText(projet.getNom());
                        nomLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #000000;");

                        /*descriptionLabel.setText("Description : " + projet.getDescription());
                        descriptionLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #555;");*/
                        equipeLabel.setText("Équipe : " + projet.getEquipe().getNomEquipe());

                        dateCreationLabel.setText("Créé le : " + projet.getDatecréation());
                        deadlineLabel.setText("Deadline : " + projet.getDeadline());
                        etatLabel.setText("État : " + projet.getEtat().name());

                        // Ajouter l'image du projet
                        ImageView imageView = new ImageView();
                        if (projet.getImageProjet() != null && !projet.getImageProjet().trim().isEmpty()) {
                            String correctPath = "C:/xampp/htdocs/img/" + new File(projet.getImageProjet()).getName();
                            System.out.println("Chemin de l'image : " + correctPath);

                            // Vérifier si le fichier existe
                            File imageFile = new File(correctPath);
                            if (imageFile.exists() && imageFile.isFile()) {
                                // Charger l'image depuis le chemin absolu
                                imageView.setImage(new Image(imageFile.toURI().toString()));
                            } else {
                                // Utiliser une image par défaut si le fichier n'existe pas
                                System.out.println("Fichier image introuvable ou chemin invalide : " + imageFile.getAbsolutePath());
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                            }
                        } else {
                            // Utiliser une image par défaut si aucune image n'est définie
                            System.out.println("Aucun chemin d'image fourni pour le projet : " + projet.getNom());
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                        }

                        // Configuration de l'ImageView
                        imageView.setFitHeight(120); // Taille de l'image
                        imageView.setFitWidth(120);
                        imageView.setPreserveRatio(true);

                        // Ajouter l'image à gauche du contenu
                        HBox contentBox = new HBox(10); // Utiliser HBox pour aligner l'image et le contenu
                        contentBox.getChildren().addAll(imageView, leftContent); // Image à gauche, contenu à droite

                        // Ajouter le contenu et les boutons dans le AnchorPane
                        cellContainer.getChildren().clear();
                        cellContainer.getChildren().addAll(contentBox, buttonBox);

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



    @FXML
    private void afficherDetailsProjet(Projet projet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherdetailsProjet.fxml"));
            Parent root = loader.load();

            // Passer le projet au contrôleur des détails
            AfficherdetailsProjetController detailsController = loader.getController();
            detailsController.setProjet(projet);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails du Projet");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }


    @FXML
    private void appliquerFiltre() {
        try {
            // Récupérer les valeurs des filtres
            String nomProjet = searchField.getText().trim();
            String etat = etatFilter.getValue();

            // Appliquer le filtre
            List<Projet> projetsFiltres = serviceProjet.rechercherProjetParEtat(nomProjet, etat);

            // Mettre à jour la liste des projets
            projetListView.getItems().clear();
            projetListView.getItems().addAll(projetsFiltres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}