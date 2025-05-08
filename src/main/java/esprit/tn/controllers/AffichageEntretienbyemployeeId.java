package esprit.tn.controllers;

import esprit.tn.entities.Entretien;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import esprit.tn.services.EntretienService;

import javax.swing.text.Element;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AffichageEntretienbyemployeeId {
    public Button btn_afficher;
    @FXML
    private ListView<Entretien> lv_entretien;

    private EntretienService entretienService = new EntretienService();

    private GemeniService gemeniService = new GemeniService();


    private ObservableList<Entretien> allEntretiens = FXCollections.observableArrayList();


    @FXML
    private Button btn_ajouter;
    @FXML
    private TextField searchField;


    @FXML
    public void initialize() throws SQLException {
        afficherEntretien();
        lv_entretien.getStylesheets().add(getClass().getResource("/controlleurAffichageById.css").toExternalForm());
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

                    Button btnGenererQuestions = new Button("📄 Générer des Questions entretien avec AI");
                    btnGenererQuestions.getStyleClass().add("button-ai");
                    btnGenererQuestions.setOnAction(event -> genererQuestionsAI(entretien.getTitre()));

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

                    Button btnMarquerTermine = new Button("✅ Marquer entrtien  Terminé");
                    btnMarquerTermine.getStyleClass().add("button-terminer");
                    btnMarquerTermine.setOnAction(event -> {
                        marquerEntretienTermine(entretien.getId());
                    });

                    btnMarquerTermine.setDisable(entretien.isStatus());

                    HBox buttonBox = new HBox(10, btnFeedback, btnVoirDetail, btnGenererQuestions, btnMarquerTermine);
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

                    // Ajouter le contenu et les boutons à la cellule
                    setGraphic(new VBox(vbox, buttonBox));
                }

            }
        });
    }



    private void genererQuestionsAI(String titre) {
        Optional<String> questionsOpt = GemeniService.getQuestionsFromChatbot(titre);

        questionsOpt.ifPresent(questions -> {
            String fileName = "questions_entretien_" + titre.replaceAll("\\s+", "_") + ".pdf";
            String downloadPath = GemeniService.generatePDF(questions, fileName);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Téléchargement réussi");
            alert.setHeaderText("Votre PDF a été téléchargé avec succès !");

            TextArea textArea = new TextArea("Le fichier a été téléchargé à l'emplacement suivant : " + downloadPath);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);

            alert.getDialogPane().setContent(textArea);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);


            alert.showAndWait();
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


    public void affichagecalendar(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageGoogleCalendar.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btn_afficher.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Entretiens dans Google Calendar");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void marquerEntretienTermine(int idEntretien) {
        try {
            Entretien entretien = entretienService.getEntretienById(idEntretien);

            if (entretien == null) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Aucun entretien trouvé avec cet ID !");
                errorAlert.showAndWait();
                return;
            }

            if (entretien.getFeedbackId() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Feedback manquant");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez ajouter un feedback avant de marquer l'entretien comme terminé !");
                alert.showAndWait();
                return;
            }

            entretienService.marquerCommeTermineEtArchive(idEntretien);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("L'entretien a été marqué comme terminé et archivé avec succès !");
            alert.showAndWait();

            refreshDatas();
        } catch (SQLException e) {
            e.printStackTrace();

            // Afficher une alerte d'erreur en cas d'échec
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Une erreur s'est produite lors de la mise à jour de l'entretien.");
            errorAlert.showAndWait();
        }
    }




















}




