package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ModifierCompteController {
    @FXML
    private Button savebutton;
    @FXML
    private TextField nom, prenom, email, adresse, imageProfil;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu, competence, experienceTravail, nombreProjet, anneeExperience, specialisation;
    @FXML
    private Label salaireLabel, competenceLabel, experienceLabel, projetLabel, anneeExpLabel, specialisationLabel;

    private String role = SessionManager.getRole();
    private int userId;

    public void initData(int userId) {
        this.userId = userId;
    }

    @FXML
    public void initialize() {
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);
        ServiceUser serviceUser = new ServiceUser();

        try {
            User u = serviceUser.findbyid(SessionManager.extractuserfromsession().getIdUser());

            if (u != null) {
                nom.setText(u.getNom());
                prenom.setText(u.getPrenom());
                email.setText(u.getEmail());
                adresse.setText(u.getAdresse());
                sexe.setValue(u.getSexe());
                imageProfil.setText(u.getImageProfil());

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

                positionFieldsAndButton();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load user data.");
        }
    }

    private void positionFieldsAndButton() {
        double startY = 310.0; // Start placing dynamic fields below fixed inputs
        double spacing = 40.0; // Space between fields

        List<Node> fields = Arrays.asList(
                salaireLabel, salaireAttendu,
                competenceLabel, competence,
                experienceLabel, experienceTravail,
                projetLabel, nombreProjet,
                anneeExpLabel, anneeExperience,
                specialisationLabel, specialisation
        );

        // Position only visible fields properly
        for (int i = 0; i < fields.size(); i += 2) {
            Label label = (Label) fields.get(i);
            TextField input = (TextField) fields.get(i + 1);

            if (label.isVisible() && input.isVisible()) {
                label.setLayoutY(startY);
                input.setLayoutY(startY + 20);
                startY += spacing; // Move down for next pair
            }
        }

        // Place the save button after the last visible input field
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

        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                adresse.getText().isEmpty() || sexe.getValue() == null || imageProfil.getText().isEmpty()) {
            showAlert("Input Error", "Please fill in all fields.");
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
            modifiedUser.setImageProfil(imageProfil.getText());

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

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
