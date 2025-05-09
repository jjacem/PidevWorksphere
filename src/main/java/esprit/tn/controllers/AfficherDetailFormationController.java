package esprit.tn.controllers;

import esprit.tn.entities.Role;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import esprit.tn.entities.Formation;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceReservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

public class AfficherDetailFormationController {

    @FXML
    private Label DescID;
    @FXML
    private Label titreID;
    @FXML
    private Label tfID;
    @FXML
    private Label DateID;
    @FXML
    private Label certifieID;
    @FXML
    private Label langueID;
    @FXML
    private VBox contentArea;

    @FXML
    private ListView<User> userListView;
    private final ServiceReservation serviceReservation = new ServiceReservation();

    @FXML
    private Button AfficherlistID;
    @FXML
    private Label nbplaceID;

    private Formation formation;


    @FXML
    public void initialize() {
        if (SessionManager.getRole().equals(Role.EMPLOYE.name())) {
            AfficherlistID.setVisible(false);
            userListView.setVisible(false);
            contentArea.setVisible(false);
        }

        userListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (user != null && !empty) {
                            HBox hbox = new HBox(10);
                            Label nomLabel = new Label("Nom: " + user.getNom());
                            Label prenomLabel = new Label("Prénom: " + user.getPrenom());
                            Label emailLabel = new Label("Email: " + user.getEmail());
                            hbox.getChildren().addAll(nomLabel, prenomLabel, emailLabel);
                            setGraphic(hbox);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
        if (formation != null) {
            titreID.setText(formation.getTitre());
            DescID.setText(formation.getDescription());
            tfID.setText(formation.getType().toString());
            DateID.setText(formation.getDate().toString());
            certifieID.setText(formation.getCertifie() != null ? formation.getCertifie().toString() : "Non spécifié");
            langueID.setText(formation.getLangue() != null ? formation.getLangue() : "Non spécifiée");
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