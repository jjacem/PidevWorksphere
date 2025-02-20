package esprit.tn.controllers;

import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherUser {

    @FXML
    private ListView<String> attributes;
    @FXML
    private ListView<User> data;
    @FXML
    private ChoiceBox<String> filterrole;
    @FXML
    private TextField search;

    private ServiceUser serviceUser;
    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        userList = FXCollections.observableArrayList();
        data.setItems(userList);

        filterrole.setItems(FXCollections.observableArrayList("RH", "Employe", "Manager", "Candidat"));

        try {
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filterrole.setOnAction(event -> {
            try {
                updateListView(filterByRole(filterrole.getValue()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                onSearch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        setupListView();
    }

    private void setupListView() {
        data.setCellFactory(new Callback<>() {
            @Override
            public ListCell<User> call(ListView<User> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (user == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox row = new HBox(10);
                            row.setSpacing(20);
                            row.setStyle("-fx-padding: 5px; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-background-color: #f9f9f9;");

                            List<String> userAttributes = getUserAttributes(user);
                            for (String attr : userAttributes) {
                                Label label = new Label(attr);
                                label.setStyle("-fx-font-size: 14px; -fx-padding: 2px 5px;");
                                HBox.setHgrow(label, Priority.ALWAYS);
                                row.getChildren().add(label);
                            }


                            Button modifyButton = new Button("Modifier");
                            modifyButton.setOnAction(event -> openModifierUser(user.getIdUser()));


                            Button deleteButton = new Button("Supprimer");
                            deleteButton.setOnAction(event -> deleteUser(user));

                            HBox buttonsBox = new HBox(5, modifyButton, deleteButton);
                            row.getChildren().add(buttonsBox);

                            setGraphic(row);
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void onSearch() throws SQLException {
        String searchText = search.getText().trim();
        if (searchText.isEmpty()) {
            loadData();
            return;
        }

        List<User> filteredUsers;
        if (searchText.contains("@")) {
            filteredUsers = serviceUser.chercherparmail(searchText);
        } else {
            filteredUsers = serviceUser.chercherparnom(searchText);
        }

        updateListView(filteredUsers);
    }

    private List<User> filterByRole(String role) throws SQLException {
        return serviceUser.afficher().stream()
                .filter(user -> user.getRole().name().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    private void updateListView(List<User> users) {
        userList.clear();
        userList.addAll(users);
    }

    private void loadData() throws SQLException {
        List<User> allUsers = serviceUser.afficher();
        updateListView(allUsers);
    }

    private void deleteUser(User user) {
        try {
            serviceUser.supprimer(user.getIdUser());
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openModifierUser(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCompte.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            ModifierCompteController  controller = loader.getController();
            controller.initData(userId);

            stage.setTitle("Modifier Utilisateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getUserAttributes(User user) {
        return List.of(
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getAdresse(),
                user.getSexe().name(),
                user.getRole().name()
        );
    }
}
