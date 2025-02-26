package esprit.tn.controllers;

import esprit.tn.entities.Role;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                            row.setStyle("-fx-padding: 10px; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-background-color: #ffffff; -fx-alignment: center;");

                            // Always show the default profile image from resources
                            ImageView profileImageView = new ImageView(new Image(getClass().getResource("/Images/profil.png").toExternalForm()));
                            profileImageView.setFitWidth(50);
                            profileImageView.setFitHeight(50);
                            profileImageView.setPreserveRatio(true);

                            row.getChildren().add(profileImageView);


                            List<String> userAttributes = getUserAttributes(user);
                            for (String attr : userAttributes) {
                                Label label = new Label(attr);
                                label.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
                                HBox.setHgrow(label, Priority.ALWAYS);
                                row.getChildren().add(label);
                            }


                            ImageView modifyIcon = new ImageView(new Image(getClass().getResource("/icons/edit.png").toExternalForm()));
                            modifyIcon.setFitWidth(16);
                            modifyIcon.setFitHeight(16);
                            Button modifyButton = new Button("", modifyIcon);
                            modifyButton.setStyle("-fx-background-color: transparent;");
                            modifyButton.setOnAction(event -> openModifierUser(user.getIdUser()));


                            ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/icons/delete.png").toExternalForm()));
                            deleteIcon.setFitWidth(16);
                            deleteIcon.setFitHeight(16);
                            Button deleteButton = new Button("", deleteIcon);
                            deleteButton.setStyle("-fx-background-color: transparent;");
                            deleteButton.setOnAction(event -> deleteUser(user));



                            ImageView promoteIcon = new ImageView(new Image(getClass().getResource("/icons/promotion.png").toExternalForm()));
                            promoteIcon.setFitWidth(16);
                            promoteIcon.setFitHeight(16);
                            Button promoteButton = new Button("Promote", promoteIcon);
                            promoteButton.setStyle("-fx-background-color: transparent;");
                            promoteButton.setOnAction(event -> PromoteUser(user));


                            HBox buttonsBox = new HBox(5, modifyButton, deleteButton,promoteButton);
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


    public void PromoteUser(User u) {
        Role userRole = u.getRole();
        ArrayList<String> s = new ArrayList<>();

        switch (userRole) {
            case CANDIDAT:
                s = new ArrayList<>(List.of("Manager", "Employe", "RH"));
                break;
            case EMPLOYE:
                s = new ArrayList<>(List.of( "RH", "Manager"));
                break;
            case RH:
                s = new ArrayList<>(List.of( "Employe", "Manager"));
                break;
            case MANAGER:
                s = new ArrayList<>(List.of("Employe","RH"));
                break;
            default:
                s = new ArrayList<>();
                break;
        }


        openPromotionWindow(s,u);
    }
    private Map<String, TextField> inputFields = new HashMap<>(); // Store references to text fields
   private ServiceUser userservice=new ServiceUser();
    public void openPromotionWindow(ArrayList<String> roles, User u) {
        Stage stage = new Stage();
        stage.setTitle("Promote User");

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(roles);

        VBox layout = new VBox(10);
        layout.getChildren().add(choiceBox);

        Button promoteButton = new Button("Promote");

        choiceBox.setOnAction(e -> {
            layout.getChildren().removeIf(node -> node instanceof TextField || node instanceof Label);
            inputFields.clear();
            String selectedRole = choiceBox.getValue();

            if ("Candidat".equals(selectedRole)) {
                System.out.println("User promoted to Candidat");
            } else if ("Employe".equals(selectedRole)) {
                layout.getChildren().addAll(createEmployeFields());
            } else if ("RH".equals(selectedRole)) {
                layout.getChildren().addAll(createRhFields());
            } else if ("Manager".equals(selectedRole)) {
                layout.getChildren().addAll(createManagerFields());
            }
        });

        promoteButton.setOnAction(e -> {
            String selectedRole = choiceBox.getValue();
            if (selectedRole != null) {
                switch (selectedRole) {
                    case "Employe":
                        userservice.changetoEmploye(u);
                        break;
                    case "RH":
                        userservice.changetoRH(u);
                        break;
                    case "Manager":
                        userservice.changetoManager(u);
                        break;
                    default:
                        System.out.println("User promoted to: " + selectedRole);
                        break;
                }

                stage.close();
            }
        });

        layout.getChildren().add(promoteButton);
        Scene scene = new Scene(layout, 350, 400);
        stage.setScene(scene);
        stage.show();
    }

    private List<Node> createEmployeFields() {
        return createFields(
                "Competence", "Departement", "Salaire", "Experience Travail"
        );
    }

    private List<Node> createRhFields() {
        return createFields(
                "Années d'expérience", "Spécialisation"
        );
    }

    private List<Node> createManagerFields() {
        return createFields(
                "Nombre de Projets", "Budget", "Département Géré"
        );
    }

    private List<Node> createFields(String... fieldNames) {
        List<Node> nodes = new ArrayList<>();
        for (String fieldName : fieldNames) {
            Label label = new Label(fieldName + ":");
            TextField textField = new TextField();
            textField.setPromptText(fieldName);
            inputFields.put(fieldName, textField);
            nodes.add(label);
            nodes.add(textField);
        }
        return nodes;
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
