package esprit.tn.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import esprit.tn.entities.Equipe;
import esprit.tn.services.ServiceEquipe;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherEquipeController {

    @FXML
    private VBox equipesContainer;

    @FXML
    private TextField searchField;


    private ServiceEquipe serviceEquipe;


    public AfficherEquipeController() {
        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        // Charger la liste des équipes
        try {
            List<Equipe> equipes = serviceEquipe.afficherEquipe();
            afficherEquipes(equipes);

            Platform.runLater(() -> {
                Stage stage = (Stage) equipesContainer.getScene().getWindow();
                stage.setMaximized(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherEquipes(List<Equipe> equipes) {
        equipesContainer.getChildren().clear(); // Vider le conteneur

        if (equipes.isEmpty()) {
            Label messageLabel = new Label("Aucune équipe trouvée");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666; -fx-font-style: italic;");
            equipesContainer.getChildren().add(messageLabel);
        } else {
            for (Equipe equipe : equipes) {
                HBox card = new HBox(20);
                card.getStyleClass().add("card");
                card.setAlignment(Pos.CENTER_LEFT);

                // Ajouter l'image de l'équipe
                ImageView imageView = new ImageView();
                if (equipe.getImageEquipe() != null && !equipe.getImageEquipe().trim().isEmpty()) {
                    String correctPath = "C:/xampp/htdocs/img/" + new File(equipe.getImageEquipe()).getName();
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

                // Nom de l'équipe
                Label nomEquipeLabel = new Label(equipe.getNomEquipe());
                nomEquipeLabel.getStyleClass().add("card-label");

                // Nombre de projets
                Label nbrProjetLabel = new Label("Projets : " + equipe.getNbrProjet());
                nbrProjetLabel.getStyleClass().add("card-label");
                nbrProjetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

                // Boutons
                Button detailsButton = new Button("Détails");
                detailsButton.getStyleClass().addAll("card-button", "details-button");
                detailsButton.setOnAction(event -> afficherDetailsEquipe(equipe));

                Button modifierButton = new Button("Modifier");
                modifierButton.getStyleClass().addAll("card-button", "modifier-button");
                modifierButton.setOnAction(event -> modifierEquipe(equipe));

                Button supprimerButton = new Button("Supprimer");
                supprimerButton.getStyleClass().addAll("card-button", "supprimer-button");
                supprimerButton.setOnAction(event -> supprimerEquipe(equipe));

                // Utilisation d'un Region pour pousser les boutons à droite
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Ajouter les éléments à la carte
                card.getChildren().addAll(imageView, nomEquipeLabel,nbrProjetLabel, spacer, detailsButton, modifierButton, supprimerButton);
                equipesContainer.getChildren().add(card);
            }
        }
    }


    @FXML
    public void redirectToAjouterEquipe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEquipe.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Ajouter une équipe");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifierEquipe(Equipe equipe) {
        try {
            // Charger le fichier FXML pour la fenêtre de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEquipe.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre de modification
            ModifierEquipeController controller = loader.getController();
            controller.setEquipeAModifier(equipe);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Modifier une équipe");
            stage.initModality(Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
            stage.setScene(new Scene(root));

            // Afficher la fenêtre et attendre sa fermeture
            stage.showAndWait();

            // Rafraîchir la liste des équipes après la modification
            try {
                List<Equipe> equipes = serviceEquipe.afficherEquipe();
                afficherEquipes(equipes);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerEquipe(Equipe equipe) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette équipe ?");
        alert.setContentText("Cette action est irréversible.");
        applyAlertStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEquipe.supprimerEquipe(equipe.getId());
                // refresh te3 liste
                List<Equipe> equipes = serviceEquipe.afficherEquipe();
                afficherEquipes(equipes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*private void afficherDetailsEquipe(Equipe equipe) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailsEquipe.fxml"));
            Parent root = loader.load();
            AfficherDetailsEquipeController controller = loader.getController();
            controller.setEquipe(equipe);

            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.setTitle("Détails de l'équipe");
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void afficherDetailsEquipe(Equipe equipe) {
        try {
            // Charger le fichier FXML pour la fenêtre de détails
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDetailsEquipe.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre de détails
            AfficherDetailsEquipeController controller = loader.getController();
            controller.setEquipe(equipe);

            // Créer une nouvelle fenêtre (Stage)
            Stage stage = new Stage();
            stage.setTitle("Détails de l'équipe");
            stage.initModality(Modality.APPLICATION_MODAL); // Rendre la fenêtre modale
            stage.setScene(new Scene(root));

            // Afficher la fenêtre et attendre sa fermeture
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void rechercherEquipe() {
        String nomEquipe = searchField.getText().trim();
        try {
            List<Equipe> equipes = serviceEquipe.rechercherEquipe(nomEquipe);
            afficherEquipes(equipes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void supprimerToutesEquipes() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer toutes les équipes ?");
        alert.setContentText("Cette action est irréversible.");
        applyAlertStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceEquipe.supprimerToutesEquipes();
                List<Equipe> equipes = serviceEquipe.afficherEquipe();
                afficherEquipes(equipes);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void versProjet() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProjet.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) equipesContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
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
    public void genererRapportStatistique() {
        try {
            // Récupérer la liste des équipes
            List<Equipe> equipes = serviceEquipe.afficherEquipe();

            if (equipes.isEmpty()) {
                showAlert("Aucune équipe trouvée", "Il n'y a aucune équipe à analyser.", Alert.AlertType.INFORMATION);
                return;
            }

            // Calcul des statistiques
            int nombreTotalEquipes = equipes.size();
            int totalMembres = equipes.stream()
                    .mapToInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0)
                    .sum();
            double moyenneMembresParEquipe = (double) totalMembres / nombreTotalEquipes;

            // Trouver l'équipe avec le plus de membres
            Equipe equipeAvecPlusDeMembres = equipes.stream()
                    .max(Comparator.comparingInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0))
                    .orElse(null);

            // Trouver l'équipe avec le moins de membres
            Equipe equipeAvecMoinsDeMembres = equipes.stream()
                    .min(Comparator.comparingInt(equipe -> equipe.getEmployes() != null ? equipe.getEmployes().size() : 0))
                    .orElse(null);

            // Calculer le nombre de projets par équipe
            /*Map<String, Long> projetsParEquipe = equipes.stream()
                    .collect(Collectors.groupingBy(
                            Equipe::getNomEquipe,
                            Collectors.summingLong(equipe -> equipe.getProjets() != null ? equipe.getProjets().size() : 0)
                    ));*/

            // Construire le rapport
            StringBuilder rapport = new StringBuilder();
            rapport.append("Rapport Statistique des Équipes:\n\n");
            rapport.append("Nombre total d'équipes: ").append(nombreTotalEquipes).append("\n");
            rapport.append("Nombre total de membres: ").append(totalMembres).append("\n");
            rapport.append("Nombre moyen de membres par équipe: ").append(String.format("%.2f", moyenneMembresParEquipe)).append("\n");
            rapport.append("Équipe avec le plus de membres: ").append(equipeAvecPlusDeMembres != null ? equipeAvecPlusDeMembres.getNomEquipe() : "N/A").append("\n");
            rapport.append("Équipe avec le moins de membres: ").append(equipeAvecMoinsDeMembres != null ? equipeAvecMoinsDeMembres.getNomEquipe() : "N/A").append("\n");

            rapport.append("\nNombre de projets par équipe:\n");
            /*projetsParEquipe.forEach((nomEquipe, nombreProjets) ->
                    rapport.append(nomEquipe).append(": ").append(nombreProjets).append(" projets\n"));*/

            // Afficher le rapport dans une boîte de dialogue
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rapport Statistique");
            alert.setHeaderText("Statistiques des Équipes");
            alert.setContentText(rapport.toString());
            applyAlertStyle(alert);
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur s'est produite lors de la génération du rapport.", Alert.AlertType.ERROR);
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyAlertStyle(alert);
        alert.showAndWait();
    }


}