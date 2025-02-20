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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
        statusChoiceBox.setItems(FXCollections.observableArrayList("Tous", "Résolu", "en cours", "Refusé"));
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
            reclamations = serviceReclamation.filterbystats(selectedStatus);
        }

        if (!searchText.isEmpty()) {
            reclamations = serviceReclamation.filterbytitle(searchText);
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


                    Label statusLabel = new Label("titre: " + r.getStatus());
                    Label titleLabel = new Label("Status: " + r.getTitre());
                    Label descriptionLabel = new Label("Description: " + r.getDescription());
                    Label dateLabel = new Label("Date: " + r.getDatedepot());

                    hbox.getChildren().addAll( statusLabel, titleLabel, descriptionLabel, dateLabel);
                    Reponse reponse = serviceReponse.checkForRepInRec(r.getId_reclamation());
                    if (reponse !=null) {


                        Button btnView = new Button();
                        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/eye.png")));
                        eyeIcon.setFitWidth(16);
                        eyeIcon.setFitHeight(16);
                        btnView.setGraphic(eyeIcon);

                        btnView.setOnAction(e -> afficherReponse(r.getId_reclamation(), r.getId_user2()));

                        hbox.getChildren().add(btnView);
                        try {
                            if (SessionManager.extractuserfromsession().getRole().equals(Role.EMPLOYE)){
                                Button ajrep = new Button();
                                ImageView edit = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit.png")));
                                edit.setFitWidth(16);
                                edit.setFitHeight(16);
                                ajrep.setGraphic(edit);
                                ajrep.setOnAction(e -> ajouterReclamation(r.getId_reclamation(), r.getId_user2()));

                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    setGraphic(hbox);
                }
            }
        });
    }

    private void ajouterReclamation(int idReclamation, int idUser2) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReponse.fxml"));
            Parent newContent = loader.load();

            AjouterReponseController controller = loader.getController();
            controller.setIds(idUser2, idReclamation);

            mainContainer.getChildren().setAll(newContent);





        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'afficher la réponse.");
            e.printStackTrace();
        }
    }

    private void afficherReponse(int idReclamation, int id_user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReponse.fxml"));
            Parent newContent = loader.load();

            AfficherReponse controller = loader.getController();
            controller.setIds(id_user, idReclamation);

            mainContainer.getChildren().setAll(newContent);





        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'afficher la réponse.");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

        if (SessionManager.extractuserfromsession().getRole().equals(Role.CANDIDAT)){

            this.btnAjouter.isVisible();
this.btnAjouter.isDisabled();



    }}

    public void ajouterReclamation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReclamation.fxml"));
            Parent newContent = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(newContent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert( "Navigation Error", "Failed to load the page: " + e.getMessage());
        }
    }}
