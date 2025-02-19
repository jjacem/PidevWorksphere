package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class ModifierCompteController {
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
User us=new User();
    public void initData(int userId) {
        this.userId = userId;
    }

    @FXML
    public void initialize() throws SQLException {
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);

        ServiceUser serviceUser = new ServiceUser();
        User u = serviceUser.findbyid(userId);

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
            User modifiedUser;
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
