
package esprit.tn.controllers;

import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceSponsor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifierSponsorController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtBudget;
    @FXML
    private Button btnModifier;
    @FXML
    private TextField txtSecteur;

    private Sponsor sponsor;
    private ServiceSponsor serviceSponsor = new ServiceSponsor();

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        txtNom.setText(sponsor.getNomSponso());
        txtPrenom.setText(sponsor.getPrenomSponso());
        txtEmail.setText(sponsor.getEmailSponso());
        txtBudget.setText(String.valueOf(sponsor.getBudgetSponso()));
        txtSecteur.setText(sponsor.getSecteurSponsor());
    }

    @FXML
    private void modifierSponsor() {
        // Vérification des champs vides
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || txtBudget.getText().isEmpty() ||
                txtSecteur.getText().isEmpty()) {

            showAlert("Erreur", "Tous les champs sont obligatoires.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du format de l'email
        if (!isValidEmail(txtEmail.getText())) {
            showAlert("Erreur", "Veuillez entrer un email valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du format du budget
        double budget;
        try {
            budget = Double.parseDouble(txtBudget.getText());
        } catch (NumberFormatException ex) {
            showAlert("Erreur", "Le budget doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        // Mise à jour de l'objet sponsor
        sponsor.setNomSponso(txtNom.getText());
        sponsor.setPrenomSponso(txtPrenom.getText());
        sponsor.setEmailSponso(txtEmail.getText());
        sponsor.setBudgetSponso(budget);
        sponsor.setSecteurSponsor(txtSecteur.getText());

        // Sauvegarde dans la base de données
        try {
            serviceSponsor.modifier(sponsor);
            showAlert("Succès", "Sponsor modifié avec succès.", Alert.AlertType.INFORMATION);
            ((Stage) btnModifier.getScene().getWindow()).close();
        } catch (SQLException ex) {
            showAlert("Erreur", "Erreur lors de la modification : " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour vérifier si l'email est valide
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
