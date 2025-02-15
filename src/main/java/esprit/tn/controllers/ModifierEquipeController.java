package esprit.tn.controllers;

import esprit.tn.entities.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import esprit.tn.services.ServiceEquipe;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModifierEquipeController {

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

    @FXML
    private Button supprimerUnButton;

    private ServiceEquipe serviceEquipe;
    private Equipe equipeAModifier;

    private ObservableList<User> employesList;
    private ObservableList<User> employesSelectionnesList;

    public ModifierEquipeController() {
        serviceEquipe = new ServiceEquipe();
    }

    public void setEquipeAModifier(Equipe equipe) {
        this.equipeAModifier = equipe;
        nomEquipeField.setText(equipe.getNomEquipe());
        employesSelectionnesList.setAll(equipe.getEmployes());
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

            // Désactiver le bouton "Confirmer" par défaut
            confirmerButton.setDisable(true);

            // Activer "Confirmer" seulement si au moins 2 employés sont sélectionnés
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
            alert.setTitle("Champ(s) manquant(s)");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner au moins deux employés.");
            alert.showAndWait();
            return;
        }

        try {
            // Mettre à jour l'équipe
            equipeAModifier.setNomEquipe(nomEquipe);
            equipeAModifier.setEmployes(employesSelectionnes);
            serviceEquipe.modifierEquipe(equipeAModifier);

            // Affichage de la confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Équipe modifiée avec succès !");
            alert.showAndWait();

            // Rediriger vers la page AfficherEquipe.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
            Parent root = loader.load();

            // Fermer la fenêtre actuelle
            Stage stage = (Stage) nomEquipeField.getScene().getWindow();
            stage.close();

            // Ouvrir la nouvelle fenêtre
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Liste des équipes");
            newStage.show();

        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la modification");
            alert.setContentText("Une erreur s'est produite lors de la modification de l'équipe.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void annuler() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annuler la modification ?");
        confirmation.setContentText("Voulez-vous vraiment annuler la modification de l'équipe ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Réinitialiser l'interface
            nomEquipeField.clear();
            employesSelectionnesList.clear();
            confirmerButton.setDisable(true);
        }
    }

    @FXML
    public void supprimerUnEmploye() {
        // Récupérer l'employé sélectionné dans la liste des employés sélectionnés
        User employeSelectionne = employesSelectionnesListView.getSelectionModel().getSelectedItem();

        if (employeSelectionne != null) {
            // Supprimer l'employé de la liste des employés sélectionnés
            employesSelectionnesList.remove(employeSelectionne);
        } else {
            // Afficher une alerte si aucun employé n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé à supprimer.");
            alert.showAndWait();
        }
    }
}