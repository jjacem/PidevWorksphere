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
<<<<<<< Updated upstream
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
=======
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
>>>>>>> Stashed changes
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import javafx.geometry.Insets;

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

<<<<<<< Updated upstream
=======
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

>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
        listView.setItems(items);
    }

    @FXML
    public void ajouterReclamation() {
        openWindow("/AjouterReclamation.fxml", "Ajouter une Réclamation");
    }

<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
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
