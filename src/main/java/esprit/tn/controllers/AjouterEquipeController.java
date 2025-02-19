package esprit.tn.controllers;

import esprit.tn.entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceEquipe;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AjouterEquipeController {

    @FXML
    private TextField nomEquipeField;

    @FXML
    private ListView<User> employesListView;

    @FXML
    private ListView<User> employesSelectionnesListView;

    @FXML
    private Button ajouterUnButton;

    @FXML
    private Button ajouterTousButton;

    @FXML
    private Button clearAllButton;

    @FXML
    private Button confirmerButton;

    @FXML
    private Button annulerButton;

    private ServiceEquipe serviceEquipe;

    private ObservableList<User> employesList;
    private ObservableList<User> employesSelectionnesList;

    public AjouterEquipeController() {
        serviceEquipe = new ServiceEquipe();
    }

    @FXML
    public void initialize() {
        try {
            // Charger les employés disponibles
            List<User> employes = serviceEquipe.getEmployesDisponibles();
            employesList = FXCollections.observableArrayList(employes);
            employesListView.setItems(employesList);

            // Activer la sélection multiple
            employesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Personnaliser l'affichage des employés dans la ListView
            employesListView.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getPrenom() + " " + user.getNom());
                    }
                }
            });

            // Initialiser la liste des employés sélectionnés
            employesSelectionnesList = FXCollections.observableArrayList();
            employesSelectionnesListView.setItems(employesSelectionnesList);

            // Personnaliser l'affichage des employés sélectionnés aussi
            employesSelectionnesListView.setCellFactory(param -> new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                    } else {
                        setText(user.getNom() + " " + user.getPrenom());
                    }
                }
            });


            confirmerButton.setDisable(true);

            employesSelectionnesList.addListener((ListChangeListener.Change<? extends User> change) -> {
                confirmerButton.setDisable(employesSelectionnesList.size() < 2);
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouterUnEmploye() {
        User employeSelectionne = employesListView.getSelectionModel().getSelectedItem();
        if (employeSelectionne != null && !employesSelectionnesList.contains(employeSelectionne)) {
            employesSelectionnesList.add(employeSelectionne);
        }
    }

    @FXML
    public void ajouterTousEmployes() {
        employesSelectionnesList.setAll(employesList);
    }

    @FXML
    public void clearAll() {
        employesSelectionnesList.clear();
    }

    @FXML
    public void confirmer() {
        String nomEquipe = nomEquipeField.getText();
        List<User> employesSelectionnes = new ArrayList<>(employesSelectionnesList);

        if (nomEquipe.isEmpty() || employesSelectionnes.size() < 2) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champ manquant");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner au moins deux employés.");
            applyAlertStyle(alert);
            alert.showAndWait();
            return;
        }

        try {
            if (serviceEquipe.nomEquipeExiste(nomEquipe)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Équipe existante");
                alert.setHeaderText(null);
                alert.setContentText("Une équipe avec ce nom existe déjà.");
                applyAlertStyle(alert);
                alert.showAndWait();
                return;
            }

            serviceEquipe.ajouterEquipe(new Equipe(0, nomEquipe, employesSelectionnes));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Équipe ajoutée avec succès !");
            applyAlertStyle(alert);
            alert.showAndWait();

            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardManager.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomEquipeField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Liste des équipes");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void annuler() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annuler la création ?");
        confirmation.setContentText("Voulez-vous vraiment annuler la création de l'équipe ?");
        applyAlertStyle(confirmation);
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                //FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardManager.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) annulerButton.getScene().getWindow();
                stage.getScene().setRoot(root);
                stage.setTitle("Liste des équipes");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void applyAlertStyle(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/alert-styles.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}