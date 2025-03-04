package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AffecterEntretien {
    @FXML
    private Button btn_valider;
    @FXML
    private ComboBox cb_employes;

//    private EntretienService entretienService = new EntretienService();

    private ServiceUser su = new ServiceUser();

    private Entretien entretien;



//    @FXML
//    public void initialize() {
//        chargerEmployes();
//        btn_valider.setOnAction(event -> affecterEntretien());
//    }


//    private void chargerEmployes() {
//        try {
//            List<User> employes = su.afficher();
//            cb_employes.setItems(FXCollections.observableArrayList(employes));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    private void chargerEmployes() {
        try {
            List<esprit.tn.entities.User> employes = su.getUsersByRoleEmployee();
            cb_employes.setItems(FXCollections.observableArrayList(employes));

            cb_employes.setCellFactory(param -> new javafx.scene.control.ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });

            cb_employes.setButtonCell(new javafx.scene.control.ListCell<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNom());
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



//    private void affecterEntretien() {
//        esprit.tn.entities.User employeSelectionne = (esprit.tn.entities.User) cb_employes.getValue();
//        if (employeSelectionne != null) {
//            entretienService.affecterEntretien(employeSelectionne.getIdUser(), entretien.getId());
//            afficherConfirmation();
//            ouvrirAffichageEntretien();
//        } else {
//            afficherErreur("Veuillez sélectionner un employé.");
//        }
//    }


    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }

    private void afficherConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Entretien affecté");
        alert.setContentText("L'entretien a été affecté avec succès.");
        alert.showAndWait();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Erreur d'affectation");
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void fermerPopup() {
        Stage stage = (Stage) cb_employes.getScene().getWindow();
        stage.close();
    }

    private void ouvrirAffichageEntretien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();

            AffichageEntretineController controller = loader.getController();
            controller.initialize();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Entretiens");
            stage.show();

        } catch (IOException | SQLException e) {
        }
    }




}
