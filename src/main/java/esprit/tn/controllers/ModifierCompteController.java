package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import esprit.tn.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class ModifierCompteController {
    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private TextField email;
    @FXML
    private TextField mdp;
    @FXML
    private TextField adresse;
    @FXML
    private ChoiceBox<Sexe> sexe;
    @FXML
    private TextField salaireAttendu;
    @FXML
    private TextField ImageProfil;
    private int userId;
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
            salaireAttendu.setText(String.valueOf(u.getSalaireAttendu()));
            ImageProfil.setText(u.getImageProfil());
        }
    }

    @FXML
    public void saveChanges(ActionEvent actionEvent) {
        ServiceUser serviceUser = new ServiceUser();

        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || email.getText().isEmpty() ||
                mdp.getText().isEmpty() || adresse.getText().isEmpty() || sexe.getValue() == null ||
                salaireAttendu.getText().isEmpty() || ImageProfil.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        try {
            double salaire = Double.parseDouble(salaireAttendu.getText());
            User modifiedUser = new User(nom.getText(), prenom.getText(), email.getText(),
                    mdp.getText(), adresse.getText(), sexe.getValue(),
                    ImageProfil.getText(), salaire);

            modifiedUser.setIdUser(SessionManager.extractuserfromsession().getIdUser());
            serviceUser.modifier(modifiedUser);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Account updated successfully!");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setContentText("Invalid salary value. Please enter a valid number.");
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setContentText("Failed to update account: " + e.getMessage());
            alert.showAndWait();
        }
    }


}
