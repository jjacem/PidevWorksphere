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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import javafx.geometry.Insets;

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
        statusChoiceBox.setItems(FXCollections.observableArrayList("Tous", "Résolu", "en cours", "Refusé","pas vus"));
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





        if (selectedStatus.equals("pas vus")) {
            reclamations = reclamations.stream()
                    .filter(r -> r.getReponse() == null)
                    .collect(Collectors.toList());
        } else if (!selectedStatus.equals("Tous")) {
            reclamations = reclamations.stream()
                    .filter(r -> r.getReponse() != null && r.getReponse().getStatus().equalsIgnoreCase(selectedStatus))
                    .collect(Collectors.toList());
        }
        if (selectedStatus.equals("Tous")) {
            reclamations = (logged.getRole() == Role.EMPLOYE)
                    ? serviceReclamation.getReclamationsByUser2(logged.getIdUser())
                    : serviceReclamation.getReclamationsByUser(logged.getIdUser());
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
                    // Create a GridPane for better alignment
                    GridPane gridPane = new GridPane();
                    gridPane.setHgap(10); // Horizontal spacing between columns
                    gridPane.setVgap(5);  // Vertical spacing between rows
                    gridPane.setPadding(new Insets(10)); // 10 pixels padding on all sides

                    // Add labels to the GridPane
                    Label titleLabel = new Label("Titre: " + r.getTitre());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    Label statusLabel = new Label("Type: " + r.getType());
                    statusLabel.setStyle("-fx-text-fill: #555;");
                    Label descriptionLabel = new Label("Description: " + r.getDescription());
                    descriptionLabel.setStyle("-fx-text-fill: #555;");
                    Label dateLabel = new Label("Date: " + r.getDatedepot());
                    dateLabel.setStyle("-fx-text-fill: #777;");

                    // Add labels to the first column
                    gridPane.add(titleLabel, 0, 0);
                    gridPane.add(statusLabel, 0, 1);
                    gridPane.add(descriptionLabel, 0, 2);
                    gridPane.add(dateLabel, 0, 3);

                    // Create an HBox for action buttons
                    HBox buttonBox = new HBox(10); // Spacing between buttons
                    buttonBox.setAlignment(Pos.CENTER_RIGHT); // Align buttons to the right

                    Reponse reponse = serviceReponse.checkForRepInRec(r.getId_reclamation());

                    if (logged.getRole() == Role.CANDIDAT) {
                        // Add Edit Button
                        Button editButton = createIconButton("/icons/edit.png", "Edit");
                        editButton.setOnAction(event -> modiferreclamtion(r));
                        buttonBox.getChildren().add(editButton);

                        // Add Delete Button
                        Button deleteButton = createIconButton("/icons/delete.png", "Delete");
                        deleteButton.setOnAction(event -> deletereclamation(r));
                        buttonBox.getChildren().add(deleteButton);
                    }

                    if (reponse != null) {
                        // Add View Button
                        Button btnView = createIconButton("/icons/eye.png", "View");
                        btnView.setOnAction(e -> afficherReponse(r.getId_reclamation(), r.getId_user2()));
                        buttonBox.getChildren().add(btnView);

                        if (logged.getRole() == Role.EMPLOYE) {
                            // Add Edit Button for Reponse
                            Button editButton = createIconButton("/icons/edit.png", "Edit");
                            editButton.setOnAction(event -> modifierreponse(r.getReponse()));
                            buttonBox.getChildren().add(editButton);

                            // Add Delete Button for Reponse
                            Button deleteButton = createIconButton("/icons/delete.png", "Delete");
                            deleteButton.setOnAction(event -> deletereponse(r.getReponse()));
                            buttonBox.getChildren().add(deleteButton);
                        }
                    } else if (logged.getRole() == Role.EMPLOYE) {
                        // Add Add Response Button
                        Button btnView = createIconButton("/icons/comments.png", "Add Response");
                        btnView.setOnAction(e -> ajouterreponse(r.getId_reclamation(), r.getId_user2()));
                        buttonBox.getChildren().add(btnView);
                    }

                    // Add the button box to the GridPane
                    gridPane.add(buttonBox, 1, 0, 1, 4); // Span across rows

                    // Set the GridPane as the graphic for the cell
                    setGraphic(gridPane);
                }
            }
        });
    }

    // Helper method to create icon buttons
    private Button createIconButton(String iconPath, String tooltipText) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        Button button = new Button("", icon);
        button.setStyle("-fx-background-color: transparent;");
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }    private void modiferreclamtion(Reclamation r) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReclamation.fxml"));
            Parent root = loader.load();
            ModifierReclamationController controller = loader.getController();
            controller.setReclamationId(r.getId_reclamation());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Modifier Reclamation");
            popupStage.setScene(new Scene(root));

            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletereclamation(Reclamation r)  {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Suppression de la Reclamation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette Reclamation ?");

        ButtonType buttonYes = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonNo = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonYes) {
                try {
                    serviceReclamation.supprimer(r.getId_reclamation());
                    showAlert("Succès", "Réponse supprimée avec succès.");
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression de la réponse.");
                    e.printStackTrace();
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