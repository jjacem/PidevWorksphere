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
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
            // Charger tous les projets au démarrage
            List<Projet> projets = serviceProjet.afficherProjet();
            projetListView.getItems().addAll(projets);

            // Ajouter un Listener sur le TextField pour la recherche dynamique
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    // Appeler la méthode de recherche avec le texte saisi
                    List<Projet> projetsFiltres = serviceProjet.rechercherProjet(newValue, newValue);

                    // Mettre à jour la ListView
                    projetListView.getItems().clear();
                    projetListView.getItems().addAll(projetsFiltres);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Personnalisation des cellules de la ListView
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
                private final Button detailsBtn = new Button("Détails");
                private final Button modifierBtn = new Button("Modifier");
                private final Button supprimerBtn = new Button("Supprimer");

                {
                    // Configuration initiale des éléments graphiques
                    leftContent.setPadding(new Insets(10));
                    leftContent.getChildren().addAll(nomLabel, descriptionLabel, equipeLabel, new HBox(10, dateCreationLabel, deadlineLabel, etatLabel));

                    detailsBtn.setStyle("-fx-background-color: #0086b3; -fx-text-fill: white;");
                    modifierBtn.setStyle("-fx-background-color: #ffbb33; -fx-text-fill: white;");
                    supprimerBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                    // Ajouter un événement au bouton "Supprimer"
                    supprimerBtn.setOnAction(event -> {
                        Projet projet = getItem();
                        if (projet != null) {
                            supprimerProjet(projet.getId());
                        }
                    });

                    buttonBox.getChildren().addAll(detailsBtn, modifierBtn, supprimerBtn);

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
                        // Mise à jour des labels avec les données du projet
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
            // Charger la vue AjouterProjet.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterProjet.fxml"));

            // Récupérer la scène actuelle
            Stage stage = (Stage) projetListView.getScene().getWindow();

            // Remplacer la scène actuelle par la nouvelle scène
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Projet"); // Optionnel : définir un titre pour la fenêtre
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
                // Si le champ de recherche est vide, afficher tous les projets
                projets = serviceProjet.afficherProjet();
            } else {
                // Rechercher par nom de projet ou nom d'équipe
                projets = serviceProjet.rechercherProjet(searchText, searchText);
            }

            System.out.println("Nombre de projets trouvés : " + projets.size()); // Log

            // Mettre à jour la ListView
            projetListView.getItems().clear();
            projetListView.getItems().addAll(projets);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerProjet(int id) {
        try {
            // Appeler la méthode du service pour supprimer le projet
            serviceProjet.supprimerProjet(id);

            // Rafraîchir la liste des projets
            List<Projet> projets = serviceProjet.afficherProjet();
            projetListView.getItems().clear();
            projetListView.getItems().addAll(projets);

            System.out.println("Projet supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la suppression du projet : " + e.getMessage());
        }
    }


}