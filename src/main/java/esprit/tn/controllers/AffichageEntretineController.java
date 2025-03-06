package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AffichageEntretineController {
    @FXML
    private ListView<Entretien> lv_entretien;

    private EntretienService entretienService = new EntretienService();

    private ServiceUser su = new ServiceUser();
    esprit.tn.entities.User users = new User();


    private ObservableList<Entretien> allEntretiens = FXCollections.observableArrayList();

    @FXML
    private Button btn_ajouter;
    @FXML
    private TextField searchField;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private Button filterButton;


    @FXML
    public void initialize() throws SQLException {
        afficherEntretien();
        lv_entretien.getStylesheets().add(getClass().getResource("/controllerAffichage.css").toExternalForm());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterEntretiens(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void afficherEntretien() throws SQLException {
        List<Entretien> entretiens = entretienService.afficher();
        ObservableList<Entretien> data = FXCollections.observableArrayList(entretiens);
        allEntretiens.setAll(entretiens);
        lv_entretien.setItems(allEntretiens);

        lv_entretien.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entretien entretien, boolean empty) {
                super.updateItem(entretien, empty);
                if (empty || entretien == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a VBox for the current entretien
                    VBox entretienBox = createEntretienBox(entretien);
                    setGraphic(entretienBox);
                }
            }
        });
    }

    // Helper method to create a VBox for each Entretien item
    private VBox createEntretienBox(Entretien entretien) {
        // Load icons
        ImageView iconTitre = new ImageView(new Image(getClass().getResourceAsStream("/icons/job-seeker.png")));
        iconTitre.setFitHeight(24);
        iconTitre.setFitWidth(24);

        ImageView iconDescription = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit-info.png")));
        iconDescription.setFitHeight(24);
        iconDescription.setFitWidth(24);

        ImageView iconDate = new ImageView(new Image(getClass().getResourceAsStream("/icons/date.png")));
        iconDate.setFitHeight(24);
        iconDate.setFitWidth(24);

        ImageView iconType = new ImageView(new Image(getClass().getResourceAsStream("/icons/type.png")));
        iconType.setFitHeight(24);
        iconType.setFitWidth(24);

        ImageView iconStatut = new ImageView(new Image(getClass().getResourceAsStream("/icons/checked.png")));
        iconStatut.setFitHeight(24);
        iconStatut.setFitWidth(24);

        ImageView iconEmploye = new ImageView(new Image(getClass().getResourceAsStream("/icons/assignment.png")));
        iconEmploye.setFitHeight(24);
        iconEmploye.setFitWidth(24);

        // Create buttons
        Button btnModifier = new Button("Modifier");
        Button btnSupprimer = new Button("Supprimer");
        Button btnVoirDetail = new Button("Voir Détails");

        // Apply CSS styles to buttons
        btnModifier.getStyleClass().add("button-modifier");
        btnSupprimer.getStyleClass().add("button-supprimer");
        btnVoirDetail.getStyleClass().add("button");

        // Add actions to buttons
        btnModifier.setOnAction(event -> ouvrirModifierEntretien(entretien));
        btnSupprimer.setOnAction(event -> supprimerEntretien(entretien));
        btnVoirDetail.setOnAction(event -> voirDetailEntretien(entretien));

        // Create an HBox to align buttons to the right
        HBox buttonBox = new HBox(10, btnVoirDetail, btnModifier, btnSupprimer);
        buttonBox.getStyleClass().add("hbox-buttons");

        // Create a VBox to organize text and buttons
        VBox vbox = new VBox(5);

        // Title
        Label titreLabel = new Label("Titre: " + entretien.getTitre(), iconTitre);
        titreLabel.getStyleClass().add("titre-label");

        // Description
        Label descriptionLabel = new Label("Description: " + entretien.getDescription(), iconDescription);
        descriptionLabel.getStyleClass().add("description-label");

        // Date and time
        Label dateLabel = new Label("Date: " + entretien.getDate_entretien() + "  Heure: " + entretien.getHeure_entretien(), iconDate);
        dateLabel.getStyleClass().add("date-label");

        // Type
        Label typeLabel = new Label("Type: " + entretien.getType_entretien(), iconType);
        typeLabel.getStyleClass().add("type-label");

        // Status
        Label statutLabel = new Label("Statut: " + (entretien.isStatus() ? "Terminé" : "En cours"), iconStatut);
        statutLabel.getStyleClass().add("statut-label");

        vbox.getChildren().addAll(titreLabel, descriptionLabel, dateLabel, typeLabel, statutLabel);

        if (entretien.getEmployeId() != 0) {
            try {
                User users = su.findbyid(entretien.getEmployeId());
                Label employeLabel = new Label("Entretien affecté chez " + users.getNom() + " " + users.getPrenom(), iconEmploye);
                employeLabel.getStyleClass().add("employe-label");
                vbox.getChildren().add(employeLabel);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // Add the VBox and HBox to the cell
        return new VBox(vbox, buttonBox);
    }









    private void voirDetailEntretien(Entretien entretien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/voirDetailsEntretien.fxml"));
            Parent root = loader.load();

            VoirDetailsEntretienController controller = loader.getController();
            controller.setEntretien(entretien);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de l'Entretien");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void filterEntretiens(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            lv_entretien.setItems(allEntretiens);
        } else {
            String lowerKeyword = keyword.toLowerCase();
            List<Entretien> filteredList = entretienService.rechercher(lowerKeyword);
            lv_entretien.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    @FXML
    public void ajouterEntretien(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEntretien.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Ajouter un Entretien");


            Scene scene = new Scene(root);
            popupStage.setScene(scene);


            popupStage.initModality(Modality.APPLICATION_MODAL);


            popupStage.setWidth(420);
            popupStage.setHeight(450);

            popupStage.setOnHidden(event -> {
                try {
                    refreshDatas();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });


            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void ouvrirModifierEntretien(Entretien entretien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEntretien.fxml"));
            Parent root = loader.load();

            ModifierEntretienController controller = loader.getController();
            controller.chargerEntretien(entretien);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Entretien");

            stage.setOnHidden(event -> {
                try {
                    refreshDatas();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void supprimerEntretien(Entretien entretien) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'entretien");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet entretien ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    entretienService.supprimer(entretien.getId());
                    afficherEntretien();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Échec de la suppression");
                    errorAlert.setContentText("Une erreur est survenue lors de la suppression de l'entretien.");
                    errorAlert.show();
                }
            }
        });
    }


    private void voirFeedback(int feedbackId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/voirFeedback.fxml"));
            Parent root = loader.load();

            lv_entretien.getScene().setRoot(root);

            voirFeedbackController controller = loader.getController();
            controller.chargerFeedback(feedbackId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Voir Feedback");
            stage.setOnHidden(event -> {
                try {
                    refreshDatas();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });


            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void ajouterFeedback(int entretienId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterFeedback.fxml"));
            Parent root = loader.load();

            AjouterFeedbackController controller = loader.getController();
            controller.setEntretienId(entretienId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Feedback");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void refreshDatas() throws SQLException {

        afficherEntretien();

    }


    private void ouvrirPopupAffectation(Entretien entretien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affecterEntretien.fxml"));
            Parent root = loader.load();

            AffecterEntretien controller = loader.getController();
            controller.setEntretien(entretien);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Affecter un employé à l'entretien");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void filterByDate(ActionEvent actionEvent) {
        // Récupérer les dates sélectionnées
        LocalDate startDate = dateDebutPicker.getValue();
        LocalDate endDate = dateFinPicker.getValue();

        // Vérifier si les dates sont nulles (non sélectionnées)
        if (startDate == null || endDate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dates manquantes");
            alert.setHeaderText("Veuillez remplir la date de début et la date de fin.");
            alert.setContentText("Pour filtrer par date, vous devez sélectionner les deux dates.");
            alert.showAndWait();
            return; // Arrêter l'exécution de la méthode
        }

        // Vérifier si la date de début est après la date de fin
        if (startDate.isAfter(endDate)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur de dates");
            alert.setHeaderText("La date de début ne peut pas être après la date de fin.");
            alert.setContentText("Veuillez sélectionner une date de début antérieure ou égale à la date de fin.");
            alert.showAndWait();
            return; // Arrêter l'exécution de la méthode
        }

        // Convertir les LocalDate en Date
        Date dateDebut = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateFin = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            // Filtrer les entretiens par date
            List<Entretien> filteredEntretiens = entretienService.filterEntretienByDate(dateDebut, dateFin);
            ObservableList<Entretien> filteredData = FXCollections.observableArrayList(filteredEntretiens);
            lv_entretien.setItems(filteredData);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de filtrage");
            alert.setHeaderText("Une erreur est survenue lors du filtrage.");
            alert.setContentText("Veuillez réessayer.");
            alert.showAndWait();
        }
    }









    public void voirHistorique(ActionEvent actionEvent) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/voirHistoriqueEntretien.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("historique  un Entretien");


            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setWidth(800);
            popupStage.setHeight(600);

            popupStage.setOnHidden(event -> {
                try {
                    refreshDatas();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });


            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



















































