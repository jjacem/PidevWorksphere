/*
package esprit.tn.controllers;

import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceSponsor;  // Assurez-vous d'avoir une classe pour le service des sponsors
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AfficherSponsorController {

    @FXML
    private ListView<Sponsor> lv_sponsor;

    @FXML
    void initialize() {
        ServiceSponsor serviceSponsor = new ServiceSponsor();
        try {
            ObservableList<Sponsor> observableList = FXCollections.observableList(serviceSponsor.afficher());
            lv_sponsor.setItems(observableList);

            lv_sponsor.setCellFactory(param -> new ListCell<Sponsor>() {
                @Override
                protected void updateItem(Sponsor sponsor, boolean empty) {
                    super.updateItem(sponsor, empty);
                    if (empty || sponsor == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Texte affichant les infos du sponsor
                        Text sponsorText = new Text(sponsor.getNomSponso() + " " + sponsor.getPrenomSponso() + " - " +
                                sponsor.getEmailSponso() + " - Budget: " + sponsor.getBudgetSponso());
                        sponsorText.getStyleClass().add("sponsor-text");

                        // Bouton Modifier
                        Button btnModifier = new Button("Modifier");
                        btnModifier.getStyleClass().add("btn-modifier");

                        // Ajouter un gestionnaire d'événements pour le bouton Modifier
                        btnModifier.setOnAction(event -> {
                            // Charger la page de modification
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSponsor.fxml"));
                                Parent root = loader.load();

                                ModifierSponsorController controller = loader.getController();
                                controller.setSponsor(sponsor);  // Passer l'objet sponsor à la page de modification

                                // Remplacer la scène actuelle avec la nouvelle scène de modification
                                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Modifier Sponsor");
                                stage.show();
                            } catch (IOException e) {
                                System.out.println("Erreur de chargement de la page de modification : " + e.getMessage());
                            }
                        });

                        // Bouton Supprimer
                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.getStyleClass().add("btn-supprimer");
                        // btnSupprimer.setOnAction(event -> { });

                        // HBox pour aligner les éléments
                        HBox hbox = new HBox(10, sponsorText, btnModifier, btnSupprimer);
                        hbox.getStyleClass().add("hbox-item");
                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
*/

package esprit.tn.controllers;

import esprit.tn.entities.Evenement;
import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceEvenement;
import esprit.tn.services.ServiceSponsor;  // Assurez-vous d'avoir une classe pour le service des sponsors
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherSponsorController {

    @FXML
    private ListView<Sponsor> lv_sponsor;
    @FXML
    private TextField txtRechercheSponso;
    @FXML
    void initialize() {
        ServiceSponsor serviceSponsor = new ServiceSponsor();
        try {
            ObservableList<Sponsor> observableList = FXCollections.observableList(serviceSponsor.afficher());
            lv_sponsor.setItems(observableList);

            lv_sponsor.setCellFactory(param -> new ListCell<Sponsor>() {
                @Override
                protected void updateItem(Sponsor sponsor, boolean empty) {
                    super.updateItem(sponsor, empty);
                    if (empty || sponsor == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Texte affichant les infos du sponsor
                        Text sponsorText = new Text(sponsor.getNomSponso() + " " + sponsor.getPrenomSponso() + " - " +
                                sponsor.getEmailSponso() + " - Budget: " + sponsor.getBudgetSponso());
                        sponsorText.getStyleClass().add("sponsor-text");

                        // Bouton Modifier
                        Button btnModifier = new Button("Modifier");
                        btnModifier.getStyleClass().add("btn-modifier");
                        btnModifier.setStyle("-fx-background-color: ffc400; -fx-text-fill: white;");

                        // Ajouter un gestionnaire d'événements pour le bouton Modifier
                        btnModifier.setOnAction(event -> {
                            // Charger la page de modification
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSponsor.fxml"));
                                Parent root = loader.load();

                                ModifierSponsorController controller = loader.getController();
                                controller.setSponsor(sponsor);  // Passer l'objet sponsor à la page de modification

                                // Remplacer la scène actuelle avec la nouvelle scène de modification
                                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Modifier Sponsor");
                                stage.show();
                            } catch (IOException e) {
                                System.out.println("Erreur de chargement de la page de modification : " + e.getMessage());
                            }
                        });

                        // Bouton Supprimer
                        Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.getStyleClass().add("btn-supprimer");
                        btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");

                        // Ajouter un gestionnaire d'événements pour le bouton Supprimer
                        btnSupprimer.setOnAction(event -> {
                            try {
                                // Afficher une boîte de confirmation avant suppression
                                Alert alertConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
                                alertConfirmation.setTitle("Confirmation");
                                alertConfirmation.setHeaderText("Voulez-vous vraiment supprimer ce sponsor ?");
                                alertConfirmation.setContentText("Cette action est irréversible.");

                                if (alertConfirmation.showAndWait().get() == ButtonType.OK) {
                                    // Si l'utilisateur confirme, procéder à la suppression
                                    int sponsorId = sponsor.getIdSponsor();
                                    System.out.println("ID du sponsor à supprimer : " + sponsorId);

                                    serviceSponsor.supprimer(sponsorId);
                                    observableList.remove(sponsor); // Met à jour la liste affichée

                                    // Afficher un message de succès
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Succès");
                                    alert.setHeaderText("Suppression réussie");
                                    alert.setContentText("Le sponsor a été supprimé avec succès.");
                                    alert.showAndWait();
                                }
                            } catch (SQLException e) {
                                // Afficher un message d'erreur en cas d'échec de suppression
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Erreur");
                                alert.setHeaderText("Erreur lors de la suppression du sponsor");
                                alert.setContentText("Une erreur est survenue : " + e.getMessage());
                                alert.showAndWait();
                            }
                        });

                        // HBox pour aligner les éléments
                        HBox hbox = new HBox(10, sponsorText, btnModifier, btnSupprimer);
                        hbox.getStyleClass().add("hbox-item");
                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    public void Onajoutersponso(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSponsor.fxml"));
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
    public void OnchercherSponsor(ActionEvent actionEvent) throws SQLException {
        String searchText = txtRechercheSponso.getText();
        ServiceSponsor serviceSponsor=new ServiceSponsor();
        // Liste des sponsor
        List<Sponsor> allSponsor = serviceSponsor.afficher();

        // Filtrer les sponsor avec Stream en fonction du texte de recherche
        List<Sponsor> filteredSponsor = allSponsor.stream()
                .filter(Sponsor -> Sponsor.getNomSponso().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        // Mettre à jour l'affichage des résultats de la recherche
        updateSponsorListView(filteredSponsor);

    }
    private void updateSponsorListView(List<Sponsor> filteredSponsor) {
        // Convertir la liste filtrée en ObservableList pour l'affichage
        ObservableList<Sponsor> observableList = FXCollections.observableArrayList(filteredSponsor);
        lv_sponsor.setItems(observableList);  // formationListView est votre ListView ou TableView
    }

}
