package esprit.tn.controllers;

import esprit.tn.entities.*;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceEquipe;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
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

    private String imagePath = "";

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
                private final ImageView imageView = new ImageView();
                private final Circle clip = new Circle(20, 20, 20); // Ajustez la taille du cercle selon vos besoins

                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Charger l'image de profil

                        if (user.getImageProfil() != null && !user.getImageProfil().trim().isEmpty()) {
                            String correctPath = "C:/xampp/htdocs/img/" + new File(user.getImageProfil()).getName();
                            System.out.println(correctPath); // Debug : afficher le chemin
                            File imageFile = new File(correctPath);
                            if (imageFile.exists() && imageFile.isFile()) {
                                imageView.setImage(new Image(imageFile.toURI().toString()));
                            } else {
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                            }
                        } else {
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                        }


                        imageView.setFitWidth(40);
                        imageView.setFitHeight(40);
                        imageView.setPreserveRatio(true);

                        // Afficher le nom et l'image
                        HBox hbox = new HBox(10); // Espacement entre l'image et le texte
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(imageView, new Label(user.getPrenom() + " " + user.getNom()));
                        setGraphic(hbox);
                    }
                }
            });

            // Initialiser la liste des employés sélectionnés
            employesSelectionnesList = FXCollections.observableArrayList();
            employesSelectionnesListView.setItems(employesSelectionnesList);

            // Personnaliser l'affichage des employés sélectionnés aussi

            employesSelectionnesListView.setCellFactory(param -> new ListCell<User>() {
                private final ImageView imageView = new ImageView();
                private final Circle clip = new Circle(20, 20, 20); // Ajustez la taille du cercle selon vos besoins

                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    if (empty || user == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Charger l'image de profil
                        if (user.getImageProfil() != null && !user.getImageProfil().trim().isEmpty()) {
                            String correctPath = "C:/xampp/htdocs/img/" + new File(user.getImageProfil()).getName();
                            System.out.println(correctPath); // Debug : afficher le chemin
                            File imageFile = new File(correctPath);
                            if (imageFile.exists() && imageFile.isFile()) {
                                imageView.setImage(new Image(imageFile.toURI().toString()));
                            } else {
                                System.out.println("Image file not found or invalid path: " + imageFile.getAbsolutePath());
                                imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                            }
                        } else {
                            System.out.println("No image path provided.");
                            imageView.setImage(new Image(getClass().getResourceAsStream("/images/profil.png")));
                        }
                        imageView.setFitWidth(40);
                        imageView.setFitHeight(40);
                        imageView.setPreserveRatio(true);

                        // Afficher le nom et l'image
                        HBox hbox = new HBox(10); // Espacement entre l'image et le texte
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(imageView, new Label(user.getPrenom() + " " + user.getNom()));
                        setGraphic(hbox);
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
    public void supprimerUnEmploye() {
        User employeSelectionne = employesSelectionnesListView.getSelectionModel().getSelectedItem();

        if (employeSelectionne != null) {
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


    @FXML
    public void confirmer() {
        String nomEquipe = nomEquipeField.getText();
        List<User> employesSelectionnes = new ArrayList<>(employesSelectionnesList);

        // Validation
        if (nomEquipe.isEmpty() || employesSelectionnes.size() < 2 || imagePath.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs et sélectionner au moins deux employés.");
            return;
        }

        try {
            // Vérifier le nom de l'équipe
            if (serviceEquipe.nomEquipeExiste(nomEquipe)) {
                showAlert("Équipe existante", "Une équipe avec ce nom existe déjà.");
                return;
            }

            // Vérifier l'utilisateur courant
            User currentUser = SessionManager.extractuserfromsession();
            if (currentUser == null || currentUser.getIdUser() <= 0) {
                showAlert("Erreur de session", "Aucun utilisateur valide en session.");
                return;
            }

            // Créer l'équipe
            Equipe equipe = new Equipe();
            equipe.setNomEquipe(nomEquipe);
            equipe.setEmployes(employesSelectionnes);
            equipe.setImageEquipe(imagePath.isEmpty() ? "images/profil.png" : imagePath);
            equipe.setId_user(currentUser.getIdUser());

            // Ajouter l'équipe
            serviceEquipe.ajouterEquipe(equipe);

            showAlert("Succès", "Équipe ajoutée avec succès !");
            closeWindow();
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyAlertStyle(alert);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmerButton.getScene().getWindow();
        stage.close();
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

    @FXML
    //private void uploadImage(ActionEvent event) {
    private void uploadImage(MouseEvent event){
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

