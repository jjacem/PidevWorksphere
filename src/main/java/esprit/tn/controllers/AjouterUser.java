package esprit.tn.controllers;

import esprit.tn.entities.Sexe;
import esprit.tn.entities.User;
import esprit.tn.services.ServiceUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AjouterUser {
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

    @FXML
    public void initialize() {
        sexe.getItems().addAll(Sexe.HOMME, Sexe.FEMME);
    }

    @FXML
    public void ajoutercandidat(ActionEvent actionEvent) {
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
            double salaire = Double.parseDouble(salaireAttendu.getText()); // Ensure valid double input
            User candidat = new User(nom.getText(), prenom.getText(), email.getText(),
                    mdp.getText(), adresse.getText(), sexe.getValue(),
                    ImageProfil.getText(), salaire);

            serviceUser.ajouter(candidat);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("User added successfully!");
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
            alert.setContentText("Failed to add user: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
