package esprit.tn.controllers;

import esprit.tn.entities.Reclamation;
import esprit.tn.entities.Reponse;
import esprit.tn.entities.Role;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReclamation;
import esprit.tn.services.ServiceReponse;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherReclamationsController {

    @FXML
    private ListView<HBox> listView;

    @FXML
    private Button btnAjouter;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceReponse serviceReponse = new ServiceReponse();
    private User logged;

    @FXML
    public void initialize() {
        try {
            logged = SessionManager.extractuserfromsession();
            setupStatusFilter();
            afficherReclamations();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la récupération des réclamations.");
            e.printStackTrace();
        }
    }

    private void setupStatusFilter() {
        statusChoiceBox.setItems(FXCollections.observableArrayList("Tous", "Résolu", "En attente", "Refusé"));
        statusChoiceBox.setValue("Tous");
        statusChoiceBox.setOnAction(event -> {
            try {
                afficherReclamations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                afficherReclamations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void afficherReclamations() throws SQLException {
        String selectedStatus = statusChoiceBox.getValue();
        String searchText = searchField.getText().trim().toLowerCase();

        List<Reclamation> reclamations = logged.getRole() == Role.EMPLOYE
                ? serviceReclamation.getReclamationsByUser2(logged.getIdUser())
                : serviceReclamation.getReclamationsByUser(logged.getIdUser());

        if (!selectedStatus.equals("Tous")) {
            reclamations = serviceReclamation.filterbystats(selectedStatus);
        }

        if (!searchText.isEmpty()) {
            reclamations = serviceReclamation.filterbytitle(searchText);
        }

        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Reclamation r : reclamations) {
            HBox hbox = new HBox(10);
            hbox.setUserData(r.getId_reclamation());

            Label label = new Label("ID: " + r.getId_reclamation() +
                    " | Status: " + r.getStatus() +
                    " | Message: " + r.getDescription() +
                    " | Candidat: " + r.getId_user() +
                    " | Employé: " + r.getId_user2());

            if (logged.getRole() == Role.CANDIDAT) {
                Button btnModifier = new Button("Modifier");
                btnModifier.setOnAction(e -> modifierReclamation(r.getId_reclamation()));

                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setOnAction(e -> supprimerReclamation(r.getId_reclamation()));

                hbox.getChildren().addAll(btnModifier, btnSupprimer);
            }

            if (logged.getRole() == Role.EMPLOYE) {
                Reponse reponse = serviceReponse.checkForRepInRec(r.getId_reclamation());
                Button btnReponse = new Button(reponse == null ? "Ajouter Réponse" : "Modifier Réponse");
                btnReponse.setOnAction(e -> gererReponse(r.getId_reclamation(), reponse != null));
                hbox.getChildren().add(btnReponse);
            }

            hbox.getChildren().add(label);
            items.add(hbox);
        }
        listView.setItems(items);
    }

    private void modifierReclamation(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReclamation.fxml"));
            Parent root = loader.load();
            ModifierReclamationController controller = loader.getController();
            controller.setReclamationId(id);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            afficherReclamations();
        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Impossible de modifier la réclamation.");
        }
    }

    private void supprimerReclamation(int id) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous supprimer cette réclamation ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                serviceReclamation.supprimer(id);
                afficherReclamations();
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de supprimer la réclamation.");
        }
    }

    private void gererReponse(int idReclamation, boolean hasReponse) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(hasReponse ? "/ModifierReponse.fxml" : "/AjouterReponse.fxml"));
            Parent root = loader.load();

            if (hasReponse) {
                ModiferReponseController controller = loader.getController();
                controller.setReclamationId(idReclamation);
            } else {
                AjouterReponseController controller = loader.getController();
                controller.setIds(this.logged.getIdUser(), idReclamation);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            afficherReclamations();
        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Impossible de gérer la réponse.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void ajouterReclamation(ActionEvent actionEvent) {
    }
}
