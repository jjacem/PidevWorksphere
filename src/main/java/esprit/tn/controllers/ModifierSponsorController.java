/*package esprit.tn.controllers;

import esprit.tn.entities.Sponsor;
import esprit.tn.services.ServiceSponsor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.SQLException;


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

    private Sponsor sponsor;
    private ServiceSponsor serviceSponsor = new ServiceSponsor();

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        txtNom.setText(sponsor.getNomSponso());
        txtPrenom.setText(sponsor.getPrenomSponso());
        txtEmail.setText(sponsor.getEmailSponso());
        txtBudget.setText(String.valueOf(sponsor.getBudgetSponso()));
    }

    @FXML
    private void modifierSponsor() {
        try {
            sponsor.setNomSponso(txtNom.getText());
            sponsor.setPrenomSponso(txtPrenom.getText());
            sponsor.setEmailSponso(txtEmail.getText());
            sponsor.setBudgetSponso(Double.parseDouble(txtBudget.getText()));

            serviceSponsor.modifier(sponsor);
            System.out.println("Sponsor modifié avec succès !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la modification : " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("Erreur : Le budget doit être un nombre valide.");
        }
    }
}
*/
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

    private Sponsor sponsor;
    private ServiceSponsor serviceSponsor = new ServiceSponsor();

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
        txtNom.setText(sponsor.getNomSponso());
        txtPrenom.setText(sponsor.getPrenomSponso());
        txtEmail.setText(sponsor.getEmailSponso());
        txtBudget.setText(String.valueOf(sponsor.getBudgetSponso()));
    }

    @FXML
    private void modifierSponsor() {
        // Contrôler si tous les champs sont remplis
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || txtBudget.getText().isEmpty()) {

            showAlert("Erreur", "Tous les champs sont obligatoires.", Alert.AlertType.ERROR);
            return;
        }

        // Contrôler le format de l'email
        if (!isValidEmail(txtEmail.getText())) {
            showAlert("Erreur", "Veuillez entrer un email valide.", Alert.AlertType.ERROR);
            return;
        }

        // Contrôler si le budget est un nombre valide
        double budget;
        try {
            budget = Double.parseDouble(txtBudget.getText());
        } catch (NumberFormatException ex) {
            showAlert("Erreur", "Le budget doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }

        // Mettre à jour l'objet sponsor avec les nouvelles valeurs
        sponsor.setNomSponso(txtNom.getText());
        sponsor.setPrenomSponso(txtPrenom.getText());
        sponsor.setEmailSponso(txtEmail.getText());
        sponsor.setBudgetSponso(budget);

        // Appeler la méthode pour modifier le sponsor dans la base de données
        try {
            serviceSponsor.modifier(sponsor);
            showAlert("Succès", "Sponsor modifié avec succès.", Alert.AlertType.INFORMATION);
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



    @FXML
    public void RetourListSponso(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherSponsor.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retourdashRH(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardHR.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
