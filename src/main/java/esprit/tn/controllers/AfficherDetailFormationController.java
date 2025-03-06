package esprit.tn.controllers;

import esprit.tn.entities.Role;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import esprit.tn.entities.Formation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceFormation;
import esprit.tn.services.ServiceReservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.List;

public class AfficherDetailFormationController {

    @FXML
    private Label DescID;
    @FXML
    private Label hdID;
    @FXML
    private Label hfID;
    @FXML
    private Label titreID;
    @FXML
    private Label tfID;
    @FXML
    private Label nbplaceID;
    @FXML
    private Label DateID;

    private Formation formation;
    @FXML
    private ListView<User> userListView;
    private final ServiceReservation serviceReservation = new ServiceReservation();
    @FXML
    private VBox contentArea;
    @FXML
    private Button AfficherlistID;

    @FXML
    public void initialize() {
        if (SessionManager.getRole().equals(Role.EMPLOYE.name())) {
            AfficherlistID.setVisible(false);
            userListView.setVisible(false);
            contentArea.setVisible(false);
        }
        // Initialisation de la ListView
        userListView = new ListView<>();
        userListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (user != null && !empty) {
                            HBox hbox = new HBox(10); // Espacement de 10 entre les labels
                            Label nomLabel = new Label("Nom: " + user.getNom());
                            nomLabel.getStyleClass().addAll("label-detail", "label-detail");
                            Label prenomLabel = new Label("Prénom: " + user.getPrenom());
                            prenomLabel.getStyleClass().addAll("label-detail", "label-detail");
                            Label emailLabel = new Label("Email: " + user.getEmail());
                            emailLabel.getStyleClass().addAll("label-detail", "label-detail");
                            hbox.getChildren().addAll(nomLabel, prenomLabel, emailLabel);
                            setGraphic(hbox);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });

        // Ajouter la ListView au contentArea
        contentArea.getChildren().add(userListView);
       // contentArea.setStyle();
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        if (formation != null) {
            titreID.setText(formation.getTitre());
            DescID.setText(formation.getDescription());
            hdID.setText(formation.getHeure_debut().toString());
            hfID.setText(formation.getHeure_fin().toString());
            tfID.setText(formation.getType().toString());
            nbplaceID.setText(String.valueOf(formation.getNb_place()));
            DateID.setText(formation.getDate().toString());
        }
    }

    @FXML
    public void onListeEmploye(ActionEvent actionEvent) {
        if (formation != null) {
            try {
                int formationId = formation.getId_f();
                List<User> users = serviceReservation.getUsersWhoReservedFormation(formationId);
                ObservableList<User> userList = FXCollections.observableArrayList(users);
                userListView.setItems(userList);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
