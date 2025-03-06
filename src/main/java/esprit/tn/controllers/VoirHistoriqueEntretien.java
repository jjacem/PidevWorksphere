package esprit.tn.controllers;

import esprit.tn.entities.HistoriqueEntretien;
import esprit.tn.services.EntretienService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VoirHistoriqueEntretien {

    @FXML
    private ListView<HistoriqueEntretien> historiqueListView;
    Button btnRestaurer ;

   // FXML Button for actions, but not used in the ListCell

    private EntretienService entretienService = new EntretienService();

    @FXML
    public void initialize() {
        try {
            afficherhistorique(); // Populate the ListView with historical data
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherhistorique() throws SQLException {

        List<HistoriqueEntretien> historiqueList = entretienService.afficherHistorique();
        ObservableList<HistoriqueEntretien> observableList = FXCollections.observableArrayList(historiqueList);
        historiqueListView.setItems(observableList);

        historiqueListView.setCellFactory(listView -> new ListCell<HistoriqueEntretien>() {
            @Override
            protected void updateItem(HistoriqueEntretien item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null && !empty) {
                    VBox vbox = new VBox(10);
                    vbox.getStyleClass().add("historique-item"); // Apply custom CSS class

                    Label titreLabel = new Label("📝 Titre: " + item.getTitre());
                    Label descriptionLabel = new Label("📄 Description: " + item.getDescription());
                    Label dateLabel = new Label("📅 Date: " + item.getDateEntretien());
                    Label heureLabel = new Label("🕒 Heure: " + item.getHeureEntretien());
                    Label typeLabel = new Label("📌 Type: " + item.getTypeEntretien());
                    Label statusLabel = new Label("✅ Status: " + (item.isStatus() ? "Active" : "Inactive"));
                    Label dateAction = new Label("📅 Date Action: " + item.getDateAction());
                    Label actionLabel = new Label("🔧 Action: " + item.getAction());

                    btnRestaurer = new Button("🔄 Restaurer");

                    if ("suppression".equals(item.getAction())) {
                        btnRestaurer.setText("🔄 Restaurer l'entretien");
                    } else {
                        btnRestaurer.setDisable(true);
                    }

                    btnRestaurer.setOnAction(event -> {
                        try {
                            restaurerEntretien(item);
                        } catch (SQLException e) {
                            e.printStackTrace(); // Handle SQLException
                        }
                    });

                    vbox.getChildren().addAll(titreLabel, descriptionLabel, dateLabel, heureLabel, typeLabel, statusLabel, actionLabel, dateAction, btnRestaurer);
                    setGraphic(vbox);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    //hh




    public void restaurerEntretien(HistoriqueEntretien historiqueEntretien) throws SQLException {
        try {
            if (historiqueEntretien != null) {
                System.out.println("Restoring entretien: " + historiqueEntretien.getTitre());
                entretienService.restaurer(historiqueEntretien.getEntretienId());
                refreshAffichageEntretien();
            } else {
                System.out.println("No entretien selected");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly
        }
    }


    private void refreshAffichageEntretien() throws SQLException {
        try {
            Stage stage = (Stage)  btnRestaurer.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEntretien.fxml"));
            Parent root = loader.load();
            AffichageEntretineController controller = loader.getController();
            controller.initialize();
//            Stage stage = (Stage) cb_type_entretien.getScene().getWindow();
//            stage.getScene().setRoot(root);
//            stage.setTitle("Liste des Entretiens");
//            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de rafraîchir l'affichage : " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    public void fermerFenetre(ActionEvent actionEvent) {

            try {


                Stage stage = (Stage) btnRestaurer.getScene().getWindow();
                stage.close();

            } catch (Exception e) {
                showAlert("Erreur", "Impossible de fermer la fenêtre.");
            }
        }


    }

