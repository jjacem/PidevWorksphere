package esprit.tn.controllers;

import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceSponsor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

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
    private Label emailErrorLabel;
    @FXML
    private Label budgetErrorLabel;

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
            emailtextfield.setStyle("-fx-border-color: red;");
            return;
        } else {
            emailtextfield.setStyle("");
        }

        double budget = 0;
        try {
            budget = Double.parseDouble(budgetText);
            if (budget < 0) {
                budgetErrorLabel.setText("Le budget doit être un nombre positif.");
                budgettextfield.setStyle("-fx-border-color: red;");
                return;
            } else {
                budgettextfield.setStyle("");
            }
        } catch (NumberFormatException e) {
            budgetErrorLabel.setText("Le budget doit être un nombre valide.");
            budgettextfield.setStyle("-fx-border-color: red;");
            return;
        }

        // Si tout est valide, procéder à l'ajout
        ServiceSponsor serviceSponsor = new ServiceSponsor();
        Sponsor sponsor = new Sponsor(nomtextfield.getText(), prenomtextfield.getText(), email, budget);

        try {
            serviceSponsor.ajouter(sponsor);
            emailErrorLabel.setText("");
            budgetErrorLabel.setText("");

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setContentText("Sponsor ajouté avec succès !");
            alert.showAndWait();

            // Fermer la fenêtre après succès
            ((Stage) nomtextfield.getScene().getWindow()).close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // Afficher un message d'erreur si nécessaire
        }
    }

    @FXML
    public void Annuler(ActionEvent actionEvent) {
        // Fermer la fenêtre sans ajouter de sponsor
        ((Stage) nomtextfield.getScene().getWindow()).close();
    }

    @FXML
    public void Reinitialiser(ActionEvent actionEvent) {
        // Réinitialiser tous les champs
        nomtextfield.setText("");
        prenomtextfield.setText("");
        emailtextfield.setText("");
        budgettextfield.setText("");
        emailErrorLabel.setText("");
        budgetErrorLabel.setText("");
        emailtextfield.setStyle("");
        budgettextfield.setStyle("");
    }

    // Méthode pour valider l'email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}