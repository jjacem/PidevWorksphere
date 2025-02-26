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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherReclamationsController {

    @FXML
    private ListView<Reclamation> listView;

    @FXML
    private Button btnAjouter;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private AnchorPane mainContainer;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceReponse serviceReponse = new ServiceReponse();
    private User logged;

    @FXML
    public void initialize() {
        try {
            checkRole();
            logged = SessionManager.extractuserfromsession();
            setupStatusFilter();
            afficherReclamations();
            setupListViewCellFactory();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la récupération des réclamations.");
            e.printStackTrace();
        }
    }

    private void setupStatusFilter() {
        statusChoiceBox.setItems(FXCollections.observableArrayList("Tous", "Résolu", "en attente", "Refusé","pas vus"));
        statusChoiceBox.setValue("Tous");
        statusChoiceBox.setOnAction(event -> refreshList());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refreshList());
    }

    private void refreshList() {
        try {
            afficherReclamations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void afficherReclamations() throws SQLException {
        String selectedStatus = statusChoiceBox.getValue();
        String searchText = searchField.getText().trim().toLowerCase();

        List<Reclamation> reclamations = (logged.getRole() == Role.EMPLOYE)
                ? serviceReclamation.getReclamationsByUser2(logged.getIdUser())
                : serviceReclamation.getReclamationsByUser(logged.getIdUser());





        if (!selectedStatus.equals("Tous")) {
            reclamations=reclamations.stream()
                    .filter(r -> r.getReponse() != null && r.getReponse().getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());        }
        if (selectedStatus.equals("Tous")) {
            reclamations = (logged.getRole() == Role.EMPLOYE)
                    ? serviceReclamation.getReclamationsByUser2(logged.getIdUser())
                    : serviceReclamation.getReclamationsByUser(logged.getIdUser());
        }
        if (selectedStatus.equals("pas")) {
            reclamations=reclamations.stream()
                    .filter(r -> r.getReponse() == null )
                    .collect(Collectors.toList());
        }

        if (!searchText.isEmpty()) {
            reclamations = reclamations.stream()
                    .filter(r -> r.getTitre().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());;
        }

        ObservableList<Reclamation> items = FXCollections.observableArrayList(reclamations);
        listView.setItems(items);
    }


    private void setupListViewCellFactory() {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(15);

                    Label titleLabel = new Label("Titre: " + r.getTitre());
                    Label statusLabel = new Label("Status: " + r.getStatus());
                    Label descriptionLabel = new Label("Description: " + r.getDescription());
                    Label dateLabel = new Label("Date: " + r.getDatedepot());

                    hbox.getChildren().addAll( titleLabel,statusLabel, descriptionLabel, dateLabel);

                    Reponse reponse = serviceReponse.checkForRepInRec(r.getId_reclamation());
                    if (reponse != null) {
                        Button btnView = new Button();
                        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/eye.png")));
                        eyeIcon.setFitWidth(16);
                        eyeIcon.setFitHeight(16);
                        btnView.setGraphic(eyeIcon);
                        btnView.setOnAction(e -> afficherReponse(r.getId_reclamation(), r.getId_user2()));
                        hbox.getChildren().add(btnView);

                        if (logged.getRole() == Role.EMPLOYE) {


                            ImageView editIcon = new ImageView(new Image(getClass().getResource("/icons/edit.png").toExternalForm()));
                            editIcon.setFitWidth(16);
                            editIcon.setFitHeight(16);
                            Button editButton = new Button("", editIcon);
                            editButton.setStyle("-fx-background-color: transparent;");
                            editButton.setOnAction(event -> modifierreponse(r.getReponse()));
                            hbox.getChildren().add(editButton);


                            ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/icons/delete.png").toExternalForm()));
                            deleteIcon.setFitWidth(16);
                            deleteIcon.setFitHeight(16);
                            Button deleteButton = new Button("", deleteIcon);
                            deleteButton.setStyle("-fx-background-color: transparent;");
                            deleteButton.setOnAction(event -> deletereponse(r.getReponse()));
                            hbox.getChildren().add(deleteButton);
                        }
                    }



                    else if (logged.getRole()==Role.EMPLOYE) {
                        Button btnView = new Button();
                        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/comments.png")));
                        eyeIcon.setFitWidth(16);
                        eyeIcon.setFitHeight(16);
                        btnView.setGraphic(eyeIcon);
                        btnView.setOnAction(e -> ajouterreponse(r.getId_reclamation(), r.getId_user2()));
                        hbox.getChildren().add(btnView);





                    }

                    setGraphic(hbox);
                }
            }
        });
    }

    private void deletereponse(Reponse reponse)  {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression de la réponse");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réponse ?");

        ButtonType buttonYes = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                try {
                    serviceReponse.supprimer(reponse.getId_reponse());
                    showAlert("Succès", "Réponse supprimée avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression de la réponse.");
                    e.printStackTrace();
                }
            }
        });

    }

    private void modifierreponse(Reponse r) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReponse.fxml"));
            Parent root = loader.load();
            ModifierReponseController controller = loader.getController();
            controller.setReponse(r);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Modifier Réponse");
            popupStage.setScene(new Scene(root));

            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ajouterreponse(int idReclamation, int idUser2) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReponse.fxml"));
            Parent root = loader.load();


            AjouterReponseController controller = loader.getController();
            controller.setIds(idUser2, idReclamation);


            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Ajouter Réponse");
            popupStage.setScene(new Scene(root));


            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void afficherReponse(int idReclamation, int idUser2) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherReponse.fxml"));
            Parent root = loader.load();


            AfficherReponse controller = loader.getController();
            controller.setIds(idUser2, idReclamation);


            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Réponse");
            popupStage.setScene(new Scene(root));


            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }    }


    private void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
            Parent newContent = loader.load();
            mainContainer.getChildren().setAll(newContent);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void checkRole() throws SQLException {
        if (SessionManager.getRole()=="EMPLOYE") {

            btnAjouter.setVisible(false);
            btnAjouter.setDisable(true);
        }
    }

    public void ajouterReclamation(ActionEvent actionEvent) {
        loadPage("/AjouterReclamation.fxml");
    }
}