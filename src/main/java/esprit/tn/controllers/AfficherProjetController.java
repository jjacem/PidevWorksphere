package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.services.ServiceProjet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import esprit.tn.entities.EtatProjet;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AfficherProjetController {
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
            afficherProjets(projets);

            // Initialiser le filtre par état
            etatFilter.getItems().addAll("TOUS", "EN_COURS", "Terminé", "Annulé");
            etatFilter.setValue("TOUS"); // Valeur par défaut

            // Ajouter des Listener sur les ComboBox pour la recherche dynamique
            etatFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
                appliquerFiltre();
            });

            // Ajouter un Listener sur le TextField pour la recherche dynamique
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    // Appel de la recherche + mise à jour de la liste
                    List<Projet> projetsFiltres = serviceProjet.rechercherProjet(newValue);
                    afficherProjets(projetsFiltres);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void afficherProjets(List<Projet> projets) {
        projetsContainer.getChildren().clear(); // Vider le conteneur

        if (projets.isEmpty()) {
            Label messageLabel = new Label("Aucun projet trouvé");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-font-style: italic;");
            projetsContainer.getChildren().add(messageLabel);
        } else {
            for (Projet projet : projets) {
                // Créer une carte pour chaque projet
                HBox card = new HBox(20);
                card.getStyleClass().add("card");
                card.setAlignment(Pos.CENTER_LEFT);

                // Ajouter l'image du projet
                ImageView imageView = new ImageView();
                if (projet.getImageProjet() != null && !projet.getImageProjet().trim().isEmpty()) {
                    String correctPath = "C:/xampp/htdocs/img/" + new File(projet.getImageProjet()).getName();
                    File imageFile = new File(correctPath);
                    if (imageFile.exists() && imageFile.isFile()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                    }
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                }
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.getStyleClass().add("card-image");

                // Informations du projet
                VBox infoBox = new VBox(5);
                infoBox.setAlignment(Pos.CENTER_LEFT);

                Label nomLabel = new Label(projet.getNom());
                nomLabel.getStyleClass().add("card-label");

                Label equipeLabel = new Label("Équipe : " + projet.getEquipe().getNomEquipe());
                equipeLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

                Label dateCreationLabel = new Label("Créé le : " + projet.getDatecréation());
                dateCreationLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

                Label deadlineLabel = new Label("Deadline : " + projet.getDeadline());
                deadlineLabel.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

                // Créer un Label pour l'état avec un style personnalisé
                Label etatLabel = new Label(projet.getEtat().name());
                etatLabel.getStyleClass().add("chip");

                // Appliquer un style en fonction de l'état
                switch (projet.getEtat()) {
                    case Terminé:
                        etatLabel.getStyleClass().add("chip-termine");
                        break;
                    case Annulé:
                        etatLabel.getStyleClass().add("chip-annule");
                        break;
                    case EnCours:
                        etatLabel.getStyleClass().add("chip-en-cours");
                        break;
                    default:
                        etatLabel.getStyleClass().add("chip-default");
                }

                infoBox.getChildren().addAll(nomLabel, equipeLabel, dateCreationLabel, deadlineLabel, etatLabel);

                // Boutons pour les actions
                Button detailsButton = new Button("Détails");
                detailsButton.getStyleClass().addAll("card-button", "details-button");
                detailsButton.setOnAction(event -> afficherDetailsProjet(projet));

                Button modifierButton = new Button("Modifier");
                modifierButton.getStyleClass().addAll("card-button", "modifier-button");
                modifierButton.setOnAction(event -> modifierProjet(projet));

                Button supprimerButton = new Button("Supprimer");
                supprimerButton.getStyleClass().addAll("card-button", "supprimer-button");
                supprimerButton.setOnAction(event -> supprimerProjet(projet.getId()));

                // Utiliser un Region pour pousser les boutons à droite
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Ajouter les éléments à la carte
                card.getChildren().addAll(imageView, infoBox, spacer, detailsButton, modifierButton, supprimerButton);
                projetsContainer.getChildren().add(card);
            }
        }
    }
    @FXML
    private void AjouterBTN() {
        try {
            // Charger le fichier FXML pour la fenêtre d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProjet.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Projet");
            stage.initModality(Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
            stage.setScene(new Scene(root));

            // Afficher la fenêtre et attendre sa fermeture
            stage.showAndWait();

            // Rafraîchir la liste des projets après l'ajout
            try {
                List<Projet> projets = serviceProjet.afficherProjet();
                afficherProjets(projets); // Rafraîchir la liste des projets
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifierProjet(Projet projet) {
        try {
            // Charger le fichier FXML pour la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProjet.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre de modification
            ModifierProjetController controller = loader.getController();
            controller.setProjetAModifier(projet); // Passer le projet à modifier

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Modifier un projet");
            stage.initModality(Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
            stage.setScene(new Scene(root));

            // Afficher la fenêtre et attendre sa fermeture
            stage.showAndWait();

            // Rafraîchir la liste des projets après la modification
            try {
                List<Projet> projets = serviceProjet.afficherProjet();
                afficherProjets(projets); // Rafraîchir la liste des projets
            } catch (SQLException e) {
                e.printStackTrace();

            }

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
            afficherProjets(projets);
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
                afficherProjets(projets);
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

                // Rafraîchir la liste des projets
                List<Projet> projets = serviceProjet.afficherProjet();
                afficherProjets(projets);
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
            afficherProjets(projetsFiltres);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}