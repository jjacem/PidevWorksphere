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
    @FXML
    private ImageView imagePreview;

    @FXML
    private Button uploadImageButton;

    private String imagePath = "";
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

        if (equipe.getImageEquipe() != null && !equipe.getImageEquipe().isEmpty()) {
            String correctPath = "C:/xampp/htdocs/img/" + new File(equipe.getImageEquipe()).getName();
            File imageFile = new File(correctPath);
            if (imageFile.exists() && imageFile.isFile()) {
                imagePreview.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
            }
            imagePath = equipe.getImageEquipe(); // Conserver le chemin de l'image
        } else {
            imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
        }
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
            alert.setTitle("Champ(s) manquant(s)");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs et sélectionner au moins deux employés.");
            applyAlertStyle(alert);
            alert.showAndWait();

            return;
        }

        try {
            if (serviceEquipe.cntrlModifEquipe(nomEquipe, equipeAModifier.getId())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Nom d'équipe existant");
                alert.setHeaderText(null);
                alert.setContentText("Une équipe avec ce nom existe déjà. Veuillez choisir un autre nom.");
                applyAlertStyle(alert);
                alert.showAndWait();

                return;
            }

            equipeAModifier.setNomEquipe(nomEquipe);
            equipeAModifier.setEmployes(employesSelectionnes);
            equipeAModifier.setImageEquipe(imagePath);
            serviceEquipe.modifierEquipe(equipeAModifier);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Équipe modifiée avec succès !");
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
        confirmation.setHeaderText("Annuler la modification ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler la modification de l'équipe ?");
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

    @FXML
    public void supprimerUnEmploye() {
        // Récupérer l'employé sélectionné dans la liste des employés sélectionnés
        User employeSelectionne = employesSelectionnesListView.getSelectionModel().getSelectedItem();

        if (employeSelectionne != null) {
            // Supprimer l'employé de la liste des employés sélectionnés
            employesSelectionnesList.remove(employeSelectionne);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un employé à supprimer");
            applyAlertStyle(alert);
            alert.showAndWait();
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
                // Définir le répertoire de destination (htdocs/img/)
                File uploadDir = new File("C:/xampp/htdocs/img");
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
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Erreur de fichier");
                alert.setHeaderText(null);
                alert.setContentText("Échec de l'upload de l'image");
                applyAlertStyle(alert);
                alert.showAndWait();
            }
        }
    }
}