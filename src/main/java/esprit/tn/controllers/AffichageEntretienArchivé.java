package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
import esprit.tn.services.EntretienService;
import esprit.tn.services.GemeniService;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AffichageEntretienArchivé {

    public Button btn_afficher;
    @FXML
    private ListView<Entretien> lv_entretien;

    private EntretienService entretienService = new EntretienService();


    private ObservableList<Entretien> allEntretiens = FXCollections.observableArrayList();





    @FXML
    public void initialize() throws SQLException {
        afficherEntretien();
        lv_entretien.getStylesheets().add(getClass().getResource("/controlleurAffichageById.css").toExternalForm());

    }

    private void afficherEntretien() throws SQLException {
        List<Entretien> entretiens = entretienService.getEntretiensByEmployeIdAndArchivé(SessionManager.extractuserfromsession().getIdUser());
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
                    ImageView iconTitre = new ImageView(new Image(getClass().getResourceAsStream("/icons/job-seeker.png")));
                    iconTitre.setFitHeight(24);
                    iconTitre.setFitWidth(24);
                    iconTitre.setPreserveRatio(true);

                    ImageView iconDescription = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit-info.png")));
                    iconDescription.setFitHeight(24);
                    iconDescription.setFitWidth(24);
                    iconDescription.setPreserveRatio(true);

                    ImageView iconDate = new ImageView(new Image(getClass().getResourceAsStream("/icons/date.png")));
                    iconDate.setFitHeight(24);
                    iconDate.setFitWidth(24);
                    iconDate.setPreserveRatio(true);

                    ImageView iconType = new ImageView(new Image(getClass().getResourceAsStream("/icons/type.png")));
                    iconType.setFitHeight(24);
                    iconType.setFitWidth(24);
                    iconType.setPreserveRatio(true);

                    ImageView iconStatut = new ImageView(new Image(getClass().getResourceAsStream("/icons/checked.png")));
                    iconStatut.setFitHeight(24);
                    iconStatut.setFitWidth(24);
                    iconStatut.setPreserveRatio(true);


                    Button btnVoirDetail = new Button("Voir Détails");
                    btnVoirDetail.getStyleClass().add("button");
                    btnVoirDetail.setOnAction(event -> voirDetailEntretien(entretien));

                    Button btnFeedback;
                    if (entretien.getFeedbackId() != 0) {
                        btnFeedback = new Button("📄 Voir Feedback");
                        btnFeedback.getStyleClass().add("button-feedback");
                        btnFeedback.setOnAction(event -> voirFeedback(entretien.getFeedbackId()));
                    } else {
                        btnFeedback = new Button("➕ Ajouter Feedback");
                        btnFeedback.getStyleClass().add("button-feedback");
                        btnFeedback.setOnAction(event -> {
                            ajouterFeedback(entretien.getId());
                            try {
                                afficherEntretien();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });
                    }



                    HBox buttonBox = new HBox(10, btnFeedback, btnVoirDetail);
                    buttonBox.getStyleClass().add("hbox-buttons");

                    VBox vbox = new VBox(5);
                    Label titreLabel = new Label("Titre: " + entretien.getTitre(), iconTitre);
                    titreLabel.getStyleClass().add("titre-label");

                    Label descriptionLabel = new Label("Description: " + entretien.getDescription(), iconDescription);
                    descriptionLabel.getStyleClass().add("description-label");

                    Label dateLabel = new Label("Date: " + entretien.getDate_entretien() + "  Heure: " + entretien.getHeure_entretien(), iconDate);
                    dateLabel.getStyleClass().add("date-label");

                    Label typeLabel = new Label("Type: " + entretien.getType_entretien(), iconType);
                    typeLabel.getStyleClass().add("type-label");

                    Label statutLabel = new Label("Statut: " + (entretien.isStatus() ? "Terminé" : "En cours"), iconStatut);
                    statutLabel.getStyleClass().add("statut-label");

                    vbox.getChildren().addAll(titreLabel, descriptionLabel, dateLabel, typeLabel, statutLabel);

                    setGraphic(new VBox(vbox, buttonBox));
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



















