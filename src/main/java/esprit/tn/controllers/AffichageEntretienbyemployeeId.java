package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AffichageEntretienbyemployeeId {
    @FXML
    private ListView<Entretien> lv_entretien;

    private EntretienService entretienService = new EntretienService();

    private ObservableList<Entretien> allEntretiens = FXCollections.observableArrayList();


    @FXML
    private Button btn_ajouter;
    @FXML
    private TextField searchField;


    @FXML
    public void initialize() throws SQLException {
        afficherEntretien();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterEntretiens(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void afficherEntretien() throws SQLException {
        List<Entretien> entretiens = entretienService.getEntretiensByEmployeId(SessionManager.extractuserfromsession().getIdUser());
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

                    Button btnVoirDetail = new Button("Voir Détails");
                    btnVoirDetail.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                    btnVoirDetail.setOnAction(event -> voirDetailEntretien(entretien));





                    Button btnFeedback;

                    if (entretien.getFeedbackId() != 0) {
                        btnFeedback = new Button("📄Voir Feedback");
                        btnFeedback.setStyle("-fx-background-color: #ffc400; -fx-text-fill: white;");

                        btnFeedback.setOnAction(event -> voirFeedback(entretien.getFeedbackId()));
                    } else {
                        btnFeedback = new Button("➕ Ajouter Feedback");
                        btnFeedback.setStyle("-fx-background-color: #ffc400; -fx-text-fill: white;");

                        btnFeedback.setOnAction(event -> {
                            ajouterFeedback(entretien.getId());
                            try {
                                afficherEntretien();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }


                    HBox buttonBox = new HBox(10, btnFeedback , btnVoirDetail);
                    buttonBox.setStyle("-fx-padding: 5px; -fx-alignment: center-left;");

                    setText("📝 Titre: " + entretien.getTitre() + "\n"
                            + "Description: " + entretien.getDescription() + "\n"
                            + "📅 Date: " + entretien.getDate_entretien() + "  🕒 Heure: " + entretien.getHeure_entretien() + "\n"
                            + "📌 Type: " + entretien.getType_entretien() + "\n"
                            + "✅ Statut: " + (entretien.isStatus() ? "Terminé ✅" : "En cours ⏳"));

                    setStyle("-fx-padding: 10px; -fx-background-color: #f5f5f5; -fx-border-color: #dcdcdc; -fx-border-radius: 5px; -fx-font-size: 14px;");
                    setGraphic(buttonBox);
                }
            }
        });

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
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Ajouter un entretien");
//            stage.show();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);


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
            stage.show();
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


    void refreshDatas() throws SQLException {

        afficherEntretien();


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



}




