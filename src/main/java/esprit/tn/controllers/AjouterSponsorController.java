/*package esprit.tn.controllers;
import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceSponsor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AjouterSponsorController {
    @FXML
    private TextField nomtextfield;
    @FXML
    private TextField prenomtextfield;
    @FXML
    private TextField emailtextfield;
    @FXML
    private TextField budgettextfield;

    @FXML
    public void AjouterSponsor(ActionEvent actionEvent) {
        ServiceSponsor serviceSponsor = new ServiceSponsor();
        Sponsor sponsor = new Sponsor(nomtextfield.getText(),prenomtextfield.getText(),emailtextfield.getText(),Double.parseDouble(budgettextfield.getText()));
        try {
            serviceSponsor.ajouter(sponsor);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("sponsor ajoute");
            alert.showAndWait();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
*/package esprit.tn.controllers;
import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceSponsor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AjouterSponsorController {
    @FXML
    private TextField nomtextfield;
    @FXML
    private TextField prenomtextfield;
    @FXML
    private TextField emailtextfield;
    @FXML
    private TextField budgettextfield;

    @FXML
    private Label emailErrorLabel; // Label pour l'email
    @FXML
    private Label budgetErrorLabel; // Label pour le budget

    @FXML
    public void AjouterSponsor(ActionEvent actionEvent) {
        // Réinitialiser les messages d'erreur
        emailErrorLabel.setText("");
        budgetErrorLabel.setText("");

        String email = emailtextfield.getText();
        String budgetText = budgettextfield.getText();

        // Validation de l'email
        if (!isValidEmail(email)) {
            emailErrorLabel.setText("L'email doit être au format valide.");
            return;
        }

        double budget = 0;
        try {
            budget = Double.parseDouble(budgetText);
        } catch (NumberFormatException e) {
            budgetErrorLabel.setText("Le budget doit être un nombre valide.");
            return;
        }

        // Si tout est valide, procéder à l'ajout
        ServiceSponsor serviceSponsor = new ServiceSponsor();
        Sponsor sponsor = new Sponsor(nomtextfield.getText(), prenomtextfield.getText(), email, budget);

        try {
            serviceSponsor.ajouter(sponsor);
            emailErrorLabel.setText(""); // Réinitialiser après succès
            budgetErrorLabel.setText(""); // Réinitialiser après succès
            // Vous pouvez ajouter un label de succès ici si vous le souhaitez.
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // Vous pouvez également afficher un message d'erreur ici si nécessaire.
        }
    }

    // Méthode pour valider l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}
