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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherReclamationsController {

    @FXML
    private ListView<HBox> listView; // Each row is an HBox that includes info and buttons

    @FXML
    private Button btnAjouter;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceReponse serviceReponse = new ServiceReponse();
    private User logged;

    @FXML
    public void initialize() {
        try {
            logged = SessionManager.extractuserfromsession();
            afficherReclamations();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la récupération des réclamations.");
            e.printStackTrace();
        }
    }

    @FXML
    public void afficherReclamations() throws SQLException {
        List<Reclamation> reclamations = logged.getRole() == Role.EMPLOYE
                ? serviceReclamation.getReclamationsByUser2(logged.getIdUser())
                : serviceReclamation.getReclamationsByUser(logged.getIdUser());

        ObservableList<HBox> items = FXCollections.observableArrayList();
        for (Reclamation r : reclamations) {
            HBox hbox = new HBox(10);
            hbox.setUserData(r.getId_reclamation());

            Label label = new Label("ID: " + r.getId_reclamation() +
                    " | Status: " + r.getStatus() +
                    " | Message: " + r.getDescription() +
                    " | Candidat: " + r.getId_user() +
                    " | Employé: " + r.getId_user2());

            Button btnModifier = new Button();
            ImageView editIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit.png")));
            editIcon.setFitWidth(20);
            editIcon.setFitHeight(20);
            btnModifier.setGraphic(editIcon);
            btnModifier.setOnAction(e -> modifierReclamation(r.getId_reclamation()));

            Button btnSupprimer = new Button();
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/delete.png")));
            deleteIcon.setFitWidth(20);
            deleteIcon.setFitHeight(20);
            btnSupprimer.setGraphic(deleteIcon);
            btnSupprimer.setOnAction(e -> supprimerReclamation(r.getId_reclamation()));

            hbox.getChildren().addAll(label, btnModifier, btnSupprimer);

            if (logged.getRole() == Role.CANDIDAT) {
                Button btnVoirReponse = new Button("Voir Réponse");
                btnVoirReponse.setOnAction(e -> voirReponse(r.getId_reclamation()));
                hbox.getChildren().add(btnVoirReponse);
            }

            if (logged.getRole() == Role.EMPLOYE) {
                Reponse reponse = serviceReponse.checkForRepInRec(r.getId_reclamation());
                Button btnReponse = new Button(reponse == null ? "Ajouter Réponse" : "Modifier Réponse");
                btnReponse.setOnAction(e -> gererReponse(r.getId_reclamation(), reponse != null));
                hbox.getChildren().add(btnReponse);
            }

            items.add(hbox);
        }
        listView.setItems(items);
    }

    @FXML
    public void ajouterReclamation() {
        openWindow("/AjouterReclamation.fxml", "Ajouter une Réclamation");
    }

    // Bottom button handlers that operate on the selected row

    @FXML
    public void modifierReclamation(ActionEvent actionEvent) {
        HBox selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une réclamation à modifier.");
            return;
        }
        int idReclamation = (int) selected.getUserData();
        modifierReclamation(idReclamation);
    }

    @FXML
    public void supprimerReclamation(ActionEvent actionEvent) {
        HBox selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une réclamation à supprimer.");
            return;
        }
        int idReclamation = (int) selected.getUserData();
        supprimerReclamation(idReclamation);
    }

    @FXML
    public void gererReponse(ActionEvent actionEvent) {
        HBox selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner une réclamation.");
            return;
        }
        int idReclamation = (int) selected.getUserData();
        if (logged.getRole() == Role.CANDIDAT) {
            voirReponse(idReclamation);
        } else if (logged.getRole() == Role.EMPLOYE) {
            Reponse reponse = serviceReponse.checkForRepInRec(idReclamation);
            gererReponse(idReclamation, reponse != null);
        }
    }

    // Private helper methods

    private void modifierReclamation(int idReclamation) {
        openWindow("/ModifierReclamation.fxml", "Modifier la Réclamation", idReclamation);
    }

    private void supprimerReclamation(int idReclamation) {
        try {
            serviceReclamation.supprimer(idReclamation);
            afficherReclamations();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de supprimer la réclamation.");
            e.printStackTrace();
        }
    }

    private void voirReponse(int idReclamation) {
        openWindow("/ShowReclamation.fxml", "Voir Réponse", idReclamation);
    }

    private void gererReponse(int idReclamation, boolean reponseExists) {
        String fxmlPath = reponseExists ? "/ModifierReponse.fxml" : "/AjouterReponse.fxml";
        openWindow(fxmlPath, reponseExists ? "Modifier la Réponse" : "Ajouter une Réponse", idReclamation);
    }

    /**
     * Opens a new window.
     * If the window is for adding a response, it sets the correct IDs via the AjouterReponseController.
     *
     * @param fxmlPath      the FXML file path
     * @param title         the window title
     * @param reclamationId the reclamation ID to pass (if any; pass -1 if not applicable)
     */
    private void openWindow(String fxmlPath, String title, int reclamationId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (reclamationId != -1) {
                if (fxmlPath.contains("ModifierReclamation")) {
                    ModifierReclamationController controller = loader.getController();
                    controller.setReclamationId(reclamationId);
                } else if (fxmlPath.contains("AjouterReponse")) {
                    AjouterReponseController controller = loader.getController();
                    controller.setIds(logged.getIdUser(), reclamationId);
                } else if (fxmlPath.contains("ModifierReponse")) {
                    ModiferReponseController controller = loader.getController();
                    controller.setReclamationId(reclamationId);
                }
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            afficherReclamations();
        } catch (IOException | SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre.");
            e.printStackTrace();
        }
    }

    private void openWindow(String fxmlPath, String title) {
        openWindow(fxmlPath, title, -1);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
