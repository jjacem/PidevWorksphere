package esprit.tn.controllers;

import esprit.tn.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceEquipe;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    @FXML
    private ImageView imagePreview;

    private String imagePath = ""; // Pour stocker le chemin de l'image

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

    /*@FXML
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomEquipeField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Liste des équipes");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }*/

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
        if (imagePath.isEmpty()) {
            imagePath = "images/profil.png"; // Chemin vers une image par défaut
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

            // Créer une nouvelle équipe avec l'image
            Equipe equipe = new Equipe(0, nomEquipe, employesSelectionnes, imagePath);
            serviceEquipe.ajouterEquipe(equipe);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Équipe ajoutée avec succès !");
            applyAlertStyle(alert);
            alert.showAndWait();
            // Rediriger vers la vue des équipes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEquipe.fxml"));
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

    @FXML
    private void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Définir le répertoire de destination (htdocs/images/)
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Générer un nom de fichier unique
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);

                // Copier le fichier vers la destination
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Stocker le chemin relatif dans la variable
                imagePath = "img/" + fileName;

                // Afficher l'image dans l'ImageView
                imagePreview.setImage(new Image(destinationFile.toURI().toString()));

            } catch (Exception e) {
                System.out.println("erreur lors de l'ajout de la photo");
            }
        }
    }
}