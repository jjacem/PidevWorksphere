package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
<<<<<<< Updated upstream
=======
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
>>>>>>> Stashed changes

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class ModifierCompteController {
    @FXML
<<<<<<< Updated upstream
    private TextField nom, prenom, email, adresse, imageProfil;
=======
    private Button savebutton;
    @FXML
    private TextField nom, prenom, email, adresse;
>>>>>>> Stashed changes
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu, competence, experienceTravail, nombreProjet, anneeExperience, specialisation;
    @FXML
    private Label salaireLabel, competenceLabel, experienceLabel, projetLabel, anneeExpLabel, specialisationLabel;
<<<<<<< Updated upstream
=======
    @FXML
    private ImageView imagePreview;

    private String imagePath = "";

    private final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private final Pattern numericPattern = Pattern.compile("\\d+(\\.\\d+)?");

>>>>>>> Stashed changes
    private String role = SessionManager.getRole();
    private int userId;
User us=new User();
    public void initData(int userId) {
        this.userId = userId;
    }

    private boolean b=false;

    public void modparadmin(Boolean k){

        this.b=k;

    }
    @FXML
<<<<<<< Updated upstream
    public void initialize() throws SQLException {
=======

    public void initialize() {
>>>>>>> Stashed changes
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);

        ServiceUser serviceUser = new ServiceUser();
        User u = serviceUser.findbyid(userId);

<<<<<<< Updated upstream
        if (u != null) {
            nom.setText(u.getNom());
            prenom.setText(u.getPrenom());
            email.setText(u.getEmail());
            adresse.setText(u.getAdresse());
            sexe.setValue(u.getSexe());
            imageProfil.setText(u.getImageProfil());

            switch (role) {
                case "CANDIDAT":
                    salaireAttendu.setText(String.valueOf(u.getSalaireAttendu()));
                    salaireAttendu.setVisible(true);
                    salaireLabel.setVisible(true);
                  String stuts=  u.getStatus().name(); //can't be edited
                    break;
                case "EMPLOYE":
                    competence.setVisible(true);
                    experienceTravail.setVisible(true);
                    competenceLabel.setVisible(true);
                    experienceLabel.setVisible(true);
                    String poste = u.getPoste();
                    Double salaire=u.getSalaire();
                    String competence=u.getCompetence();


                break;
                case "MANAGER":
                    nombreProjet.setVisible(true);
                    projetLabel.setVisible(true);
                    double budget=u.getBudget();
                    String dep=u.getDepartementGere();

                    break;
                case "RH":
                    anneeExperience.setVisible(true);
                    specialisation.setVisible(true);
                    anneeExpLabel.setVisible(true);
                    specialisationLabel.setVisible(true);
                    break;
            }
            System.out.println("role et"+role);
=======
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
>>>>>>> Stashed changes
        }
    }

    @FXML
    public void saveChanges() {
        ServiceUser serviceUser = new ServiceUser();

        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                adresse.getText().isEmpty() || sexe.getValue() == null || imageProfil.getText().isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        try {
<<<<<<< Updated upstream
            User modifiedUser;
=======
            User modifiedUser = SessionManager.extractuserfromsession();
            modifiedUser.setIdUser(SessionManager.extractuserfromsession().getIdUser());
            modifiedUser.setNom(nom.getText());
            modifiedUser.setPrenom(prenom.getText());
            modifiedUser.setEmail(email.getText());
            modifiedUser.setAdresse(adresse.getText());
            modifiedUser.setSexe(sexe.getValue());
            modifiedUser.setImageProfil(imagePath);

>>>>>>> Stashed changes
            switch (role) {
                case "CANDIDAT":
                    double salaire = Double.parseDouble(salaireAttendu.getText());
                    modifiedUser = new User(nom.getText(), prenom.getText(), email.getText(), "11",
                            adresse.getText(), sexe.getValue(), imageProfil.getText(), salaire);
                    break;
                case "EMPLOYE":
                    modifiedUser = new User(nom.getText(), prenom.getText(), email.getText(), "11",
                            adresse.getText(), sexe.getValue(), imageProfil.getText(), "", 0.0,
                            Integer.parseInt(experienceTravail.getText()), "", competence.getText());
                    break;
                case "MANAGER":
                    modifiedUser = new User(nom.getText(), prenom.getText(), email.getText(), "11",
                            adresse.getText(), sexe.getValue(), imageProfil.getText(), "",
                            Integer.parseInt(nombreProjet.getText()), 0.0);
                    break;
                case "RH":
                    modifiedUser = new User(nom.getText(), prenom.getText(), email.getText(), "11",
                            adresse.getText(), sexe.getValue(), imageProfil.getText(),
                            Integer.parseInt(anneeExperience.getText()), specialisation.getText());
                    break;
                default:
                    showAlert("Error", "Invalid role.");
                    return;
            }

            modifiedUser.setIdUser(SessionManager.extractuserfromsession().getIdUser());
            serviceUser.modifier(modifiedUser);

            showAlert("Success", "Account updated successfully!");
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid number format.");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account.");
        }
    }

<<<<<<< Updated upstream
=======
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

>>>>>>> Stashed changes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}