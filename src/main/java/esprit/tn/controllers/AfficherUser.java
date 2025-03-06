package esprit.tn.controllers;

import esprit.tn.entities.Status;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import esprit.tn.entities.Role;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
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
    @FXML
    private Button showReclamations;

    private ServiceUser serviceUser;
    private ObservableList<User> userList;

    public void initialize() {
        serviceUser = new ServiceUser();
        userList = FXCollections.observableArrayList();
        data.setItems(userList);

        // Modified ChoiceBox initialization with "Tout" as default
        filterrole.setItems(FXCollections.observableArrayList("Tout", "RH", "Employe", "Manager", "Candidat"));
        filterrole.setValue("Tout"); // Set default value

        try {
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filterrole.setOnAction(event -> {
            try {
                String selectedRole = filterrole.getValue();
                if ("Tout".equals(selectedRole)) {
                    loadData(); // Show all users when "Tout" is selected
                } else {
                    updateListView(filterByRole(selectedRole));
                }
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
                            HBox row = new HBox(15);
                            row.setPadding(new Insets(10));
                            row.setAlignment(Pos.CENTER_LEFT);
                            row.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-background-color: #ffffff;");

                            ImageView profileImageView = new ImageView(new Image(getClass().getResource("/Images/profil.png").toExternalForm()));
                            profileImageView.setFitWidth(50);
                            profileImageView.setFitHeight(50);
                            profileImageView.setPreserveRatio(true);

                            VBox userInfoBox = new VBox(5);
                            userInfoBox.setAlignment(Pos.CENTER_LEFT);

                            List<String> userAttributes = getUserAttributes(user);
                            for (String attr : userAttributes) {
                                Label label = new Label(attr);
                                label.setStyle("-fx-font-size: 14px;");
                                userInfoBox.getChildren().add(label);
                            }

                            HBox buttonsBox = new HBox(10);
                            buttonsBox.setAlignment(Pos.CENTER_RIGHT);

                            // Modify Button
                            ImageView modifyIcon = new ImageView(new Image(getClass().getResource("/icons/edit.png").toExternalForm()));
                            modifyIcon.setFitWidth(16);
                            modifyIcon.setFitHeight(16);
                            Button modifyButton = new Button("Modifier", modifyIcon);
                            modifyButton.setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-text-fill: #ffffff; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            );
                            modifyButton.setGraphicTextGap(5);
                            modifyButton.setOnAction(event -> {
                                try {
                                    openModifierUser(user.getIdUser());
                                    refreshData();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            modifyButton.setOnMouseEntered(e -> modifyButton.setStyle(
                                    "-fx-background-color: rgba(255, 255, 255, 0.2); " +
                                            "-fx-text-fill: #edc525; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            ));
                            modifyButton.setOnMouseExited(e -> modifyButton.setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-text-fill: #fa6969; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            ));

                            // Delete Button
                            ImageView deleteIcon = new ImageView(new Image(getClass().getResource("/icons/delete.png").toExternalForm()));
                            deleteIcon.setFitWidth(16);
                            deleteIcon.setFitHeight(16);
                            Button deleteButton = new Button("Supprimer", deleteIcon);
                            deleteButton.setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-text-fill: #ff5555; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            );
                            deleteButton.setGraphicTextGap(5);
                            deleteButton.setOnAction(event -> deleteUser(user));
                            deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                                    "-fx-background-color: rgba(255, 85, 85, 0.2); " +
                                            "-fx-text-fill: #ff5555; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            ));
                            deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-text-fill: #ff5555; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            ));

                            // Promote Button
                            ImageView promoteIcon = new ImageView(new Image(getClass().getResource("/icons/promotion.png").toExternalForm()));
                            promoteIcon.setFitWidth(16);
                            promoteIcon.setFitHeight(16);
                            Button promoteButton = new Button("Promu", promoteIcon);
                            promoteButton.setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-text-fill: #55ff55; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            );
                            promoteButton.setGraphicTextGap(5);
                            promoteButton.setOnAction(event -> PromoteUser(user));
                            promoteButton.setOnMouseEntered(e -> promoteButton.setStyle(
                                    "-fx-background-color: rgba(85, 255, 85, 0.2); " +
                                            "-fx-text-fill: #55ff55; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            ));
                            promoteButton.setOnMouseExited(e -> promoteButton.setStyle(
                                    "-fx-background-color: transparent; " +
                                            "-fx-text-fill: #55ff55; " +
                                            "-fx-font-size: 12px; " +
                                            "-fx-padding: 5 10 5 10;"
                            ));

                            // Ban/Unban Button
                            Button banUnbanButton;
                            if (user.isBanned()==true) {
                                ImageView unbanIcon = new ImageView(new Image(getClass().getResource("/icons/unban.png").toExternalForm()));
                                unbanIcon.setFitWidth(16);
                                unbanIcon.setFitHeight(16);
                                banUnbanButton = new Button("Débloquer", unbanIcon);
                                banUnbanButton.setStyle(
                                        "-fx-background-color: transparent; " +
                                                "-fx-text-fill: #55ffff; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-padding: 5 10 5 10;"
                                );
                                banUnbanButton.setOnAction(event -> {
                                    try {
                                        unbanUser(user);
                                        refreshData();
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                banUnbanButton.setOnMouseEntered(e -> banUnbanButton.setStyle(
                                        "-fx-background-color: rgba(85, 255, 255, 0.2); " +
                                                "-fx-text-fill: #55ffff; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-padding: 5 10 5 10;"
                                ));
                                banUnbanButton.setOnMouseExited(e -> banUnbanButton.setStyle(
                                        "-fx-background-color: transparent; " +
                                                "-fx-text-fill: #55ffff; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-padding: 5 10 5 10;"
                                ));
                            } else {
                                ImageView banIcon = new ImageView(new Image(getClass().getResource("/icons/banned.png").toExternalForm()));
                                banIcon.setFitWidth(16);
                                banIcon.setFitHeight(16);
                                banUnbanButton = new Button("Bloquer", banIcon);
                                banUnbanButton.setStyle(
                                        "-fx-background-color: transparent; " +
                                                "-fx-text-fill: #ffaa00; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-padding: 5 10 5 10;"
                                );
                                banUnbanButton.setOnAction(event -> {
                                    try {
                                        banuser(user);
                                        refreshData();
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                banUnbanButton.setOnMouseEntered(e -> banUnbanButton.setStyle(
                                        "-fx-background-color: rgba(255, 170, 0, 0.2); " +
                                                "-fx-text-fill: #ffaa00; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-padding: 5 10 5 10;"
                                ));
                                banUnbanButton.setOnMouseExited(e -> banUnbanButton.setStyle(
                                        "-fx-background-color: transparent; " +
                                                "-fx-text-fill: #ffaa00; " +
                                                "-fx-font-size: 12px; " +
                                                "-fx-padding: 5 10 5 10;"
                                ));
                            }
                            banUnbanButton.setGraphicTextGap(5);

                            buttonsBox.getChildren().addAll(modifyButton, deleteButton, promoteButton, banUnbanButton);

                            HBox.setHgrow(userInfoBox, Priority.ALWAYS);
                            row.getChildren().addAll(profileImageView, userInfoBox, buttonsBox);

                            setGraphic(row);
                        }
                    }
                };
            }
        });
    }

    private void banuser(User user) throws SQLException {
        serviceUser.banUser(user.getIdUser());
    }

    private void unbanUser(User user) throws SQLException {
        serviceUser.unbanUser(user.getIdUser());
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

    private void refreshData() throws SQLException {
        String selectedRole = filterrole.getValue();
        if ("Tout".equals(selectedRole)) {
            loadData();
        } else {
            updateListView(filterByRole(selectedRole));
        }
    }

    @FXML
    private void showReclamationList() {
        try {
            List<User> reclamationUsers = serviceUser.returnReclamation();
            Stage stage = new Stage();
            stage.setTitle("Liste des Réclamations");
            stage.setResizable(false);

            // Main container with gradient background
            AnchorPane root = new AnchorPane();
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1E3A8A, #2563EB);");

            // Card container
            VBox card = new VBox(15);
            card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 20; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0.2, 0, 0);");
            card.setPrefWidth(450);
            card.setPrefHeight(350);

            // Title
            Label title = new Label("Réclamations des Utilisateurs");
            title.setStyle("-fx-font-size: 20px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: #1E3A8A; " +
                    "-fx-padding: 0 0 15 0;");

            // Scrollable content
            ScrollPane scrollPane = new ScrollPane();
            // Correct methods for scroll bar policies
            scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 10;");
            content.setPrefWidth(410); // Set width to fit card minus padding

            if (reclamationUsers.isEmpty()) {
                Label emptyLabel = new Label("Aucune réclamation trouvée");
                emptyLabel.setStyle("-fx-font-size: 16px; " +
                        "-fx-text-fill: #6B7280; " +
                        "-fx-alignment: center;");
                content.getChildren().add(emptyLabel);
            } else {
                for (User user : reclamationUsers) {
                    // Individual reclamation card
                    HBox reclamationBox = new HBox(15);
                    reclamationBox.setStyle("-fx-background-color: #F9FAFB; " +
                            "-fx-background-radius: 10; " +
                            "-fx-padding: 15; " +
                            "-fx-border-color: #E5E7EB; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-width: 1;");

                    // User info
                    Label reclamationLabel = new Label(user.getNom() + " " + user.getPrenom() + ": " + user.getMessagereclamation());
                    reclamationLabel.setStyle("-fx-font-size: 14px; " +
                            "-fx-text-fill: #1F2937; " +
                            "-fx-wrap-text: true;");
                    reclamationLabel.setMaxWidth(300);

                    // Unban button
                    Button unbanButton = new Button("Débloquer");
                    unbanButton.setStyle("-fx-background-color: #10B981; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 12px; " +
                            "-fx-font-weight: bold; " +
                            "-fx-padding: 8 15; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand;");

                    // Button hover effect
                    unbanButton.setOnMouseEntered(e -> unbanButton.setStyle(
                            "-fx-background-color: #059669; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 12px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 8 15; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.2, 0, 0);"
                    ));
                    unbanButton.setOnMouseExited(e -> unbanButton.setStyle(
                            "-fx-background-color: #10B981; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 12px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-padding: 8 15; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-cursor: hand;"
                    ));

                    unbanButton.setOnAction(event -> {
                        try {
                            // Unban and remove reclamation
                            serviceUser.unbanUser(user.getIdUser());

                            // Remove from display
                            content.getChildren().remove(reclamationBox);

                            // Refresh main list
                            refreshData();

                            // If list becomes empty, show empty message
                            if (content.getChildren().isEmpty()) {
                                Label emptyLabel = new Label("Aucune réclamation trouvée");
                                emptyLabel.setStyle("-fx-font-size: 16px; " +
                                        "-fx-text-fill: #6B7280; " +
                                        "-fx-alignment: center;");
                                content.getChildren().add(emptyLabel);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de débloquer l'utilisateur");
                        }
                    });

                    reclamationBox.getChildren().addAll(reclamationLabel, unbanButton);
                    HBox.setHgrow(reclamationLabel, Priority.ALWAYS);
                    content.getChildren().add(reclamationBox);
                }
            }

            // Correct method to set content
            scrollPane.setContent(content);
            card.getChildren().addAll(title, scrollPane);

            // Center the card
            AnchorPane.setTopAnchor(card, 25.0);
            AnchorPane.setBottomAnchor(card, 25.0);
            AnchorPane.setLeftAnchor(card, 25.0);
            AnchorPane.setRightAnchor(card, 25.0);

            root.getChildren().add(card);

            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add("data:text/css," +
                    ".scroll-pane > .viewport {" +
                    "    -fx-background-color: transparent;" +
                    "}" +
                    ".scroll-pane {" +
                    "    -fx-background-color: transparent;" +
                    "}" +
                    ".scroll-bar:vertical {" +
                    "    -fx-pref-width: 10;" +
                    "    -fx-background-color: transparent;" +
                    "}" +
                    ".scroll-bar:vertical .thumb {" +
                    "    -fx-background-color: #3B82F6;" +
                    "    -fx-background-radius: 5;" +
                    "}" +
                    ".scroll-bar:vertical .track {" +
                    "    -fx-background-color: #E5E7EB;" +
                    "    -fx-background-radius: 5;" +
                    "}"
            );

            stage.setScene(scene);
            stage.show();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les réclamations");
        }
    }    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void PromoteUser(User u) {
        Role userRole = u.getRole();
        ArrayList<String> s = new ArrayList<>();

        switch (userRole) {
            case CANDIDAT:
                s = new ArrayList<>(List.of("Manager", "Employe", "RH"));
                break;
            case EMPLOYE:
                s = new ArrayList<>(List.of("RH", "Manager"));
                break;
            case RH:
                s = new ArrayList<>(List.of("Employe", "Manager"));
                break;
            case MANAGER:
                s = new ArrayList<>(List.of("Employe", "RH"));
                break;
            default:
                s = new ArrayList<>();
                break;
        }

        openPromotionWindow(s, u);
    }

    private Map<String, TextField> inputFields = new HashMap<>();
    private ServiceUser userservice = new ServiceUser();

    public void openPromotionWindow(ArrayList<String> roles, User u) {
        Stage stage = new Stage();
        stage.setTitle("Promote User");

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(roles);
        choiceBox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 5px;");

        VBox layout = new VBox(10);
        layout.getChildren().add(choiceBox);

        Button promoteButton = new Button("Promote");
        promoteButton.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 10px 20px;");

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
                        String competence = inputFields.get("Competence").getText();
                        String departement = inputFields.get("Departement").getText();
                        Double salaire = Double.parseDouble(inputFields.get("Salaire").getText());
                        int experienceTravail = Integer.parseInt(inputFields.get("Experience Travail").getText());

                        u.setCompetence(competence);
                        u.setDepartement(departement);
                        u.setSalaire(salaire);
                        u.setExperienceTravail(experienceTravail);
                        userservice.changetoEmploye(u);
                        break;

                    case "RH":
                        int anneesExperience = Integer.parseInt(inputFields.get("Années d'expérience").getText());
                        String specialisation = inputFields.get("Spécialisation").getText();
                        u.setAnsExperience(anneesExperience);
                        u.setSpecialisation(specialisation);
                        userservice.changetoRH(u);
                        break;

                    case "Manager":
                        int nombreProjets = Integer.parseInt(inputFields.get("Nombre de Projets").getText());
                        Double budget = Double.parseDouble(inputFields.get("Budget").getText());
                        String departementGere = inputFields.get("Département Géré").getText();
                        u.setDepartementGere(departementGere);
                        u.setNombreProjet(nombreProjets);
                        u.setBudget(budget);
                        userservice.changetoManager(u);
                        break;

                    default:
                        System.out.println("User promoted to: " + selectedRole);
                        break;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Promotion Successful");
                alert.setHeaderText(null);
                alert.setContentText("Utilisateur promu à: " + selectedRole);
                alert.showAndWait();

                stage.close();
                try {
                    refreshData();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        layout.getChildren().add(promoteButton);
        layout.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 20px; -fx-alignment: center;");

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
            label.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-margin-right: 10px;");

            TextField textField = new TextField();
            textField.setPromptText(fieldName);
            textField.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 5px; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-prompt-text-fill: #999; -fx-focus-color: #007bff;");

            inputFields.put(fieldName, textField);
            nodes.add(label);
            nodes.add(textField);
        }
        return nodes;
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceUser.supprimer(user.getIdUser());
                refreshData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void openModifierUser(int userId) throws SQLException {
        ServiceUser userService = new ServiceUser();
        User user = userService.findbyid(userId);

        if (user == null) {
            System.out.println("Utilisateur avec ID " + userId + " non trouvé.");
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Modifier Utilisateur");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        Label titleLabel = new Label("Modification de l'utilisateur");
        titleLabel.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: #2c3e50; " +
                        "-fx-padding: 0 0 15 0;"
        );
        vbox.getChildren().add(titleLabel);

        String role = user.getRole().name();
        switch (role) {
            case "CANDIDAT":
                ComboBox<Status> statusCombo = createStyledComboBox(Status.values(), user.getStatus());
                HBox statusBox = createLabeledField("Statut", statusCombo);

                TextField salaireField = createStyledTextField(String.valueOf(user.getSalaireAttendu()), "Salaire attendu (en DT)");
                addNumericValidation(salaireField, true);
                HBox salaireBox = createLabeledField("Salaire attendu", salaireField);

                vbox.getChildren().addAll(statusBox, salaireBox);
                vbox.getChildren().add(createSaveButton(user, userService, stage,
                        () -> {
                            user.setStatus(statusCombo.getValue());
                            user.setSalaireAttendu(Double.parseDouble(salaireField.getText()));
                        }, salaireField));
                break;

            case "EMPLOYE":
                TextField competenceField = createStyledTextField(user.getCompetence(), "Compétences");
                HBox competenceBox = createLabeledField("Compétences", competenceField);

                TextField posteField = createStyledTextField(user.getPoste(), "Poste occupé");
                HBox posteBox = createLabeledField("Poste", posteField);

                TextField expField = createStyledTextField(String.valueOf(user.getExperienceTravail()), "Expérience (en années)");
                addNumericValidation(expField, false);
                HBox expBox = createLabeledField("Expérience", expField);

                vbox.getChildren().addAll(competenceBox, posteBox, expBox);
                vbox.getChildren().add(createSaveButton(user, userService, stage,
                        () -> {
                            user.setCompetence(competenceField.getText());
                            user.setPoste(posteField.getText());
                            user.setExperienceTravail(Integer.parseInt(expField.getText()));
                        }, expField));
                break;

            case "MANAGER":
                TextField projetField = createStyledTextField(String.valueOf(user.getNombreProjet()), "Nombre de projets");
                addNumericValidation(projetField, false);
                HBox projetBox = createLabeledField("Projets", projetField);

                TextField deptField = createStyledTextField(user.getDepartementGere(), "Département géré");
                HBox deptBox = createLabeledField("Département", deptField);

                TextField budgetField = createStyledTextField(String.valueOf(user.getBudget()), "Budget (en DT)");
                addNumericValidation(budgetField, true);
                HBox budgetBox = createLabeledField("Budget", budgetField);

                vbox.getChildren().addAll(projetBox, deptBox, budgetBox);
                vbox.getChildren().add(createSaveButton(user, userService, stage,
                        () -> {
                            user.setNombreProjet(Integer.parseInt(projetField.getText()));
                            user.setDepartementGere(deptField.getText());
                            user.setBudget(Double.parseDouble(budgetField.getText()));
                        }, projetField, budgetField));
                break;

            case "RH":
                TextField ansExpField = createStyledTextField(String.valueOf(user.getAnsExperience()), "Années d'expérience");
                addNumericValidation(ansExpField, false);
                HBox ansExpBox = createLabeledField("Expérience", ansExpField);

                TextField specField = createStyledTextField(user.getSpecialisation(), "Spécialisation");
                HBox specBox = createLabeledField("Spécialisation", specField);

                vbox.getChildren().addAll(ansExpBox, specBox);
                vbox.getChildren().add(createSaveButton(user, userService, stage,
                        () -> {
                            user.setAnsExperience(Integer.parseInt(ansExpField.getText()));
                            user.setSpecialisation(specField.getText());
                        }, ansExpField));
                break;

            default:
                vbox.getChildren().add(new Label("Rôle non reconnu: " + role));
                Button closeBtn = createStyledButton("Fermer", "#e74c3c");
                closeBtn.setOnAction(event -> stage.close());
                vbox.getChildren().add(closeBtn);
                break;
        }

        Scene scene = new Scene(vbox, 400, 450);
        scene.getStylesheets().add("data:text/css," +
                "text { -fx-font-family: 'Arial'; }" +
                "button:hover { -fx-cursor: hand; }"
        );
        stage.setScene(scene);
        stage.showAndWait();
    }

    private static <T> ComboBox<T> createStyledComboBox(T[] items, T defaultValue) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);
        comboBox.setValue(defaultValue);
        comboBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 5;"
        );
        return comboBox;
    }

    private static TextField createStyledTextField(String text, String prompt) {
        TextField textField = new TextField(text != null ? text : "");
        textField.setPromptText(prompt);
        textField.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #dcdcdc; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5; " +
                        "-fx-padding: 8; " +
                        "-fx-font-size: 14px;"
        );
        return textField;
    }

    private static HBox createLabeledField(String labelText, Node field) {
        Label label = new Label(labelText + ":");
        label.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #34495e; " +
                        "-fx-font-weight: bold;"
        );
        HBox hbox = new HBox(15, label, field);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }

    private static Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 25; " +
                        "-fx-background-radius: 8; " +
                        "-fx-font-weight: bold;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + darkenColor(color) + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 25; " +
                        "-fx-background-radius: 8; " +
                        "-fx-font-weight: bold;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 25; " +
                        "-fx-background-radius: 8; " +
                        "-fx-font-weight: bold;"
        ));
        return button;
    }

    private static Button createSaveButton(User user, ServiceUser userService, Stage stage,
                                           Runnable updateUser, TextField... fieldsToValidate) {
        Button saveButton = createStyledButton("Sauvegarder", "#3498db");
        saveButton.setOnAction(event -> {
            for (TextField field : fieldsToValidate) {
                if (field.getStyle().contains("red") || field.getText().isEmpty()) {
                    showErrorAlert("Veuillez corriger les champs invalides");
                    return;
                }
            }
            updateUser.run();
            try {
                userService.modifier(user);
                stage.close();
            } catch (SQLException e) {
                showErrorAlert("Erreur lors de la sauvegarde");
            }
        });
        return saveButton;
    }

    private static void addNumericValidation(TextField field, boolean allowDecimal) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String regex = allowDecimal ? "[0-9]*\\.?[0-9]+" : "[0-9]+";
            if (!newVal.matches(regex) && !newVal.isEmpty()) {
                field.setStyle(field.getStyle().replace("-fx-border-color: #dcdcdc;", "-fx-border-color: #e74c3c;"));
                field.setTooltip(new Tooltip("Veuillez entrer un nombre " + (allowDecimal ? "valide" : "entier")));
            } else {
                field.setStyle(field.getStyle().replace("-fx-border-color: #e74c3c;", "-fx-border-color: #dcdcdc;"));
                field.setTooltip(null);
            }
        });
    }

    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-border-color: #e74c3c; " +
                        "-fx-border-radius: 5;"
        );
        alert.showAndWait();
    }

    private static String darkenColor(String color) {
        return color.equals("#3498db") ? "#2980b9" :
                color.equals("#e74c3c") ? "#c0392b" : color;
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