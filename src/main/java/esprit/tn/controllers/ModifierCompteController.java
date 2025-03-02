package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ModifierCompteController {
    @FXML
    private Button savebutton;
    @FXML
    private TextField nom, prenom, email, adresse;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu, competence, experienceTravail, nombreProjet, anneeExperience, specialisation;
    @FXML
    private Label salaireLabel, competenceLabel, experienceLabel, projetLabel, anneeExpLabel, specialisationLabel;
    @FXML
    private ImageView imagePreview;

    private String imagePath = "";

    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Pattern numericPattern = Pattern.compile("\\d+(\\.\\d+)?");

    private String role = SessionManager.getRole();
    private int userId;

    public void initData(int userId) {
        this.userId = userId;
    }

    private boolean b=false;

    public void modparadmin(Boolean k){

        this.b=k;

    }
    @FXML

    public void initialize() {
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);
        ServiceUser serviceUser = new ServiceUser();

        try {
            // Admin flag logic
            if (b) {
                // Admin is modifying, so hide certain fields
                User u = serviceUser.findbyid(userId);
                nom.setVisible(false);
                prenom.setVisible(false);
                email.setVisible(false);
                adresse.setVisible(false);
                sexe.setVisible(false);
                imagePreview.setVisible(false);

                this.role = u.getRole().name();

                switch (role) {
                    case "CANDIDAT":
                        salaireAttendu.setText(String.valueOf(u.getSalaireAttendu()));
                        showFields(salaireAttendu, salaireLabel);
                        break;
                    case "EMPLOYE":
                        competence.setText(u.getCompetence());
                        experienceTravail.setText(String.valueOf(u.getExperienceTravail()));
                        showFields(competence, competenceLabel, experienceTravail, experienceLabel);
                        break;
                    case "MANAGER":
                        nombreProjet.setText(String.valueOf(u.getNombreProjet()));
                        showFields(nombreProjet, projetLabel);
                        break;
                    case "RH":
                        anneeExperience.setText(String.valueOf(u.getAnsExperience()));
                        specialisation.setText(u.getSpecialisation());
                        showFields(anneeExperience, anneeExpLabel, specialisation, specialisationLabel);
                        break;
                }
            } else {
                // Regular user is modifying, show all fields
                User u = serviceUser.findbyid(SessionManager.extractuserfromsession().getIdUser());
                if (u != null) {
                    nom.setText(u.getNom());
                    prenom.setText(u.getPrenom());
                    email.setText(u.getEmail());
                    adresse.setText(u.getAdresse());
                    sexe.setValue(u.getSexe());
                    imagePath = u.getImageProfil();
                    imagePreview.setImage(new Image(new File(imagePath).toURI().toString()));

                    hideAllFields(); // Hide all initially

                    switch (role) {
                        case "CANDIDAT":
                            salaireAttendu.setText(String.valueOf(u.getSalaireAttendu()));
                            showFields(salaireAttendu, salaireLabel);
                            break;
                        case "EMPLOYE":
                            competence.setText(u.getCompetence());
                            experienceTravail.setText(String.valueOf(u.getExperienceTravail()));
                            showFields(competence, competenceLabel, experienceTravail, experienceLabel);
                            break;
                        case "MANAGER":
                            nombreProjet.setText(String.valueOf(u.getNombreProjet()));
                            showFields(nombreProjet, projetLabel);
                            break;
                        case "RH":
                            anneeExperience.setText(String.valueOf(u.getAnsExperience()));
                            specialisation.setText(u.getSpecialisation());
                            showFields(anneeExperience, anneeExpLabel, specialisation, specialisationLabel);
                            break;
                    }
                }
                positionFieldsAndButton();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user data.");
        }
    }

    private void positionFieldsAndButton() {
        double startY = 310.0;
        double spacing = 40.0;

        List<Node> fields = Arrays.asList(
                salaireLabel, salaireAttendu,
                competenceLabel, competence,
                experienceLabel, experienceTravail,
                projetLabel, nombreProjet,
                anneeExpLabel, anneeExperience,
                specialisationLabel, specialisation
        );

        for (int i = 0; i < fields.size(); i += 2) {
            Label label = (Label) fields.get(i);
            TextField input = (TextField) fields.get(i + 1);

            if (label.isVisible() && input.isVisible()) {
                label.setLayoutY(startY);
                input.setLayoutY(startY + 20);
                startY += spacing;
            }
        }

        savebutton.setLayoutY(startY + 20);
    }

    private void hideAllFields() {
        List<Node> allFields = Arrays.asList(
                salaireAttendu, salaireLabel,
                competence, competenceLabel,
                experienceTravail, experienceLabel,
                nombreProjet, projetLabel,
                anneeExperience, anneeExpLabel,
                specialisation, specialisationLabel
        );

        for (Node node : allFields) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    private void showFields(Node... nodes) {
        for (Node node : nodes) {
            node.setVisible(true);
            node.setManaged(true);
        }
    }

    @FXML
    public void saveChanges() {
        ServiceUser serviceUser = new ServiceUser();

        if (!validateInputs()) {
            return;
        }

        try {
            User modifiedUser = SessionManager.extractuserfromsession();
            modifiedUser.setIdUser(SessionManager.extractuserfromsession().getIdUser());
            modifiedUser.setNom(nom.getText());
            modifiedUser.setPrenom(prenom.getText());
            modifiedUser.setEmail(email.getText());
            modifiedUser.setAdresse(adresse.getText());
            modifiedUser.setSexe(sexe.getValue());
            modifiedUser.setImageProfil(imagePath);

            switch (role) {
                case "CANDIDAT":
                    modifiedUser.setSalaireAttendu(Double.parseDouble(salaireAttendu.getText()));
                    break;
                case "EMPLOYE":
                    modifiedUser.setCompetence(competence.getText());
                    modifiedUser.setExperienceTravail(Integer.parseInt(experienceTravail.getText()));
                    break;
                case "MANAGER":
                    modifiedUser.setNombreProjet(Integer.parseInt(nombreProjet.getText()));
                    break;
                case "RH":
                    modifiedUser.setAnsExperience(Integer.parseInt(anneeExperience.getText()));
                    modifiedUser.setSpecialisation(specialisation.getText());
                    break;
                default:
                    showAlert("Error", "Invalid role.");
                    return;
            }

            serviceUser.modifier(modifiedUser);
            showAlert("Success", "Account updated successfully!");

            // Refresh window after saving
            refreshWindow();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account.");
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Define upload directory (htdocs/images/)
                File uploadDir = new File("C:\\xampp\\htdocs\\img");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Generate unique filename
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(uploadDir, fileName);

                // Copy file to the destination
                Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Store relative path in variable
                imagePath = "htdocs/images/" + fileName;

                // Display image in ImageView
                imagePreview.setImage(new Image(destinationFile.toURI().toString()));

            } catch (Exception e) {
                showAlert("File Error", "Failed to upload image.");
            }
        }
    }

    private void refreshWindow() {
        try {
            Stage stage = (Stage) savebutton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/ModifierCompte.fxml"));
            Scene newScene = new Scene(loader.load());

            stage.setScene(newScene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to refresh the window.");
        }
    }

    private boolean validateInputs() {
        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                adresse.getText().isEmpty() || sexe.getValue() == null || imagePath.isEmpty()) {
            showAlert("Input Error", "Please fill in all required fields.");
            return false;
        }

        if (!emailPattern.matcher(email.getText()).matches()) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}